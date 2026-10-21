import Logo from './Logo'

const referencias = [
  'ABREMA. Panorama dos Resíduos Sólidos no Brasil 2024. São Paulo: ABREMA, 2024.',
  'ABNT. NBR 14865: Copos plásticos descartáveis. Rio de Janeiro: ABNT.',
  'BRASIL. Lei nº 12.305, de 2 de agosto de 2010. Política Nacional de Resíduos Sólidos.',
  'BRASIL. Decreto nº 6.514, de 22 de julho de 2008. Infrações e sanções administrativas ao meio ambiente.',
  'CONAMA. Resolução nº 275, de 25 de abril de 2001. Código de cores para coletores.',
  'RANJAN, V. P.; JOSEPH, A.; GOEL, S. Microplastics and other harmful substances released from disposable paper cups into hot water. Journal of Hazardous Materials, v. 404, 124118, 2021.',
  'POORTINGA, W.; WHITAKER, L. Promoting the use of reusable coffee cups through environmental messaging, the provision of alternatives and financial incentives. Sustainability, v. 10, n. 3, 873, 2018.',
]

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-conteudo">
        <div>
          <Logo />
          <p className="texto-cinza footer-info">
            UPX2 · Usina de Projetos Experimentais II
            <br />
            Análise e Desenvolvimento de Sistemas
            <br />
            Centro Universitário Facens · Sorocaba/SP · 2026
          </p>
        </div>
        <div>
          <h3>Referências</h3>
          <ul className="referencias">
            {referencias.map((r) => (
              <li key={r}>{r}</li>
            ))}
          </ul>
        </div>
      </div>
    </footer>
  )
}
