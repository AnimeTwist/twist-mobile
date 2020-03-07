import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream


plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.navigationPlugin)
}

val secretProperties = Properties()
if (System.getenv("CI") != "true") {
    val secretsFile = rootProject.file("secrets.properties")
    secretProperties.load(FileInputStream(secretsFile))
} else {
    secretProperties.setProperty("decrypt_key", System.getenv("DECRYPT_KEY"))
}


android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "dev.smoketrees.twist"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 12
        versionName = "2.2.2-prod9"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        buildConfigField(
            "byte[]",
            "CRYPTO_KEY",
            "new byte[] {" + (secretProperties["decrypt_key"] as String).toByteArray()
                .joinToString()
                    + "}"
        )

        javaCompileOptions {
            annotationProcessorOptions { arguments = mapOf("room.incremental" to "true") }
        }

    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile(file("proguard-rules.pro"))
            multiDexEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // For Kotlin projects
    kotlinOptions {
        jvmTarget = "1.8"
    }

    androidExtensions.isExperimental = true
}

kapt {
    useBuildCache = true
}

dependencies {
    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.appCompat)
    implementation(Libraries.coreKtx)
    implementation(Libraries.legacySupport)

    implementation(Libraries.fragment)

    // Lifecycle components
    implementation(Libraries.lifecycleExtensions)
    implementation(Libraries.liveDataKtx)
    implementation(Libraries.viewModel)

    // Navigation component
    implementation(Libraries.navFragment)
    implementation(Libraries.navUi)

    // Paging support
    implementation(Libraries.pagingRuntime)

    // Koin for DI
    implementation(Libraries.koin)

    // Networking stuff
    implementation(Libraries.gson)
    implementation(Libraries.retrofit)
    implementation(Libraries.gsonConverter)
    implementation(Libraries.okHttpInterceptor)

    // UI
    implementation(Libraries.constraintLayout)
    implementation(Libraries.recyclerView)
    implementation(Libraries.materialComponents)
    implementation(Libraries.materialDialogs)

    // apache commons
    implementation(Libraries.apacheCommons)

    // Exoplayer - stream video
    implementation(Libraries.exoplayer)

    // Spinkit - loading animations
    implementation(Libraries.spinKit)

    // Room for database stuff
    implementation(Libraries.room)
    kapt(Libraries.roomKapt)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(Libraries.roomKtx)

    // glide for image loading
    implementation(Libraries.glide)
    kapt(Libraries.glideKapt)

    // multidex
    implementation(Libraries.multidex)

    // android system permissions
    implementation(Libraries.quickPerms)

    // html rendering
    implementation(Libraries.htmlTextView)

    // Fuzzy search
    implementation(Libraries.fuzzy)
}

object {
    fun byteArrayLiteral(string: String) =
        "new byte[] {" + string.toByteArray().joinToString() + "}"
}
