package com.zeeshanproject.Airbnbapp.dto;

import com.zeeshanproject.Airbnbapp.entity.User;
import com.zeeshanproject.Airbnbapp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {

    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;

}
