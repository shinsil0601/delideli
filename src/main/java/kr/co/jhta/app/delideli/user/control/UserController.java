package kr.co.jhta.app.delideli.user.control;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.jhta.app.delideli.common.security.CustomAuthenticationDetails;
import kr.co.jhta.app.delideli.common.security.JwtTokenProvider;
import kr.co.jhta.app.delideli.common.service.EmailService;
import kr.co.jhta.app.delideli.common.service.EmailVerificationService;
import kr.co.jhta.app.delideli.user.account.domain.UserAccount;
import kr.co.jhta.app.delideli.user.account.domain.UserAddress;
import kr.co.jhta.app.delideli.user.board.domain.Board;
import kr.co.jhta.app.delideli.user.cart.domain.Cart;
import kr.co.jhta.app.delideli.user.cart.domain.CartOptions;
import kr.co.jhta.app.delideli.user.cart.service.CartService;
import kr.co.jhta.app.delideli.user.dto.CartDTO;
import kr.co.jhta.app.delideli.user.dto.UserDTO;
import kr.co.jhta.app.delideli.user.account.exception.DuplicateUserIdException;
import kr.co.jhta.app.delideli.user.account.service.UserService;
import kr.co.jhta.app.delideli.user.store.domain.Menu;
import kr.co.jhta.app.delideli.user.store.domain.OptionGroup;
import kr.co.jhta.app.delideli.user.store.domain.StoreInfo;
import kr.co.jhta.app.delideli.user.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final EmailVerificationService emailVerificationService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final CartService cartService;


    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            log.info("User is authenticated: {}", user.getUsername());
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
            log.info("프로필 이미지 : " + userAccount.getUserProfile());
        } else {
            log.info("User is not authenticated");
        }
        return "index";
    }

    // 로그인창 이동
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        //log.info("로그인창 넘어옴");
        return "user/account/login";
    }

    // 로그인 (JWT 토큰 생성 및 캐시에 저장)
    @PostMapping("/loginProc")
    public String loginProc(@RequestParam String userId, @RequestParam String password, HttpServletResponse response) {
        log.debug("loginProc 호출됨");
        log.debug("입력된 사용자 이름: {}", userId);
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);
            authenticationToken.setDetails(new CustomAuthenticationDetails("ROLE_USER"));
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);
            log.info("JWT 토큰 : {}", token);

            // JWT 토큰을 쿠키에 추가
            Cookie cookie = jwtTokenProvider.createCookie(token);
            response.addCookie(cookie);

            return "redirect:home";
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: " + userId);
            log.error("Authentication failed", e);
            return "redirect:/user/login?error="  + urlEncode("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // URL 인코딩을 위한 유틸리티 메서드
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 인코딩은 항상 지원되므로 이 예외가 발생할 가능성은 거의 없음
            throw new RuntimeException(e);
        }
    }

    // 회원가입 창 이동
    @GetMapping("/register")
    public String register() {
        return "user/account/register";
    }

    // 회원가입
    @PostMapping("/register")
    public String registerUser(UserDTO userDTO, Model model) {
        try {
            userService.registerUser(userDTO);
            return "redirect:/user/login";
        } catch (DuplicateUserIdException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/account/register";
        }
    }

    // 아이디 찾기 창 이동
    @GetMapping("/findId")
    public String findId() {
        return "user/account/findId";
    }

    // 이메일로 아이디 전송
    @PostMapping("/findId")
    @ResponseBody
    public Map<String, Boolean> findId(@RequestParam String userName, @RequestParam String userEmail) {
        boolean success = userService.findIdAndSendEmail(userName, userEmail);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    // 비밀번호 찾기 창 이동
    @GetMapping("/findPw")
    public String findPw() {
        return "user/account/findPw";
    }

    // 비밀번호 변경 링크 생성 및 이메일 전송
    @PostMapping("/sendResetLink")
    @ResponseBody
    public Map<String, String> sendResetLink(@RequestParam String userId, @RequestParam String userEmail) {
        Map<String, String> response = new HashMap<>();
        if (userService.validateUser(userId, userEmail)) {
            String token = jwtTokenProvider.generateToken(userId, "RESET_PASSWORD", 3600000L); // 1시간 유효기간
            String resetLink = "http://localhost:8080/user/userChangePw?token=" + token;
            emailService.sendPasswordResetLink(userEmail, resetLink);
            response.put("message", "비밀번호 변경 링크가 이메일로 전송되었습니다.");
        } else {
            response.put("error", "사용자 정보가 일치하지 않습니다.");
        }
        return response;
    }

    // 비밀번호 변경 창 이동 (비로그인시)
    @GetMapping("/userChangePw")
    public String userChangePw(@RequestParam String token, Model model) {
        if (jwtTokenProvider.validateToken(token) && "RESET_PASSWORD".equals(jwtTokenProvider.getRoleFromToken(token))) {
            String userId = jwtTokenProvider.getUsernameFromToken(token);
            model.addAttribute("userId", userId);
            return "user/account/changePw";
        } else {
            model.addAttribute("errorMessage", "유효하지 않은 링크입니다.");
            return "redirect:/user/login";
        }
    }

    // 비밀번호 변경 (비로그인시)
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String userId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("userId", userId);
            return "user/account/changePw";
        }
        userService.updatePassword(userId, newPassword);
        return "redirect:/user/login";
    }

    // 인증코드 전송
    @PostMapping("/sendVerificationCode")
    @ResponseBody
    public Map<String, String> sendVerificationCode(@RequestParam String email) {
        String verificationCode = emailService.sendEmail(email);
        emailVerificationService.saveVerificationCode(email, verificationCode);
        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 코드가 전송되었습니다.");
        return response;
    }

    // 인증코드 확인
    @PostMapping("/verifyCode")
    @ResponseBody
    public Map<String, Boolean> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = emailVerificationService.verifyCode(email, code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);
        return response;
    }

    // 아이디 중복 확인
    @PostMapping("/checkUserId")
    @ResponseBody
    public boolean checkUserId(@RequestParam String userId) {
        return userService.checkUserIdExists(userId);
    }

    // 이메일 중복 확인
    @PostMapping("/checkUserEmail")
    @ResponseBody
    public boolean checkUserEmail(@RequestParam String email) {
        return userService.checkUserEmailExists(email);
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/user/login";
    }

    // 마이페이지(로그인한 사용자 정보 확인)
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/myPage";
    }

    // 내 정보 확인
    @GetMapping("/checkAccount")
    public String checkAccount(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/checkAccount";
    }

    // 비밀번호 확인
    @PostMapping("/checkPw")
    public String checkPw(@AuthenticationPrincipal User user,@RequestParam String userId, @RequestParam String userPw, Model model) {
        if (userService.checkPw(userId, userPw)) {
            return "redirect:/user/modifyUser";
        } else {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "user/mypage/checkAccount";
        }
    }

    // 내 정보 수정 창 이동
    @GetMapping("/modifyUser")
    public String modifyUser(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/modifyUser";
    }

    // 내 정보 수정
    @PostMapping("/modifyUser")
    public String modifyUser(@ModelAttribute UserDTO userDTO, @AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
        }
        userService.modifyUser(userDTO);
        return "redirect:/user/myPage";
    }

    // 비밀번호 변경 (로그인시)
    @GetMapping("/updatePw")
    public String updatePw(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        model.addAttribute("user", userAccount);
        return "user/mypage/updatePw";
    }

    // 비밀번호 업데이트
    @PostMapping("/updatePassword")
    public String updatePassword(@AuthenticationPrincipal User user, @RequestParam String userId, @RequestParam String userPw, @RequestParam String newPassword, Model model) {
        // 기존 비밀번호와 입력한 비밀번호가 일치하는 지 확인
        if (!passwordEncoder.matches(userPw, userService.findUserById(userId).getUserPw())) {
            UserAccount userAccount = userService.findUserById(user.getUsername());
            model.addAttribute("user", userAccount);
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
            return "user/mypage/updatePw";
        }

        userService.updatePassword(userId, newPassword);

        return "redirect:/user/myPage";
    }

    // 내 주소 관리
    @GetMapping("/myAddress")
    public String myAddress(@AuthenticationPrincipal User user, Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        ArrayList<UserAddress> addressList = userService.userAddressList(userAccount.getUserKey());
        model.addAttribute("user", userAccount);
        model.addAttribute("addressList", addressList);
        return "user/mypage/myAddress";
    }

    // 주소 추가
    @PostMapping("/addAddress")
    @ResponseBody
    public ResponseEntity<?> addAddress(@RequestParam String newAddress, @RequestParam String newAddrDetail, @RequestParam String newZipcode, @AuthenticationPrincipal User user) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        userService.addAddress(userAccount.getUserKey(), newAddress, newAddrDetail, newZipcode);
        return ResponseEntity.ok().build();
    }

    // 주소 수정
    @PostMapping("/modifyAddress")
    @ResponseBody
    public ResponseEntity<?> modifyAddress(@RequestParam int addressKey, @RequestParam String newAddress, @RequestParam String newAddrDetail, @RequestParam String newZipcode) {
        userService.modifyAddress(addressKey, newAddress, newAddrDetail, newZipcode);
        return ResponseEntity.ok().build();
    }

    // 대표 주소 설정
    @PostMapping("/setDefaultAddress")
    @ResponseBody
    public ResponseEntity<?> setDefaultAddress(@RequestParam int addressKey, @AuthenticationPrincipal User user) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        userService.setDefaultAddress(userAccount.getUserKey(), addressKey);
        return ResponseEntity.ok().build();
    }

    // 주소 삭제
    @PostMapping("/deleteAddress")
    @ResponseBody
    public ResponseEntity<?> deleteAddress(@RequestParam int addressKey) {
        userService.deleteAddress(addressKey);
        return ResponseEntity.ok().build();
    }

    // 찜 목록 조회
    @GetMapping("/myLike")
    public String myLike(@AuthenticationPrincipal User user,
                         @RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "pageSize", defaultValue = "12") int pageSize,
                         Model model) {
        UserAccount userAccount = userService.findUserById(user.getUsername());
        int userKey = userAccount.getUserKey();

        // 찜한 가게 목록 가져오기
        ArrayList<StoreInfo> likedStores = userService.getLikedStores(userKey);

        // 각 가게의 추가 정보 (평점, 리뷰 개수 등) 가져오기
        for (StoreInfo store : likedStores) {
            Double averageRating = storeService.getAverageRatingForStore(store.getStoreInfoKey());
            store.setAverageRating(averageRating != null ? averageRating : 0.0);

            int reviewCount = storeService.getReviewCountForStore(store.getStoreInfoKey());
            store.setReviewCount(reviewCount);
        }

        // 페이지네이션 적용
        int totalStores = likedStores.size();
        int totalPages = (int) Math.ceil((double) totalStores / pageSize);
        ArrayList<StoreInfo> storeList = paginateStores(likedStores, page, pageSize);

        // 모델에 데이터 추가
        model.addAttribute("user", userAccount);
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);

        return "user/mypage/myLike";
    }

    // 장바구니 추가
    @PostMapping("/addToCart")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestBody Map<String, Object> cartRequest,
                                         @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            UserAccount userAccount = userService.findUserById(user.getUsername());

            // 문자열로 받은 데이터들을 적절한 타입으로 변환
            int menuKey = Integer.parseInt(cartRequest.get("menuKey").toString());
            int quantity = Integer.parseInt(cartRequest.get("quantity").toString());

            // ArrayList로 변환
            ArrayList<Integer> selectedOptions = new ArrayList<>();
            for (Object option : (ArrayList<?>) cartRequest.get("selectedOptionKeys")) {
                selectedOptions.add(Integer.parseInt(option.toString()));
            }

            // 장바구니에 항목 추가
            cartService.addItemToCart(userAccount.getUserKey(), menuKey, quantity, selectedOptions);

            response.put("success", true);
        } catch (Exception e) {
            log.error("장바구니 추가 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "오류가 발생했습니다. 다시 시도해 주세요.");
        }

        return response;
    }

    // 페이지네이션 처리 메서드
    private ArrayList<StoreInfo> paginateStores(ArrayList<StoreInfo> allStores, int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allStores.size());

        if (fromIndex > toIndex) {
            return new ArrayList<>();
        }

        return new ArrayList<>(allStores.subList(fromIndex, toIndex));
    }
  
}
