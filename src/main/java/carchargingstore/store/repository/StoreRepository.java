package carchargingstore.store.repository;

import carchargingstore.store.model.StatusEnum;
import carchargingstore.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;


public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByStationId(String stationId);
    Long countByStartedAtGreaterThanEqual(LocalDateTime oneMinuteAgo);
    Long countByStartedAtGreaterThanEqualAndStatus(LocalDateTime oneMinuteAgo,StatusEnum status);
    Long countByStoppedAtGreaterThanEqual(LocalDateTime oneMinuteAgo);
}
