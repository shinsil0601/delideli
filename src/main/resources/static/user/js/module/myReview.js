function showReviewList(type) {
    if (type === 'available') {
        $('.myReview__btn-con--1').addClass('active');
        $('.myReview__btn-con--2').removeClass('active');
        document.getElementById('available-review-list').style.display = 'grid';
        document.getElementById('written-review-list').style.display = 'none';
    } else {
        $('.myReview__btn-con--2').addClass('active');
        $('.myReview__btn-con--1').removeClass('active');
        document.getElementById('available-review-list').style.display = 'none';
        document.getElementById('written-review-list').style.display = 'grid';
    }
}