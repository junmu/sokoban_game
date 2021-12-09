package app.sokoban.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES256Encrypter {

    private final String algorithm = "AES/CBC/PKCS5Padding";
    private final String key = "72662891972036206509712161138294";
    private final String iv = key.substring(0, 16); // 16 byte

    public Cipher getEncrypter() throws Exception{
        Cipher aes256Cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        aes256Cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        return aes256Cipher;
    }

    public Cipher getDecrypter() throws Exception {
        Cipher aes256Cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        aes256Cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        return aes256Cipher;
    }

    public String encrypt(String text) throws Exception {
        byte[] encrypted = getEncrypter().doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws IllegalStateException {
        String result;

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = getDecrypter().doFinal(decodedBytes);
            result = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("파일의 암호화 형식이 올바르지 않습니다.");
        }

        return result;
    }
}
