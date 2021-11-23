package com.gestionnaire_de_stage.service;

import com.gestionnaire_de_stage.dto.CurriculumDTO;
import com.gestionnaire_de_stage.dto.OfferDTO;
import com.gestionnaire_de_stage.dto.StudentCurriculumsDTO;
import com.gestionnaire_de_stage.exception.CurriculumAlreadyTreatedException;
import com.gestionnaire_de_stage.exception.IdDoesNotExistException;
import com.gestionnaire_de_stage.model.Curriculum;
import com.gestionnaire_de_stage.model.OfferApplication;
import com.gestionnaire_de_stage.model.Student;
import com.gestionnaire_de_stage.repository.CurriculumRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final StudentService studentService;
    private final OfferService offerService;

    public CurriculumService(
            CurriculumRepository curriculumRepository,
            StudentService studentService,
            OfferService offerService
    ) {
        this.curriculumRepository = curriculumRepository;
        this.studentService = studentService;
        this.offerService = offerService;
    }

    public Curriculum convertMultipartFileToCurriculum(MultipartFile file, Long studentId)
            throws IOException, IdDoesNotExistException, IllegalArgumentException {
        Assert.isTrue(file != null, "Fichier est null");
        Assert.isTrue(studentId != null, "StudentId est null");

        Student student = studentService.getOneByID(studentId);

        //noinspection ConstantConditions
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        return new Curriculum(
                fileName,
                file.getContentType(),
                file.getBytes(),
                student
        );
    }

    public Curriculum create(Curriculum curriculum) throws IllegalArgumentException, IdDoesNotExistException {
        Assert.isTrue(curriculum != null, "Curriculum est null");
        return curriculumRepository.save(curriculum);
    }

    public Curriculum getOneByID(Long aLong) throws IdDoesNotExistException {
        Assert.isTrue(aLong != null, "ID est null");
        if (!curriculumRepository.existsById(aLong))
            throw new IdDoesNotExistException();
        return curriculumRepository.getById(aLong);
    }

    public List<CurriculumDTO> mapToCurriculumDTOList(List<OfferApplication> offerApplicationList) {
        List<CurriculumDTO> curriculumDTOList = new ArrayList<>();
        Student student;
        CurriculumDTO curriculumDTO = new CurriculumDTO();
        Curriculum curriculum;
        OfferDTO offerDto;
        for (OfferApplication offerApp : offerApplicationList) {
            curriculum = offerApp.getCurriculum();
            student = curriculum.getStudent();
            offerDto = offerService.mapToOfferDTO(offerApp.getOffer());

            curriculumDTO.setFirstName(student.getFirstName());
            curriculumDTO.setLastName(student.getLastName());
            curriculumDTO.setFileName(curriculum.getName());
            curriculumDTO.setFile(curriculum.getData());
            curriculumDTO.setOfferDTO(offerDto);
            curriculumDTOList.add(curriculumDTO);
        }
        return curriculumDTOList;
    }

    public List<Curriculum> findAllByStudent(Student student) throws IllegalArgumentException {
        Assert.notNull(student, "L'etudiant ne peut pas être null");

        return curriculumRepository.findAllByStudent(student);
    }

    public StudentCurriculumsDTO allCurriculumsByStudentAsStudentCurriculumsDTO(Student student) throws IllegalArgumentException {
        List<Curriculum> curriculumListByStudent = findAllByStudent(student);
        Curriculum principal;

        int index = curriculumListByStudent.indexOf(student.getPrincipalCurriculum());
        principal = curriculumListByStudent.contains(student.getPrincipalCurriculum())
                ? curriculumListByStudent.get(index) : null;

        return new StudentCurriculumsDTO(principal, curriculumListByStudent);
    }

    public boolean validate(Long idCurriculum, boolean valid) throws
            IdDoesNotExistException, CurriculumAlreadyTreatedException, IllegalArgumentException {
        Assert.isTrue(idCurriculum != null, "Le id du curriculum ne peut pas être null");

        Optional<Curriculum> curriculumOptional = curriculumRepository.findById(idCurriculum);

        if (curriculumOptional.isEmpty())
            throw new IdDoesNotExistException();
        if (curriculumOptional.get().getIsValid() != null)
            throw new CurriculumAlreadyTreatedException("Ce curriculum a déjà été traité");

        Curriculum curriculum = curriculumOptional.get();
        curriculum.setIsValid(valid);
        curriculumRepository.save(curriculum);
        return true;
    }

    public Curriculum findOneById(Long idCurriculum) throws IllegalArgumentException, IdDoesNotExistException {
        Assert.isTrue(idCurriculum != null, "Le id du curriculum ne peut pas être null");
        Optional<Curriculum> byId = curriculumRepository.findById(idCurriculum);
        if (byId.isEmpty())
            throw new IdDoesNotExistException();

        return byId.get();
    }

    public List<Curriculum> findAllByStudentId(Long id) throws IdDoesNotExistException {
        Student student = studentService.getOneByID(id);
        return findAllByStudent(student);
    }
}
