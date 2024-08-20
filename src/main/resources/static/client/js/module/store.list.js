function toggleStorePause(button) {
    const storeInfoKey = $(button).data('store-id');
    const currentStatus = $(button).find('span').text() === 'ON';

    $.ajax({
        url: 'toggleStorePause/' + storeInfoKey,
        type: 'POST',
        data: JSON.stringify({storePause: !currentStatus}),
        contentType: 'application/json',
        success: function(response) {
            if (response.success) {
                $(button).toggleClass('toggle-on toggle-off');
                $(button).find('span').text(response.newStatus ? 'ON' : 'OFF');
                location.reload();
            } else {
                alert('상태를 변경하는데 실패했습니다.');
            }
        },
        error: function() {
            alert('에러가 발생했습니다.');
        }
    });
}

function toggleStoreDelete(button) {
    const storeInfoKey = $(button).data('store-id');
    const currentStatus = $(button).text() === '삭제 취소';

    $.ajax({
        url: 'toggleStoreDelete/' + storeInfoKey,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                $(button).text(response.newStatus ? '삭제 취소' : '삭제');
                location.reload();
            } else {
                alert('삭제 상태를 변경하는데 실패했습니다.');
            }
        },
        error: function() {
            alert('에러가 발생했습니다.');
        }
    });
}
