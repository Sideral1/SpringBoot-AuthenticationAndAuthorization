package com.vyro.vyro_api.SecureEndPoints.AdminRoleEndpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/python")
@RequiredArgsConstructor
public class PythonLoigcController {

        @GetMapping
        public String test() {

                return "Success PYTHON";
        }

}
