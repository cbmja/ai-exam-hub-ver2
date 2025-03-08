$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

    /* 저장소 생성 폼 */
    $(document).on('click', '#create-repository-btn', function(){

        $('#repo-form').css('display' , 'flex');

    });

    /* 저장소 생성 폼 닫기 */
    $(document).on('click', '#repo-form-close', function(){

        $('#repo-form').css('display' , 'none');

        $('#repo-form-name').val("");
        $('#repo-form-description').val("");
    });


    /* 검색버튼 */
    $(document).on('click', '#repository-search-btn', function(){

        let search = $('#repository-search-input').val();

        window.location.href='/member/repository?page=0&search='+search;
    });


    /* 저장소 클릭 */
    $(document).on('click', '.repository-element', function(){

        let repositoryCode = $(this).data('repositorycode');

        window.location.href='/member/repository/'+repositoryCode;
    });



    /* 저장소 생성 저장 */
    $(document).on('click', '#repo-form-submit', function(){

        let repositoryName = $('#repo-form-name').val();
        let description = $('#repo-form-description').val();

        $.ajax({
            url: '/member/repository/create',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                repositoryName : repositoryName,
                description : description
            }),
            success: function(response) {


                switch (response){

                    case 'DB err': alert('서버 에러입니다.'); break;
                    case 'server err': alert('DB 에러입니다.'); break;
                    case 'success': alert('생성완료. 새로고침 해주세요.');
                        $('#repo-form-name').val("");
                        $('#repo-form-description').val("");
                    break;
                }

            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('서버 에러입니다. 잠시 후 다시 시도해주세요.');
            }
        });


    });



    /* 저장소 수정 저장 */
    $(document).on('click', '#repository-edit-btn', function(){

        let repositoryName = $('#repository-title').val();
        let description = $('#repository-description').val();
        let repositoryCode = $('#repository-code').val();

        $.ajax({
            url: '/member/repository/edit',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                repositoryName : repositoryName,
                description : description,
                repositoryCode : repositoryCode
            }),
            success: function(response) {


                switch (response){

                    case 'DB err': alert('DB 에러입니다.'); break;
                    case 'diff': alert('회원님의 저장소가 아닙니다.'); break;
                    case 'server err': alert('서버 에러입니다.'); break;
                    case 'success': alert('수정 완료.');

                    $('#repo-detail-title').empty().append(repositoryName);
                    break;
                }

            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('서버 에러입니다. 잠시 후 다시 시도해주세요.');
            }
        });


    });

    /* 문제 추가 클릭 */
    $(document).on('click', '#extractor-btn', function(){

        let repositoryCode = $("#repository-code").val();

        window.location.href='/member/extract/'+repositoryCode;
    });


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});