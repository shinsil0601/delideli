package kr.co.jhta.app.delideli.client.control;


import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.store.domain.Category;
import kr.co.jhta.app.delideli.client.store.service.ClientCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientStoreController {

    @Autowired
    private final ClientService clientService;
    @Autowired
    private final ClientCategoryService clientCategoryService;

    @GetMapping("/storeRegister")
    public String storeRegister(@AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<Category> category = clientCategoryService.getAllCategory();
        model.addAttribute("client", clientAccount);
        model.addAttribute("category", category);

        return "/client/store/store.register";
    }
}
