plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.theftguard.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.theftguard.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    // Signing is loaded from a local, UNCOMMITTED keystore.properties (or matching
    // environment variables in CI) so the public repo never ships a signing key.
    // Using a fixed key keeps every build's signature identical, so updates install
    // over the previous version without an uninstall. When no key is configured,
    // builds fall back to Android's default debug signing - fine for local testing
    // and CI debug artifacts. See keystore.properties.example and README → Build.
    val keystorePropsFile = rootProject.file("keystore.properties")
    val keystoreProps = java.util.Properties().apply {
        if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
    }
    fun signingValue(propKey: String, envKey: String): String? =
        keystoreProps.getProperty(propKey) ?: System.getenv(envKey)

    val storeFilePath = signingValue("storeFile", "KEYSTORE_FILE")
    val hasCustomSigning = storeFilePath != null && rootProject.file(storeFilePath).exists()

    signingConfigs {
        if (hasCustomSigning) {
            create("shared") {
                storeFile = rootProject.file(storeFilePath!!)
                storePassword = signingValue("storePassword", "KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            if (hasCustomSigning) signingConfig = signingConfigs.getByName("shared")
        }
        release {
            isMinifyEnabled = false
            // Use the shared key if configured, otherwise fall back to debug signing
            // so a plain checkout still produces an installable APK.
            signingConfig = if (hasCustomSigning) {
                signingConfigs.getByName("shared")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // The two mail libraries both ship META-INF license/notice files; drop the
    // duplicates so the APK packager doesn't fail merging them.
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE",
                "META-INF/DEPENDENCIES",
            )
        }
    }
}

dependencies {
    // Shizuku: lets the app run shell-privileged actions (turn on mobile data,
    // Wi-Fi and location when the alarm triggers) if the user has Shizuku running.
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    // Send the location by email over SMTP (used when the user picks email delivery).
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
}
