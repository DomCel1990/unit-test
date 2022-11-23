package com.example.unittest.controllers;

import com.example.unittest.entities.User;
import com.example.unittest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }
    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
       return userService.getById(id);
    }
    @PutMapping("/{id}")
    public User update(@RequestBody User user, @PathVariable Long id){
        return userService.update(user,id);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
