package com.dc2f.lsp

import com.dc2f.util.toStringReflective
import mu.KotlinLogging
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints
import org.eclipse.lsp4j.services.*
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

class Dc2fLanguageServer : LanguageServer, LanguageClientAware {
    private lateinit var client: LanguageClient

    override fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun shutdown(): CompletableFuture<Any> {
        logger.info { "shutdown" }
        return CompletableFuture.completedFuture(null)
    }

    override fun getTextDocumentService(): TextDocumentService {
        logger.info { "getTextDocumentService()" }
        return Dc2fTextDocumentService()
    }

    override fun exit() {
        logger.info { "exit" }
    }

    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult> {
        logger.info { "Initializing with params ${params?.toString()}" }
        val capabilities = ServerCapabilities()
        capabilities.textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
        capabilities.completionProvider = CompletionOptions(true, listOf(".", "="))
        return CompletableFuture.completedFuture(InitializeResult(capabilities))
    }

    override fun getWorkspaceService(): WorkspaceService {
        logger.info("getWorkspaceService()")
        return Dc2fWorkspaceService()
    }

}

class Dc2fTextDocumentService : TextDocumentService {
    override fun didOpen(params: DidOpenTextDocumentParams?) {
        logger.info { "didOpen $params" }
    }

    override fun didSave(params: DidSaveTextDocumentParams?) {
        logger.info { "didSave $params" }
    }

    override fun didClose(params: DidCloseTextDocumentParams?) {
        logger.info { "didClose $params" }
    }

    override fun didChange(params: DidChangeTextDocumentParams?) {
        logger.info { "didChange $params" }
    }

    override fun completion(position: CompletionParams?): CompletableFuture<Either<MutableList<CompletionItem>, CompletionList>> {
        logger.info("completion for $position")
        return CompletableFuture.completedFuture((Either.forRight(CompletionList(true, listOf(CompletionItem("Lorem Ipsum"))))))
    }

}

class Dc2fWorkspaceService : WorkspaceService {
    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
        logger.info { "didChangeWatchedFiles $params" }
    }

    override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {
        logger.info { "didChangeConfiguration $params" }

    }

}
