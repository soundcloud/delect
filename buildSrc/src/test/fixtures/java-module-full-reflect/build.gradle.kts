plugins {
    java
    application
}

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.delect.test.java.Starter"
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler:2.24")
    implementation("com.google.dagger:dagger:2.24")
}