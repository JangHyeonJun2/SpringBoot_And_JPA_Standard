package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 상품주문 () throws Exception {
        //given
        Member member = getMember();

        Book book = getBook("시골 JPA", 10000, 10);

        int orderCount = 3;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order one = orderRepository.findOne(orderId);
        assertThat(OrderStatus.ORDER).isEqualTo(one.getStatus());
        assertAll(
                () -> assertEquals(1, one.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다."),
                () -> assertEquals(30000, one.getTotalPrice(),"주문한 가격은 가겨 * 수량이다."),
                () -> assertEquals(7, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.")
        );
    }

    @Test
    public void 주문취소 () throws Exception {
        //given
        Member member = getMember();
        Book item = getBook("시골 JPA", 10000, 10);
        int count  = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), count);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order one = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCLE, one.getStatus(), "주문 취소시 상태는 CANCEL이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");

    }

    @Test
    public void 상품주문_재고수량초과 () throws Exception {
        //given
        Member member = getMember();
        Book book = getBook("시골 JPA", 10000, 10);

        int count = 11;

        //when

        //then
        NotEnoughStockException ex = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), count);
        });
//        System.out.println(ex);
        assertEquals("need more stock", ex.getMessage());
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "관악구", "1234"));
        em.persist(member);
        return member;
    }
}