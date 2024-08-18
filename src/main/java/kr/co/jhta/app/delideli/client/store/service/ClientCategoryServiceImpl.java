package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.ClientCategory;
import kr.co.jhta.app.delideli.client.store.mapper.ClientCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientCategoryServiceImpl implements ClientCategoryService {

    private final ClientCategoryMapper clientCategoryMapper;

    public ClientCategoryServiceImpl(ClientCategoryMapper clientCategoryMapper) {
        this.clientCategoryMapper = clientCategoryMapper;
    }

    @Override
    public ArrayList<ClientCategory> getAllCategory() {
        return clientCategoryMapper.getAllCategory();
    }
}
