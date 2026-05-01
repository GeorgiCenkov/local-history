package com.example.localhistory.user.dto.response;

import lombok.Data;

// Response row for the global student leaderboard.
@Data
public class StudentLeaderboardDTO {
    private Integer rank;
    private Long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer level;
    private Integer points;
    private Integer pointsRequired;
    private Boolean currentUser;
}
