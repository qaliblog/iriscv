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
    
    // OpenCV Android - Must use SDK JAR (Maven dependency not available)
    val opencvSdkPath = project.rootProject.file("opencv-android-sdk")
    if (opencvSdkPath.exists()) {
        // Search for JAR file in common locations
        val possibleJarPaths = mutableListOf<String>()
        
        // Standard paths
        possibleJarPaths.add("${opencvSdkPath}/sdk/java/opencv.jar")
        possibleJarPaths.add("${opencvSdkPath}/sdk/java/opencv-android.jar")
        possibleJarPaths.add("${opencvSdkPath}/sdk/java/opencv_java4.jar")
        possibleJarPaths.add("${opencvSdkPath}/sdk/java/opencv_java.jar")
        
        // Also search recursively for any JAR files
        val sdkJavaDir = file("${opencvSdkPath}/sdk/java")
        if (sdkJavaDir.exists() && sdkJavaDir.isDirectory) {
            sdkJavaDir.walkTopDown().forEach { file ->
                if (file.isFile && file.extension == "jar") {
                    possibleJarPaths.add(file.absolutePath)
                }
            }
        }
        
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
            // Debug output
            println("ERROR: OpenCV JAR not found. Searching SDK structure...")
            println("SDK path: ${opencvSdkPath.absolutePath}")
            if (file("${opencvSdkPath}/sdk").exists()) {
                println("SDK/sdk directory exists")
                file("${opencvSdkPath}/sdk").listFiles()?.forEach { f ->
                    println("  ${f.name}")
                }
            }
            if (sdkJavaDir.exists()) {
                println("SDK/sdk/java directory exists")
                sdkJavaDir.listFiles()?.forEach { f ->
                    println("    ${f.name}")
                }
            }
            // Find all JARs in SDK
            val allJars = file(opencvSdkPath).walkTopDown().filter { it.extension == "jar" }.toList()
            if (allJars.isNotEmpty()) {
                println("Found JAR files in SDK:")
                allJars.forEach { println("  ${it.absolutePath}") }
            } else {
                println("No JAR files found in SDK")
            }
            throw RuntimeException("OpenCV Android SDK JAR not found. Please ensure OpenCV SDK is downloaded correctly.")
        }
    } else {
        throw RuntimeException("OpenCV Android SDK not found at: ${opencvSdkPath.absolutePath}")
    }
}
