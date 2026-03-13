package dev.oauth.resourceserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "resource_member_profile") // MySQL의 member 테이블과 매핑
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId; // 로그인 시 필요한 아이디

    private String name;
    private String email;
    private String gender;
    private LocalDate birthdate;


}