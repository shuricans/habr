# Habr, but worse. Anyway...
See current deployed version 
[habr v-1.2](http://80.78.240.245:8080/)  
See [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)  
Deployed on [reg.ru](https://www.reg.ru/), cheapest config (15 GB SSD, 1 GB RAM, 1 vCPU)

### Short description of project & implemented functionality:
This is a bad version of a great project with the name - [https://habr.com/](https://habr.com/)
> since v-1.0
- [JWT](https://jwt.io/) authentication & authorization (basic implementation)
- refresh token, for smooth user experience
- **markdown** markup language, see example of [post](http://80.78.240.245:8080/habr/1)
- [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)
- default admin user `admin | password`
- unit & integration tests on backend
> since v-1.2
- real posts are displayed on the "/habr" & "/habr/{postId}" pages
- added pagination
- main images on the article card are fake for now
- user can edit own data in his personal account on the site
- user can view their publications in his personal account
- added several endpoints, see current [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)

### How run this project?
##### 1. First way - docker compose:
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* Grab [docker-compose.yml](https://github.com/shuricans/habr/blob/dev/docker-compose.yml) from root directory.
> Depending on installed version of docker, commands may be different:  
> `docker-compose` or `docker compose`
* Run `docker compose up -d`
* App ready > navigate to [http://localhost:8080](http://localhost:8080)
* View backend API description [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
* To stop containers, use `docker compose stop` command.
* To stop and remove - `docker compose down` command.
> You can view logs of running apps via `docker compose logs -f`  
> and containers statistics via `docker stats`

##### 2. Second way - docker & IntelliJ IDEA or your favorite IDE:
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* required versions or mb higher:
    * JDK 11
    * maven 3.8.3
    * [node](https://nodejs.org/en/) v16.17.0
    * npm 8.15.0
    * [Angular CLI](https://angular.io/cli): 15.0.2
* Clone project from github
* First, in parent module **habr** run `mvn clean install`
* All tests must pass successfully. I hope :)
* Database. I recommend using this [docker image **v-1.2**](https://hub.docker.com/r/shuricans/habr-db/tags)
* docker compose help us (take this [docker-compose.yml](https://gist.github.com/shuricans/f69c74390f3d49c26613816fc498c07b))
* Use `docker compose up -d` command for it.
> You can stop and remove this container by `docker compose down`
* Navigate to the backend app package `cd backend-api-app/`
* Use `mvn spring-boot:run` command to run backend (`Ctrl + c` to stop app)
* Alternatively you can run it from file **BackendApiAppApplication** with IDE.
* Now let's go and start frontend with [Angular CLI](https://angular.io/cli)
* Navigate to the frontend app package `cd frontend-app/`
* Run `npm install` - this command installs a package and any packages that it depends on.
* Run `ng serve` - builds and serves your application, rebuilding on file changes.
* App ready > navigate to [http://localhost:4200](http://localhost:4200)
* View backend API description [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
* To stop frontend app use `Ctrl + c`