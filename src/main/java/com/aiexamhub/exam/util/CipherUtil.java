package com.aiexamhub.exam.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

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


    public String createCode(){

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS");
        Random random = new Random();
        char r1 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');
        char r2 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');
        char r3 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');

        int i1 = random.nextInt(10);
        int i2 = random.nextInt(10);
        int i3 = random.nextInt(10);

        return String.valueOf(r1) + i1 + String.valueOf(r2) + i2 + String.valueOf(r3) + i3 +"_" + now.format(formatter);
    }

}
