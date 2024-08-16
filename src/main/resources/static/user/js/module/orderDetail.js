$(document).ready(function() {
    $('#sameMenuButton').on('click', function() {
        // storeInfoKey와 userKey를 가져옵니다.
        const storeInfoKey = $('#storeInfoKey').val();
        const userKey = $('#userKey').val();

        // 주문 메뉴, 수량, 옵션을 가져옵니다.
        const orderDetails = [];
        $('.menu-item').each(function() {
            const menuName = $(this).data('menu-name');
            const quantity = $(this).data('quantity');
            const optionName = $(this).data('option-name');

            orderDetails.push({
                menuName: menuName,
                quantity: quantity,
                optionName: optionName
            });
        });

        // 서버로 데이터를 전송합니다.
        $.ajax({
            url: '/user/addSameMenu',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                storeInfoKey: storeInfoKey,
                userKey: userKey,
                orderDetails: orderDetails
            }),
            headers: {
                'X-CSRF-Token': $('meta[name="_csrf"]').attr('content')
            },
            success: function(response) {
                console.log(response); // 서버로부터의 응답을 로그로 출력
                alert('같은 메뉴가 성공적으로 담겼습니다.');
            },
            error: function(xhr, status, error) {
                console.error('Error:', status, error);
                alert('같은 메뉴 담기에 실패했습니다.');
            }
        });
    });
});
