package com.pranava.example.school;

import com.pranava.example.student.Student;
import com.pranava.example.student.StudentDto;
import com.pranava.example.student.StudentMapper;
import com.pranava.example.student.StudentResponseDto;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SchoolMapperTest {
   private StudentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StudentMapper();
    }

    @Test
   public void shouldMapStudentDtoToStudent(){
        StudentDto dto = new StudentDto("John", "Doe", "john@email.com", 1);

        Student student = mapper.toStudent(dto);

        assertEquals(dto.email(), student.getEmail());
        assertEquals(dto.firstName(),student.getFirstName());
        assertEquals(dto.lastName(),student.getLastName());
        assertNotNull(student.getSchool());
        assertEquals(dto.schoolId(),student.getSchool().getId());
   }

   @Test
   public void should_throw_null_pointer_exception_when_studentDto_is_null(){
        var exp = assertThrows(NullPointerException.class, ()->mapper.toStudent(null));
        assertEquals("This Student Dto Should not be null", exp.getMessage());

   }

   @Test
   public void shouldMapStudentToStudentResponseDto(){

        //Given
        Student student = new Student("john", "Doe", "john@email.com", 1);

        //when
       StudentResponseDto dto = mapper.toStudentResponseDto(student);

       //then
       assertEquals(dto.firstName(), student.getFirstName());
       assertEquals(dto.lastName(), student.getLastName());
       assertEquals(dto.email(), student.getEmail());

   }
}