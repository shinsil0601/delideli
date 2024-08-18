$(document).ready(function () {
    /* 필드 */
    const inputPw = $("#userPw"); // 비밀번호
    const inputChkPw = $("#checkUserPw"); // 비밀번호 확인

    /* 유효성메시지 */
    const passwordMessage = $("#passwordMessage");

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
    });
});