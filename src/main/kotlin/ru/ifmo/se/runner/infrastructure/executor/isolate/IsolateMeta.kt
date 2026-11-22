package ru.ifmo.se.runner.infrastructure.executor.isolate

data class IsolateMeta(
    val time: Double = 0.0,
    val wallTime: Double = 0.0,
    val memoryKb: Int = 0,
    val exitCode: Int = 0,
    val status: String? = null,
    val message: String? = null,
) {
    companion object {
        fun parse(metaContent: String): IsolateMeta {
            val lines = metaContent.lines()
            val map = mutableMapOf<String, String>()
            for (line in lines) {
                val colonIndex = line.indexOf(':')
                if (colonIndex > 0) {
                    val key = line.substring(0, colonIndex).trim()
                    val value = line.substring(colonIndex + 1).trim()
                    map[key] = value
                }
            }

            return IsolateMeta(
                time = map["time"]?.toDoubleOrNull() ?: 0.0,
                wallTime = map["time-wall"]?.toDoubleOrNull() ?: 0.0,
                memoryKb = map["cg-mem"]?.toIntOrNull() ?: map["max-rss"]?.toIntOrNull() ?: 0,
                exitCode = map["exitcode"]?.toIntOrNull() ?: 0,
                status = map["status"],
                message = map["message"],
            )
        }
    }
}
