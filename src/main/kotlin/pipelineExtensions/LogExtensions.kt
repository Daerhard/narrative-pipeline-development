package pipelineExtensions

import io.github.oshai.kotlinlogging.KotlinLogging


private val logger = KotlinLogging.logger {}

fun <T> T.logInfo(message: (T) -> String): T =
    also { logger.info { message(it) } }

fun <T> T.logWarning(message: (T) -> String): T =
    also { logger.warn { message(it) } }

fun <T> T.logError(message: (T) -> String): T =
    also { logger.error { message(it) } }
