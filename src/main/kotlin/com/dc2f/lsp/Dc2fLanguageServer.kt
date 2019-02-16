package com.dc2f.lsp

import mu.KotlinLogging
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.CompletableFutures
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.*
import java.net.URI
import java.nio.file.*
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

class Dc2fLanguageServer : LanguageServer, LanguageClientAware, WorkspaceService {
    private lateinit var client: LanguageClient
    private lateinit var initParams: InitializeParams
    internal lateinit var workspace: CompletableFuture<Dc2fWorkspace>

    override fun connect(client: LanguageClient) {
        this.client = client
    }

    override fun shutdown(): CompletableFuture<Any> {
        logger.info { "shutdown" }
        return CompletableFuture.completedFuture(null)
    }

    override fun getTextDocumentService(): TextDocumentService {
        logger.info { "getTextDocumentService()" }
        return Dc2fTextDocumentService(this)
    }

    override fun exit() {
        logger.info { "exit" }
    }

    private lateinit var workspaceLoadingThread: Thread

    override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
        logger.info { "Initializing with params ${params}" }
        return CompletableFutures.computeAsync { cancel ->
//        return CompletableFuture.completedFuture({
            this.initParams = params
            val rootPath = uriToPath(params.rootUri)
            if (!Files.exists(rootPath)) {
                logger.error { "Unable to find root path $rootPath" }
            }
//            this.workspace = CompletableFuture.completedFuture(Dc2fWorkspace(rootPath, javaClass.classLoader))
            this.workspace = CompletableFuture.supplyAsync {
                val workspace = Dc2fWorkspace(rootPath, javaClass.classLoader)
                workspace.load()
                workspace
            }
            val capabilities = ServerCapabilities()
            capabilities.textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
            capabilities.completionProvider = CompletionOptions(true, listOf(".", "="))
            logger.debug { "ok, replying $capabilities" }
            InitializeResult(capabilities)
        }
    }

    override fun getWorkspaceService(): WorkspaceService {
        logger.info("getWorkspaceService()")
        return object : WorkspaceService {
            override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {

            }

            override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {

            }

        }
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
        logger.info { "didChangeWatchedFiles $params" }
    }

    override fun didChangeConfiguration(params: DidChangeConfigurationParams?) {
        logger.info { "didChangeConfiguration $params" }

    }

}

class Dc2fTextDocumentService(
    val languageServer: Dc2fLanguageServer
) : TextDocumentService {
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

    override fun completion(position: CompletionParams): CompletableFuture<Either<MutableList<CompletionItem>, CompletionList>> {
        logger.info("completion for $position")
        return languageServer.workspace.thenApplyAsync { workspace ->
            val path = uriToPath(position.textDocument.uri)

            Either.forRight<MutableList<CompletionItem>, CompletionList>(
                CompletionList(
                    workspace.autocompletion(
                        path,
                        FilePosition(position.position.line + 1, position.position.character + 1)
                    )
                )
            )
        }
    }
//        CompletableFuture.supplyAsync {
//            logger.info("completion for $position")
//            val path = uriToPath(position.textDocument.uri)
//
//            Either.forRight<MutableList<CompletionItem>, CompletionList>(
//                CompletionList(
//                        .autocompletion(
//                        path, FilePosition(position.position.line+1, position.position.character+1))
//                )
//            )
////            return (Either.forRight(CompletionList(true, listOf(CompletionItem("Lorem Ipsum")))))
//        }

}

private fun uriToPath(uri: String) : Path =
    FileSystems.getDefault().getPath(URI(uri).toURL().file)
