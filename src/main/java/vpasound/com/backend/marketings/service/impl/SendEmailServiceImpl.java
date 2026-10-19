package vpasound.com.backend.marketings.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.TemplateEngine;

import lombok.AllArgsConstructor;
import vpasound.com.backend.marketings.service.SendEmailService;

@Service
@AllArgsConstructor
public class SendEmailServiceImpl implements SendEmailService{

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	
	@Override
	public void sendOrderConfirmationEmail(
			String toEmail, 
			String customerName, 
			String orderId, 
			String totalPrice, 
			String paymentStatus
	) {
		try {
			Context context = new Context();
			context.setVariable("customerName", customerName);
			context.setVariable("orderId", orderId);
			context.setVariable("totalPrice", totalPrice);
			context.setVariable("paymentStatus", paymentStatus);
			
			String htmlContent = templateEngine.process("/backend/marketings/email/order_email", context);
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject("Xác Nhận Đơn Hàng Thành Công #" + orderId);
			helper.setText(htmlContent, true); // Tham số true bắt buộc để hiển thị định dạng HTML

			// 4. Thực hiện gửi mail
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Lỗi xảy ra khi gửi email xác nhận đơn hàng: " + e.getMessage(), e);
		}
	}
}
