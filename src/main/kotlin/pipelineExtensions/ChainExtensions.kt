package pipelineExtensions

inline fun <T> T?.orElseThrow(exception: () -> Exception): T {
    return this ?: throw exception()
}

inline fun <T> T.proceedIf(check: (T) -> Boolean): T? {
    return if (check(this)) this else null
}

suspend inline fun <T> T.suspendProceedIf(
    check: suspend (T) -> Boolean
): T? {
    return if(check(this)) this else null
}