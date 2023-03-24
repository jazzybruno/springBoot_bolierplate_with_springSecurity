package com.jazzybruno.example.v1.models;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @NotNull
    public Long roleId;
    @Column
    @NotNull
    public String roleName;

}
