package com.dc2f.lsp.test

import assertk.Assert
import assertk.assertions.*
import org.eclipse.lsp4j.CompletionItem

fun Assert<List<CompletionItem>>.containsLabelsOnly(labels: List<String>) =
    transform { list -> list.map { it.label } }.containsOnly(*labels.toTypedArray())

fun Assert<List<CompletionItem>>.containsLabelsOnly(vararg labels: String) =
    transform { list -> list.map { it.label } }.containsOnly(*labels)
