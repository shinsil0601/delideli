$(document).ready(function(){
    // AOS
    AOS.init();

    // header category dropdown
    $('.header__bot-menu > li').hover(
        function() {
            $(this).find('ul').stop(true, true).fadeToggle('slow');
        }
    );

    // MainPage Swiper
    const mainPageSwiper = new Swiper(".mainBanner__con", {
        sliedePerView: 'auto',
        spaceBetween: 80,
        loop: true,
        autoplay: {
            delay: 3000,
            disableOnInteraction: false,
        },
        navigation: {
          nextEl: ".mainBanner__prevNext--next",
          prevEl: ".mainBanner__prevNext--prev",
        },
        pagination: {
            el: ".mainBanner__pagination"
        },
    });
});