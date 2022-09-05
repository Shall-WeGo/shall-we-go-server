package com.sam.shallwego.domain.savelocation.service;

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

import java.util.function.Supplier;

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
        return findLocationMonoByAddress(() -> findLocationOrElseByAddress(locationDto.getLocation()))
                .flatMap(location -> {
                    if (location.getId() == null) {
                        return Mono.fromCallable(() -> locationRepository.save(location))
                                .subscribeOn(Schedulers.boundedElastic());
                    }

                    return Mono.just(location);
                }).doOnNext(location -> memberService.memberMonoByUsername(username)
                        .doOnNext(member -> {
                            SaveLocation saveLocation = new SaveLocation(null, member, location);
                            Mono.fromCallable(() -> saveLocationRepository.save(saveLocation))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }).publishOn(Schedulers.boundedElastic())
                        .subscribe()).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> deleteLocation(final String token,
                                     final String address) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return findLocationMonoByAddress(() -> findLocationByAddress(address))
                .doOnNext(location -> memberService.memberMonoByUsername(username)
                        .doOnNext(member -> Mono.fromCallable(() -> saveLocationRepository.findByMemberAndLocation(member, location)
                                .orElseThrow(SaveLocation.NotSavedException::new))
                                .subscribeOn(Schedulers.boundedElastic())
                                .doOnNext(saveLocation -> Mono.fromRunnable(() -> saveLocationRepository.delete(saveLocation))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe()).subscribe()).publishOn(Schedulers.boundedElastic())
                        .subscribe()).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Transactional(readOnly = true)
    protected Mono<Location> findLocationMonoByAddress(Supplier<Location> supplier) {
        return Mono.fromCallable(supplier::get)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true)
    protected Location findLocationOrElseByAddress(String address) {
        return locationRepository.findByAddress(address)
                .orElse(new Location(null, address));
    }

    @Transactional(readOnly = true)
    protected Location findLocationByAddress(String address) {
        return locationRepository.findByAddress(address)
                .orElseThrow(Location.NotExistsException::new);
    }
}
