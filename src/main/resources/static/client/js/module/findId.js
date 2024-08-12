$(document).ready(function () {
    /* 필드 */
    const inputEID = $("#clientEID"); // 사업자등록번호
    const inputName = $("#clientName"); // 이름

    /* 유효성메시지 */
    const clientEIDMessage = $("#clientEIDMessage");

    /* 아이디 찾기 버튼 */
    const findIdButton = $("#findIdButton");

    /* 유효성 검사 */
    function validateForm() {
        const allFilled = inputEID.val() && inputName.val();
        const allSuccess = clientEIDMessage.hasClass('success-message');

        if (allFilled && allSuccess) {
            findIdButton.prop('disabled', false);
        } else {
            findIdButton.prop('disabled', true);
        }
    }

    /* 사업자번호 - 양식 */
    inputEID.on('input', function(){
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
        validateForm();
    });

    /* api를 이용한 사업자번호 조회 */
    $("#checkEID").click(function () {
        let clientEID = inputEID.val();
        clientEIDMessage.text("");

        $.ajax({
            url: "https://api.odcloud.kr/api/nts-businessman/v1/status",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                b_no: [clientEID]
            }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Infuser XUXRe+FdFMkLRnaf0Lk4O3Y8FtaOkNX6UlFup4nZKTVo/RIHRFFTW56jFW7dGllnJq2XXGUYdSqnr+lBk/5nxw==");
            },
            success: function (response) {
                if (response.data[0].b_stt_cd === "01") {
                    clientEIDMessage.text("유효한 사업자 등록번호입니다.")
                        .removeClass('error-message').addClass('success-message').show();
                    validateForm(); // 유효성 검사 업데이트
                } else {
                    clientEIDMessage.text("유효하지 않은 사업자 등록번호입니다.")
                        .removeClass('success-message').addClass('error-message').show();
                    validateForm();
                }
            },
            error: function () {
                clientEIDMessage.text("사업자 등록번호 확인에 실패했습니다.").show();
                validateForm();
            }
        });
    });

    /* 폼 제출 */
    $("#findIdForm").submit(function(event) {
        event.preventDefault();

        const clientEID = inputEID.val();
        const clientName = inputName.val();

        $.post("/client/findId", { clientEID: clientEID, clientName: clientName }, function(data) {
            if (data.success) {
                alert("아이디가 이메일로 전송되었습니다.");
            } else {
                alert("입력한 정보와 일치하는 사용자를 찾을 수 없습니다.");
            }
        }).fail(function() {
            alert("아이디 찾기에 실패했습니다.");
        });
    });

    /* 초기 유효성 검사 호출 */
    validateForm();
});
