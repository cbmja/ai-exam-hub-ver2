$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

const pdfjsLib = window['pdfjsLib'] || window['pdfjs-dist/build/pdf'];
pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.worker.min.js';

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


/* 공통 보기 생성 폼 닫기 */
$(document).on('click', '#common-passage-form-close', function(){

    $('#common-passage-form').css('display' , 'none');

    $('#common-passage-no').val("");
    $('#common-passage-content').val("");

});

/* 공통 보기 생성 폼 열기 */
$(document).on('click', '#common-passage-btn', function(){

    $('#common-passage-form').css('display' , 'flex');

});


/* 문제 추출 버튼 클릭시 */
$(document).on('click', '#extract-question-btn', function () {

    if (!upLoadFile) {
        alert('시험지를 업로드 하세요.');
        return;
    }

    $('#extract-form').css('display', 'none');
    $('#extract-exam').css('display', 'block').empty();

    const reader = new FileReader();
    reader.readAsArrayBuffer(upLoadFile);

    reader.onload = function () {
        const arrayBuffer = reader.result;
        const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });

        loadingTask.promise
            .then(function (pdf) {
                let totalPages = pdf.numPages;

                function renderPage(pageNum) {
                    pdf.getPage(pageNum).then(function (page) {
                        const scale = 2.5; // 해상도
                        const viewport = page.getViewport({ scale });

                        // Canvas 생성
                        const canvas = $('<canvas></canvas>')[0];
                        const context = canvas.getContext('2d');


                        canvas.width = viewport.width;
                        canvas.height = viewport.height;


                        $(canvas).css({
                            width: viewport.width / scale + "px",
                            height: viewport.height / scale + "px"
                        });

                        /
                        page.render({ canvasContext: context, viewport }).promise.then(function () {
                            // Canvas를 이미지로 변환 (고해상도 유지)
                            const img = $('<img>')
                                .attr('src', canvas.toDataURL('image/png'))
                                .css({
                                    width: "70%", // 반응형 크기 조절
                                    "max-width": viewport.width + "px",
                                    "height": "auto" // 가로·세로 비율 유지
                                });

                            // `extract-exam` div에 이미지 추가
                            $('#extract-exam').append(img);

                            // 다음 페이지 처리
                            if (pageNum < totalPages) {
                                renderPage(pageNum + 1);
                            }
                        });
                    });
                }

                // 첫 페이지부터 렌더링 시작
                renderPage(1);
            })
            .catch(function (error) {
                console.error('PDF 로드 중 오류 발생:', error);
            });
    };
});





//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});