$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

    /* 회원가입 진행 */
    $(document).on('click', '#join-submit', function(){

        let email = $('#join-email').val().trim();
        let memberId = $('#join-id').val().trim();
        let memberPw = $('#join-pw').val().trim();
        let memberPw2 = $('#join-pw2').val().trim();
        let phone = $('#join-phone').val().trim();

        let isErr = false;

        if(!email || !memberId || !memberPw || !memberPw2 || !phone){
            alert('모두 입력해주세요.');
            isErr = true;
        }

        if (!/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{7,}$/.test(memberPw)) {
            alert('비밀번호는 최소 7자리의 숫자 + 영문 + 특수문자로 구성되어야 합니다.');
            isErr = true;
        }

        if(!/^[A-Za-z0-9]{5,}$/.test(memberId)){
            alert('아이디는 5자리 이상의 영문 + 숫자로 구성 되어야 합니다.');
            isErr = true;
        }

        if(memberPw && memberPw2 && memberPw !== memberPw2){
            alert('비밀번호 확인이 틀렸습니다.');
            isErr = true;
        }

        if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
            alert('이메일 형식이 맞지 않습니다. ex) userId@domain.com');
            isErr = true;
        }

        if (!/^010\d{8}$/.test(phone)) {
            alert('전화번호 형식이 맞지 않습니다. ex) 010XXXXXXXX');
            isErr = true;
        }

        if(isErr){
            isErr = false;
            return;
        }



        $.ajax({
            url: '/member/join', // 서버 URL
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
                    case 'join success': alert('회원가입 완료.');

                        $('#join-email').val("");
                        $('#join-id').val("");
                        $('#join-pw').val("");
                        $('#join-pw2').val("");
                        $('#join-phone').val("");


                        $('#login-id').val("");
                        $('#login-pw').val("");
                        $('#login-form-ar').css('display' , 'flex');
                        $('#join-form-ar').css('display' , 'none');
                        break;
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


    /* 로그인 */
    $(document).on('click', '#login-submit', function(){


        let memberId = $('#login-id').val().trim();
        let memberPw = $('#login-pw').val().trim();

        if(!memberId || !memberPw){
            alert('아이디와 비밀번호를 모두 입력해주세요.');
            return;
        }


        $.ajax({
            url: '/member/login', // 서버 URL
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                memberId : memberId,
                memberPw : memberPw
            }),
            success: function(response) {

                console.log(response);
                switch (response){
                    case 'login success':
                        $('#login-id').val("");
                        $('#login-pw').val("");
                        window.location.href='/index';

                        break;
                    case 'server err': alert('서버 에러입니다.'); break;
                    case 'wrong pw': alert('비밀번호가 일치하지 않습니다.'); break;
                    case 'wrong id': alert('존재하지 않는 아이디입니다.'); break;
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