package gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) {
        try {
            BufferedImage img = ImageIO.read(new File("./img/nicoico.png"));
            if (img != null) {
                System.out.println("読み込み成功！ 幅: " + img.getWidth() + " 高さ: " + img.getHeight());
            } else {
                System.out.println("読み込み失敗：nullが返されました");
            }
        } catch (IOException e) {
            System.out.println("IOException発生：画像が読み込めません");
            e.printStackTrace();
        }
    }
}