package com.gestionnaire_de_stage.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionnaire_de_stage.dto.StudentCurriculumsDTO;
import com.gestionnaire_de_stage.dto.ValidationCurriculum;
import com.gestionnaire_de_stage.exception.CurriculumAlreadyTreatedException;
import com.gestionnaire_de_stage.exception.CurriculumUsedException;
import com.gestionnaire_de_stage.exception.IdDoesNotExistException;
import com.gestionnaire_de_stage.model.Curriculum;
import com.gestionnaire_de_stage.model.Student;
import com.gestionnaire_de_stage.service.CurriculumService;
import com.gestionnaire_de_stage.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(CurriculumController.class)
public class CurriculumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurriculumService curriculumService;

    @MockBean
    private StudentService studentService;

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void uploadCurriculumTest_withValidEntries() throws Exception {
        Long studentId = 1L;
        Curriculum dummyCurriculum = getDummyCurriculum();
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.convertMultipartFileToCurriculum(any(), any())).thenReturn(dummyCurriculum);
        when(curriculumService.create(any())).thenReturn(dummyCurriculum);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).contains("File Uploaded Successfully");
    }

    @Test
    public void uploadCurriculumTest_withInvalidStudentID() throws Exception {
        Long studentId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.convertMultipartFileToCurriculum(any(), any())).thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Invalid Student ID");
    }

    @Test
    public void uploadCurriculumTest_fileThrowsIOException() throws Exception {
        Long studentId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.convertMultipartFileToCurriculum(any(), any())).thenThrow(IOException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("IO Error: check file integrity!");
    }

    @Test
    public void uploadCurriculumTest_studentIdThrowsIdDoesNotExistException() throws Exception {
        Long studentId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.create(any())).thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Invalid Student ID");
    }

    @Test
    public void uploadCurriculumTest_idStudentNull() throws Exception {
        Long studentId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.create(any())).thenThrow(new IllegalArgumentException("L'étudiant est null"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("L'étudiant est null");
    }

    @Test
    public void uploadCurriculumTest_idCurriculumNull() throws Exception {
        Long studentId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(curriculumService.create(any())).thenThrow(new IllegalArgumentException("L'étudiant est null"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/curriculum/upload")
                                .file(file)
                                .param("id", MAPPER.writeValueAsString(studentId)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("L'étudiant est null");
    }


    @Test
    public void testGetAllCurriculumNotValidatedYet_withValidList() throws Exception {
        List<Curriculum> dummyList = Arrays.asList(new Curriculum(), new Curriculum(), new Curriculum());
        when(curriculumService.findAllCurriculumNotValidatedYet()).thenReturn(dummyList);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/invalid/students")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        List<Curriculum> returnedList = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < dummyList.size(); i++) {
            assertThat(returnedList.get(i).getIsValid()).isEqualTo(dummyList.get(i).getIsValid());
            assertThat(returnedList.get(i).getName()).isEqualTo(dummyList.get(i).getName());
            assertThat(returnedList.get(i).getStudent()).isEqualTo(dummyList.get(i).getStudent());
            assertThat(returnedList.get(i).getId()).isEqualTo(dummyList.get(i).getId());
            assertThat(returnedList.get(i).getData()).isEqualTo(dummyList.get(i).getData());
            assertThat(returnedList.get(i).getType()).isEqualTo(dummyList.get(i).getType());
        }

    }

    @Test
    public void testGetAllCurriculumNotValidatedYet_withEmptyList() throws Exception {
        when(curriculumService.findAllCurriculumNotValidatedYet()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/invalid/students")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        List<Curriculum> actualCurriculumList = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualCurriculumList).isEmpty();

    }

    @Test
    public void testGetAllCurriculumValidated_withValidList() throws Exception {
        Curriculum cv1 = new Curriculum();
        Curriculum cv2 = new Curriculum();
        Curriculum cv3 = new Curriculum();

        List<Curriculum> dummyList = Stream.of(cv1, cv2, cv3).peek(c -> c.setIsValid(true)).collect(Collectors.toList());

        when(curriculumService.findAllCurriculumValidated()).thenReturn(dummyList);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/valid/students")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        List<Curriculum> returnedList = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(returnedList.size()).isEqualTo(dummyList.size());
        for (int i = 0; i < dummyList.size(); i++) {
            assertThat(returnedList.get(i).getIsValid()).isEqualTo(dummyList.get(i).getIsValid());
            assertThat(returnedList.get(i).getName()).isEqualTo(dummyList.get(i).getName());
            assertThat(returnedList.get(i).getStudent()).isEqualTo(dummyList.get(i).getStudent());
            assertThat(returnedList.get(i).getId()).isEqualTo(dummyList.get(i).getId());
            assertThat(returnedList.get(i).getData()).isEqualTo(dummyList.get(i).getData());
            assertThat(returnedList.get(i).getType()).isEqualTo(dummyList.get(i).getType());
        }
    }

    @Test
    public void testGetAllCurriculumValidated_withEmptyList() throws Exception {
        when(curriculumService.findAllCurriculumValidated()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/valid/students")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        List<Curriculum> actualCurriculumList = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualCurriculumList).isEmpty();

    }

    @Test
    public void testValidate() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, true);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Curriculum validé!");
    }

    @Test
    public void testValidate_whenCvNonExistent() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, true);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Curriculum non existant!");
    }

    @Test
    public void testValidate_whenCurriculumAlreadyTreated() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, true);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenThrow(CurriculumAlreadyTreatedException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Curriculum déjà traité!");
    }

    @Test
    public void testValidate_withIdCurriculumNull() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(null, true);
        when(curriculumService.validate(any(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Le id du curriculum ne peut pas être null"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Le id du curriculum ne peut pas être null");
    }

    @Test
    public void testReject() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, false);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Curriculum rejeté!");
    }

    @Test
    public void testReject_whenCvNonExistent() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, false);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Curriculum non existant!");
    }

    @Test
    public void testReject_whenCurriculumAlreadyTreated() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(1L, false);
        when(curriculumService.validate(anyLong(), anyBoolean())).thenThrow(CurriculumAlreadyTreatedException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Curriculum déjà traité!");
    }

    @Test
    public void testReject_withIdCurriculumNull() throws Exception {
        ValidationCurriculum validationCurriculum = new ValidationCurriculum(null, false);
        when(curriculumService.validate(any(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Le id du curriculum ne peut pas être null"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/curriculum/validate")
                                .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(validationCurriculum)))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Le id du curriculum ne peut pas être null");
    }

    @Test
    public void testGetCurriculumById() throws Exception {
        String text = "some xml";
        Curriculum dummyCurriculum = getDummyCurriculum();
        dummyCurriculum.setData(text.getBytes());

        when(curriculumService.findOneById(anyLong())).thenReturn(dummyCurriculum);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/download/{id}", dummyCurriculum.getId())
                                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(text);
        assertThat(response.getHeader(HttpHeaders.CONTENT_DISPOSITION)).contains(dummyCurriculum.getName());
    }

    @Test
    public void testGetCurriculumById_withIdNull() throws Exception {
        Long id = null;
        when(curriculumService.findOneById(any()))
                .thenThrow(new IllegalArgumentException("Le id du curriculum ne peut pas être null"));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/download/{idCurriculum}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Le id du curriculum ne peut pas être null");
    }

    @Test
    public void testGetCurriculumById_withCurriculumNonExistant() throws Exception {
        Long id = 34L;
        when(curriculumService.findOneById(any())).thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/curriculum/download/{idCurriculum}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Curriculum non existant!");
    }

    @Test
    public void testAllCurriculumsByStudentAsStudentCurriculumsDTO_withValidEntries() throws Exception {
        Student student = getDummyStudent();
        StudentCurriculumsDTO studentCurriculumsDTO = getDummyStudentCurriculumsDTO();
        when(studentService.getOneByID(any()))
                .thenReturn(student);
        when(curriculumService.allCurriculumsByStudentAsStudentCurriculumsDTO(any()))
                .thenReturn(studentCurriculumsDTO);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/curriculum/all_student/{studentID}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        final StudentCurriculumsDTO actual = MAPPER.readValue(response.getContentAsString(), StudentCurriculumsDTO.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getPrincipal().getId()).isEqualTo(studentCurriculumsDTO.getPrincipal().getId());
    }

    @Test
    public void testAllCurriculumsByStudentAsStudentCurriculumsDTO_withNullStudent() throws Exception {
        Student student = getDummyStudent();
        when(studentService.getOneByID(any()))
                .thenReturn(student);
        when(curriculumService.allCurriculumsByStudentAsStudentCurriculumsDTO(any()))
                .thenThrow(new IllegalArgumentException("L'etudiant ne peut pas être null"));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/curriculum/all_student/{studentID}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("L'etudiant ne peut pas être null");
    }

    @Test
    public void testAllCurriculumsByStudentAsStudentCurriculumsDTO_withIdNotFound() throws Exception {
        Student student = getDummyStudent();
        when(studentService.getOneByID(any()))
                .thenThrow(IdDoesNotExistException.class);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/curriculum/all_student/{studentID}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Invalid Student ID");
    }

    @Test
    public void testDeleteOneById() throws Exception {
        Curriculum curriculum = getDummyCurriculum();
        doNothing().when(curriculumService).deleteOneById(any());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/curriculum/delete/{curriculumId}", curriculum.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Curriculum éffacé");
    }

    @Test
    void testDeleteOneById_throwsIllegalArg() throws Exception {
        Curriculum curriculum = getDummyCurriculum();
        doThrow(new IllegalArgumentException("No null values!"))
                .when(curriculumService)
                .deleteOneById(any());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/curriculum/delete/{curriculumId}", curriculum.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("No null values!");
    }

    @Test
    void testDeleteOneById_throwsCurriculumUsedException() throws Exception {
        Curriculum curriculum = getDummyCurriculum();
        doThrow(new CurriculumUsedException("Impossible de supprimer."))
                .when(curriculumService)
                .deleteOneById(any());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/curriculum/delete/{curriculumId}", curriculum.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Impossible de supprimer.");
    }

    private StudentCurriculumsDTO getDummyStudentCurriculumsDTO() {
        return new StudentCurriculumsDTO(getDummyCurriculum(), getDummyCurriculumList());
    }

    private List<Curriculum> getDummyCurriculumList() {
        Curriculum dummyCurriculum1 = getDummyCurriculum();
        Curriculum dummyCurriculum2 = getDummyCurriculum();
        Curriculum dummyCurriculum3 = getDummyCurriculum();

        return Arrays.asList(dummyCurriculum1, dummyCurriculum2, dummyCurriculum3);
    }

    private Student getDummyStudent() {
        Student dummyStudent = new Student();
        dummyStudent.setId(1L);
        dummyStudent.setLastName("Winter");
        dummyStudent.setFirstName("Summer");
        dummyStudent.setEmail("cant@outlook.com");
        dummyStudent.setPassword("cantPass");
        dummyStudent.setDepartment("info");
        dummyStudent.setMatricule("4673943");
        return dummyStudent;
    }

    Curriculum getDummyCurriculum() {
        Curriculum curriculum = new Curriculum();
        curriculum.setName("myFileeee");
        curriculum.setType("pdf");
        curriculum.setId(1L);
        return curriculum;
    }
}