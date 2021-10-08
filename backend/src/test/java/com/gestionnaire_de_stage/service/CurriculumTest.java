package com.gestionnaire_de_stage.service;

import com.gestionnaire_de_stage.model.Curriculum;
import com.gestionnaire_de_stage.model.Student;
import com.gestionnaire_de_stage.repository.CurriculumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurriculumTest {

    @InjectMocks
    private CurriculumService curriculumService;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private StudentService studentService;

    @Test
    public void testConvertMultipartFileToCurriculum_WithValidData() throws IOException {
        Student student = new Student();
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain","some xml".getBytes());
        when(studentService.getOneByID(any())).thenReturn(Optional.of(student));

        Optional<Curriculum> actual = curriculumService.convertMultipartFileToCurriculum(file, student.getId());

        assertThat(actual).isNotNull();
    }

    @Test
    public void testConvertMultipartFileToCurriculum_WithInvalidData() throws IOException {
        Student student = new Student();
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain","some xml".getBytes());
        when(studentService.getOneByID(any())).thenReturn(Optional.empty());

        Optional<Curriculum> actual = curriculumService.convertMultipartFileToCurriculum(file, student.getId());

        assertThat(actual).isEqualTo(Optional.empty());
    }
}