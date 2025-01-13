package ru.practicum.explore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Stats;

import java.util.List;

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
    public Mono<ResponseEntity<HitDtoOut>> addRecordWithId(@PathVariable(name = "id") Integer id,
                                                           HttpServletRequest request) {
        log.info("POST/ Проверка параметров запроса метода addRecordWithId, URI - {}, IP - {}", request.getRequestURI(), request.getRemoteAddr());
        return webClient.post()
                .uri("/hit")
                .bodyValue(new HitDtoIn("ewm-main-service", request.getRequestURI(), request.getRemoteAddr()))
                .exchangeToMono(response -> response.toEntity(HitDtoOut.class));
    }

    @GetMapping("/stats")
    public Mono<ResponseEntity<List<Stats>>> getHits(@RequestParam(name = "start") String start,
                                                     @RequestParam(name = "end") String end,
                                                     @RequestParam(name = "uris", required = false) String[] uris,
                                                     @RequestParam(name = "unique", defaultValue = "false", required = false) Boolean unique) {
        log.info("GET/ Проверка параметров запроса метода getHits, start - {}, end - {}, uris - {}, unique - {}", start, end, uris, unique);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .exchangeToMono(response -> response.toEntityList(Stats.class));
    }
}
