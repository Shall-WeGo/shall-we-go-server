package com.sam.shallwego.domain.savelocation.repository;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaveLocationRepository
        extends JpaRepository<SaveLocation, Long> {

    Optional<SaveLocation> findByMemberAndLocation(Member member, Location location);
}
