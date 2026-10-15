plugins { java; application }
repositories { mavenCentral() }
application { mainClass.set("com.example.proveedores.ProveedoresApplication") }
// Unifica el directorio de trabajo para leer el .env de la raiz, con o sin scripts.
tasks.named<JavaExec>("run") { workingDir(rootProject.projectDir.parentFile) }
java { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.7"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core:11.19.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.19.1")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.test { useJUnitPlatform() }
