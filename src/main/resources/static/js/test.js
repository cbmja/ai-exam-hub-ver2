$(document).ready(function () {
//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS


    const pdfUrl = "/25_O_odd.pdf";
    const jsonUrl = "/res_json.json";

    const pdfContainer = document.getElementById("pdf-container");

    let formData = new FormData();
    formData.append("pdf", pdfUrl);

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

    pdfjsLib.getDocument(pdfUrl).promise.then(pdf => {
        fetch(jsonUrl)
            .then(response => response.json())
            .then(data => {
                const processedRects = new Map(); // 페이지별 테두리 저장

                // JSON 데이터를 페이지별로 정리
                data.forEach(rect => {
                    if (!processedRects.has(rect.page)) {
                        processedRects.set(rect.page, new Set());
                    }
                    const rectKey = `${rect.x}-${rect.y}-${rect.width}-${rect.height}`;
                    processedRects.get(rect.page).add(rectKey);
                });

                for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
                    if (!processedRects.has(pageNum)) continue;

                    pdf.getPage(pageNum).then(page => {
                        const viewport = page.getViewport({ scale: 1.5 });
                        const pageWrapper = document.createElement("div");
                        pageWrapper.style.position = "relative";
                        pageWrapper.style.width = `${viewport.width}px`;
                        pageWrapper.style.height = `${viewport.height}px`;
                        pdfContainer.appendChild(pageWrapper);

                        const canvas = document.createElement("canvas");
                        canvas.width = viewport.width;
                        canvas.height = viewport.height;
                        pageWrapper.appendChild(canvas);
                        const ctx = canvas.getContext("2d");

                        const renderContext = {
                            canvasContext: ctx,
                            viewport: viewport
                        };
                        page.render(renderContext);

                        processedRects.get(pageNum).forEach(rectKey => {
                            const [x, y, width, height] = rectKey.split("-").map(Number);

                            const div = document.createElement("div");
                            div.className = "highlight-box";
                            div.style.left = `${x * 1.5}px`;
                            div.style.top = `${y * 1.5}px`;
                            div.style.width = `${width * 1.5}px`;
                            div.style.height = `${height * 1.5}px`;
                            div.style.position = "absolute";
                            div.style.zIndex = "10";
                            div.style.pointerEvents = "auto";
                            div.setAttribute("data-page", pageNum);
                            pageWrapper.appendChild(div);
                        });
                    });
                }
            });
    });


$(document).on('click', '.open-folder-btn', function () {
    $('#file-input-d').click();
});



$(document).on('click', '.highlight-box', function () {
        let element  = $(this);

        var position = element.position();  // left, top 값
        var width = element.width();        // width 값
        var height = element.height();      // height 값

        console.log('Left:', position.left);
        console.log('Top:', position.top);
        console.log('Width:', width);
        console.log('Height:', height);

        var pageNum = $(this).data('page') - 1;

        $('#test-box').css({
            left: position.left,
            top: position.top + ( 1786.5 * pageNum), // 페이지 길이만큼  플러스 해줘야함. 2페이지는 1만큼 , 3페이지는 2만큼. 현제 페이지 -1만큼 y값 보정 필요
            width: width,
            height: height,
        });


/*
        // 캡쳐 영역이 있는 부분을 이미지로 생성
        html2canvas(document.body, {
            x: position.left,
            y: position.top + window.scrollY,
            width: width,
            height: height,
        }).then((canvas) => {

            const imageData = canvas.toDataURL("image/png");

            $.ajax({ // 서드파티로 전송 후 값 가져옴 --- --- --- --- --- --- --- --- 서버로 보내지 말고 바로 naver로 쏴도 될듯 --- --- --- --- --- --- --- --- --- --- --- ---
                url: "/member/naver-ocr",
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify({ image : imageData , answerNo : 0}),
                success: function (response) {
                    if(response == 'err'){
                        alert("서버 에러");
                        return;
                    }

                    console.log(response);

                },
                error: function (xhr, status, error) {
                    alert("서버 에러");
                },
            });

        });
        */
});


$(document).on('click', '.q-cate-btn', function () {
    var type = $(this).data('type');

    var isSelected = $(this).data('selected');

    if(!isSelected){
        $(this).css('border' , '2px solid black');
        $(this).data('selected' , true);
    }else{
        $(this).css('border' , 'none');
        $(this).data('selected' , false);
    }
});

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
});