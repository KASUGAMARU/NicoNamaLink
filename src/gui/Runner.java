package gui;

import javax.swing.*;
import javax.swing.event.*;

import model.CommandBuilder;
import model.Settings;
import net.miginfocom.swing.MigLayout;
import service.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Runner {
  private final CommandBuilder commandBuilder = new CommandBuilder();

  private JTextField urlField, usersessionField, filenameField, pathField;
  private JTextArea outputArea;
  private JFrame frame;
  private JProgressBar progressBar;
  private JLabel progress;
  private JCheckBox ffmpegCheck, deleteTemp;

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

  Settings loaded = LoadService.loadDataService();
  FocusListener listener = new SelectAllOnFocusListener();

  /* フォームとボタン */
  private JPanel createFormPanel() {
    urlField = new JTextField(40);// 1.URL入力
    urlField.addFocusListener(listener);
    usersessionField = new JTextField(loaded.getSession(), 40);// 2.ユーザーセッション入力
    usersessionField.addFocusListener(listener);
    filenameField = new JTextField(40);// 3.ファイル名入力
    filenameField.addFocusListener(listener);
    pathField = new JTextField(loaded.getPath(), 40); // 4.パスを表示する
    pathField.addFocusListener(listener);
    outputArea = new JTextArea(5, 40);// 5.コマンドを表示する
    outputArea.setLineWrap(true);// 折り返すようにする
    outputArea.setText("値を入力してコマンドを生成する");// 初期値を表示する
    outputArea.setEditable(false);//読み取り専用にする
    progress = new JLabel("進捗：");
    progressBar = new JProgressBar();// 6.プログレスバー追加
    progressBar.setPreferredSize(new Dimension(450, 20));
    progressBar.setIndeterminate(false);
    ffmpegCheck = new JCheckBox("ffmpegでmp4で出力する", true);
    deleteTemp = new JCheckBox("一時ファイルを削除する", true);

    /* 入力の変更を監視するリスナー */
    DocumentListener formListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        updateCommand();
      }

      public void removeUpdate(DocumentEvent e) {
        updateCommand();
      }

      public void changedUpdate(DocumentEvent e) {
        updateCommand();
      }
    };
    ItemListener checkBoxListener = new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateCommand();
      }
    };

    /* リスナーの登録 */
    urlField.getDocument().addDocumentListener(formListener);
    usersessionField.getDocument().addDocumentListener(formListener);
    filenameField.getDocument().addDocumentListener(formListener);
    ffmpegCheck.addItemListener(checkBoxListener);
    deleteTemp.addItemListener(checkBoxListener);

    JButton chooseButton = new JButton("パス選択");
    chooseButton.addActionListener(e -> chooseFolder());// パス選択アクションの呼び出し

    JButton saveButton = new JButton("ユーザーセッションとパスの保存");
    saveButton.addActionListener(
        e -> SaveService.saveDataServie(usersessionField.getText().trim(), pathField.getText().trim()));// 実行アクションの呼び出し

    JButton runButton = new JButton("実行");
    runButton.addActionListener(e -> executeCommand().execute());// 実行アクションの呼び出し

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
    formpanel.add(ffmpegCheck, "skip, wrap");
    formpanel.add(deleteTemp, "skip, wrap");
    formpanel.add(saveButton, "skip, wrap");
    formpanel.add(runButton, "skip, wrap");
    formpanel.add(progress, "skip, wrap");
    formpanel.add(progressBar, "skip, wrap");
    return formpanel;
  }

  private static class SelectAllOnFocusListener extends FocusAdapter {
    @Override
    public void focusGained(FocusEvent e) {
      Component c = e.getComponent();
      if (c instanceof JTextField textField) {
        textField.selectAll();
      }
    }
  }

  /* 変更を検知した際の処理 */
  private void updateCommand() {
    String url = urlField.getText().trim();
    String session = usersessionField.getText().trim();
    String filename = filenameField.getText().trim();
    boolean useffmpeg = ffmpegCheck.isSelected();
    boolean deleteTs = deleteTemp.isSelected();
    if (!useffmpeg) {
      deleteTemp.setEnabled(false);
      deleteTemp.setSelected(false);
    } else {
      deleteTemp.setEnabled(true);
    }
    commandBuilder.commandUpdata(url, session, filename, useffmpeg, deleteTs);
    outputArea.setText(String.join(" ", commandBuilder.getCommandAsString()));
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
  private SwingWorker<Void, String> executeCommand() {
    return new SwingWorker<>() {
      private Exception error = null;
      private int exitCode = 0;

      BufferedReader reader = null;

      @Override
      protected Void doInBackground() {
        try {
          progressBar.setIndeterminate(true);
          ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join(" ", outputArea.getText()));
          builder.directory(new File(pathField.getText()));
          builder.redirectErrorStream(true);
          Process process = builder.start();
          reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
          String line;
          while ((line = reader.readLine()) != null) {
            if (line.contains("Segment") && line.contains("complete")) {
              publish(line);
            }
          }
          exitCode = process.waitFor();
        } catch (Exception ex) {
          error = ex;
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        }
        return null;
      }

      @Override
      protected void process(List<String> chunks) {
        String latest = chunks.get(chunks.size() - 1);// 最新の1行を抽出
        progress.setText("進捗：" + latest.substring(20));
      }

      @Override
      protected void done() {
        progressBar.setIndeterminate(false);
        if (error != null) {
          JOptionPane.showMessageDialog(frame, "コマンド実行に失敗しました。\n" + error.getMessage(), "エラー",
              JOptionPane.ERROR_MESSAGE);
        } else if (exitCode != 0) {
          JOptionPane.showMessageDialog(frame, "コマンド実行中にエラーが発生しました。\n終了コード: " + exitCode, "エラー",
              JOptionPane.ERROR_MESSAGE);
        } else {
          JOptionPane.showMessageDialog(frame, "ダウンロードに成功しました", "完了", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    };
  }

}
