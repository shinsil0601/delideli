$(function () {
    IMP.init("imp66848612");
});

function checkform() {
    var selectedAmount = document.querySelector('select[name="userMoney"]').value;
    var userKey = document.getElementById('userKey').textContent;
    var userName = document.getElementById('userName').textContent;

    IMP.request_pay({
        pg: "html5_inicis",
        pay_method: "card",
        merchant_uid: "p_" + new Date().getTime(), // 주문 고유 번호
        name: "delideli.co",
        amount: selectedAmount,
        buyer_name: userName,
        buyer_email: "gildong@gmail.com",
    }, function (resp) {
        console.log(resp);
        if (resp.success) {
            $.ajax({
                url: "/user/charge",
                type: "post",
                data: {
                    amount: resp.paid_amount,
                    userKey: userKey
                },
                success: function (result) {
                    console.log(result);
                    console.log("성공콘솔 " + result.success);
                    if (result.success) {
                        // 결제 성공 시 사용자 포인트를 업데이트
                        var currentPointElement = document.getElementById('userPoint');
                        var currentPointText = currentPointElement.textContent.replace('원', '');
                        var currentPoint = parseInt(currentPointText.replace(/,/g, ''), 10);
                        var newPoint = currentPoint + resp.paid_amount;
                        currentPointElement.textContent = newPoint.toLocaleString() + '원';
                        alert("적립금 충전이 완료되었습니다.");
                    } else {
                        alert("적립금 결제 중 오류가 생겼습니다.");
                    }
                },
                error: function (xhr, status, error) {
                    console.log(status);
                    alert("결제 처리 중 오류가 발생했습니다: " + error);
                }
            });
        } else {
            alert('결제에 실패했습니다.');
        }
    });
}