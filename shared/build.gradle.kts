import com.mikepenz.aboutlibraries.plugin.AboutLibrariesTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.aboutLibraries)
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
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // Reaching browser APIs from Kotlin/Wasm is experimental interop by definition, and
        // kotlin-browser is nothing but that interop.
        compilerOptions.optIn.add("kotlin.js.ExperimentalWasmJsInterop")

        browser()
        binaries.executable()
    }
    
    android {
       namespace = "dev.yuyuyuyuyu.locationremover.shared"
       compileSdk {
           version = release(providers.gradleProperty("sdk.compile").get().toInt()) {
               minorApiLevel = providers.gradleProperty("sdk.compile.minor").get().toInt()
           }
       }
       minSdk = providers.gradleProperty("sdk.min").get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.yuyuyuyuyu.myComposables)
            implementation(libs.aboutLibraries.composeCore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
        }
        webMain.dependencies {
            implementation(libs.wrappers.browser)
        }
        webTest.dependencies {
            implementation(libs.compose.uiTest)
        }
    }
}

mapOf(
    "android" to "androidMain",
    "wasmJs" to "wasmJsMain",
).forEach { (target, sourceSetName) ->
    val libraryDefinitionsDirectory = layout.buildDirectory.dir("generated/libraryDefinitions/$target")
    val exportLibraryDefinitions =
        tasks.register<AboutLibrariesTask>("exportLibraryDefinitionsFor${target.replaceFirstChar(Char::uppercase)}") {
            variant = target
            includeMetaData = false
            prettyPrint = false
            excludeFields = emptySet()
            configureOutputFile(libraryDefinitionsDirectory.map { it.file("files/aboutlibraries.json") })
            configure()
        }

    compose.resources {
        customDirectory(
            sourceSetName = sourceSetName,
            directoryProvider = exportLibraryDefinitions.map { libraryDefinitionsDirectory.get() },
        )
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

tasks.matching { it.name == "prepareLibraryDefinitionsAndroidMain" }.configureEach { enabled = false }
