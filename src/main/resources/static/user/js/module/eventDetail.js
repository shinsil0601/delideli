$(document).ready(function () {
    getCommentList();
    validateCommentForm();

    // 댓글 작성 폼 검증
    $('textarea[name="commentContents"], input[name="userKey"]').on('input', function () {
        validateCommentForm();
    });
});

function validateCommentForm() {
    var commentContents = $('textarea[name="commentContents"]').val();
    var userKey = $('input[name="userKey"]').val();
    var isFormValid = commentContents.trim() !== "" && userKey !== "";

    $('button[name="comment-submit"]').prop('disabled', !isFormValid);
}

// 댓글 조회
function getCommentList() {
    var userKey = $('input[name=userKey]').val();
    var boardKey = $('input[name=boardKey]').val();
    $.ajax({
        type: 'GET',
        url: '/user/getCommentList',
        data: {
            userKey: userKey,
            boardKey: boardKey
        },
        success: function (result) {
            console.log("성공시" + result);
            console.log("userKey: " + userKey + ", boardKey: " + boardKey);
            renderComments(result, null, $("#comment"), userKey);
        },
        error: function (xhr, status, error) {
            console.error("AJAX 요청 실패");
            console.error("상태: " + status);
            console.error("오류: " + error);
            console.error("응답: " + xhr.responseText);
            console.log("userKey: " + userKey + ", boardKey: " + boardKey);
        },
        complete: function () {
            console.log("AJAX 요청 완료");
        }
    });
}

function renderComments(comments, parentId, parentElement, currentUserKey) {
    comments.filter(comment => comment.commentParent === parentId).forEach(comment => {
        var commentDiv = $("<div class='comment'></div>").appendTo(parentElement);
        var commentContentDiv = $("<div class='comment-content'><strong>" + comment.userNickname + ":</strong> " + comment.commentContents + "</div>").appendTo(commentDiv);

        var replyButton = $("<button>답글</button>").appendTo(commentDiv);

        // 현재 사용자가 댓글 작성자인 경우에만 수정/삭제 버튼을 표시
        if (comment.userKey == currentUserKey) {
            var editButton = $("<button>수정</button>").appendTo(commentDiv);
            var deleteButton = $("<button>삭제</button>").appendTo(commentDiv);
        }

        // 답글 작성 폼
        var replyForm = $("<div class='reply-form' style='display:none;'></div>").appendTo(commentDiv);
        var replyTextarea = $("<textarea class='form-control'></textarea>").appendTo(replyForm);
        var replySubmitButton = $("<button>작성완료</button>").appendTo(replyForm);

        replyButton.on("click", function () {
            replyForm.toggle();
        });

        // 답글 버튼 처리
        replySubmitButton.on("click", function () {
            console.log("작성완료키: " + comment.commentKey);
            var replyContent = replyTextarea.val();
            $.ajax({
                type: 'POST',
                url: '/user/insertReplyComment',
                data: {
                    userKey: $('input[name=userKey]').val(),
                    boardKey: $('input[name=boardKey]').val(),
                    commentParent: comment.commentKey,
                    commentContents: replyContent,
                    userNickname: $('input[name=userNickname]').val()
                },
                success: function (result) {
                    alert("답글이 등록되었습니다.");
                    location.reload();
                },
                error: function (xhr, status, error) {
                    console.error("AJAX 요청 실패");
                    console.error("상태: " + status);
                    console.error("오류: " + error);
                    console.error("응답: " + xhr.responseText);
                }
            });
        });

        // 수정 버튼 처리
        if (comment.userKey == currentUserKey) {
            editButton.on("click", function () {
                if ($(this).next(".edit-form").length === 0) {
                    console.log("수정완료키: " + comment.commentKey);
                    var editForm = $("<div class='edit-form'></div>").insertAfter($(this));
                    var editTextarea = $("<textarea class='form-control'></textarea>").val(comment.commentContents).appendTo(editForm);
                    var editSubmitButton = $("<a href='#' class='save-button' data-comment-key='" + comment.commentKey + "'>저장</a>").appendTo(editForm);

                    editSubmitButton.on("click", function (event) {
                        event.preventDefault(); // 기본 동작 막기
                        var newContent = editTextarea.val();
                        var commentKey = $(this).data("comment-key"); // comment_key 값 가져오기
                        $.ajax({
                            type: 'POST',
                            url: '/user/editComment',
                            data: {
                                commentKey: commentKey,
                                commentContents: newContent
                            },
                            success: function (result) {
                                alert("댓글이 수정되었습니다.");
                                location.reload();
                            },
                            error: function (xhr, status, error) {
                                console.error("AJAX 요청 실패");
                                console.error("상태: " + status);
                                console.error("오류: " + error);
                                console.error("응답: " + xhr.responseText);
                            }
                        });
                    });
                }
            });

            // 삭제 버튼 처리
            deleteButton.on("click", function () {
                if (confirm("정말로 댓글을 삭제하시겠습니까?")) {
                    $.ajax({
                        type: 'POST',
                        url: '/user/deleteComment',
                        data: {
                            commentKey: comment.commentKey
                        },
                        success: function (result) {
                            alert("댓글이 삭제되었습니다.");
                            location.reload();
                        },
                        error: function (xhr, status, error) {
                            console.error("AJAX 요청 실패");
                            console.error("상태: " + status);
                            console.error("오류: " + error);
                            console.error("응답: " + xhr.responseText);
                        }
                    });
                }
            });
        }

        commentDiv.append("<hr>");
        // 대댓글 영역
        var repliesDiv = $("<div class='replies' style='margin-left: 20px;'></div>").appendTo(commentDiv); // margin-left를 추가하여 들여쓰기
        renderComments(comments, comment.commentKey, repliesDiv, currentUserKey);
    });
}
