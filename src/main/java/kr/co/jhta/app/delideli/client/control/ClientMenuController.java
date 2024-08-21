package kr.co.jhta.app.delideli.client.control;

import kr.co.jhta.app.delideli.client.account.domain.ClientAccount;
import kr.co.jhta.app.delideli.client.account.service.ClientService;
import kr.co.jhta.app.delideli.client.menu.domain.ClientMenu;
import kr.co.jhta.app.delideli.client.menu.domain.ClientMenuGroup;
import kr.co.jhta.app.delideli.client.menu.domain.ClientOption;
import kr.co.jhta.app.delideli.client.menu.domain.ClientOptionGroup;
import kr.co.jhta.app.delideli.client.menu.service.ClientMenuService;
import kr.co.jhta.app.delideli.client.menu.service.ClientOptionService;
import kr.co.jhta.app.delideli.client.store.domain.ClientStoreInfo;
import kr.co.jhta.app.delideli.client.store.service.ClientStoreService;
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
import java.util.*;
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
    @Autowired
    private final ClientOptionService clientOptionService;

    @GetMapping("/menu")
    public String menu(@AuthenticationPrincipal User user,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                       Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);

        // 가게 목록 페이징 처리
        ArrayList<ClientStoreInfo> storeList = clientStoreService.getAllStoreWithPaging(clientAccount.getClientKey(), page, pageSize);
        model.addAttribute("store", storeList);

        int totalStores = clientStoreService.getTotalStoreCountByClientKey(clientAccount.getClientKey());
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);

        // 페이지네이션 정보 추가
        Map<String, Object> paginationMap = createPaginationMap(page, totalPages);

        model.addAttribute("map", paginationMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize); // 페이지 사이즈 설정
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", "menu");
        model.addAttribute("on", "menu");

        return "client/menu/menu.list";
    }

    // 메뉴 화면 이동
    @GetMapping("/menu/{storeKey}")
    public String menu(@PathVariable("storeKey") int storeKey,
                       @AuthenticationPrincipal User user,
                       @RequestParam(value = "filter", required = false) String filter,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientStoreInfo> storeInfo = clientStoreService.getAllStore(clientAccount.getClientKey());
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

        model.addAttribute("on", "menu");
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

    // 메뉴 수정으로 이동
    @GetMapping("/updateMenu/{menuKey}")
    public String updateMenu(@PathVariable("menuKey") int menuKey, @AuthenticationPrincipal User user, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        ClientMenu menu = clientMenuService.getMenuById(menuKey);
        model.addAttribute("menu", menu);
        ArrayList<ClientMenuGroup> clientMenuGroup = clientMenuService.getAllMenuGroup(menu.getStoreInfoKey());
        model.addAttribute("menuGroup", clientMenuGroup);
        model.addAttribute("on", "menu");
        return "client/menu/updateMenu";
    }

    // 메뉴 수정 처리
    @PostMapping("/updateMenu")
    public String updateMenu(@RequestParam("menuKey") int menuKey,
                             @RequestParam("storeKey") int storeKey,
                             @RequestParam("menuName") String menuName,
                             @RequestParam("menuGroupKey") int menuGroupKey,
                             @RequestParam("menuPrice") int menuPrice,
                             @RequestParam(value = "menuImg", required = false) MultipartFile menuImg,
                             @RequestParam("currentMenuImg") String currentMenuImg, // 현재 이미지 경로를 hidden input으로 받습니다.
                             Model model) {
        model.addAttribute("on", "menu");
        try {
            // ClientMenu 객체 생성 및 필드 설정
            ClientMenu menu = new ClientMenu();
            menu.setMenuKey(menuKey);
            menu.setStoreInfoKey(storeKey);
            menu.setMenuName(menuName);
            menu.setMenuGroupKey(menuGroupKey);
            menu.setMenuPrice(menuPrice);

            // 이미지 파일이 있으면 처리, 없으면 기존 이미지 유지
            if (menuImg != null && !menuImg.isEmpty()) {
                String fileName = saveProfileImage(menuImg);
                menu.setMenuImg(fileName);
            } else {
                menu.setMenuImg(currentMenuImg);  // 기존 이미지 유지
            }

            // 메뉴 업데이트 처리 (서비스 호출)
            clientMenuService.updateMenu(menu);

            // 성공적으로 업데이트되면 해당 가게의 메뉴 리스트로 리다이렉트
            return "redirect:/client/menu/" + storeKey;

        } catch (Exception e) {
            e.printStackTrace();

            // 오류 메시지 설정
            model.addAttribute("message", "메뉴 업데이트 중 오류가 발생했습니다.");
            return "client/menu/updateMenu";
        }
    }

    // 메뉴 등록창 이동
    @GetMapping("/menuRegister/{storeKey}")
    public String registerMenu(@PathVariable("storeKey") int storeKey, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("storeKey", storeKey);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        ArrayList<ClientMenuGroup> clientMenuGroup = clientMenuService.getAllMenuGroup(storeKey);
        model.addAttribute("menuGroup", clientMenuGroup);
        model.addAttribute("client", clientAccount);
        model.addAttribute("on", "menu");
        return "client/menu/registerMenu";
    }

    // 메뉴 그룹(카테고리) 추가
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

    // 메뉴 등록
    @PostMapping("/registerMenu")
    public String registerMenu(@RequestParam("storeKey") int storeKey, @RequestParam("menuName") String menuName, @RequestParam("category") int menuGroupKey, @RequestParam("price") int menuPrice, @RequestParam("menuImage") MultipartFile menuImage, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("on", "menu");

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

    // 메뉴 옵션 추가창 이동
    @GetMapping("/registerMenuOption/{menuKey}")
    public String registerMenuOption(@AuthenticationPrincipal User user, @PathVariable("menuKey") int menuKey, Model model) {
        ArrayList<ClientOptionGroup> optionGroups = clientOptionService.getMenuOptionByMenuKey(menuKey);
        ClientMenu menu = clientMenuService.getMenuById(menuKey);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("storeKey", menu.getStoreInfoKey());
        model.addAttribute("menuKey", menuKey);
        model.addAttribute("optionList", optionGroups);
        model.addAttribute("on", "menu");
        return "client/menu/registerMenuOption";
    }

    // 메뉴 옵션그룹 추가창 (모달창)
    @GetMapping("/addOptionGroup/{menuKey}")
    public String addOptionGroup(@AuthenticationPrincipal User user, @PathVariable("menuKey") int menuKey, Model model) {
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("menuKey", menuKey);
        model.addAttribute("on", "menu");
        return "client/menu/addOptionGroup";
    }

    // 메뉴 옵션그룹 등록
    @PostMapping("/saveOptionGroup")
    public ResponseEntity<String> saveOptionGroup(@RequestBody ClientOptionGroup clientOptionGroup) {
        clientOptionService.addOptionGroup(clientOptionGroup);
        return ResponseEntity.ok("옵션 그룹이 성공적으로 등록되었습니다.");
    }

    // 메뉴 옵션그룹 수정창 (모달창)
    @GetMapping("/updateOptionGroup/{optionGroupKey}")
    public String updateOptionGroup(@AuthenticationPrincipal User user, @PathVariable("optionGroupKey") int optionGroupKey, Model model) {
        ClientOptionGroup clientOptionGroup = clientOptionService.getOptionGroupByKey(optionGroupKey);
        ClientAccount clientAccount = clientService.findClientById(user.getUsername());
        model.addAttribute("client", clientAccount);
        model.addAttribute("optionGroup", clientOptionGroup);
        model.addAttribute("on", "menu");
        return "client/menu/updateOptionGroup";
    }

    // 메뉴 옵션그룹 수정
    @PostMapping("/updateOptionGroup")
    public ResponseEntity<String> updateOptionGroup(@RequestBody ClientOptionGroup clientOptionGroup) {
        clientOptionService.updateOptionGroup(clientOptionGroup);
        return ResponseEntity.ok("옵션 그룹이 성공적으로 수정되었습니다.");
    }

    // 메뉴 옵션그룹 제거
    @PostMapping("/deleteOptionGroup/{optionGroupKey}")
    public ResponseEntity<String> deleteOptionGroup(@PathVariable("optionGroupKey") int optionGroupKey) {
        clientOptionService.deleteOptionbyOptionGroupKey(optionGroupKey);
        clientOptionService.deleteOptionGroup(optionGroupKey);
        return ResponseEntity.ok("옵션 그룹이 성공적으로 제거되었습니다.");
    }

    // 메뉴 옵션 추가
    @PostMapping("/addOption")
    @ResponseBody
    public Map<String, Object> addOption(@RequestBody ClientOption clientOption) {
        Map<String, Object> response = new HashMap<>();
        ///log.info("clientOption {}", clientOption);
        try {
            clientOptionService.addOption(clientOption);
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "옵션 추가 중 오류가 발생했습니다.");
        }
        return response;
    }

    // 옵션 상태 변경
    @PostMapping("/updateOptionStatus")
    @ResponseBody
    public Map<String, Object> updateOptionStatus(@RequestBody ClientOption clientOption) {
        Map<String, Object> response = new HashMap<>();
        try {
            clientOptionService.updateOptionStatus(clientOption.getOptionKey(), clientOption.getOptionStatus());
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "상태 변경 중 오류가 발생했습니다.");
        }
        return response;
    }

    // 옵션 제거
    @PostMapping("/deleteOption")
    @ResponseBody
    public Map<String, Object> deleteOption(@RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        int optionKey = request.get("optionKey");
        try {
            // 옵션 삭제 서비스 호출
            clientOptionService.deleteOptionByOptionKey(optionKey);
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "옵션 삭제 중 오류가 발생했습니다.");
        }
        return response;
    }

    // 이미지 저장
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

    // 페이지네이션 정보를 맵핑하는 메서드
    private Map<String, Object> createPaginationMap(int currentPage, int totalPages) {
        Map<String, Object> map = new HashMap<>();
        map.put("prev", currentPage > 1);
        map.put("next", currentPage < totalPages);
        map.put("startPageNo", 1); // 필요에 따라 조정 가능
        map.put("endPageNo", totalPages); // 필요에 따라 조정 가능
        return map;
    }
}
