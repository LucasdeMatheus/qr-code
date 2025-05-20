package com.myproject.qrcode.controller;

import com.myproject.qrcode.domain.qrcode.dymanic.contact.DataVCardContact;
import com.myproject.qrcode.domain.qrcode.dymanic.contact.VCardContact;
import com.myproject.qrcode.domain.qrcode.dymanic.contact.VCardContactRepository;
import com.myproject.qrcode.domain.qrcode.dymanic.url.DataDynamicQRcode;
import com.myproject.qrcode.domain.qrcode.dymanic.url.DynamicQRcode;
import com.myproject.qrcode.domain.qrcode.dymanic.url.DynamicQRcodeRepository;
import com.myproject.qrcode.domain.qrcode.DataQRcode;
import com.myproject.qrcode.domain.qrcode.QRcode;
import com.myproject.qrcode.domain.qrcode.QRcodeRepository;
import com.myproject.qrcode.service.QRCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDateTime;

@RestController
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private QRcodeRepository qRcodeRepository;

    @Autowired
    private DynamicQRcodeRepository dynamicQRcodeRepository;

    @Autowired
    private VCardContactRepository vCardContactRepository;

    // qrcode simples --------------------------------------------------------------------------------------------------
    @GetMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody DataQRcode dataQRcode) {
        QRcode qRcode = new QRcode();

        try {
            // Definir o tamanho do QR Code
            int width = 300;
            int height = 300;

            // Gerar e salvar qrcode no banco de dados
            qRcode.setQRcodeImg(qrCodeService.generateQRCodeImage(dataQRcode.text(), width, height, dataQRcode.background(), dataQRcode.QRcodeColor()));
            qRcode.setQrcodeContent(dataQRcode.text());
            qRcode.setGenerationDate(LocalDateTime.now());
            qRcode.setExpirationDate(dataQRcode.expirationDate());
            qRcode.setActive(Boolean.TRUE);

            qRcodeRepository.save(qRcode);

            // Retornar a imagem QR Code como resposta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(qRcode.getQRcodeImg());
        } catch (Exception e) {
            // Retornar uma resposta com erro em caso de exceção
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/r/qrcode/{id}")
    public ResponseEntity<byte[]> returnQRcode(@PathVariable("id") Long id){
        QRcode qRcode = qRcodeRepository.findById(id).get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(qRcode.getQRcodeImg());
    }


    // qrcode dinamico --------------------------------------------------------------------------------------------------
    @GetMapping("/generate-dynamic-qr")
    public ResponseEntity<byte[]> generateDynamicQRCode(@RequestBody DataDynamicQRcode dataDynamicQRcode) {
        DynamicQRcode dynamicQRcode = new DynamicQRcode();

        try {
            dynamicQRcode = dynamicQRcodeRepository.save(dynamicQRcode);
            // Definir o tamanho do QR Code
            int width = 300;
            int height = 300;

            // url para direcionar para o get
            String localUrl = "http://localhost:8080/qrcode/dynamic/" + dynamicQRcode.getId();

            // Gerar o QR Code como um array de bytes
            dynamicQRcode.setQRcodeImg(qrCodeService.generateQRCodeImage(localUrl, width, height, dataDynamicQRcode.background(), dataDynamicQRcode.QRcodeColor()));

            // url pra caso de erros da plataforma
            dynamicQRcode.setQrcodeContent(dataDynamicQRcode.text());

            // verificar se os campos de urls dinamica estão vazios
            if (dataDynamicQRcode.urlIos() != null && !dataDynamicQRcode.urlIos().isEmpty()) {
                dynamicQRcode.setUrlIos(dataDynamicQRcode.urlIos());
            }
            if (dataDynamicQRcode.urlAndroid() != null && !dataDynamicQRcode.urlAndroid().isEmpty()) {
                dynamicQRcode.setUrlAndroid(dataDynamicQRcode.urlAndroid());
            }
            if (dataDynamicQRcode.urlWindows() != null && !dataDynamicQRcode.urlWindows().isEmpty()) {
                dynamicQRcode.setUrlWindows(dataDynamicQRcode.urlWindows());
            }

            // salva no DB
            dynamicQRcode.setGenerationDate(LocalDateTime.now());
            dynamicQRcode.setExpirationDate(dataDynamicQRcode.expirationDate());
            dynamicQRcode.setActive(Boolean.TRUE);

            dynamicQRcodeRepository.save(dynamicQRcode);

            // Retornar a imagem QR Code como resposta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(dynamicQRcode.getQRcodeImg());
        } catch (Exception e) {
            // Retornar uma resposta com erro em caso de exceção
            return ResponseEntity.status(500).body(null);
        }
    }

    // retorna qrcode dinamico salvo
    @GetMapping("/r/qrcode/dynamic/{id}")
    public ResponseEntity<String> returnContent(@PathVariable("id") Long id, HttpServletRequest request){
        DynamicQRcode dynamicQRcode = dynamicQRcodeRepository.getReferenceById(id);

        // url principal para caso de erros
        String content = dynamicQRcode.getQrcodeContent();

        // verifica a plataforma
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        System.out.println(userAgent);

        if (userAgent.contains("android") && dynamicQRcode.getUrlAndroid() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, dynamicQRcode.getUrlAndroid())
                    .build();
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod") && dynamicQRcode.getUrlIos() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, dynamicQRcode.getUrlIos())
                    .build();
        } else if (userAgent.contains("windows") && dynamicQRcode.getUrlWindows() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, dynamicQRcode.getUrlWindows())
                    .build();
        }


        return ResponseEntity.ok(content);
    }

    // editar o qrcode dinamico
    @PutMapping("/edit/qrcode/dynamic/{id}")
    public ResponseEntity<Boolean> updateDynamicQRCode(@PathVariable("id") Long id, @RequestBody DataDynamicQRcode.DataEditDynamicQR data) {
        DynamicQRcode dynamicQRcode = dynamicQRcodeRepository.getReferenceById(id);

        // verifica e atribui as edições
        if (data.text() != null && !data.text().isBlank()) {
            dynamicQRcode.setQrcodeContent(data.text());
        }
        if (data.expirationDate() != null) {
            dynamicQRcode.setExpirationDate(data.expirationDate());
        }
        if (data.urlWindows() != null && !data.urlWindows().isBlank()) {
            dynamicQRcode.setUrlWindows(data.urlWindows());
        }
        if (data.urlAndroid() != null && !data.urlAndroid().isBlank()) {
            dynamicQRcode.setUrlAndroid(data.urlAndroid());
        }
        if (data.urlIos() != null && !data.urlIos().isBlank()) {
            dynamicQRcode.setUrlIos(data.urlIos());
        }

        dynamicQRcodeRepository.save(dynamicQRcode);
        return ResponseEntity.ok(true);
    }

    // qrcode para Contato ---------------------------------------------------------------------------------------------

    @PostMapping("/qrcode/vcard")
    public ResponseEntity<byte[]> generateVCard(@RequestBody DataVCardContact dataVCardContact) {
        VCardContact vCardContact = new VCardContact();
        try {

            String vCardContent = dataVCardContact.generateVCardContent();
            byte[] qrCode = qrCodeService.generateQRCodeImage(
                    vCardContent,
                    300,
                    300,
                    Color.WHITE,
                    Color.RED
            );

            // salvando no banco de dados
            vCardContact.setName(dataVCardContact.fullName());
            vCardContact.setQRcodeImg(qrCode);
            vCardContactRepository.save(vCardContact);

            // retorna o qr code com o conteudo do VCardContact
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/r/qrcode/vcard/{name}")
    public ResponseEntity<byte[]> returnVCard(@PathVariable String name){
        VCardContact vCardContact = vCardContactRepository.findByName(name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(vCardContact.getQRcodeImg());
    }
}

