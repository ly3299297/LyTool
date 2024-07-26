package com.ly.gpt.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

public class Base64ToImage {
    public static void convertBase64ToImage(String base64ImageString, String pathToFile)  {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imageBytes = decoder.decode(base64ImageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            bis.close();
            File outputFile = new File(pathToFile);
            ImageIO.write(image, "png", outputFile); // 可以改为jpeg, bmp, gif, png等格式
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
 
    public static void main(String[] args) {
        // 示例Base64编码的图片字符串
        String base64Image = "iVBOR..."; // 这里应该是完整的Base64编码字符串
        // 图片要保存的路径
        String outputPath = "output.png";
 
        try {
            convertBase64ToImage(base64Image, outputPath);
            System.out.println("图片转换成功，保存路径：" + outputPath);
        } catch (Exception e) {
            System.out.println("转换过程中发生错误：" + e.getMessage());
        }
    }
}