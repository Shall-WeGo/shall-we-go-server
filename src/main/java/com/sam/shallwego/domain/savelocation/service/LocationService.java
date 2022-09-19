package com.sam.shallwego.domain.savelocation.service;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import com.sam.shallwego.domain.savelocation.repository.SaveLocationRepository;
import com.sam.shallwego.domain.savelocation.ro.SaveLocationRO;
import com.sam.shallwego.global.exception.BusinessException;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.logging.Level;
import java.util.stream.Collectors;

@Slf4j
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
        return findLocationOrElseByAddress(locationDto.getLocation())
                .flatMap(location -> {
                    if (location.getId() == null) {
                        return Mono.fromSupplier(() -> locationRepository.save(location))
                                .subscribeOn(Schedulers.boundedElastic());
                    }

                    return Mono.just(location);
                }).doOnNext(location -> memberService.memberMonoByUsername(username)
                        .doOnNext(member -> {
                            SaveLocation saveLocation = new SaveLocation(null, member, location);
                            Mono.fromSupplier(() -> saveLocationRepository.save(saveLocation))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }).publishOn(Schedulers.boundedElastic())
                        .subscribe()).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Object> deleteLocation(final String token,
                                     final String address) {
        log.warn("서비스 시작");
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return findLocationByAddress(address)
                .flatMap(location -> memberService.memberMonoByUsername(username)
                        .flatMap(member -> Mono.fromCallable(() -> saveLocationRepository.findByMemberAndLocation(member, location)
                                        .orElseThrow(SaveLocation.NotSavedException::new))
                                .onErrorResume(BusinessException.class, e -> {e.printStackTrace(); return Mono.error(e);})
                                .subscribeOn(Schedulers.boundedElastic()))
                        .doOnSuccess(saveLocation -> Mono.fromRunnable(() -> saveLocationRepository.delete(saveLocation))
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe())).publishOn(Schedulers.boundedElastic())
                .onErrorResume(Mono::error).publishOn(Schedulers.boundedElastic())
                .flatMap(saveLocation -> Mono.empty())
                .log("서비스 끝", Level.WARNING);
    }

    @Transactional(readOnly = true)
    public Flux<SaveLocationRO> findLocationsByMember(final String token) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return memberService.memberMonoByUsername(username)
                .flatMap(member -> Mono.fromCallable(() -> saveLocationRepository
                        .findAllByMember(member)
                        .parallelStream()
                        .map(SaveLocationRO::new)
                        .collect(Collectors.toList()))
                        .subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true)
    public Mono<Location> findLocationOrElseByAddress(String address) {
        return Mono.fromCallable(() -> locationRepository.findByAddress(address)
                .orElse(new Location(null, address)))
                .publishOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true)
    public Mono<Location> findLocationByAddress(String address) {
        return Mono.fromCallable(() -> locationRepository.findByAddress(address)
                .orElseThrow(Location.NotExistsException::new))
                .publishOn(Schedulers.boundedElastic());
    }
}
