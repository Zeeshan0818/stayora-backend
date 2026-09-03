package com.zeeshanproject.Airbnbapp.dto;

import com.zeeshanproject.Airbnbapp.entity.enums.Role;
import lombok.Data;

@Data
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private Role role;

}
