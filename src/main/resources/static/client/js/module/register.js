$(document).ready(function () {
    /* 필드 */
    const inputEID = $("#clientEID"); // 사업자등록번호
    const inputName = $("#clientName"); // 이름
    const inputPhone = $("#clientPhone"); // 휴대폰번호
    const inputId = $("#clientId"); // 아이디
    const inputPw = $("#clientPw"); // 비밀번호
    const inputChkPw = $("#checkClientPw"); // 비밀번호확인
    const inputEmail = $("#clientEmail"); // 이메일
    const inputEmailCode = $("#verificationCode"); // 인증코드
    const inputBank = $("#bankName"); // 은행명
    const inputBankAccount = $("#bankAccount"); // 계좌번호

    /* 유효성메시지 */
    const clientEIDMessage = $("#clientEIDMessage");
    const clientIdMessage = $("#clientIdMessage");
    const passwordMessage = $("#passwordMessage");
    const clientEmailMessage = $("#clientEmailMessage");
    const clientEmailChkMsg = $("#clientEmailChkMsg");

    /* 등록 버튼 */
    const registerButton = $("#registerButton");

    /* 아이디 중복체크 버튼 */
    const checkIdButton = $("#checkClientIdButton");

    /* 유효성 검사 */
    function validateForm() {
        const allFilled = inputEID.val() && inputName.val() && inputPhone.val() &&
            inputId.val() && inputPw.val() && inputChkPw.val() &&
            inputEmail.val() && inputEmailCode.val() &&
            inputBank.val() && inputBankAccount.val();

        const allSuccess = clientEIDMessage.hasClass('success-message') &&
            clientIdMessage.hasClass('success-message') &&
            passwordMessage.hasClass('success-message') &&
            clientEmailMessage.hasClass('success-message') &&
            clientEmailChkMsg.hasClass('success-message');

        if (allFilled && allSuccess) {
            registerButton.prop('disabled', false);
        } else {
            registerButton.prop('disabled', true);
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

    /* 휴대폰번호 - 양식 */
    inputPhone.on('input', function(){
        let input = $(this).val().replace(/[^0-9]/g, '');

        if (input.length > 3 && input.length <= 7) {
            input = input.replace(/(\d{3})(\d+)/, '$1-$2');
        } else if (input.length > 7) {
            input = input.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
        }

        $(this).val(input);
        validateForm();
    });

    /* 아이디 - 버튼활성화 */
    inputId.on('input', function() {
        let clientId = $(this).val();

        if (clientId.length > 0) {
            checkIdButton.prop('disabled', false);
        } else {
            checkIdButton.prop('disabled', true);
        }
        validateForm();
    });

    /* 아이디 - 중복조회 */
    checkIdButton.click(function () {
        let clientId = inputId.val();
        clientIdMessage.text("");

        $.post("/client/checkClientId", { clientId: clientId }, function (data) {
            if (data) {
                clientIdMessage.text("사용할 수 없는 아이디입니다.")
                    .removeClass('success-message').addClass('error-message').show();
            } else {
                clientIdMessage.text("사용할 수 있는 아이디입니다.")
                    .removeClass('error-message').addClass('success-message').show();
            }
            validateForm();
        }).fail(function () {
            clientIdMessage.text("아이디 확인에 실패했습니다.").show();
            validateForm();
        });
    });

    /* 비밀번호 - 비밀번호확인 */
    inputChkPw.keyup(function () {
        let password = inputPw.val();
        let checkPassword = inputChkPw.val();
        passwordMessage.text("");

        if (password !== checkPassword) {
            passwordMessage.text("비밀번호가 일치하지 않습니다.")
                .removeClass('success-message').addClass('error-message').show();
        } else {
            passwordMessage.text("비밀번호가 일치합니다.")
                .removeClass('error-message').addClass('success-message').show();
        }
        validateForm();
    });

    /* 이메일 - 양식 */
    inputEmail.keyup(function () {
        let email = inputEmail.val();
        clientEmailMessage.text("");

        if (email.includes('@')) {
            sendCodeButton.prop('disabled', false);
            clientEmailMessage.text("사용할 수 있는 이메일입니다.")
                .removeClass('error-message').addClass('success-message').show();

            $.ajax({
                url: "/client/checkClientEmail",
                type: "POST",
                data: { email: email },
                success: function (data) {
                    if (data) {
                        clientEmailMessage.text("사용할 수 없는 이메일입니다.")
                            .removeClass('success-message').addClass('error-message').show();
                        sendCodeButton.prop('disabled', true);
                    } else {
                        clientEmailMessage.text("사용할 수 있는 이메일입니다.")
                            .removeClass('error-message').addClass('success-message').show();
                    }
                    validateForm();
                },
                error: function () {
                    clientEmailMessage.text("이메일 확인에 실패했습니다.");
                    validateForm();
                }
            });
        } else {
            sendCodeButton.prop('disabled', true);
            clientEmailMessage.text("사용할 수 없는 이메일입니다.")
                .removeClass('success-message').addClass('error-message').show();
            validateForm();
        }
    });

    /* 이메일 - 인증코드 */
    const sendCodeButton = $("#sendCodeButton");
    sendCodeButton.click(function() {
        let email = inputEmail.val();

        $.post("/client/sendVerificationCode", { email: email }, function(data) {
            alert("인증코드가 전송되었습니다.");
            validateForm();
        }).fail(function() {
            alert("인증코드 전송 실패");
            validateForm();
        });
    });

    /* 이메일 - 인증코드확인 */
    $("#verifyCodeButton").click(function() {
        let email = inputEmail.val();
        let code = inputEmailCode.val();
        clientEmailChkMsg.text("");

        $.post("/user/verifyCode", { email: email, code: code }, function(data) {
            if (data.valid) {
                clientEmailChkMsg.text("이메일 인증이 완료되었습니다.")
                    .removeClass('error-message').addClass('success-message').show();
            } else {
                clientEmailChkMsg.text("인증 코드가 일치하지 않습니다.")
                    .removeClass('success-message').addClass('error-message').show();
            }
            validateForm();
        }).fail(function () {
            clientEmailChkMsg.text("인증 코드 확인에 실패했습니다.")
                .removeClass('success-message').addClass('error-message').show();
            validateForm();
        });
    });

    /* 은행명 */
    inputBank.on('input', function(){
        validateForm();
    });

    /* 계좌번호 */
    inputBankAccount.on('input', function(){
        validateForm();
    });

    /* 초기 유효성 검사 호출 */
    validateForm();
});
