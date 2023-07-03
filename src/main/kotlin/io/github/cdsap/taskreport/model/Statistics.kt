package io.github.cdsap.taskreport.model

data class Statistics(
    val mean: Long = 0L,
    val p25: Double = 0.0,
    val p50: Double = 0.0,
    val p75: Double = 0.0,
    val p90: Double = 0.0,
    val p99: Double = 0.0
)
