package com.example.localhistory.user;

import com.example.localhistory.user.dto.response.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST controller for user lookup helpers used by authenticated app screens.
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/search?search=value — find students a teacher can assign homework to.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<UserDTO>> searchStudents(@RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(userService.searchStudents(search));
    }
}
