package core.utils;

import core.FrontController;

import java.security.MessageDigest;

public class Encoder {

    private static MessageDigest encoder;
    static {
        try {
            encoder = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            FrontController.die(Encoder.class, e);
        }
    }

    public static String md5(String source) {
        try {
            byte[] bytes = encoder.digest(source.getBytes("UTF-8"));
            return getString(bytes);
        } catch (Exception e) {
            FrontController.die(Encoder.class, e);
            return null;
        }
    }

    private static String getString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            String hex = Integer.toHexString((int) 0x00FF & b);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
