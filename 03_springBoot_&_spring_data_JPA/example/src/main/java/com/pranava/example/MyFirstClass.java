package com.pranava.example;


public class MyFirstClass {

    private String myVar;

    public MyFirstClass(String myVar) {
        this.myVar = myVar;
    }

    public String SayHello(){
        return "Hello Spring JPA" + myVar;
    }
}
