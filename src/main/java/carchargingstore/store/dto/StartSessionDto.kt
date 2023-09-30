package carchargingstore.store.dto

import carchargingstore.store.model.StatusEnum
import java.time.LocalDateTime

data class StartSessionDto(
        val id: String?,
        val stationId: String?,
        val startedAt: LocalDateTime?,
        val status: StatusEnum? = StatusEnum.FINISHED

)
