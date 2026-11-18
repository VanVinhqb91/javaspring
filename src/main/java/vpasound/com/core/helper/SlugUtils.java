package vpasound.com.core.helper;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SlugUtils {
	private static final Pattern NONLATIN = Pattern.compile("[^a-zA-Z0-9-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // Xử lý chữ Đ/đ của tiếng Việt
        String temp = input.replace("đ", "d").replace("Đ", "D");
        
        // Tách dấu tiếng Việt và loại bỏ
        String normalized = Normalizer.normalize(temp, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        // Chuyển khoảng trắng thành gạch ngang và xóa ký tự đặc biệt
        String nowhitespace = WHITESPACE.matcher(withoutAccents).replaceAll("-");
        String slug = NONLATIN.matcher(nowhitespace).replaceAll("");
        
        // Xóa nhiều dấu gạch ngang liên tiếp và cắt gạch ngang ở 2 đầu
        return slug.toLowerCase(Locale.ENGLISH)
                   .replaceAll("-{2,}", "-")
                   .replaceAll("^-|-$", "");
    }
}
