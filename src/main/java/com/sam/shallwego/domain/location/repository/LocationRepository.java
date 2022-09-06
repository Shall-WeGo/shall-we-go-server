package com.sam.shallwego.domain.location.repository;

import com.sam.shallwego.domain.location.entity.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {

    Optional<Location> findByAddress(String address);
}
