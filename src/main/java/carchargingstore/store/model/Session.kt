package carchargingstore.store.model

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime

@Entity
data class Session (
        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name="UUID" , strategy = "org.hibernate.id.UUIDGenerator")
        val id:String?,
        var stationId: String?,
        var startedAt : LocalDateTime?,
        var stoppedAt : LocalDateTime?,
        @Enumerated(EnumType.STRING)
        var status : StatusEnum? =StatusEnum.FINISHED

) {
        constructor() : this(null, null, LocalDateTime.now(), LocalDateTime.now(), StatusEnum.FINISHED)
}
