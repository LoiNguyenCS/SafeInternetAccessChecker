package com.github.loinguyencs.safeinternetaccesschecker.effect

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
/**
 * This is not an effect. This annotation notices the checker that the annotated function should be checked for network access safety
 */
annotation class InternetSafeCheck()
