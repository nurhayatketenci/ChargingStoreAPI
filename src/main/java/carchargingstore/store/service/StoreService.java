package carchargingstore.store.service;


import carchargingstore.store.dto.StartSessionDto;
import carchargingstore.store.dto.StartSessionDtoConverter;
import carchargingstore.store.dto.SummaryDto;
import carchargingstore.store.exception.StationAlreadyExistException;
import carchargingstore.store.exception.SessionNotAvailableException;
import carchargingstore.store.exception.SessionNotFoundException;
import carchargingstore.store.model.StatusEnum;
import carchargingstore.store.model.Session;
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
        Session session = findByStationId(stationId);
        if (session.getStatus() != StatusEnum.FINISHED) {
            logger.info("Session processing continues");
            throw new SessionNotAvailableException("Session processing continues");
        }
        session.setStartedAt(LocalDateTime.now());
        session.setStatus(StatusEnum.IN_PROGRESS);
        logger.info("Session started successfully ");
        StartSessionDto startSessionDto = converter.convert(session);
        storeRepository.save(session);
        return startSessionDto;
    }


    public Session stopChargingSession(String stationId) {
        Session session = findByStationId(stationId);
        if (session.getStatus() == StatusEnum.FINISHED) {
            logger.info("This session has not started yet");
            throw new SessionNotAvailableException("This session has not started yet");
        }
        session.setStationId(stationId);
        session.setStatus(StatusEnum.FINISHED);
        session.setStoppedAt(LocalDateTime.now());
        logger.info("Session stoped successfully");
        this.storeRepository.save(session);
        return session;
    }


    public SummaryDto getChargingSessionSummary() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<Session> chargingSessions = storeRepository.findAll();

        long totalCount = chargingSessions.size();

        long startedCount = chargingSessions.stream()
                .filter(session -> session.getStatus() == StatusEnum.IN_PROGRESS && session.getStartedAt()
                        .isAfter(oneMinuteAgo)).count();

        long stoppedCount = chargingSessions.stream()
                .filter(session -> session.getStatus() == StatusEnum.FINISHED && session.getStoppedAt()
                        .isAfter(oneMinuteAgo)).count();

        return new SummaryDto(totalCount, startedCount, stoppedCount);
    }


    public void addNewStationId(String stationId) {
        boolean isStationIdExists = this.storeRepository.existsByStationId(stationId);
        if (isStationIdExists) {
            logger.info("This station id already exist stationId : " + stationId);
            throw new StationAlreadyExistException("This station id already exist stationId : " + stationId);
        }
        Session newSession = new Session();
        newSession.setStationId(stationId);
        this.storeRepository.save(newSession);
        logger.info("Station id added successfully");
    }


    protected Session findByStationId(String stationId) {
        return storeRepository.findByStationId(stationId)
                .orElseThrow(() -> new SessionNotFoundException("There is no such session id: " + stationId));
    }


    public List<Session> getAllSession() {
        return this.storeRepository.findAll();
    }


}
