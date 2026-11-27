package vpasound.com.core.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DatabaseAutoBackupService {
	@Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${app.backup.container-name}")
    private String containerName;
    
    @Value("${app.backup.db-name}")
    private String dbName;

    @Value("${app.backup.folder}")
    private String backupFolder;

    @Scheduled(cron = "0 59 23 */2 * *", zone = "Asia/Ho_Chi_Minh")
    public void autoExportSqlFile() {
    	try {
            // 1. Tạo thư mục public/database neu chua co
            File dir = new File(backupFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. Đặt tên file theo ngày giờ: ten_db_2026-11-27_02-00-00.sql
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            File outputFile = new File(dir, dbName + "_" + timestamp + ".sql");

            // 3. Lệnh gọi Docker Desktop trên Windows (Không dùng -it)
            List<String> command = new ArrayList<>();
            command.add("docker");
            command.add("exec");
            command.add(containerName);
            command.add("mysqldump");
            command.add("-u" + dbUser);
            if (dbPassword != null && !dbPassword.isEmpty()) {
                command.add("-p" + dbPassword);
            }
            command.add("--databases");
            command.add(dbName);

            // 4. Thực thi và đổ dữ liệu thẳng ra file .sql trên ổ D:
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(outputFile); // Ghi dữ liệu SQL ra file
            
            // Tạo file log tạm để bắt lỗi nếu Docker Desktop chưa bật hoặc sai mật khẩu
            File errorLog = new File(dir, "backup_error.log");
            pb.redirectError(errorLog);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0 && outputFile.length() > 0) {
                errorLog.delete(); // Xóa file log lỗi nếu thành công
                System.out.println("✅ [CRON] Backup SQL thành công tại: " + outputFile.getAbsolutePath());
                cleanOldBackups(dir, 7); // Giữ lại bản sao lưu trong 7 ngày gần nhất
            } else {
                System.err.println("❌ [CRON] Backup thất bại! Kiểm tra chi tiết tại: " + errorLog.getAbsolutePath());
                if (outputFile.exists() && outputFile.length() == 0) {
                    outputFile.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi hệ thống khi chạy backup: " + e.getMessage());
            e.printStackTrace();
        }
    }

 // Tự động xóa các file .sql cũ hơn N ngày
    private void cleanOldBackups(File folder, int daysToKeep) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".sql"));
        if (files == null) return;

        long cutoff = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        for (File file : files) {
            if (file.lastModified() < cutoff) {
                file.delete();
            }
        }
    }
}
