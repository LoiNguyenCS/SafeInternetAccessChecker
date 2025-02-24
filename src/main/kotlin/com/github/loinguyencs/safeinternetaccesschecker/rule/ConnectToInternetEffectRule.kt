package com.github.loinguyencs.safeinternetaccesschecker.rule

import com.github.loinguyencs.safeinternetaccesschecker.util.MatchingAnnotationsVisitor
import com.github.loinguyencs.safeinternetaccesschecker.util.getFQNames
import com.github.loinguyencs.safeinternetaccesschecker.util.insideTryExpression
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.hasAnnotation
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

/**
 * A rule that ensures no risky Internet connection calls are made
 * inside targeted functions without proper exception handling.
 *
 * A "risky Internet connection" is defined as a function call that has effect
 * `@HasRiskyInternetConnection` and is not enclosed within a `try-catch` block.
 *
 * Targeted functions are those that are annotated with `@InternetSafeCheck` or have
 * the names `main` or `onCreate`.
 *
 * If a risky Internet connection is found inside a targeted function without a
 * `try-catch` block, a code smell is reported.
 */
class ConnectToInternetEffectRule(config: Config) : Rule(config) {

    // List of effects that imply risky Internet connection. For now, we only have "@HasRiskyInternetConnection"
    private val effectsOfRiskyInternetConnection = listOf(
        "com.github.loinguyencs.safeinternetaccesschecker.effect.HasRiskyInternetConnection",
        "HasRiskyInternetConnection")

    private var insideTargetFunction = false

    private var listOfRiskyInternetConnectionFunction = listOf<String>()

    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Safe Internet Access Rule",
        Debt.FIVE_MINS,
    )

    override fun preVisit(root: KtFile) {
        super.preVisit(root)
        MatchingAnnotationsVisitor.setAnnotations(effectsOfRiskyInternetConnection)
        root.accept(MatchingAnnotationsVisitor)
        listOfRiskyInternetConnectionFunction = MatchingAnnotationsVisitor.foundFunctionNames.toList()
        println("Here is our list $listOfRiskyInternetConnectionFunction")
    }

    /**
     * This function is called when a named function is visited. It checks if the function
     * is annotated with `@InternetSafeCheck` or is either `main` or `onCreate`.
     *
     * If the function is one of the targeted functions, it sets the `insideTargetFunction`
     * flag to `true` and processes the function body.
     *
     * @param function The function being visited.
     */
    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.hasAnnotation("InternetSafeCheck")
            || function.name == "main"
            || function.name == "onCreate") {
            insideTargetFunction = true
            super.visitNamedFunction(function)
            insideTargetFunction = false
            return
        }
        super.visitNamedFunction(function)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!insideTargetFunction) {
            return
        }

        val functionFqName = expression.getFQNames(bindingContext)

        println("We get $functionFqName")
        if (listOfRiskyInternetConnectionFunction.contains(functionFqName)
            && !expression.insideTryExpression()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Internet-connecting function calls ${expression.text} should be inside a try-catch block."
                )
            )
        }
    }
}
