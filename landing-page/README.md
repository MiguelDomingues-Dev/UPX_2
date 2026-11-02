# Banner Digital Interativo para Projetos Acadêmicos

Este repositório contém um modelo de landing page em formato de **banner digital interativo**, desenvolvido para apresentação de projetos acadêmicos, startups, MVPs, UPX, TCCs e disciplinas de desenvolvimento de software.

A página pode ser publicada gratuitamente no **GitHub Pages** e utilizada em apresentações, bancas, pitchs, feiras acadêmicas e portfólio profissional.

## Estrutura do projeto

```bash
banner-digital-github/
├── index.html
├── style.css
├── script.js
├── README.md
└── assets/
    ├── imagens/
    └── docs/
```

## O que os alunos devem alterar

No arquivo `index.html`, substituir os textos genéricos por informações reais do projeto:

- Nome do projeto
- Slogan
- Problema identificado
- Solução proposta
- Funcionalidades
- Tecnologias utilizadas
- Arquitetura da solução
- Link do protótipo Figma
- Link do repositório GitHub
- Integrantes da equipe
- Disciplina, instituição e semestre

No arquivo `style.css`, é possível alterar:

- Cores principais
- Tipografia
- Espaçamentos
- Bordas
- Tamanho das seções
- Estilo dos cards

No arquivo `script.js`, já existem:

- menu responsivo;
- animações de entrada;
- QR Code gerado automaticamente com o link da página publicada.

## Como publicar no GitHub Pages

1. Criar um repositório público no GitHub.
2. Enviar os arquivos `index.html`, `style.css`, `script.js`, `README.md` e a pasta `assets`.
3. Acessar o repositório no GitHub.
4. Clicar em **Settings**.
5. Acessar **Pages**.
6. Em **Build and deployment**, selecionar:
   - Source: `Deploy from a branch`
   - Branch: `main`
   - Folder: `/root`
7. Salvar.
8. Aguardar o GitHub gerar o link público.

O link ficará semelhante a:

```bash
https://seu-usuario.github.io/nome-do-repositorio/
```

## Uso como banner de apresentação

Este modelo foi pensado para substituir ou complementar um banner estático. Durante uma apresentação, os alunos podem exibir a landing page em um notebook, projetor ou TV e permitir que a banca acesse pelo QR Code.

A página inclui seções para:

- apresentação visual do projeto;
- problema e solução;
- funcionalidades;
- tecnologias;
- arquitetura;
- demonstração do MVP;
- ODS e impacto;
- equipe;
- QR Code de acesso.

## Sugestões de melhoria

Os alunos podem enriquecer a página incluindo:

- imagens reais do sistema;
- GIFs demonstrativos;
- vídeo incorporado;
- link para protótipo Figma;
- link para aplicação funcional;
- diagrama de arquitetura;
- resultados de testes com usuários;
- métricas de avaliação;
- depoimentos ou validações.

## Critérios de avaliação sugeridos

| Critério | Peso |
|---|---:|
| Clareza da proposta e do problema | 2,0 |
| Qualidade visual e organização da página | 2,0 |
| Apresentação da solução e funcionalidades | 2,0 |
| Demonstração do MVP ou protótipo | 1,5 |
| Responsividade e usabilidade | 1,0 |
| Publicação correta no GitHub Pages | 1,0 |
| README e organização do repositório | 0,5 |

## Observação

Após publicar no GitHub Pages, abra a página pelo link público para que o QR Code seja gerado corretamente com o endereço real da landing page.

------------------------------------------

# Landpage – Projeto Acadêmico

Está explicação a seguir, serve para informar como o grupo pode publicar a landpage pelo recurso Github Pages


A proposta da página é funcionar como:

- banner digital;
- apresentação visual;
- portfólio do projeto;
- vitrine tecnológica;
- material para pitch e bancas;
- página oficial publicada online.

---

Para reproduzir e hospedar o projeto banner-digital-interativo-github-pages.zip, faça assim:

1. Descompactar o arquivo

Extraia o .zip. A pasta deve conter algo parecido com:

index.html
style.css
script.js
assets/
README.md

O arquivo mais importante é o index.html, pois o GitHub Pages procura esse arquivo para abrir a página.

2. Criar um repositório no GitHub

No GitHub:

- New repository

Sugestão de nome:

- landingpage

Deixe como Public.

3. Enviar os arquivos

Entre no repositório criado e clique em:

Add file > Upload files

Envie todos os arquivos da pasta extraída, não envie apenas a pasta compactada. 
Caso tenha dificuldade siga os procedimentos de envio utilizando o Github Desktop ou Git pelo prompt.

A estrutura final no GitHub deve ficar assim:

banner-digital-projeto/
│
├── index.html
├── style.css
├── script.js
├── assets/
└── README.md


4. Ativar o GitHub Pages

No repositório, acesse:

Settings > Pages

Em Build and deployment, selecione:

Source: Deploy from a branch
Branch: main
Folder: /root

Depois clique em Save.



5. Acessar o site publicado

Após alguns minutos, o GitHub irá gerar um link parecido com:

https://seuusuario.github.io/banner-digital-projeto/

Esse será o link público da landing page.



6. Editar o conteúdo do projeto

Os alunos deverão alterar principalmente o arquivo:

index.html

Para trocar:

nome do projeto;
nome da equipe;
problema;
solução;
funcionalidades;
tecnologias;
links do GitHub;
QR Code;
imagens;
vídeo demonstrativo.

As imagens devem ser colocadas dentro de: assets/


7. Entrega recomendada dos alunos

A entrega pode conter:

Nome da equipe:
Nome do projeto:
Link do GitHub:
Link do GitHub Pages:
Link do vídeo demonstrativo:
Link do protótipo:

Exemplo:

Equipe X – Projeto Y
GitHub: https://github.com/usuario/X
Landing Page: https://usuario.github.io/X/
Vídeo: link do YouTube ou Drive
Protótipo: link do Figma
