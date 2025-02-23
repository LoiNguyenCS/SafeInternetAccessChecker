package com.github.loinguyencs.safeinternetaccesschecker.util

import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.utils.IDEAPluginsCompatibilityAPI

/**
 * Checks if the call expression has at least one annotation that matches any annotation in the provided list.
 *
 * @param bindingContext: The binding context used for resolving the call.
 * @param annotationList: A list of fully qualified annotation names to search for.
 *
 * @return `true` if at least one matching annotation is found, `false` otherwise.
 */
@OptIn(IDEAPluginsCompatibilityAPI::class)
fun KtCallExpression.hasMatchingAnnotations(bindingContext: BindingContext, annotationList: List<String>): Boolean {
    val resolvedCall = this.getResolvedCall(bindingContext) ?: return false
    val originalFunction = resolvedCall.resultingDescriptor.original as? SimpleFunctionDescriptorImpl ?: return false
    originalFunction.annotations.forEach {
            annotation -> println(annotation.fqName)
    }
    return originalFunction.annotations.any { annotation ->
        annotation.fqName?.asString() in annotationList
    }
}

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

