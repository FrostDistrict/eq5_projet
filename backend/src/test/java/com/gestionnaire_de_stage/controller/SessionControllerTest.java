package com.gestionnaire_de_stage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestionnaire_de_stage.enums.TypeSession;
import com.gestionnaire_de_stage.exception.SessionAlreadyExistException;
import com.gestionnaire_de_stage.model.Session;
import com.gestionnaire_de_stage.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(SessionController.class)
public class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    private final ObjectMapper MAPPER = new ObjectMapper();


    @Test
    public void testCreateSession() throws Exception {
        MAPPER.registerModule(new JavaTimeModule());
        LocalDate dateDebut = LocalDate.now().minusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(1);
        Session session = new Session(1L, TypeSession.HIVER, dateDebut, dateFin);
        when(sessionService.createSession(any())).thenReturn(session);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(session)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Session créée avec succès!");
    }


    @Test
    public void testCreateSession_whenSessionAlreadyExist() throws Exception {
        MAPPER.registerModule(new JavaTimeModule());
        LocalDate dateDebut = LocalDate.now().minusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(1);
        Session session = new Session(1L, TypeSession.HIVER, dateDebut, dateFin);
        when(sessionService.createSession(any())).thenThrow(new SessionAlreadyExistException("Une Session existe déjà!"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(session)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Une Session existe déjà!");
    }

    @Test
    public void testCreateSession_withTypeSessionNull() throws Exception {
        MAPPER.registerModule(new JavaTimeModule());
        LocalDate dateDebut = LocalDate.now().minusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(1);
        Session session = new Session(1L, null, dateDebut, dateFin);
        when(sessionService.createSession(any())).thenThrow(new IllegalArgumentException("Le type de session est obligatoire"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(session)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Le type de session est obligatoire");
    }

    @Test
    public void testCreateSession_withDateDebutNull() throws Exception {
        MAPPER.registerModule(new JavaTimeModule());
        LocalDate dateFin = LocalDate.now().plusDays(1);
        Session session = new Session(1L, TypeSession.ETE, null, dateFin);
        when(sessionService.createSession(any())).thenThrow(new IllegalArgumentException("La date de début est obligatoire"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(session)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("La date de début est obligatoire");
    }

    @Test
    public void testCreateSession_withDateFinNull() throws Exception {
        MAPPER.registerModule(new JavaTimeModule());
        LocalDate dateDebut = LocalDate.now().plusDays(1);
        Session session = new Session(1L, TypeSession.ETE, dateDebut, null);
        when(sessionService.createSession(any())).thenThrow(new IllegalArgumentException("La date de fin est obligatoire"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(session)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("La date de fin est obligatoire");
    }
}