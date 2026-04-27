package com.example.localhistory.user.mapper;

import com.example.localhistory.user.dto.request.UserCreateRequest;
import com.example.localhistory.user.dto.response.StudentDTO;
import com.example.localhistory.user.dto.response.TeacherDTO;
import com.example.localhistory.user.dto.response.UserDTO;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.Teacher;
import com.example.localhistory.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @SubclassMapping(source = Student.class, target = StudentDTO.class)
    @SubclassMapping(source = Teacher.class, target = TeacherDTO.class)
    UserDTO toDto(User user);

    Student toStudent(UserCreateRequest request);
    Teacher toTeacher(UserCreateRequest request);
}