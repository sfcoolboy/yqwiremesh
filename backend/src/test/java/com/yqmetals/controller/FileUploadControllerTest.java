package com.yqmetals.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUploadControllerTest {

    private static final int MAX_FILE_SIZE = 3 * 1024 * 1024;

    @TempDir
    Path uploadDir;

    private FileUploadController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new FileUploadController();
        Field uploadDirField = FileUploadController.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(controller, uploadDir.toString());
    }

    @Test
    void rejectsFilesLargerThanThreeMegabytes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "too-large.png", "image/png", new byte[MAX_FILE_SIZE + 1]);

        ResponseEntity<?> response = controller.upload(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("图片太大，请控制在3MB以内上传", ((Map<?, ?>) response.getBody()).get("error"));
        try (var files = Files.list(uploadDir)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void storesValidImageWithinThreeMegabytes() throws Exception {
        byte[] input = createNoisePng(700);
        assertTrue(input.length <= MAX_FILE_SIZE);
        MockMultipartFile file = new MockMultipartFile(
                "file", "valid.png", "image/png", input);

        ResponseEntity<?> response = controller.upload(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        try (var files = Files.list(uploadDir)) {
            Path storedFile = files.findFirst().orElseThrow();
            assertTrue(Files.size(storedFile) <= MAX_FILE_SIZE);
        }
    }

    private byte[] createNoisePng(int size) throws Exception {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Random random = new Random(20261112L);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                image.setRGB(x, y, random.nextInt(0x1000000));
            }
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(image, "PNG", output));
        assertTrue(output.size() > 0);
        return output.toByteArray();
    }
}
