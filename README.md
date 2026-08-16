# 🚀 Even4

Sistema desktop de gerenciamento de eventos desenvolvido em Java e JavaFX.

## 📋 Sobre o projeto

O **Even4** é uma plataforma voltada para a organização e o gerenciamento completo de eventos acadêmicos e corporativos. Ele centraliza a criação de eventos principais, sub-eventos, sessões, controle de participantes e até submissão de artigos em uma única interface gráfica intuitiva.

### Explicação simples

- **O que o projeto faz:** Permite o cadastro e gerenciamento de eventos, emissão de certificados e controle de usuários.
- **Para quem foi desenvolvido:** Organizadores de eventos, instituições de ensino e coordenadores de conferências.
- **Qual problema resolve:** Elimina a desorganização no controle de participantes, sessões e submissões de artigos, unificando tudo em um sistema local de fácil acesso.
- **Principais diferenciais:** Interface gráfica nativa com JavaFX, arquitetura organizada (MVC + Facade) e persistência robusta com PostgreSQL via JPA/Hibernate.

## 🎯 Objetivos

1. Facilitar o gerenciamento hierárquico de eventos, sub-eventos e sessões.
2. Simplificar o fluxo de submissão de artigos científicos e controle de participantes.
3. Automatizar a geração e visualização de certificados.

## ✨ Funcionalidades

- ✅ Gerenciamento de eventos e sub-eventos
- ✅ Controle de sessões e participantes
- ✅ Submissão de artigos científicos
- ✅ Geração e visualização de certificados
- ✅ Autenticação e controle de usuários
- ✅ Persistência de dados com JPA/Hibernate

## 🖥️ Demonstração

### Login
<a href="https://ibb.co/fdV6yBZC"><img src="https://i.ibb.co/xqK9BPwM/Captura-de-tela-de-2026-08-16-01-03-10.png" alt="Captura-de-tela-de-2026-08-16-01-03-10" border="0"></a>

### Tela Incial
<a href="https://ibb.co/RT4kxK60"><img src="https://i.ibb.co/CKs3ky97/Captura-de-tela-de-2026-08-16-01-02-46.png" alt="Captura-de-tela-de-2026-08-16-01-02-46" border="0"></a>

## 🛠️ Tecnologias utilizadas

### Front-end (Desktop)

- JavaFX
- FXML
- CSS

### Back-end

- Java (JDK 21)
- Maven
- JPA / Hibernate

### Banco de dados

- PostgreSQL

### Ferramentas

- Git
- GitHub
- GitHub Actions (CI)
- SonarCloud
- Docker
- Dotenv

## 📦 Pré-requisitos

Antes de começar, instale:

- Git
- Java JDK 21 ou superior
- Maven
- Docker (opcional)
- PostgreSQL (caso não utilize Docker)

Verifique as versões:

```bash
git --version
java -version
mvn -version
docker --version
```

## 🚀 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/evertnrs/even4.git
```

### 2. Entre na pasta

```bash
cd even4
```

### 3. Instale as dependências

```bash
mvn clean install -DskipTests
```

### 4. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
ENVIRONMENT=development
DB_URL=jdbc:postgresql://localhost:5432/even4
DB_USER=seu_usuario
DB_PASS=sua_senha
```

### 5. Execute o projeto

```bash
mvn javafx:run
```

A janela do **Even4** será aberta.

## 📖 Como usar

Após iniciar a aplicação:

1. Na tela de login (`loginScreen`), cadastre um usuário ou faça login.
2. Crie um novo evento.
3. Organize o cronograma criando sub-eventos e sessões.
4. Adicione participantes às sessões.
5. Gere certificados quando necessário.

## 🧪 Testes

O projeto utiliza **JUnit** para testes de unidade e integração.

Execute:

```bash
mvn test
```

A qualidade do código é monitorada pelo **SonarCloud**, integrado ao GitHub Actions.

## 🐳 Docker

### Construir a imagem

```bash
docker build -t even4-app .
```

### Executar o container (Linux)

> Recomendado utilizar o **Docker Engine** (`docker context use default`) para aplicações JavaFX.

```bash
xhost +SI:localuser:root

docker run -it --rm \
  --net=host \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v $(pwd)/.env:/app/.env \
  even4-app
```

Também é possível utilizar um `docker-compose.yml` para iniciar simultaneamente o banco PostgreSQL e a aplicação.

## 📁 Estrutura do projeto

```text
even4/
├── .github/
│   └── workflows/
├── src/
│   ├── main/
│   │   ├── java/br/upe/
│   │   │   ├── controller/
│   │   │   ├── facade/
│   │   │   ├── persistence/
│   │   │   ├── ui/
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── fxml/
│   │       ├── images/
│   │       └── stylesheets/
│   └── test/java/br/upe/
├── .env
├── .gitignore
├── Dockerfile
├── pom.xml
└── README.md
```

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

Consulte o arquivo `LICENSE` para mais detalhes.

## 🤝 Contribuindo

Contribuições são bem-vindas.

Antes de contribuir:

- siga o padrão descrito em `CONTRIBUTING.md`;
- utilize **Conventional Commits**;
- concorde com o licenciamento MIT para suas contribuições.

## ⭐ Apoie o projeto

Se este projeto foi útil para você:

- ⭐ Dê uma estrela no repositório.
- 🐛 Reporte problemas.
- 💡 Sugira melhorias.
- 🤝 Envie contribuições.
- 📢 Compartilhe o projeto.

Obrigado pelo apoio! ❤️

## 📞 Suporte

Ao abrir uma Issue, informe:

- descrição do problema;
- passos para reproduzir;
- comportamento esperado;
- comportamento obtido;
- logs de erro;
- sistema operacional;
- versão do projeto.

## 📚 Documentação

A documentação do projeto está organizada em:

- README (documentação principal)
- `CONTRIBUTING.md` (guia de contribuição)
- `LICENSE` (licença)

---

Desenvolvido com ❤️ por **Everton**.