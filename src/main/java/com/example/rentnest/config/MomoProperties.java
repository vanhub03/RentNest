package com.example.rentnest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {
    private String endpoint; //API tạo thanh toán Momo
    private String partnerCode; //Partner code do momo cấp
    private String accessKey; // access key do momo cấp
    private String secretKey; // Secret key do momo cấp
    private String redirectUrl; //Url frontend momo chuyển về sau khi thanh toán
    private String ipnUrl; // URL backend public để momo gửi kết quả server-to-server
    private String requestType = "captureWallet"; // loại request thanh toán ví momo thông thường
    private String lang = "vi";
}
