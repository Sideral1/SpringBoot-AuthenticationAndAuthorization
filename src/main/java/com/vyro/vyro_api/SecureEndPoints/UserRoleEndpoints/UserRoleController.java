package com.vyro.vyro_api.SecureEndPoints.UserRoleEndpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserRoleController {

    @GetMapping
    public String test() {
        return "Success USER";
    }
}
