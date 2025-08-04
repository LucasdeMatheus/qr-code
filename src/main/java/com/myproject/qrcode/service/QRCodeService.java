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
import java.util.Map;
import java.util.function.BiConsumer;

@Service
public class QRCodeService {

    @Autowired
    private QRcodeRepository qRcodeRepository;

    public byte[] generateQRCodeImage(
            String type,
            String content,
            Map<String, Object> JsonNode,
            Color background,
            Color qrColor,
            LocalDateTime expirationDate) throws Exception {

        switch (type){
            case "url":
            case "text":
                break;
            case "vcard":
                content = gerarVCardString(JsonNode);
                break;
            case "email":
                content = gerarEmailString(JsonNode);
                break;
            case "geo":
                content = gerarGeoString(JsonNode);
                break;
            case "pix":
                content = gerarPixString(JsonNode); // você gera o payload PIX padrão
                break;
            case "wifi":
                content = gerarWifiString(JsonNode);
        }

        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.MARGIN, 1); // Definir margem do QR Code
        int width = 300;
        int height = 300;
        // Gerar o QR Code como BitMatrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hintMap);

        // Criar uma imagem docker-compose.yml partir do BitMatrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(background); // Cor de fundo
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(qrColor); // Cor do QR Code
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitMatrix.get(i, j)) {
                    image.setRGB(i, j, qrColor.getRGB());
                } else {
                    image.setRGB(i, j, background.getRGB());
                }
            }
        }


        // Converter docker-compose.yml imagem para um array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        QRcode qRcode = new QRcode();
        qRcode.setActive(true);
        qRcode.setQRcodeImg(baos.toByteArray());
        qRcode.setGenerationDate(LocalDateTime.now());
        qRcode.setQrcodeContent(content);

        return baos.toByteArray();
    }

    // vcard
    private String gerarVCardString(Map<String, Object> vcardData) {
        String fn = (String) vcardData.get("fn");
        String org = (String) vcardData.get("org");
        String title = (String) vcardData.get("title");
        String tel = (String) vcardData.get("tel");
        String email = (String) vcardData.get("email");

        String vcardString =
                "BEGIN:VCARD\r\n" +
                        "VERSION:3.0\r\n" +
                        "FN:" + fn + "\r\n" +
                        "ORG:" + org + "\r\n" +
                        "TITLE:" + title + "\r\n" +
                        "TEL;TYPE=CELL:" + tel + "\r\n" +
                        "EMAIL:" + email + "\r\n" +
                        "END:VCARD\r\n";

        return vcardString;
    }



    // email
    private String gerarEmailString(Map<String, Object> emailData) {
        // "mailto:email@exemplo.com?subject=Assunto%20Codificado&body=Corpo%20da%20mensagem%20codificado"

        String email = (String) emailData.get("email");
        String subject = (String) emailData.get("subject");
        String body = (String) emailData.get("body");
        String emailString = "mailto:" + email + "?subject=" + subject.replace(" ", "%20") + "&body="
        + body.replace(" ", "%20");
        return emailString;
    }

    // geo
    private String gerarGeoString(Map<String, Object> geoData) {
        // "geo:-23.561684,-46.625378"
        String latitude  = (String) geoData.get("latitude");
        String longitude   = (String) geoData.get("longitude");
        String geoString = "geo:" + latitude + "," + longitude;
        return geoString;
    }

    // pix (feito pelo geemini kkkk)
    public String gerarPixString(Map<String, Object> pixData) {
        // Extrai e valida os dados de entrada
        String chave = (String) pixData.get("chave");
        String cidade = (String) pixData.get("cidade");
        String nome = (String) pixData.get("nome");
        Object valorObj = pixData.get("valor");
        String txid = (String) pixData.get("txid");

        // Formata o valor para duas casas decimais
        String valor = null;
        if (valorObj != null) {
            valor = String.format("%.2f", Double.parseDouble(valorObj.toString())).replace(",", ".");
        }

        // Define valores padrão se os campos estiverem ausentes e aplica limites
        nome = (nome == null || nome.isEmpty()) ? "NOME DA LOJA" : nome;
        nome = nome.length() > 25 ? nome.substring(0, 25) : nome;

        cidade = (cidade == null || cidade.isEmpty()) ? "SAO PAULO" : cidade;
        cidade = cidade.length() > 15 ? cidade.substring(0, 15) : cidade;

        StringBuilder sb = new StringBuilder();

        // Lambda para simplificar docker-compose.yml adição de campos no payload
        BiConsumer<String, String> appendField = (id, value) -> {
            sb.append(id);
            sb.append(String.format("%02d", value.length()));
            sb.append(value);
        };

        // Adiciona os campos obrigatórios
        appendField.accept("00", "01"); // Payload Format Indicator
        appendField.accept("26", buildMerchantAccountInfo(chave)); // Merchant Account Information
        appendField.accept("52", "0000"); // Merchant Category Code
        appendField.accept("53", "986"); // Currency Code (BRL)

        // Adiciona o valor se ele existir
        if (valor != null) {
            appendField.accept("54", valor);
        }

        appendField.accept("58", "BR"); // Country Code
        appendField.accept("59", nome); // Merchant Name
        appendField.accept("60", cidade); // Merchant City

        // Adiciona dados adicionais (TXID) se existirem
        String additionalData = buildAdditionalData(txid);
        if (!additionalData.isEmpty()) {
            appendField.accept("62", additionalData);
        }

        // Calcula e adiciona o CRC16
        String payloadSemCRC = sb.toString() + "6304";
        String crc16 = calcularCRC16(payloadSemCRC);
        sb.append("6304").append(crc16);

        return sb.toString();
    }

    private String buildMerchantAccountInfo(String chave) {
        StringBuilder merchantInfo = new StringBuilder();
        merchantInfo.append("0014br.gov.bcb.pix");
        merchantInfo.append("01");
        merchantInfo.append(String.format("%02d", chave.length()));
        merchantInfo.append(chave);
        return merchantInfo.toString();
    }

    private String buildAdditionalData(String txid) {
        StringBuilder additionalData = new StringBuilder();
        if (txid != null && !txid.isEmpty()) {
            additionalData.append("05");
            additionalData.append(String.format("%02d", txid.length()));
            additionalData.append(txid);
        }
        return additionalData.toString();
    }

    private String calcularCRC16(String input) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;
        byte[] bytes = input.getBytes();

        for (byte b : bytes) {
            crc ^= (b << 8) & 0xFFFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = ((crc << 1) ^ polynomial) & 0xFFFF;
                } else {
                    crc = (crc << 1) & 0xFFFF;
                }
            }
        }
        return String.format("%04X", crc);
    }

    // wifi
    private String gerarWifiString(Map<String, Object> wifiData) {
        String type = (String) wifiData.get("type"); // ex: WPA, WEP, nopass
        String SSID = (String) wifiData.get("SSID");
        String password = "";

        if (!"nopass".equalsIgnoreCase(type)) {
            password = (String) wifiData.get("password");
            if (password == null) password = "";
        }

        String wifiString;
        if ("nopass".equalsIgnoreCase(type)) {
            // rede aberta, sem senha
            wifiString = "WIFI:T:nopass;S:" + SSID + ";;";
        } else {
            wifiString = "WIFI:T:" + type + ";S:" + SSID + ";P:" + password + ";;";
        }

        return wifiString;
    }


    // expirar qrcode
    @Scheduled(fixedRate = 60 * 60 * 1000) // docker-compose.yml cada 1h
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
