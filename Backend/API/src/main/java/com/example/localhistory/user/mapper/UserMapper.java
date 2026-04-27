package com.example.localhistory.user.mapper;

import com.example.localhistory.user.dto.request.UserCreateRequest;
import com.example.localhistory.user.dto.response.StudentDTO;
import com.example.localhistory.user.dto.response.TeacherDTO;
import com.example.localhistory.user.dto.response.UserDTO;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.Teacher;
import com.example.localhistory.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UserDTO toDto(User user) {
        if (user instanceof Student student) return toDto(student);
        if (user instanceof Teacher teacher) return toDto(teacher);
        throw new IllegalArgumentException("Unknown user type");
    }
    @Mapping(target = "role", expression = "java(com.example.localhistory.user.model.Role.STUDENT)")
    StudentDTO toDto(Student student);

    @Mapping(target = "role", expression = "java(com.example.localhistory.user.model.Role.TEACHER)")
    TeacherDTO toDto(Teacher teacher);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "pointsRequired", ignore = true)
    Student toStudent(UserCreateRequest request);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    Teacher toTeacher(UserCreateRequest request);
}