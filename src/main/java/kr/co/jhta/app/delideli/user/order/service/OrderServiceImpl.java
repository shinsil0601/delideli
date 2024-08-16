package kr.co.jhta.app.delideli.user.order.service;

import kr.co.jhta.app.delideli.user.order.domain.Order;
import kr.co.jhta.app.delideli.user.order.domain.OrderDetail;
import kr.co.jhta.app.delideli.user.order.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public void saveOrder(Order order) {
        orderMapper.insertOrder(order);
    }

    public void saveOrderDetail(OrderDetail orderDetail) {
        orderMapper.insertOrderDetail(orderDetail);
    }

    @Override
    public ArrayList<Order> getOrdersByUserKey(int userKey) {
        return orderMapper.findOrderById(userKey);
    }

    @Override
    public ArrayList<OrderDetail> getOrderDetailsByOrderKey(int orderKey) {
        return orderMapper.findOrderDetailById(orderKey);
    }

    @Override
    public boolean cancelOrder(int orderKey) {
        int result = orderMapper.cancelOrder(orderKey);
        System.out.println("Order cancellation result: " + result);
        return result > 0;
    }

    @Override
    public Order getOrderByKey(int orderKey) {
        Order order = orderMapper.findOrderByOrderKey(orderKey);
        if (order != null) {
            ArrayList<OrderDetail> orderDetails = orderMapper.findOrderDetailById(orderKey);
            order.setOrderDetails(orderDetails);
        }
        return order;
    }

    @Override
    public ArrayList<Order> getReviewableOrders(int userKey, LocalDateTime sevenDaysAgo) {
        return orderMapper.findReviewableOrders(userKey, sevenDaysAgo);
    }

}
