package kr.co.jhta.app.delideli.user.order.mapper;

import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.domain.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Mapper
public interface OrderMapper {
    void insertOrder(Order order);

    void insertOrderDetail(OrderDetail orderDetail);

    ArrayList<Order> findOrderById(int userKey);

    ArrayList<OrderDetail> findOrderDetailById(int orderKey);

    Integer cancelOrder(int orderKey);

    Order findOrderByOrderKey(int orderKey);

    ArrayList<Order> findReviewableOrders(int userKey, LocalDateTime sevenDaysAgo);
}
