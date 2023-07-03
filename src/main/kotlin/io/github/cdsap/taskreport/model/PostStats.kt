package io.github.cdsap.taskreport.model

data class PostStats(
    val name: String,
    val module: String,
    var executions: Outcomes,
    val durationsExecuted: Statistics,
    val durationsFromCache: Statistics,
    val durationsFromUpToDate: Statistics,
    val durationsFromCacheLocal: Statistics,
    val durationsFromCacheRemote: Statistics,
    val fingerprintingExecuted: Statistics,
    val fingerprintingFromCache: Statistics,
    val fingerprintingFromUpToDate: Statistics,
    val fingerprintingFromCacheLocal: Statistics,
    val fingerprintingFromCacheRemote: Statistics,
    val type: String
)
