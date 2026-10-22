import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

ktlint {
    version = libs.versions.ktlintCli.get()
    filter {
        val buildPath = layout.buildDirectory.get().asFile.path
        exclude { it.file.path.startsWith(buildPath) }
    }
}

detekt {
    source.setFrom("src")
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
    basePath = rootDir
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "dev.yuyuyuyuyu.locationremover.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.yuyuyuyuyu.locationremover"
            packageVersion = "1.0.0"
        }
    }
}