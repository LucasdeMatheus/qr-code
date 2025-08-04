package com.myproject.qrcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String text) {
        try {
            // Definir o tamanho do QR Code
            int width = 300;
            int height = 300;

            // Gerar o QR Code como um array de bytes
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(text, width, height);

            // Retornar a imagem QR Code como resposta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(qrCodeImage);
        } catch (Exception e) {
            // Retornar uma resposta com erro em caso de exceção
            return ResponseEntity.status(500).body(null);
        }
    }
}
