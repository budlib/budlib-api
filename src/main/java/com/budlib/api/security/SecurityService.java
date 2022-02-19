package com.budlib.api.security;

import java.util.ArrayList;
import java.util.List;

import com.budlib.api.model.Librarian;
import com.budlib.api.repository.LibrarianRepository;
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
        List<Librarian> allLibrarians = this.librarianRepository.findAll();
        List<Librarian> searchResults = new ArrayList<>();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getEmail() != null && eachLibrarian.getEmail().toLowerCase().contains(username)) {
                searchResults.add(eachLibrarian);
            }
        }
        if (searchResults.size() > 0) {

            return new User(username, searchResults.get(0).getPassword(),
                    new ArrayList<>());

        }

        else {
            return null;
        }
    }
}
