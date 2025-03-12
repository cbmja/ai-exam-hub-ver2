# 패딩을 5 포인트로 수정하여 겹치는 테두리를 병합하는 작업을 다시 진행

import fitz  # PyMuPDF

# PDF 파일 경로
pdf_path = "/mnt/data/25_O_odd.pdf"
output_pdf_path = "/mnt/data/25_O_odd_pages_bordered_with_merged_borders_v3.pdf"

# PDF 열기
doc = fitz.open(pdf_path)

# 5 포인트 패딩을 적용
padding = 5  # 패딩 크기 설정 (5 포인트)

# 모든 페이지에 대해 작업
for page_num in range(doc.page_count):
    page = doc.load_page(page_num)

    # 겹치는 테두리를 병합할 사각형 리스트
    rects = []

    for block in page.get_text("dict")["blocks"]:
        if "lines" in block:  # 텍스트 블록인 경우
            # 패딩을 적용하여 사각형 계산
            rect = fitz.Rect(
                block["bbox"][0] - padding,  # 왼쪽
                block["bbox"][1] - padding,  # 위쪽
                block["bbox"][2] + padding,  # 오른쪽
                block["bbox"][3] + padding   # 아래쪽
            )
            rects.append(rect)

    # 겹치는 사각형 병합
    merged_rects = []
    while rects:
        rect = rects.pop(0)
        # 병합된 사각형에 포함될지 확인
        merged = False
        for idx, merged_rect in enumerate(merged_rects):
            if merged_rect.intersects(rect):  # 겹칠 경우 병합
                merged_rects[idx] = merged_rect | rect  # 사각형 병합
                merged = True
                break
        if not merged:
            merged_rects.append(rect)  # 겹치지 않으면 새로운 사각형 추가

    # 병합된 사각형에 빨간색 테두리 그리기
    for merged_rect in merged_rects:
        page.draw_rect(merged_rect, color=(1, 0, 0), width=2)  # 빨간색 테두리 추가

# 변경된 PDF 저장
doc.save(output_pdf_path)
doc.close()

# 결과 PDF 파일 경로 반환
output_pdf_path
