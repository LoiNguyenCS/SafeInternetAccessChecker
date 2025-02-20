import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.matchers.collections.shouldHaveSize
import org.detekt.ucr.rule.ConnectToInternetEffectRule
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.jvmClasspathRoots
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

@KotlinCoreEnvironmentTest
internal class InternetConnectEffectRuleTest(private val env: KotlinCoreEnvironment) {

    @Test
    fun `should not report internet call inside try-catch block`() {
        val safeCode = """
            import org.detekt.ucr.effect.HasRiskyInternetConnection
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
    fun `should report internet call outside try-catch block`() {
        val code = """
            import org.detekt.ucr.effect.HasRiskyInternetConnection
            class NetworkClient {
                @HasRiskyInternetConnection
                fun fetchData() {
                    println("Fetching data from the internet")
                }

                fun unsafeMethod() {
                    fetchData()  // This should be flagged
                }
            }
        """
        val findings = ConnectToInternetEffectRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 1
    }
}
