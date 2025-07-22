package gui;

import javax.swing.*;
import javax.swing.event.*;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Runner {
  static String[] command = {"streamlink","URL"," best","--niconico-user-session","ユーザーセッション"," --output","ファイル名.mp4"};//要素数8
  public static void main(String args[]){
    JFrame frame = new JFrame("ニコ生ダウンロードコマンド生成ツール");
   
    frame.setSize(700, 600);//サイズを同時に指定する(場所と同時はsetBounds)
    frame.setLocationRelativeTo(null);//常に中央に配置
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Xを押した時にアプリも終了する設定
    frame.setLayout(new BorderLayout());//フレームで使用されるレイアウトマネージャーを変更
    
    /*タイトルとアイコン */
    JLabel title = new JLabel("ニコ生ダウンロードコマンド生成ツール");//ラベル
    title.setFont(new Font("メイリオ", Font.BOLD, 21)); //フォント変更
    ImageIcon icon = new ImageIcon("./img/nicoicox50.png");//画像ラベル
    JLabel iconlabel = new JLabel(icon);

    JPanel titlepanel = new JPanel();
    titlepanel.setLayout(new FlowLayout());//panelにレイアウトを設定
    titlepanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));//高さを設定
    titlepanel.add(title);
    titlepanel.add(iconlabel);

    /*URL */
    JLabel urllabel = new JLabel("URL：");
    JTextField url = new JTextField(40);//1.URL入力
    /*ユーザーセッション */
    JLabel usersessionlabel = new JLabel("ユーザーセッション：");
    JTextField usersessionl = new JTextField(40);//2.ユーザーセッション入力
    /*ファイル名 */
    JLabel filenamelabel = new JLabel("ファイル名：");
    JTextField filename = new JTextField(40);//3.ファイル名入力

    /*フォルダ選択 */
    JLabel chooselabel = new JLabel("保存先：");
    JButton chooseButton = new JButton("パス選択");//保存先フォルダ選択ボタンの作成
    JTextField chooseTextField = new JTextField(40); //パスを表示する

    //ボタンにアクションを設定する
    chooseButton.addActionListener(e ->{
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("保存先を選択してください");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// フォルダ選択モード
      chooser.setAcceptAllFileFilterUsed(false); //ファイル選択を無効化

      int result = chooser.showOpenDialog(frame);
      if(result == JFileChooser.APPROVE_OPTION){
        File selectedFolder = chooser.getSelectedFile();
        chooseTextField.setText(selectedFolder.getAbsolutePath());
      }

    });

    /*TextField内の文字を結合してTextAreaに表示 */
     JLabel mixlabel = new JLabel("出力コマンド：");
    JTextArea mixTextArea = new JTextArea(5,40 );
    mixTextArea.setLineWrap(true);//折り返すようにする
    mixTextArea.setText(String.join(" ",command));//初期値を表示する
    //変更を監視するリスナーを作成
    DocumentListener formListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { updateText(); }
        public void removeUpdate(DocumentEvent e) { updateText(); }
        public void changedUpdate(DocumentEvent e) { updateText(); }
        //変更を検知した際の処理を記述
        private void updateText() {
          command[1] = url.getText();
          command[4] = usersessionl.getText();
          command[6] = "\"" + filename.getText() + ".mp4\"";
          mixTextArea.setText(String.join(" ",command));
        }
    };
    //フィールドにリスナーを登録
    url.getDocument().addDocumentListener(formListener); 
    usersessionl.getDocument().addDocumentListener(formListener); 
    filename.getDocument().addDocumentListener(formListener);


    //実行ボタン
    JButton runButton = new JButton("実行");
    runButton.addActionListener(e ->{
      try{
      ProcessBuilder builder = new ProcessBuilder("cmd","/c",String.join(" ",command));
      builder.directory(new File(chooseTextField.getText()));
      builder.start();
      }catch(IOException ex){
        ex.printStackTrace();
      }
    }
    );

    /*パネル、フォームのレイアウト作成 */
    JPanel formpanel = new JPanel(new MigLayout("","[right][grow]","[][][]"));

    
    formpanel.add(urllabel);
    formpanel.add(url, "wrap");

    formpanel.add(usersessionlabel);
    formpanel.add(usersessionl, "wrap");

    formpanel.add(filenamelabel);
    formpanel.add(filename, "wrap");

    formpanel.add(chooselabel);
    formpanel.add(chooseTextField);
    formpanel.add(chooseButton, "wrap");
  
    formpanel.add(mixlabel);
    formpanel.add(mixTextArea, "wrap");
    formpanel.add(runButton, "skip, wrap");
   

    frame.add(titlepanel, BorderLayout.NORTH);
    frame.add(formpanel, BorderLayout.CENTER);//frameにformpanelを入れる

    frame.setVisible(true);//ここで表示
  };

  
}
