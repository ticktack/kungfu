package ai.llm.qanything.entity;

import ai.llm.util.SignKit;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author yfq
 */
@Data
@AllArgsConstructor
public class AuthConfig {

    private String curtime;
    private String salt;
    private String signType;
    private String sign;
    private String appKey;


    public AuthConfig(String appKey, String appSecret, String q) {
        String salt = UUID.randomUUID().toString();
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = SignKit.calculateSign(appKey, appSecret, q, salt, curtime);
        this.appKey = appKey;
        this.curtime = curtime;
        this.salt = salt;
        this.sign = sign;
        this.signType = "v3";
    }


}
