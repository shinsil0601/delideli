function showReviewList(type) {
    if (type === 'available') {
        document.getElementById('available-review-list').style.display = 'block';
        document.getElementById('written-review-list').style.display = 'none';
    } else {
        document.getElementById('available-review-list').style.display = 'none';
        document.getElementById('written-review-list').style.display = 'block';
    }
}