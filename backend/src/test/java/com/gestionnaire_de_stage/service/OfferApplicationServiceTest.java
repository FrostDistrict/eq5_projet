package com.gestionnaire_de_stage.service;

import com.gestionnaire_de_stage.dto.UpdateStatusDTO;
import com.gestionnaire_de_stage.enums.Status;
import com.gestionnaire_de_stage.exception.*;
import com.gestionnaire_de_stage.model.Curriculum;
import com.gestionnaire_de_stage.model.Offer;
import com.gestionnaire_de_stage.model.OfferApplication;
import com.gestionnaire_de_stage.model.Student;
import com.gestionnaire_de_stage.repository.OfferApplicationRepository;
import com.gestionnaire_de_stage.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferApplicationServiceTest {
    @InjectMocks
    private OfferApplicationService offerApplicationService;
    @Mock
    private OfferService offerService;
    @Mock
    private StudentService studentService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private OfferApplicationRepository offerApplicationRepository;

    @Test
    void testCreate() throws Exception {
        OfferApplication dummyOfferApplication = getDummyOfferApp();
        Student dummyStudent = getDummyStudent();
        Offer dummyOffer = getDummyOffer();
        when(offerApplicationRepository.save(any())).thenReturn(dummyOfferApplication);
        when(studentService.getOneByID(any())).thenReturn(dummyStudent);
        when(offerService.findOfferById(any())).thenReturn(Optional.of(dummyOffer));

        OfferApplication actualOfferApplication = offerApplicationService.create(dummyOffer.getId(), dummyStudent.getId());

        assertThat(actualOfferApplication).isEqualTo(dummyOfferApplication);
        assertThat(actualOfferApplication.getStatus()).isEqualTo(Status.CV_ENVOYE);
    }

    @Test
    void testCreate_withStudentNonExistant() throws Exception {
        Offer dummyOffer = getDummyOffer();
        when(studentService.getOneByID(any())).thenThrow(IdDoesNotExistException.class);
        when(offerService.findOfferById(any())).thenReturn(Optional.of(getDummyOffer()));

        assertThrows(IdDoesNotExistException.class,
                () -> offerApplicationService.create(dummyOffer.getId(), 36L));
    }

    @Test
    void testCreate_withOfferNonExistant() throws Exception {
        Student dummyStudent = getDummyStudent();
        when(studentService.getOneByID(any())).thenReturn(dummyStudent);
        when(offerService.findOfferById(any())).thenReturn(Optional.empty());

        assertThrows(IdDoesNotExistException.class,
                () -> offerApplicationService.create(34L, dummyStudent.getId()));
    }

    @Test
    void testCreate_withIdOfferNull() {
        Student dummyStudent = getDummyStudent();

        assertThrows(IllegalArgumentException.class,
                () -> offerApplicationService.create(null, dummyStudent.getId()));
    }

    @Test
    void testCreate_withIdStudentNull() throws IdDoesNotExistException {
        Offer dummyOffer = getDummyOffer();
        when(studentService.getOneByID(any())).thenThrow(IllegalArgumentException.class);
        when(offerService.findOfferById(any())).thenReturn(Optional.of(dummyOffer));

        assertThrows(IllegalArgumentException.class,
                () -> offerApplicationService.create(dummyOffer.getId(), null));
    }

    @Test
    void testCreate_withCvNull() throws IdDoesNotExistException {
        Offer dummyOffer = getDummyOffer();
        Student dummyStudent = getDummyStudent();
        dummyStudent.setPrincipalCurriculum(null);
        when(studentService.getOneByID(any())).thenReturn(dummyStudent);
        when(offerService.findOfferById(any())).thenReturn(Optional.of(dummyOffer));

        assertThrows(StudentHasNoCurriculumException.class,
                () -> offerApplicationService.create(dummyOffer.getId(), dummyStudent.getId()));
    }

    @Test
    void testCreate_withStudentAlreadyApplied() throws Exception {
        Offer dummyOffer = getDummyOffer();
        Student dummyStudent = getDummyStudent();
        when(studentService.getOneByID(any())).thenReturn(dummyStudent);
        when(offerService.findOfferById(any())).thenReturn(Optional.of(dummyOffer));
        when(offerApplicationRepository.existsByOfferAndCurriculum(any(), any())).thenReturn(true);

        assertThrows(StudentAlreadyAppliedToOfferException.class,
                () -> offerApplicationService.create(dummyOffer.getId(), dummyStudent.getId()));
    }

    @Test
    void testGetAllByOfferCreatorEmail_withValidEntries() {
        List<OfferApplication> offerApplicationList = getDummyOfferAppList();
        String email = "americanm@email.com";
        when(offerApplicationRepository.getAllByOffer_CreatorEmail(any()))
                .thenReturn(offerApplicationList);

        List<OfferApplication> actualOfferAppList = offerApplicationService.getAllByOfferCreatorEmail(email);

        assertThat(actualOfferAppList.size()).isEqualTo(offerApplicationList.size());
        assertThat(actualOfferAppList).isEqualTo(offerApplicationList);
    }

    @Test
    void testGetAllByOfferStudentApplied() throws IdDoesNotExistException {
        List<OfferApplication> offerApplicationList = getDummyOfferAppList();
        Student dummyStudent = getDummyStudent();
        when(studentService.getOneByID(any())).thenReturn(dummyStudent);
        when(offerApplicationRepository.getAllByStatusAndCurriculum_StudentId(Status.EN_ATTENTE_REPONSE, dummyStudent.getId()))
                .thenReturn(offerApplicationList);

        List<OfferApplication> actualOfferAppList = offerApplicationService
                .getAllOffersStudentApplied(dummyStudent.getId());

        assertThat(actualOfferAppList.size()).isEqualTo(offerApplicationList.size());
        assertThat(actualOfferAppList).isEqualTo(offerApplicationList);
    }

    @Test
    void testGetAllByOfferStudentApplied_withIdInvalid() throws IdDoesNotExistException {
        Student dummyStudent = getDummyStudent();
        when(studentService.getOneByID(any())).thenReturn(null);

        assertThrows(IdDoesNotExistException.class,
                () -> offerApplicationService.getAllOffersStudentApplied(dummyStudent.getId()));
    }

    @Test
    void testGetAllByOfferCreatorEmail_withNullEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> offerApplicationService.getAllByOfferCreatorEmail(null));
    }

    @Test
    public void testUpdateStatus_withTrue() throws IdDoesNotExistException {
        OfferApplication dummyOfferApplication = getDummyOfferApp();
        UpdateStatusDTO updateStatusDTO = new UpdateStatusDTO(dummyOfferApplication.getId(), true);
        when(offerApplicationRepository.getById(any())).thenReturn(dummyOfferApplication);
        when(offerApplicationRepository.save(any())).thenReturn(dummyOfferApplication);

        boolean isAccepted =  offerApplicationService.updateStatus(updateStatusDTO);

        assertThat(isAccepted).isTrue();
    }

    @Test
    public void testUpdateStatus_withFalse() throws IdDoesNotExistException {
        OfferApplication dummyOfferApplication = getDummyOfferApp();
        UpdateStatusDTO updateStatusDTO = new UpdateStatusDTO(dummyOfferApplication.getId(), false);
        when(offerApplicationRepository.getById(any())).thenReturn(dummyOfferApplication);
        when(offerApplicationRepository.save(any())).thenReturn(dummyOfferApplication);

        boolean isAccepted = offerApplicationService.updateStatus(updateStatusDTO);

        assertThat(isAccepted).isFalse();
    }

    @Test
    void testSetInterviewDate_withValidIDs() throws Exception {
        OfferApplication offerApplication = getDummyOfferApp();
        when(offerApplicationRepository.existsById(any())).thenReturn(true);
        when(offerApplicationRepository.getById(any())).thenReturn(offerApplication);
        offerApplication.setInterviewDate(LocalDateTime.now());
        when(offerApplicationRepository.save(any())).thenReturn(offerApplication);

        OfferApplication actual = offerApplicationService.setInterviewDate(offerApplication.getId(), LocalDateTime.now().plusDays(1));

        assertThat(actual).isEqualTo(offerApplication);
    }

    @Test
    void testSetInterviewDate_withNullIDs() {
        assertThrows(IllegalArgumentException.class,
                () -> offerApplicationService.setInterviewDate(null, null));
    }

    @Test
    void testSetInterviewDate_withOfferAppIdNotExist() {
        when(offerApplicationRepository.existsById(any())).thenReturn(false);

        assertThrows(IdDoesNotExistException.class,
                () -> offerApplicationService.setInterviewDate(5L, LocalDateTime.now()));
    }

    @Test
    void testSetInterviewDate_withDateInvalid() {
        OfferApplication offerApplication = getDummyOfferApp();
        when(offerApplicationRepository.existsById(any())).thenReturn(true);

        assertThrows(DateNotValidException.class,
                () -> offerApplicationService.setInterviewDate(
                        offerApplication.getId(),
                        LocalDateTime.now().minusDays(5)));
    }

    @Test
    void testGetAllByOfferStatusAndStudentID_withValidEntries() {
        List<OfferApplication> offerApplicationList = getDummyOfferAppList();
        when(offerApplicationRepository.getAllByStatusAndCurriculum_StudentId(any(), any()))
                .thenReturn(offerApplicationList);

        List<OfferApplication> actualList = offerApplicationService.getAllByOfferStatusAndStudentID(Status.CV_ENVOYE, 1L);

        assertThat(actualList.size()).isGreaterThan(1);
        assertThat(actualList).isEqualTo(offerApplicationList);
    }

    @Test
    void testGetAllByOfferStatusAndStudentID_withNullEntries() {
        assertThrows(IllegalArgumentException.class,
                () -> offerApplicationService.getAllByOfferStatusAndStudentID(null, null));
    }

    private OfferApplication getDummyOfferApp() {
        OfferApplication offerApplicationDTO = new OfferApplication();
        offerApplicationDTO.setOffer(getDummyOffer());
        offerApplicationDTO.setCurriculum(new Curriculum());
        offerApplicationDTO.setId(1L);
        offerApplicationDTO.setStatus(Status.CV_ENVOYE);

        return offerApplicationDTO;
    }

    private Offer getDummyOffer() {
        Offer dummyOffer = new Offer();
        dummyOffer.setDepartment("Un departement");
        dummyOffer.setAddress("ajsaodas");
        dummyOffer.setId(1L);
        dummyOffer.setDescription("oeinoiendw");
        dummyOffer.setSalary(10);
        dummyOffer.setTitle("oeinoiendw");
        return dummyOffer;
    }

    private Student getDummyStudent() {
        Student dummyStudent = new Student();
        dummyStudent.setId(1L);
        dummyStudent.setLastName("Candle");
        dummyStudent.setFirstName("Tea");
        dummyStudent.setEmail("cant@outlook.com");
        dummyStudent.setPassword("cantPass");
        dummyStudent.setDepartment("info");
        dummyStudent.setMatricule("4673943");
        dummyStudent.setPrincipalCurriculum(new Curriculum());
        return dummyStudent;
    }

    private UpdateStatusDTO getDummuyUpdateStatusDTO(){
      return new UpdateStatusDTO(1L, true);
    }

    private Curriculum getDummyCurriculum() {
        Curriculum dummyCurriculum = new Curriculum();
        dummyCurriculum.setId(1L);
        dummyCurriculum.setName("Curriculum");
        dummyCurriculum.setStudent(getDummyStudent());
        return dummyCurriculum;
    }

    private List<OfferApplication> getDummyOfferAppList() {
        List<OfferApplication> offerApplicationList = new ArrayList<>();
        OfferApplication dummyOfferApplication = new OfferApplication();
        dummyOfferApplication.setOffer(getDummyOffer());
        dummyOfferApplication.setCurriculum(new Curriculum());
        dummyOfferApplication.setId(1L);
        dummyOfferApplication.setStatus(Status.CV_ENVOYE);
        offerApplicationList.add(dummyOfferApplication);

        dummyOfferApplication.setId(2L);
        offerApplicationList.add(dummyOfferApplication);

        dummyOfferApplication.setId(3L);
        offerApplicationList.add(dummyOfferApplication);

        return offerApplicationList;
    }
}
