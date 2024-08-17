package kr.co.jhta.app.delideli.client.store.service;

import kr.co.jhta.app.delideli.client.store.domain.Category;
import kr.co.jhta.app.delideli.client.store.mapper.ClientCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("clientCategoryService")
public class ClientCategoryServiceImpl implements ClientCategoryService {

    private final ClientCategoryMapper clientCategoryMapper;

    public ClientCategoryServiceImpl(ClientCategoryMapper clientCategoryMapper) {
        this.clientCategoryMapper = clientCategoryMapper;
    }

    @Override
    public ArrayList<Category> getAllCategory() {
        return clientCategoryMapper.getAllCategory();
    }
}
