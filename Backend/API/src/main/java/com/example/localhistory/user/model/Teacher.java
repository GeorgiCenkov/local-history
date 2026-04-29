package com.example.localhistory.user.model;

import com.example.localhistory.landmark.model.Landmark;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
@Getter
@Setter
public class Teacher extends User {
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Landmark> landmarks;
}
