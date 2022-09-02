package com.sam.shallwego.domain.member.repository;

import com.sam.shallwego.domain.member.entity.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

}
