package service;

import java.io.*;
import com.google.gson.Gson;

import model.Settings;

/*保存した入力情報を読み取る */
public class LoadService {
    public static Settings loadDataService() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(PathProviderService.getSaveFilePath().toFile())) {
            return gson.fromJson(reader, Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Settings("","");
        }
    }
}
