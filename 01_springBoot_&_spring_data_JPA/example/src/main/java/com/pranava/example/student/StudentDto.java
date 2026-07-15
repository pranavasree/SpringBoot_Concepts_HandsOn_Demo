package com.pranava.example.student;

import jakarta.validation.constraints.NotEmpty;

public record StudentDto(

        @NotEmpty(message = "FirstName Should not be Empty")
        String firstName,

        @NotEmpty(message = "LastName Should not be Empty")
        String lastName,

        String email,

        Integer schoolId
) {


}
