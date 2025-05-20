package com.myproject.qrcode.domain.qrcode;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class QRcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob // Anotação para indicar dados binários grandes
    private byte[] QRcodeImg;
    private String qrcodeContent;
    private LocalDateTime generationDate;
    private LocalDateTime expirationDate;
    private Boolean isActive;

    public byte[] getQRcodeImg() {
        return QRcodeImg;
    }

    public void setQRcodeImg(byte[] QRcoodeImg) {
        this.QRcodeImg = QRcoodeImg;
    }

    public String getQrcodeContent() {
        return qrcodeContent;
    }

    public void setQrcodeContent(String qrcodeContent) {
        this.qrcodeContent = qrcodeContent;
    }

    public LocalDateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(LocalDateTime generationDate) {
        this.generationDate = generationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }



}
