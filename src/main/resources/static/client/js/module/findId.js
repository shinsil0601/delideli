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
                findIdFormValidity();
            },
            error: function () {
                $("#clientEIDMessage").text("사업자 등록번호 확인에 실패했습니다.");
                isClientEIDValid = false;
                findIdFormValidity();
            }
        });
    });

    function findIdFormValidity() {
        if (isClientEIDValid) {
            $("#findIdButton").prop("disabled", false);
        } else {
            $("#findIdButton").prop("disabled", true);
        }
    }

    $("#findIdForm").submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var clientEID = $("#clientEID").val(); // 수정된 부분
        var clientName = $("#clientName").val();

        $("#message").text("");
        $("#errorMessage").text("");

        $.post("/client/findId", { clientEID: clientEID, clientName: clientName}, function(data) {
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