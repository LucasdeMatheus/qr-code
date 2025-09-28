package com.myproject.qrcode.domain.barcode;

import com.google.zxing.BarcodeFormat;

public record DataBarCode(
        String code,
        TypeCode typeCode
) {
}
