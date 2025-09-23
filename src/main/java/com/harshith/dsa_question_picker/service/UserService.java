package com.harshith.dsa_question_picker.service;

import com.harshith.dsa_question_picker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails loadUserByProviderId(String providerId) throws UsernameNotFoundException {
        return loadUserByUsername(providerId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByProviderId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
