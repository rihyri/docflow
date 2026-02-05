package com.dockflow.backend.controller.main;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
            System.out.println("로그인 사용자: " + userDetails.getUsername());
        }

        return "main/main";
    }
}
