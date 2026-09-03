package com.zeeshanproject.Airbnbapp.security;

import com.zeeshanproject.Airbnbapp.Exception.ResourceNotFoundException;
import com.zeeshanproject.Airbnbapp.dto.LoginDto;
import com.zeeshanproject.Airbnbapp.dto.SignUpRequestDto;
import com.zeeshanproject.Airbnbapp.dto.UserDto;
import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.entity.enums.Role;
import com.zeeshanproject.Airbnbapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto signUp(SignUpRequestDto signUpRequestDto){
        User user = userRepository
                .findByEmail(signUpRequestDto.getEmail())
                .orElse(null);

        if (user != null) {
            throw new RuntimeException(
                    "User is already present with the same email id"
            );
        }
        User newUser = modelMapper.map(signUpRequestDto, User.class);
        Role selectedRole = signUpRequestDto.getRole();
        if (selectedRole == null) {
            selectedRole = Role.GUEST;
        }
        newUser.setRoles(Set.of(selectedRole));
        newUser.setPassword(
                passwordEncoder.encode(signUpRequestDto.getPassword())
        );
        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword()));
        User user = (User) authentication.getPrincipal();

        String arr[] = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshTokens(user);
        return arr;
    }

    public String RefreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
