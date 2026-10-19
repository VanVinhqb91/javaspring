package vpasound.com.fontend.elastic;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "products")
@Setting(settingPath = "/elastic/es-config.json")
@Data
public class ProductElastic {
	@Id
    private String id;

    // Sử dụng analyzer custom cho tính năng auto-search
    @Field(type = FieldType.Text, analyzer = "autocomplete_analyzer", searchAnalyzer = "standard")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer price;
}
