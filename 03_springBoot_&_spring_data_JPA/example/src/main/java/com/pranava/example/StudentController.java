package com.pranava.example;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

   private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/students")
    public Student post(@RequestBody Student student){

        return repository.save(student);

    }

    @GetMapping("/students")
    public List<Student> findAllStudents(){

        return repository.findAll();
    }

    @GetMapping("/students/{student-id}")
    public Student findStudentById(@PathVariable("student-id") Integer id){

        return repository.findById(id).orElse(new Student());

    }

    @GetMapping("/students/search/{student-name}")
    public List<Student> findAllByFirstname(@PathVariable("student-name") String name){


        return repository.findAllByFirstnameContaining(name);
    }

    @DeleteMapping("/students/{student-id}")
    public  void delete(@PathVariable("student-id") Integer id){
        repository.deleteById(id);
    }


}
