package com.zeeshanproject.Airbnbapp.advice;

import jakarta.servlet.http.HttpServlet;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ApiError {

    private HttpStatus status;
    private String message;
    private List<String> Suberrors;
}
