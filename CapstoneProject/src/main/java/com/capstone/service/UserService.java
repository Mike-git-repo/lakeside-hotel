package com.capstone.service;

import com.capstone.model.User;
import com.capstone.request.LoginRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    User findUserById(Long userId) throws Exception;

    public User signinHandler(@RequestBody LoginRequest loginRequest);

    boolean findByUsername(LoginRequest loginRequest);

    User saveUser(LoginRequest loginRequest);
}
