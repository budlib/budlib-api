package com.budlib.api.security;

import java.util.ArrayList;

import com.budlib.api.LibrarianRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService implements UserDetailsService {
    @Autowired
    LibrarianRepository librarianRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        if (librarianRepository.findByuserName(username).size() > 0) {

            return new User(username, librarianRepository.findByuserName(username).get(0).getPassword(),
                    new ArrayList<>());

        } else {
            return null;
        }

    }

}
