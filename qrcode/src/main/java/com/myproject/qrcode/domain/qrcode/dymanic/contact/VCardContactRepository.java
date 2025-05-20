package com.myproject.qrcode.domain.qrcode.dymanic.contact;

import com.myproject.qrcode.domain.qrcode.QRcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VCardContactRepository extends JpaRepository<VCardContact, Long> {
    VCardContact findByName(String name);
}
