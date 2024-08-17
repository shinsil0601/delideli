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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // 메뉴 화면 이동
    @GetMapping("/menu/{storeKey}")
    public String menu(@PathVariable("storeKey") int storeKey,
                       @AuthenticationPrincipal User user,
                       @RequestParam(value = "filter", required = false) String filter,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<StoreInfo> storeInfo = clientStoreService.getAllStore(clientAccount.getClientKey());
        ArrayList<ClientMenu> menuList;

        if (filter != null && keyword != null && !keyword.isEmpty()) {
            // 필터와 키워드가 있는 경우 검색 실행
            menuList = clientMenuService.searchMenu(storeKey, filter, keyword);
        } else {
            // 필터와 키워드가 없으면 모든 메뉴를 불러옴
            menuList = clientMenuService.getAllMenu(storeKey);
        }

        // 메뉴 그룹화 작업
        Map<String, List<ClientMenu>> groupedMenu = menuList.stream()
                .collect(Collectors.groupingBy(menu -> menu.getClientMenuGroup().getMenuGroupName()));

        model.addAttribute("client", clientAccount);
        model.addAttribute("store", storeInfo);
        model.addAttribute("groupedMenu", groupedMenu);
        model.addAttribute("filter", filter);
        model.addAttribute("keyword", keyword);
        log.info("groupedMenu: {}", groupedMenu);

        return "client/menu/menu";
    }

    // 상태 변경
    @PostMapping("/menu/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateMenuStatus(@RequestParam("menuKey") int menuKey,
                                                   @RequestParam("status") String status) {
        try {

            clientMenuService.updateMenuStatus(menuKey, status);
            return ResponseEntity.ok("메뉴 상태가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 상태 변경 중 오류가 발생했습니다.");
        }
    }

    // 메뉴 삭제
    @PostMapping("/menu/delete")
    @ResponseBody
    public ResponseEntity<String> deleteMenu(@RequestParam("menuKey") int menuKey) {
        try {
            clientMenuService.deleteMenu(menuKey);
            return ResponseEntity.ok("메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 삭제 중 오류가 발생했습니다.");
        }
    }
    
    // 메뉴 그룹 삭제
    @PostMapping("/menu/deleteGroup")
    @ResponseBody
    public ResponseEntity<String> deleteMenuGroup(@RequestParam("menuGroupKey") int menuGroupKey) {
        try {
            clientMenuService.deleteMenuGroup(menuGroupKey);
            return ResponseEntity.ok("메뉴 그룹과 해당 그룹의 메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴 그룹 삭제 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/updateMenu/{menuKey}")
    public String updateMenu(@PathVariable("menuKey") int menuKey, @AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        ClientMenu menu = clientMenuService.getMenuById(menuKey);
        model.addAttribute("menu", menu);
        log.info("menu: {}", menu);
        return "client/menu/updateMenu"; // updateMenu.html 파일을 만들어야 합니다.
    }

}
