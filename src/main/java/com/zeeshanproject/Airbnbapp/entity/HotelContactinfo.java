package com.zeeshanproject.Airbnbapp.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactinfo {

    private String address;
    private String phoneNumber;
    private String email;
    private String location;

}
