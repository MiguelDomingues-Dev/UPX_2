const menuToggle = document.getElementById('menuToggle');
const menu = document.getElementById('menu');

menuToggle.addEventListener('click', () => {
  const aberto = menu.classList.toggle('open');
  menuToggle.setAttribute('aria-expanded', aberto);
});

menu.querySelectorAll('a').forEach((link) => {
  link.addEventListener('click', () => {
    menu.classList.remove('open');
    menuToggle.setAttribute('aria-expanded', false);
  });
});

const revealElements = document.querySelectorAll('.reveal');
const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.12 });

revealElements.forEach((element) => observer.observe(element));

// Galeria das telas do sistema
const telas = [
  {
    titulo: 'Painel do mês',
    imagem: 'painel.png',
    texto: 'O resumo que o gestor abre primeiro: total do mês comparado com o anterior, custo de destinação em reais, taxa de descarte correto e setor que mais gera resíduo.',
  },
  {
    titulo: 'Registrar resíduo',
    imagem: 'registro.png',
    texto: 'Setor, categoria, quilos e data. A bolinha mostra a cor do coletor, e o sistema confirma o total do mês ao salvar.',
  },
  {
    titulo: 'Histórico',
    imagem: 'historico.png',
    texto: 'Todos os registros, com filtro por período, setor e categoria. O botão Exportar CSV gera uma planilha que abre no Excel.',
  },
  {
    titulo: 'Metas de redução',
    imagem: 'metas.png',
    texto: 'Cada meta parte da média dos meses anteriores. 100% quer dizer alvo atingido. O mês em andamento só conta quando fecha.',
  },
  {
    titulo: 'Piloto de descartáveis',
    imagem: 'descartaveis.png',
    texto: 'Consumo de copos mês a mês, gasto em reais e custo por uso do copo e da caneca. O simulador mostra em quantos meses a troca se paga.',
  },
  {
    titulo: 'Inspeções de descarte',
    imagem: 'inspecoes.png',
    texto: 'Alguém confere os coletores e marca se o descarte estava correto. O placar é por setor, nunca por pessoa.',
  },
];

const tabs = document.querySelectorAll('.tab');
const telaImagem = document.getElementById('telaImagem');
const telaBarra = document.getElementById('telaBarra');
const telaTitulo = document.getElementById('telaTitulo');
const telaTexto = document.getElementById('telaTexto');

tabs.forEach((tab) => {
  tab.addEventListener('click', () => {
    const tela = telas[Number(tab.dataset.tela)];

    tabs.forEach((t) => {
      t.classList.toggle('active', t === tab);
      t.setAttribute('aria-selected', t === tab);
    });

    telaImagem.src = `assets/imagens/${tela.imagem}`;
    telaImagem.alt = tela.titulo;
    telaBarra.textContent = `Reduza · ${tela.titulo}`;
    telaTitulo.textContent = tela.titulo;
    telaTexto.textContent = tela.texto;
  });
});

// Pré-carrega as outras telas para a troca ser instantânea
window.addEventListener('load', () => {
  telas.forEach((tela) => {
    new Image().src = `assets/imagens/${tela.imagem}`;
  });
});

// QR code aponta para a pasta do projeto no Google Drive
const qrCode = document.getElementById('qrCode');
const linkDrive = 'https://drive.google.com/drive/folders/1dUNGdxdcWQ1Qt3oy-vyQ6WFCJOCS_iZ2';

qrCode.src = `https://api.qrserver.com/v1/create-qr-code/?size=440x440&qzone=4&ecc=M&color=10302A&bgcolor=FFFFFF&data=${encodeURIComponent(linkDrive)}`;
qrCode.alt = 'QR code para a pasta do projeto Reduza no Google Drive';
