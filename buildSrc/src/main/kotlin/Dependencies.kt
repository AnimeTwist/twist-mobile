const val kotlinVersion = "1.3.70"

object BuildPlugins {
    object Versions {
        const val buildToolsVersion = "4.0.0-beta02"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val navigationPlugin = "androidx.navigation.safeargs.kotlin"
    const val navigationClasspath = "androidx.navigation:navigation-safe-args-gradle-plugin:2.2.1"
}

object Libraries {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val coreKtx = "androidx.core:core-ktx:1.2.0"
    const val legacySupport = "androidx.legacy:legacy-support-v4:1.0.0"
    const val fragment = "androidx.fragment:fragment-ktx:1.2.2"

    // Lifecycle components
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"

    // Nav component
    const val navFragment = "androidx.navigation:navigation-fragment-ktx:2.2.1"
    const val navUi = "androidx.navigation:navigation-ui-ktx:2.2.1"

    // Paging
    const val pagingRuntime = "androidx.paging:paging-runtime-ktx:2.1.1"

    // Koin
    const val koin = "org.koin:koin-android-viewmodel:2.0.1"

    // Networking
    const val gson = "com.google.code.gson:gson:2.8.5"
    const val retrofit = "com.squareup.retrofit2:retrofit:2.6.2"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:2.6.2"
    const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:4.2.2"

    // UI
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0-alpha01"
    const val materialComponents = "com.google.android.material:material:1.1.0"
    const val materialDialogs = "com.afollestad.material-dialogs:core:3.1.1"

    // apache commons
    const val apacheCommons = "org.apache.commons:commons-lang3:3.9"

    // Exoplayer - stream video
    const val exoplayer = "com.google.android.exoplayer:exoplayer:2.10.5"

    // Spinkit - loading anims
    const val spinKit = "com.github.ybq:Android-SpinKit:1.4.0"

    // Room for database stuff
    const val room = "androidx.room:room-runtime:2.2.4"
    const val roomKapt = "androidx.room:room-compiler:2.2.4"

    // optional - Kotlin Extensions and Coroutines support for Room
    const val roomKtx = "androidx.room:room-ktx:2.2.4"

    // glide for image loading
    const val glide = "com.github.bumptech.glide:glide:4.10.0"
    const val glideKapt = "com.github.bumptech.glide:compiler:4.10.0"

    // multidex
    const val multidex = "com.android.support:multidex:1.0.3"

    // android system permissions
    const val quickPerms = "com.github.quickpermissions:quickpermissions-kotlin:0.4.0"

    // HTML rendering
    const val htmlTextView = "org.sufficientlysecure:html-textview:3.9"

    // Fuzzy search
    const val fuzzy = "me.xdrop:fuzzywuzzy:1.2.0"
}

object TestLibraries {
    private object Versions {
        const val junit4 = "4.13"
        const val testRunner = "1.1.0-alpha4"
        const val espresso = "3.2.0"
    }

    const val junit4 = "junit:junit:${Versions.junit4}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

object AndroidSdk {
    const val min = 21
    const val compile = 29
    const val target = 29
}

