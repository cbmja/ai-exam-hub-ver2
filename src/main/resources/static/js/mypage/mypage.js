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


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});