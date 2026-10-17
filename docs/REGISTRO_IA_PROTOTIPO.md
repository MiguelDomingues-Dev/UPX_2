# Registro de uso de IA — Protótipo funcional (AC2)

Entrada para o registro de uso de inteligência artificial do projeto, conforme exigência da professora orientadora.

| Campo | Informação |
|---|---|
| Data | 13/10/2026 |
| Ferramenta | Claude (Anthropic), usado no app Claude |
| Modelo | Configurado como `claude-opus-5-5` (o modelo que efetivamente respondeu pode variar) |
| Quem usou | Miguel Augusto Camargo Domingues (líder do grupo) |
| Para quê | Construir o protótipo funcional em Java + MySQL e a documentação técnica dele |

## Pedidos feitos à IA (prompts)

1. "Agora precisamos construir o app usando somente java e mysql para o projeto"
2. "Certo, vamos desenvolver o projeto do protótipo com Java + Mysql, logo documentar tudo também."

Contexto já existente na conversa e usado pela IA: o banco `reduza_db` v2, as personas, os objetivos do documento da AC1, as telas da demonstração em vídeo, a regra única de metas e a restrição do grupo de usar somente Java no sistema.

## O que a IA gerou

- Código Java do sistema: 68 arquivos, cerca de 5.900 linhas (telas, gráficos, acesso ao banco, regras e segurança).
- Versão 3 do script do banco e o script de dados fictícios.
- Testes automatizados das regras (JUnit) e os testes de integração usados na verificação.
- README, documentação técnica, manual do usuário e texto-base da seção "Desenvolvimento do Projeto".

## O que foi decisão do grupo (não da IA)

- Tema, escopo do sistema (gestão de resíduos por setor e categoria) e piloto de copos.
- Uso exclusivo de Java + MySQL no sistema.
- Personas, categorias da empresa parceira e a regra de medir por setor, não por pessoa.

## Como o resultado foi verificado

- Compilação com Java 17 sem erros.
- 16 testes de regras aprovados; 33 verificações de integração com banco real aprovadas.
- Execução automatizada das telas e conferência visual de todas as capturas.

## Responsabilidade do grupo

- Ler e entender o código antes da apresentação (cada integrante deve saber explicar a parte ligada à sua função).
- Reescrever com as próprias palavras qualquer texto que entre no documento ou no artigo.
- Confirmar com a empresa parceira os valores reais antes de substituir os dados fictícios.

## Por que a IA foi usada

*(Seção a ser escrita pelo grupo.)*
