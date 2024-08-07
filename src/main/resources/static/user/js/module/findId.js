$(document).ready(function() {
    $("#findIdForm").submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var userName = $("#userName").val();
        var userEmail = $("#userEmail").val();

        $("#message").text("");
        $("#errorMessage").text("");

        $.post("/user/findId", { userName: userName, userEmail: userEmail }, function(data) {
            if (data.success) {
                $("#message").text("아이디가 이메일로 전송되었습니다.");
            } else {
                $("#errorMessage").text("입력한 정보와 일치하는 사용자를 찾을 수 없습니다.");
            }
        }).fail(function() {
            $("#errorMessage").text("아이디 찾기에 실패했습니다.");
        });
    });
});