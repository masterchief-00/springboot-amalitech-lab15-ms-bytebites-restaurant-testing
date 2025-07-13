# Code review report

## Details
- ⚙️ Project: [ByteBites Restaurant Microservices lab (LAB14)](https://github.com/clevy11/Bytebits_Microservices)
- 🧑🏼‍💻 Review for: [Caleb Levy](https://github.com/clevy11)

### Microservice design and structure
- The author put in a good effort in the design stage, diagrams describing the architecture and sequence flows such as authentication, order processing and so on.
- The author uses a minimalistic approach using a monorepo with a standalone config git repo

### Spring Boot and Spring Cloud + Service discovery and configuration
- The `spring-cloud-config-server` dependency in the config server unlocks different spring boot behaviour including loading configuration yml files from the config repo, the other services get to read their respective configs through the config server
- The author also employs Netflix eureka dependencies to allow service discoveries and coordination between services
- The author configures the API gateway service to perform routing and load balancing activities dismissing the need for services communicating directly

### Security
- The author uses JWT tokens to authenticate users and easily move around essential details like roles and user details around the services in a stateless manner
- The author sets filtering at the gateway service level blocking and allowing requests to services accordingly to their roles + JWT validation.

### Messaging integration
- The app is making use of RabbitMQ to broadcast changes such as order creation
- The notification service seems to be the consuming service for these broadcasts

### Docker and deployment setup
- The project is set up a `docker-compose` file which seems to be pulling docker images for rabbitmq, postgres, redis and setting up volumes+networks for docker
- There seems to be no setup for deployment

## Conclusion
- The author demonstrated technical knowledge and capabilities by handling the Security, messaging, architecture and design aspects accurately
- The author needs to work on the use of docker for deployment setup of the project.