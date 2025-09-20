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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }



}
