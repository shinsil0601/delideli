package kr.co.jhta.app.delideli.user.order.service;

import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.domain.OrderDetail;
import kr.co.jhta.app.delideli.user.order.mapper.UserOrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    private final UserOrderMapper userOrderMapper;

    public UserOrderServiceImpl(UserOrderMapper userOrderMapper) {
        this.userOrderMapper = userOrderMapper;
    }

    public void saveOrder(Order order) {
        userOrderMapper.insertOrder(order);
    }

    public void saveOrderDetail(OrderDetail orderDetail) {
        userOrderMapper.insertOrderDetail(orderDetail);
    }

    @Override
    public ArrayList<Order> getOrdersByUserKey(int userKey) {
        return userOrderMapper.findOrderById(userKey);
    }

    @Override
    public ArrayList<OrderDetail> getOrderDetailsByOrderKey(int orderKey) {
        return userOrderMapper.findOrderDetailById(orderKey);
    }

    @Override
    public boolean cancelOrder(int orderKey) {
        int result = userOrderMapper.cancelOrder(orderKey);
        //System.out.println("Order cancellation result: " + result);
        return result > 0;
    }

    @Override
    public Order getOrderByKey(int orderKey) {
        Order order = userOrderMapper.findOrderByOrderKey(orderKey);
        if (order != null) {
            ArrayList<OrderDetail> orderDetails = userOrderMapper.findOrderDetailById(orderKey);
            order.setOrderDetails(orderDetails);
        }
        return order;
    }

    @Override
    public ArrayList<Order> getReviewableOrders(int userKey, LocalDateTime sevenDaysAgo) {
        return userOrderMapper.findReviewableOrders(userKey, sevenDaysAgo);
    }

    @Override
    public ArrayList<Order> getOrdersWithoutReview(int userKey, LocalDateTime fromDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("userKey", userKey);
        params.put("fromDate", fromDate);
        return userOrderMapper.getOrdersWithoutReview(params);
    }

    @Override
    public ArrayList<Order> getOrdersWithReview(int userKey) {
        return userOrderMapper.getOrdersWithReview(userKey);
    }
}
