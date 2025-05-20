package com.myproject.qrcode.domain.qrcode.dymanic.contact;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

public class VCardContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob // Anotação para indicar dados binários grandes
    private byte[] QRcodeImg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getQRcodeImg() {
        return QRcodeImg;
    }

    public void setQRcodeImg(byte[] QRcodeImg) {
        this.QRcodeImg = QRcodeImg;
    }
}
