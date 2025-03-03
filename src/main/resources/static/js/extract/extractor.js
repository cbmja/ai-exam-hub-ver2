$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

/* pdf 드래그 영역 */
const dropArea = $('#extract-pdf-ar');

/* 현재 업로드 파일 정보 */
let upLoadFile;
let originalFileName;

// 드래그가 drop 영역에 들어갈 때 스타일 변경
dropArea.on('dragover', function(event) {
    event.preventDefault();
    dropArea.addClass('extract-pdf-ar-dragover');
});

// 드래그가 drop 영역을 벗어날 때 스타일 변경
dropArea.on('dragleave', function() {
    dropArea.removeClass('extract-pdf-ar-dragover');
});

// 파일을 드롭할 때 처리
dropArea.on('drop', function(event) {
    event.preventDefault();
    dropArea.removeClass('extract-pdf-ar-dragover');

    let files = event.originalEvent.dataTransfer.files;

    if (files.length > 0) {

        upLoadFile = files[0];
        originalFileName = upLoadFile.name;

        let fileType = upLoadFile.type;
        let fileExtension = upLoadFile.name.split('.').pop().toLowerCase();
        // PDF 파일만 허용
        if (fileType != 'application/pdf' || fileExtension != 'pdf') {
            alert('현재는 pdf 파일만 서비스 중 입니다. 죄송합니다.');
            return;
        }
    }

    $('#extract-pdf-title').empty().append(originalFileName);

    if(originalFileName && files){
        dropArea.addClass('extract-pdf-ar-dragover');
    }

});


//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});