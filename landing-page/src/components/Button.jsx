export default function Button({ href, tipo = 'primario', children }) {
  return (
    <a className={'btn btn-' + tipo} href={href}>
      {children}
    </a>
  )
}
