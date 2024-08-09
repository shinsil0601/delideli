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

$(document).ready(function() {
    $("#sendCodeButton").click(function() {
        var email = $("#userEmail").val();
        $.post("/user/sendVerificationCode", { email: email }, function(data) {
            $("#message").text("인증 코드가 전송되었습니다.");
        }).fail(function() {
            $("#errorMessage").text("인증 코드 전송에 실패했습니다.");
        });
    });

    $("#verifyCodeButton").click(function() {
        var email = $("#userEmail").val();
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

    $("#checkUserIdButton").click(function() {
        var userId = $("#userId").val();
        $("#userIdMessage").text("");  // Clear previous message
        $.post("/user/checkUserId", { userId: userId }, function(data) {
            if (data) {
                $("#userIdMessage").text("사용할 수 없는 아이디입니다.").removeClass('success-message').addClass('error-message');
            } else {
                $("#userIdMessage").text("사용할 수 있는 아이디입니다.").removeClass('error-message').addClass('success-message');
            }
        }).fail(function() {
            $("#userIdMessage").text("아이디 확인에 실패했습니다.");
        });
    });

    $("#checkUserPw").keyup(function() {
        var password = $("#userPw").val();
        var checkPassword = $("#checkUserPw").val();
        if (password !== checkPassword) {
            $("#passwordMessage").text("비밀번호가 일치하지 않습니다.").removeClass('success-message').addClass('error-message');
        } else {
            $("#passwordMessage").text("비밀번호가 일치합니다.").removeClass('error-message').addClass('success-message');
        }
    });

    $("#userEmail").keyup(function () {
        var email = $("#userEmail").val();
        $("#userEmailMessage").text("");
        // 이메일 중복 확인
        $.ajax({
            url: "/user/checkUserEmail",
            type: "POST",
            data: {email: email},
            async: false,
            success: function (data) {
                if (data) {
                    $("#userEmailMessage").text("사용할 수 없는 이메일입니다.").removeClass('success-message').addClass('error-message');
                    isValid = false;
                } else {
                    $("#userEmailMessage").text("사용할 수 있는 이메일입니다.").removeClass('error-message').addClass('success-message');
                }
            },
            error: function () {
                $("#errorMessage").text("이메일 확인에 실패했습니다.");
                isValid = false;
            }
        });
    });

    $("#registerForm").submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var email = $("#userEmail").val();
        var isValid = true;

        $("#userEmailMessage").text("");
        $("#errorMessage").text("");
        $("#message").text("");

        // 폼 제출
        if (isValid) {
            $("#registerForm")[0].submit();
        }
    });
<<<<<<< HEAD
});
=======

    // 프로필 이미지 미리보기
    $("#userProfile").change(function() {
        var input = this;
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $("#profilePreview").attr("src", e.target.result).show();
            }
            reader.readAsDataURL(input.files[0]);
        }
    });
});
>>>>>>> 50262eee1813a5901bf4222c5f2a642f70836d66
