package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //RollBack을 위해서!
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Test
    void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Owen");

        //when
        Long savedId = memberService.join(member);

        //then
        Assertions.assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }

    @Test
    void 중복_회원_예약() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("jang1");

        Member member2 = new Member();
        member2.setName("jang1");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2);
        }catch (IllegalArgumentException e) {
            return;
        }

//        assertThrows(IllegalArgumentException.class, () -> {
//            memberService.join(member2);
//        });


        //then
        fail("예외가 발생해야합니다.");
    }

}