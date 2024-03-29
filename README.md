# Springboot Webflux Reactive app

![GitHub](https://img.shields.io/github/license/b-palaniappan/reactive-app)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/b-palaniappan/reactive-app/gradle.yml)
![GitHub top language](https://img.shields.io/github/languages/top/b-palaniappan/reactive-app)
![GitHub last commit](https://img.shields.io/github/last-commit/b-palaniappan/reactive-app)

## Local MongoDB database in docker

* download docker image locally `docker image pull mongo:6`
* run local docker container `docker run --rm --name local-mongo -p 27017:27017 -d mongo:6`

## Todo

* ~~Global Error Handling~~
* ~~Validation~~
* ~~Basic Auth~~
* Security using JWT
* Binary communication protocol using RSocket. Will be different project with client / Server.

## Sample auth-user

* Create MongoDB database - `use reactiveApp`
* Create collection - `db.createCollection("userAuth", { } )`
* Create sample user record in MongoDB collection `userAuth`

```
db.userAuth.insertMany([
    {
        "_id": "a_zZmkxHSnBxZsgq1wteabp",
        "name": "John Doe",
        "username": "john",
        "password": "{bcrypt}$2y$10$2lkhtyLowEl5mex7UaUsbuMOgo968sVoCpwDCgPLyrVotzEZzUIuW",
        "authorities": ["ROLE_ADMIN", "ROLE_USER"],
        "plainPassword": "HelloWorld@123"
    },
    {
        "_id": "a_Vrroae7F2VxN9U3hGF1hZ",
        "name": "Jack Reacher",
        "username": "jack",
        "password": "{bcrypt}$2y$10$/TGZiKf8o4WodOxnsDsNWeUlilkFjdsyTNM5WgCw07p7Hp9AzU7ly",
        "authorities": ["ROLE_USER"],
        "plainPassword": "HelloWorld@123"
    }
])
```
