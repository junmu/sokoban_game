import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES256Encryter extends AES256 {

    @Override
    public String encrypt(String text) throws Exception {
        byte[] encrypted = super.getAes256Cipher().doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String cipherText) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = super.getAes256Cipher().doFinal(decodedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
