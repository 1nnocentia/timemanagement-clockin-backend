package com.clockin.clockin.service;

import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * This method loads user details by username OR email.
     *
     * @param usernameOrEmail
     * @return UserDetails object
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // search by username
        User user = userRepository.findByUsername(usernameOrEmail);

        // if not found by username, search by email
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        // email and username not valid, throw exception
        if (user == null) {
            throw new UsernameNotFoundException("No user found with this username or email: " + usernameOrEmail);
        }

        // return UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}