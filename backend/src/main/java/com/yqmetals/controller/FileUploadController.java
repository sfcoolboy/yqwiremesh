package com.yqmetals.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/uploads")
public class FileUploadController {

    @Value("${app.upload.dir:L:/????/AI??/international trade website/uploads/}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 3L * 1024 * 1024;
    private static final int MAX_DIMENSION = 3000;
    private static final int TARGET_MAX_WIDTH = 2400;
    private static final int TARGET_MAX_HEIGHT = 2400;

    @PostMapping("/image")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        String ext = getFileExtension(file.getOriginalFilename());
        if (!isValidImageExt(ext)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
        }
        long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "图片太大，请控制在3MB以内上传"));
        }
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
            }
            BufferedImage processed = compress(original, ext);
            String newName = UUID.randomUUID() + "." + ext;
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(newName);
            byte[] bytes = encodeImage(processed, ext);
            int resizeAttempts = 0;
            while (bytes.length > MAX_FILE_SIZE && resizeAttempts < 6) {
                double ratio = Math.min(0.9,
                        Math.max(0.1, Math.sqrt((double) MAX_FILE_SIZE / bytes.length) * 0.9));
                int width = Math.max(1, (int) Math.round(processed.getWidth() * ratio));
                int height = Math.max(1, (int) Math.round(processed.getHeight() * ratio));
                processed = scale(processed, width, height);
                bytes = encodeImage(processed, ext);
                resizeAttempts++;
            }
            if (bytes.length > MAX_FILE_SIZE) {
                return ResponseEntity.status(413)
                        .body(Map.of("error", "图片压缩后仍超过3MB，请压缩后重试"));
            }
            Files.write(target, bytes);
            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/" + newName);
            result.put("filename", newName);
            if (bytes.length < size) result.put("compressed", "true");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSize() {
        return ResponseEntity.status(413)
                .body(Map.of("error", "图片太大，请控制在3MB以内上传"));
    }

    private BufferedImage compress(BufferedImage src, String ext) throws IOException {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w > MAX_DIMENSION || h > MAX_DIMENSION) {
            double ratio = Math.min((double) TARGET_MAX_WIDTH / w, (double) TARGET_MAX_HEIGHT / h);
            BufferedImage resized = scale(src, (int)(w * ratio), (int)(h * ratio));
            return isJpeg(ext) ? qualityCompress(resized, 0.85f) : resized;
        }
        return isJpeg(ext) ? qualityCompress(src, 0.9f) : src;
    }

    private byte[] encodeImage(BufferedImage image, String ext) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = ext.equalsIgnoreCase("png") ? "PNG" : "JPEG";
        if (!ImageIO.write(image, format, baos)) {
            throw new IOException("Unsupported image format: " + format);
        }
        return baos.toByteArray();
    }

    private BufferedImage scale(BufferedImage src, int tw, int th) {
        BufferedImage dst = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, tw, th, null);
        g.dispose();
        return dst;
    }

    private BufferedImage qualityCompress(BufferedImage src, float q) throws IOException {
        float quality = q;
        ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
        writer.setOutput(ios);
        for (int i = 0; i < 10; i++) {
            baos.reset();
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(src, null, null), param);
            ios.flush();
            if (baos.size() <= MAX_FILE_SIZE) break;
            quality -= 0.08f;
            if (quality <= 0.1f) { quality = 0.1f; break; }
        }
        writer.dispose();
        ios.close();
        return ImageIO.read(new java.io.ByteArrayInputStream(baos.toByteArray()));
    }

    private boolean isJpeg(String ext) {
        return "jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext);
    }

    private boolean isValidImageExt(String ext) {
        return ext != null && List.of("jpg","jpeg","png","webp","gif").contains(ext.toLowerCase());
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "jpg";
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
