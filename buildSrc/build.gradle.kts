repositories {
    google()
    jcenter()
    maven ("https://jitpack.io")
}

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}