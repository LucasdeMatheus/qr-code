package com.myproject.qrcode.controller;

import com.myproject.qrcode.domain.barcode.DataBarCode;
import com.myproject.qrcode.domain.qrcode.*;
import com.myproject.qrcode.service.CodeGeneretorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class QRCodeController {

    @Autowired
    private CodeGeneretorService codeGeneretorService;



    // qrcode simples --------------------------------------------------------------------------------------------------

    @PostMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody DataQRcode data) {
        try {
            byte[] img = codeGeneretorService.generateQRCodeImage(
                    data.type(),
                    data.content(),
                    data.JsonNode(),
                    data.background(),
                    data.qrCodeColor(),
                    data.expirationDate()
            );

            if (img == null || img.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(img);

        } catch (Exception e) {
            e.printStackTrace();  // ou usar Logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generate-bar")
    public ResponseEntity<byte[]> genarateBarCode(@RequestBody DataBarCode data){
        try {
            byte[] img = codeGeneretorService.generateBarcodeImage(
                    data.code(),
                    data.typeCode()
            );

            if (img == null || img.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(img);

        } catch (Exception e) {
            e.printStackTrace();  // ou usar Logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

