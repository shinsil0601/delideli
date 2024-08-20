// 배달 예상 시간을 조정하는 함수
function adjustTime(value) {
    const timeDisplay = document.getElementById('deliveryTime');
    let currentTime = parseInt(timeDisplay.textContent);
    let newTime = currentTime + value;

    // 시간은 최소 10분까지, 그 이상은 제한 없음
    if (newTime >= 10) {
        timeDisplay.textContent = newTime + '분';
    }
}

$(document).ready(function() {
    // "접수하기" 버튼 클릭 이벤트
    $('#acceptButton').click(function() {
        const orderKey = $(this).data('order-key');
        const estimatedTime = $('#deliveryTime').text().replace('분', '');

        $.ajax({
            type: "POST",
            url: "/client/acceptOrder",
            data: {
                orderKey: orderKey,
                estimatedTime: estimatedTime
            },
            success: function(response) {
                if (response === "success") {
                    alert("주문이 접수되었습니다.");
                    window.close();
                    window.opener.location.reload();
                } else {
                    alert("주문 접수에 실패했습니다.");
                }
            },
            error: function() {
                alert("서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.");
            }
        });
    });

    // "거절하기" 버튼 클릭 이벤트
    $('#rejectButton').click(function() {
        const orderKey = $(this).data('order-key');

        $.ajax({
            type: "POST",
            url: "/client/rejectOrder",
            data: {
                orderKey: orderKey
            },
            success: function(response) {
                if (response === "success") {
                    alert("주문이 거절되었습니다.");
                    window.close();
                    window.opener.location.reload();
                } else {
                    alert("주문 거절에 실패했습니다.");
                }
            },
            error: function() {
                alert("서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.");
            }
        });
    });
});