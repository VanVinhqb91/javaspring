package vpasound.com.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "vha_user_product_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProductActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "vpa_activity_table_gen")
    @TableGenerator(
        name = "vpa_activity_table_gen",
        table = "vha_product_kafka_sequences",              
        pkColumnName = "seq_name",
        valueColumnName = "seq_count",
        pkColumnValue = "user_activity_id",
        allocationSize = 5               
    )
    private Long id;


    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private Integer price;
    
    @Column(name = "qty")
    private Integer qty;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(name = "timestamp")
    private String timestamp;

}
