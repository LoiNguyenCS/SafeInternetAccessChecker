# Safe Internet Access Checker

SafeInternetAccessChecker is a **Detekt rule** designed to enforce safe internet access in Kotlin applications. It is implemented based on the notion of an **effect system**, a static system used to track the side effects of functions. It ensures that risky internet connection function calls do not exist inside targeted functions.

---

## Checking Rules:
The checker operates using an effect system consisting of two effects:

1. **`@Safe` (Default Effect)**
   - All functions are assumed **safe** unless explicitly annotated otherwise.
   - No internet connection or only safe calls are present.

2. **`@HasRiskyInternetConnection` (Explicit Effect)**
   - Contain function calls that initiate internet connections without exception handling.
   - Must be explicitly marked by the developer.

The core rule of this checker is: 

> Inside **targeted functions**, there should be no function calls to `@HasRiskyInternetConnection` functions **without a try-catch block**.

This ensures that risky internet operations are either explicitly handled or safely encapsulated, reducing the likelihood of unhandled network errors.

Note: targeted function are `main()` and `onCreate()` by default. To add more targeted functions, use `@InternetSafeCheck` annotation.

---

## Example Usage

```kotlin
import java.io.IOException
import java.net.URL
import java.net.URLConnection

// Risky function without try-catch - should be marked with @HasRiskyInternetConnection
@HasRiskyInternetConnection
fun fetchDataFromAPI() {
    val url = URL("https://example.com")
    val connection: URLConnection = url.openConnection() // no warnings triggered here, since fetchDataFromAPI() is not a targeted function for network access safety check.
    connection.connect()
}

// Safe function with proper try-catch handling
fun fetchDataSafely() {
    try {
        val url = URL("https://example.com")
        val connection: URLConnection = url.openConnection()
        connection.connect()
    } catch (e: IOException) {
        println("Connection failed: ${e.message}")
    }
}

// Default targeted functions, main() and onCreate(), will be checked for network access safety. 
fun main() {
    fetchDataFromAPI() // 🚨 The checker will flag this as unsafe and raise a warning.
}

// Added targeted function that will also be checked
@InternetSafeCheck
fun processDataSafely() {
    fetchDataSafely() // ✅ This is safe and does not trigger a warning
}
```

---

## Usage Instructions

### Step 1: Add Dependencies
To begin with, [add Detekt](https://detekt.dev/docs/intro) to your project.

To use this checker, add the following to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Then, add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
   detektPlugins("com.github.LoiNguyenCS:SafeInternetAccessChecker:v1.2.2") 
   compileOnly("com.github.LoiNguyenCS:SafeInternetAccessChecker:v1.2.2")
}
```

### Step 2: Configure Detekt
Add the following to your `detekt.yml` configuration file in order for Detekt to apply the checker:

```yaml
SafeInternetAccessRule:
  ConnectToInternetEffectRule:
    active: true
```

### Step 3: Annotate Your Functions
Mark functions that initiate internet connections without proper error handling with `@HasRiskyInternetConnection`. Keep in mind that Detekt is a static analysis tool and cannot handle dependency injection, so annotate functions broadly, particularly the root-level ones. If you want to enforce checks on specific functions, annotate them with `@InternetSafeCheck`.

### Step 4: Run Detekt
Execute the following command to analyze your project and see the report:

```sh
./gradlew detektMain
```

### Example Project:
To see an example of how an Android project can be configured and annotated to apply SafeInternetAccessChecker, consult the `internet-unsafe` branch of [this project](https://github.com/LoiNguyenCS/BookShelf/tree/internet-unsafe).

---

## Summary
✅ **Ensures safer internet connections in critical functions.**  
✅ **Uses a lightweight annotation-based effect system.**  

---

## Future Work
To reduce the burden of manually annotating functions, we plan to develop an **effect inference algorithm**. This algorithm will automatically infer the appropriate effect annotations for some functions, reducing developer effort.

