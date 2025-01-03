package ru.practicum.explore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;

@RestController
@Slf4j
@RequestMapping("/events")
public class HitGatewayController {
    private final WebClient webClient;

    public HitGatewayController(WebClient.Builder webClientBuilder, @Value("${server.url}") String serverUrl) {
        this.webClient = webClientBuilder.baseUrl(serverUrl)
                .filter((request, next) -> next.exchange(request)
                        .flatMap(response -> {
                            if (response.statusCode().isError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            return Mono.error(new ResponseStatusException(response.statusCode(), body));
                                        });
                            }
                            return Mono.just(response);
                        }))
                .build();
    }

    @PostMapping
    public Mono<ResponseEntity<HitDtoOut>> addRecord(HttpServletRequest request) {
        log.info("POST/ Проверка параметров запроса метода addRecord, URI - {}, IP - {}", request.getRequestURI(), request.getRemoteAddr());
        return webClient.post()
                .uri("/hit")
                .bodyValue(new HitDtoIn("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()))
                .exchangeToMono(response -> response.toEntity(HitDtoOut.class));
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<HitDtoOut>> addRecordWithId(@Positive @PathVariable(name = "id") Long id,
                                                           HttpServletRequest request) {
        log.info("POST/ Проверка параметров запроса метода addRecordWithId, URI - {}, IP - {}", request.getRequestURI(), request.getRemoteAddr());
        return webClient.post()
                .uri("/hit")
                .bodyValue(new HitDtoIn("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()))
                .exchangeToMono(response -> response.toEntity(HitDtoOut.class));
    }
}
