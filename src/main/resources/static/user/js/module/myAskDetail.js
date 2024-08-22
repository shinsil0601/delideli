function confirmDelete(boardKey) {
    if (confirm('정말로 삭제하시겠습니까?')) {
        // 사용자가 확인을 누른 경우 삭제를 진행
        location.href = '/user/myAskDelete/' + boardKey;
    }
    // 취소를 누른 경우 아무 것도 하지 않음
}