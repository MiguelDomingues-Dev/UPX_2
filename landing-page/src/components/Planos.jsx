import Button from './Button'

export default function Planos() {
  return (
    <section id="planos" className="secao">
      <div className="secao-topo">
        <p className="subtitulo">Planos</p>
        <h2>Teste por 1 mês sem pagar nada.</h2>
        <p className="descricao">
          No primeiro mês, a empresa usa o Reduza com os próprios dados e vê a economia antes de decidir.
          Depois, escolhe o plano.
        </p>
      </div>

      <div className="planos">
        <div className="plano">
          <h3>Essencial</h3>
          <p className="texto-cinza">Para começar a medir.</p>
          <p className="preco">
            R$ 49<small>/mês</small>
          </p>
          <ul className="lista-check">
            <li>Até 3 setores</li>
            <li>Registro de resíduos por categoria</li>
            <li>Painel do mês e histórico</li>
            <li>Comparativo entre meses</li>
            <li>Metas por setor</li>
          </ul>
          <Button href="#contato" tipo="secundario">Começar o mês grátis</Button>
        </div>

        <div className="plano destaque">
          <h3>Profissional</h3>
          <span className="selo">Mostra a economia em reais</span>
          <p className="preco">
            R$ 99<small>/mês</small>
          </p>
          <ul className="lista-check">
            <li>Setores ilimitados</li>
            <li>Tudo do plano Essencial</li>
            <li>Custo de destinação e de compras em reais</li>
            <li>Pontos de coleta e inspeção do descarte</li>
            <li>Comparação entre descartável e reutilizável</li>
            <li>Relatórios prontos para auditoria</li>
          </ul>
          <Button href="#contato">Começar o mês grátis</Button>
        </div>
      </div>

      <p className="aviso-planos">Valores provisórios, em validação durante o piloto do projeto.</p>
    </section>
  )
}
