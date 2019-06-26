plugins {
    id("com.soundcloud.delect")
} 

delect {
    useReflectForASBuilds = true
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "5.4.1"
}