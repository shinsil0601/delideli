$(document).ready(function () {
    function checkFormValidity() {
        const isEIDValid = $("#clientEIDMessage").hasClass("success-message");
        const isClientIdValid = $("#clientIdMessage").hasClass("success-message");
        const isPasswordValid = $("#passwordMessage").hasClass("success-message");
        const isEmailValid = $("#clientEmailMessage").hasClass("success-message");

        if (isEIDValid && isClientIdValid && isPasswordValid && isEmailValid) {
            $("#registerButton").prop("disabled", false);
        } else {
            $("#registerButton").prop("disabled", true);
        }
    }

    $("#sendCodeButton").click(function() {
        var email = $("#clientEmail").val();
        $.post("/client/sendVerificationCode", { email: email }, function(data) {
            $("#message").text("인증 코드가 전송되었습니다.");
        }).fail(function() {
            $("#errorMessage").text("인증 코드 전송에 실패했습니다.");
        });
    });

    $("#verifyCodeButton").click(function() {
        var email = $("#clientEmail").val();
        var code = $("#verificationCode").val();
        $.post("/user/verifyCode", { email: email, code: code }, function(data) {
            if (data.valid) {
                $("#message").text("이메일 인증이 완료되었습니다.");
                $("#registerButton").prop("disabled", false);
            } else {
                $("#errorMessage").text("인증 코드가 일치하지 않습니다.");
            }
        }).fail(function() {
            $("#errorMessage").text("인증 코드 확인에 실패했습니다.");
        });
    });

    $("#checkClientIdButton").click(function () {
        var clientId = $("#clientId").val();
        $("#clientIdMessage").text("");  // 이전 메세지 제거
        $.post("/client/checkClientId", { clientId: clientId }, function (data) {
            if (data) {
                $("#clientIdMessage").text("사용할 수 없는 아이디입니다.").removeClass('success-message').addClass('error-message');
            } else {
                $("#clientIdMessage").text("사용할 수 있는 아이디입니다.").removeClass('error-message').addClass('success-message');
            }
            checkFormValidity();
        }).fail(function () {
            $("#clientIdMessage").text("아이디 확인에 실패했습니다.");
            checkFormValidity();
        });
    });

    $("#checkClientPw").keyup(function () {
        var password = $("#clientPw").val();
        var checkPassword = $("#checkClientPw").val();
        if (password !== checkPassword) {
            $("#passwordMessage").text("비밀번호가 일치하지 않습니다.").removeClass('success-message').addClass('error-message');
        } else {
            $("#passwordMessage").text("비밀번호가 일치합니다.").removeClass('error-message').addClass('success-message');
        }
        checkFormValidity();
    });

    $("#checkEID").click(function () {
        var clientEID = $("#clientEID").val();
        $("#clientEIDMessage").text("");  // 이전 메세지 제거
        $.ajax({
            url: "https://api.odcloud.kr/api/nts-businessman/v1/status",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                b_no: [clientEID]
            }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Infuser " + "XUXRe+FdFMkLRnaf0Lk4O3Y8FtaOkNX6UlFup4nZKTVo/RIHRFFTW56jFW7dGllnJq2XXGUYdSqnr+lBk/5nxw==");
            },
            success: function (response) {
                if (response.data[0].b_stt_cd === "01") {
                    $("#clientEIDMessage").text("유효한 사업자 등록번호입니다.").removeClass('error-message').addClass('success-message');
                } else {
                    $("#clientEIDMessage").text("유효하지 않은 사업자 등록번호입니다.").removeClass('success-message').addClass('error-message');
                }
                checkFormValidity();
            },
            error: function () {
                $("#clientEIDMessage").text("사업자 등록번호 확인에 실패했습니다.");
                checkFormValidity();
            }
        });
    });

    $("#clientEmail").keyup(function () {
        var email = $("#clientEmail").val();
        $("#clientEmailMessage").text("");  // 이전 메세지 제거

        // 이메일 중복 확인
        $.ajax({
            url: "/client/checkClientEmail",
            type: "POST",
            data: { email: email },
            success: function (data) {
                if (data) {
                    $("#clientEmailMessage").text("사용할 수 없는 이메일입니다.").removeClass('success-message').addClass('error-message');
                } else {
                    $("#clientEmailMessage").text("사용할 수 있는 이메일입니다.").removeClass('error-message').addClass('success-message');
                }
                checkFormValidity();
            },
            error: function () {
                $("#clientEmailMessage").text("이메일 확인에 실패했습니다.");
                checkFormValidity();
            }
        });
    });

    $("#registerForm").submit(function (event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var isValid = true;

        $("#clientEmailMessage").text("");
        $("#errorMessage").text("");
        $("#message").text("");

        if (isValid) {
            $("#registerForm")[0].submit();
        }
    });
});