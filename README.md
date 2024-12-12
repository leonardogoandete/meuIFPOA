# meuIFPOA

## Descrição
Este projeto é um aplicativo Android desenvolvido para a disciplina de TCC2 afim de obter a conclusão do curso.
O objetivo é facilitar o acesso a informações e serviços para a comunidade do Instituto Federal do Rio Grande do Sul - Campus Porto Alegre.

## Tecnologias Utilizadas
- Java
- Kotlin
- Gradle

## Funcionalidades
- Download de documentos em PDF
- Visualização de notícias
- Visualização de notas
- Visualização de dados acadêmicos
- Visualização de cargas horárias

## Como Executar
1. Clone o repositório:
    ```sh
    git clone https://github.com/leonardogoandete/meuIFPOA.git
    ```
2. Abra o projeto no Android Studio.
3. Conecte um dispositivo Android ou inicie um emulador.
4. Execute o aplicativo.

## APIs
Link para a API REST: [meuifpoa-back](https://app.poa.ifrs.edu.br/meuifpoa/q/swagger-ui/)
Link para o repositório da API: [meuifpoa-back](https://github.com/leonardogoandete/meuifpoa-back/tree/okhttp3)

O aplicativo consome uma API RESTful desenvolvida em Java. As rotas disponíveis são:
- `GET /noticias` - Retorna todas as notícias cadastradas.
- `POST /noticias` - Retorna noticias com base no filtro ou quantidade de resultados.
- `GET /documento` - Retorna um documento em PDF codificado em base64.
- `POST /sync` - Realiza a sincronização de dados acadêmicos.

## Documentação Dokka
A documentação do código foi gerada utilizando a ferramenta Dokka. Para acessar a documentação, abra o arquivo `index.html` localizado na pasta `docs/dokka`.
Caso deseje gerar a documentação novamente, execute o comando:
```sh 
./gradlew dokka
```

## Documentação
- **Documentação do Firebase**: [https://firebase.google.com/docs](https://firebase.google.com/docs)
- **Documentação do Android**: [https://developer.android.com/docs](https://developer.android.com/docs)
- **Documentação do Retrofit**: [https://square.github.io/retrofit/](https://square.github.io/retrofit/)
- **Documentação do MPAndroidChart**: [https://weeklycoding.com/mpandroidchart/](https://weeklycoding.com/mpandroidchart/)
- **Documentação do Glide**: [https://github.com/bumptech/glide](https://github.com/bumptech/glide)


## Licença
Este projeto está licenciado sob a Licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
