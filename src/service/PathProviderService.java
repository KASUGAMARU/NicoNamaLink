package service;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathProviderService {
   private static Path getJarDir() {
      try {
         URI jarPath = PathProviderService.class
               .getProtectionDomain()
               .getCodeSource()
               .getLocation()
               .toURI();
         return Paths.get(jarPath).getParent();// jarのディレクトリ
      } catch (Exception e) {
         e.printStackTrace();
         // 失敗時、IDE使用時はカレントにフォールバック
         return Path.of("").toAbsolutePath();
      }
   }

   public static Path getSaveFilePath() {
      return getJarDir().resolve("data").resolve("saves.json");
   }

}
