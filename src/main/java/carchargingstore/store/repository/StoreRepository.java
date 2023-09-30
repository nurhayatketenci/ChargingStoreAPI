package carchargingstore.store.repository;

import carchargingstore.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByStationId(String stationId);
}
