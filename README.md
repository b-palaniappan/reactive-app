# Springboot Webflux Reactive app

## Local MongoDB database in docker
* download docker image locally `docker image pull mongo:bionic`
* run local docker container `docker run --rm --name local-mongo -p 27017:27017 -d mongo:bionic`

## Todo
* ~~Global Error Handling~~
* ~~Validation~~
* ~~Basic Auth~~  
* Security using JWT
* Binary communication protocol using RSocket.

## Sample auth-user record to be inserted in MongoDB collection `user-auth`
```
{
	"_id": "zZmkxHSnBxZsgq1wteabp",
	"name": "John Doe",
	"username": "john",
	"password": "{bcrypt}$2a$10$3AsrHlo10/ntxG71gpVObO4Qdot/oECSa894SiaiagC58m3.ThcIS",
	"authorities": ["ROLE_ADMIN", "ROLE_USER"],
	"plainPassword": "HelloWorld@123"
}

{
	"_id": "Vrroae7F2VxN9U3hGF1hZ",
	"name": "Jack Reacher",
	"username": "jack",
	"password": "{bcrypt}$2y$12$5QtIoFB1SfrRsrt.BmgZpOSY1TULZQfGgtjtr8htBxCvT8r/sZYxm",
	"authorities": ["ROLE_USER"],
	"plainPassword": "HelloWorld@123"
}
```