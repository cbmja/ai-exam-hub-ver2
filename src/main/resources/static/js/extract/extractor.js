$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

const pdfjsLib = window['pdfjsLib'] || window['pdfjs-dist/build/pdf'];
pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.worker.min.js';

let examCateCode;
let examOrgCode;
let examYear;
let examMonth;
let examSubjectCode;
let examType;

let examCateName;
let examOrgName;
let examSubjectName;
let examTypeName;

const currentYear = new Date().getFullYear();
const selectBox = $("#year-form");

// 20년 전까지 반복문으로 option 추가
for (let year = currentYear; year >= currentYear - 35; year--) {
    selectBox.append(`<option value="${year}">${year}</option>`);
}

// 시험 select 변경 시 값 저장 / select 동적으로 띄우기
$(document).on('change', '.extract-select', function(){

    let id = $(this).attr('id');
    let value = $(this).val();

    let originalText = $('#'+id+' option:selected').text();

    switch(id){
        case 'exam-cate-form' : // 시험종류
            examCateCode = value;
            examCateName = originalText;
            if(examCateCode == 'ETC'){
                $('#exam-org-form').empty().append('<option selected disabled> 주관 </option>');
                return;
                break;
            }

            // 선택한 시험에 따라 다음 select 생성
            $.ajax({
                url: '/member/org',
                method: 'GET',
                data: { examCateCode: examCateCode },
                success: function(list) {

                if(list === null){
                    alert('서버 에러입니다.');
                    return;
                }

                let str;

                // 수능 , 모의고사
                if(examCateCode == 'CSAT' || examCateCode == 'MOCK'){
                    str = '<option selected disabled> 주관 </option>';
                }

                // 중간고사 , 기말고사
                if(examCateCode == 'FINAL' || examCateCode == 'MIDTERM'){
                    str = '<option selected disabled> 학교 </option>';
                }

                // 문제집
                if(examCateCode == 'WOBO'){
                    str = '<option selected disabled> 출판사 </option>';
                }



                for(const ele of list){
                    str += `<option value=${ele.examOrgCode}>${ele.examOrgName}</option>`;
                }
                $('#exam-org-form').empty().append(str).prop('disabled' , false);
                },
                error: function(xhr, status, error) {
                    console.error('Error:', error);
                    alert('서버 에러입니다. 잠시 후 다시 시도해 주세요.');
                }
            });


            $.ajax({
                url: '/member/subject',
                method: 'GET',
                data: { examCateCode: examCateCode },
                success: function(list) {

                if(list === null){
                    alert('서버 에러입니다.');
                    return;
                }

                let str = '<option selected disabled> 과목 </option>';

                for(const ele of list){
                    str += `<option value=${ele.subjectCode}>${ele.subjectName}</option>`;
                }
                $('#subject-form').empty().append(str);
                },
                error: function(xhr, status, error) {
                    console.error('Error:', error);
                    alert('서버 에러입니다. 잠시 후 다시 시도해 주세요.');
                }
            });


            break;
        case 'exam-org-form' : // 출제기관
            examOrgCode = value;
            examOrgName = originalText;
            break;
        case 'subject-form' : // 과목
            examSubjectCode = value;
            examSubjectName = originalText;
            break;
        case 'year-form' :
            examYear = value;
            break;
        case 'month-form' :
            examMonth = value;
            break;
        case 'type-form' :
            examType = value;
            examTypeName = originalText;
            break;
    }

});

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

    let selectedExam = `${examYear}_${examMonth} 월_${examCateName}_${examSubjectName}_${examTypeName}`;

    $('#select-exam-info').empty().append(selectedExam);

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


/* 자동 추출 버튼 */
$(document).on('click', '#extract-question-auto-btn', function(){

    if (!upLoadFile) {
        alert('시험지를 업로드 하세요.');
        return;
    }

    let selectedExam = `${examYear}_${examMonth} 월_${examCateName}_${examSubjectName}_${examTypeName}`;

    $('#select-exam-info').empty().append(selectedExam);

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


    var formData = new FormData();
    formData.append("pdf", upLoadFile);


    $.ajax({
        url: "/member/extract/auto",
        method: "POST",
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {

            if(response == 'err'){
                alert("서버 에러입니다.");
                return;
            }

            $('#extract-res').css('display' , 'flex');
            $('#extract-res').append(response);

            console.log("파일 업로드 성공:", response);
        },
        error: function (xhr, status, error) {
            alert("서버 에러");
        },
    });


});

/* 문제 추출 버튼 클릭시 */
$(document).on('click', '.open-folder-btn', function () {
    $('#file-input-d').click();
});


/* 문제 추출 버튼 클릭시 */
$(document).on('click', '#ext-test-btn', function () {
    window.location.href='/test';
});
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});

