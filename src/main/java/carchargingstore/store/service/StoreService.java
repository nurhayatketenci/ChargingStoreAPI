package carchargingstore.store.service;


import carchargingstore.store.dto.StartSessionDto;
import carchargingstore.store.dto.StartSessionDtoConverter;
import carchargingstore.store.dto.SummaryDto;
import carchargingstore.store.exception.SessionNotAvailableException;
import carchargingstore.store.exception.SessionNotFoundException;
import carchargingstore.store.model.StatusEnum;
import carchargingstore.store.model.Store;
import carchargingstore.store.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final StartSessionDtoConverter converter;
    private Logger logger = LoggerFactory.getLogger(StoreService.class);


    public StoreService(StoreRepository storeRepository, StartSessionDtoConverter converter) {
        this.storeRepository = storeRepository;
        this.converter = converter;
    }

    public StartSessionDto startChargingSession(String stationId) {
        Store store = findByStationId(stationId);
        if (store.getStatus() != StatusEnum.FINISHED) {
            logger.info("Session processing continues");
            throw new SessionNotAvailableException("Session processing continues");
        }
        store.setStartedAt(LocalDateTime.now());
        store.setStatus(StatusEnum.IN_PROGRESS);
        logger.info("Session started successfully");
        return converter.convert(storeRepository.save(store));
    }

    protected Store findByStationId(String stationId) {
        return storeRepository.findByStationId(stationId)
                .orElseThrow(() -> new SessionNotFoundException("There is no such session id: " + stationId));
    }

    public Store stopChargingSession(String stationId) {
        Store store = findByStationId(stationId);
        if (store.getStatus() == StatusEnum.FINISHED) {
            logger.info("This session has not started yet");
            throw new SessionNotAvailableException("This session has not started yet");
        }
        store.setStationId(stationId);
        store.setStatus(StatusEnum.FINISHED);
        store.setStoppedAt(LocalDateTime.now());
        store.setStartedAt(null);
        logger.info("Session stoped successfully");
        return this.storeRepository.save(store);
    }
    public SummaryDto getChargingSessionSummaryLastMinute() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long totalCount = storeRepository.countByStartedAtGreaterThanEqual(oneMinuteAgo);
        long startedCount = storeRepository.countByStartedAtGreaterThanEqualAndStatus(oneMinuteAgo, StatusEnum.IN_PROGRESS);
        long stoppedCount = storeRepository.countByStoppedAtGreaterThanEqual(oneMinuteAgo);

        return new SummaryDto(totalCount, startedCount, stoppedCount);
    }

    public List<Store> getAllSession() {
        return this.storeRepository.findAll();
    }
}
