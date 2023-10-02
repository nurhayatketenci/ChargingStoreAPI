package carchargingstore.store.service;

import carchargingstore.store.dto.StartSessionDto;
import carchargingstore.store.dto.StartSessionDtoConverter;
import carchargingstore.store.exception.SessionNotAvailableException;
import carchargingstore.store.exception.SessionNotFoundException;
import carchargingstore.store.model.StatusEnum;
import carchargingstore.store.model.Store;
import carchargingstore.store.repository.StoreRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    @InjectMocks
    private StoreService service;

    @Mock
    StoreRepository repository;

    @Mock
    StartSessionDtoConverter converter;
    private StartSessionDto startSessionDto;

    @Test
    void testFindByStationId_whenStationIdExists_shouldReturnStore(){
        Store store = new Store("id" , "stationId" , LocalDateTime.now() , LocalDateTime.now() , StatusEnum.FINISHED);
        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.of(store));
        Store result = service.findByStationId("stationId");
        Assert.assertEquals(result,store);
    }
    @Test
    void testFindByStationId_whenStationIdDoesNotExists_shouldThrowSessionNotFoundException(){
        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.empty());
        assertThrows(SessionNotFoundException.class , () -> service.findByStationId("stationId"));
    }
    @Test
    void testStartChargingSession_whenStationIdExistAndStatuIsFinished_shouldReturnStartSessionDto(){
        Store store = new Store("id" , "stationId" , LocalDateTime.now() , LocalDateTime.now() , StatusEnum.FINISHED);
        StartSessionDto startSessionDto=new StartSessionDto("id" ,"stationId" , LocalDateTime.now(),StatusEnum.IN_PROGRESS);

        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.of(store));
        Mockito.when(converter.convert(store)).thenReturn(startSessionDto);

        StartSessionDto result = service.startChargingSession("stationId");

        assertEquals(result,startSessionDto);
    }

    @Test
    void testStartChargingSession_whenStationIdExistAndStatuInProgress_shouldReturnStartSessionDto(){
        Store store = new Store();
        store.setStatus(StatusEnum.IN_PROGRESS);

        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.of(store));

        assertThrows(SessionNotAvailableException.class , () -> service.startChargingSession("stationId"));
    }

    @Test
    void testStopChargingSession_whenStationIdExistAndStatusInProgress_shouldReturnStoreWithStatusFinished(){
        Store store = new Store("id" , "stationId" , LocalDateTime.now() , LocalDateTime.now() , StatusEnum.IN_PROGRESS);
        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.of(store));
        Store result = service.stopChargingSession("stationId");
        assertEquals(StatusEnum.FINISHED, result.getStatus());
    }
    @Test
    void testStopChargingSession_whenStationIdExistAndStatusFinished_shouldReturnStoreWithStatusFinished(){
        Store store = new Store();
        store.setStatus(StatusEnum.FINISHED);

        Mockito.when(repository.findByStationId("stationId")).thenReturn(Optional.of(store));

        assertThrows(SessionNotAvailableException.class , () -> service.stopChargingSession("stationId"));
    }


}