package carchargingstore.store.dto;

import carchargingstore.store.model.Session;
import org.springframework.stereotype.Component;

@Component
public class StartSessionDtoConverter {
    public StartSessionDto convert(Session from){
        return new StartSessionDto(
                from.getId(),
                from.getStationId(),
                from.getStartedAt(),
                from.getStatus()
        );
    }
}
