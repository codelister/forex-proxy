Forex-Proxy is a service that provides a simple api to get the latest exchange rates.

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
