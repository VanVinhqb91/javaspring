package vpasound.com.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AddressConverter implements AttributeConverter<AddressSnapshot, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. Chuyển từ Object sang Chuỗi String JSON để lưu xuống Database
    @Override
    public String convertToDatabaseColumn(AddressSnapshot attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi đóng gói chuỗi JSON: " + e.getMessage());
        }
    }

    // 2. Chuyển ngược từ Chuỗi trong DB lên thành Object Java cho Sư phụ dùng
    @Override
    public AddressSnapshot convertToEntityAttribute(String dbData) {
        try {
            return dbData == null || dbData.isBlank() ? null : objectMapper.readValue(dbData, AddressSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi giải mã chuỗi JSON: " + e.getMessage());
        }
    }
}
