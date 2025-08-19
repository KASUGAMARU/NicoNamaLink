package service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FileNameService {

    public static Optional<String> fetchTitle(JFrame frame, String url) {
        if (url == null || url.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "URLを入力してください", "入力エラー", JOptionPane.WARNING_MESSAGE);
            return Optional.empty();
        }
        try {
            Document document = Jsoup.connect(url).get();
            String title = document.title();
            if(title.contains("上映会")){
                String[] parts = title.split("上映会");
                title = parts[0];
            }

            return Optional.of(title);
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(frame, "無効なURLです", "URL入力エラー", JOptionPane.ERROR_MESSAGE);
        } catch (HttpStatusException e) {
            JOptionPane.showMessageDialog(frame, "有効なページが見つかりません", "HTTP接続エラー", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "接続に失敗しました", "通信エラー", JOptionPane.ERROR_MESSAGE);
        }
        return Optional.empty();
    }

}
