package com.example.localhistory.security.service;

import com.example.localhistory.security.model.AppUserDetails;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security calls this with whatever string was set as the JWT subject.
     * Since we store the user's ID in the token subject, we parse it back to Long
     * and look the user up by ID — no email involved.
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
            return new AppUserDetails(user);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id in token: " + userId);
        }
    }
}