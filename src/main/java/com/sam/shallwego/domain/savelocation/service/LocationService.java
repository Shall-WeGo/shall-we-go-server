package com.sam.shallwego.domain.savelocation.service;

import com.sam.shallwego.domain.embedded.MemberId;
import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import com.sam.shallwego.domain.savelocation.repository.SaveLocationRepository;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final SaveLocationRepository saveLocationRepository;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public Mono<Void> saveLocation(final String token,
                                   final LocationDto locationDto) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return findLocationByAddress(locationDto.getLocation())
                .flatMap(location -> {
                    if (location.getId() == null) {
                        return Mono.fromCallable(() -> locationRepository.save(location))
                                .subscribeOn(Schedulers.boundedElastic());
                    }

                    return Mono.just(location);
                }).doOnNext(location -> memberService.memberMonoByUsername(username)
                        .doOnNext(member -> {
                            MemberId memberId = new MemberId(member);
                            SaveLocation saveLocation = new SaveLocation(memberId, location);
                            Mono.fromCallable(() -> saveLocationRepository.save(saveLocation))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }).publishOn(Schedulers.boundedElastic())
                        .subscribe()).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Transactional(readOnly = true)
    protected Mono<Location> findLocationByAddress(String address) {
        return Mono.fromCallable(() -> locationRepository.findByAddress(address)
                .orElse(new Location(null, address)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
