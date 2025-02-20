import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.matchers.collections.shouldHaveSize
import com.github.loinguyencs.safeinternetaccesschecker.ConnectToInternetEffectRule
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class InternetConnectEffectRuleTest(private val env: KotlinCoreEnvironment) {

    @Test
    fun `should not detect internet call inside try-catch block`() {
        val safeCode = """
            import effects.HasRiskyInternetConnection
            class NetworkClient {
                @HasRiskyInternetConnection
                fun fetchData() {
                    println("Fetching data from the internet")
                }

                fun safeMethod() {
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
        class A {
          class B
        }
        """
        val findings = ConnectToInternetEffectRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 0
    }
}
