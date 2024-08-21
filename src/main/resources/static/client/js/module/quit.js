$(document).ready(function() {
    $('#checkPwForm').on('submit', function(e) {
        e.preventDefault(); // 폼의 기본 제출 동작 방지

        // 확인 메시지 표시
        if (confirm('탈퇴하시겠습니까?')) {

            // AJAX로 POST 요청 전송
            $.ajax({
                url: '/client/quit',
                type: 'POST',
                data: $(this).serialize(), // 폼 데이터 전송
                success: function(response) {
                    // 성공적으로 탈퇴 처리된 후의 동작
                    if (response === "success") {
                        window.location.href = '/client/login'; // 로그인 페이지로 리다이렉트
                    } else {
                        alert('비밀번호가 일치하지 않습니다. 다시 시도해주세요.');
                    }
                },
                error: function(xhr, status, error) {
                    // 에러 처리
                    alert('탈퇴 처리 중 문제가 발생했습니다. 다시 시도해주세요.');
                }
            });
        } else {
            // '아니오'를 누른 경우
            alert('탈퇴가 취소되었습니다.');
        }
    });
});
