pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "iriscv"
include(":app")

// Include OpenCV SDK as a module if it exists
val opencvSdkPath = file("opencv-android-sdk")
if (opencvSdkPath.exists() && file("${opencvSdkPath}/sdk/build.gradle").exists()) {
    include(":opencv")
    project(":opencv").projectDir = file("${opencvSdkPath}/sdk")
}
