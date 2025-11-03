package com.smartShopper.smart_price_backend.service.email;

import com.smartShopper.smart_price_backend.entity.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("misbashaikh1203@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("✅ Mail sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendPriceDropAlert(Wishlist wishlist) {
        try {
            String toEmail = wishlist.getUser().getEmail();
            String subject = "🔥 Price Drop Alert: " + wishlist.getProduct().getTitle();

            String message = String.format(
                    """
                    Hi %s,
                    
                    Great news! 🎉
                    
                    The price of '%s' has dropped on %s!
                    
                    💰 Current Price: ₹%s
                    🎯 Your Target Price: ₹%s
                    📦 Platform: %s
                    
                    Check it out here: %s
                    
                    Don't miss this deal!
                    
                    Happy Shopping! 💸
                    — Smart Shopper Team
                    """,
                    wishlist.getUser().getName(),
                    wishlist.getProduct().getTitle(),
                    wishlist.getProduct().getPlatform(),
                    wishlist.getProduct().getCurrentPrice(),
                    wishlist.getTargetPrice(),
                    wishlist.getProduct().getPlatform(),
                    wishlist.getProduct().getPlatformProductUrl()
            );

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("misbashaikh1203@gmail.com");
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(message);

            mailSender.send(mail);
            System.out.println("📧 Price drop alert sent to: " + toEmail + " for product: " + wishlist.getProduct().getTitle());
        } catch (Exception e) {
            System.err.println("❌ Failed to send price drop alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
}