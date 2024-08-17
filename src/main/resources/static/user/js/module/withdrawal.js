function confirmDeletion(event) {
    event.preventDefault();

    const userPwInput = document.getElementById('userPw').value;
    const serverPw = document.getElementById('serverPw').value;

    // 비밀번호 비교
    if (userPwInput === serverPw) {
        // 비밀번호가 일치하면 확인 메시지 표시
        if (confirm('정말 삭제하시겠습니까?')) {
            document.getElementById('checkPwForm').submit();
        }
    } else {
        alert('비밀번호가 일치하지 않습니다.');
    }
}