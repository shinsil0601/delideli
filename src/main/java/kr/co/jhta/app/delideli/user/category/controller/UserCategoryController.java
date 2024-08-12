package kr.co.jhta.app.delideli.user.category.controller;

import kr.co.jhta.app.delideli.user.category.dto.CategoryDTO;
import kr.co.jhta.app.delideli.user.category.dto.CategoryStoreInfoDTO;
import kr.co.jhta.app.delideli.user.category.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    //카테고리 메인
    @GetMapping("/category")
    public String categoryMain(Model model) {
        List<CategoryDTO> list = userCategoryService.getCategoryAllList();
        model.addAttribute("list", list);
        return "userCategory";
    }

    //해당카테고리 상점 가져오기
    @GetMapping("/categorySort/{num}")
    @ResponseBody
    public List<CategoryStoreInfoDTO> sortCategory(@PathVariable int num) {
        List<CategoryStoreInfoDTO> list = userCategoryService.getCategoryStoreInfo(num);

        // 로그로 리스트 출력
        if (list != null) {
            log.info("CategoryStoreInfo list size: {}", list.size());
            for (CategoryStoreInfoDTO item : list) {
                log.info("CategoryStoreInfo: {}", item);
            }
        } else {
            log.info("CategoryStoreInfo list is null");
        }

        return list;
    }

    // 전체 상점 목록 가져오기
    @GetMapping("/categorySort/all")
    @ResponseBody
    public List<CategoryStoreInfoDTO> getAllStores() {
        log.info("getAllStores called");
        List<CategoryStoreInfoDTO> list = userCategoryService.getAllStores();

        // 로그로 리스트 출력
        if (list != null) {
            log.info("AllStores list size: {}", list.size());
            for (CategoryStoreInfoDTO item : list) {
                log.info("StoreInfo: {}", item);
            }
        } else {
            log.info("AllStores list is null");
        }

        return list;
    }

}
