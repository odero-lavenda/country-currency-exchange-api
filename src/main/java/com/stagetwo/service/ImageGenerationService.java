package com.stagetwo.service;


import com.stagetwo.entity.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ImageGenerationService {
    private static final String CACHE_DIR = "cache";
    private static final String IMAGE_FILE = "summary.png";
    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 600;

    public void generateSummaryImage(long totalCountries, List<Country> topCountries, LocalDateTime lastRefreshed) {
        try {
            // Create cache directory if it doesn't exist
            Path cachePath = Paths.get(CACHE_DIR);
            if (!Files.exists(cachePath)) {
                Files.createDirectories(cachePath);
            }

            BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background
            g2d.setColor(new Color(240, 248, 255));
            g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

            // Header
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(0, 0, IMAGE_WIDTH, 80);

            // Title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.drawString("Country Data Summary", 50, 50);

            // Total countries
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Total Countries: " + totalCountries, 50, 130);

            // Top 5 countries header
            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            g2d.drawString("Top 5 Countries by Estimated GDP:", 50, 180);

            // Draw top countries
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            int yPos = 220;
            int rank = 1;

            for (Country country : topCountries) {
                String gdpFormatted = String.format("%.2f", country.getEstimatedGdp());
                String text = String.format("%d. %s - $%s", rank++, country.getName(), gdpFormatted);
                g2d.drawString(text, 70, yPos);
                yPos += 35;
            }

            // Last refreshed
            g2d.setFont(new Font("Arial", Font.ITALIC, 16));
            g2d.setColor(new Color(100, 100, 100));
            String timestamp = lastRefreshed.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            g2d.drawString("Last Refreshed: " + timestamp, 50, IMAGE_HEIGHT - 30);

            g2d.dispose();

            // Save image
            File outputFile = new File(CACHE_DIR + File.separator + IMAGE_FILE);
            ImageIO.write(image, "png", outputFile);

            log.info("Summary image generated successfully at {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to generate summary image", e);
            throw new RuntimeException("Failed to generate summary image", e);
        }
    }

    public File getSummaryImage() {
        File imageFile = new File(CACHE_DIR + File.separator + IMAGE_FILE);
        return imageFile.exists() ? imageFile : null;
    }
}
