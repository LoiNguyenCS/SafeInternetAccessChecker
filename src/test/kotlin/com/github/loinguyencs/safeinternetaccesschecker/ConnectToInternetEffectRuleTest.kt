import com.github.loinguyencs.safeinternetaccesschecker.rule.ConnectToInternetEffectRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.matchers.collections.shouldHaveSize
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class InternetConnectEffectRuleTest(private val env: KotlinCoreEnvironment) {

    @Test
    fun `should not detect internet call inside try-catch block`() {
        val safeCode = """
            import com.github.loinguyencs.safeinternetaccesschecker.effect.HasRiskyInternetConnection
            class NetworkClient {
                @HasRiskyInternetConnection
                fun fetchData() {
                    println("Fetching data from the internet")
                }

                fun main() {
                    try {
                        fetchData()
                    } catch (e: Exception) {
                        println("Caught network exception")
                    }
                }
            }
        """.trimIndent()
        val findings = ConnectToInternetEffectRule(Config.empty).compileAndLintWithContext(env, safeCode)
        findings shouldHaveSize 0
    }

    @Test
    fun `should detect internet call outside try-catch block`() {
        val code = """
            import com.github.loinguyencs.safeinternetaccesschecker.effect.HasRiskyInternetConnection
            import com.github.loinguyencs.safeinternetaccesschecker.effect.InternetSafeCheck
            class NetworkClient { 
                @HasRiskyInternetConnection
                fun fetchData() {
                    println("Fetching data from the internet")
                }

                @InternetSafeCheck
                fun unsafeMethod() {
                    fetchData()  // This should be flagged
                }
            }
        """
        val findings = ConnectToInternetEffectRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 1
    }
}
