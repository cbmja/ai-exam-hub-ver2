package com.aiexamhub.exam.controller;

import com.aiexamhub.exam.dto.*;
import com.aiexamhub.exam.service.LoginService;
import com.aiexamhub.exam.service.RepositoryService;
import com.aiexamhub.exam.util.CipherUtil;
import com.aiexamhub.exam.util.FileToMultipartFile;
import com.aiexamhub.exam.util.OcrUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final LoginService loginService;
    private final RepositoryService repositoryService;
    private final SqlSessionTemplate sql;
    private final CipherUtil cipherUtil;

    private final OcrUtil ocrUtil;


    // ok
    // 회원가입
    @PostMapping("/join")
    @ResponseBody
    public String joinProc(@RequestBody Member form){

        String res = "";

        try{
            res = loginService.join(form);
        }catch (Exception e){
            e.printStackTrace();
            res = "server err";
        }

        return res;
    }

    // ok
    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public String loginProc(@RequestBody Member form, HttpServletResponse response , Model model){

        String res = "";

        try{
            res = loginService.login(form , response , model);
        }catch (Exception e){
            e.printStackTrace();
            res = "server err";
        }

        return res;
    }

    // ok
    // 로그인
    @GetMapping("/logout")
    public String logout(HttpServletResponse response , Model model){

        // 기존 로그인 정보 삭제
        Cookie oldCookie = new Cookie("mCode", "");
        oldCookie.setMaxAge(0);
        oldCookie.setPath("/");
        response.addCookie(oldCookie);

        model.addAttribute("isLogin" , false);

        return "redirect:/index";
    }
    
    // ok
    // 나의 저장소
    @GetMapping("/repository")
    public String repository(ServletRequest servletRequest, Model model , @RequestParam(name = "page" , defaultValue = "0") int page ,
                                                                         @RequestParam(name = "search" , defaultValue = "") String search ,
                                                                         @RequestParam(name = "searchType" , defaultValue = "repository_name") String searchType ,
                                                                         @RequestParam(name = "sortType" , defaultValue = "created_at") String sortType ,
                                                                         @RequestParam(name = "sort" , defaultValue = "DESC") String sort){

        try {
            HttpServletRequest req = (HttpServletRequest) servletRequest;

            String memberCode = (String)(req.getAttribute("memberCode"));

            Map<String , Object> res = repositoryService.getRepositories(page , search , searchType ,sortType ,sort);

            List<Repository> list = (List<Repository>)(res.get("list"));
            Page pageData = (Page)(res.get("page"));

            model.addAttribute("list" , list);
            model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));
            model.addAttribute("page" , pageData);
            model.addAttribute("navSelected" , "mypage");

            return "view/member/repository";
        }catch (Exception e){
            e.printStackTrace();

            return "view/err";
        }



    }

    // ok
    // 저장소 상세
    @GetMapping("/repository/{repositoryCode}")
    public String repositoryDetail(ServletRequest servletRequest , Model model , @PathVariable(name = "repositoryCode" , required = true) String repositoryCode){


        try{

            HttpServletRequest req = (HttpServletRequest) servletRequest;

            model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));

            Page page = new Page();
            page.setSearch(repositoryCode);

            List<Question> list = sql.selectList("com.aiexamhub.exam.mapper.QuestionMapper.selectByRepositoryCode" , page);

            Repository repository = sql.selectOne("com.aiexamhub.exam.mapper.RepositoryMapper.selectByRepositoryCode" , repositoryCode);

            for(Question q : list){

                String type = "";

                switch (q.getExamType()){
                    case "A": type = "A형"; break;
                    case "B": type = "B형"; break;
                    case "1": type = "1형"; break;
                    case "2": type = "2형"; break;
                    case "odd": type = "홀수형"; break;
                    case "even": type = "짝수형"; break;
                    case "none": type = "타입없음"; break;
                }

                q.setExamTypeName(type);
                
                if(q.getSubjectDetailName().isEmpty()){
                    q.setSubjectDetailName("공통");
                }

            }


            model.addAttribute("list" , list);
            model.addAttribute("repository" , repository);
            model.addAttribute("navSelected" , "mypage");

            return "view/member/question";
        }catch (Exception e){
            e.printStackTrace();
            return "view/err";
        }




    }


    // ok
    // 저장소 생성
    @PostMapping("/repository/create")
    @ResponseBody
    public String createRepository(ServletRequest servletRequest , @RequestBody Repository form){
        String res = "success";

        try{
            HttpServletRequest req = (HttpServletRequest) servletRequest;

            form.setRepositoryCode(cipherUtil.createCode());

            String memberCode = (String)(req.getAttribute("memberCode"));

            form.setMemberCode(memberCode);

            if(sql.insert("com.aiexamhub.exam.mapper.RepositoryMapper.save" , form) <= 0){
                return "DB err";
            }
        }catch (Exception e){
            e.printStackTrace();
            res = "server err";
        }


        return res;
    }

    // ok
    // 저장소 수정
    @PostMapping("/repository/edit")
    @ResponseBody
    public String editRepository(ServletRequest servletRequest , @RequestBody Repository form){
        
        try {
            return repositoryService.edit(form , servletRequest);
        }catch (Exception e){
            e.printStackTrace();
            return "server err";
        }

    }

    // ok
    // 출력화면
    @GetMapping("/extract/{repositoryCode}")
    public String extract(ServletRequest servletRequest, Model model , @PathVariable(name = "repositoryCode") String repositoryCode){
        try {
            HttpServletRequest req = (HttpServletRequest) servletRequest;

            List<ExamCate> cateList = sql.selectList("com.aiexamhub.exam.mapper.ExamCateMapper.selectAll");

            model.addAttribute("cateList" , cateList);
            model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));
            model.addAttribute("repositoryCode" , repositoryCode);

            return "view/extract/extractor";
        }catch (Exception e){
            return "view/err";
        }

    }

    // ok
    // 시험 종류 선택시 주관 select
    @GetMapping("/org")
    @ResponseBody
    public List<ExamOrg> org(@RequestParam(name = "examCateCode") String examCateCode){
        try {

            List<ExamOrg> cateList = sql.selectList("com.aiexamhub.exam.mapper.ExamOrgMapper.selectByCateCode" , examCateCode);

            if(cateList == null){
                cateList = new ArrayList<>();
            }

            return cateList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    // ok
    // 시험 종류 선택시 과목 select
    @GetMapping("/subject")
    @ResponseBody
    public List<Subject> subject(@RequestParam(name = "examCateCode") String examCateCode){
        try {

            List<Subject> subjectList = sql.selectList("com.aiexamhub.exam.mapper.SubjectMapper.selectByCateCode" , examCateCode);

            if(subjectList == null){
                subjectList = new ArrayList<>();
            }

            return subjectList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    // ok
    // 자동추출 pdf
    @PostMapping("/extract/auto")
    @ResponseBody
    public String autoExtract(@RequestParam(value = "pdf" , required = false) MultipartFile pdf){

        if(pdf == null || pdf.isEmpty()){
            File file = new File("C:\\Users\\jeon\\Desktop\\시험_사이트\\수능 기출 모음\\kor\\25_O_odd.pdf");
            pdf = new FileToMultipartFile(file);
        }

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> fontData = new ArrayList<>();
        String text = "";
        try (PDDocument document = Loader.loadPDF(convertMultipartFileToFile(pdf))) {

            // PDF 텍스트 추출 (폰트 정보 포함)
            FontInfoExtractor fontExtractor = new FontInfoExtractor();
            fontExtractor.setFontData(fontData);
            fontExtractor.getText(document);

            response.put("fonts", fontData);

            for(int i = 0; i < fontData.size(); i++){

                // font는 '한 글자'.
                /* ex)
                 * {
                 *   "text": "수",
                 *   "x": 129.20339,
                 *   "y": 64783.0283,
                 * }
                 * */
                // fontData 는 위의 font 들로 이루어진 시험지 전체의 텍스트 list
                Map<String, Object> font = fontData.get(i);


                // 텍스트의 y좌표를 통해 문단을 분리 하는 작업
                // 첫 인덱스
                if(i == 0){
                    text += (String)font.get("text");
                }else{
                    // y 좌표가 동일하면 같은 행
                    if((float)font.get("y") == (float)fontData.get(i-1).get("y")){
                        text += (String)font.get("text");
                    }else{
                        // 다르면 줄바꿈

                        // 다음 행과의 높이
                        float height = (float)font.get("y") - (float)(float)fontData.get(i-1).get("y");

                        // 높이가 음수가 나오는 경우
                        // 1. 간헐적으로 등장하는 -0.xxx : 미세하게 이전 텍스트보다 높게 존재하는 경우
                        // - 줄바꿈 하지 않도록 처리
                        // 2. 좌측 하단이 끝나고 우측 상단으로 이동한 경우
                        // - 양수로 변환 해도 구별 가능 (|height| >= 800 인 경우 줄바꿈)

                        if(height < 0){
                            height = height * -1f;
                        }

                        // 1. 높이가 소숫점 대이면 같은 행으로 취급
                        // 2. 높이가 18.xx , 19.xx , 20.xx이면 같은 문장 내 줄바꿈 이므로 같은 행으로 취급
                        if(Math.floor(height) == 0 || Math.floor(height) == 18 || Math.floor(height) == 19 || Math.floor(height) == 20){
                            text += (String)font.get("text");
                        }else{
                            // 이외에는 분류 타입이 바뀌는 부분으로 간주.
                            // ex)
                            // 1. 문제 지문 -> 문제 텍스트
                            // 2. 문제 텍스트 -> 문제 보기 or 선택지 1번
                            // 3. 문제 보기 -> 선택지 1번
                            // 4. 선택지 5번 -> 문제 지문 or 다음 문제 텍스트

                            // 가독성을 위해 줄바꿈.
                            text += /*"-----"+height+*/"<br>"+(String)font.get("text");
                        }
                    }

                }

            }

            // 문제와 상관 없는 텍스트 제거
            StringBuilder sb = new StringBuilder(text);
            replaceInStringBuilder(sb, "이 문제지에 관한 저작권은 한국교육과정평가원에 있습니다.", "");
            replaceInStringBuilder(sb, "2025학년도 대학수학능력시험 문제지<br>", "");
            replaceInStringBuilder(sb, "(언어와 매체)", "");
            replaceInStringBuilder(sb, "(화법과 작문)", "");
            replaceInStringBuilder(sb, "<br>짝수형<br>", "");
            replaceInStringBuilder(sb, "<br>홀수형<br>", "");
            replaceInStringBuilder(sb, "제1교시", "");

            text = sb.toString()
                    //.replaceAll("<br>(?:<br>)?(\\d+)\\s*<br>(?:<br>)?(\\d+)\\s*<br>(?:<br>)?(\\d+)\\s*<br>","<br>")
                    .replaceAll("\\d+\\s*<br>20<br>", "") // 총 페이지 텍스트 삭제

                    .replaceAll("(<br>)(\\d+\\.)", "$1<br><hr><br><div class=\"question-block\" data-questionno=\"$2\"><input style=\"width: 95%; height: 30px; border: 3px solid #c0c2cc;\" class=\"input-box q-bb\" type=\"text\" value=\"[$2번 문제]<br>") // 문제 시작 부분
                    .replaceAll("①" , "\"><br><input style=\"width: 95%;\" class=\"input-box i-bb\" type=\"text\" value=\"①") // 문제 종료 , 1번 선택지 시작

                    .replaceAll("(②|③|④|⑤)", "\"/><br><input style=\"width: 95%;\" class=\"input-box i-bb\" type=\"text\" value=\"$1") // 1,2,3,4 번 선택지 종료 , 2,3,4,5 번 선택지 시작

                    .replaceAll("\\[(\\d+)～(\\d+)\\]" , "<br><hr><br><div class=\"passage-block\"><textarea class=\"textarea-box t-bb\" style=\"height: 300px; width: 95%;\">[$1~$2]") // 공통 지문 시작

                    .replaceAll("<br>\\d+\\s*<br>" , "") // 페이지 정보 텍스트 삭제
                    // .replaceAll("<보 기>" , "<br><보 기>")
                    .replaceAll("(<textarea class=\"textarea-box t-bb\" style=\"height: 300px; width: 95%;\">)(.*?)(?=<hr>)","$1$2</textarea></div><hr>"); // 공통 지문 종료

            text = text.replaceAll("(\"/><br><input style=\"width: 95%;\" class=\"input-box i-bb\" type=\"text\" value=\"⑤)(.*?)(?=<hr>)" , "$1$2\"></div><hr>") // 5번 선택지 종료
                    .replaceAll("<br>" , "")
                    .replaceAll("<hr><hr>" , "<div class=\"r-e-c\" style=\"margin-left: auto; margin-right: 10px;\"><img src=\"/img/1.png\" style=\"width: 35px; height: 35px;\" class=\"q-cate-btn\" data-type=\"1\"><img src=\"/img/2.png\" style=\"width: 35px; height: 35px;\" class=\"q-cate-btn\" data-type=\"2\"><img src=\"/img/3.png\" style=\"width: 35px; height: 35px;\" class=\"q-cate-btn\" data-type=\"3\"><button class=\"ext-btn open-folder-btn\">이미지 추가</button><button class=\"ext-btn\">저장</button></div><div style=\"margin-bottom: 10px; margin-top: 30px; width: 90%; border-bottom: 2px solid #FE6E2F; \"></div>");

            exportImg(document);

            // 특정 문자(쉼표)를 기준으로 문자열을 분리
            String[] splitArray = text.split("<hr style=\"margin-bottom: 30px; margin-top: 30px;\">");

            // 배열을 List로 변환
            List<String> list = Arrays.asList(splitArray);

            for(String ele : list){
                System.out.println(ele);
            }

            return text;

        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "PDF 로드 실패: " + e.getMessage());
            return "err";
        }

    }

    private String exportImg(PDDocument doc){

        try {
            String outputFolder = "C:\\Users\\jeon\\Desktop\\extract_img";

            int i = 1;
            for (PDPage page : doc.getPages()) {
                // 페이지 좌표 변환 행렬
                AffineTransform transform = page.getMatrix().createAffineTransform();

                for (COSName cosName : page.getResources().getXObjectNames()) {
                    PDXObject xObject = page.getResources().getXObject(cosName);

                    if (xObject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xObject;
                        BufferedImage bufferedImage = image.getImage();

                        // 이미지 좌표값 가져오기 (반올림 X)
                        double x = transform.getTranslateX();
                        double y = transform.getTranslateY();

                        String imagePath = outputFolder + "/"+( i++ )+"x" + x + "_y" + y + ".png";

                        File outputFile = new File(imagePath);
                        ImageIO.write(bufferedImage, "png", outputFile);
                        System.out.println("Extracted: " + imagePath);
                    } else if (xObject instanceof PDFormXObject) {
                        extractImagesFromFormXObject((PDFormXObject) xObject, outputFolder);
                    }
                }
            }
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "err";
        }
    }

    private static void extractImagesFromFormXObject(PDFormXObject formXObject, String outputFolder) throws IOException {
        for (COSName cosName : formXObject.getResources().getXObjectNames()) {
            PDXObject xObject = formXObject.getResources().getXObject(cosName);

            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;
                BufferedImage bufferedImage = image.getImage();
                String imagePath = outputFolder + "/embedded_x_y.png";

                File outputFile = new File(imagePath);
                ImageIO.write(bufferedImage, "png", outputFile);
                System.out.println("Extracted: " + imagePath);
            }
        }
    }


    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("uploaded-", ".pdf"); // 임시 파일 생성
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    public static void replaceInStringBuilder(StringBuilder sb, String target, String replacement) {
        int index;
        while ((index = sb.indexOf(target)) != -1) {
            sb.replace(index, index + target.length(), replacement);
        }
    }


    // ok-02/25---------------------------------------------------------------------------------------------------------
    // 03/01 2차 귀찮아서 확인 안함 보류 -----------------------------------------------------------------------------------
    @PostMapping("/naver-ocr")
    @ResponseBody
    public String img(ServletRequest servletRequest , @RequestBody Map<String , Object> form , Model model){

        try{
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            model.addAttribute("isLogin" , (Boolean)req.getAttribute("isLogin"));

            String base64Image = (String)form.get("image");

            String img64 = base64Image.replaceAll("data:image/png;base64,","");

            String ocrResult = ocrUtil.sendOCRRequest(img64);

            JSONObject jsonObject = new JSONObject(ocrResult);
            JSONArray images = jsonObject.getJSONArray("images");

            StringBuilder inferTextBuilder = new StringBuilder();

            // Iterate through images and extract inferText from fields
            for (int i = 0; i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                JSONArray fields = image.getJSONArray("fields");

                for (int j = 0; j < fields.length(); j++) {
                    JSONObject field = fields.getJSONObject(j);
                    String inferText = field.getString("inferText");
                    inferTextBuilder.append(inferText);

                    // Add a space if lineBreak is false
                    if (!field.getBoolean("lineBreak")) {
                        inferTextBuilder.append(" ");
                    }
                }
            }

            // Output the result
            String result = inferTextBuilder.toString().trim();


            return result;
        }catch (Exception e){
            e.printStackTrace();
            return "err";
        }

    }






}



class FontInfoExtractor extends PDFTextStripper {
    private List<Map<String, Object>> fontData;

    public FontInfoExtractor() throws IOException {
        super();
        this.fontData = new ArrayList<>();  // 기본 리스트 초기화
    }

    public void setFontData(List<Map<String, Object>> fontData) {
        this.fontData = fontData;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        Map<String, Object> fontInfo = new HashMap<>();
        fontInfo.put("text", text.getUnicode());
        fontInfo.put("size", text.getFontSizeInPt());
        fontInfo.put("width", text.getWidth());
        fontInfo.put("x", text.getX());
        fontInfo.put("y",text.getY());
        fontData.add(fontInfo);
    }
}


