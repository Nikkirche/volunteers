package com.volunteer.home.controller;

import com.volunteer.home.entity.User;
import com.volunteer.home.repository.MyRoleRepository;
import com.volunteer.home.repository.MyUserRepository;
import com.volunteer.home.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * Created by Алексей on 16.02.2017.
 */
@Controller
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    MyUserRepository myUserRepository;

    @Autowired
    MyRoleRepository myRoleRepository;

    @Autowired
    SecurityService securityService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(User user) {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }

        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setConfirmPassword(user.getPassword());
        logger.debug(String.format("User created %s", user.toString()));
        user.setRole(myRoleRepository.findOne(2l));//ROLE_USER
        myUserRepository.save(user);
        securityService.autologin(user.getEmail(), password);
        return "redirect:/result";
    }

    @RequestMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", myUserRepository.findAll());
        return "users";
    }
}
