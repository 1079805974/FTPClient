package GUI;

import javax.swing.*;
import java.awt.*;

public class FolderPanel extends JPanel {
    private String folderName;

    public FolderPanel(String folderName) {
        this.setVisible(true);
        this.folderName = folderName;
        this.setLayout(new GridLayout(2, 1));
        ImageIcon icon = new ImageIcon("folder.jpg");
        JLabel imageLabel = new JLabel(icon);
        JLabel nameLabel = new JLabel(folderName);
        this.add(imageLabel);
        this.add(nameLabel);
    }

    public String getFoldername() {
        return folderName;
    }

    public void setFoldername(String folderName) {
        this.folderName = folderName;
    }
}

