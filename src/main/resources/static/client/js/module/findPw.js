var isClientEIDValid = false; // 사업자 등록번호 유효성 변수

$(document).ready(function() {
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
                    isClientEIDValid = true;
                } else {
                    $("#clientEIDMessage").text("유효하지 않은 사업자 등록번호입니다.").removeClass('success-message').addClass('error-message');
                    isClientEIDValid = false;
                }
                findPwFormValidity();
            },
            error: function () {
                $("#clientEIDMessage").text("사업자 등록번호 확인에 실패했습니다.");
                isClientEIDValid = false;
                findPwFormValidity();
            }
        });
    });

    function findPwFormValidity() {
        if (isClientEIDValid) {
            $("#sendResetLinkButton").prop("disabled", false);
        } else {
            $("#sendResetLinkButton").prop("disabled", true);
        }
    }

    $("#sendResetLinkButton").click(function() {
        var clientId = $("#clientId").val();
        var clientEID = $("#clientEID").val();
        var clientName = $("#clientName").val();
        $.post("/client/sendResetLink", { clientId: clientId, clientEID: clientEID, clientName: clientName }, function(data) {
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