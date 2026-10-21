import Header from './components/Header'
import Hero from './components/Hero'
import Problema from './components/Problema'
import ComoFunciona from './components/ComoFunciona'
import Telas from './components/Telas'
import ProximaEtapa from './components/ProximaEtapa'
import Calculadora from './components/Calculadora'
import Planos from './components/Planos'
import Qualidade from './components/Qualidade'
import Equipe from './components/Equipe'
import Contato from './components/Contato'
import Footer from './components/Footer'
import './App.css'

function App() {
  return (
    <>
      <Header />
      <main className="container" id="inicio">
        <Hero />
        <Problema />
        <ComoFunciona />
        <Telas />
        <ProximaEtapa />
        <Calculadora />
        <Planos />
        <Qualidade />
        <Equipe />
        <Contato />
      </main>
      <Footer />
    </>
  )
}

export default App
