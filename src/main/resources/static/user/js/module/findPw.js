$(document).ready(function() {
    /* 필드 */
    const inputEmail = $("#userEmail"); // 이메일

    /* 유효성메시지 */
    const userEmailMessage = $("#userEmailMessage");

    inputEmail.keyup(function () {
        let email = inputEmail.val();
        userEmailMessage.text("");

        if (email.includes('@')) {
            userEmailMessage.text("사용할 수 있는 이메일입니다.")
                .removeClass('error-message').addClass('success-message').show();
        } else {
            userEmailMessage.text("사용할 수 없는 이메일입니다.")
                .removeClass('success-message').addClass('error-message').show();
        }
    });

    $("#sendResetLinkButton").click(function() {
        let userId = $("#userId").val();
        let userEmail = inputEmail.val();

        $.post("/user/sendResetLink", { userId: userId, userEmail: userEmail }, function(data) {
            if (data.message) {
                alert(data.message);
            } else {
                alert(data.error);
            }
        }).fail(function() {
            alert("비밀번호 변경 링크 전송에 실패했습니다.")
        });
    });
});