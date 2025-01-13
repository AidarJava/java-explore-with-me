package ru.practicum.explore.participation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.participation.dto.ParticipationDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class ParticipationController {
    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationDtoOut> getParticipationForAnotherUser(@PathVariable(name = "userId") Integer userId) {
        log.info("GET/ Проверка параметров запроса метода getParticipationForAnotherUser, userId - {}", userId);
        return participationService.getParticipationForAnotherUser(userId);
    }

    @PostMapping
    public ParticipationDtoOut addParticipation(@PathVariable(name = "userId") Integer userId,
                                                @RequestParam(name = "eventId") Integer eventId) {
        log.info("POST/ Проверка параметров запроса метода addParticipation, userId - {}, eventId - {}", userId, eventId);
        return participationService.addParticipation(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationDtoOut cancelParticipation(@PathVariable(name = "userId") Integer userId,
                                                   @PathVariable(name = "requestId") Integer requestId) {
        log.info("PATCH/ Проверка параметров запроса метода cancelParticipation, userId - {}, requestId - {}", userId, requestId);
        return participationService.cancelParticipation(userId, requestId);
    }

}
