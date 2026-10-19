package vpasound.com.core.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vha_orders")
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	public Long id;
	
	@Column(name = "user_id")
	public Integer userId;
	
	@Column(name = "order_code")
	public Integer orderCode;
	
	@Column(name = "total_price")
	public Integer totalPrice;
	
	@Column(name = "payment_method")
	public Integer paymentMethod;
	
	@Column(name = "payment_status")
	public Integer paymentStatus;
	
	@Column(name = "status")
	public String status;
	
	@Column(name = "billing_address")
	public String billingAddress;
	
	@Column(name = "shipping_address")
	public String shippingAddress;
	
	@Column(name = "note")
	public String note;
	
	@Column(name = "created_at")
	public Date createdAt;
	
	@Column(name = "updated_at")
	public Date updatedAt;
	
	@Column(name = "shipping_fee")
	public Integer shippingFee;
	
	@Column(name = "discount_amount")
	public Integer discountAmount;
	
	@Column(name = "final_price")
	public Double finalPrice;
	
	@Column(name = "receiver_name")
	public String receiverName;
}