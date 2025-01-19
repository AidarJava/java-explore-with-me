package ru.yandex.practicum.ewm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explore.comments.CommentService;
import ru.practicum.explore.comments.controllers.CommentPrivateController;
import ru.practicum.explore.comments.dto.CommentDtoIn;
import ru.practicum.explore.comments.dto.CommentSortDtoOut;

import static org.hamcrest.Matchers.is;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllersTests {
    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentPrivateController commentPrivateController;

    private MockMvc mvc;

    private CommentDtoIn commentDtoIn;

    private CommentSortDtoOut commentSortDtoOut;
    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        commentDtoIn = new CommentDtoIn();
        commentDtoIn.setText("Новый комментарий");

        commentSortDtoOut = new CommentSortDtoOut();
        commentSortDtoOut.setId(0);
        commentSortDtoOut.setCreator("Василий");
        commentSortDtoOut.setEventAnnotation("Воздушные шары");
        commentSortDtoOut.setText("Новый комментарий");

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SneakyThrows
    @Test
    void saveNewComment() {
        mvc = MockMvcBuilders
                .standaloneSetup(commentPrivateController)
                .build();
        when(commentService.addComment(any(), any(), any()))
                .thenReturn(commentSortDtoOut);

        mvc.perform(post("/comments/users/0?eventId=0")
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentSortDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.creator", is(commentSortDtoOut.getCreator())))
                .andExpect(jsonPath("$.eventAnnotation", is(commentSortDtoOut.getEventAnnotation())))
                .andExpect(jsonPath("$.text", is(commentSortDtoOut.getText())));
    }

    @SneakyThrows
    @Test
    void updateComment() {
        mvc = MockMvcBuilders
                .standaloneSetup(commentPrivateController)
                .build();
        when(commentService.updateComment(any(), any(), any()))
                .thenReturn(commentSortDtoOut);

        mvc.perform(patch("/comments/users/0?commentId=0")
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentSortDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.creator", is(commentSortDtoOut.getCreator())))
                .andExpect(jsonPath("$.eventAnnotation", is(commentSortDtoOut.getEventAnnotation())))
                .andExpect(jsonPath("$.text", is(commentSortDtoOut.getText())));
    }
}