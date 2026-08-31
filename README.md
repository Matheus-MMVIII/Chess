# Chess

A simple full-stack chess application built with Java, Maven, HTML, CSS, and JavaScript.

The project was developed to practice object-oriented programming, application architecture, HTTP communication, REST-style APIs, chess game modeling, and frontend-to-backend integration.

## Overview

The application provides a chess API implemented in Java and a lightweight web frontend that consumes the API and displays the chess board.

The backend is responsible for representing the chess game, managing pieces and their movements, validating game operations, and exposing the application through HTTP endpoints.

The frontend provides a visual interface for interacting with the chess board and communicating with the backend.

## Features

* Chess board representation
* Individual chess piece models
* Piece movement logic
* Game state management
* HTTP API
* JSON-based communication
* Web frontend
* Backend/frontend integration
* Unit tests
* Layered project structure

## Architecture

The backend is organized into separate responsibilities:

```text
src/
├── main/
│   └── java/
│       └── com/
│           └── chess/
│               ├── exception/
│               ├── http/
│               │   ├── handler/
│               │   └── util/
│               ├── model/
│               ├── service/
│               └── App.java
│
└── test/
    └── java/
        └── com/
            └── chess/
```

### Model

The `model` package contains the core entities used to represent the chess game:

* `Piece`
* `Pawn`
* `Rook`
* `Horse`
* `Bishop`
* `Queen`
* `King`
* `Table`
* `GameStatus`

This layer is responsible for representing the state and behavior of the chess domain.

### Service

The `service` package contains the application's business logic.

`ChessService` coordinates operations involving the chess game and acts as an intermediary between the HTTP layer and the domain model.

### HTTP

The `http` package contains the HTTP-related components responsible for exposing the chess functionality through the API.

```text
http/
├── handler/
└── util/
```

This separation keeps HTTP communication independent from the chess domain logic.

### Exception

The `exception` package contains application-specific exceptions used to handle invalid operations and errors.

## Frontend

The frontend is intentionally lightweight and does not use a frontend framework.

```text
frontend/
├── index.html
├── script.js
└── style.css
```

It uses:

* HTML5 for structure
* CSS3 for styling
* JavaScript for interaction and API communication

The frontend communicates with the Java backend through HTTP requests.

## Technologies

| Technology | Purpose                                 |
| ---------- | --------------------------------------- |
| Java 17    | Backend and chess logic                 |
| Maven      | Build and dependency management         |
| HTML5      | Frontend structure                      |
| CSS3       | Frontend styling                        |
| JavaScript | Frontend behavior and API communication |
| JUnit      | Testing                                 |
| Git        | Version control                         |

## Getting Started

### Prerequisites

Make sure the following are installed:

* Java 17 or newer
* Maven
* Git

Verify your Java installation:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

### Clone the repository

```bash
git clone https://github.com/Matheus-MMVIII/Chess.git
cd Chess
```

### Build the project

```bash
mvn clean package
```

### Run the application

Run the main application through your IDE or execute the generated application according to the Maven configuration.

The backend starts the HTTP server used by the frontend.

### Run tests

```bash
mvn test
```

## Project Structure

The project follows a layered architecture:

```text
             Frontend
                 |
                 | HTTP
                 v
        +------------------+
        |   HTTP Layer     |
        |    Handlers      |
        +------------------+
                 |
                 v
        +------------------+
        |   Service Layer  |
        |  ChessService    |
        +------------------+
                 |
                 v
        +------------------+
        |   Domain Model   |
        | Pieces / Table   |
        |   Game Status    |
        +------------------+
```

This structure makes it possible to evolve the application without coupling the chess rules directly to the HTTP implementation.

## Learning Goals

This project was created primarily as a learning project, with emphasis on:

* Object-oriented programming
* Encapsulation and inheritance
* Polymorphism
* Domain modeling
* Separation of responsibilities
* HTTP communication
* API development
* JSON serialization
* Backend/frontend integration
* Exception handling
* Unit testing
* Maven project management

## Future Improvements

Possible improvements for future versions include:

* Complete chess rule validation
* Check and checkmate detection
* Castling
* En passant
* Pawn promotion
* Draw detection
* Move history
* Player accounts
* Multiplayer games
* WebSocket communication
* Persistent game storage
* Improved frontend interface
* API documentation
* Integration tests
* Docker support
