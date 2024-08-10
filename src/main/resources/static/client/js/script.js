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
});