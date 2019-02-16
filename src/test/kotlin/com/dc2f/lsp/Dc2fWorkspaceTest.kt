package com.dc2f.lsp

import mu.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.FileSystems

private val logger = KotlinLogging.logger {}

internal class Dc2fWorkspaceTest {

    @Test
    fun load() {
//        val url = javaClass.getResource("blugg.txt")
//        val path = File(url.file).toPath()
        val path = FileSystems.getDefault().getPath("src/test/resources/com/dc2f/lsp/test").toAbsolutePath()

        val workspace = Dc2fWorkspace(path, javaClass.classLoader)
        workspace.load()
        val completions = workspace.autocompletion(
            path.resolve("simple-blog/001.articles.blog/001.my-first-post.article/_index.yml"),
            FilePosition(4, 3)
        )
        logger.debug { "got completions: ${completions?.toStringShort()}"}
    }
}