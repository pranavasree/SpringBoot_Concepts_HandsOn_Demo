package com.pranava.example;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class FirstController {

    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String satHello(){
        return "Hello PRanav";
    }

    @PostMapping("/post")
    public String post(@RequestBody String message){

        return "request accepted" + message;
    }
}
