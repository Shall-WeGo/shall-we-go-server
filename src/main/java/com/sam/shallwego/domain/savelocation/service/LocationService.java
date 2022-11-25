package com.sam.shallwego.domain.savelocation.service;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import com.sam.shallwego.domain.savelocation.repository.SaveLocationRepository;
import com.sam.shallwego.domain.savelocation.ro.SaveLocationRO;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final SaveLocationRepository saveLocationRepository;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public Mono<SaveLocationRO> saveLocation(final String token,
                                   final LocationDto locationDto) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return findLocationOrElseByAddress(locationDto.getLocation(), locationDto.getPlace(), String.valueOf(locationDto.getCoordinateX()), String.valueOf(locationDto.getCoordinateY()))
                .flatMap(location -> {
                    if (location.getId() == null) {
                        return Mono.fromSupplier(() -> locationRepository.save(location))
                                .subscribeOn(Schedulers.boundedElastic());
                    }

                    return Mono.just(location);
                })
                .flatMap(location -> memberService.memberMonoByUsername(username)
                        .flatMap(member -> Mono.fromCallable(() -> saveLocationRepository.existsByMemberAndLocation(member, location))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(SaveLocation.AlreadySavedException::new);
                                    }

                                    SaveLocation saveLocation = new SaveLocation(null, member, location);
                                    return Mono.fromSupplier(() -> saveLocationRepository.save(saveLocation))
                                            .subscribeOn(Schedulers.boundedElastic());
                                }))).flatMap(saveLocation -> Mono.just(new SaveLocationRO(saveLocation)));
    }

    public Mono<Object> deleteLocation(final String token,
                                     final long addressId) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return memberService.memberMonoByUsername(username)
                .flatMap(member -> existsByMemberAndId(member, addressId)
                        .flatMap(exists -> {
                            if (!exists) {
                                return Mono.error(SaveLocation.NotSavedException::new);
                            }
                            return Mono.fromRunnable(() -> saveLocationRepository.deleteById(addressId))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }));
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
    public Mono<Location> findLocationOrElseByAddress(String... info) {
        return Mono.fromCallable(() -> locationRepository.findByAddress(info[0])
                .orElse(new Location(null, info[0], info[1], Double.parseDouble(info[2]), Double.parseDouble(info[3]))))
                .publishOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true)
    public Mono<Location> findLocationByAddress(String address) {
        return Mono.fromCallable(() -> locationRepository.findByAddress(address)
                .orElseThrow(Location.NotExistsException::new))
                .publishOn(Schedulers.boundedElastic());
    }

    @Transactional(readOnly = true)
    protected Mono<Boolean> existsByMemberAndId(Member member, long addressId) {
        return Mono.fromCallable(() -> saveLocationRepository
                        .existsByMemberAndId(member, addressId))
                .publishOn(Schedulers.boundedElastic());
    }
}
