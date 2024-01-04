package com.fileShare.auth.userdetails;

import com.fileShare.auth.utils.CustomAuthorityUtils;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.MemberNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;

    public MemberDetailsService(MemberRepository memberRepository, CustomAuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.authorityUtils = authorityUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member findMember = optionalMember.orElseThrow(MemberNotFoundException::new);

        return new MemberDetails(findMember, authorityUtils);
    }

    protected final class MemberDetails implements UserDetails {
        private final Member member;
        private final CustomAuthorityUtils authorityUtils;

        MemberDetails(Member member, CustomAuthorityUtils authorityUtils) {
            this.member = member;
            this.authorityUtils = authorityUtils;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(member.getAuthorities());
        }

        @Override
        public String getUsername() {
            return member.getEmail();
        }

        @Override
        public String getPassword() {
            return member.getPassword();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}