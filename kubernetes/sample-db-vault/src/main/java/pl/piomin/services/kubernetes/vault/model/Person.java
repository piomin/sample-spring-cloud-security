package pl.piomin.services.kubernetes.vault.model;

import javax.persistence.*;

public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer externalId;

}
