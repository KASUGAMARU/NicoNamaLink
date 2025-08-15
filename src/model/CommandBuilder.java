package model;

import java.util.List;
import java.util.ArrayList;

/*コマンドを構成するクラス */
public class CommandBuilder {
    private List<String> baseCommand = new ArrayList<>();
    private List<String> ffmpegCommand = new ArrayList<>();
    private List<String> deleteCommand = new ArrayList<>();

    private boolean useffmpeg = false;
    private boolean deleteTemp = false;

    /*StreamLinkのコマンドを構成する */
    public void commandUpdata(String url,String session,String filename,boolean useffmpeg,boolean deleteTemp){
        this.useffmpeg = useffmpeg;
        this.deleteTemp = deleteTemp;

        baseCommand.clear();
        baseCommand.add("streamlink");
        baseCommand.add("--loglevel");
        baseCommand.add("debug");
        baseCommand.add(url);
        baseCommand.add("best");
        baseCommand.add("--niconico-user-session");
        baseCommand.add(session);
        baseCommand.add("--output");

        String outputFilename = useffmpeg ? "temp.ts" : "\"" + filename + ".mp4\"";
        baseCommand.add(outputFilename);

        if(useffmpeg){
            ffmpegCommand.clear();
            ffmpegCommand.add("&&");
            ffmpegCommand.add("ffmpeg");
            ffmpegCommand.add("-i");
            ffmpegCommand.add("temp.ts");
            ffmpegCommand.add("-c");
            ffmpegCommand.add("copy");
            ffmpegCommand.add("\"" + filename+ ".mp4\"");
        }

        if(deleteTemp){
            deleteCommand.clear();
            deleteCommand.add("&&");
            deleteCommand.add("del");
            deleteCommand.add("temp.ts");
        }
    }

    /*ffmpegのコマンドを構成する */
    public List<String> getFullCommand(){
        List<String> full = new ArrayList<>(baseCommand);
        if(useffmpeg) full.addAll(ffmpegCommand);
        if(deleteTemp) full.addAll(deleteCommand);
        return full;
    }

    /*中間ファイルを削除するコマンドを構成する */
    public String getCommandAsString(){
        return String.join(" ",getFullCommand());
    }
}
