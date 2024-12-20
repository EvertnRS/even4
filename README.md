# Even4

## Descrição

O Even4 é uma aplicação desenvolvida como parte da disciplina de Programação 3. O projeto tem como objetivo gerenciar eventos, incluindo funcionalidades para inscrição, gestão de subeventos, submissão de artigos e muito mais.

## Funcionalidades

- **Inscrições**:

  - **Sessão**:
    - **Abertura**: Detalhes sobre a cerimônia de abertura.
    - **Apresentação de Comemoração**: Sessões especiais para celebrações.
    - **Seminário Principal**: Organização e gestão do evento principal.
    - **Artigos Estruturados**: Compartilhamento e discussão de artigos submetidos pelos participantes.

- **Eventos**: Cadastro, edição e gerenciamento completo dos eventos disponíveis, com suporte a datas e locais personalizados.

- **SubEventos**: Possibilita a criação de subeventos específicos vinculados a eventos principais, com funcionalidades semelhantes de gestão.

- **Sessões**: Organização de sessões específicas dentro de eventos, incluindo atribuição de palestrantes e participantes.

- **Submeter Artigo**: Plataforma para submissão e revisão de artigos acadêmicos e científicos, com sistema de avaliação integrado.

- **Perfil**: Permite que os usuários atualizem suas informações pessoais, visualizem inscrições e acompanhem o progresso dos eventos.

## Requisitos

- **Java** 21 ou superior
- **Maven** 3.9.4 ou superior
- **JavaFX** 19

## Instalação

1. Clone o repositório:

   ```bash
   git clone https://github.com/EvertnRS/even4.git
   ```

2. Navegue até o diretório do projeto:

   ```bash
   cd even4
   ```

3. Compile o projeto usando o Maven:

   ```bash
   mvn clean install
   ```

## Uso

Após a instalação, você pode executar a aplicação com o seguinte comando:

```bash
mvn javafx:run
```

A aplicação inclui uma interface gráfica intuitiva, suportada pelo JavaFX, para facilitar a interação do usuário. Após o login, você poderá:

- Inscrever-se em eventos.
- Submeter artigos para avaliação.
- Acompanhar suas inscrições e eventos em tempo real.

## Contribuição

1. Faça um fork deste repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas alterações (`git commit -m 'Adiciona nova feature'`).
4. Faça o push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
