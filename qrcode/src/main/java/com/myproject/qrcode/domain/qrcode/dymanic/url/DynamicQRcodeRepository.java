package com.myproject.qrcode.domain.qrcode.dymanic.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicQRcodeRepository extends JpaRepository<DynamicQRcode, Long> {
}
