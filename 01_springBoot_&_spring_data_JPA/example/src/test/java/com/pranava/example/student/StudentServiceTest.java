package com.pranava.example.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    //Which service we want to test

    @InjectMocks
    private StudentService studentService;

    //declare the dependencies

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_successfully_save_a_student(){
        //given
        StudentDto dto = new StudentDto("John", "Doe", "john@email.com", 1);

        Student student = new Student("John", "Doe", "john@email.com", 1);

        Student savedStudent = new Student("John", "Doe", "john@email.com", 1);

        savedStudent.setId(1);

        StudentResponseDto response = new StudentResponseDto(
                "John",
                "Doe",
                "john@email.com"
        );


        //Mock the calls
        when(studentMapper.toStudent(dto)).thenReturn(student);

        when(studentRepository.save(student)).thenReturn(savedStudent);

        when(studentMapper.toStudentResponseDto(savedStudent))
                .thenReturn(response);

        //when
        StudentResponseDto responseDto = studentService.saveStudent(dto);

        //then
        assertEquals(dto.firstName(), responseDto.firstName());
        assertEquals(dto.lastName(), responseDto.lastName());
        assertEquals(dto.email(), responseDto.email());

        verify(studentMapper, times(1)).toStudent(dto);
        verify(studentRepository, times(1)).save(student);
        verify(studentMapper, times(1)).toStudentResponseDto(savedStudent);


    }


    @Test
    public void should_return_all_students(){
        //Given
        List<Student> students = new ArrayList<>();
        students.add(new Student("John", "Doe", "john@email.com", 1));

        //Mock the calls
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toStudentResponseDto(any(Student.class)))
                .thenReturn(new StudentResponseDto("John", "Doe", "john@email.com"));

        List<StudentResponseDto> responseDtos = studentService.findAllStudents();

        //Then
        assertEquals(students.size(), responseDtos.size());

        verify(studentRepository, times(1)).findAll();

    }
}