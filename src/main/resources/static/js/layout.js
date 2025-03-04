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


    /* 로그인 모달 */
    $(document).on('click', '#login-btn', function(){

        $('#login-form').css('display' , 'flex');

        $('#login-id').val("");
        $('#login-pw').val("");

    });

    /* 로그인 모달 */
    $(document).on('click', '#login-btn2', function(){

        $('#login-id').val("");
        $('#login-pw').val("");

        $('#login-form-ar').css('display' , 'flex');
        $('#join-form-ar').css('display' , 'none');

    });

    /* 회원가입 모달 */
    $(document).on('click', '#join-btn', function(){

        $('#join-email').val("");
        $('#join-id').val("");
        $('#join-pw').val("");
        $('#join-pw2').val("");
        $('#join-phone').val("");

        $('#login-form-ar').css('display' , 'none');

        $('#join-form-ar').css('display' , 'flex');

    });


    /* 회원가입 , 로그인 모달 닫기 */
    $(document).on('click', '.login-modal-close-btn', function(){

        $('#login-form-ar').css('display' , 'flex');
        $('#join-form-ar').css('display' , 'none');
        $('#login-form').css('display' , 'none');
    });

    // 로그아웃
    $(document).on('click', '#logout-btn', function(){


        window.location.href='/member/logout';

    });

    // 나의 저장소
    $(document).on('click', '#mypage-btn', function(){

        window.location.href='/member/repository';

    });

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});