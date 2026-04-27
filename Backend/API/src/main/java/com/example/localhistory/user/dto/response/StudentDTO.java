package com.example.localhistory.user.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDTO extends  UserDTO{
    private Integer level;
    private Integer points;
    private Integer pointsRequired;
}
