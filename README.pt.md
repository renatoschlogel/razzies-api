# Razzies API

Uma API RESTful desenvolvida em Spring Boot para identificar os produtores com o menor e o maior intervalo entre vitórias consecutivas no "Golden Raspberry Awards" (Razzie Awards) Cateoria de "Pior Filme".

---

[Read in English](README.md)

---

## Visão Geral

Este projeto implementa uma API para analisar dados históricos dos Golden Raspberry Awards (conhecidos como Razzies). O objetivo principal é identificar e expor os produtores que ganharam o prêmio de "Pior Filme" com os menores e maiores intervalos de tempo entre suas vitórias consecutivas.

---

## 🚀 Tecnologias Principais

* **Java 21+**: Linguagem de programação.
* **Spring Boot 3.x**: Framework principal para construção da API.
* **Spring Data JPA & Hibernate**: Para persistência de dados.
* **H2 Database**: Banco de dados em memória para testes e desenvolvimento.
* **Lombok**: Para reduzir código boilerplate.
* **JUnit 5 & MockMvc**: Para testes de integração.
* **Gradle**: Ferramenta de automação de build e gerenciamento de dependências.
* **Springdoc OpenAPI (Swagger UI)**: Para documentação e teste interativo da API.

---

## 🏛️ Arquitetura e Decisões de Design (Clean Architecture)

O projeto é estruturado em camadas distintas para garantir a separação de interesses e a independência da lógica de negócio em relação a frameworks ou detalhes de infraestrutura.

* **`domain` (Coração da Aplicação)**
    * Contém a lógica de negócio pura e os modelos de domínio (`model`).
    * É independente de frameworks e do banco de dados.
    * Define interfaces (`ports`) que as camadas externas devem implementar para interagir com o domínio.
    * Inclui os casos de uso (`usecases`) que orquestram a lógica principal.

* **`api` (Interface de Usuário)**
    * Responsável por expor a funcionalidade da aplicação via endpoints REST.
    * O `Controller` recebe requisições HTTP, valida entradas e invoca o `UseCase` apropriado do domínio.
    * Converte modelos de domínio retornados pelo `UseCase` para DTOs do contrato da API.
    * Contém uma subpasta `config` para configurações específicas da API, como a configuração do Swagger/OpenAPI.

* **`infra` (Detalhes de Implementação)**
    * Implementa os detalhes técnicos e as interfaces (`ports`) definidas na camada `domain`.
    * Contém a estrutura de banco de dados (`db`), que inclui as entidades JPA (`entities`), os repositórios Spring Data (`repositories`), e uma subpasta **`config`** para carregadores de dados como o `LoaderCSVToDatabase`.

---

## ⚙️ Fluxo de Dados e Lógica de Negócio

O fluxo completo para a obtenção dos intervalos de produtores segue estes passos:

1.  **Carga de Dados (`LoaderCSVToDatabase`)**:
    * Na inicialização da aplicação, o `LoaderCSVToDatabase` lê um arquivo CSV (caminho configurável via `app.data.csv-path-movies-import`).
    * Ele processa cada linha, convertendo os dados em `MovieEntity` e persistindo-os no banco de dados H2.

2.  **Requisição da API**:
    * Um cliente envia uma requisição `GET` para o endpoint `/api/v1/worst-movies/producers-intervals`.

3.  **Controller (`WorstMoviesController`)**:
    * Recebe a requisição HTTP.
    * Delega a execução da lógica de negócio para o caso de uso (`GetProducersWithAwardIntervalsUseCase`).

4.  **Caso de Uso (`GetProducersWithAwardIntervalsUseCase`)**:
    * Consulta todos os filmes vencedores através do `WinningMovieRepositoryPort`.
    * **Normalização de Produtores**: A string de produtores de cada filme é parseada, separando-os em produtores individuais e removendo caracter invalidos.
    * **Agrupamento por Produtor**: Os anos de vitória são agrupados para cada produtor individual.
    * **Cálculo de Intervalos**: Para cada produtor com duas ou mais vitórias, o algoritmo calcula todos os intervalos entre anos de vitória consecutivos.
    * **Identificação de Extremos**: Os menores e maiores intervalos são identificados.
    * **Coleta de Resultados**: Todas as entradas de produtores que correspondem aos intervalos mínimo e máximo são coletadas em listas separadas.
    * Retorna um objeto de domínio `AwardIntervalsResult` contendo as listas de produtores `min` e `max`.

5.  **Resposta da API**:
    * O `WorstMoviesController` recebe o `AwardIntervalsResult` do `UseCase`.
    * Mapeia este objeto de domínio e suas listas de produtores para os DTOs de resposta (`WorstMovieProducersIntervalsResponseDto` e `WorstMovieProducerIntervalDto`).
    * Retorna a resposta em formato JSON com status `200 OK`.

---

## 💡 Endpoints da API

### `GET /api/v1/worst-movies/producers-intervals`

Retorna uma lista de produtores com o menor e o maior intervalo entre duas vitórias consecutivas no prêmio de "Pior Filme".

* **URL Base**: `http://localhost:8080` (em ambiente de desenvolvimento)
* **Endpoint**: `/api/v1/worst-movies/producers-intervals`
* **Método**: `GET`
* **Exemplo de Resposta de Sucesso (Status: 200 OK)**:

    ```json
    {
      "min": [
        {
          "producer": "Lorenzo di Bonaventura",
          "interval": 2,
          "previousWin": 2009,
          "followingWin": 2011
        }
      ],
      "max": [
        {
          "producer": "Buzz Feitshans",
          "interval": 9,
          "previousWin": 1985,
          "followingWin": 1994
        }
      ]
    }
    ```

### 📄 Documentação Interativa da API (Swagger UI)

A API é documentada usando **Springdoc OpenAPI**

* **URL do Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🚀 Como Rodar a Aplicação

1.  **Pré-requisitos**:
    * Java Development Kit (JDK) 21 ou superior.
    * **Gradle 8.0** ou superior.
    * (Opcional) Uma IDE como IntelliJ IDEA ou VS Code com suporte a Spring Boot.

2.  **Clone o repositório**:
    ```bash
    git clone [https://github.com/renatoschlogel/razzies-api.git](https://github.com/renatoschlogel/razzies-api.git)
    cd razzies-api
    ```

3.  **Construa o projeto**:
    ```bash
    ./gradlew clean build
    ```
    Isso compilará o código e baixará as dependências necessárias.

4.  **Execute a aplicação**:
    ```bash
    ./gradlew bootRun
    ```
    A aplicação será iniciada e estará disponível em `http://localhost:8080`. O CSV de dados padrão (`movielist.csv`) será carregado automaticamente na inicialização.

### Configurando o Caminho do CSV de Importação

Você pode configurar o caminho do arquivo CSV de importação através da propriedade `app.data.csv-path-movies-import`.

* **No `application.yml`:**
    ```yaml
    app:
      data:
        csv-path-movies-import: "classpath:/data/movielist.csv"
    ```
  Ou para um caminho absoluto:
    ```yaml
    app:
      data:
        csv-path-movies-import: "file:/caminho-completo-seu-arquivo.csv"
    ```

* **Via linha de comando (ao executar com `./gradlew bootRun`):**
    ```bash
    ./gradlew bootRun --args='--app.data.csv-path-movies-import=file:/caminho-para-meu-movielist.csv'
    ```

---

## 🧪 Testes e Validação

A qualidade da aplicação é garantida pelo teste de integração, `WorstMoviesControllerIntegrationTest`, que valida o fluxo completo da API.

### Executando os Testes

Para rodar todos os testes do projeto:

```bash
./gradlew test
```

### Configuração de Teste Integração Flexível

O teste de integração é configuravel e flexível:

* **Perfis de Teste**: Utiliza o perfil `test` (`@ActiveProfiles("test")`) para carregar configurações específicas do arquivo `src/test/resources/application-test.yml`.
* **Caminhos de Dados Configuráveis via Linha de Comando**:
  Para executar os testes com um CSV de entrada diferente e/ou um JSON de resultado esperado diferente, você pode passar as propriedades diretamente na linha de comando. 

    Exemplo de comando:
    ```bash
    ./gradlew test -Dapp.data.csv-path-movies-import="classpath:data/movies_list_custom.csv" \
                   -Dtest.data.expected-json-path="classpath:data/expected_response_custom.json"
    ```
  Neste exemplo:
    * `-Dapp.data.csv-path-movies-import`: Sobrescreve o caminho do CSV de entrada para o teste.
    * `-Dtest.data.expected-json-path`: Sobrescreve o caminho do JSON esperado para a validação do teste.

* **Validação Dinâmica**: O teste executa uma chamada real ao endpoint da API e compara o JSON de resposta com o conteúdo do arquivo JSON esperado, conforme carregado da configuração.

Essa abordagem permite testar o comportamento da API com diferentes conjuntos de dados de forma isolada, sem a necessidade de alterar o código-fonte dos testes, apenas ajustando as propriedades na linha de comando ou no arquivo de configuração de teste.

---

## Licença

Este projeto está licenciado sob a licença [MIT License](https://opensource.org/licenses/MIT).

---

## Contato

Renato Welinton Schlogel - [renato.s@outlook.com](mailto:renato.s@outlook.com)

[GitHub: renatoschlogel](https://github.com/renatoschlogel)

---


