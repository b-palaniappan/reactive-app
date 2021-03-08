# Springboot Webflux Reactive app

## Local MongoDB database in docker
* download docker image locally `docker image pull mongo:bionic`
* run local docker container `docker run --rm --name local-mongo -p 27017:27017 -d -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=mongoPass mongo:bionic`

## Todo
* Global Error Handling
* Validation
* Security using JWT
* Binary communication protocol using RSocket.
