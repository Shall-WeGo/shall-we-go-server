package com.sam.shallwego.service.location;

import com.sam.shallwego.domain.embedded.MemberId;
import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import com.sam.shallwego.domain.savelocation.repository.SaveLocationRepository;
import com.sam.shallwego.domain.savelocation.service.LocationService;
import com.sam.shallwego.global.jwt.JwtUtil;
import com.sam.shallwego.global.jwt.config.JwtSecretConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SaveLocationRepository saveLocationRepository;

    @Mock
    private MemberService memberService;

    private final JwtSecretConfig jwtSecretConfig = new JwtSecretConfig();

    @Spy
    private JwtUtil jwtUtil = new JwtUtil(
            jwtSecretConfig.setInit("accessSecret", "refreshSecret")
    );

    private Member member() {
        return new Member(0L, "username", "password");
    }

    private Location location() {
        return location("address");
    }

    private Location location(String address) {
        return new Location(0L, address);
    }

    @Test
    @DisplayName("장소 저장 성공 테스트")        // 성공만 가능한 Service
    void saveLocationFailed() {
        // given
        Member member = member();
        LocationDto locationDto = new LocationDto("location");
        Location location = location(locationDto.getLocation());
        SaveLocation saveLocation = new SaveLocation(new MemberId(member), location);
        when(locationRepository.findByAddress(anyString()))
                .thenReturn(Optional.empty());
        when(locationRepository.save(any()))
                .thenReturn(location);
        when(memberService.memberMonoByUsername(anyString()))
                .thenReturn(Mono.just(member()));
        when(saveLocationRepository.save(any()))
                .thenReturn(saveLocation);

        // when
        Mono<Void> saveLocationMono = locationService
                .saveLocation(jwtUtil.generateAccessToken(member.getUsername()), locationDto);

        // then
        StepVerifier.create(saveLocationMono.log())
                .expectComplete()
                .verify();
    }


}
