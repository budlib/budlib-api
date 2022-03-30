package com.budlib.api.service;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

@Service
public class LibrarianService implements UserDetailsService {
    @Autowired
    private LibrarianRepository librarianRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        List<Librarian> allLibrarians = this.librarianRepository.findAll();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getEmail().equalsIgnoreCase(username)) {
                // return new User(username, eachLibrarian.getPassword(),
                // this.getAuthorities(eachLibrarian));

                UserDetails user = User.withUsername(username)
                        .password(eachLibrarian.getPassword())
                        .roles(eachLibrarian.getRole().toString())
                        // .authorities(this.getAuthorities(eachLibrarian)).build();
                        .build();

                return user;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    private Collection<GrantedAuthority> getAuthorities(Librarian l) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(l.getRole().toString()));

        return authorities;
    }

    public Librarian getLibrarianByEmail(String email) {
        List<Librarian> allLibrarians = this.librarianRepository.findAll();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getEmail().equalsIgnoreCase(email)) {
                return eachLibrarian;
            }
        }

        return null;
    }
}
