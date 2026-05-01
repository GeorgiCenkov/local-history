package com.example.localhistory.user;

import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import com.example.localhistory.user.dto.response.StudentLeaderboardDTO;
import com.example.localhistory.user.dto.response.UserDTO;
import com.example.localhistory.user.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

//TODO Add logic for safely handling awarding points
// Service for user-related operations shared by auth, progress, and teacher student search.
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void CreateUser(){
        throw new NotImplementedException();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchStudents(String search) {
        return userRepository.searchStudents(search == null ? "" : search.trim())
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentLeaderboardDTO> getLeaderboard(String currentUserEmail, String search) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        Long currentUserId = userRepository.findByEmail(currentUserEmail)
                .map(User::getId)
                .orElse(null);

        List<Student> rankedStudents = userRepository.findStudentsForLeaderboard();

        return java.util.stream.IntStream.range(0, rankedStudents.size())
                .mapToObj(index -> toLeaderboardDTO(rankedStudents.get(index), index + 1, currentUserId))
                .filter(row -> normalizedSearch.isBlank() || matchesLeaderboardSearch(row, normalizedSearch))
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentLeaderboardDTO getCurrentStudentLeaderboardRow(String studentEmail) {
        User currentUser = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + studentEmail));

        if (!(currentUser instanceof Student)) {
            throw new IllegalArgumentException("Only students can have a leaderboard rank");
        }

        return findCurrentStudentRank(currentUser.getId());
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

    /** Converts a sorted student into a ranked leaderboard row. */
    private StudentLeaderboardDTO toLeaderboardDTO(Student student, int rank, Long currentUserId) {
        StudentLeaderboardDTO dto = new StudentLeaderboardDTO();
        dto.setRank(rank);
        dto.setStudentId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setLevel(student.getLevel() != null ? student.getLevel() : 1);
        dto.setPoints(student.getPoints() != null ? student.getPoints() : 0);
        dto.setPointsRequired(student.getPointsRequired() != null ? student.getPointsRequired() : 100);
        dto.setCurrentUser(Objects.equals(student.getId(), currentUserId));
        return dto;
    }

    /** Matches name and email without changing the rank, which remains global. */
    private boolean matchesLeaderboardSearch(StudentLeaderboardDTO row, String search) {
        String fullName = (row.getFirstName() + " " + row.getLastName()).toLowerCase(Locale.ROOT);
        return fullName.contains(search)
                || row.getFirstName().toLowerCase(Locale.ROOT).contains(search)
                || row.getLastName().toLowerCase(Locale.ROOT).contains(search)
                || row.getEmail().toLowerCase(Locale.ROOT).contains(search);
    }

    /** Finds the authenticated student after ranks have been assigned globally. */
    private StudentLeaderboardDTO findCurrentStudentRank(Long studentId) {
        List<Student> rankedStudents = userRepository.findStudentsForLeaderboard();

        for (int index = 0; index < rankedStudents.size(); index++) {
            Student student = rankedStudents.get(index);

            if (Objects.equals(student.getId(), studentId)) {
                return toLeaderboardDTO(student, index + 1, studentId);
            }
        }

        throw new EntityNotFoundException("Student not found in leaderboard");
    }
}
