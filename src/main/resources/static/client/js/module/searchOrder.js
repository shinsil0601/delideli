function openOrderDetail(button) {
    var orderKey = button.getAttribute("data-orderkey");
    var url = '/client/OrderDetail/' + orderKey;
    window.open(url, '_blank', 'width=600,height=400');
}
$(document).ready(function() {
    const today = new Date().toISOString().split('T')[0];

    // 서버에서 전달된 startDate와 endDate 값이 있는지 확인
    const serverStartDate = $('#startDate').val();
    const serverEndDate = $('#endDate').val();

    // startDate와 endDate가 없다면 오늘 날짜로 초기화
    if (!serverStartDate) {
        $('#startDate').val(today);
    }

    if (!serverEndDate) {
        $('#endDate').val(today);
    }

    $('#startDate').attr('max', today);
    $('#endDate').attr('max', today);

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
