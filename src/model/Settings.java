package model;

import lombok.Data;

@Data
public class Settings {
    private String session;
    private String path;

    // JSON書き込みに使用
    public Settings(String session, String path) {
        this.session = session;
        this.path = path;
    }

    // JSON読み込みに使用
    public Settings() {
    }
}