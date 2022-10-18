package com.sam.shallwego.domain.savelocation.repository;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaveLocationRepository
        extends JpaRepository<SaveLocation, Long> {

    boolean existsByMemberAndLocation(Member member, Location location);

    List<SaveLocation> findAllByMember(Member member);

    boolean existsByMemberAndId(Member member, Long id);
}
