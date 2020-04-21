package com.richj.helloworld.RichHelloWorld;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: RichHelloWorld
 * Package: com.richj.helloworld.RichHelloWorld
 * User: richardjones
 * Date: 21/04/2020
 */
@RestController
public class HelloWorld {
    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }

}
