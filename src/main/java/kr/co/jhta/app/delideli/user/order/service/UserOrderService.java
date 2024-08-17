package kr.co.jhta.app.delideli.user.order.service;

import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.domain.OrderDetail;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface UserOrderService {

    void saveOrder(Order order);

    void saveOrderDetail(OrderDetail orderDetail);

    ArrayList<Order> getOrdersByUserKey(int userKey);

    ArrayList<OrderDetail> getOrderDetailsByOrderKey(int orderKey);

    boolean cancelOrder(int orderKey);

    Order getOrderByKey(int orderKey);

    ArrayList<Order> getReviewableOrders(int userKey, LocalDateTime sevenDaysAgo);

}
