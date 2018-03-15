package com.rentit.sales.domain.model;

import lombok.*;

import javax.persistence.*;
@Entity
@Getter
@NoArgsConstructor(force=true,access= AccessLevel.PRIVATE)
@AllArgsConstructor(staticName="of")
@Table(name="USERS_TABLE")
public class UserTable {
        @Id
        @Column(name="ID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        String id;
        @Column(name="username")
        String username;
        @Column(name="email")
        String email;
        @Column(name="password")
        String password;
        @Column(name="role")
        int role;

}