Forex-Proxy is a Spring Boot application that exposes a simple api to get the latest exchange rates from a provider.

## Requirements
- colima
- docker
- paidy container
- Gradle
- JDK 17

## Setup
This assumes a user is running with macOS. It was developed with IntelliJ IDEA 2022.3.1 (recommended).

### Gather dependencies
```bash
brew install openjdk@17
brew install colima
brew install docker
brew install gradle
docker pull paidyinc/one-frame:latest
```

### Run the environment
```bash
colima start
docker run -p 8081:8080 paidyinc/one-frame
./gradlew bootRun
```

### Run the tests
```bash
./gradlew test --tests "com.example.*Unit*"
```

### Run integration tests
Ensure the environment is up and running. Then, run the following command.
```bash
./gradlew test --tests "com.example.*Integration*"
```

### Run the bash script which makes 10000 requests to the api
```bash
./test.sh
```

## Design Considerations
I kept this pretty minimal and focused on the main requirements. There's simple validation, error handling, and unit tests.
I am also new to Spring Boot, so there are probably better ways to do things. Nevertheless, it was a fun project to work on.
The paidy container has a 1000 request limit, so we need to cache the exchange rates. I added a basic cache evicting after 4 minutes.
This enables fresh enough exchange rates and enabling over 10000 requests per day.
There's a bash script that can be used to make 10000 requests to the api. It's not perfect, but works for now.
I added a simple integration test for the service to ensure the cache is used as expected. The cache usage
should guarantee fresh exchange rates are fetched. A heavy assumption here is that the paidy container always returns fresh values.

With more time, I'd like to add more tests and improve existing ones.


## If this were a real project
For an MVP, it needs to be useful enough for the client. We can continuously iterate on the MVP to make it more useful.
Production readiness is a separate concern, depending on the client's needs.
Production-ready means the service has sufficient reliability, performance, and security to be used in production.
- Requirements are met. Doesn't stop us from starting with a subset and incrementally adding changes. Let users play with the beta.
- Can handle anticipated traffic and auto-scale (performance tested)
- Security review has been passed
- CI/CD pipeline enabling developers to quickly iterate or rollback changes
- Monitoring and observability (can improve this over time)
- Runbook exists to ensure smooth on-call experience

### Regarding the api
- If the api is not working, should the user get an old value or an error?
    - If old value, should we let them know it's old?
- If the api is too flaky, maybe a separate job can deal with collecting fresh exchange rates and persist it in our cache or db.
    - The cheapest solution is probably just an in-memory cache.
        - 5 instances with their own in-memory cache could lead to inconsistency
            - If that's important, we should probably have a centralized database storing the exchange rates.

### Response models
- JSON responses with minimal customization
- Errors should have a well formatted message, maybe error code.
- Generating an openapi spec from the code would be extremely useful for consumers of the api.

### Credential management
- For non-local environments, store credentials in a secret manager.
    - A tool like AWS Secrets Manager or Hashicorp Vault can be used to store credentials.

### Testing
- Tests help us confidently iterate on an existing codebase.
    - Unit tests
    - Integration tests
    - End-to-end/System tests are great for testing the entire system from the user's perspective.
    - Load tests are great for testing the system's performance. Useful for capacity planning or experimenting with new technologies.
- Unit and integration tests are mandatory for an MVP.

### Development and Deployment
I've had success with just having feature branches and merging to main. For development velocity, I find this approach
to be ideal. Whatever is on the main branch should be released to production asap. You should rely on feature flagging
to control the rollout of new features.

I've previously worked with projects that cherry-pick commits from feature branches to some release branch. I find this
often leads to release pipeline blockage and inconsistent environments across dev/staging/production. There may be
cases where we want to do this, but unless the team has sufficient capacity to handle this (i.e. dedicated release managers),
I would avoid this approach.