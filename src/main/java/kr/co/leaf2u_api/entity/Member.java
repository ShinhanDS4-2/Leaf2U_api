package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique = true)
    private String email;

    @Column(nullable=false)
    private String pattern_password;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String phone_number;

    @Column(nullable=false)
    private String birthday;

    @Column(nullable=false)
    private String gender;

    @Column(nullable=false)
    private char savingAccountYn;
}
