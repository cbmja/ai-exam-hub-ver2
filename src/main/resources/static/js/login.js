$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

    /* 회원가입 진행 */
    $(document).on('click', '#join-submit', function(){

        let email = $('#join-email').val();
        let memberId = $('#join-id').val();
        let memberPw = $('#join-pw').val();
        let memberPw2 = $('#join-pw2').val();
        let phone = $('#join-phone').val();

        if(!email || !memberId || !memberPw || !memberPw2 || !phone){
            alert('모두 입력해주세요.');
            return;
        }

        if(memberPw && memberPw2 && memberPw !== memberPw2){
            alert('비밀번호 확인이 틀렸습니다.');
            return;
        }

        $.ajax({
            url: '/join', // 서버 URL
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                email : email,
                memberId : memberId,
                memberPw : memberPw,
                phone : phone
            }),
            success: function(response) {

                console.log(response);
                switch (response){
                    case 'join success': alert('회원가입 완료.'); break;
                    case 'duplicate email': alert('사용중인 이메일 입니다.'); break;
                    case 'duplicate id': alert('사용중인 아이디 입니다.'); break;
                    case 'duplicate phone': alert('사용중인 전화번호 입니다.'); break;
                    case 'sql err': alert('DB 에러입니다.'); break;
                    case 'server err': alert('서버 에러입니다.'); break;
                }

            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('서버 에러입니다. 잠시 후 다시 시도해주세요.');
            }
        });



    });


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});