$(document).ready(function() {
    // 페이지가 로드될 때 스크롤 위치를 복원
    if (localStorage.getItem('scrollPosition')) {
        // 로컬 스토리지에 저장된 스크롤 위치로 이동
        $(window).scrollTop(localStorage.getItem('scrollPosition'));
        // 위치를 복원한 후에는 스토리지에서 제거
        localStorage.removeItem('scrollPosition');
    }

    // URL에서 현재 카테고리 ID 가져오기
    var currentCategoryId = window.location.pathname.split('/').pop();

    // 페이지 로드 시 현재 카테고리에 'active' 클래스 추가
    $('.mainCategory__items li').each(function() {
        var categoryId = $(this).data('category-id');
        if (currentCategoryId && categoryId == currentCategoryId) {
            $(this).addClass('active');
        } else if (!currentCategoryId && $(this).index() === 0) {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    });

    // 카테고리 클릭 시
    $('.mainCategory__items li').click(function() {
        // 모든 카테고리에서 'active' 클래스 제거
        $('.mainCategory__items li').removeClass('active');

        // 클릭된 카테고리에 'active' 클래스 추가
        $(this).addClass('active');

        // 현재 스크롤 위치를 로컬 스토리지에 저장
        localStorage.setItem('scrollPosition', $(window).scrollTop());

        // 카테고리 선택 처리 및 페이지 리로드
        var categoryId = $(this).data('category-id');
        window.location.href = '/user/home/' + categoryId;
    });
});
