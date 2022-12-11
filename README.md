# Habr, but worse. Anyway...
See current deployed version 
[habr v-1.6](http://80.78.240.245:8080/)  
See [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)  
Deployed on [reg.ru](https://www.reg.ru/), cheapest config (15 GB SSD, 1 GB RAM, 1 vCPU)

### Modules short overview
- **backend-api-app** - spring boot application, backend REST API
  - This app is also an authorization/authentication server
  - Stateless auth
  - Refresh token - smooth user experience
  - swagger ui
  - unit & integration tests
- **database** - plugin module
  - database entities
  - repositories
  - specifications
  - integration tests
- **frontend-app** - Angular application, frontend
- **picture-service** - plugin module, just a service for manipulating images
- **picture-service-api-app** - spring boot application, API for getting images
  - swagger ui
  - endpoint example `/api/v1/picture/{id}`
  
### Common instruments
- [Spring Boot](https://spring.io/projects/spring-boot) -  makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run"
- [Spring Security](https://spring.io/projects/spring-security) - is a powerful and highly customizable authentication and access-control framework
- [JWT](https://jwt.io/) - JSON Web Tokens are an open, industry standard __RFC 7519__ method for representing claims securely between two parties
- [docker](https://www.docker.com/) - build, deploy, run, update and manage containers
- [jib-maven-plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) - containerization tool for maven projects
- [nginx](https://nginx.org/) - using as simple reverse proxy
- [swagger](https://swagger.io/) - to visualize and interact with the API’s resources
- [PostgreSQL](https://www.postgresql.org/) - the world's most advanced open source relational database
- [liquibase](https://docs.liquibase.com/) - version control
- [Testcontainers for Java](https://www.testcontainers.org/) - is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container
- [Project Lombok](https://projectlombok.org/) - is a java library that automatically plugs into your editor and build tools, spicing up your java
- [Angular](https://angular.io/) - is a platform for building mobile and desktop web applications
- [Font Awesome](https://github.com/FortAwesome/Font-Awesome) - is the Internet's icon library and toolkit, used by millions of designers, developers, and content creators
- [ng-bootstrap](https://ng-bootstrap.github.io) - Angular widgets built from the ground up using Bootstrap 5 CSS with APIs designed for the Angular ecosystem
- [ngx-markdown](https://github.com/jfcere/ngx-markdown) - parse markdown to HTML, and more
- [ngx-pagination](https://github.com/michaelbromley/ngx-pagination) - pagination for Angular
- [RxJS](https://rxjs.dev/) - reactive extension library for JavaScript

### Short description of project & implemented functionality:
This is a team study project.  
What it is? Is a collaborative blog about IT, computer science and anything related to the Internet.
We tried used agile methodology: backlogs, sprints, user-stories, job-stories etc.  
Main instrument for managing all stuff was a [Kaiten](https://kaiten.ru/)  

__Releases notes:__
> v-1.0
- [JWT](https://jwt.io/) authentication & authorization (basic implementation)
- refresh token, for smooth user experience
- **markdown** markup language, see example of [post](http://80.78.240.245:8080/habr/1)
- [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)
- default admin user `admin | password`
- unit & integration tests on backend
> v-1.2
- real posts are displayed on the "/habr" & "/habr/{postId}" pages
- added pagination
- main images on the article card are fake for now
- user can edit own data in his personal account on the site
- user can view their publications in his personal account
- added several endpoints, see current [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)
> v-1.4
- all pages on topics work, articles of the corresponding category are displayed, new ones first
- user can create a draft article
- user can edit all owns articles
- user can publish, hide and delete own articles
- added a public user profile page, (displays user information and articles) [see admin page](http://80.78.240.245:8080/user/admin).
- added a [search page](http://80.78.240.245:8080/search/) with sortable table (*for now with limited functionality)
- see example search by [**#tag_test** ](http://80.78.240.245:8080/search?tag=tag_test)
- added several endpoints, see current [backend-api (swagger)](http://80.78.240.245:8080/api/v1/swagger-ui/index.html#)
- *main images in the article card are still fake*
> v-1.6
- added [help page](http://80.78.240.245:8080/help)
- now user can upload images for their articles
- user can set the main image for the article, shown on preview
- redesign modal window for editing article
- additional header for moderators and admins
- several bugs fix

### How run this project?
##### 1. First way - docker compose:
* Be sure you have [docker](https://docs.docker.com/engine/install/) installed.
* Grab [docker-compose.yml](https://github.com/shuricans/habr/blob/dev/docker-compose.yml) from root directory.
* Take the directory called "images" from the root of this project (*just pictures inside*)
* Place *docker-compose.yml* and *"images"* in same directory.
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
* Database. I recommend using ready image
  * docker compose help us (take this [docker-compose.yml](https://gist.github.com/shuricans/0b5c0790d014fdec7815ff1fa0c019fc))
  * Use `docker compose up -d` command for it.
  * If you need another tags, go to [habr-db images](https://hub.docker.com/r/shuricans/habr-db/tags)
> You can stop and remove this container by `docker compose down`
* Navigate to the backend app package `cd backend-api-app/`
  * Use `mvn spring-boot:run` command to run backend (`Ctrl + c` to stop app)
  * Alternatively you can run it from file **BackendApiAppApplication** with IDE.
* Navigate to the picture-service-api-app package `cd picture-service-api-app/`
  * Use `mvn spring-boot:run` command to run picture-service (`Ctrl + c` to stop app)
  * Alternatively you can run it from file **PictureServiceApiAppApplication** with IDE.
* Now let's go and start frontend with [Angular CLI](https://angular.io/cli)
  * Navigate to the frontend app package `cd frontend-app/`
  * Run `npm i` - this command installs a package and any packages that it depends on.
  * Run `ng serve` - builds and serves your application, rebuilding on file changes.
* App ready > navigate to [http://localhost:4200](http://localhost:4200)
* View backend API description [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
* To stop frontend app use `Ctrl + c`