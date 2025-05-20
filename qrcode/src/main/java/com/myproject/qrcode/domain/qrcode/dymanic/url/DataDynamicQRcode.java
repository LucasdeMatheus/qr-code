package com.myproject.qrcode.domain.qrcode.dymanic.url;

import java.awt.*;
import java.time.LocalDateTime;

public record DataDynamicQRcode(
        String text,
        Color background,
        Color QRcodeColor,
        LocalDateTime expirationDate,
        String urlWindows,
        String urlAndroid,
        String urlIos
) {
    public static record DataEditDynamicQR(
            String text,
            LocalDateTime expirationDate,
            String urlWindows,
            String urlAndroid,
            String urlIos
    ) {
    }
}
