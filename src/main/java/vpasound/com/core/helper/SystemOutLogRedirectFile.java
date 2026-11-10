package vpasound.com.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Component
public class SystemOutLogRedirectFile {

    private static final Logger printLogger = LoggerFactory.getLogger("PRINT_LN_FILE");
    
    @EventListener(ApplicationReadyEvent.class)
    public void redirectSystemOut() {

        System.setOut(new PrintStream(new OutputStream() {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    String line = buffer.toString(StandardCharsets.UTF_8).trim();
                    
                    if (!line.isEmpty()) {
                        // 🌟 BÍ QUYẾT TRUY VẾT CAO CẤP: Lấy dấu vết ngăn xếp xem ai là người gọi lệnh in này
                        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                        boolean isYourCode = false;

                        for (StackTraceElement element : stackTrace) {
                            String className = element.getClassName();
                            // Chỉ nhận diện các dòng in xuất phát từ package gốc của bạn (vpasound.com)
                            // Và loại trừ chính cái file Redirect này để tránh vòng lặp vô hạn
                            if (className.startsWith("vpasound.com") 
                                    && !className.contains("SystemOutLogRedirectFile")) {
                                isYourCode = true;
                                break;
                            }
                        }

                        // 🚫 CHẶN LỌC TUYỆT ĐỐI: 
                        // Bỏ qua biểu tượng Banner Spring Boot, dải gạch ngang (====), log Hibernate DDL và Exception hệ thống
                        if (line.startsWith(":: Spring Boot ::") || 
                            line.contains("alter table") || 
                            line.contains("modify column") || 
                            line.contains("Hibernate:") || 
                            line.contains("Caused by:") || 
                            line.trim().startsWith("at ") || 
                            line.contains("Process terminated") ||
                            line.startsWith("2026-") ||
                            !isYourCode) { // Nếu không phải do bạn chủ động gõ println trong code -> HỦY LUÔN!
                            
                            buffer.reset();
                            return;
                        }
                        printLogger.info(line);
                    }
                    buffer.reset();
                } else if (b != '\r') {
                    buffer.write(b);
                }
            }
        }, true));
        
        System.out.println("🎉 [SYSTEM-OUT] Đã kích hoạt bộ lọc StackTrace tối cao, quét sạch 100% rác Spring và Hibernate!");
    }
}
