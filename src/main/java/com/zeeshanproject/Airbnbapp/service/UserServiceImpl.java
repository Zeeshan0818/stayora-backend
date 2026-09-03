package com.zeeshanproject.Airbnbapp.service;

import com.zeeshanproject.Airbnbapp.Exception.ResourceNotFoundException;
import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public User getUserbyId(Long id) {
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with Id: "+id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
