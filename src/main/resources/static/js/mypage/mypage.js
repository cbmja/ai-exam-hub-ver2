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

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});