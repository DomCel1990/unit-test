package com.example.unittest.service;

import com.example.unittest.entities.User;
import com.example.unittest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        return userRepository.save(user);
    }
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getById(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) return user.get();
        else return null;
    }
    public User update(User user, Long id){
        user.setId(id);
        return userRepository.save(user);
    }
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }




}
