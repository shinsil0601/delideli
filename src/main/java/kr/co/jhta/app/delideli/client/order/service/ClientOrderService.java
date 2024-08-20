package kr.co.jhta.app.delideli.client.order.service;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;

import java.util.ArrayList;
import java.util.Map;

public interface ClientOrderService {

    int getTotalWaitOrdersByStoreInfoKey(int storeInfoKey);

    ArrayList<ClientOrder> getWaitOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize);

    ClientOrder getOrderDeatailByOrderKey(int orderKey);

    void updateOrder(ClientOrder order);

    int getTotalprocessingOrderByStoreInfoKey(int storeInfoKey);

    ArrayList<ClientOrder> getprocessingOrderByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize);

    int getTotalSuccessOrdersByStoreInfoKey(int storeInfoKey);

    ArrayList<ClientOrder> getSuccessOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize);

    int countTotalOrder(Map<String, Object> params);

    ArrayList<ClientOrder> getTotalOrdersByStoreInfoKeyWithPaging(Map<String, Object> params, int page, int pageSize);
}
