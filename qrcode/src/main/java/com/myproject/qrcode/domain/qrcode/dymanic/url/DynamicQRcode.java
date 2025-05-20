package com.myproject.qrcode.domain.qrcode.dymanic.url;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DynamicQRcode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Anotação para indicar dados binários grandes
    private byte[] QRcodeImg;

    private String qrcodeContent;

    private String urlWindows;

    private String urlAndroid;

    private String urlIos;

    private LocalDateTime generationDate;

    private LocalDateTime expirationDate;

    private Boolean isActive;

    public DynamicQRcode() {
        this.id = null;
        this.qrcodeContent = null;
        this.urlWindows= null;
        this.urlAndroid = null;
        this.urlIos = null;
        this.QRcodeImg = null;
        this.generationDate = null;
        this.expirationDate = null;
        this.isActive = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getQRcodeImg() {
        return QRcodeImg;
    }

    public void setQRcodeImg(byte[] QRcodeImg) {
        this.QRcodeImg = QRcodeImg;
    }

    public String getQrcodeContent() {
        return qrcodeContent;
    }

    public void setQrcodeContent(String qrcodeContent) {
        this.qrcodeContent = qrcodeContent;
    }

    public String getUrlWindows() {
        return urlWindows;
    }

    public void setUrlWindows(String urlWindows) {
        this.urlWindows = urlWindows;
    }

    public String getUrlAndroid() {
        return urlAndroid;
    }

    public void setUrlAndroid(String urlAndroid) {
        this.urlAndroid = urlAndroid;
    }

    public String getUrlIos() {
        return urlIos;
    }

    public void setUrlIos(String urlIos) {
        this.urlIos = urlIos;
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
