/**
 * A registry that maintains a list of functions and annotations associated with risky Internet access.
 *
 * This object serves two main purposes:
 * 1. Identifying **library methods** that initiate Internet connections (e.g., OkHttp, Retrofit, Java networking).
 * 2. Tracking **annotations** that imply Internet usage (e.g., Retrofit's HTTP method annotations).
 *
 */
object RiskyInternetAccessRegistry {

    /**
     * A set of fully qualified method names from popular libraries that initiate Internet connections.
     * These methods are commonly used for making network requests and should be carefully monitored.
     */
    val popularRiskyLibraryMethods = hashSetOf(
        "java.net.HttpURLConnection.connect",
        "java.net.URL.openStream",
        "okhttp3.OkHttpClient.newCall",
        "retrofit2.Retrofit.create",
        "org.apache.http.client.HttpClient.execute",
        "android.webkit.WebView.loadUrl"
    )

    /**
     * A set of annotations that imply a function is responsible for making Internet requests.
     * This includes both custom-defined effects (e.g., `@HasRiskyInternetConnection`) and
     * common annotations from Retrofit that trigger network operations.
     */
    val internetAccessAnnotations = hashSetOf(
        // SafeInternetAccessChecker-specific effect
        "com.github.loinguyencs.safeinternetaccesschecker.effect.HasRiskyInternetConnection",
        "HasRiskyInternetConnection",

        // Retrofit annotations (initiate Internet connection)
        "retrofit2.http.GET",
        "retrofit2.http.POST",
        "retrofit2.http.PUT",
        "retrofit2.http.DELETE",
        "retrofit2.http.PATCH",
        "retrofit2.http.HEAD",
        "retrofit2.http.OPTIONS"
    )
}
