package com.github.loinguyencs.safeinternetaccesschecker.util

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Checks if the resolved function of a [KtCallExpression] has an annotation that matches
 * any of the provided annotation names (without considering arguments).
 *
 * The check is case-sensitive, and only the fully qualified annotation name is compared.
 *
 * @receiver The [KtCallExpression] to analyze.
 * @param bindingContext The [BindingContext] used for resolving the function call.
 * @param annotations A [HashSet] containing fully qualified annotation names to check against.
 * @return `true` if the resolved function has at least one matching annotation; `false` otherwise.
 */
fun KtCallExpression.hasMatchingAnnotation(bindingContext: BindingContext, annotations: HashSet<String>): Boolean {
    val resolvedFunction = this.getResolvedCall(bindingContext)?.resultingDescriptor ?: return false
    return resolvedFunction.annotations.any { annotation ->
        val fqName = annotation.fqName?.asString() ?: return@any false
        val annotationNameWithoutArguments = fqName.substringBefore("{")
        annotations.contains(annotationNameWithoutArguments)
    }
}

fun KtCallExpression.getFqName(bindingContext: BindingContext): String =
    this.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull().toString()


/**
 * Checks if the call expression is inside a `try` expression.
 *
 * @return `true` if the call expression is inside a `try` block, `false` otherwise.
 */
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

