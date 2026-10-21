const dados = [
  {
    numero: '81 mi t',
    texto: 'de resíduos sólidos urbanos gerados no Brasil em 2023. Mais de 41% teve destino inadequado.',
    fonte: 'Fonte: ABREMA, Panorama dos Resíduos Sólidos 2024',
  },
  {
    numero: 'R$ 50 mi',
    texto:
      'é a multa máxima para quem descarta resíduos em desacordo com a lei ou deixa de dar a destinação adequada. A mínima é de R$ 5 mil.',
    fonte: 'Fonte: Decreto nº 6.514/2008, arts. 61 e 62',
  },
  {
    numero: 'Lei 12.305',
    texto:
      'A Política Nacional de Resíduos Sólidos exige que empresas sujeitas ao plano de gerenciamento controlem e comprovem o destino dos seus resíduos.',
    fonte: 'Fonte: Brasil, Lei nº 12.305/2010',
  },
]

export default function Problema() {
  return (
    <section id="problema" className="secao">
      <div className="secao-topo">
        <p className="subtitulo">O problema</p>
        <h2>O desperdício existe, mas ninguém vê o custo.</h2>
        <p className="descricao">
          Quando alguém joga um copo com café no coletor de papel, o lote pode virar rejeito. A empresa
          paga para descartar o que poderia vender. Sem medir por setor, esse custo não aparece em
          nenhum relatório.
        </p>
      </div>

      <div className="grade-3">
        {dados.map((item) => (
          <div className="card" key={item.numero}>
            <span className="card-numero">{item.numero}</span>
            <p>{item.texto}</p>
            <p className="fonte">{item.fonte}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
