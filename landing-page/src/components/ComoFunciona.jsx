const passos = [
  {
    titulo: 'Registrar',
    texto:
      'Cada setor informa o peso do resíduo por categoria: plástico, papel, não reciclável e perigosos. As compras de descartáveis entram pelo valor do pacote.',
  },
  {
    titulo: 'Acompanhar',
    texto:
      'O painel mostra o total do mês, a variação em relação ao mês anterior, o setor que mais gera e o custo em reais.',
  },
  {
    titulo: 'Reduzir',
    texto:
      'A empresa define metas por setor e acompanha a economia em reais: menos rejeito pago, mais reciclável vendido e menos descartáveis comprados.',
  },
]

export default function ComoFunciona() {
  return (
    <section id="como-funciona" className="secao faixa">
      <div className="secao-topo">
        <p className="subtitulo">Como funciona</p>
        <h2>Três etapas, do registro à redução.</h2>
      </div>

      <div className="grade-3">
        {passos.map((passo, i) => (
          <div className="passo" key={passo.titulo}>
            <span className="passo-num">Passo {i + 1}</span>
            <h3>{passo.titulo}</h3>
            <p className="texto-cinza">{passo.texto}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
