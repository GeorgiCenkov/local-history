package com.example.localhistory.user.dto.response;

import jakarta.persistence.Column;

public class StudentDTO extends  UserDTO{
    private Integer level;
    private Integer points;
    private Integer pointsRequired;
}
