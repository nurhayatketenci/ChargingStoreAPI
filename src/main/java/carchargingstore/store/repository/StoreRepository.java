package carchargingstore.store.repository;

import carchargingstore.store.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StoreRepository extends JpaRepository<Session, String> {
    Optional<Session> findByStationId(String stationId);

    Boolean existsByStationId(String stationId);

}
