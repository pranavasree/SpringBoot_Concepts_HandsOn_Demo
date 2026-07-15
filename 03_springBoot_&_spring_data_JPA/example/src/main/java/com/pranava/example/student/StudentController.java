package com.pranava.example.student;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

   private final StudentService studentService;

    public StudentController(StudentService studentService) {

        this.studentService = studentService;
    }

    @PostMapping("/students")
    public StudentResponseDto post(@RequestBody StudentDto dto){

            return this.studentService.saveStudent(dto);

    }



    @GetMapping("/students")
    public List<StudentResponseDto> findAllStudents(){

        return studentService.findAllStudents();
    }

    @GetMapping("/students/{student-id}")
    public StudentResponseDto findStudentById(@PathVariable("student-id") Integer id){

        return studentService.findStudentById(id);

    }

    @GetMapping("/students/search/{student-name}")
    public List<StudentResponseDto> findAllByFirstname(@PathVariable("student-name") String name){


        return studentService.findAllByFirstname(name);
    }

    @DeleteMapping("/students/{student-id}")
    public  void delete(@PathVariable("student-id") Integer id){
        studentService.delete(id);
    }


}
