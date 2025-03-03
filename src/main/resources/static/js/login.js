$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

    /* 회원가입 진행 */
    $(document).on('click', '#join-submit', function(){

        let email = $('#join-email').val();
        let userId = $('#join-id').val();
        let userPw = $('#join-pw').val();
        let userPw2 = $('#join-pw2').val();
        let phone = $('#join-phone').val();

        if(!email || !userId || !userPw || !userPw2 || !phone){
            alert('모두 입력해주세요.');
            return;
        }

        if(userPw && userPw2 && userPw !== userPw2){
            alert('비밀번호 확인이 틀렸습니다.');
            return;
        }



    });


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});