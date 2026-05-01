package com.example.localhistory.user;

import com.example.localhistory.user.model.User;
import com.example.localhistory.user.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Repository for user lookup, authentication, and teacher-facing student search.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
            select u from Student u
            where (
                lower(u.firstName) like lower(concat('%', :search, '%'))
                or lower(u.lastName) like lower(concat('%', :search, '%'))
                or lower(u.email) like lower(concat('%', :search, '%'))
                or lower(concat(u.firstName, ' ', u.lastName)) like lower(concat('%', :search, '%'))
            )
            order by u.firstName asc, u.lastName asc, u.email asc
            """)
    List<User> searchStudents(@Param("search") String search);

    @Query("""
            select s from Student s
            order by coalesce(s.level, 1) desc,
                     coalesce(s.points, 0) desc,
                     s.firstName asc,
                     s.lastName asc,
                     s.email asc
            """)
    List<Student> findStudentsForLeaderboard();
}
