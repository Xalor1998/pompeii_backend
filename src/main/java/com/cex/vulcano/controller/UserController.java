package com.cex.vulcano.controller;

import com.cex.vulcano.model.User;
import com.cex.vulcano.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("register");
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }
}
