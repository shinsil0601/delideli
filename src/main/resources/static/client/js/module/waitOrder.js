function openOrderDetail(button) {
    var orderKey = button.getAttribute("data-orderkey");
    var url = '/client/waitOrderDetail/' + orderKey;
    window.open(url, '_blank', 'width=600,height=400');
}
