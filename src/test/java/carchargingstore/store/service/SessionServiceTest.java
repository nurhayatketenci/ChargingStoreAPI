package carchargingstore.store.service;

import carchargingstore.store.dto.StartSessionDto;
import carchargingstore.store.dto.StartSessionDtoConverter;
import carchargingstore.store.dto.SummaryDto;
import carchargingstore.store.exception.SessionNotAvailableException;
import carchargingstore.store.exception.SessionNotFoundException;
import carchargingstore.store.model.StatusEnum;
import carchargingstore.store.model.Session;
import carchargingstore.store.repository.StoreRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    @InjectMocks
    private StoreService service;

    @Mock
    StoreRepository repository;

    @Mock
    StartSessionDtoConverter converter;

    private StartSessionDto startSessionDto;

    @Test
    void testFindByStationId_whenStationIdExists_shouldReturnStore() {
        Session session = new Session("id", "stationId", LocalDateTime.now(), LocalDateTime.now(), StatusEnum.FINISHED);
        when(repository.findByStationId("stationId")).thenReturn(Optional.of(session));
        Session result = service.findByStationId("stationId");
        Assert.assertEquals(result, session);
    }

    @Test
    void testFindByStationId_whenStationIdDoesNotExists_shouldThrowSessionNotFoundException() {
        when(repository.findByStationId("stationId")).thenReturn(Optional.empty());
        assertThrows(SessionNotFoundException.class, () -> service.findByStationId("stationId"));
    }

    @Test
    void testStartChargingSession_whenStationIdExistAndStatuIsFinished_shouldReturnStartSessionDto() {
        Session session = new Session(
                "id",
                "stationId",
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEnum.FINISHED);

        StartSessionDto startSessionDto = new StartSessionDto(
                "id",
                "stationId",
                LocalDateTime.now(),
                StatusEnum.IN_PROGRESS);

        when(repository.findByStationId("stationId")).thenReturn(Optional.of(session));
        when(converter.convert(session)).thenReturn(startSessionDto);

        StartSessionDto result = service.startChargingSession("stationId");

        assertNotNull(result);
        assertEquals(startSessionDto, result);
    }

    @Test
    void testStartChargingSession_whenStationIdExistAndStatuInProgress_shouldThrowSessionNotAvailableException() {

        Session session = new Session(
                "id",
                "stationId",
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEnum.IN_PROGRESS);

        when(repository.findByStationId("stationId")).thenReturn(Optional.of(session));

        assertThrows(SessionNotAvailableException.class, () -> service.startChargingSession("stationId"));
    }

    @Test
    void testStopChargingSession_whenStationIdExistAndStatusInProgress_shouldReturnStoreWithStatusFinished() {
        Session session = new Session(
                "id",
                "stationId",
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEnum.IN_PROGRESS);
        when(repository.findByStationId("stationId")).thenReturn(Optional.of(session));
        Session result = service.stopChargingSession("stationId");
        assertEquals(StatusEnum.FINISHED, result.getStatus());
    }

    @Test
    void testStopChargingSession_whenStationIdExistAndStatusFinished_shouldReturnStoreWithStatusFinished() {
        Session session = new Session();
        session.setStatus(StatusEnum.FINISHED);

        when(repository.findByStationId("stationId")).thenReturn(Optional.of(session));

        assertThrows(SessionNotAvailableException.class, () -> service.stopChargingSession("stationId"));
    }

    @Test
    void testGetChargingSessionSummary_shouldReturnSummaryDto() {
        SummaryDto expectedResult = new SummaryDto(2L, 1L, 1L);

        List<Session> sessionList = List.of(
                new Session("id", "stationId", LocalDateTime.now(), LocalDateTime.now().minusMinutes(1), StatusEnum.IN_PROGRESS),
                new Session("id", "stationId", LocalDateTime.now(), LocalDateTime.now().minusMinutes(1), StatusEnum.FINISHED)
        );

        when(repository.findAll()).thenReturn(sessionList);

        SummaryDto result = service.getChargingSessionSummary();

        assertEquals(expectedResult, result);
    }


}