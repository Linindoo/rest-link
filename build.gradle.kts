plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    intellijPlatform {
        defaultRepositories()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    intellijPlatform {
        local("D:\\SOFT\\IntelliJ IDEA Community Edition 2024.2.0.1")
//        intellijIdeaCommunity("2024.1")
        zipSigner()
        instrumentationTools()
        bundledPlugin("com.intellij.java")

    }
}

group = "cn.olange"
version = "1.0.1"


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks{
    patchPluginXml {
        sinceBuild = "223"
        untilBuild = "242.*"
        version = project.version
    }
}