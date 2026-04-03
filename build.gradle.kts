plugins {
    // To optionally create a shadow/fat jar that bundle up any non-core dependencies
    id("com.gradleup.shadow") version "8.3.5"
    // QuPath Gradle extension convention plugin
    id("qupath-conventions")
}

// Get version from Environment Variable (GitHub Actions) or fallback to VERSION file
// 1. Get the tag name from GitHub (e.g., "v1.0.3" or "v1.0.3-rc1")
val githubTag = System.getenv("GITHUB_REF_NAME")

// 2. Determine the final version string
val releaseVersion = if (githubTag != null && githubTag.startsWith("v")) {
    githubTag.removePrefix("v") // Use the tag (stripped of 'v')
} else {
    file("VERSION").readText().trim() // Fallback to your SNAPSHOT file
}

// TODO: Configure your extension here (please change the defaults!)
qupathExtension {
    name = "qupath-extension-orthanc"
    group = "io.github.qupath"
    version = releaseVersion

    description = "An extension to connect QuPath to an Orthanc instance"
    automaticModule = "io.github.qupath.extension.orthanc"
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.scijava.org/content/repositories/releases/") }
    maven { url = uri("https://www.dcm4che.org/maven2/") }
}

// TODO: Define your dependencies here
dependencies {

    // Main dependencies for most QuPath extensions
    shadow(libs.bundles.qupath)
    shadow(libs.bundles.logging)
    shadow(libs.qupath.fxtras)

    // For testing
    testImplementation(libs.bundles.qupath)
    testImplementation(libs.junit)

    // HTTP client for Orthanc API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON processing
    implementation("com.google.code.gson:gson:2.10.1")

    // DICOM support
    implementation("org.dcm4che:dcm4che-core:5.29.2")
    implementation("org.dcm4che:dcm4che-imageio:5.29.2")
}
