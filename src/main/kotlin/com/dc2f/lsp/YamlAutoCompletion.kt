package com.dc2f.lsp;

import com.dc2f.ContentDef
import com.dc2f.util.isJavaType
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.dataformat.yaml.*
import mu.KotlinLogging
import org.eclipse.lsp4j.CompletionItem
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/// 1 based (!!!!) position in the yaml content.
data class FilePosition(val line: Int, val character: Int)

private val logger = KotlinLogging.logger {}

class YamlAutoCompletion<T : ContentDef>(content: String, val pos: FilePosition, val rootClass: KClass<T>) {

    val factory = YAMLFactory()
    val parser = factory.createParser(content.reader()) as YAMLParser

    fun findCompletion(): List<CompletionItem>? {
        parser.forEach { token ->
            logger.info { "got token: $token at ${parser.currentLocation}" }
            handleObject(token, rootClass)?.let {
                return it
            }
        }
        return null
//        findAvailableAttributes(klass)
    }

    private fun checkPosition(token: JsonToken, klass: KClass<*>?) =
        if (parser.currentLocation.lineNr >= pos.line && parser.currentLocation.columnNr >= pos.character) {
            logger.info { "Found requested position. ${parser.currentLocation} vs $pos" }
            klass?.let(::findAvailableAttributes) ?: emptyList()
        } else { null }

    private fun handleObject(lastToken: JsonToken, klass: KClass<*>?): List<CompletionItem>? {
        assert(lastToken.isStructStart)
        var lastFieldName: String? = null
        checkPosition(lastToken, klass)?.let {
            return it
        }
        parser.forEach { token ->
            logger.trace { "Token: $token at ${parser.currentLocation}" }

            when (token) {
                JsonToken.FIELD_NAME -> {
                    lastFieldName = parser.valueAsString
                    logger.debug { "Found field name $lastFieldName" }
                }
                JsonToken.START_OBJECT -> {
                    logger.debug { "Found new nested object." }
                    handleObject(token, klass?.let { klass ->
                        klass.kotlinMemberProperties.first { it.name == lastFieldName }.returnType.classifier as? KClass<*>
                    })?.let {
                        return it
                    }
                }
                else -> {}
            }

            checkPosition(lastToken, klass)?.let {
                return it
            }
        }
        return null
    }

    private fun findAvailableAttributes(klass: KClass<*>): List<CompletionItem> {
        return klass.kotlinMemberProperties
            .map { member ->
                CompletionItem(member.name).also { item ->
                    item.detail = member.returnType.toString()
                }
            }
    }
}

val KClass<*>.kotlinMemberProperties get() =
    memberProperties.filter { member -> !member.returnType.isJavaType }

private inline fun YAMLParser.forEach(iterator: (token: JsonToken) -> Unit) =
    asSequence().forEach(iterator)

private fun YAMLParser.asSequence() = generateSequence { nextToken() }
