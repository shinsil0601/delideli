package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import kr.co.jhta.app.delideli.client.menu.domain.ClientMenuGroup;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        //log.info("groupedMenu: {}", groupedMenu);

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
        //log.info("menu: {}", menu);
        return "client/menu/updateMenu"; // updateMenu.html 파일을 만들어야 합니다.
    }

    @GetMapping("/menuRegister/{storeKey}")
    public String registerMenu(@PathVariable("storeKey") int storeKey, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("storeKey", storeKey);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientMenuGroup> clientMenuGroup = clientMenuService.getAllMenuGroup(storeKey);
        model.addAttribute("menuGroup", clientMenuGroup);
        model.addAttribute("client", clientAccount);
        return "client/menu/registerMenu";
    }

    @PostMapping("/addMenuGroup")
    public ResponseEntity<?> addMenuGroup(@RequestBody Map<String, Object> requestData) {
        try {
            String storeKeyStr = (String) requestData.get("storeKey");
            int storeKey = Integer.parseInt(storeKeyStr);
            String menuGroupName = (String) requestData.get("menuGroupName");

            ClientMenuGroup clientMenuGroup = new ClientMenuGroup();
            clientMenuGroup.setStoreInfoKey(storeKey);
            clientMenuGroup.setMenuGroupName(menuGroupName);

            clientMenuService.addMenuGroup(clientMenuGroup);

            return ResponseEntity.ok().body("카테고리 추가 성공");
        } catch (Exception e) {
            // 예외 메시지와 스택 트레이스를 로그에 기록
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 추가 실패: " + e.getMessage());
        }
    }

    @PostMapping("/registerMenu")
    public String registerMenu(
            @RequestParam("storeKey") int storeKey,
            @RequestParam("menuName") String menuName,
            @RequestParam("category") int menuGroupKey,
            @RequestParam("price") int menuPrice,
            @RequestParam("menuImage") MultipartFile menuImage,
            RedirectAttributes redirectAttributes) {

        try {
            // ClientMenu 객체 생성 및 값 설정
            ClientMenu clientMenu = new ClientMenu();
            clientMenu.setStoreInfoKey(storeKey);
            clientMenu.setMenuName(menuName);
            clientMenu.setMenuGroupKey(menuGroupKey);
            clientMenu.setMenuPrice(menuPrice);
            if (!menuImage.isEmpty()) {
                String fileName1 = saveProfileImage(menuImage);
                clientMenu.setMenuImg(fileName1);
            }

            // 여기에서 실제로 DB에 저장하는 서비스 호출
            clientMenuService.addMenu(clientMenu);

            // 저장된 메뉴의 키 값을 가져옴
            int menuKey = clientMenu.getMenuKey();

            // 메뉴 키 값을 리다이렉트 경로에 추가
            redirectAttributes.addAttribute("menuKey", menuKey);

            // /client/registerMenuOption/{menuKey}로 리다이렉트
            return "redirect:/client/registerMenuOption/{menuKey}";
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 처리할 경로로 리다이렉트
            return "redirect:/client/registerMenu?error";
        }
    }

    @GetMapping("/registerMenuOption/{menuKey}")
    public String registerMenuOption(@PathVariable("menuKey") int menuKey, Model model) {
        // 메뉴 정보를 가져옴
        ClientMenu clientMenu = clientMenuService.getMenuById(menuKey);

        // 모델에 메뉴 정보 추가
        model.addAttribute("menu", clientMenu);

        // 추가 옵션을 처리하기 위한 페이지로 이동
        return "client/menu/registerMenuOption";
    }


    // 프로필 이미지 저장
    private String saveProfileImage(MultipartFile file) {
        String uploadDir = "src/main/resources/static/client/images/uploads/";
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        String filePath = uploadDir + uniqueFilename;

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/client/images/uploads/" + uniqueFilename;
    }

}
