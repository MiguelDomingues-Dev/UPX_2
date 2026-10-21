import { useState } from 'react'

const PRECO_PLANO = 99
const PESO_COPO = 1.8 // gramas

function reais(valor, casas = 0) {
  return valor.toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: casas,
    maximumFractionDigits: casas,
  })
}

function numero(valor, casas = 1) {
  return valor.toLocaleString('pt-BR', { minimumFractionDigits: casas, maximumFractionDigits: casas })
}

export default function Calculadora() {
  const [form, setForm] = useState({
    rejeito: 2000,
    custoKg: 0.3,
    porcentagem: 15,
    vendaKg: 0.4,
    pessoas: 25,
    copos: 4,
    dias: 22,
    pacote: 6.5,
    caneca: 15,
    horas: 6,
    valorHora: 30,
  })

  function mudar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  // converte tudo pra número (campo vazio vira 0)
  const v = {}
  for (const campo in form) {
    v[campo] = Number(form[campo]) || 0
  }

  const recuperado = (v.rejeito * v.porcentagem) / 100
  const ecoDestinacao = recuperado * v.custoKg
  const ecoVenda = recuperado * v.vendaKg
  const coposMes = v.pessoas * v.copos * v.dias
  const ecoCopos = (coposMes * v.pacote) / 100
  const ecoHoras = v.horas * v.valorHora

  const total = ecoDestinacao + ecoVenda + ecoCopos + ecoHoras
  const liquido = total - PRECO_PLANO
  const custoCanecas = v.pessoas * v.caneca
  const plasticoAno = (coposMes * PESO_COPO * 12) / 1000

  const barras = [
    { nome: 'Rejeito que deixa de ser pago', valor: ecoDestinacao },
    { nome: 'Venda do reciclável recuperado', valor: ecoVenda },
    { nome: 'Copos que deixam de ser comprados', valor: ecoCopos },
    { nome: 'Horas de relatório economizadas', valor: ecoHoras },
  ]
  const maior = Math.max(ecoDestinacao, ecoVenda, ecoCopos, ecoHoras, PRECO_PLANO, 1)

  let prazoCanecas = ''
  if (custoCanecas > 0 && liquido > 0) {
    const meses = Math.round(custoCanecas / liquido)
    if (custoCanecas / liquido < 1) prazoCanecas = 'menos de 1 mês'
    else prazoCanecas = 'cerca de ' + meses + (meses === 1 ? ' mês' : ' meses')
  }

  return (
    <section id="calculadora" className="secao faixa">
      <div className="secao-topo">
        <p className="subtitulo">Calculadora de retorno</p>
        <h2>Quanto o Reduza devolve para a sua empresa?</h2>
        <p className="descricao">
          Os valores preenchidos são exemplos. Troque pelos números da sua empresa e veja quanto o
          sistema economiza em relação ao preço do plano.
        </p>
      </div>

      <div className="calc">
        <form className="calc-form" onSubmit={(e) => e.preventDefault()}>
          <fieldset>
            <legend>Resíduos</legend>
            <label>
              Rejeito por mês (kg)
              <input type="number" name="rejeito" min="0" value={form.rejeito} onChange={mudar} />
            </label>
            <label>
              Custo de destinação do rejeito (R$ por kg)
              <input type="number" name="custoKg" min="0" step="0.01" value={form.custoKg} onChange={mudar} />
            </label>
            <label>
              Parte do rejeito que é reciclável descartado errado (%)
              <input type="number" name="porcentagem" min="0" max="100" value={form.porcentagem} onChange={mudar} />
            </label>
            <label>
              Preço de venda do reciclável (R$ por kg)
              <input type="number" name="vendaKg" min="0" step="0.01" value={form.vendaKg} onChange={mudar} />
            </label>
          </fieldset>

          <fieldset>
            <legend>Copos descartáveis</legend>
            <label>
              Pessoas que usam copos
              <input type="number" name="pessoas" min="0" value={form.pessoas} onChange={mudar} />
            </label>
            <label>
              Copos por pessoa, por dia
              <input type="number" name="copos" min="0" step="0.5" value={form.copos} onChange={mudar} />
            </label>
            <label>
              Dias úteis no mês
              <input type="number" name="dias" min="1" max="31" value={form.dias} onChange={mudar} />
            </label>
            <label>
              Pacote com 100 copos (R$)
              <input type="number" name="pacote" min="0" step="0.01" value={form.pacote} onChange={mudar} />
            </label>
            <label className="inteiro">
              Preço de uma caneca reutilizável (R$)
              <input type="number" name="caneca" min="0" step="0.01" value={form.caneca} onChange={mudar} />
            </label>
          </fieldset>

          <fieldset>
            <legend>Relatórios</legend>
            <label>
              Horas por mês montando relatórios
              <input type="number" name="horas" min="0" step="0.5" value={form.horas} onChange={mudar} />
            </label>
            <label>
              Custo da hora de trabalho (R$)
              <input type="number" name="valorHora" min="0" value={form.valorHora} onChange={mudar} />
            </label>
          </fieldset>
        </form>

        <div className="calc-resultado">
          <div>
            <span className="rotulo">Economia estimada por mês</span>
            <p className="total">{reais(total)}</p>
            <span className="texto-cinza">{reais(total * 12)} por ano</span>
          </div>

          <div className="barras">
            {barras.map((b) => (
              <div className="barra-linha" key={b.nome}>
                <span>{b.nome}</span>
                <div className="barra">
                  <div className="barra-cheia" style={{ width: (b.valor / maior) * 100 + '%' }}></div>
                </div>
                <b>{reais(b.valor)}</b>
              </div>
            ))}
            <div className="barra-linha custo">
              <span>Plano Profissional</span>
              <div className="barra">
                <div className="barra-cheia ocre" style={{ width: (PRECO_PLANO / maior) * 100 + '%' }}></div>
              </div>
              <b>− {reais(PRECO_PLANO)}</b>
            </div>
          </div>

          <div className="resumo">
            <div>
              <span className="rotulo">Retorno líquido por mês</span>
              <p className="resumo-valor">{reais(liquido)}</p>
            </div>
            <div>
              <span className="rotulo">Para cada R$ 1 do plano</span>
              <p className="resumo-valor">{reais(total / PRECO_PLANO, 2)}</p>
            </div>
            <div>
              <span className="rotulo">Rejeito evitado por ano</span>
              <p className="resumo-valor">{numero((recuperado * 12) / 1000)} t</p>
            </div>
          </div>

          {liquido <= 0 ? (
            <p className="conclusao">
              Com esses números, o plano custa mais do que a economia estimada. Vale conferir os dados da
              empresa ou começar pelo plano Essencial.
            </p>
          ) : (
            <p className="conclusao">
              Com esses números, a empresa economiza <strong>{reais(total)} por mês</strong>. Descontado o
              plano Profissional, sobram <strong>{reais(liquido)}</strong>, e cada R$ 1 pago no plano volta
              como <strong>{reais(total / PRECO_PLANO, 2)}</strong>.
              {prazoCanecas && ` As canecas (${reais(custoCanecas)}) se pagam em ${prazoCanecas}.`} A empresa
              ainda deixa de descartar {numero(plasticoAno)} kg de plástico em copos por ano.
            </p>
          )}

          <p className="calc-obs">
            A economia de relatórios considera que o Reduza gera os dados prontos e elimina o tempo de
            consolidar planilhas. Os resultados são estimativas e dependem dos números reais de cada
            empresa.
          </p>
        </div>
      </div>
    </section>
  )
}
