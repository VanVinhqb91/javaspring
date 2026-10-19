package vpasound.com.backend.marketings.service;

public interface SendEmailService {
	public void sendOrderConfirmationEmail(
			String toEmail, 
			String customerName, 
			String orderId, 
			String totalPrice, 
			String paymentStatus
	);
}
