package io.github.cdsap.taskreport.model

data class Outcomes(
    var executions: Int = 0,
    var executionsExecuted: Int = 0,
    var executionsUpToDate: Int = 0,
    var executionsFromCache: Int = 0,
    var executionsFromCacheLocal: Int = 0,
    var executionsFromCacheRemote: Int = 0,
    var percentageExecution: Float = 0f,
    var percentageCaching: Float = 0f,
    var percentageLocalCaching: Float = 0f,
    var percentageRemoteCaching: Float = 0f,
    var percentageUpToDate: Float = 0f
)
