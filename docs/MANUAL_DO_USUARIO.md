# Reduza — Manual do usuário

Este manual mostra como usar cada tela do Reduza. As imagens usam os dados fictícios da "Empresa Exemplo".

---

## Primeiro acesso

1. Entre com `admin@reduza.local` e a senha `reduza123`.
2. Clique em **Trocar senha**, no topo da janela, e defina uma senha nova (mínimo de 8 caracteres, com letras e números).
3. Vá em **Cadastros** e confira, nesta ordem:
   - **Setores**: os departamentos da empresa e o número de colaboradores de cada um;
   - **Categorias**: os tipos de resíduo, a cor do coletor e, se souber, o **custo de destinação por kg**;
   - **Pontos de coleta**: as estações de lixeiras de cada setor;
   - **Itens do piloto**: o copo descartável e a alternativa reutilizável (caneca), com o peso de cada um;
   - **Usuários**: quem vai usar o sistema e com qual perfil.

| Perfil | Pode |
|---|---|
| Operador | Registrar resíduos, compras, consumo e inspeções; ver o painel, o histórico e as metas |
| Gestor | Tudo do Operador + criar metas e editar cadastros |
| Administrador | Tudo do Gestor + gerenciar usuários |

![Login](capturas/01_login.png)

---

## Rotina do dia a dia

### Registrar resíduo

Sempre que um coletor for esvaziado e pesado:

1. Abra **Registrar resíduo**.
2. Escolha o **setor** e a **categoria** (a bolinha mostra a cor do coletor).
3. Digite a **quantidade em kg** (ex.: `12,5`). A data já vem preenchida com hoje.
4. Clique em **Salvar registro** ou aperte Enter.

O aviso verde confirma o registro e mostra o total do mês. Os últimos registros aparecem à direita. Para corrigir um lançamento, selecione-o e clique em **Excluir selecionado** e depois registre de novo. O Operador só pode excluir os próprios registros.

![Registrar resíduo](capturas/04_registro.png)

### Registrar uma inspeção do descarte

De tempos em tempos, por exemplo uma vez por semana, alguém olha os coletores:

1. Abra **Inspeções**.
2. Escolha o **ponto de coleta** e o **coletor** inspecionado.
3. Marque o resultado: **Correto**, **Parcial** ou **Incorreto**.
4. Clique em **Salvar inspeção**.

À direita aparece a taxa de descarte correto de cada setor no mês escolhido.

![Inspeções](capturas/08_inspecoes.png)

---

## Acompanhamento

### Painel do mês

Escolha o **mês de referência**. O painel mostra:

- **Resíduos no mês**, comparado com o mês anterior. Se o mês ainda não terminou, a comparação usa os mesmos dias do mês anterior.
- **Custo de destinação**, calculado pelo custo por kg de cada categoria.
- **Descarte correto**, vindo das inspeções.
- **Metas ativas**, com o progresso médio.
- O gráfico **por categoria** (mês anterior em tom claro, mês atual em cor cheia).
- O gráfico de **evolução** dos últimos meses, com a marca de início das metas.
- O **ranking de setores** e uma **leitura rápida** em texto.

![Painel](capturas/02_painel.png)

### Histórico

Filtre por período, setor e categoria e clique em **Filtrar**. O total do período aparece acima da tabela. **Exportar CSV** gera um arquivo que abre direto no Excel.

![Histórico](capturas/05_historico.png)

### Metas

Cada cartão mostra o percentual de progresso, a barra e os três números do cálculo: linha de base, média atual e alvo.

Para criar uma meta (Gestor ou Administrador):

1. Dê um nome e escolha o setor (ou **Empresa toda**) e a categoria (ou **Todas**).
2. Defina o período.
3. Clique em **Usar a média dos 3 meses anteriores** para preencher a linha de base automaticamente.
4. Informe a redução desejada em %. A prévia mostra o alvo em kg.
5. Clique em **Criar meta**.

O mês em andamento só entra no progresso quando termina.

![Metas](capturas/06_metas.png)

---

## Piloto de descartáveis

Abra **Descartáveis**:

- **Indicadores**: copos consumidos no mês, plástico gerado, gasto em reais e custo por uso.
- **Gráfico**: consumo mês a mês. As colunas verdes são os meses depois da compra das canecas.
- **Descartável × reutilizável**: compara quanto custa cada uso do copo e da caneca.
- **Simulador**: mude o número de pessoas, copos por dia e preço da caneca para ver em quantos meses a troca se paga.
- **Registrar compra**: digite como está na nota (pacotes, unidades por pacote e valor do pacote). O custo unitário é calculado pelo sistema.
- **Registrar consumo**: quantos copos o setor usou ou repôs.

![Descartáveis](capturas/07_descartaveis.png)

---

## Problemas comuns

| Mensagem | O que fazer |
|---|---|
| "Não foi possível conectar ao banco de dados" | Ligue o MySQL e confira o `reduza.properties` |
| "O banco reduza_db não existe" | Execute `database/reduza_db.sql` |
| "E-mail ou senha incorretos" | Confira os dados; o administrador pode definir uma senha nova em Cadastros > Usuários |
| "Já existe um cadastro com esse nome" | Use outro nome ou edite o cadastro existente |
| "Não é possível excluir: há registros ligados" | Use **Desativar**: o cadastro some das listas, mas o histórico fica preservado |
