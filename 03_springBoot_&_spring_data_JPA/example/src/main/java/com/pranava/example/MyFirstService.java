package com.pranava.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class MyFirstService {

//    @Autowired
    private final MyFirstClass myFirstClass;

    @Value("${my.prop}")
    private String myCustomPropFromAnotherFile;

    public String getMyCustomPropFromAnotherFile() {
        return myCustomPropFromAnotherFile;
    }

    public MyFirstService(
            @Qualifier("bean1") MyFirstClass myFirstClass) {
        this.myFirstClass = myFirstClass;
    }

//    private Environment environment;
//
//    @Autowired
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }

    public String tellAStory(){

        return "The Dependency is saying " + myFirstClass.SayHello();

    }

//    public String getJavaVersion(){
//
//        return environment.getProperty("java.version");
//
//    }
//
//    public String readProp(){
//
//        return environment.getProperty("spring.application.name");
//
//    }


}
