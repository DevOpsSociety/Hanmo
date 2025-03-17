package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor

public class DepartmentEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "department_name", length = 100, nullable = false, unique = true)
    private String department_name;

    public DepartmentEntity(String department_name) {
        this.department_name = department_name;
    }
}
