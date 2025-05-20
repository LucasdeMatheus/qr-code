package com.myproject.qrcode.domain.qrcode;

import java.awt.*;
import java.time.LocalDateTime;

public record DataQRcode(
        String text,
        Color background,
        Color QRcodeColor,
        LocalDateTime expirationDate
) {
}
