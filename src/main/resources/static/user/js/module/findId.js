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

    $("#findIdForm").submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        let userName = $("#userName").val();
        let userEmail = inputEmail.val();

        $.post("/user/findId", { userName: userName, userEmail: userEmail }, function(data) {
            if (data.success) {
                alert("아이디가 이메일로 전송되었습니다.")
            } else {
                alert("입력한 정보와 일치하는 사용자를 찾을 수 없습니다.")
            }
        }).fail(function() {
            alert("아이디 찾기에 실패했습니다.")
        });
    });
});