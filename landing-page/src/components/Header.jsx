import Logo from './Logo'
import Button from './Button'

export default function Header() {
  return (
    <header className="header">
      <div className="container header-conteudo">
        <Logo />
        <nav className="menu">
          <a href="#problema">O problema</a>
          <a href="#como-funciona">Como funciona</a>
          <a href="#telas">O sistema</a>
          <a href="#calculadora">Retorno</a>
          <a href="#planos">Planos</a>
          <a href="#equipe">Equipe</a>
        </nav>
        <Button href="https://github.com/MiguelDomingues-Dev/UPX_2">Ver no GitHub</Button>
      </div>
    </header>
  )
}
