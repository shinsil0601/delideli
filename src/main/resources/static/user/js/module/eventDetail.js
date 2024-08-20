$(document).ready(function () {
    getCommentList();
    validateCommentForm();

    // 댓글 작성 폼 검증
    $('textarea[name="commentContents"], input[name="userKey"]').on('input', function () {
        validateCommentForm();
    });
});

function validateCommentForm() {
    const commentContents = $('textarea[name="commentContents"]').val();
    const userKey = $('input[name="userKey"]').val();
    const isFormValid = commentContents.trim() !== "" && userKey !== "";

    $('button[name="comment-submit"]').prop('disabled', !isFormValid);
}

// 댓글 조회
function getCommentList() {
    const userKey = $('input[name=userKey]').val();
    const boardKey = $('input[name=boardKey]').val();
    //console.log(`userKey: ${userKey}`);
    //console.log(`boardKey: ${boardKey}`);
    $.ajax({
        type: 'GET',
        url: '/user/getCommentList',
        data: {
            userKey: userKey,
            boardKey: boardKey
        },
        success: function (result) {
            // console.log("성공시" + result);
            // console.log("userKey: " + userKey + ", boardKey: " + boardKey);
            renderComments(result, null, $("#comment__view"), userKey);
        },
        error: function (xhr, status, error) {
            // console.error("AJAX 요청 실패");
            //console.error("상태: " + status);
            //console.error("오류: " + error);
            //console.error("응답: " + xhr.responseText);
            // console.log("userKey: " + userKey + ", boardKey: " + boardKey);
        },
        complete: function () {
            // console.log("AJAX 요청 완료");
        }
    });
}

function renderComments(comments, parentId, parentElement, currentUserKey) {
    comments.filter(comment => comment.commentParent === parentId).forEach(comment => {
        const commentDiv = $("<div class='comment__item'></div>").appendTo(parentElement);
        const commentContentDiv = $("<div class='comment-content'><strong>" + comment.userNickname + ":</strong><p>" + comment.commentContents + "</p></div>").appendTo(commentDiv);
        const commentBtnDiv = $("<div class='comment-btn__items'></div>").appendTo(commentDiv);

        const replyButton = $("<button type='button' class='cb-comment'>답글</button>").appendTo(commentBtnDiv);

        // 현재 사용자가 댓글 작성자인 경우에만 수정/삭제 버튼을 표시
        if (comment.userKey == currentUserKey) {
            const editButton = $("<button type='button' class='cb-edit'>수정</button>").appendTo(commentBtnDiv);
            const deleteButton = $("<button type='button' class='cb-delete'>삭제</button>").appendTo(commentBtnDiv);
        }

        // 답글 작성 폼
        const replyForm = $("<div class='reply-form' style='display:none; border-top: 1px solid var(--gray-200); margin-top: 1rem; padding-top: 1rem;'></div>").appendTo(commentDiv);
        const replyFormContent = $(`
            <div class="cmt-write__form">
                <textarea class="form-control" rows="3" placeholder="답글을 작성하세요"></textarea>
                <div class="comments-write--btn">
                    <button type="button" class="btn btn-primary reply-submit-btn">작성완료</button>
                </div>
            </div>
        `).appendTo(replyForm);

        replyButton.on("click", function () {
            replyForm.toggle();
        });

        // 답글 버튼 처리
        replyFormContent.find(".reply-submit-btn").on("click", function () {
            const replyContent = replyFormContent.find("textarea").val();
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
                    //console.error("AJAX 요청 실패");
                    //console.error("상태: " + status);
                    //console.error("오류: " + error);
                    //console.error("응답: " + xhr.responseText);
                }
            });
        });

        // 수정 버튼 처리
        if (comment.userKey == currentUserKey) {
            editButton.on("click", function () {
                // 기존에 열려 있는 수정 폼이 있으면 닫기
                $(".edit-form").remove();

                // 수정 폼을 현재 클릭한 댓글 아래에 추가
                const editForm = $("<div class='edit-form' style='border-top: 1px solid var(--gray-200); margin-top: 1rem; padding-top: 1rem;'></div>").appendTo(commentDiv);
                const editFormContent = $(`
                    <p>댓글 수정</p>
                    <div class="cmt-write__form">
                        <textarea class="form-control" rows="3">` + comment.commentContents + `</textarea>
                        <div class="comments-write--btn">
                            <button type="button" class="btn btn-primary edit-submit-btn">저장</button>
                        </div>
                    </div>
                `).appendTo(editForm);

                editFormContent.find(".edit-submit-btn").on("click", function () {
                    const newContent = editFormContent.find("textarea").val();
                    $.ajax({
                        type: 'POST',
                        url: '/user/editComment',
                        data: {
                            commentKey: comment.commentKey,
                            commentContents: newContent
                        },
                        success: function (result) {
                            alert("댓글이 수정되었습니다.");
                            location.reload();
                        },
                        error: function (xhr, status, error) {
                            //console.error("AJAX 요청 실패");
                            //console.error("상태: " + status);
                            //console.error("오류: " + error);
                            //console.error("응답: " + xhr.responseText);
                        }
                    });
                });
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
                            //console.error("AJAX 요청 실패");
                            //console.error("상태: " + status);
                            //console.error("오류: " + error);
                            //console.error("응답: " + xhr.responseText);
                        }
                    });
                }
            });
        }

        // 대댓글 영역
        const repliesDiv = $("<div class='replies' style='margin-left: 2rem; border-top: 1px solid var(--gray-200); margin-top: 1rem; padding-top: 1rem;'></div>").appendTo(commentDiv);
        renderComments(comments, comment.commentKey, repliesDiv, currentUserKey);
    });
}