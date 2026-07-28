package com.endustri.arizasistemi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void acilDurumMailiGonder(String kime, String konu, String icerik) {
        SimpleMailMessage mesaj = new SimpleMailMessage();
        mesaj.setFrom("sistem@fabrikam.com"); 
        mesaj.setTo(kime);
        mesaj.setSubject(konu);
        mesaj.setText(icerik);
        mailSender.send(mesaj); 
    }
}