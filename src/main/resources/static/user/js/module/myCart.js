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
                console.error('Error:', error);
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
            });
    }
}