plugins {
	java
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//web client
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//cache
	implementation("org.springframework.boot:spring-boot-starter-cache")

	//square okhttp3
	testImplementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
	testImplementation("com.squareup.okhttp3:okhttp")
	testImplementation("com.squareup.okhttp3:mockwebserver")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
