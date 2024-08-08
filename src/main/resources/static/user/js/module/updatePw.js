$(document).ready(function () {
    $("#checkUserPw").keyup(function() {
        var newPassword = $("#newUserPw").val();
        var checkPassword = $("#checkUserPw").val();
        if (newPassword !== checkPassword) {
            $("#passwordMessage").text("비밀번호가 일치하지 않습니다.").removeClass('success-message').addClass('error-message');
        } else {
            $("#passwordMessage").text("비밀번호가 일치합니다.").removeClass('error-message').addClass('success-message');
        }
    });
});