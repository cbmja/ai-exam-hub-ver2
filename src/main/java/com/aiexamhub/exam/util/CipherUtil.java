package com.aiexamhub.exam.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CipherUtil {


    @Value("${hash.default_secret}")
    private String default_secret;

    public String decrypt(String encryptedText) {
        String result = "";
        try {
            // SecretKeySpec 생성 (암호화에 사용한 키를 동일하게 사용)
            SecretKeySpec keySpec = new SecretKeySpec(default_secret.getBytes(), "AES");

            // Cipher 객체 생성 및 초기화
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // Base64 디코딩 후 복호화
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            // 결과 문자열 변환
            result = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 02/25 1차 ok-----------------------------------------------------------------------------------------------------
    // 03/01 2차 ok-----------------------------------------------------------------------------------------------------
    public String encrypt(String plainText){
        String result = "";
        try{
            SecretKeySpec keySpec = new SecretKeySpec(default_secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            result = Base64.getEncoder().encodeToString(encryptedBytes);
        }catch(Exception e){
            e.printStackTrace();
            result = "err";
        }

        return result;
    }

}
