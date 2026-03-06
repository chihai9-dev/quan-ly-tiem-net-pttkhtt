package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Tiện ích mã hóa mật khẩu đơn giản (SHA-256)
 */
public class PasswordEncoder {

    // Mã hóa mật khẩu
    public static String encode(String rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return rawPassword; // Fallback nếu lỗi (không khuyến khích)
        }
    }

    // Kiểm tra mật khẩu (So sánh mật khẩu nhập vào với mật khẩu đã mã hóa)
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        String newHash = encode(rawPassword);
        return newHash.equals(encodedPassword);
    }
}