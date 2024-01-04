package com.fileShare.domain.member.entity;


import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.member.dto.MemberInfo;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.global.Authority;
import com.fileShare.global.exception.MemberNotUpdatedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

import java.util.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@OnDelete(action = OnDeleteAction.CASCADE)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "member")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<File> files = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    private Board board;


    public static Member createMember(String email, String nickname, String password) {

        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .authority(Authority.ROLE_USER)
                .build();
    }

    public Member updateMember(Long loginId, MemberInfo memberInfo) {

        return Member.builder()
                .memberId(loginId)
                .nickname(memberInfo.getNickname())
                .build();
    }

    public void updatePassword(String password) {
        if(password == null || password.equals(this.password)) {
            throw new MemberNotUpdatedException();
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

}
