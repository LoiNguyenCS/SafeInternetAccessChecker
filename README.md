# Safe Internet Access Checker

SafeInternetAccessChecker is a **Detekt rule** designed to enforce safe internet access in Kotlin applications. It is implemented based on the notion of an **effect system**, a system used to track the side effects of functions. It ensures that risky internet connection function calls do not exist inside targeted functions.

## Effect System Rules:
The checker operates using an effect system consisting of two effects:

1. **`@Safe` (Default Effect)**
   - All functions are assumed **safe** unless explicitly annotated otherwise.
   - No internet connection or only safe calls are present.

2. **`@HasRiskyInternetConnection` (Explicit Effect)**
   - Contain function calls that initiate internet connections without exception handling.
   - Must be explicitly marked by the developer.

---

## How It Works

### 1. Targeted Functions
By default, the rule checks:
- `main()`
- `onCreate()`

If any of these functions contain a risky internet connection, the rule will raise a **detekt warning**.

#### Marking Additional Functions
To add more functions to the check, use `@InternetAccessSafetyCheck`:
```kotlin
// The list of targeted functions will include the below function.
@InternetAccessSafetyCheck
fun myFunction() {
    fetchDataFromAPI() 
}
```

---

## Example Usage

### 1. Risky Internet Connection Without Try-Catch (Violation)
```kotlin
fun fetchDataFromAPI() {
    val url = URL("https://example.com")
    val connection = url.openConnection() // Risky: No try-catch
    connection.connect()
}
```
🚨 This function should be annotated with `@HasRiskyInternetConnection` as below:
```kotlin
@HasRiskyInternetConnection
fun fetchDataFromAPI() {
    val url = URL("https://example.com")
    val connection = url.openConnection()
    connection.connect()
}
```

### 2. Safe Internet Connection (Allowed)
```kotlin
fun fetchDataSafely() {
    try {
        val url = URL("https://example.com")
        val connection = url.openConnection()
        connection.connect()
    } catch (e: IOException) {
        println("Connection failed: ${e.message}")
    }
}
```
✅ This function does not require `@HasRiskyInternetConnection` since it handles exceptions properly.

### 3. Checking a Function
```kotlin
@InternetSafeCheck
fun processData() {
    fetchDataFromAPI() // 🚨 Detekt will flag this as unsafe
}
```
Since `fetchDataFromAPI()` is marked `@HasRiskyInternetConnection`, `processData()` will trigger a warning.

---

## Summary
✅ **Ensures safer internet connections in critical functions.**  
✅ **Uses a lightweight annotation-based effect system.**  
✅ **Raises warnings when unsafe internet calls are detected.**

---

## Future Work

To reduce the burden of manually annotating functions, we plan to develop an effect inference algorithm. This algorithm will automatically infer the appropriate effect annotations for some functions. 
