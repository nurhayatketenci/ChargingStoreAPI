package carchargingstore.store.dto;

import carchargingstore.store.model.Store;
import org.springframework.stereotype.Component;

@Component
public class StartSessionDtoConverter {
    public StartSessionDto convert(Store from){
        return new StartSessionDto(
                from.getId(),
                from.getStationId(),
                from.getStartedAt(),
                from.getStatus()
        );
    }
}
