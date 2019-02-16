package com.dc2f.lsp

import com.dc2f.*
import com.dc2f.util.readString
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.services.WorkspaceService
import java.nio.file.*
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

data class Dc2fConfig(val rootPath: String, val rootClass: String)

public class Dc2fWorkspace(
    path: Path,
    private var classLoader: ClassLoader
) {

    val rootPath: Path
    val rootClass: KClass<ContentDef>

    init {
        val configPath = path.resolve("dc2f.yml")
        if (!Files.exists(configPath)) {
            throw IllegalArgumentException("Missing required `dc2f.yml` at $configPath")
        }
        val config = ContentLoader.objectMapper.readValue<Dc2fConfig>(configPath.readString())
        rootPath = path.resolve(config.rootPath)
        require(Files.exists(rootPath)) {
            throw IllegalArgumentException("Can't find configured root path $rootPath")
        }
        @Suppress("UNCHECKED_CAST")
        rootClass = requireNotNull(classLoader.loadClass(config.rootClass)?.kotlin as? KClass<ContentDef>) {
            "Unable to find class ${config.rootClass}"
        }
    }

    private lateinit var loadedContent: LoadedContent<ContentDef>

    fun load(): LoadedContent<ContentDef> {
        loadedContent = ContentLoader(rootClass)
            .load(rootPath)
        return loadedContent
    }

    fun autocompletion(file: Path, pos: FilePosition): List<CompletionItem>? {
        val path = loadedContent.context.findContentPath(file.toAbsolutePath()) ?:
            throw IllegalArgumentException("Path does not correspond to a valid content. $file")

        val content = loadedContent.context.contentByPath[path] ?:
            throw IllegalArgumentException("Unable to find content for path $path")

        return YamlAutoCompletion(file.readString(), pos, content.javaClass.kotlin)
            .findCompletion()

    }




}
