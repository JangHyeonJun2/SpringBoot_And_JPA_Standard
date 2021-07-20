package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional //테스트에 트랜잭션널이 있으면 데이터를 다시 롤백해버린다. 하지만 다른곳에서는 정상작동!
    void memberTest() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        Member member2 = new Member();
        member2.setUsername("memberB");

        //when
        Long save = memberRepository.save(member);
        Long save2 = memberRepository.save(member2);
        Member findMember = memberRepository.find(save);


        //then
        assertThat(save).isEqualTo(findMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);


    }

}