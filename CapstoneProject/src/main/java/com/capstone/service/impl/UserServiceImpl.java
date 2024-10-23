package com.capstone.service.impl;

import com.capstone.model.User;
import com.capstone.repository.UserRepository;
import com.capstone.request.LoginRequest;
import com.capstone.service.UserService;
import com.capstone.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> opt = userRepository.findById(userId);

        if (opt.isPresent()) {
            return opt.get();
        }
        throw new Exception("User not found with id" + userId);
    }

    public User signinHandler(@RequestBody LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null || !loginRequest.getPassword().equals(user.getPassword())) {
            return null;
        }

        return user;


    }

    @Override
    public boolean findByUsername(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.getUsername());
        if(user != null){
            return false;
        }

        return true;
    }

    @Override
    public User saveUser(LoginRequest loginRequest) {
        User user = new User();
        String email = loginRequest.getEmail();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        User savedUser = userRepository.save(user);
        return savedUser;
    }


}
