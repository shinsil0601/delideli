$(document).ready(function() {
    // 필수 여부 라디오 버튼 상태에 따라 최소 선택 옵션 수 입력 필드 제어
    $('input[name="required"]').change(function() {
        if ($('#requiredYes').is(':checked')) {
            $('#minSelectOption').prop('disabled', false); // 필수 선택인 경우, 입력 필드를 활성화
            $('#minSelectOption').val(1); // 기본값 1로 설정
        } else {
            $('#minSelectOption').prop('disabled', true); // 필수 선택이 아닌 경우, 입력 필드를 비활성화
            $('#minSelectOption').val(0); // 값을 0으로 고정
        }
    });

    // 페이지 로드 시 필수 여부가 "아니요"로 설정된 경우 최소 선택 옵션 수를 0으로 설정
    if (!$('#requiredYes').is(':checked')) {
        $('#minSelectOption').prop('disabled', true);
        $('#minSelectOption').val(0);
    }

    // 폼 제출 처리
    $('#saveOptionGroup').click(function() {
        $.ajax({
            type: 'POST',
            url: '/client/updateOptionGroup', // 실제 서버 URL에 맞게 조정
            contentType: 'application/json',
            data: JSON.stringify({
                optionGroupKey: $('#optionGroupKey').val(),
                optionGroupName: $('#optionGroupName').val(),
                required: $('input[name="required"]:checked').val(), // 선택된 필수 여부 값을 가져옴
                minSelectOption: $('#minSelectOption').val(),
                maxSelectOption: $('#maxSelectOption').val()
            }),
            success: function(response) {
                alert('옵션 그룹이 성공적으로 수정되었습니다.');
                window.opener.location.reload(); // 부모 창 새로고침
                window.close(); // 현재 창 닫기
            },
            error: function(xhr, status, error) {
                alert('옵션 그룹 수정 중 오류가 발생했습니다.');
                console.error(error);
            }
        });
    });
});
