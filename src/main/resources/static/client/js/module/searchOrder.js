function openOrderDetail(button) {
    var orderKey = button.getAttribute("data-orderkey");
    var url = '/client/OrderDetail/' + orderKey;
    window.open(url, '_blank', 'width=600,height=400');
}

$(document).ready(function() {
    // 로컬 시간대에 맞춘 오늘 날짜를 가져옴
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1 필요
    const day = String(today.getDate()).padStart(2, '0');
    const formattedToday = `${year}-${month}-${day}`;

    // 서버에서 전달된 startDate와 endDate 값이 있는지 확인
    const serverStartDate = $('#startDate').val();
    const serverEndDate = $('#endDate').val();

    // startDate와 endDate가 없다면 오늘 날짜로 초기화
    if (!serverStartDate) {
        $('#startDate').val(formattedToday);
    }

    if (!serverEndDate) {
        $('#endDate').val(formattedToday);
    }

    $('#startDate').attr('max', formattedToday);
    $('#endDate').attr('max', formattedToday);

    $('#startDate').on('change', function() {
        const startDate = $(this).val();
        $('#endDate').attr('min', startDate);

        if ($('#endDate').val() < startDate) {
            $('#endDate').val(startDate);
        }
    });

    $('#endDate').on('change', function() {
        const startDate = $('#startDate').val();
        const endDate = $(this).val();

        if (endDate < startDate) {
            $(this).val(startDate);
        }
    });
});
