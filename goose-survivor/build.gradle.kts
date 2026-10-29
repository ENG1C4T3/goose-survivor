plugins {
    id("java")
    id("application")
    id("checkstyle")
}

repositories {
    mavenCentral()
}

group = "com.eng1c4t3.goosesurvivor"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

sourceSets {
    main {
        resources.srcDir("assets")
    }
}

checkstyle {
    toolVersion = "10.26.1"
    configFile = file("${rootDir}/config/checkstyle/google_checks.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

application {
    mainClass = "com.eng1c4t3.goosesurvivor.GooseSurvivor"
    applicationDefaultJvmArgs = buildList {
        add("--enable-native-access=ALL-UNNAMED")
        if (System.getProperty("os.name").lowercase().contains("mac")) {
            add("-XstartOnFirstThread")
        }
    }
}

val gdxVersion = "1.14.2"
val lwjglVersion = "3.3.6"

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl") {
            useVersion(lwjglVersion)
        }
    }
}

dependencies {
    // JUnit
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // LibGDX Core & Desktop Backend
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named<JavaExec>("run") {
    workingDir = file("assets")
}