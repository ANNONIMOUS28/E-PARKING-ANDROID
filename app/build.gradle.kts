import java.util.Properties

    plugins {
        alias(libs.plugins.android.application)
    }

    /*
     * URL base del backend.
     *
     * Se lee de local.properties (NO versionado) para que cada equipo
     * apunte a su propio servidor sin modificar archivos del repositorio.
     * Si no esta definida, se usa el valor predeterminado de
     * gradle.properties, que si se versiona.
     */
    val eparkingBaseUrl: String = run {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        localProperties.getProperty("eparking.baseUrl")
            ?: providers.gradleProperty("eparking.baseUrl").get()
    }

    android {
        namespace = "com.eparking.android"
        compileSdk {
            version = release(37)
        }

        defaultConfig {
            applicationId = "com.eparking.android"
            minSdk = 24
            targetSdk = 37
            versionCode = 2
            versionName = "1.0.2"

            buildConfigField(
                "String",
                "EPARKING_BASE_URL",
                "\"$eparkingBaseUrl\""
            )

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildFeatures {
            buildConfig = true
        }

        buildTypes {
            release {
                optimization {
                    enable = false
                }
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    dependencies {
        implementation(libs.activity.ktx)
        implementation(libs.appcompat)
        implementation(libs.constraintlayout)
        implementation(libs.material)
        implementation("com.android.volley:volley:1.2.1")
        testImplementation(libs.junit)
        androidTestImplementation(libs.espresso.core)
        androidTestImplementation(libs.ext.junit)
    }