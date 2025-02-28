package com.github.loinguyencs.safeinternetaccesschecker.rule

import RiskyInternetAccessRegistry.internetAccessAnnotations
import RiskyInternetAccessRegistry.popularRiskyLibraryMethods
import com.github.loinguyencs.safeinternetaccesschecker.util.getFqName
import com.github.loinguyencs.safeinternetaccesschecker.util.hasMatchingAnnotation
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
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Ensures that functions do not make risky internet connections
 * without proper exception handling.
 *
 * A "risky internet connection" refers to a function annotated with
 * `@HasRiskyInternetConnection`. These functions contain internet-connecting
 * calls that are not enclosed in a `try-catch` block.
 *
 * By default, functions are considered `@Safe` unless explicitly annotated.
 * A safe function must not contain unhandled internet connection calls.
 *
 * If a risky internet connection is detected inside a `@Safe` function,
 * a code smell is reported.
 */

class ConnectToInternetEffectRule(config: Config) : Rule(config) {



    // Functions have @Safe effect if they have no unhandled network calls.
    private var insideSafeFunction = false

    private var listOfRiskyInternetConnectionFunction = popularRiskyLibraryMethods

    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Safe Internet Access Rule",
        Debt.FIVE_MINS,
    )


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
        if (!function.hasAnnotation("HasRiskyInternetConnection")) {
            insideSafeFunction = true
            super.visitNamedFunction(function)
            insideSafeFunction = false
            return
        }
        super.visitNamedFunction(function)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!insideSafeFunction) {
            return
        }

        val initiateInternetConnection =
            listOfRiskyInternetConnectionFunction.contains(expression.getFqName(bindingContext))
                    || expression.hasMatchingAnnotation(bindingContext, internetAccessAnnotations)
        if ( initiateInternetConnection && !expression.insideTryExpression()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    message = "The call ${expression.text} initiates an internet connection but is not handled properly. Wrap it in a try-catch block or annotate the enclosing function with @HasRiskyInternetConnection.")
            )
        }
    }
}
