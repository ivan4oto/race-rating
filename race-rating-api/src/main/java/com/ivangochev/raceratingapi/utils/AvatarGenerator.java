package com.ivangochev.raceratingapi.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Slf4j
public class AvatarGenerator {
    public static BufferedImage generateAvatar(String username, int size) {
        log.info("Generating avatar for " + username);
        // Get the first letter (uppercase)
        String initial = username.substring(0, 1).toUpperCase();

        // Create image
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Anti-aliasing for text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set random background color
        Color bgColor = getRandomColor();
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, size, size);

        // Set font and color for text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size / 2));

        // Measure the text
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(initial);
        int textHeight = fm.getAscent();

        // Center the text
        int x = (size - textWidth) / 2;
        int y = (size + textHeight) / 2 - fm.getDescent();

        g2d.drawString(initial, x, y);
        g2d.dispose();

        return image;
    }

    private static Color getRandomColor() {
        Random rand = new Random();
        // Brighter range of RGB to avoid too-dark backgrounds
        int r = 100 + rand.nextInt(156);
        int g = 100 + rand.nextInt(156);
        int b = 100 + rand.nextInt(156);
        return new Color(r, g, b);
    }


}
