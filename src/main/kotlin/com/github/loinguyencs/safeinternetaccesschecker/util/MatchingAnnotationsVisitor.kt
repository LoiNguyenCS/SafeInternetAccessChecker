package com.github.loinguyencs.safeinternetaccesschecker.util

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * A visitor that traverses Kotlin functions and collects the fully qualified names (FQNs)
 * of functions annotated with specific annotations.
 */
object MatchingAnnotationsVisitor : DetektVisitor() {

    /** List of annotation names (without @) to match against function annotations. */
    private var listOfAnnotations: List<String> = emptyList()

    /** Internally mutable set to store the fully qualified names of matching functions. */
    private var _foundFunctionNames = mutableSetOf<String>()

    /**
     * Publicly accessible immutable set of function FQNs that contain the specified annotations.
     */
    val foundFunctionNames: Set<String> get() = _foundFunctionNames

    /**
     * Sets the list of annotation names to check for before running the visitor.
     *
     * @param annotations List of annotation names (without @).
     */
    fun setAnnotations(annotations: List<String>) {
        listOfAnnotations = annotations
    }

    /**
     * Clears previously found function names. Should be called before visiting a new set of files.
     */
    fun reset() {
        _foundFunctionNames.clear()
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val fqName = function.fqName?.asString() ?: function.name ?: "<anonymous>"

        val hasMatchingAnnotation = function.annotationEntries.any { annotation ->
            annotation.shortName?.asString() in listOfAnnotations
        }

        if (hasMatchingAnnotation) {
            _foundFunctionNames.add(fqName)
        }
    }
}
