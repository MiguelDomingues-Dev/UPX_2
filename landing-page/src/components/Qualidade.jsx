const coletores = [
  { nome: 'Plástico', cor: '#D62B2B' },
  { nome: 'Papel', cor: '#1F5FBF' },
  { nome: 'Não reciclável', cor: '#8A8F8C' },
  { nome: 'Perigosos', cor: '#E8822A' },
]

const ods = [
  { numero: 3, nome: 'Saúde e bem-estar' },
  { numero: 8, nome: 'Trabalho decente e crescimento econômico' },
  { numero: 9, nome: 'Indústria, inovação e infraestrutura' },
  { numero: 11, nome: 'Cidades e comunidades sustentáveis' },
  { numero: 12, nome: 'Consumo e produção responsáveis' },
]

export default function Qualidade() {
  return (
    <section className="secao">
      <div className="secao-topo">
        <p className="subtitulo">Sustentabilidade e qualidade de vida</p>
        <h2>Menos resíduo para a empresa, mais saúde para quem trabalha nela.</h2>
      </div>

      <div className="grade-2">
        <div className="card">
          <h3>O copo vai junto com o café</h3>
          <p className="texto-cinza">
            Copos descartáveis, inclusive os de papel, têm uma película plástica por dentro. Em contato
            com água a 85–90 °C, um copo de 100 ml liberou cerca de 25 mil micropartículas de plástico em
            15 minutos. Trocar o descartável por uma caneca reduz o resíduo e essa exposição ao mesmo
            tempo.
          </p>
          <p className="fonte">Fonte: Ranjan, Joseph e Goel (2021), Journal of Hazardous Materials</p>
        </div>

        <div className="card">
          <h3>Descartar certo precisa ser fácil</h3>
          <p className="texto-cinza">
            A maioria das pessoas descarta errado por pressa ou dúvida, e não por descaso. O Reduza
            registra os pontos de coleta de cada setor e o resultado de inspeções simples do descarte. O
            placar é por setor, nunca por pessoa.
          </p>
          <div className="coletores">
            {coletores.map((c) => (
              <span className="coletor" key={c.nome}>
                <i style={{ background: c.cor }}></i>
                {c.nome}
              </span>
            ))}
          </div>
          <p className="fonte">Cores dos coletores: Resolução CONAMA nº 275/2001</p>
        </div>
      </div>

      <div className="ods">
        {ods.map((item) => (
          <div className="ods-item" key={item.numero}>
            <span className="ods-numero">{item.numero}</span>
            <span>{item.nome}</span>
          </div>
        ))}
      </div>
    </section>
  )
}
