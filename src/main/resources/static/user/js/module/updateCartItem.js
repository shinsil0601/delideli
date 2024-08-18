document.addEventListener("DOMContentLoaded", function () {
    const quantityInput = document.getElementById("quantity");
    const totalPriceElement = document.getElementById("totalPrice");
    const basePriceElement = document.getElementById("basePrice");
    const updateCartItemButton = document.getElementById("updateCartItemButton");

    const basePrice = parseInt(basePriceElement.getAttribute('data-price')) || 0;
    const optionCheckboxes = document.querySelectorAll(".option-checkbox");

    updateTotalPrice();

    quantityInput.addEventListener("change", updateTotalPrice);
    optionCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", updateTotalPrice);
    });

    function updateTotalPrice() {
        let quantity = parseInt(quantityInput.value) || 1;
        let totalPrice = basePrice * quantity;

        optionCheckboxes.forEach(function (checkbox) {
            if (checkbox.checked) {
                totalPrice += (parseInt(checkbox.dataset.price) || 0);
            }
        });

        totalPriceElement.textContent = totalPrice + '원';
    }

    updateCartItemButton.addEventListener("click", function () {
        const selectedOptions = [];
        optionCheckboxes.forEach(function (checkbox) {
            if (checkbox.checked) {
                selectedOptions.push(parseInt(checkbox.value));
            }
        });

        const quantity = parseInt(quantityInput.value) || 1;
        updateCartItem(quantity, selectedOptions);
    });

    function updateCartItem(quantity, selectedOptions) {
        const cartKey = document.getElementById("menuDetailContainer").getAttribute("data-menu-key");

        fetch('/user/updateCartItem', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                cartKey: cartKey,
                quantity: quantity,
                selectedOptionKeys: selectedOptions
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server error: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert("장바구니가 성공적으로 업데이트되었습니다.");
                    window.opener.location.reload(); // 부모 창 새로고침
                    window.close(); // 팝업 창 닫기
                } else {
                    alert("장바구니 업데이트에 실패했습니다. 다시 시도해 주세요.");
                }
            })
            .catch(error => {
               // console.error('Error:', error);
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
            });
    }
});
