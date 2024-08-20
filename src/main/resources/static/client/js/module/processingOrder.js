function openOrderDetail(button) {
    var orderKey = button.getAttribute("data-orderkey");
    var url = '/client/OrderDetail/' + orderKey;
    window.open(url, '_blank', 'width=600,height=400');
}
function updateOrderStatus(button) {
    const orderKey = $(button).data('orderkey');
    let newStatus = '';

    // 버튼의 텍스트에 따라 새로운 상태 결정
    if ($(button).text().includes('배송 시작')) {
        newStatus = '배달중';
    } else if ($(button).text().includes('픽업 요청')) {
        newStatus = '포장(픽업대기)';
    } else if ($(button).text().includes('배달 완료 처리')) {
        newStatus = '배달 완료';
    } else if ($(button).text().includes('픽업 완료 처리')) {
        newStatus = '픽업 완료';
    }

    // 서버로 상태 업데이트 요청 전송
    $.ajax({
        url: '/client/updateOrderStatus',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            orderKey: orderKey,
            orderMethod: newStatus
        }),
        success: function(response) {
            if (response.status === 'success') {
                alert('주문 상태가 업데이트되었습니다.');
                window.location.reload(); // 페이지 새로고침
            } else {
                alert('주문 상태 업데이트에 실패했습니다.');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error:', error);
            alert('서버 요청 중 오류가 발생했습니다.');
        }
    });
}