package kr.co.jhta.app.delideli.client.order.service;

import kr.co.jhta.app.delideli.client.order.domain.ClientOrder;
import kr.co.jhta.app.delideli.client.order.mapper.ClientOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ClientOrderServiceImpl implements ClientOrderService {
    @Autowired
    private final ClientOrderMapper clientOrderMapper;

    public ClientOrderServiceImpl(ClientOrderMapper clientOrderMapper) {
        this.clientOrderMapper = clientOrderMapper;
    }


    @Override
    public int getTotalWaitOrdersByStoreInfoKey(int storeInfoKey) {
        return clientOrderMapper.countWaitOrders(storeInfoKey);
    }

    @Override
    public ArrayList<ClientOrder> getWaitOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return clientOrderMapper.getWaitOrdersByStoreInfoKeyWithPaging(storeInfoKey, offset, pageSize);
    }

    @Override
    public ClientOrder getOrderDeatailByOrderKey(int orderKey) {
        return clientOrderMapper.getWaitOrderDeatailByOrderKey(orderKey);
    }

    @Override
    public void updateOrder(ClientOrder order) {
        clientOrderMapper.updateOrder(order);
    }

    @Override
    public int getTotalprocessingOrderByStoreInfoKey(int storeInfoKey) {
        return clientOrderMapper.countProcessingOrder(storeInfoKey);
    }

    @Override
    public ArrayList<ClientOrder> getprocessingOrderByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return clientOrderMapper.getProcessingOrderByStoreInfoKeyWithPaging(storeInfoKey, offset, pageSize);
    }

    @Override
    public int getTotalSuccessOrdersByStoreInfoKey(int storeInfoKey) {
        return clientOrderMapper.countSuccessOrder(storeInfoKey);
    }

    @Override
    public ArrayList<ClientOrder> getSuccessOrdersByStoreInfoKeyWithPaging(int storeInfoKey, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return clientOrderMapper.getSuccessOrdersByStoreInfoKeyWithPaging(storeInfoKey, offset, pageSize);
    }

    @Override
    public int countTotalOrder(Map<String, Object> params) {
        return clientOrderMapper.countTotalOrder(params);
    }

    @Override
    public ArrayList<ClientOrder> getTotalOrdersByStoreInfoKeyWithPaging(Map<String, Object> params, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        return clientOrderMapper.getTotalOrdersByStoreInfoKeyWithPaging(params);
    }

}
