package ai.llm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yfq
 */
public class SignKit {
    /**
     * 计算鉴权签名 -
     * 计算方式 : sign = sha256(appKey + input(q) + salt + curtime + appSecret)
     *
     * @param appKey    您的应用ID
     * @param appSecret 您的应用密钥
     * @param q         请求内容
     * @param salt      随机值
     * @param curtime   当前时间戳(秒)
     * @return 鉴权签名sign
     */
    public static String calculateSign(String appKey, String appSecret, String q, String salt, String curtime) {
        String strSrc = appKey + getInput(q) + salt + curtime + appSecret;
        try {
            return encrypt(strSrc);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encrypt(String strSrc) throws NoSuchAlgorithmException {
        byte[] bt = strSrc.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bt);
        byte[] bts = md.digest();
        StringBuilder des = new StringBuilder();
        for (byte b : bts) {
            String tmp = (Integer.toHexString(b & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    private static String getInput(String input) {
        if (input == null) {
            return null;
        }
        String result;
        int len = input.length();
        if (len <= 20) {
            result = input;
        } else {
            String startStr = input.substring(0, 10);
            String endStr = input.substring(len - 10, len);
            result = startStr + len + endStr;
        }
        return result;
    }
}
