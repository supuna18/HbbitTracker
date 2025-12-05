pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        // ✅ Added: JitPack repository for GitHub libraries (like MPAndroidChart)
        maven(url = "https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // ✅ Added: JitPack here too (so dependencies can be resolved)
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "HbbitTracker" // ✅ Removed space to match appId convention
include(":app")
