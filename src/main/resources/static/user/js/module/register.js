function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';
            var extraAddr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            if(data.userSelectedType === 'R'){
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
            }

            document.getElementById('userZipcode').value = data.zonecode;
            document.getElementById("userAddress").value = addr + extraAddr;
            document.getElementById("userAddrDetail").focus();
        }
    }).open();
}

$(document).ready(function () {
    /* 필드 */
    const inputId = $("#userId"); // 아이디
    const inputName = $("#userName"); // 이름
    const inputNickname = $("#userNickname"); // 닉네임
    const inputPw = $("#userPw"); // 비밀번호
    const inputChkPw = $("#checkUserPw"); // 비밀번호 확인
    const inputBirth = $("#userBirth"); // 생년월일
    const inputPhone = $("#userPhone"); // 휴대폰번호
    const inputEmail = $("#userEmail"); // 이메일
    const inputEmailCode = $("#verificationCode"); // 인증코드
    const inputZipcode = $("#userZipcode"); // 우편번호
    const inputAddress = $("#userAddress"); // 기본주소
    const inputAddrDetail = $("#userAddrDetail"); // 상세주소
    const inputProfile = $("#userProfile"); // 프로필사진

    /* 유효성메시지 */
    const userIdMessage = $("#userIdMessage");
    const passwordMessage = $("#passwordMessage");
    const userEmailMessage = $("#userEmailMessage");
    const userEmailChkMsg = $("#userEmailChkMsg");

    /* 등록 버튼 */
    const registerButton = $(".account__user-btn--b");

    /* 아이디 중복체크 버튼 */
    const checkIdButton = $("#checkUserIdButton");

    /* 인증코드 전송 버튼 */
    const sendCodeButton = $("#sendCodeButton");

    /* 유효성 검사 */
    function validateForm() {
        const allFilled = inputId.val() && inputName.val() && inputNickname.val() &&
            inputPw.val() && inputChkPw.val() && inputBirth.val() &&
            inputPhone.val() && inputEmail.val() && inputEmailCode.val() &&
            inputZipcode.val() && inputAddress.val() && inputAddrDetail.val();

        const allSuccess = userIdMessage.hasClass('success-message') &&
            passwordMessage.hasClass('success-message') &&
            userEmailMessage.hasClass('success-message') &&
            userEmailChkMsg.hasClass('success-message');

        if (allFilled && allSuccess) {
            registerButton.prop('disabled', false);
        } else {
            registerButton.prop('disabled', true);
        }
    }

    /* 아이디 입력 - 중복확인 버튼 활성화 */
    inputId.on('input', function () {
        if (inputId.val().length > 0) {
            checkIdButton.prop('disabled', false);
        } else {
            checkIdButton.prop('disabled', true);
        }
        validateForm();
    });

    /* 아이디 - 중복조회 */
    checkIdButton.click(function () {
        let userId = inputId.val();
        userIdMessage.text("");

        $.post("/user/checkUserId", { userId: userId }, function (data) {
            if (data) {
                userIdMessage.text("사용할 수 없는 아이디입니다.")
                    .removeClass('success-message').addClass('error-message').show();
            } else {
                userIdMessage.text("사용할 수 있는 아이디입니다.")
                    .removeClass('error-message').addClass('success-message').show();
            }
            validateForm();
        }).fail(function () {
            userIdMessage.text("아이디 확인에 실패했습니다.").show();
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

    /* 생년월일 - 양식 */
    inputBirth.on('input', function () {
        let input = $(this).val().replace(/[^0-9]/g, '');

        if (input.length > 4 && input.length <= 6) {
            input = input.replace(/(\d{4})(\d{2})/, '$1-$2-');
        } else if (input.length > 6) {
            input = input.replace(/(\d{4})(\d{2})(\d+)/, '$1-$2-$3');
        }

        $(this).val(input);
        validateForm();
    });

    /* 휴대폰번호 - 양식 (010-1111-2222) */
    inputPhone.on('input', function () {
        let input = $(this).val().replace(/[^0-9]/g, '');

        if (input.length > 3 && input.length <= 7) {
            input = input.replace(/(\d{3})(\d+)/, '$1-$2');
        } else if (input.length > 7) {
            input = input.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
        }

        $(this).val(input);
        validateForm();
    });

    /* 이메일 - 양식 및 중복조회 */
    inputEmail.keyup(function () {
        let email = inputEmail.val();
        userEmailMessage.text("");

        if (email.includes('@')) {
            sendCodeButton.prop('disabled', false);
            userEmailMessage.text("사용할 수 있는 이메일입니다.")
                .removeClass('error-message').addClass('success-message').show();

            $.ajax({
                url: "/user/checkUserEmail",
                type: "POST",
                data: { email: email },
                success: function (data) {
                    if (data) {
                        userEmailMessage.text("사용할 수 없는 이메일입니다.")
                            .removeClass('success-message').addClass('error-message').show();
                        sendCodeButton.prop('disabled', true);
                    } else {
                        userEmailMessage.text("사용할 수 있는 이메일입니다.")
                            .removeClass('error-message').addClass('success-message').show();
                    }
                    validateForm();
                },
                error: function () {
                    userEmailMessage.text("이메일 확인에 실패했습니다.");
                    validateForm();
                }
            });
        } else {
            sendCodeButton.prop('disabled', true);
            userEmailMessage.text("사용할 수 없는 이메일입니다.")
                .removeClass('success-message').addClass('error-message').show();
            validateForm();
        }
    });

    /* 이메일 - 인증코드 전송 */
    sendCodeButton.click(function () {
        let email = inputEmail.val();

        $.post("/user/sendVerificationCode", { email: email }, function (data) {
            alert("인증코드가 전송되었습니다.");
            validateForm();
        }).fail(function () {
            alert("인증코드 전송 실패");
            validateForm();
        });
    });

    /* 이메일 - 인증코드 확인 */
    $("#verifyCodeButton").click(function () {
        let email = inputEmail.val();
        let code = inputEmailCode.val();
        userEmailChkMsg.text("");

        $.post("/user/verifyCode", { email: email, code: code }, function (data) {
            if (data.valid) {
                userEmailChkMsg.text("이메일 인증이 완료되었습니다.")
                    .removeClass('error-message').addClass('success-message').show();
            } else {
                userEmailChkMsg.text("인증 코드가 일치하지 않습니다.")
                    .removeClass('success-message').addClass('error-message').show();
            }
            validateForm();
        }).fail(function () {
            userEmailChkMsg.text("인증 코드 확인에 실패했습니다.")
                .removeClass('success-message').addClass('error-message').show();
            validateForm();
        });
    });

    /* 우편번호 검색 */
    $("#userZipcode").on('click', function () {
        execDaumPostcode();
    });

    /* 프로필사진 - 업로드 및 미리보기 */
    inputProfile.on('change', function () {
        const fileName = inputProfile.val();
        $(".regUpload-file").val(fileName);

        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#profilePreview').css('background-image', `url(${e.target.result})`);
            }
            reader.readAsDataURL(file);
        } else {
            $('#profilePreview').css('background-image', `url('/user/images/profile-default.png')`);
        }
        validateForm();
    });

    /* 초기 유효성 검사 호출 */
    validateForm();
});


