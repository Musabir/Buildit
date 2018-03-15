package com.rentit.sales.web.controller;

import com.rentit.user.domain.model.User;
import com.rentit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    HttpSession session;

    @GetMapping("loginPage")
    public String loginPage() {
        if(session != null  &&  session.getAttribute("user") != null) {
            return "login";
        }
        return "redirect:/dashboard/catalog/form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("error", "Enter valid username and password!");
            return "login";
        }
        int role = userService.login(user).getRole();
        String email = userService.login(user).getEmail();
        String username = userService.login(user).getUsername();
        if(role <= 0) {
            model.addAttribute("error", "Username or password is incorrect!");
            return "login";
        }

        model.addAttribute("role", role);
        user.setRole(role);
        user.setUsername(username);
        user.setEmail(email);
        System.out.println("---------------------------><"+role+"");
        session.setAttribute("user", user);
        if(role==2)
        return "redirect:/dashboard/catalog/form";
        else
        return "redirect:/dashboard/allorders";

    }

    @GetMapping("logout")
    public String logout() {
        session.invalidate();
        return "login";
    }
}
