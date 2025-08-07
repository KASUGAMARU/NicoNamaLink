package gui;

import java.io.*;
import com.google.gson.*;

/*入力情報を保存する */
class Settings {
    String session;
    String path;

    Settings(String session, String path) {
        this.session = session;
        this.path = path;
    }
}

public class SaveService {
    static void saveDataServie(String session, String path) {
        Settings settings = new Settings(session, path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("data/saves.json")) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
