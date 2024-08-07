$(document).ready(function() {
    $("#sendResetLinkButton").click(function() {
        var userId = $("#userId").val();
        var userEmail = $("#userEmail").val();
        $.post("/user/sendResetLink", { userId: userId, userEmail: userEmail }, function(data) {
            if (data.message) {
                $("#message").text(data.message);
            } else {
                $("#errorMessage").text(data.error);
            }
        }).fail(function() {
            $("#errorMessage").text("비밀번호 변경 링크 전송에 실패했습니다.");
        });
    });
});