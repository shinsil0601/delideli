function openEditCartItemWindow(cartKey) {
    const url = '/user/editCartItem/' + cartKey;
    window.open(url, 'editCartItemWindow', 'width=400,height=600');
}

function deleteCartItem(cartKey) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch('/user/deleteCartItem', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ cartKey: cartKey })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('삭제에 실패했습니다.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert("삭제되었습니다.");
                    location.reload(); // 페이지 새로고침
                } else {
                    alert("삭제에 실패했습니다.");
                }
            })
            .catch(error => {
                //console.error('Error:', error);
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
            });
    }
}

// 결제 버튼 클릭 처리
function processPayment(storeInfoKey, minOrderAmount) {
    const totalPriceElement = document.getElementById('storeTotalPrice-' + storeInfoKey);

    // 요소가 존재하는지 확인
    if (!totalPriceElement) {
        //console.error('총 금액 요소를 찾을 수 없습니다:', 'storeTotalPrice-' + storeInfoKey);
        return;
    }

    const storeTotalPrice = parseInt(totalPriceElement.textContent.replace('총 금액: ', '').replace('원', '').replace(/,/g, ''));

    if (isNaN(storeTotalPrice)) {
        //console.error('총 금액을 파싱할 수 없습니다:', totalPriceElement.textContent);
        return;
    }

    if (storeTotalPrice < minOrderAmount) {
        alert('최소 주문 금액을 충족하지 못했습니다.');
    } else {
        // 결제 페이지로 이동
        window.location.href = '/user/order?storeInfoKey=' + storeInfoKey;
    }
}

