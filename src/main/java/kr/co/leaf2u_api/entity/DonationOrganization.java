package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DonationOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String telNumber;

    @Column(nullable = false)
    private String description;
}
