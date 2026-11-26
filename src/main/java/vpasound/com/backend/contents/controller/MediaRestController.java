package vpasound.com.backend.contents.controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vpasound.com.core.entity.MediaFileEntity;
import vpasound.com.core.entity.MediaFolderEntity;
import vpasound.com.core.repository.MediaFileRepository;
import vpasound.com.core.repository.MediaFolderRepository;

@RestController
@RequestMapping("/api/media")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MediaRestController {

    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Value("${app.upload.category.dir}")
    private String uploadCategoryDir;

    private final MediaFileRepository mediaFileRepository;
    private final MediaFolderRepository mediaFolderRepository;

    // 1. Lấy danh sách ảnh
    @GetMapping
    public List<MediaFileEntity> getAllMedia() {
        return mediaFileRepository.findAllByOrderByCreatedAtDesc();
    }

    // 2. Upload ảnh vào ổ cứng và MySQL
    @PostMapping("/upload")
    public List<MediaFileEntity> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "media_gallery") String folder) throws IOException {
        String uploadFolder = "category".equals(folder) ? uploadCategoryDir : uploadDir;
        List<MediaFileEntity> savedList = new ArrayList<>();
        Path folderPath = Paths.get(uploadFolder, folder);
        Files.createDirectories(folderPath);

        for (MultipartFile file : files) {
            byte[] bytes = file.getBytes();
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toUpperCase();
            String storedName = UUID.randomUUID() + "_" + originalName;

            // Lưu file vật lý ra ổ cứng server
            Path targetPath = folderPath.resolve(storedName);
            Files.write(targetPath, bytes);

            // Đọc kích thước thật của ảnh
            BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(bytes));
            int width = (bimg != null) ? bimg.getWidth() : 0;
            int height = (bimg != null) ? bimg.getHeight() : 0;

            // Lưu thông tin + dữ liệu BLOB vào MySQL
            MediaFileEntity media = new MediaFileEntity();
            media.setTitle(originalName.replaceFirst("[.][^.]+$", ""));
            media.setStoredFileName(storedName);
            media.setDiskPath(targetPath.toString());
            media.setFormat(ext);
            media.setWidth(width);
            media.setHeight(height);
            media.setFolder(folder);
            media.setFileData(bytes);
            media.setUrl("temp");

            MediaFileEntity saved = mediaFileRepository.save(media);
            saved.setUrl("/api/media/view/" + saved.getId());
            savedList.add(mediaFileRepository.save(saved));
        }
        return savedList;
    }

    // 3. Đọc ảnh từ ổ cứng (dự phòng đọc từ DB nếu file ổ cứng bị mất)
    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewImage(@PathVariable Long id) throws IOException {
    	MediaFileEntity media = mediaFileRepository.findById(id).orElseThrow();
        Path path = Paths.get(media.getDiskPath());
        
        byte[] imageBytes = Files.exists(path) ? Files.readAllBytes(path) : media.getFileData();
        MediaType contentType = media.getFormat().equals("PNG") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        
        return ResponseEntity.ok().contentType(contentType).body(imageBytes);
    }

    // 4. Xóa ảnh khỏi cả ổ cứng và MySQL
    @DeleteMapping
    public ResponseEntity<Void> deleteImages(@RequestBody List<Long> ids) throws IOException {
        for (Long id : ids) {
        	MediaFileEntity media = mediaFileRepository.findById(id).orElse(null);
            if (media != null) {
                Files.deleteIfExists(Paths.get(media.getDiskPath()));
                mediaFileRepository.delete(media);
            }
        }
        return ResponseEntity.ok().build();
    }
    
 // Xóa thư mục (Xóa trong DB + Xóa ảnh thuộc thư mục đó)
    @DeleteMapping("/folders/{id}")
    public ResponseEntity<?> deleteFolder(@PathVariable Integer id) throws IOException {
        MediaFolderEntity folder = mediaFolderRepository.findById(id).orElse(null);
        if (folder != null) {
            // Xóa tất cả ảnh thuộc thư mục này trong DB và ổ cứng
            List<MediaFileEntity> filesInFolder = mediaFileRepository.findByFolder(folder.getSlug());
            for (MediaFileEntity file : filesInFolder) {
                Files.deleteIfExists(Paths.get(file.getDiskPath()));
                mediaFileRepository.delete(file);
            }
            // Xóa thư mục rỗng trên ổ cứng và xóa bản ghi thư mục trong DB
            Files.deleteIfExists(Paths.get(uploadDir, folder.getSlug()));
            mediaFolderRepository.delete(folder);
        }
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/folders")
    public ResponseEntity<String> createFolderOnServer(@RequestParam("name") String name) throws IOException {
    	MediaFolderEntity mediaFolderEntity = new MediaFolderEntity();
        String cleanName = name.trim().toLowerCase().replaceAll("\\s+", "_");
        Path newFolderPath = Paths.get(uploadDir, cleanName);
        Files.createDirectories(newFolderPath);
        mediaFolderEntity.setName(name);
        mediaFolderEntity.setSlug(cleanName);
        mediaFolderRepository.save(mediaFolderEntity);
        return ResponseEntity.ok(cleanName);
    }
}
