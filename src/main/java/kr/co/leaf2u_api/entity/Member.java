package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

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

    @Column(nullable = false)
    private char card_yn;

    @LastModifiedDate
    @Column(name="password_update_date")
    private LocalDateTime passwordUpdateDate;
}
