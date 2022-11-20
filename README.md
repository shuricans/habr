# Habr, but worse.
See current deployed version
[habr](http://80.78.240.245:8080/)  
See [swagger-api](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)  
Deployed on service [reg.ru](https://www.reg.ru/)

### Short description of project & implemented functionality:
- This is a bad version of a great project with the name - [https://habr.com/](https://habr.com/)
- [JWT](https://jwt.io/) authentication & authorization (basic implementation)
- refresh token, for smooth user experience
- **markdown** markup language, see example of [post](http://80.78.240.245:8080/habr/1)
- [swagger](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)
- default admin user `admin | password`
- unit & integration tests on backend

### How run this project?
##### 1. First way - docker compose:
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* Grab [docker-compose.yml](https://github.com/shuricans/habr/blob/dev/docker-compose.yml) from root directory.
* Next use `docker-compose` or `docker compose` command.
* Simple run `docker compose up -d` that's all.
* See logs via `docker compose logs -f`
* See stats via `docker stats`
* App ready > navigate to [http://localhost:8080](http://localhost:8080)
* View backend API description [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
* To stop containers, use `docker compose stop` command.

##### 2. Second way - docker & IntelliJ IDEA or your favorite IDE:
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* required versions or mb higher:
    * JDK 11
    * maven 3.8.3
    * node v16.17.0
    * npm 8.15.0
    * Angular CLI: 14.2.3
* Clone project from github
* First of all, in parent module run `mvn clean install`
* All tests must pass successfully. I hope :)
* Database. I recommend using docker image for Postgres.
* Run db container: (take this [docker-compose.yml](https://gist.github.com/shuricans/4d2269beac546e2b659b67adbfea75d3))
* Use `docker compose up -d` command for it.
* Navigate to the backend app package `cd backend-api-app/`
* Use `mvn spring-boot:run` command to run backend
* Alternatively you can run it from file **BackendApiAppApplication** with IDE.
* Now let's go and start frontend with Angular CLI
* Navigate to the frontend app package `cd frontend-app/`
* `npm install`
* `ng serve`
* App ready > navigate to [http://localhost:4200](http://localhost:4200)
* View backend API description [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
* To stop use `Ctrl + c`