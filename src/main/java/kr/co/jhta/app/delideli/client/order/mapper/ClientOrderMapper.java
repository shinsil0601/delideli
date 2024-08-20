package kr.co.jhta.app.delideli.client.order.mapper;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface ClientOrderMapper {
    int countWaitOrders(int storeInfoKey);

    ArrayList<ClientOrder> getWaitOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int offset, int pageSize);

    ClientOrder getWaitOrderDeatailByOrderKey(int orderKey);

    void updateOrder(ClientOrder order);

    int countProcessingOrder(int storeInfoKey);

    ArrayList<ClientOrder> getProcessingOrderByStoreInfoKeyWithPaging(int storeInfoKey, int offset, int pageSize);

    int countSuccessOrder(int storeInfoKey);

    ArrayList<ClientOrder> getSuccessOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int offset, int pageSize);

    int countTotalOrder(Map<String, Object> params);

    ArrayList<ClientOrder> getTotalOrdersByStoreInfoKeyWithPaging(Map<String, Object> params);
}
