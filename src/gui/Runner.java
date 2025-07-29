package gui;

import javax.swing.*;
import javax.swing.event.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Runner {
  private static String[] command = {
      "streamlink", "URL", " best",
      "--niconico-user-session", "ユーザーセッション", " --output", "ファイル名.mp4"
  };

  private JTextField urlField, usersessionField, filenameField, pathField;
  private JTextArea outputArea;
  private JFrame frame;

  public static void main(String args[]) {
    SwingUtilities.invokeLater(() -> new Runner().initUI());
  }

  private void initUI() {
    frame = new JFrame("ニコ生ダウンロードコマンド生成ツール");
    frame.setSize(700, 600);// サイズを同時に指定する(場所と同時はsetBounds)
    frame.setLocationRelativeTo(null);// 常に中央に配置
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Xを押した時にアプリも終了する設定
    frame.setLayout(new BorderLayout());// フレームで使用されるレイアウトマネージャーを変更

    frame.add(createTitlePanel(), BorderLayout.NORTH);
    frame.add(createFormPanel(), BorderLayout.CENTER);

    frame.setVisible(true);// ここで表示
  }

  /* タイトルとアイコン */
  private JPanel createTitlePanel() {
    JLabel title = new JLabel("ニコ生ダウンロードコマンド生成ツール");// ラベル
    title.setFont(new Font("メイリオ", Font.BOLD, 21)); // フォント変更
    JLabel iconlabel = new JLabel(new ImageIcon("./img/nicoicox50.png"));// 画像ラベル

    JPanel titlepanel = new JPanel(new FlowLayout());// panelを作成し、レイアウトを設定
    titlepanel.add(title);
    titlepanel.add(iconlabel);
    return titlepanel;
  }

  /* フォームとボタン */
  private JPanel createFormPanel() {
    urlField = new JTextField(40);// 1.URL入力
    usersessionField = new JTextField(40);// 2.ユーザーセッション入力
    filenameField = new JTextField(40);// 3.ファイル名入力
    pathField = new JTextField(40); // 4.パスを表示する
    outputArea = new JTextArea(5, 40);// 5.コマンドを表示する
    outputArea.setLineWrap(true);// 折り返すようにする
    outputArea.setText(String.join(" ", command));// 初期値を表示する

    /* 入力の変更を監視するリスナー */
    DocumentListener formListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { updateCommand(); }
      public void removeUpdate(DocumentEvent e) { updateCommand(); }
      public void changedUpdate(DocumentEvent e) { updateCommand(); }

    };
    /* リスナーの登録 */
    urlField.getDocument().addDocumentListener(formListener);
    usersessionField.getDocument().addDocumentListener(formListener);
    filenameField.getDocument().addDocumentListener(formListener);

    JButton chooseButton = new JButton("パス選択");
    chooseButton.addActionListener(e -> chooseFolder());// パス選択アクションの呼び出し

    JButton runButton = new JButton("実行");
    runButton.addActionListener(e -> executeCommand());// 実行アクションの呼び出し

    JPanel formpanel = new JPanel(new MigLayout("", "[right][grow]", "[][][]"));
    formpanel.add(new JLabel("URL："));
    formpanel.add(urlField, "wrap");
    formpanel.add(new JLabel("ユーザーセッション："));
    formpanel.add(usersessionField, "wrap");
    formpanel.add(new JLabel("ファイル名："));
    formpanel.add(filenameField, "wrap");
    formpanel.add(new JLabel("保存先："));
    formpanel.add(pathField);
    formpanel.add(chooseButton, "wrap");
    formpanel.add(new JLabel("出力コマンド："));
    formpanel.add(outputArea, "wrap");
    formpanel.add(runButton, "skip, wrap");
    return formpanel;
  }

  /* 変更を検知した際の処理 */
  private void updateCommand() {
    command[1] = urlField.getText().trim();
    command[4] = usersessionField.getText().trim();
    command[6] = "\"" + filenameField.getText().trim() + ".mp4\"";
    outputArea.setText(String.join(" ", command));
  }

  /* フォルダ選択ボタンアクション */
  private void chooseFolder() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("保存先を選択してください");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// フォルダ選択モード
    chooser.setAcceptAllFileFilterUsed(false); // ファイル選択を無効化
    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
      File selectedFolder = chooser.getSelectedFile();
      pathField.setText(selectedFolder.getAbsolutePath());
    }
  }

  /* 実行ボタンアクション */
  private void executeCommand() {
    try {
      ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join(" ", command));
      builder.directory(new File(pathField.getText()));
      builder.start();
    } catch (IOException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "コマンド実行中にエラーが発生しました。", "エラー", JOptionPane.ERROR_MESSAGE);
    }
  }
}
