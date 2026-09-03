package com.zeeshanproject.Airbnbapp.dto;

import com.zeeshanproject.Airbnbapp.entity.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles;

}
