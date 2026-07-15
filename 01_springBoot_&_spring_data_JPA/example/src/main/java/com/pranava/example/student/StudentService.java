package com.pranava.example.student;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repository;

    private final StudentMapper studentMapper;

    public StudentService(StudentRepository repository, StudentMapper studentMapper) {
        this.repository = repository;
        this.studentMapper = studentMapper;
    }

    public StudentResponseDto saveStudent(StudentDto dto){
        var student = studentMapper.toStudent(dto);
        var savedStudent = repository.save(student);

        return studentMapper.toStudentResponseDto(savedStudent);
    }

    public List<StudentResponseDto> findAllStudents(){

        return repository.findAll().stream().map(studentMapper::toStudentResponseDto).collect(Collectors.toUnmodifiableList());
    }

    public StudentResponseDto findStudentById(@PathVariable("student-id") Integer id){

        return repository.findById(id).map(studentMapper::toStudentResponseDto)
        .orElse(null);

    }

    public List<StudentResponseDto> findAllByFirstname(@PathVariable("student-name") String name){
        return repository.findAllByFirstnameContaining(name).stream().map(studentMapper::toStudentResponseDto).collect(Collectors.toUnmodifiableList());
    }

    public  void delete(@PathVariable("student-id") Integer id){
        repository.deleteById(id);
    }
}

