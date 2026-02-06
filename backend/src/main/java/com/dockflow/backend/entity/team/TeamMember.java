package com.dockflow.backend.entity.team;

import com.dockflow.backend.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TEAM_MEMBER", uniqueConstraints = @UniqueConstraint(columnNames = {"team_no", "member_no"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_no")
    private Long teamMemberNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_no", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private TeamRole role;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public enum TeamRole {
        OWNER,
        ADMIN,
        MEMBER,
        VIEWER
    }

    public void updateRole(TeamRole newRole) {
        this.role = newRole;
    }

    public boolean hasPermission(TeamRole requiredRole) {
        return this.role.ordinal() <= requiredRole.ordinal();
    }

}
