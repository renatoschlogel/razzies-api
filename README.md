# Razzies API

A RESTful API developed in Spring Boot to identify producers with the shortest and longest intervals between consecutive "Worst Picture" wins at the "Golden Raspberry Awards" (Razzie Awards) Category.

---

[Leia em Português](README.pt.md)

---

## Overview

This project implements an API to analyze historical data from the Golden Raspberry Awards (known as Razzies). The primary goal is to identify and expose producers who won the "Worst Picture" award with the shortest and longest time intervals between their consecutive victories.

---

## 🚀 Key Technologies

* **Java 21+**: Programming language.
* **Spring Boot 3.x**: Main framework for API construction.
* **Spring Data JPA & Hibernate**: For data persistence.
* **H2 Database**: In-memory database for development and testing.
* **Lombok**: To reduce boilerplate code.
* **JUnit 5 & MockMvc**: For integration testing.
* **Gradle**: Build automation and dependency management tool.
* **Springdoc OpenAPI (Swagger UI)**: For API documentation and interactive testing.

---

## 🏛️ Architecture and Design Decisions (Clean Architecture)

The project is structured into distinct layers to ensure separation of concerns and the independence of business logic from frameworks or infrastructure details.

* **`domain` (Application Core)**
    * Contains pure business logic and domain models (`model`).
    * It is independent of frameworks and the database.
    * Defines interfaces (`ports`) that external layers must implement to interact with the domain.
    * Includes the use cases (`usecases`) that orchestrate the main logic.

* **`api` (User Interface)**
    * Responsible for exposing application functionality via REST endpoints.
    * The `Controller` receives HTTP requests, validates inputs, and invokes the appropriate `UseCase` from the domain.
    * Converts domain models returned by the `UseCase` to API contract DTOs.
    * Contains a `config` subfolder for API-specific configurations, such as Swagger/OpenAPI setup.

* **`infra` (Implementation Details)**
    * Implements the technical details and the interfaces (`ports`) defined in the `domain` layer.
    * Contains the database structure (`db`), which includes JPA entities (`entities`), Spring Data repositories (`repositories`), and a **`config`** subfolder for data loaders like `LoaderCSVToDatabase`.

---

## ⚙️ Data Flow and Business Logic

The complete flow for obtaining producer award intervals follows these steps:

1.  **Data Loading (`LoaderCSVToDatabase`)**:
    * Upon application startup, `LoaderCSVToDatabase` reads a CSV file (path configurable via `app.data.csv-path-movies-import`).
    * It processes each row, converting the data into `MovieEntity` and persisting it in the H2 database.

2.  **API Request**:
    * A client sends a `GET` request to the `/api/v1/worst-movies/producers-intervals` endpoint.

3.  **Controller (`WorstMoviesController`)**:
    * Receives the HTTP request.
    * Delegates the execution of business logic to the use case (`GetProducersWithAwardIntervalsUseCase`).

4.  **Use Case (`GetProducersWithAwardIntervalsUseCase`)**:
    * Queries all winning movies through the `WinningMovieRepositoryPort`.
    * **Producer Normalization**: The producer string from each movie is parsed, separating individual producers and removing invalid characters.
    * **Grouping by Producer**: Victory years are grouped for each individual producer.
    * **Interval Calculation**: For each producer with two or more wins, the algorithm calculates all intervals between consecutive award years.
    * **Extreme Identification**: The shortest and longest intervals are identified.
    * **Result Collection**: All producer entries corresponding to the minimum and maximum interval values are collected into separate lists.
    * Returns an `AwardIntervalsResult` domain object containing the `min` and `max` producer lists.

5.  **API Response**:
    * The `WorstMoviesController` receives the `AwardIntervalsResult` from the `UseCase`.
    * Maps this domain object and its internal producer lists to the API contract DTOs (`WorstMovieProducersIntervalsResponseDto` and `WorstMovieProducerIntervalDto`).
    * Returns the response in JSON format with `200 OK` status.

---

## 💡 API Endpoints

### `GET /api/v1/worst-movies/producers-intervals`

Returns a list of producers with the shortest and longest intervals between two consecutive "Worst Picture" wins.

* **Base URL**: `http://localhost:8080` (in development environment)
* **Endpoint**: `/api/v1/worst-movies/producers-intervals`
* **Method**: `GET`
* **Example Success Response (Status: 200 OK)**:

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

### 📄 Interactive API Documentation (Swagger UI)

The API is documented using **Springdoc OpenAPI**.

* **Swagger UI URL**: `http://localhost:8080/swagger-ui.html`

---

## 🚀 How to Run the Application

1.  **Prerequisites**:
    * Java Development Kit (JDK) 21 or higher.
    * **Gradle 8.0** or higher.
    * (Optional) An IDE like IntelliJ IDEA or VS Code with Spring Boot support.

2.  **Clone the repository**:
    ```bash
    git clone [https://github.com/renatoschlogel/razzies-api.git](https://github.com/renatoschlogel/razzies-api.git)
    cd razzies-api
    ```

3.  **Build the project**:
    ```bash
    ./gradlew clean build
    ```
    This will compile the code and download necessary dependencies.

4.  **Execute the application**:
    ```bash
    ./gradlew bootRun
    ```
    The application will start and be available at `http://localhost:8080`. The default data CSV (`movielist.csv`) will be loaded automatically on startup.

### Configuring the CSV Import Path

You can configure the CSV import file path via the `app.data.csv-path-movies-import` property.

* **In `application.yml`:**
    ```yaml
    app:
      data:
        csv-path-movies-import: "classpath:/data/movielist.csv"
    ```
  Or for an absolute path:
    ```yaml
    app:
      data:
        csv-path-movies-import: "file:/full-path-to-your-file.csv"
    ```

* **Via command line (when running with `./gradlew bootRun`):**
    ```bash
    ./gradlew bootRun --args='--app.data.csv-path-movies-import=file:/path-to-my-movielist.csv'
    ```

---

## 🧪 Tests and Validation

Application quality is ensured by the integration test, `WorstMoviesControllerIntegrationTest`, which validates the complete API flow.

### Running Tests

To run all project tests:

```bash
./gradlew test
```

### Flexible Integration Test Configuration

The integration test is configurable and flexible:

* **Test Profiles**: It uses the `test` profile (`@ActiveProfiles("test")`) to load specific configurations from the `src/test/resources/application-test.yml` file.
* **Configurable Data Paths via Command Line**:
  To run tests with a different input CSV and/or an expected JSON result, you can pass the properties directly on the command line.

  Example command:
    ```bash
    ./gradlew test -Dapp.data.csv-path-movies-import="classpath:data/movies_list_custom.csv" \
                   -Dtest.data.expected-json-path="classpath:data/expected_response_custom.json"
    ```
  In this example:
    * `-Dapp.data.csv-path-movies-import`: Overrides the input CSV path for the test.
    * `-Dtest.data.expected-json-path`: Overrides the expected JSON path for test validation.

* **Dynamic Validation**: The test performs a real call to the API endpoint and compares the JSON response with the expected JSON content, as loaded from the configuration.

This approach allows testing the API's behavior with different data sets in isolation, without needing to change the test source code, only by adjusting properties on the command line or in the test configuration file.

---

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## Contact

Renato Welinton Schlogel - [renato.s@outlook.com](mailto:renato.s@outlook.com)

[GitHub: renatoschlogel](https://github.com/renatoschlogel)

---