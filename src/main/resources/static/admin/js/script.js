$(document).ready(function(){
    /* aside drop-down */
    $('.aside__nav--depth1 > li').click(
        function() {
            $(this).find('ul').stop(true, true).slideToggle('slow');
            $(this).find('> a').stop(true, true).toggleClass('active');
            $(this).siblings().find('ul').slideUp();
            $(this).siblings().find('> a').removeClass('active');
        }
    )
    /* overview countUp */
    $('.overview-item--count').each(function() {
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
    })
});