package com.dc2f.lsp

import com.dc2f.ContentDef
import mu.KotlinLogging
import org.eclipse.lsp4j.CompletionItem
import org.junit.jupiter.api.Test

interface TestSeo : ContentDef {
    val title: String
    val description: String
    val keywords: String
}

interface TestAuthor : ContentDef {
    val firstName: String
    val lastName: String
}

interface TestArticle : ContentDef {
    val headline: String
    val category: String
    val seo: TestSeo
    val author: TestAuthor
}

private val logger = KotlinLogging.logger {}

internal class YamlAutoCompletionTest {

    companion object {
        // language=yaml
        val testYaml = """
            headline: "Lorem Ipsum"
            author:
              firstName: "Herbert"
              lastName: "Poul"
            category: Examples
        """.trimIndent()
    }

    private fun testCompletion(line: Int, character: Int) =
        YamlAutoCompletion(
            testYaml, FilePosition(line, character), TestArticle::class
        ).findCompletion()

    @Test
    fun findCompletion() {
        val completions = testCompletion(1, 1)
        logger.debug { "completions: ${completions?.shortToString()}" }
    }

    @Test
    fun nestedCompletion() {
        val completions = testCompletion(3, 5)
        logger.debug { "completions: ${completions?.shortToString()}" }
    }
}

fun List<CompletionItem>.shortToString() =
    joinToString(",\n", "\n") { "${it.label}: ${it.detail}" }
