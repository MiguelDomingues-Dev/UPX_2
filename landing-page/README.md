# Landing page do Reduza

Página de apresentação do **Reduza**, o sistema de gestão de resíduos que estamos desenvolvendo na UPX2 (Facens, 2026).

A ideia da página é explicar o projeto para quem não conhece: qual problema ele resolve, como o sistema funciona, as telas do protótipo e quanto uma empresa pode economizar usando ele.

## O que tem na página

- Apresentação do projeto e do problema (custo do resíduo, multas, Lei 12.305)
- Como funciona: registrar, acompanhar e reduzir
- Telas do sistema em Java, com abas para trocar entre elas
- Próximos passos do projeto
- Calculadora de retorno: a empresa coloca os próprios números e vê a economia estimada
- Planos, sustentabilidade e qualidade de vida, equipe e referências

## Tecnologias

- React
- Vite
- CSS puro (sem framework)

## Como rodar

Precisa ter o Node.js instalado.

```bash
cd landing-page
npm install
npm run dev
```

Depois é só abrir o endereço que aparece no terminal (normalmente `http://localhost:5173`).

Para gerar a versão final:

```bash
npm run build
```

Os arquivos ficam na pasta `dist`.

## Estrutura

```
landing-page/
├── public/
│   ├── favicon.svg
│   └── telas/          prints do sistema
└── src/
    ├── components/     uma seção da página por arquivo
    ├── App.jsx
    ├── App.css         estilo das seções
    ├── index.css       cores e estilos gerais
    └── main.jsx
```

As cores ficam em variáveis no começo do `index.css`, então dá para mudar o tema por ali.

## Observações

- Os dados que aparecem nas telas são fictícios, só para demonstração.
- Os preços dos planos ainda são provisórios.
- Os valores da calculadora são estimativas.

## Equipe DevNexus

- Miguel – gestor do projeto e engenheiro de software
- Rafael – redes e banco de dados
- Thiago – Scrum Master e Product Owner
- Marcos – requisitos e viabilidade
- Luciana – front-end e back-end

Orientação: Profa. Brenda Medina · Mentoria: Prof. Daniel Ohata
