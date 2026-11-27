package vpasound.com.backend.systems.helper;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vpasound.com.core.repository.SystemCronjobRepository;

@Component
public class CronjobBackupDatabaseHelper extends CronjobBaseDynamicHelper {

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

    public static final String CRON_KEY = "backup_database_cronjob";
    
    public CronjobBackupDatabaseHelper(SystemCronjobRepository systemCronjobRepository) {
    	super(systemCronjobRepository);
    }

    @Override
    public String getCronKey() {
        return CRON_KEY;
    }

    @Override
    public void executeTask() {
        try {
            File dir = new File(backupFolder);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = LocalDateTime.now(VIETNAM_ZONE).format(DateTimeFormatter.ofPattern("ddMMyyyy_HH-mm-ss"));
            File outputFile = new File(dir, dbName + "_" + timestamp + ".sql");

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

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(outputFile);

            File errorLog = new File(dir, "backup_error.log");
            pb.redirectError(errorLog);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0 && outputFile.length() > 0) {
                errorLog.delete();
                System.out.println("✅ [CRON] Backup SQL thành công: " + outputFile.getAbsolutePath());
                cleanOldBackups(dir, 7);
            } else {
                System.err.println("❌ [CRON] Backup thất bại! Xem log: " + errorLog.getAbsolutePath());
                if (outputFile.exists() && outputFile.length() == 0) outputFile.delete();
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi hệ thống khi chạy backup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanOldBackups(File folder, int daysToKeep) {
    	File[] files = folder.listFiles(file -> file.isFile() && file.getName().endsWith(".sql"));
        if (files == null) return;
        
        long cutoff = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        for (File file : files) {
            if (file.lastModified() < cutoff) file.delete();
        }
    }
}
