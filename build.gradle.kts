import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
	buildscript {
		repositories {
			maven { setUrl("https://maven.aliyun.com/repository/central") }
			maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
			maven { setUrl("https://maven.aliyun.com/repository/google") }
			maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
			maven { setUrl("https://maven.aliyun.com/repository/public") }
		}
	}
	repositories {
		maven { setUrl("https://maven.aliyun.com/repository/central") }
		maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
		maven { setUrl("https://maven.aliyun.com/repository/google") }
		maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
		maven { setUrl("https://maven.aliyun.com/repository/public") }
	}
}

plugins {
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
	kotlin("plugin.serialization") version "2.0.0"
}

group = "icu.crepusculumx"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
//	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")


}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
