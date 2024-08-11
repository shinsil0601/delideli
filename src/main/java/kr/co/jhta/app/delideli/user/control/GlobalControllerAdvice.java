package kr.co.jhta.app.delideli.user.control;

import kr.co.jhta.app.delideli.user.store.domain.Category;
import kr.co.jhta.app.delideli.user.store.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CategoryService categoryService;

    @Autowired
    public GlobalControllerAdvice(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute
    public void addCategoriesToModel(Model model) {
        // 모든 페이지에 자동으로 카테고리 목록을 추가하는 메서드
        ArrayList<Category> categoryList = categoryService.getAllCategory();
        model.addAttribute("categoryList", categoryList);
    }
}
