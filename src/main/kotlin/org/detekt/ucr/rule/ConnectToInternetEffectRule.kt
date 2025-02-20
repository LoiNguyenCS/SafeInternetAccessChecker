package com.github.loinguyencs.safeinternetaccesschecker

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.utils.IDEAPluginsCompatibilityAPI

class ConnectToInternetEffectRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Custom Rule",
        Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.hasConnectToInternetEffect(bindingContext)
            && !expression.insideTryExpression()
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Internet-connecting function calls should be inside a try-catch block."
                )
            )
        }
    }
}

@OptIn(IDEAPluginsCompatibilityAPI::class)
fun KtCallExpression.hasConnectToInternetEffect(bindingContext: BindingContext): Boolean {
    val resolvedCall = this.getResolvedCall(bindingContext) ?: return false
    val originalFunction = resolvedCall.resultingDescriptor.original as? SimpleFunctionDescriptorImpl ?: return false
    return originalFunction.annotations.any { annotation ->
        annotation.fqName?.asString() in listOf("HasRiskyInternetConnection", "effects.HasRiskyInternetConnection")
    }
}

fun KtCallExpression.insideTryExpression(): Boolean {
    var parent = this.parent
    while (parent != null) {
        if (parent is KtTryExpression) {
            return true
        }
        parent = parent.parent
    }
    return false
}
