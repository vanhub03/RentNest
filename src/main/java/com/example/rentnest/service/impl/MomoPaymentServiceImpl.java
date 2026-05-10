package com.example.rentnest.service.impl;

import com.example.rentnest.config.MomoProperties;
import com.example.rentnest.enums.ContractStatus;
import com.example.rentnest.enums.PaymentStatus;
import com.example.rentnest.enums.RoomStatus;
import com.example.rentnest.model.*;
import com.example.rentnest.model.dto.response.MomoPaymentUrlResponse;
import com.example.rentnest.model.dto.response.MomoReturnResponse;
import com.example.rentnest.repository.ContractRepository;
import com.example.rentnest.repository.DepositPaymentRepository;
import com.example.rentnest.repository.OccupantRepository;
import com.example.rentnest.repository.RoomRepository;
import com.example.rentnest.service.MomoPaymentService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MomoPaymentServiceImpl implements MomoPaymentService {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final MomoProperties momoProperties; //đọc cấu hình momo trong application.yml
    private final ContractRepository contractRepository; //lấy và lưu trạng thái hợp đồng
    private final DepositPaymentRepository depositPaymentRepository; //lưu giao dịch tiền cọc nội bộ
    private final OccupantRepository occupantRepository; //kích hoạt người thuê đại diện sau thanh toán
    private final RoomRepository roomRepository; //chuyển status của room sang RENTED sau thanh toán
    private final RestTemplate restTemplate = new RestTemplate(); // gọi API tạo thanh toán sang momo

    public MomoPaymentServiceImpl(MomoProperties momoProperties, ContractRepository contractRepository, DepositPaymentRepository depositPaymentRepository, OccupantRepository occupantRepository, RoomRepository roomRepository) {
        this.momoProperties = momoProperties;
        this.contractRepository = contractRepository;
        this.depositPaymentRepository = depositPaymentRepository;
        this.occupantRepository = occupantRepository;
        this.roomRepository = roomRepository;
    }


    //tạo DepositPayment + gửi request lên momo để lấy link thanh toasn
    @Override
    @Transactional
    public MomoPaymentUrlResponse createDepositPaymentUrl(Long tenantId, Long rentalRequestId) {
        validateMomoConfig();

        Contract contract = contractRepository.findByRentalRequest_IdAndRentalRequest_Tenant_Id(rentalRequestId, tenantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng cần thanh toán"));
        if(contract.getStatus() == ContractStatus.ACTIVE){
            throw new RuntimeException("Hợp đồng không ở trạng thái chờ thanh toán cọc");
        }

        BigDecimal depositAmount = contract.getDepositAmount(); // tiền cọc lấy từ hợp đồng
        if(depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("Số tiền cọc không hợp lệ");
        }

        String amount = depositAmount.setScale(0, RoundingMode.HALF_DOWN).toPlainString(); // momo muốn nhận VND dạng số nguyên
        String timestamp = LocalDateTime.now(APP_ZONE).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")); // tạo thời gian để cho orderId là duy nhất
        String orderId = "DEP" + contract.getId() + timestamp;
        String requestId = "REQ" + contract.getId() + timestamp;
        String orderInfo = "Thanh toán cọc hợp đồng " + contract.getId();
        String extraData = "";

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + momoProperties.getRequestType(); // thự tự tham số đúng theo tài liệu của momo
        String signature = signHmacSHA256(rawSignature, momoProperties.getSecretKey()); // ký request bằng secretKey momo cấp
        Map<String, Object> requestBody = new LinkedHashMap<>(); // linkedhashmap giữ thứ tự field khi log hoặc debug cho dễ đọc
        requestBody.put("partnerCode", momoProperties.getPartnerCode());
        requestBody.put("requestType", momoProperties.getRequestType());
        requestBody.put("ipnUrl", momoProperties.getIpnUrl());
        requestBody.put("redirectUrl", momoProperties.getRedirectUrl());
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("requestId", requestId);
        requestBody.put("extraData", extraData);
        requestBody.put("signature", signature);
        requestBody.put("lang", momoProperties.getLang());

        HttpHeaders headers = new HttpHeaders(); // Header HTTP gửi sang Momo
        headers.setContentType(MediaType.APPLICATION_JSON); // momo api nhận json
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
        Map<String, String> momoResponse = restTemplate.postForObject(momoProperties.getEndpoint(), httpEntity, Map.class);
        if(momoResponse == null){
            throw new RuntimeException("Momo không trả về giữ liệu thanh toán");
        }

        int resultCode = Integer.parseInt(String.valueOf(momoResponse.get("resultCode")));
        if(resultCode != 0){
            throw new RuntimeException(String.valueOf(momoResponse.getOrDefault("message", "Không tạo được thanh toán momo")));
        }
        String payUrl = String.valueOf(momoResponse.get("payUrl"));
        if(!StringUtils.hasText(payUrl)){
            throw new RuntimeException("Momo không trả về payurl");
        }

        User tenant = contract.getRentalRequest().getTenant();
        DepositPayment payment = DepositPayment.builder()
                .contract(contract)
                .tenant(tenant)
                .orderId(orderId)
                .requestId(requestId)
                .amount(depositAmount)
                .status(PaymentStatus.PENDING)
                .paymentUrl(payUrl)
                .momoResultCode(resultCode)
                .momoMessage(String.valueOf(momoResponse.get("message")))
                .build();
        depositPaymentRepository.save(payment); // lưu trước đ return url có dữ liệu đối chiếu
        return MomoPaymentUrlResponse.builder()
                .orderId(orderId)
                .requestId(requestId)
                .amount(depositAmount)
                .paymentUrl(payUrl)
                .qrCodeUrl(momoResponse.get("qrCodeUrl"))
                .deepLink(momoResponse.get("deepLink"))
                .build();
    }

    //nhanaj thông tin momo trả về đã ttoan thành công hay chưa
    @Override
    @Transactional
    public MomoReturnResponse handleDepositReturn(Long tenantId, Map<String, String> momoParams) {
        DepositPayment payment = processMomoResult(new HashMap<>(momoParams), tenantId);
        if(payment.getStatus() == PaymentStatus.SUCCESS){
            return buildReturnResponse(payment, true, "Thanh toán cọc thành công. Bạn đã trở thành người thuê phòng chính thức");
        }
        return buildReturnResponse(payment, false, "Thanh toán chưa thành công. Mã MoMo: " + payment.getMomoResultCode());
    }

   private MomoReturnResponse  buildReturnResponse(DepositPayment payment, boolean success, String message){
        return MomoReturnResponse.builder()
                .success(success)
                .message(message)
                .contractId(payment.getContract().getId())
                .rentalRequestId(payment.getContract().getRentalRequest().getId())
                .build();
    }

//    @Override
//    public Map<String, Object> handleDepositIpn(Map<String, Object> momoParams) {
//        return Map.of();
//    }

    private DepositPayment processMomoResult(Map<String, Object> momoParams, Long tenantId){
        if(!isValidSignature(momoParams)){
            throw new RuntimeException("Chữ ký momo không hợp lệ");
        }
        String orderId = String.valueOf(momoParams.get("orderId"));
        DepositPayment payment = depositPaymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch thanh toán"));
        if(tenantId != null && !payment.getTenant().getId().equals(tenantId)){
            throw new RuntimeException("Bạn không có quyền xác nhận giao dịch này");
        }
        if(payment.getAmount().compareTo(new BigDecimal(String.valueOf(momoParams.get("amount")))) != 0){
            markFailed(payment, momoParams, "Số tiền momo trả về không khớp với tiền cọc");
            throw new RuntimeException("Số tiền thanh toán không hợp lệ");
        }
        if(payment.getStatus() == PaymentStatus.SUCCESS){
            return payment;
        }
        int resultCode = Integer.parseInt((String) momoParams.get("resultCode")); // nếu resultCode = 0 là thành công
        if(resultCode != 0){
            markFailed(payment, momoParams, "Momo trả về mã thất bại: " + resultCode);
            return payment;
        }
        activeContractAfterDeposit(payment, momoParams);
        return payment;
    }

    private void activeContractAfterDeposit(DepositPayment payment, Map<String, Object> momoParams){
        Contract contract = payment.getContract();
        Room room = contract.getRoom();
        Occupant representative = contract.getRepresentativeOccupant();

        representative.setActive(true); //người đại diện trở thành occupant đang họat động
        occupantRepository.save(representative);

        room.setStatus(RoomStatus.RENTED); // phòng chuyển sang đã cho thuê
        roomRepository.save(room);

        contract.setStatus(ContractStatus.ACTIVE); // hợp đồng chính thưcs có hiệu lực
        contractRepository.save(contract);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setMomoTransId(toNullableString(momoParams.get("transId")));
        payment.setMomoResultCode(Integer.parseInt((String) momoParams.get("resultCode")));
        payment.setMomoMessage(toNullableString(momoParams.get("message")));
        payment.setMomoPayType(toNullableString(momoParams.get("payType")));
        payment.setPaidAt(LocalDateTime.now(APP_ZONE));
        payment.setFailureReason(null);
        depositPaymentRepository.save(payment);
    }

    private void markFailed(DepositPayment payment, Map<String, Object> momoParams, String reason){
        payment.setStatus(PaymentStatus.FAILED);
        payment.setMomoTransId(toNullableString(momoParams.get("transId")));
        payment.setMomoResultCode(Integer.parseInt((String) momoParams.get("resultCode")));
        payment.setMomoMessage(toNullableString(momoParams.get("message")));
        payment.setMomoPayType(toNullableString(momoParams.get("payType")));
        payment.setPaidAt(LocalDateTime.now(APP_ZONE));
        payment.setFailureReason(reason);
        depositPaymentRepository.save(payment);
    }

    private boolean isValidSignature(Map<String, Object> momoParams) {
        String signature = toNullableString(momoParams.get("signature")); // Chữ ký MoMo gửi về.
        if (!StringUtils.hasText(signature)) {
            return false;
        }

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + momoParams.get("amount")
                + "&extraData=" + valueOrEmpty(momoParams.get("extraData"))
                + "&message=" + valueOrEmpty(momoParams.get("message"))
                + "&orderId=" + momoParams.get("orderId")
                + "&orderInfo=" + valueOrEmpty(momoParams.get("orderInfo"))
                + "&orderType=" + valueOrEmpty(momoParams.get("orderType"))
                + "&partnerCode=" + momoParams.get("partnerCode")
                + "&payType=" + valueOrEmpty(momoParams.get("payType"))
                + "&requestId=" + momoParams.get("requestId")
                + "&responseTime=" + momoParams.get("responseTime")
                + "&resultCode=" + momoParams.get("resultCode")
                + "&transId=" + momoParams.get("transId"); // Thứ tự đúng theo docs MoMo cho redirectUrl/ipnUrl.
        String expectedSignature = signHmacSHA256(rawSignature, momoProperties.getSecretKey()); // Tự ký lại bằng secretKey.
        return expectedSignature.equalsIgnoreCase(signature); // So sánh chữ ký RentNest tính với chữ ký MoMo gửi.
    }

    private String toNullableString(Object value){
        return value == null ? null : value.toString();
    }

    private String valueOrEmpty(Object value){
        return value == null ? "" : value.toString();
    }

    private String signHmacSHA256(String data, String key) {
        try {
            // Khởi tạo thuật toán HmacSHA256
            Mac mac = Mac.getInstance("HmacSHA256");

            // Tạo khóa bí mật từ key và bộ mã UTF-8
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

            // Khởi tạo thuật toán với khóa
            mac.init(secretKeySpec);

            // Sinh ra mảng byte chữ ký từ dữ liệu
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Chuyển đổi mảng byte sang chuỗi hex (chuỗi chữ ký)
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // Padding nếu cần
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            // Ném ra lỗi nếu có vấn đề trong quá trình ký
            throw new RuntimeException("Failed to generate HMAC SHA256 signature", e);
        }
    }

    private void validateMomoConfig(){
        if(!StringUtils.hasText(momoProperties.getEndpoint())
                || !StringUtils.hasText(momoProperties.getPartnerCode())
        || !StringUtils.hasText(momoProperties.getAccessKey())
        || !StringUtils.hasText(momoProperties.getSecretKey())
        || !StringUtils.hasText(momoProperties.getRedirectUrl())
        || !StringUtils.hasText(momoProperties.getIpnUrl())){
            throw new RuntimeException("Chưa cấu hình đầy đủ MoMo");
        }
    }
}
