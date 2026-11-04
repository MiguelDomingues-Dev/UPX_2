# Landing page do Reduza

Banner digital do Reduza, sistema de gestão de resíduos para empresas desenvolvido pela equipe DevNexus na disciplina UPX2 (Centro Universitário Facens, 2026).

Publicada em: https://migueldomingues-dev.github.io/UPX_2/

## Estrutura

```
landing-page/
├── index.html        conteúdo da página
├── style.css         estilos (cores definidas em :root)
├── script.js         menu, animações, galeria de telas e QR Code
└── assets/
    ├── imagens/      telas do sistema e ícone
    └── docs/
```

HTML, CSS e JavaScript puros: não há build nem dependências.

## Seções

1. Início, com a tela real do painel do mês
2. Números sobre resíduos no Brasil
3. O problema
4. Como funciona (registrar, acompanhar, reduzir)
5. Tecnologias e arquitetura
6. O sistema: galeria com as seis telas do protótipo
7. Resultados esperados e ODS
8. Equipe
9. QR Code para abrir a página no celular

## Atualizar as telas

As capturas ficam em `assets/imagens/` (`painel.png`, `registro.png`, `historico.png`, `metas.png`, `descartaveis.png`, `inspecoes.png`). Para trocar uma tela, substitua o arquivo mantendo o nome. Os títulos e textos da galeria ficam na lista `telas` do `script.js`.

## Ver localmente

Abra o `index.html` no navegador. Aberta pelo arquivo local, a página gera o QR Code com o link do GitHub Pages.

## Publicação

O workflow `.github/workflows/static.yml` publica a pasta `landing-page` no GitHub Pages a cada push na branch `main`.
