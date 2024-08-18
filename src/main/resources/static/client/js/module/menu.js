$(document).ready(function() {
    // 상태 변경 버튼
    $(".status-btn").click(function() {
        var menuKey = $(this).data("menukey");
        var status = $(this).data("status");

        // Ajax 호출을 통해 서버에 상태 업데이트 요청
        $.ajax({
            url: '/client/menu/updateStatus',  // 상태 업데이트를 처리할 경로
            method: 'POST',
            data: {
                menuKey: menuKey,
                status: status
            },
            success: function(response) {
                // 성공적으로 상태가 업데이트된 경우 버튼 스타일 업데이트
                $(".status-btn[data-menukey='" + menuKey + "']").removeClass("active");
                $(".status-btn[data-menukey='" + menuKey + "'][data-status='" + status + "']").addClass("active");
                location.reload();
            },
            error: function(xhr, status, error) {
                // 오류가 발생한 경우 오류 메시지 출력
                alert("상태 변경에 실패했습니다.");
            }
        });
    });

    // 메뉴 삭제 버튼 클릭 처리
    $(".delete-menu-btn").click(function() {
        if (confirm("이 메뉴를 삭제하시겠습니까?")) {
            var menuKey = $(this).data("menukey");

            $.ajax({
                url: '/client/menu/delete',
                method: 'POST',
                data: {
                    menuKey: menuKey
                },
                success: function(response) {
                    location.reload();
                },
                error: function(xhr, status, error) {
                    alert("메뉴 삭제에 실패했습니다.");
                }
            });
        }
    });

    // 메뉴 그룹 삭제 버튼 클릭 처리
    $(".delete-group-btn").click(function() {
        if (confirm("이 메뉴 그룹과 해당 그룹의 모든 메뉴를 삭제하시겠습니까?")) {
            var menuGroupKey = $(this).data("groupkey");

            $.ajax({
                url: '/client/menu/deleteGroup',
                method: 'POST',
                data: {
                    menuGroupKey: menuGroupKey
                },
                success: function(response) {
                    location.reload();
                },
                error: function(xhr, status, error) {
                    alert("메뉴 그룹 삭제에 실패했습니다.");
                }
            });
        }
    });
});
