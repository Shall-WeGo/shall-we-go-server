package com.sam.shallwego.domain.savelocation.controller;

import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public Mono<Void> saveLocation(Mono<Authentication> authenticationMono,
                                   @RequestBody @Valid LocationDto locationDto) {
        return authenticationMono
                .map(authentication -> authentication.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(token -> locationService.saveLocation(token, locationDto).subscribe())
                .then();
    }
}
