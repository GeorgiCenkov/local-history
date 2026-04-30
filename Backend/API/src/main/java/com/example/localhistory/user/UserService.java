package com.example.localhistory.user;

import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

//TODO Add logic for safely handling awarding points
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void CreateUser(){
        throw new NotImplementedException();
    }

    @Transactional
    public void awardPoints(Long userId, int points) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with specified id"));

        if (!(user instanceof Student student)) {
            throw new AccessDeniedException("Only students can receive points");
        }

        initializeMissingStudentProgress(student);

        int newPoints = student.getPoints() + points;

        // Award points, level up, save remaining points, etc.
        while (newPoints >= student.getPointsRequired()) {
            newPoints -= student.getPointsRequired();
            student.setLevel(student.getLevel() + 1);
            student.setPointsRequired((int)(student.getPointsRequired() * 1.2)); // Always need 20% more points
        }

        student.setPoints(newPoints);
    }

    private void initializeMissingStudentProgress(Student student) {
        if (student.getLevel() == null || student.getLevel() < 1) {
            student.setLevel(1);
        }

        if (student.getPoints() == null || student.getPoints() < 0) {
            student.setPoints(0);
        }

        if (student.getPointsRequired() == null || student.getPointsRequired() < 1) {
            student.setPointsRequired(100);
        }
    }
}
