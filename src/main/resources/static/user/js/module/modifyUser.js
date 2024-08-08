$(document).ready(function() {
    // 이메일 인증 코드 전송
    $("#sendCodeButton").click(function() {
        var email = $("#userEmail").val();
        $.post("/user/sendVerificationCode", { email: email }, function(data) {
            $("#message").text("인증 코드가 전송되었습니다.");
        }).fail(function() {
            $("#errorMessage").text("인증 코드 전송에 실패했습니다.");
        });
    });

    // 이메일 인증 코드 확인
    $("#verifyCodeButton").click(function() {
        var email = $("#userEmail").val();
        var code = $("#verificationCode").val();
        $.post("/user/verifyCode", { email: email, code: code }, function(data) {
            if (data.valid) {
                $("#message").text("이메일 인증이 완료되었습니다.");
                $("#ModifyButton").prop("disabled", false);
            } else {
                $("#errorMessage").text("인증 코드가 일치하지 않습니다.");
            }
        }).fail(function() {
            $("#errorMessage").text("인증 코드 확인에 실패했습니다.");
        });
    });

    // 이메일 중복 확인 및 인증 코드 전송/확인 비활성화 처리
    $("#userEmail").keyup(function () {
        var email = $("#userEmail").val();
        var currentUserEmail = $("#currentUserEmail").val();

        if (email === currentUserEmail) {
            $("#userEmailMessage").text("");
            $("#sendCodeButton").prop("disabled", true).hide();
            $("#verifyCodeButton").prop("disabled", true).hide();
            $("#verificationCode").hide();
            $("label[for='verificationCode']").hide();
            $("#ModifyButton").prop("disabled", false);
        } else {
            $("#sendCodeButton").prop("disabled", false).show();
            $("#verifyCodeButton").prop("disabled", false).show();
            $("#verificationCode").show();
            $("label[for='verificationCode']").show();
            $("#ModifyButton").prop("disabled", true);

            $("#userEmailMessage").text("");
            $.ajax({
                url: "/user/checkUserEmail",
                type: "POST",
                data: { email: email },
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
        }
    });

    // 프로필 이미지 미리보기
    $("#userProfile").change(function() {
        var input = this;
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $("#profilePreview").attr("src", e.target.result);
            }
            reader.readAsDataURL(input.files[0]);
        }
    });

    // 폼 제출
    $("#modifyUserForm").submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var email = $("#userEmail").val();
        var currentUserEmail = $("#currentUserEmail").val();
        var isValid = true;

        $("#userEmailMessage").text("");
        $("#errorMessage").text("");
        $("#message").text("");

        // 현재 이메일과 동일하면 바로 제출, 다르면 인증 확인 후 제출
        if (email === currentUserEmail || $("#ModifyButton").prop("disabled") === false) {
            $("#modifyUserForm")[0].submit();
        } else {
            $("#errorMessage").text("이메일 인증을 완료해주세요.");
        }
    });

    // 페이지 로드 시 현재 이메일과 입력된 이메일이 동일한지 체크
    $("#userEmail").keyup();
});
