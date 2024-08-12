document.addEventListener("DOMContentLoaded", function () {
    const quantityInput = document.getElementById("quantity");
    const totalPriceElement = document.getElementById("totalPrice");
    const basePriceElement = document.getElementById("basePrice");
    const addToCartButton = document.getElementById("addToCartButton");  // 장바구니 버튼 선택

    // 로그인 상태 체크 (HTML data- 속성에서 읽어옴)
    const menuDetailContainer = document.getElementById("menuDetailContainer");
    const isLoggedIn = menuDetailContainer.getAttribute("data-is-logged-in") === "true";
    const menuKey = menuDetailContainer.getAttribute("data-menu-key");

    // 기본 가격을 dataset에서 가져오는데 문제가 있다면 getAttribute를 사용
    const basePrice = parseInt(basePriceElement.getAttribute('data-price')) || 0;

    const optionCheckboxes = document.querySelectorAll(".option-checkbox");

    // 초기 총 금액 설정
    updateTotalPrice();

    // 수량 변경 시 총 금액 업데이트
    quantityInput.addEventListener("change", updateTotalPrice);

    // 옵션 선택 시 총 금액 업데이트
    optionCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", updateTotalPrice);
    });

    function updateTotalPrice() {
        let quantity = parseInt(quantityInput.value) || 1; // 수량이 NaN일 경우 1로 기본 설정
        let totalPrice = basePrice * quantity; // 메뉴 가격에 수량을 곱함

        optionCheckboxes.forEach(function (checkbox) {
            if (checkbox.checked) {
                totalPrice += (parseInt(checkbox.dataset.price) || 0); // 선택된 옵션 가격 더하기
            }
        });

        totalPriceElement.textContent = totalPrice + '원';
    }

    // 옵션 그룹명 클릭 시 접이식 처리
    const optionGroupHeaders = document.querySelectorAll(".option-group-header");
    optionGroupHeaders.forEach(function (header) {
        header.addEventListener("click", function () {
            const options = header.nextElementSibling;
            if (options.style.display === "none" || options.style.display === "") {
                options.style.display = "block";
            } else {
                options.style.display = "none";
            }
        });
    });

    // 장바구니 버튼 클릭 시 로그인 여부 확인 및 장바구니에 추가
    addToCartButton.addEventListener("click", function () {
        if (!isLoggedIn) {
            // 비로그인 시
            alert("로그인 후 이용해 주세요.");
            return;
        }

        // 옵션 및 수량 정보 가져오기
        const selectedOptions = [];
        optionCheckboxes.forEach(function (checkbox) {
            if (checkbox.checked) {
                selectedOptions.push(parseInt(checkbox.value));
            }
        });

        const quantity = parseInt(quantityInput.value) || 1;

        // 장바구니에 추가 요청
        addToCart(menuKey, quantity, selectedOptions);
    });

    function addToCart(menuKey, quantity, selectedOptions) {
        // AJAX 요청을 사용하여 서버에 장바구니 추가 요청을 보냄
        fetch('/user/addToCart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                menuKey: menuKey,
                quantity: quantity,
                selectedOptionKeys: selectedOptions
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("장바구니에 추가되었습니다.");
                    window.close(); // 창 닫기
                } else {
                    alert("장바구니 추가에 실패했습니다. 다시 시도해 주세요.");
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
            });
    }
});
