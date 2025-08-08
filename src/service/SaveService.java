package service;

import java.io.*;
import com.google.gson.*;

import model.Settings;

/*入力情報を保存する */
public class SaveService {
    public static void saveDataServie(String session, String path) {
        Settings settings = new Settings(session, path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("data/saves.json")) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
