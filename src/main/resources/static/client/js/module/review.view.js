// 리뷰 신고를 처리하는 함수
function reportReview(reviewKey, element) {
    if (confirm("정말 신고하시겠습니까?")) { // 확인 창 표시
        $.ajax({
            type: "POST",
            url: "/client/reviewReport/" + reviewKey,
            success: function(response) {
                if (response.status === "success") {
                    $(element).text("신고 완료");
                    $(element).css("pointer-events", "none"); // 버튼 비활성화
                } else if (response.status === "already_reported") {
                    alert("이미 신고된 리뷰입니다.");
                } else {
                    alert("신고 처리에 실패했습니다. 다시 시도해주세요.");
                }
            },
            error: function(xhr, status, error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });

    }
}
$(document).ready(function() {
    // 수정하기 버튼 클릭 시
    $('.editButton').click(function() {
        var $this = $(this);  // 현재 클릭된 버튼을 참조
        $this.hide();  // 수정하기 버튼 숨기기
        $this.siblings('.commentText').hide();  // 사장님 답변 텍스트 숨기기
        $this.siblings('.editComment').show();  // 텍스트 영역 보이기
        $this.siblings('.saveButton').show();  // 수정 버튼 보이기
    });

    // 수정 버튼 클릭 시
    $('.saveButton').click(function() {
        var $this = $(this);  // 현재 클릭된 버튼을 참조
        var reviewKey = $this.data('review-key');
        var updatedComment = $this.siblings('.editComment').val();

        $.ajax({
            type: 'POST',
            url: '/client/editReview/' + reviewKey,
            data: JSON.stringify({ comment: updatedComment }),
            contentType: 'application/json',
            success: function(response) {
                alert('수정되었습니다.');
                location.reload();  // 페이지를 새로고침하여 업데이트된 내용을 반영합니다.
            },
            error: function(error) {
                alert('수정에 실패했습니다. 다시 시도해 주세요.');
            }
        });
    });

    // 저장 버튼 클릭 시
    $('.saveNewButton').click(function() {
        var $this = $(this);
        var reviewKey = $this.data('review-key');
        var newComment = $this.siblings('.writeComment').val();

        $.ajax({
            type: 'POST',
            url: '/client/saveReview/' + reviewKey,
            data: JSON.stringify({ comment: newComment }),
            contentType: 'application/json',
            success: function(response) {
                alert('저장되었습니다.');
                location.reload();  // 페이지를 새로고침하여 업데이트된 내용을 반영합니다.
            },
            error: function(error) {
                alert('저장에 실패했습니다. 다시 시도해 주세요.');
            }
        });
    });
});
