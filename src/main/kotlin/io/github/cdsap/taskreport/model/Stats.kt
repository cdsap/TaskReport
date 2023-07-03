package io.github.cdsap.taskreport.model

data class Stats(
    var type: String,
    var executions: Int = 0,
    var executionsExecuted: Int = 0,
    var executionsUpToDate: Int = 0,
    var executionsFromCache: Int = 0,
    var executionsFromCacheLocal: Int = 0,
    var executionsFromCacheRemote: Int = 0,
    val durationsExecuted: MutableList<Long>,
    val durationsFromCache: MutableList<Long>,
    val durationsFromUpToDate: MutableList<Long>,
    val durationsFromCacheLocal: MutableList<Long>,
    val durationsFromCacheRemote: MutableList<Long>,
    val fingerprintingExecuted: MutableList<Long>,
    val fingerprintingFromCache: MutableList<Long>,
    val fingerprintingFromUpToDate: MutableList<Long>,
    val fingerprintingFromCacheLocal: MutableList<Long>,
    val fingerprintingFromCacheRemote: MutableList<Long>
)
