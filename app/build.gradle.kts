plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.qali.iriscv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.qali.iriscv"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Build unsigned release APK for CI/CD (no signingConfig specified)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    
    sourceSets {
        getByName("main") {
            val opencvSdkPath = project.rootProject.file("opencv-android-sdk")
            if (opencvSdkPath.exists()) {
                val nativeLibsPath = "${opencvSdkPath}/sdk/native/libs"
                if (file(nativeLibsPath).exists()) {
                    jniLibs.srcDirs(nativeLibsPath)
                }
            }
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    
    // OpenCV - Try Maven dependency first, fallback to SDK JAR
    val opencvSdkPath = project.rootProject.file("opencv-android-sdk")
    val opencvJarPath = "${opencvSdkPath}/sdk/java/opencv.jar"
    
    if (opencvSdkPath.exists() && file(opencvJarPath).exists()) {
        // Use SDK JAR if available
        implementation(files(opencvJarPath))
        println("Using OpenCV from SDK: $opencvJarPath")
    } else {
        // Fallback to Maven dependency (if available)
        // Note: This may not exist in Maven Central, but worth trying
        implementation("org.opencv:opencv-android:4.8.0")
        println("Using OpenCV from Maven Central")
    }
}
