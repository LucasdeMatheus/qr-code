package com.myproject.qrcode.service;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.myproject.qrcode.domain.qrcode.QRcode;
import com.myproject.qrcode.domain.qrcode.QRcodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

@Service
public class QRCodeService {

    @Autowired
    private QRcodeRepository qRcodeRepository;

    public byte[] generateQRCodeImage(String text, int width, int height, Color background, Color QRcodeColor) throws Exception {
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.MARGIN, 1); // Definir margem do QR Code

        // Gerar o QR Code como BitMatrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

        // Criar uma imagem a partir do BitMatrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(background); // Cor de fundo
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(QRcodeColor); // Cor do QR Code
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitMatrix.get(i, j)) {
                    image.setRGB(i, j, QRcodeColor.getRGB());
                } else {
                    image.setRGB(i, j, background.getRGB());
                }
            }
        }


        // Converter a imagem para um array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }


    // expirar qrcode
    @Scheduled(fixedRate = 60 * 60 * 1000) // a cada 1h
    public void verificarQRCodesExpirados() {
        List<QRcode> codigos = qRcodeRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();

        for (QRcode codigo : codigos) {
            if (codigo.getExpirationDate().isBefore(agora) && codigo.getActive()) {
                codigo.setActive(false);
                qRcodeRepository.save(codigo);
            }
        }
    }
}
