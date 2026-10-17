# Texto-base — Seção 9 "Desenvolvimento do Projeto" (AC2)

> **RASCUNHO PARA O GRUPO REESCREVER.** Este texto foi gerado com apoio de IA a partir do código do protótipo. A professora pediu que o texto do documento seja do grupo: usem como roteiro, conferindo cada informação no sistema, e reescrevam com as palavras de vocês. As figuras estão na pasta `docs/capturas`.

---

## 9. Desenvolvimento do Projeto

### 9.1 Arquitetura e tecnologias

O Reduza foi desenvolvido como aplicação desktop em Java, com interface construída em Swing e banco de dados MySQL acessado por JDBC. Essa escolha atende à orientação da disciplina de utilizar Java e banco de dados relacional e permite executar o sistema em qualquer computador com o Java instalado.

O código foi organizado em camadas, separando responsabilidades:

- **model**: as entidades do sistema (setor, categoria, registro, meta, item, inspeção);
- **dao**: o acesso ao banco, onde ficam todas as consultas SQL;
- **servico**: as regras de negócio (autenticação, cálculo de metas e de custos);
- **ui**: as telas, os componentes visuais e os gráficos;
- **util**: a formatação de números, valores e datas no padrão brasileiro.

Os gráficos foram desenhados no próprio projeto com a biblioteca gráfica do Java (Java2D), sem bibliotecas externas de interface. A única dependência de execução é o driver oficial do MySQL.

### 9.2 Banco de dados

O modelo de dados, elaborado na fase anterior, chegou à versão 3. São dez tabelas: usuários, setores, categorias, registros de resíduo, metas, itens, compras por lote, consumo de itens, pontos de coleta e inspeções de descarte. Há também oito views, que entregam ao sistema os cálculos prontos: totais mensais, variação entre meses, custo por item, progresso das metas e qualidade do descarte.

Na versão 3, as metas passaram a poder abranger a empresa inteira. O cálculo de progresso passou a considerar apenas meses encerrados, para que um mês incompleto não sugira uma redução que ainda não aconteceu.

*(Inserir aqui a figura do DER gerado no drawDB ou no MySQL Workbench.)*

### 9.3 Interface e funcionalidades

O sistema possui sete áreas, acessadas por um menu lateral:

- **Login** (Figura X): acesso com e-mail e senha; cada usuário tem um perfil (Operador, Gestor ou Administrador).
- **Painel do mês** (Figura X): resíduos do mês comparados com o mês anterior, custo de destinação, taxa de descarte correto, metas, gráfico por categoria, evolução dos últimos meses, ranking de setores e uma leitura rápida em texto.
- **Registrar resíduo** (Figura X): registro por setor, categoria, peso e data, com confirmação do total do mês.
- **Histórico** (Figura X): consulta com filtros e exportação para planilha.
- **Metas** (Figura X): metas de redução com barra de progresso e criação de novas metas, com a linha de base sugerida pela média dos meses anteriores.
- **Descartáveis** (Figura X): acompanhamento do estudo piloto de copos, comparação de custo por uso entre copo e caneca e simulador de retorno da troca.
- **Inspeções** (Figura X): registro da qualidade do descarte nos coletores e taxa de acerto por setor.
- **Cadastros**: setores, categorias, itens, pontos de coleta e usuários.

As cores das categorias seguem a Resolução CONAMA nº 275/2001, a mesma usada nos coletores físicos, para que a tela e a lixeira falem a mesma língua.

### 9.4 Regras de cálculo

Para evitar resultados diferentes em telas diferentes, foi definida uma regra única de progresso das metas:

- alvo = linha de base × (1 − redução %);
- progresso = (linha de base − média mensal) ÷ (linha de base − alvo) × 100.

O custo unitário dos itens é calculado a partir da nota de compra (valor do pacote ÷ unidades), nunca digitado. A comparação entre descartável e reutilizável usa o **custo por uso**: o preço dividido pelo número de usos estimados. Com os dados de exemplo, isso mostra que a caneca custa menos por uso que o copo.

### 9.5 Segurança

As senhas são armazenadas apenas como hash (algoritmo PBKDF2, com sal aleatório por usuário). Todas as consultas usam parâmetros (`PreparedStatement`), o que impede ataques de injeção de SQL. As permissões variam por perfil, e o sistema impede que o último administrador seja desativado.

### 9.6 Testes e validação

O protótipo foi verificado em três níveis:

- testes automatizados das regras de cálculo (16 casos);
- testes de integração com o banco de dados (33 verificações, incluindo login, registros, metas, custos e cadastros);
- execução automatizada das telas, conferindo os fluxos de registro, criação de meta e cadastro.

Os dados usados nos testes e nas figuras são fictícios. A validação com dados reais será feita no estudo piloto, mediante autorização da empresa parceira.

### 9.7 Ferramentas de apoio

- **GitHub**: versionamento do código (link);
- **Trello**: organização das tarefas (link);
- **Figma**: protótipo visual das telas (link);
- **drawDB**: diagrama do banco (link);
- **Landing page**: apresentação do produto, com calculadora de economia (link).

O uso de inteligência artificial no projeto está documentado no registro de uso de IA, conforme orientação da disciplina.
