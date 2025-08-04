package com.myproject.qrcode.domain.qrcode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.myproject.qrcode.service.ColorDeserializer;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;

public record DataQRcode(
        String type,
        Map<String, Object> JsonNode,
        String content,
        @JsonDeserialize(using = ColorDeserializer.class)
        Color background,
        @JsonDeserialize(using = ColorDeserializer.class)
        Color qrCodeColor,
        LocalDateTime expirationDate
) {}
