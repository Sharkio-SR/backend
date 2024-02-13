# 🦈 Sharkio - backend

## Requirements
To run the backend you'll need :

- java openjdk 21.0.2 2024-01-16
- Apache Maven 3.6.3
- Docker

## Run backend locally
To run the backend locally,  from project root :
```shell
mvn springboot:run
```

## Run tests
To run the backend tests, run from project root :
```shell
mvn springboot:test-run
```

## Run with docker
To run the project with docker, use these commands from project root
```shell
docker build -t sharkio/backend .
```

```shell
docker run -d -name backend -p 8080:8080 sharkio/backend
```