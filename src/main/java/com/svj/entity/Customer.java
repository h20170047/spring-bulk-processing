package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CUSTOMERS_DETAILS")
public class Customer {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String contactNumber;
    private String country;
    private String dob;
    private String age;
}
