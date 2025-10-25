package com.example.busconnect.domine.entities;

import com.example.busconnect.domine.entities.enums.UserRole;
import com.example.busconnect.domine.entities.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import lombok.Builder.Default;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private  String username;

     @Column(nullable = false,unique = true, length = 100)
    private String email;

     @Column(nullable = false, length = 10)
    private String phone;

     @Enumerated(EnumType.STRING)
     private UserRole role;

     @Default
     @Enumerated(EnumType.STRING)
     @Column(nullable = false, length = 20)
     private UserStatus status =  UserStatus.ACTIVE;

     @Column(nullable = false, length = 255)
    private String passwordHash;
     @Column(name ="date_of_birth", nullable = false)
     private LocalDate dateOfBirth;

     @Default
     @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();
     public int getAge(){
         return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
     }

}
