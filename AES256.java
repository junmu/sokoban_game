import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AES256 {
    private final String algorithm = "AES/CBC/PKCS5Padding";
    private final String key = "72662891972036206509712161138294";
    private final String iv = key.substring(0, 16); // 16 byte

    abstract public String encrypt(String text) throws Exception;
    abstract public String decrypt(String text) throws Exception;

    public Cipher getAes256Cipher() throws Exception {
        Cipher aes256Cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        aes256Cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        return aes256Cipher;
    }
}
