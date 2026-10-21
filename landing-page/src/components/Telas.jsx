import { useState } from 'react'
import Janela from './Janela'

const telas = [
  {
    aba: 'Painel',
    titulo: 'Painel do mês',
    imagem: 'telas/painel.png',
    texto: 'O resumo que o gestor abre primeiro.',
    itens: [
      'Total do mês comparado com o anterior',
      'Custo de destinação em reais',
      'Taxa de descarte correto',
      'Setor que mais gera resíduo',
    ],
  },
  {
    aba: 'Registrar resíduo',
    titulo: 'Registrar resíduo',
    imagem: 'telas/registro.png',
    texto:
      'Setor, categoria, quilos e data. A bolinha mostra a cor do coletor, e o sistema confirma o total do mês ao salvar.',
  },
  {
    aba: 'Histórico',
    titulo: 'Histórico',
    imagem: 'telas/historico.png',
    texto:
      'Todos os registros, com filtro por período, setor e categoria. O botão Exportar CSV gera uma planilha que abre no Excel.',
  },
  {
    aba: 'Metas',
    titulo: 'Metas de redução',
    imagem: 'telas/metas.png',
    texto:
      'Cada meta parte da média dos meses anteriores. 100% quer dizer alvo atingido. O mês em andamento só conta quando fecha.',
  },
  {
    aba: 'Descartáveis',
    titulo: 'Piloto de descartáveis',
    imagem: 'telas/descartaveis.png',
    texto:
      'Consumo de copos mês a mês, gasto em reais e custo por uso do copo e da caneca. O simulador mostra em quantos meses a troca se paga.',
  },
  {
    aba: 'Inspeções',
    titulo: 'Inspeções de descarte',
    imagem: 'telas/inspecoes.png',
    texto:
      'Alguém confere os coletores e marca se o descarte estava correto. O placar é por setor, nunca por pessoa.',
  },
]

export default function Telas() {
  const [ativa, setAtiva] = useState(0)
  const tela = telas[ativa]

  return (
    <section id="telas" className="secao">
      <div className="secao-topo">
        <p className="subtitulo">O sistema</p>
        <h2>Telas reais do protótipo.</h2>
        <p className="descricao">
          O Reduza foi feito em Java, com banco de dados MySQL. Escolha uma tela para ver o que ela faz.
        </p>
      </div>

      <div className="abas">
        {telas.map((t, i) => (
          <button
            key={t.aba}
            className={i === ativa ? 'aba ativa' : 'aba'}
            onClick={() => setAtiva(i)}
          >
            {t.aba}
          </button>
        ))}
      </div>

      <div className="tela">
        <Janela titulo={tela.titulo} imagem={tela.imagem} alt={tela.titulo} />
        <div className="tela-info">
          <h3>{tela.titulo}</h3>
          <p className="texto-cinza">{tela.texto}</p>
          {tela.itens && (
            <ul>
              {tela.itens.map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <p className="aviso">Dados fictícios de uma empresa exemplo, usados para demonstração.</p>
    </section>
  )
}
