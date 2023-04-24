package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.zerock.b01.domain.MemberRole.ADMIN;
import static org.zerock.b01.domain.MemberRole.USER;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers() {

        IntStream.rangeClosed(1, 100).forEach(i ->{

            Member member = Member.builder()
                    .mid("member" + i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email"+i+"@aaa.bbb")
                    .build();

            member.addRole(USER);

            if (i >= 90) {
                member.addRole(ADMIN);
            }
            memberRepository.save(member);

        });

    }

    @Test
    public void testRead() {

        Optional<Member> result = memberRepository.getWithRoles("member100");

        Member member = result.orElseThrow();

        log.info("member = " + member);
        log.info("member.getRoleSet() = " + member.getRoleSet());

        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));
    }

}