package com.sam.shallwego.repository;

import com.sam.shallwego.domain.location.repository.MemberRepository;
import com.sam.shallwego.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "classpath:application-test.properties")
public class MemberRepositoryTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MemberRepository memberRepository;

    private Member member(String username, String password) {
        return Member.builder()
                .id(null)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }

    private Member member() {
        return member("username", "1234");
    }

    @DisplayName("회원가입 테스트")
    @Test
    void saveMember() {
        // given
        String username = "pizza";
        String password = "1234";
        Member member = member(username, password);

        // when
        Member savedMember = memberRepository.save(member);
        
        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo(username);
        assertThat(passwordEncoder.matches(password, savedMember.getPassword()))
                .isTrue();
    }

    @DisplayName("회원 찾기 테스트")
    @Test
    void findMember() {
        // given
        Member member = member();
        memberRepository.save(member);
    
        // when
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow();
        
        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getPassword()).isEqualTo(member.getPassword());
    }
}
