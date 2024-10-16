import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {

  intellijPlatform {
    local("F:\\ThirdGrade\\java\\IntelliJ IDEA 2024.2.1")
    bundledPlugin("com.intellij.java")
    pluginVerifier()
    zipSigner()
    instrumentationTools()
    testFramework(TestFrameworkType.Platform)
  }
  testImplementation("junit:junit:4.13.2")
  implementation("org.eclipse.jgit:org.eclipse.jgit:6.10.0.202406032230-r")


}

intellijPlatform {
  pluginConfiguration {
    //sinceBuild.set("232")
    //untilBuild.set("242.*")
  }

  publishing {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  signing {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }
}

tasks {
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
  }
}