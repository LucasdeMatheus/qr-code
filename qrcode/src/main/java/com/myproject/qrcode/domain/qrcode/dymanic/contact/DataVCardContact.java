package com.myproject.qrcode.domain.qrcode.dymanic.contact;

public record DataVCardContact(
        String fullName,
        String organization,
        String title,
        String phone,
        String email
) {

    public String generateVCardContent() {
        return """
    BEGIN:VCARD
    VERSION:3.0
    FN:%s
    ORG:%s
    TITLE:%s
    TEL;TYPE=CELL:%s
    EMAIL:%s
    END:VCARD
    """.formatted(
                fullName,
                organization,
                title,
                phone,
                email
        );
    }
}

