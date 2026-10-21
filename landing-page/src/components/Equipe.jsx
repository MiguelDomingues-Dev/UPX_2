const membros = [
  { nome: 'Miguel', funcao: 'Gestor do projeto e engenheiro de software' },
  { nome: 'Rafael', funcao: 'Redes e banco de dados' },
  { nome: 'Thiago', funcao: 'Scrum Master e Product Owner' },
  { nome: 'Marcos', funcao: 'Requisitos e viabilidade' },
  { nome: 'Luciana', funcao: 'Front-end e back-end' },
]

export default function Equipe() {
  return (
    <section id="equipe" className="secao faixa">
      <div className="secao-topo">
        <p className="subtitulo">Equipe DevNexus</p>
        <h2>Quem está construindo o Reduza</h2>
      </div>

      <div className="equipe">
        {membros.map((m) => (
          <div className="membro" key={m.nome}>
            <span className="avatar">{m.nome[0]}</span>
            <b>{m.nome}</b>
            <span className="texto-cinza">{m.funcao}</span>
          </div>
        ))}
      </div>

      <p className="orientacao">Orientação: Profa. Brenda Medina · Mentoria: Prof. Daniel Ohata</p>
    </section>
  )
}
