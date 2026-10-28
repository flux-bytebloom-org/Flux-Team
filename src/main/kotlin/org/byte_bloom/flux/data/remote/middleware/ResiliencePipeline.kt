package org.byte_bloom.flux.data.remote.middleware

class ResiliencePipeline(
    private val middlewares: List<ResilienceMiddleware>
) {
    suspend fun <T> execute(block: suspend () -> T): T {
        val chain = middlewares.foldRight(block) { middleware, next ->
            { middleware.intercept(next) }
        }
        return chain()
    }
}
