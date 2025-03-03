$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

    /* 스크롤시 헤더 숨기기 */
    $(window).scroll(function() {
        if ($(this).scrollTop() > 0) {
            $('#navbar').css('top', '0');
            $('#sidebar').css({
                top: '65px',
                height: 'calc(100% - 65px)'
            });
        } else {
            $('#navbar').css('top', '40px');
            $('#sidebar').css({
                top: '105px',
                height: 'calc(100% - 105px)'
            });
        }
    });

    /* 사이드바 숨기기 나타내기 */
    $(document).on('click', '#sidebar-hide-btn', function(){

        let type = $(this).data('type');

        if(type === 'hide'){
            $(this).empty().append('>').css('left', '0').data('type' , 'show');
            $('#sidebar').css('display' , 'none');
        }else{
            $(this).empty().append('<').css('left', '201px').data('type' , 'hide');
            $('#sidebar').css('display' , 'flex');
        }


    });


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});