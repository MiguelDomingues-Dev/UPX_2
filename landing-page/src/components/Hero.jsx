import Button from './Button'
import Janela from './Janela'

export default function Hero() {
  return (
    <section className="hero">
      <div className="hero-texto">
        <p className="subtitulo">Gestão de resíduos para empresas</p>
        <h1>Sua empresa já paga para jogar resíduo fora. Saiba onde economizar.</h1>
        <p className="descricao">
          O Reduza mostra, por setor, quanto resíduo a empresa gera e quanto isso custa: a destinação
          paga, as compras de descartáveis e o reciclável que vira rejeito por descarte errado. Com os
          números, a empresa gasta menos e cumpre a lei com menos trabalho.
        </p>
        <div className="hero-botoes">
          <Button href="#calculadora">Calcular o retorno</Button>
          <Button href="#planos" tipo="secundario">Ver os planos</Button>
        </div>
        <p className="hero-nota">Projeto da disciplina UPX2, Centro Universitário Facens, 2026.</p>
      </div>

      <div className="hero-imagem">
        <Janela titulo="Painel do mês" imagem="/telas/painel.png" alt="Painel do mês do Reduza" />
      </div>
    </section>
  )
}
