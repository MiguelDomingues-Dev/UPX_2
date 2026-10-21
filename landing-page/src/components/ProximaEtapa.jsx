const ideias = [
  {
    titulo: 'Diagnóstico',
    texto: 'Perguntas simples sobre porte, tipo de resíduo e volume mostram em que estágio a empresa está.',
  },
  {
    titulo: 'O que a lei pede',
    texto:
      'Uma lista do que se aplica à empresa: PGRS ou dispensa, MTR, certificado de destinação e logística reversa, cada item com a lei ao lado.',
  },
  {
    titulo: 'Quem coleta na região',
    texto:
      'Coletoras de Sorocaba e Votorantim por tipo de resíduo, com base em informação pública e a fonte de cada dado.',
  },
]

export default function ProximaEtapa() {
  return (
    <section className="secao">
      <div className="secao-topo">
        <p className="subtitulo">Próxima etapa</p>
        <h2>Para a empresa pequena que ainda não sabe por onde começar.</h2>
        <p className="descricao">
          Muitas micro e pequenas empresas não sabem o que a lei de resíduos pede delas. O Reduza vai
          orientar esse primeiro passo.
        </p>
      </div>

      <div className="grade-3">
        {ideias.map((item) => (
          <div className="card" key={item.titulo}>
            <span className="etiqueta">Em desenvolvimento</span>
            <h3>{item.titulo}</h3>
            <p className="texto-cinza">{item.texto}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
