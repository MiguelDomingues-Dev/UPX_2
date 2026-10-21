// moldura de janela em volta dos prints do sistema
export default function Janela({ titulo, imagem, alt }) {
  return (
    <div className="janela">
      <div className="janela-barra">
        <i></i>
        <i></i>
        <i></i>
        <span>Reduza · {titulo}</span>
      </div>
      <img src={imagem} alt={alt} />
    </div>
  )
}
