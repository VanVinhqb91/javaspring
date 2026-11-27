package vpasound.com.core.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "system_cronjob")
public class SystemCronjobEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer id;
	
	@Column(name = "name")
	public String name;
	
	@Column(name = "cron_key", unique = true)
	public String cronKey;
	
	@Column(name = "cron_value")
	public String cronValue;
	
	@CreationTimestamp
    private LocalDateTime createdAt;
	
	@Column(name = "status")
	public Boolean status;
}
