<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông báo trạng thái yêu cầu thuê phòng</title>
    <style>
        body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; color: #333333; }
        .email-wrapper { width: 100%; background-color: #f4f7f6; padding: 40px 20px; }
        .email-content { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .email-header { background-color: #0F52BA; padding: 30px 20px; text-align: center; }
        .email-header h1 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 600; }
        .email-body { padding: 30px; line-height: 1.6; }
        .email-body h2 { font-size: 18px; color: #333333; margin-top: 0; }
        .info-box { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 20px; margin: 20px 0; }
        .info-row { margin-bottom: 10px; border-bottom: 1px dashed #cbd5e1; padding-bottom: 10px; }
        .info-row:last-child { margin-bottom: 0; border-bottom: none; padding-bottom: 0; }
        .info-label { font-weight: 600; color: #64748b; display: inline-block; width: 150px; }
        .info-value { color: #0f172a; font-weight: 500; }
        .status-box { border-radius: 6px; padding: 18px; margin: 20px 0; font-weight: 500; }
        .status-approved { background-color: #ecfdf5; border-left: 4px solid #10B981; color: #065f46; }
        .status-rejected { background-color: #fef2f2; border-left: 4px solid #ef4444; color: #991b1b; }
        .reason-box { background-color: #fffbeb; border-left: 4px solid #f59e0b; padding: 15px; margin-top: 15px; font-style: italic; color: #475569; }
        .cta-button { display: block; text-align: center; margin: 30px 0 10px; }
        .cta-button a { background-color: #10B981; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 6px; font-weight: 600; font-size: 16px; display: inline-block; }
        .email-footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 13px; color: #94a3b8; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
<div class="email-wrapper">
    <div class="email-content">
        <div class="email-header">
            <h1>RentNest</h1>
        </div>

        <div class="email-body">
            <h2>Xin chào <span>${tenantName!"Khách hàng"}</span>,</h2>

            <#if requestStatus == "APPROVED">
                <p>Yêu cầu thuê phòng của bạn đã được chủ nhà phê duyệt.</p>

                <div class="status-box status-approved">
                    Trạng thái: Đã được duyệt
                </div>
            <#elseif requestStatus == "REJECTED">
                <p>Rất tiếc, yêu cầu thuê phòng của bạn đã bị từ chối.</p>

                <div class="status-box status-rejected">
                    Trạng thái: Đã bị từ chối
                </div>
            </#if>

            <div class="info-box">
                <div class="info-row">
                    <span class="info-label">Phòng yêu cầu:</span>
                    <span class="info-value">${roomName} - ${hostelName}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Chủ nhà:</span>
                    <span class="info-value">${landlordName!"Chủ nhà"}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Ngày dọn vào:</span>
                    <span class="info-value">${expectedMoveInDate}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Trạng thái:</span>
                    <span class="info-value">
                        <#if requestStatus == "APPROVED">
                            Đã được duyệt
                        <#elseif requestStatus == "REJECTED">
                            Đã bị từ chối
                        <#else>
                            ${requestStatus}
                        </#if>
                    </span>
                </div>
            </div>

            <#if requestStatus == "REJECTED">
                <p style="margin-bottom: 5px; font-weight: 600;">Lý do từ chối:</p>
                <div class="reason-box">
                    "${rejectReason!"Không phù hợp với tiêu chí hiện tại"}"
                </div>
            </#if>

            <#if requestStatus == "APPROVED">
                <p style="margin-top: 25px;">
                    Bạn có thể đăng nhập vào hệ thống để xem chi tiết yêu cầu và tiếp tục các bước tiếp theo.
                </p>

                <div class="cta-button">
                    <a href="${actionUrl}" target="_blank">Xem Chi Tiết Yêu Cầu</a>
                </div>
            <#else>
                <p style="margin-top: 25px;">
                    Bạn có thể tiếp tục tìm kiếm các phòng phù hợp khác trên hệ thống RentNest.
                </p>

                <div class="cta-button">
                    <a href="${actionUrl}" target="_blank">Tìm Phòng Khác</a>
                </div>
            </#if>
        </div>

        <div class="email-footer">
            <p>Đây là email tự động từ hệ thống RentNest. Vui lòng không trả lời email này.</p>
            <p>&copy; 2026 RentNest. Quản lý nhà trọ thông minh.</p>
        </div>
    </div>
</div>
</body>
</html>