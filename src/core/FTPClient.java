package core;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FTPClient {
    private String IP;
    private int port = 21;
    private int dataPort;
    private Socket cmdSocket;
    private Socket dataSocket;
    private PrintWriter cmdWriter, dataWriter;
    private BufferedReader cmdReader, dataReader;
    private boolean connected = false, isLogin = false;
    private InputStream dataInputStream;
    private OutputStream dataOutputStream;
    private ActionListener downloadListener,uploadListener;
    public void setDownloadListener(ActionListener listener){
        this.downloadListener=listener;
    }
    public void setUploadListener(ActionListener listener){
        this.uploadListener=listener;
    }
    public static void main(String args[]) throws IOException, ParseException {
        FTPClient client = new FTPClient();
        client.connect("139.129.147.196");
        client.login("qxu1608230133","qxu1608230133");
        client.getFilesInfo("/htdoc");
    }
    public void connect(String IP) {
        try {
            this.IP = IP;
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String IP, int port) {
        try {
            this.IP = IP;
            this.port = port;
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) throws IOException {
        if (verify(username, password)) {
            isLogin = true;
        } else {
            System.out.println("用户名或密码错误!");
            throw new IOException("PASSERR");
        }
    }

    public void download(String remoteFile, String localFile) throws IOException {
        startPASV();
        if (fileExist(remoteFile)) {
            System.out.println("开始下载文件");
            saveFile(localFile);
            endPASV();
            String res=cmdReader.readLine();
            System.out.println(res);
            if (res.startsWith("226")) {
                System.out.println("下载完成");
                if(downloadListener!=null){
                    downloadListener.actionDone();
                }
            }
        }
        endPASV();
    }

    public void upload(String localFile, String remoteFile) throws IOException {
        startPASV();
        cmd("STOR " + remoteFile);
        File file = new File(localFile);
        InputStream fileIS = new FileInputStream(file);
        byte flush[] = new byte[1024];
        int len = 0;
        while (0 <= (len = fileIS.read(flush))) {
            dataOutputStream.write(flush, 0, len);
        }
        dataOutputStream.flush();
        fileIS.close();
        endPASV();
        String res = cmdReader.readLine();
        System.out.println(res);
        if (res.startsWith("226")) {
            if(uploadListener!=null){
                uploadListener.actionDone();
            }
        }
    }
    /*断点续传*/
    public void upload(String localFile, String remoteFile,int skip) throws IOException {
        cmd("REST " + skip);
        String res = cmdReader.readLine();
        startPASV();
        cmd("STOR " + remoteFile);
        File file = new File(localFile);
        InputStream fileIS = new FileInputStream(file);
        if(!(skip>file.length()))
            fileIS.skip(skip);
        else{
            fileIS.skip(file.length());
        }
        byte flush[] = new byte[1024];
        int len = 0;
        while (0 <= (len = fileIS.read(flush))) {
            dataOutputStream.write(flush, 0, len);
        }
        dataOutputStream.flush();
        fileIS.close();
        endPASV();
        res = cmdReader.readLine();
        System.out.println(res);
        if (res.startsWith("226")) {
            if(uploadListener!=null){
                uploadListener.actionDone();
            }
        }
    }

    private void endPASV() throws IOException {
        dataOutputStream.flush();
        dataInputStream.close();
        dataOutputStream.close();
        dataSocket.close();
    }

    private boolean fileExist(String remoteFile) {
        String response = cmd("RETR " + remoteFile);
        if (response.startsWith("150"))
            return true;
        else
            return false;
    }

    private boolean verify(String username, String password) {
        if (cmd("USER " + username).startsWith("331")) {
            if (cmd("PASS " + password).startsWith("230")) {
                return true;
            }
        }
        return false;
    }

    private void saveFile(String filename) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename,"rw");
        byte buffer[] = new byte[1024];
        int len = 0;
        while ((len = dataInputStream.read(buffer)) >= 0) {
            file.write(buffer, 0, len);
        }
        file.close();
    }
    /*断点续传*/
    private void saveFile(String filename,int skip) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename,"rw");
        file.seek(skip);
        byte buffer[] = new byte[1024];
        int len = 0;
        while ((len = dataInputStream.read(buffer)) >= 0) {
            file.write(buffer, 0, len);
        }
        file.close();
    }
    public ArrayList<RemoteFileInfo> getFilesInfo(String dir) throws IOException, ParseException {
        ArrayList<RemoteFileInfo> fileInfoList = new ArrayList<>();
        startPASV();
        if (cmd("LIST " + dir).startsWith("150")) {
            String fileInfoLine;
            while ((fileInfoLine = dataReader.readLine()) != null) {
                System.out.println(fileInfoLine);
                RemoteFileInfo fileInfo = RemoteFileInfo.parse(fileInfoLine, dir);
                fileInfoList.add(fileInfo);
            }
        }
        System.out.println(cmdReader.readLine());
        endPASV();
        return fileInfoList;
    }

    public boolean disconnect() {
        if (connected) {
            try {
                cmd("QUIT");
                cmdReader.close();
                cmdWriter.close();
                cmdSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void startPASV() throws IOException {
        String response = cmd("PASV");
        if (response.startsWith("227")) {
            dataPort = parsePort(response);
            dataSocket = new Socket(IP, dataPort);
            dataInputStream = dataSocket.getInputStream();
            dataOutputStream = dataSocket.getOutputStream();
            dataReader = new BufferedReader(new InputStreamReader(dataInputStream));
            dataWriter = new PrintWriter(dataOutputStream);
        }
    }

    private int parsePort(String str) {
        Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
        Matcher matcher = pattern.matcher(str);
        String ipAndPort = "";
        while (matcher.find()) {
            ipAndPort = matcher.group();
        }
        String splitIpAndPort[] = ipAndPort.split(",");
        int port = Integer.parseInt(splitIpAndPort[4]) * 256 + Integer.parseInt(splitIpAndPort[5]);
        return port;
    }

    private String cmd(String cmd) {
        String response;
        cmdWriter.println(cmd);
        cmdWriter.flush();
        try {
            response = cmdReader.readLine();
            System.out.println(response);
            return response == null ? "" : response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void init() throws IOException {
        cmdSocket = new Socket(this.IP, port);
        cmdWriter = new PrintWriter(cmdSocket.getOutputStream());
        cmdReader = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
        String welcome;
        while ((welcome = cmdReader.readLine()) != null) {
            System.out.println(welcome);
            if (welcome.startsWith("220 ")) {
                connected = true;
                break;
            }
        }
        ;
    }

    @Deprecated
    public void send(String cmd) {
        cmdWriter.println(cmd);
        cmdWriter.flush();
    }

    @Deprecated
    public String getResponse() throws IOException {
        return cmdReader.readLine();
    }

    public int getSize(String filename) {
        String response = cmd("SIZE "+filename);
        if (response.startsWith("213")) {
            return Integer.parseInt(response.split(" ")[1]);
        } else {
            return -1;
        }
    }

    public boolean makeDir(String dir) {
        if (cmd("MKD " + dir).startsWith("257")) {
            return true;
        }
        return false;
    }

    public boolean removeDir(String dir) {
        if (cmd("RMD " + dir).startsWith("250")) {
            return true;
        }
        return false;
    }

    public boolean deleteFile(String filename) {
        if (cmd("DELE " + filename).startsWith("250")) {
            return true;
        }
        return false;
    }
}
