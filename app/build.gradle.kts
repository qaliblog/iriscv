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
    
    // OpenCV - Must use Android SDK JAR (not Maven, as Maven version is for desktop Java)
    val opencvSdkPath = project.rootProject.file("opencv-android-sdk")
    if (opencvSdkPath.exists()) {
        // Try to find OpenCV JAR in SDK
        val possibleJarPaths = listOf(
            "${opencvSdkPath}/sdk/java/opencv.jar",
            "${opencvSdkPath}/sdk/java/opencv-android.jar",
            "${opencvSdkPath}/sdk/java/opencv_java4.jar",
            "${opencvSdkPath}/sdk/java/opencv_java.jar"
        )
        
        var jarFound = false
        for (jarPath in possibleJarPaths) {
            val jarFile = file(jarPath)
            if (jarFile.exists()) {
                implementation(files(jarFile))
                println("Using OpenCV Android SDK JAR: $jarPath")
                jarFound = true
                break
            }
        }
        
        if (!jarFound) {
            // Debug: list SDK structure
            println("WARNING: OpenCV JAR not found. SDK structure:")
            if (file("${opencvSdkPath}/sdk").exists()) {
                file("${opencvSdkPath}/sdk").listFiles()?.forEach { f ->
                    println("  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
                }
            }
            if (file("${opencvSdkPath}/sdk/java").exists()) {
                file("${opencvSdkPath}/sdk/java").listFiles()?.forEach { f ->
                    println("    java/${f.name}")
                }
            }
            // Don't fail build, but warn - native libs might still work
            println("WARNING: OpenCV JAR not found - Android-specific classes will be unavailable")
        }
    } else {
        println("WARNING: OpenCV SDK not found - OpenCV features will not work")
    }
}
