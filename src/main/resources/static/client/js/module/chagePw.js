$(document).ready(function () {
    /* 필드 */
    const inputPw = $("#clientPw"); // 비밀번호
    const inputChkPw = $("#checkClientPw"); // 비밀번호 확인

    /* 유효성메시지 */
    const passwordMessage = $("#passwordMessage");

    /* 변경하기 버튼 */
    const changePwButton = $("#registerButton");

    /* 유효성 검사 */
    function validateForm() {
        const allFilled = inputPw.val() && inputChkPw.val();
        const passwordMatch = inputPw.val() === inputChkPw.val();

        if (allFilled && passwordMatch) {
            changePwButton.prop('disabled', false);
        } else {
            changePwButton.prop('disabled', true);
        }
    }

    /* 비밀번호 확인 */
    inputChkPw.keyup(function () {
        const password = inputPw.val();
        const checkPassword = inputChkPw.val();
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

    /* 초기 유효성 검사 호출 */
    validateForm();
});
