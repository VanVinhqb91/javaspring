package vpasound.com.core.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vha_media_files")
@Getter
@Setter
public class MediaFileEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String storedFileName;
    private String diskPath;      // Đường dẫn vật lý trên ổ cứng
    private String url;           // Đường dẫn API hiển thị ảnh
    private String format;        // JPEG, PNG...
    private Integer width;
    private Integer height;
    private String folder;        // wysiwyg, collection, gear...

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore                   // Không trả mảng byte nặng khi lấy danh sách
    private byte[] fileData;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
