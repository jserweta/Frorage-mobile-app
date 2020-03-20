package com.frorage.server.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SwaggerController {

    @GetMapping("/docs")
    public String redirect(){
        return "redirect:/swagger-ui.html";
    }
}
