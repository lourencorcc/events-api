package com.example.events_api.service;


import com.example.events_api.entity.UserInfo;
import com.example.events_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserInfoService(UserInfoRepository repository, @Lazy PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    // Method to load user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database by email (username)
        Optional<UserInfo> userInfo = repository.findByEmail(username);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Convert UserInfo to UserDetails (UserInfoDetails)
        // UserInfo user = userInfo.get();
        // return new User(user.getEmail(), user.getPassword(), user.getRoles());
        return new UserInfoDetails(userInfo.get());
    }

    // Add any additional methods for registering or managing users
    public ResponseEntity<String> addUser(UserInfo userInfo) {
        // Encrypt password before saving
        System.out.println("Trying to add user: " + userInfo);
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Added user successfully");
    }

    public ResponseEntity<String> removeUserByEmail(String email) {
        Optional<UserInfo> userOpt = repository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        repository.delete(userOpt.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body("Removed user successfully");
    }

}
