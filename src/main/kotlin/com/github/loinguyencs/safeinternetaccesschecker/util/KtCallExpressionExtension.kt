package com.github.loinguyencs.safeinternetaccesschecker.util

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Get the fully qualified function name of a KtCallExpression. Note that the return result is nullable.
 */
fun KtCallExpression.getFQNames(bindingContext: BindingContext) = this.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull().toString()

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

