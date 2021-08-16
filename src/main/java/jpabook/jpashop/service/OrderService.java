package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문

    /**
     * 아래의 코드를 보면 delivery, orderItem을 각가의 repository에 save를 하지않고 Order.createOrder에 매개변수에 집어넣었다. 이게 가능한 이유는 Order 엔티티를 보면 delivery, orderItem
     * 이 casecade를 ALL를 해놨기 때문이다.
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member findMember = memberRepository.findOne(memberId);
        Item findItem = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(findMember.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(findItem, findItem.getPrice(), count);

        //주문생성
        Order order = Order.createOrder(findMember, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }
    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();
    }

    //검색
    public List<Order> findOrder(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
