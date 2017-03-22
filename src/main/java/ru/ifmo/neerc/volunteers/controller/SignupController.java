package ru.ifmo.neerc.volunteers.controller;

import org.thymeleaf.spring.support.Layout;
import ru.ifmo.neerc.volunteers.entity.Role;
import ru.ifmo.neerc.volunteers.entity.User;
import ru.ifmo.neerc.volunteers.form.UserForm;
import ru.ifmo.neerc.volunteers.repository.RoleRepository;
import ru.ifmo.neerc.volunteers.repository.UserRepository;
import ru.ifmo.neerc.volunteers.service.SecurityService;
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
@Layout("empty")
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SecurityService securityService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(@ModelAttribute("user") UserForm user) {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") UserForm userForm, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }
        User user=new User(userForm);
        logger.debug(String.format("User created %s", user.toString()));
        Role role=roleRepository.findOne(2l);//ROLE_USER
        user.setRole(role);
        role.getUsers().add(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        roleRepository.save(role);
        securityService.autologin(user.getEmail(), userForm.getPassword());
        return "redirect:/result";
    }

    @RequestMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
