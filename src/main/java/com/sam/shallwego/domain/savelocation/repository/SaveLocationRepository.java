package com.sam.shallwego.domain.savelocation.repository;

import com.sam.shallwego.domain.embedded.MemberId;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaveLocationRepository
        extends JpaRepository<SaveLocation, MemberId> {
}
