window.onload = function () {
    // 메뉴 그룹 클릭 이벤트 처리
    const menuGroups = document.querySelectorAll(".menu-group h3");

    menuGroups.forEach(function (group) {
        group.addEventListener("click", function () {
            const menuItems = group.nextElementSibling;
            if (menuItems.style.display === "none" || menuItems.style.display === "") {
                menuItems.style.display = "block";
            } else {
                menuItems.style.display = "none";
            }
        });
    });

    // 메뉴 항목 클릭 이벤트 처리
    const menuItems = document.querySelectorAll(".menu-item");

    menuItems.forEach(function (item) {
        item.addEventListener("click", function () {
            const menuKey = item.getAttribute("data-menu-key");
            openMenuDetail(menuKey);
        });
    });

    // 찜하기 및 취소 이벤트 처리
    const wishlistBtn = document.getElementById("wishlist-btn");
    if (wishlistBtn) {
        wishlistBtn.addEventListener("click", function () {
            const storeInfoKey = wishlistBtn.getAttribute("data-store-info-key");
            toggleLike(storeInfoKey);
        });
    }
};

// 모달창 생성 및 사이즈 설정
function openMenuDetail(menuKey) {
    const url = '/user/menuDetail/' + menuKey;
    window.open(url, 'menuDetailWindow', 'width=400,height=600');
}

// 찜하기 및 취소 기능
function toggleLike(storeInfoKey) {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/user/toggleLike", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
        if (xhr.status === 200) {
            const response = xhr.responseText;
            if (response === "not_logged_in") {
                alert("로그인이 필요합니다.");
            } else {
                // 버튼 텍스트를 토글합니다.
                const button = document.getElementById("wishlist-btn");
                button.textContent = button.textContent === "찜하기" ? "찜 취소" : "찜하기";
            }
        }
    };
    xhr.send("storeInfoKey=" + storeInfoKey);
}
