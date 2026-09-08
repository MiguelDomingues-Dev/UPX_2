Reduza

Sistema de gestão de resíduos para ambiente corporativo.

Projeto desenvolvido na disciplina UPX2 — Usina de Projetos Experimentais II do Centro Universitário Facens, no curso de Análise e Desenvolvimento de Sistemas.

Sobre o projeto

Organizações raramente sabem quanto resíduo geram, por qual setor e de qual tipo. Sem esse dado, não há linha de base, não há meta e não há como saber se qualquer iniciativa de sustentabilidade funcionou.

O Reduza registra a quantidade de resíduos gerados por setor e por categoria (plástico, papel, orgânico e não reciclável), acompanha a evolução ao longo do tempo, permite definir metas de redução e estima a economia associada.

Como estudo piloto, o projeto acompanha a categoria de materiais descartáveis em um setor real: mede a linha de base, aplica uma ação de incentivo ao uso de alternativas reutilizáveis e mede novamente, comparando os resultados.

Objetivo

Fornecer à organização uma base objetiva de decisão sobre resíduos, e um método replicável de medição e intervenção que possa ser aplicado a outros setores e a outras categorias.

Tecnologias
Java — desenvolvimento da aplicação
Banco de dados relacional — armazenamento dos registros e indicadores
Figma — protótipos de interface e fluxos de dados
Trello — acompanhamento de tarefas e divisão do trabalho
Git / GitHub — versionamento e documentação

As versões e bibliotecas específicas serão registradas aqui conforme forem definidas.

Estrutura do repositório
/docs        documentação do projeto e material da AC1
/src         código-fonte da aplicação
/database    scripts de criação e carga do banco de dados
/design      identidade visual e exportações do protótipo
Organização das branches
Branch	Finalidade
main	Somente versões aprovadas pela equipe
equipe/upx-devnexu	Branch de integração; todo trabalho é reunido aqui
proto/<nome>	Exploração individual, sem compromisso de entrar no projeto
feature/<descrição>	Funcionalidades já acordadas pela equipe

Nenhuma alteração entra em main ou na branch de integração sem pull request revisado por outro integrante.

Como contribuir (equipe)
bash
# partir sempre da branch de integração atualizada
git checkout equipe/upx-devnexu
git pull

# criar a branch de trabalho
git checkout -b feature/nome-da-funcionalidade

# ao concluir
git push -u origin feature/nome-da-funcionalidade
# abrir pull request para equipe/upx-devnexu

Mensagens de commit em português, no imperativo e descrevendo o que muda. Exemplo: Adiciona cálculo de variação percentual entre períodos.

Equipe — DevNexus
Integrante	Funções
Miguel Domingues	a definir
Luciana Rodrigues   a definir
Thiago Cortez   a definir
Rafael Magalhães    a definir
Marcos Brandão  a definir


Orientação: Profa. Brenda Medina Mentoria: Prof. Daniel Ohata

Links do projeto
Trello: a definir
Figma: a definir
Landing page: a definir
Identidade visual

Os arquivos da marca estão em /design. A paleta oficial:

Cor	Hex
Verde-petróleo escuro	
#1F6F6B
Verde-petróleo médio	
#2D8A84
Verde-água	
#4AA8A1
Verde-água claro	
#8FC9C4
Grafite	
#3D4A48
Situação atual

Projeto em fase inicial: definição de escopo e elaboração da documentação da AC1.

Projeto acadêmico. Centro Universitário Facens — Sorocaba/SP.