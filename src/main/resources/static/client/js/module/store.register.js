function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우
                addr = data.jibunAddress;
            }

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
            }

            document.getElementById('newZipcode').value = data.zonecode;
            document.getElementById("newAddress").value = addr + extraAddr;
            document.getElementById("newAddrDetail").focus();
        }
    }).open();
}

function syncValues(sourceId, targetId) {
    const source = document.getElementById(sourceId);
    const target = document.getElementById(targetId);

    source.addEventListener('input', function() {
        target.value = source.value;
    });
}

window.onload = function() {
    // 주문 금액 1과 그에 대응하는 필드 동기화
    syncValues('order-amount-1', 'order-amount-1-over');
    // 주문 금액 2와 그에 대응하는 필드 동기화
    syncValues('order-amount-2', 'order-amount-2-over');

    // 폼 제출 처리
    document.getElementById("storeRegisterForm").addEventListener("submit", function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 막습니다.

        var formData = new FormData(this);

        fetch('/client/storeRegister', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json()) // JSON 형식으로 응답을 처리
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.href = '/client/storeList'; // 성공 시 리다이렉트
                } else {
                    alert("등록 실패: " + data.message); // 실패 시 메시지 표시
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('서버와의 통신 중 오류가 발생했습니다.');
            });
    });
};
