package com.myproject.qrcode.domain.qrcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRcodeRepository extends JpaRepository<QRcode, Long> {
}
