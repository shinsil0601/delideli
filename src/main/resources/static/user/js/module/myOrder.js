// 탭 전환 함수
function switchTab(tab) {
    const $inProgressTab = $('#in-progress-orders');
    const $completedTab = $('#completed-orders');

    if (tab === 'in-progress') {
        $inProgressTab.show();
        $completedTab.hide();
    } else if (tab === 'completed') {
        $inProgressTab.hide();
        $completedTab.show();
    }
}

// 기본적으로 '진행중' 탭을 활성화
$(document).ready(function() {
    switchTab('in-progress');
});

// 주문 취소로 변경
function cancelOrder(element) {
    const orderKey = $(element).data('order-key'); // 버튼의 data-order-key 속성에서 orderKey를 가져옵니다.
    if (confirm("정말로 주문을 취소하시겠습니까?")) {
        $.ajax({
            url: '/user/cancelOrder',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ orderKey: orderKey }), // JSON 객체로 orderKey를 전송합니다.
            headers: {
                'X-CSRF-Token': $('meta[name="_csrf"]').attr('content')
            },
            success: function(response) {
                alert(response); // 서버에서 받은 메시지를 표시합니다.
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("주문 취소에 실패했습니다. 다시 시도해주세요.");
                //console.error("Error:", status, error); // 디버깅 정보 로그 출력
            }
        });
    }
}