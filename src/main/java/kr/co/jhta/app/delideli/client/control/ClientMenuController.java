package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import kr.co.jhta.app.delideli.client.menu.service.ClientMenuService;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
@Slf4j
public class ClientMenuController {
    @Autowired
    private final ClientService clientService;
    @Autowired
    private final ClientStoreService clientStoreService;
    @Autowired
    private final ClientMenuService clientMenuService;

    @GetMapping("/menu/{storeKey}")
    public String menu(@PathVariable("storeKey") int storeKey, @AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<StoreInfo> storeInfo = clientStoreService.getAllStore(clientAccount.getClientKey());
        ArrayList<ClientMenu> menuList = clientMenuService.getAllMenu(storeKey);
        // 메뉴 그룹화 작업
        Map<String, List<ClientMenu>> groupedMenu = menuList.stream()
                .collect(Collectors.groupingBy(menu -> menu.getClientMenuGroup().getMenuGroupName()));

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeInfo);
        model.addAttribute("groupedMenu", groupedMenu);

        return "client/menu/menu";
    }

}
