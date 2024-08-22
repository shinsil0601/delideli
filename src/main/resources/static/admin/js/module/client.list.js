function toggleAccess(clientKey) {
    const button = document.getElementById('toggle-btn-' + clientKey);
    const statusSpan = document.getElementById('status-' + clientKey);

    if (!statusSpan) {
        console.error(`상태 표시 요소를 찾을 수 없습니다. clientKey: ${clientKey}`);
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/admin/member/clientAccess',
        data: { clientKey: clientKey },
        success: function(response) {
            if (response.status === 'success') {
                if (statusSpan) {
                    if (response.clientAccess === 1) {
                        button.innerText = '승인취소';
                        statusSpan.innerText = '승인완료';
                    } else {
                        button.innerText = '승인';
                        statusSpan.innerText = '승인대기';
                    }
                }
            } else {
                alert('처리 중 오류가 발생했습니다.');
            }
        },
        error: function() {
            alert('서버와 통신 중 오류가 발생했습니다.');
        }
    });
}