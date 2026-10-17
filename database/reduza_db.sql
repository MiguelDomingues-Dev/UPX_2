-- =====================================================================
-- REDUZA — Sistema de gestão de resíduos em ambiente corporativo
-- Script de criação do banco de dados — versão 3
-- SGBD: MySQL 8.0 ou superior (compatível com MariaDB 10.5+)
-- Grupo DevNexus — UPX2 — Facens
--
-- Mudanças da versão 3 (protótipo Java):
--   * Metas podem valer para a empresa toda (setor_id vazio)
--   * Senhas com PBKDF2 (biblioteca padrão do Java), sem Spring
--   * Dados reais da empresa removidos deste arquivo; exemplos
--     fictícios ficam em dados_demo.sql
-- Mudanças da versão 2:
--   * Registros por movimentação, com data real
--   * Compras por lote, com cálculo automático de custo unitário
--   * Itens reutilizáveis e comparação de custo por uso
--   * Pontos de coleta e inspeção da qualidade do descarte
--   * Regra única para o progresso das metas
-- =====================================================================

-- Garante que acentos sejam gravados corretamente, qualquer que seja o cliente
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS reduza_db;
CREATE DATABASE reduza_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE reduza_db;

-- =====================================================================
-- CADASTROS BÁSICOS
-- =====================================================================

-- Usuários do sistema. A senha nunca é gravada em texto puro:
-- a aplicação gera o hash PBKDF2-SHA256 (classe Senhas) antes de inserir.
CREATE TABLE usuario (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    senha_hash  VARCHAR(255) NOT NULL,
    perfil      ENUM('ADMIN', 'GESTOR', 'OPERADOR') NOT NULL DEFAULT 'OPERADOR',
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_email UNIQUE (email)
) ENGINE = InnoDB;

-- Departamentos da empresa.
CREATE TABLE setor (
    id                 INT          NOT NULL AUTO_INCREMENT,
    nome               VARCHAR(100) NOT NULL,
    descricao          VARCHAR(255) NULL,
    qtd_colaboradores  INT          NOT NULL DEFAULT 0,
    ativo              BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_setor PRIMARY KEY (id),
    CONSTRAINT uq_setor_nome UNIQUE (nome),
    CONSTRAINT ck_setor_colaboradores CHECK (qtd_colaboradores >= 0)
) ENGINE = InnoDB;

-- Tipos de resíduo, editáveis pela empresa.
-- cor_coletor segue a Resolução CONAMA nº 275/2001.
-- custo_kg = custo de destinação por kg, quando a empresa souber.
CREATE TABLE categoria (
    id           INT           NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(60)   NOT NULL,
    descricao    VARCHAR(255)  NULL,
    cor_coletor  VARCHAR(30)   NULL,
    reciclavel   BOOLEAN       NOT NULL DEFAULT FALSE,
    perigoso     BOOLEAN       NOT NULL DEFAULT FALSE,
    custo_kg     DECIMAL(10,2) NULL,
    ativo        BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_categoria PRIMARY KEY (id),
    CONSTRAINT uq_categoria_nome UNIQUE (nome),
    CONSTRAINT ck_categoria_custo CHECK (custo_kg IS NULL OR custo_kg >= 0)
) ENGINE = InnoDB;

-- =====================================================================
-- REGISTRO DE RESÍDUOS
-- =====================================================================

-- Cada movimentação de resíduo, com a data em que ocorreu.
-- Vários registros por setor no mesmo mês são permitidos; os totais
-- mensais e anuais são calculados pelas views.
CREATE TABLE registro_residuo (
    id             INT           NOT NULL AUTO_INCREMENT,
    setor_id       INT           NOT NULL,
    categoria_id   INT           NOT NULL,
    usuario_id     INT           NOT NULL,
    data_registro  DATE          NOT NULL,
    peso_kg        DECIMAL(10,2) NOT NULL,
    observacao     VARCHAR(255)  NULL,
    criado_em      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_registro PRIMARY KEY (id),
    CONSTRAINT fk_registro_setor     FOREIGN KEY (setor_id)     REFERENCES setor (id),
    CONSTRAINT fk_registro_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_registro_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuario (id),
    CONSTRAINT ck_registro_peso CHECK (peso_kg > 0)
) ENGINE = InnoDB;

-- =====================================================================
-- METAS
-- Regra única de progresso (a mesma em todas as telas):
--   alvo_kg     = linha_base_kg × (1 − reducao_percentual / 100)
--   progresso % = (linha_base − média mensal no período)
--                 ÷ (linha_base − alvo) × 100
-- 0% = nada reduzido; 100% = meta atingida; acima de 100% = superada;
-- negativo = o resíduo aumentou em relação à linha de base.
-- setor_id vazio (NULL) = meta da empresa toda.
-- =====================================================================
CREATE TABLE meta (
    id                  INT           NOT NULL AUTO_INCREMENT,
    setor_id            INT           NULL,
    categoria_id        INT           NULL,
    descricao           VARCHAR(120)  NOT NULL,
    periodo_inicio      DATE          NOT NULL,
    periodo_fim         DATE          NOT NULL,
    linha_base_kg       DECIMAL(10,2) NOT NULL,
    reducao_percentual  DECIMAL(5,2)  NOT NULL,
    ativa               BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_meta PRIMARY KEY (id),
    CONSTRAINT fk_meta_setor     FOREIGN KEY (setor_id)     REFERENCES setor (id),
    CONSTRAINT fk_meta_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT ck_meta_periodo    CHECK (periodo_fim >= periodo_inicio),
    CONSTRAINT ck_meta_percentual CHECK (reducao_percentual > 0 AND reducao_percentual <= 100),
    CONSTRAINT ck_meta_base       CHECK (linha_base_kg > 0)
) ENGINE = InnoDB;

-- =====================================================================
-- DESCARTÁVEIS E ALTERNATIVAS (piloto)
-- =====================================================================

-- Itens contados em unidades: descartáveis e alternativas reutilizáveis.
-- usos_estimados: 1 para descartável; vida útil estimada para reutilizável.
CREATE TABLE item (
    id               INT          NOT NULL AUTO_INCREMENT,
    categoria_id     INT          NOT NULL,
    nome             VARCHAR(100) NOT NULL,
    tipo             ENUM('DESCARTAVEL', 'REUTILIZAVEL') NOT NULL DEFAULT 'DESCARTAVEL',
    peso_unitario_g  DECIMAL(8,2) NOT NULL,
    usos_estimados   INT          NOT NULL DEFAULT 1,
    fonte_peso       VARCHAR(255) NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT uq_item_nome UNIQUE (nome),
    CONSTRAINT ck_item_peso CHECK (peso_unitario_g > 0),
    CONSTRAINT ck_item_usos CHECK (usos_estimados >= 1)
) ENGINE = InnoDB;

-- Compras por lote. O custo unitário não é digitado: é calculado a
-- partir do valor do pacote e da quantidade por pacote.
CREATE TABLE compra_lote (
    id                   INT           NOT NULL AUTO_INCREMENT,
    item_id              INT           NOT NULL,
    usuario_id           INT           NOT NULL,
    data_compra          DATE          NOT NULL,
    qtd_pacotes          INT           NOT NULL,
    unidades_por_pacote  INT           NOT NULL,
    valor_pacote         DECIMAL(10,2) NOT NULL,
    fornecedor           VARCHAR(120)  NULL,
    CONSTRAINT pk_compra PRIMARY KEY (id),
    CONSTRAINT fk_compra_item    FOREIGN KEY (item_id)    REFERENCES item (id),
    CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT ck_compra_pacotes  CHECK (qtd_pacotes > 0),
    CONSTRAINT ck_compra_unidades CHECK (unidades_por_pacote > 0),
    CONSTRAINT ck_compra_valor    CHECK (valor_pacote >= 0)
) ENGINE = InnoDB;

-- Consumo (retirada/reposição) de itens por setor.
-- É a medição do antes e depois da intervenção.
CREATE TABLE consumo_item (
    id             INT      NOT NULL AUTO_INCREMENT,
    setor_id       INT      NOT NULL,
    item_id        INT      NOT NULL,
    usuario_id     INT      NOT NULL,
    data_consumo   DATE     NOT NULL,
    quantidade     INT      NOT NULL,
    criado_em      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_consumo PRIMARY KEY (id),
    CONSTRAINT fk_consumo_setor   FOREIGN KEY (setor_id)   REFERENCES setor (id),
    CONSTRAINT fk_consumo_item    FOREIGN KEY (item_id)    REFERENCES item (id),
    CONSTRAINT fk_consumo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT ck_consumo_qtd CHECK (quantidade > 0)
) ENGINE = InnoDB;

-- =====================================================================
-- PONTOS DE COLETA E QUALIDADE DO DESCARTE
-- =====================================================================

-- Estações de lixeiras de cada setor.
CREATE TABLE ponto_coleta (
    id           INT          NOT NULL AUTO_INCREMENT,
    setor_id     INT          NOT NULL,
    nome         VARCHAR(100) NOT NULL,
    localizacao  VARCHAR(150) NULL,
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_ponto PRIMARY KEY (id),
    CONSTRAINT fk_ponto_setor FOREIGN KEY (setor_id) REFERENCES setor (id)
) ENGINE = InnoDB;

-- Inspeção amostral: alguém olha o coletor e classifica o conteúdo.
-- Mede o comportamento de descarte sem nenhum sensor.
CREATE TABLE inspecao_descarte (
    id             INT          NOT NULL AUTO_INCREMENT,
    ponto_id       INT          NOT NULL,
    categoria_id   INT          NOT NULL,
    usuario_id     INT          NOT NULL,
    data_inspecao  DATE         NOT NULL,
    resultado      ENUM('CORRETO', 'PARCIAL', 'INCORRETO') NOT NULL,
    observacao     VARCHAR(255) NULL,
    CONSTRAINT pk_inspecao PRIMARY KEY (id),
    CONSTRAINT fk_inspecao_ponto     FOREIGN KEY (ponto_id)     REFERENCES ponto_coleta (id),
    CONSTRAINT fk_inspecao_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_inspecao_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuario (id)
) ENGINE = InnoDB;

-- Índices para filtros por data (histórico, comparativos)
CREATE INDEX ix_registro_data ON registro_residuo (data_registro);
CREATE INDEX ix_consumo_data  ON consumo_item (data_consumo);
CREATE INDEX ix_compra_data   ON compra_lote (data_compra);
CREATE INDEX ix_inspecao_data ON inspecao_descarte (data_inspecao);

-- =====================================================================
-- VIEWS — consultas prontas que a aplicação lê diretamente
-- =====================================================================

-- Total mensal por setor e categoria, com custo de destinação
CREATE VIEW vw_residuo_mensal AS
SELECT
    CAST(DATE_FORMAT(r.data_registro, '%Y-%m-01') AS DATE) AS mes,
    s.id    AS setor_id,
    s.nome  AS setor,
    c.id    AS categoria_id,
    c.nome  AS categoria,
    SUM(r.peso_kg)                            AS peso_kg,
    COUNT(*)                                  AS qtd_registros,
    SUM(r.peso_kg) * COALESCE(c.custo_kg, 0)  AS custo_destinacao
FROM registro_residuo r
JOIN setor     s ON s.id = r.setor_id
JOIN categoria c ON c.id = r.categoria_id
GROUP BY CAST(DATE_FORMAT(r.data_registro, '%Y-%m-01') AS DATE),
         s.id, s.nome, c.id, c.nome, c.custo_kg;

-- Variação em relação ao mês anterior
CREATE VIEW vw_variacao_mensal AS
SELECT
    mes, setor, categoria, peso_kg,
    LAG(peso_kg) OVER (PARTITION BY setor_id, categoria_id ORDER BY mes) AS peso_mes_anterior,
    ROUND(
        (peso_kg - LAG(peso_kg) OVER (PARTITION BY setor_id, categoria_id ORDER BY mes))
        / NULLIF(LAG(peso_kg) OVER (PARTITION BY setor_id, categoria_id ORDER BY mes), 0)
        * 100, 2
    ) AS variacao_percentual
FROM vw_residuo_mensal;

-- Total anual por categoria
CREATE VIEW vw_residuo_anual AS
SELECT
    YEAR(mes)             AS ano,
    categoria,
    SUM(peso_kg)          AS peso_kg,
    SUM(custo_destinacao) AS custo_destinacao
FROM vw_residuo_mensal
GROUP BY YEAR(mes), categoria;

-- Custo unitário médio de cada item, ponderado por todas as compras,
-- e custo por uso (o que permite comparar descartável x reutilizável)
CREATE VIEW vw_custo_item AS
SELECT
    i.id   AS item_id,
    i.nome AS item,
    i.tipo,
    i.usos_estimados,
    SUM(cl.qtd_pacotes * cl.unidades_por_pacote)             AS unidades_compradas,
    SUM(cl.qtd_pacotes * cl.valor_pacote)                    AS valor_total,
    ROUND(SUM(cl.qtd_pacotes * cl.valor_pacote)
          / SUM(cl.qtd_pacotes * cl.unidades_por_pacote), 4) AS custo_unitario,
    ROUND(SUM(cl.qtd_pacotes * cl.valor_pacote)
          / SUM(cl.qtd_pacotes * cl.unidades_por_pacote)
          / i.usos_estimados, 4)                             AS custo_por_uso
FROM item i
JOIN compra_lote cl ON cl.item_id = i.id
GROUP BY i.id, i.nome, i.tipo, i.usos_estimados;

-- Despesa mensal com compras de itens
CREATE VIEW vw_despesa_mensal AS
SELECT
    CAST(DATE_FORMAT(cl.data_compra, '%Y-%m-01') AS DATE) AS mes,
    i.nome AS item,
    SUM(cl.qtd_pacotes * cl.unidades_por_pacote)                         AS unidades,
    SUM(cl.qtd_pacotes * cl.valor_pacote)                                AS valor_gasto,
    ROUND(SUM(cl.qtd_pacotes * cl.unidades_por_pacote)
          * i.peso_unitario_g / 1000, 3)                                 AS peso_kg
FROM compra_lote cl
JOIN item i ON i.id = cl.item_id
GROUP BY CAST(DATE_FORMAT(cl.data_compra, '%Y-%m-01') AS DATE),
         i.id, i.nome, i.peso_unitario_g;

-- Consumo mensal por setor: unidades, kg, custo e unidades por colaborador
CREATE VIEW vw_consumo_mensal AS
SELECT
    CAST(DATE_FORMAT(ci.data_consumo, '%Y-%m-01') AS DATE) AS mes,
    s.nome AS setor,
    i.nome AS item,
    i.tipo,
    SUM(ci.quantidade)                                            AS unidades,
    ROUND(SUM(ci.quantidade) * i.peso_unitario_g / 1000, 3)       AS peso_kg,
    ROUND(SUM(ci.quantidade) * cu.custo_unitario, 2)              AS custo,
    ROUND(SUM(ci.quantidade) / NULLIF(s.qtd_colaboradores, 0), 1) AS unidades_por_colaborador
FROM consumo_item ci
JOIN setor s ON s.id = ci.setor_id
JOIN item  i ON i.id = ci.item_id
LEFT JOIN vw_custo_item cu ON cu.item_id = i.id
GROUP BY CAST(DATE_FORMAT(ci.data_consumo, '%Y-%m-01') AS DATE),
         s.id, s.nome, s.qtd_colaboradores, i.id, i.nome, i.tipo,
         i.peso_unitario_g, cu.custo_unitario;

-- Taxa de descarte correto por setor e mês
CREATE VIEW vw_qualidade_descarte AS
SELECT
    CAST(DATE_FORMAT(ins.data_inspecao, '%Y-%m-01') AS DATE) AS mes,
    s.nome AS setor,
    COUNT(*)                                                  AS inspecoes,
    SUM(ins.resultado = 'CORRETO')                            AS corretas,
    SUM(ins.resultado = 'PARCIAL')                            AS parciais,
    SUM(ins.resultado = 'INCORRETO')                          AS incorretas,
    ROUND(SUM(ins.resultado = 'CORRETO') / COUNT(*) * 100, 1) AS taxa_correto
FROM inspecao_descarte ins
JOIN ponto_coleta pc ON pc.id = ins.ponto_id
JOIN setor s         ON s.id = pc.setor_id
GROUP BY CAST(DATE_FORMAT(ins.data_inspecao, '%Y-%m-01') AS DATE), s.id, s.nome;

-- Progresso das metas (regra descrita na tabela meta).
-- Só meses já encerrados entram na média: um mês pela metade
-- daria a falsa impressão de que o resíduo caiu.
CREATE VIEW vw_progresso_meta AS
SELECT
    m.id,
    m.descricao,
    m.setor_id,
    COALESCE(s.nome, 'Empresa toda') AS setor,
    m.categoria_id,
    c.nome AS categoria,
    m.periodo_inicio,
    m.periodo_fim,
    m.linha_base_kg,
    m.reducao_percentual,
    ROUND(m.linha_base_kg * (1 - m.reducao_percentual / 100), 2) AS alvo_kg,
    ROUND(SUM(r.peso_kg)
          / NULLIF(COUNT(DISTINCT DATE_FORMAT(r.data_registro, '%Y-%m')), 0), 2) AS media_mensal_kg,
    ROUND(
        (m.linha_base_kg - SUM(r.peso_kg)
            / NULLIF(COUNT(DISTINCT DATE_FORMAT(r.data_registro, '%Y-%m')), 0))
        / (m.linha_base_kg * m.reducao_percentual / 100) * 100, 1
    ) AS progresso_percentual
FROM meta m
LEFT JOIN setor     s ON s.id = m.setor_id
LEFT JOIN categoria c ON c.id = m.categoria_id
LEFT JOIN registro_residuo r
       ON (m.setor_id IS NULL OR r.setor_id = m.setor_id)
      AND (m.categoria_id IS NULL OR r.categoria_id = m.categoria_id)
      AND r.data_registro BETWEEN m.periodo_inicio AND m.periodo_fim
      AND r.data_registro < CAST(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') AS DATE)
WHERE m.ativa = TRUE
GROUP BY m.id, m.descricao, m.setor_id, s.nome, m.categoria_id, c.nome,
         m.periodo_inicio, m.periodo_fim, m.linha_base_kg, m.reducao_percentual;

-- =====================================================================
-- DADOS INICIAIS
-- Usuário administrador padrão: admin@reduza.local / senha reduza123
-- (troque a senha no primeiro acesso, em Cadastros > Usuários)
-- =====================================================================

INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES
('Administrador', 'admin@reduza.local', 'pbkdf2_sha256$120000$KkrYQjpH8O+spzcbbgDGxg==$WWEPlaZAIv3PxO9412Pl4m4XzWhj5LzUXObA8AmnUDk=', 'ADMIN');

-- Categorias padrão; cores conforme CONAMA 275/2001.
-- custo_kg (custo de destinação) fica vazio até a empresa informar.
INSERT INTO categoria (nome, cor_coletor, reciclavel, perigoso) VALUES
('Plástico',           'Vermelho', TRUE,  FALSE),
('Papel',              'Azul',     TRUE,  FALSE),
('Não reciclável',     'Cinza',    FALSE, FALSE),
('Resíduos perigosos', 'Laranja',  FALSE, TRUE),
('Orgânico',           'Marrom',   FALSE, FALSE);

-- Peso do copo conforme especificações baseadas na ABNT NBR 14865
INSERT INTO item (categoria_id, nome, tipo, peso_unitario_g, usos_estimados, fonte_peso) VALUES
(1, 'Copo plástico 200 ml', 'DESCARTAVEL', 1.80, 1,
 'Especificação técnica baseada na ABNT NBR 14865 (180 g por pacote de 100)');
