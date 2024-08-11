$(document).ready(function () {
    /* 필드 */
    const inputId = $("#clientId"); // 아이디
    const inputEID = $("#clientEID"); // 사업자등록번호
    const inputName = $("#clientName"); // 이름

    /* 유효성메시지 */
    const clientEIDMessage = $("#clientEIDMessage");

    /* 비밀번호 찾기 버튼 */
    const sendResetLinkButton = $("#sendResetLinkButton");

    /* 유효성 검사 */
    function validateForm() {
        const allFilled = inputId.val() && inputEID.val() && inputName.val();
        const allSuccess = clientEIDMessage.hasClass('success-message');

        if (allFilled && allSuccess) {
            sendResetLinkButton.prop('disabled', false);
        } else {
            sendResetLinkButton.prop('disabled', true);
        }
    }

    /* 사업자번호 입력 */
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
                } else {
                    clientEIDMessage.text("유효하지 않은 사업자 등록번호입니다.")
                        .removeClass('success-message').addClass('error-message').show();
                }
                validateForm();
            },
            error: function () {
                clientEIDMessage.text("사업자 등록번호 확인에 실패했습니다.").show();
                validateForm();
            }
        });
    });

    /* 폼 제출 */
    $("#findPwForm").submit(function(event) {
        event.preventDefault();

        const clientId = inputId.val();
        const clientEID = inputEID.val();
        const clientName = inputName.val();

        $.post("/client/sendResetLink", { clientId: clientId, clientEID: clientEID, clientName: clientName }, function(data) {
            if (data.message) {
                alert(data.message);
            } else {
                alert(data.error);
            }
        }).fail(function() {
            alert("비밀번호 변경 링크 전송에 실패했습니다.");
        });
    });

    /* 초기 유효성 검사 호출 */
    validateForm();
});
