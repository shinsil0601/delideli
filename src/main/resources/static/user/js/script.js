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

    // MainCategory Tab
    $('.mainCategory__items > li').click(function(){
        $('.mainCategory__items > li').removeClass('active');
        $(this).addClass('active');
        $('.mainCategory__shop').removeClass('active');

        const index = $(this).index();
        $('.mainCategory__shop').eq(index).addClass('active');
    });

    // MainEvent Swiper
    const mainEventSwiper = new Swiper(".mainEvent__con", {
        sliedePerView: '1',
        loop: true,
        autoplay: {
            delay: 3000,
            disableOnInteraction: false,
        },
        navigation: {
            nextEl: ".mainEvent__prevNext--next",
            prevEl: ".mainEvent__prevNext--prev",
        },
    });

    // MyPage Point CountUp
    $('.js-point').each(function() {
        $(this).prop("Counter", 0).animate(
            {
                Counter: $(this).text(),
            },
            {
                duration: 4000,
                easing: "swing",
                step: function(now) {
                    now = Number(Math.ceil(now)).toLocaleString('en');
                    $(this).text(now);
                },
            },
        );
    });
});