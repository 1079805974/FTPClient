package GUI;

import core.ActionListener;
import core.FTPClient;
import core.RemoteFileInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Stack;

public class MainFrame {
    private static FTPClient client;
    private static ArrayList<RemoteFileInfo> fileInfos;
    private static String currentDir = "/";
    private static JPanel mainPanel;
    private static JFrame frame,loginFrame;
    private static JLabel dirLabel;
    private static JScrollPane body;
    private static Stack<String> history = new Stack<>();

    public static void initFTP() throws IOException, ParseException {
        client = new FTPClient();
    }

    public static void main(String args[]) throws IOException, ParseException {
        initFTP();
        frame = new JFrame();
        loginFrame=new LoginFrame();
    }

    public static void updateDir(String newDir) throws IOException, ParseException {
        history.push(currentDir);
        currentDir = newDir;
        dirLabel.setText("当前目录:" + newDir);
        frame.remove(body);
        frame.remove(mainPanel);
        drawPanel(newDir);
        body = new JScrollPane(mainPanel);
        mainPanel.setPreferredSize(new Dimension(900, 600));
        body.setPreferredSize(new Dimension(900, 600));
        frame.add(BorderLayout.CENTER, body);
    }
    public static void refreshDir() throws IOException, ParseException {
        frame.remove(body);
        frame.remove(mainPanel);
        drawPanel(currentDir);
        body = new JScrollPane(mainPanel);
        mainPanel.setPreferredSize(new Dimension(900, 600));
        body.setPreferredSize(new Dimension(900, 600));
        frame.add(BorderLayout.CENTER, body);
    }
    public static void back(String history) throws IOException, ParseException {
        currentDir = history;
        dirLabel.setText("当前目录:" + history);
        frame.remove(body);
        frame.remove(mainPanel);
        drawPanel(history);
        body = new JScrollPane(mainPanel);
        mainPanel.setPreferredSize(new Dimension(900, 600));
        body.setPreferredSize(new Dimension(900, 600));
        frame.add(BorderLayout.CENTER, body);
    }

    public static void drawPanel(String dir) throws IOException, ParseException {
        fileInfos = client.getFilesInfo(dir);
        JPanel filePanel;
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
        for (RemoteFileInfo info : fileInfos) {
            if (info.isFolder) {
                filePanel = new FolderPanel(info.getFilename());
                mainPanel.add(filePanel);
                final String infomation = info.toString();
                filePanel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int clickTime = e.getClickCount();
                        int clickButton = e.getButton();
                        if (clickTime == 1 && clickButton == MouseEvent.BUTTON3) {
                            System.out.println("右键单击");
                            JOptionPane.showMessageDialog(frame, infomation, "详细信息", JOptionPane.INFORMATION_MESSAGE);
                        } else if (clickTime == 2 && clickButton == MouseEvent.BUTTON1) {
                            System.out.println("左键双击");
                            try {
                                updateDir(currentDir + info.getFilename() + "/");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
            } else {
                filePanel = new FilePanel(info.getFilename());
                final String filename = info.getFilename();
                final String infomation = info.toString();
                filePanel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int clickTime = e.getClickCount();
                        int clickButton = e.getButton();
                        if (clickTime == 2 && clickButton == MouseEvent.BUTTON1) {
                            System.out.println("左键双击");
                            System.out.println(currentDir + filename);
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setSelectedFile(new File(filename));
                            int returnVal = fileChooser.showSaveDialog(frame);
                            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            File file = fileChooser.getSelectedFile();
                            System.out.println(file.getAbsolutePath());
                            if(returnVal==JFileChooser.APPROVE_OPTION){
                                try {
                                    client.download(currentDir + filename, file.getAbsolutePath());
                                    client.setDownloadListener(() -> JOptionPane.showMessageDialog(frame, "下载完成！", "下载完成", JOptionPane.INFORMATION_MESSAGE));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else if (clickTime == 1 && clickButton == MouseEvent.BUTTON3) {
                            System.out.println("右键单击");
                            JOptionPane.showMessageDialog(frame, infomation, "详细信息", JOptionPane.INFORMATION_MESSAGE);
                        }else if (clickTime == 2 && clickButton == MouseEvent.BUTTON2) {
                            int n = JOptionPane.showConfirmDialog(frame, "确定要删除么！", "详细信息", JOptionPane.YES_NO_OPTION);
                            if(n==0){
                                if(client.deleteFile(currentDir + filename))
                                    JOptionPane.showMessageDialog(frame, "删除成功！", "删除成功", JOptionPane.INFORMATION_MESSAGE);
                                else
                                    JOptionPane.showMessageDialog(frame, "删除失败！", "删除失败", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
                mainPanel.add(filePanel);
            }
        }
    }

    public static void login(String[] text) throws ParseException {
        client.connect(text[2]);
        try {
            client.login(text[0],text[1]);
            frame.setVisible(true);
            loginFrame.setVisible(false);
            initMainFrame();
        } catch (IOException e) {
            //e.printStackTrace();
            if(e.getMessage().equals("PASSERR")){
                loginFrame.setVisible(true);
                JOptionPane.showMessageDialog(frame, "密码错误", "详细信息", JOptionPane.ERROR_MESSAGE);
                frame.setVisible(false);
            }else{
                e.printStackTrace();
            }
        }
    }
    public static void initMainFrame() throws IOException, ParseException {
        drawPanel("/");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirLabel = new JLabel("当前目录：" + currentDir);
        JButton back = new JButton("返回");
        JButton upload = new JButton("上传");
        JButton refresh = new JButton("刷新");
        refresh.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    refreshDir();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        upload.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal=fileChooser.showOpenDialog(frame);
                File file = fileChooser.getSelectedFile();
                new Thread() {
                    @Override
                    public void run() {
                        if(returnVal==0){
                            try {
                                client.upload(file.getAbsolutePath(), currentDir + file.getName());
                                client.setUploadListener(() -> JOptionPane.showMessageDialog(frame, "上传完成！", "上传完成", JOptionPane.INFORMATION_MESSAGE));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                           }
                        }
                    }
                }.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        back.setPreferredSize(new Dimension(70, 20));
        upload.setPreferredSize(new Dimension(70, 20));
        back.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!history.empty()) {
                    try {
                        back(history.pop());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        updateDir("/");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        dirPanel.add(dirLabel);
        dirPanel.add(back);
        dirPanel.add(upload);
        dirPanel.add(refresh);
        dirPanel.setMaximumSize(new Dimension(frame.getWidth(), 20));
        body = new JScrollPane(mainPanel);
        frame.add(BorderLayout.NORTH, dirPanel);
        frame.add(BorderLayout.CENTER, body);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
