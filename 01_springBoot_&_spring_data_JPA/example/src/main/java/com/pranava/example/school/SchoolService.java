package com.pranava.example.school;


import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolService {

    private final SchoolMapper schoolMapper;

    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolMapper schoolMapper, SchoolRepository schoolRepository) {
        this.schoolMapper = schoolMapper;
        this.schoolRepository = schoolRepository;
    }

    public SchoolDto create(@RequestBody SchoolDto dto){
        var school = schoolMapper.toSchool(dto);
        var savedSchool = schoolRepository.save(school);
        return dto;
    }

    public List<SchoolDto> findAll(){

        return schoolRepository.findAll().stream().map(schoolMapper::toSchoolDto).collect(Collectors.toUnmodifiableList());
    }
}
