package io.github.cdsap.taskreport.output

import io.github.cdsap.taskreport.model.PostStats
import io.github.cdsap.taskreport.model.Statistics
import java.io.File

class GeneralCsvOutput(private val outcome: List<PostStats>, private val isFilterByType: Boolean) {

    fun write(requestedTask: String?, typeReport: String) {
        val csv = "${typeReport}-report-$requestedTask-${System.currentTimeMillis()}.csv"
        val moduleLabel = if(isFilterByType) "" else "module, "
        val headers =
            "task,$moduleLabel executions,outcome_executed,outcome_up_to_date,outcome_from_cache,outcome_from_cache_local," +
                "outcome_from_cache_remote,${headersDuration("duration_executed")},${headersDuration("duration_up_to_date")}," +
                "${headersDuration("durations_from_cache")},${headersDuration("durations_from_cache_local")},${
                    headersDuration(
                        "durations_from_cache_remote"
                    )
                }," +
                "${headersDuration("fingerprinting_executed")},${headersDuration("fingerprinting_up_to_date")}," +
                "${headersDuration("fingerprinting_from_cache")},${headersDuration("fingerprinting_from_cache_local")},${
                    headersDuration(
                        "fingerprinting_from_cache_remote"
                    )
                }\n"

        var values = ""
        outcome.forEach {
            val moduleValue = if(isFilterByType) "" else "${it.module}, "

            values += "${it.name},$moduleValue ${it.executions.executions},${it.executions.executionsExecuted},${it.executions.executionsUpToDate},${it.executions.executionsFromCache},${it.executions.executionsFromCacheLocal},${it.executions.executionsFromCacheRemote}," +
                "${valuesDuration(it.durationsExecuted)},${valuesDuration(it.durationsFromUpToDate)},${valuesDuration(it.durationsFromCache)},${valuesDuration(it.durationsFromCacheLocal)},${valuesDuration(it.durationsFromCacheRemote)},"+
                "${valuesDuration(it.fingerprintingExecuted)},${valuesDuration(it.fingerprintingFromUpToDate)},${valuesDuration(it.fingerprintingFromCache)},${valuesDuration(it.fingerprintingFromCacheLocal)},${valuesDuration(it.fingerprintingFromCacheRemote)}\n"
        }
        File(csv).writeText("""$headers$values""".trimIndent())
        println("File $csv created")
    }

    private fun headersDuration(name: String) = "mean_${name},p25_${name},p50_${name},p75_${name},p90_${name},p99_${name}"

    private fun valuesDuration(stat: Statistics) = "${stat.mean},${stat.p25},${stat.p50},${stat.p75},${stat.p90},${stat.p99}"
}
