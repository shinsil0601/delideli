$(document).ready(function () {
    const storeKey = $('#storeKey').val();

    $('#addCategory').on('click', function () {
        const newCategoryName = prompt("추가할 카테고리 이름을 입력하세요:");

        if (newCategoryName) {
            $.ajax({
                url: '/client/addMenuGroup',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    menuGroupName: newCategoryName,
                    storeKey: storeKey
                }),
                success: function (response) {
                    alert('카테고리 추가 성공');
                    location.reload(); // 페이지 새로고침
                },
                error: function (xhr, status, error) {
                    alert('카테고리 추가 실패');
                    //console.error('Error:', error);
                }
            });
        } else {
            alert("카테고리 이름을 입력해주세요.");
        }
    });
});
