package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class FilePanel extends JPanel {
    private String filename;
    private JLabel imageLabel;

    public FilePanel(String filename) {
        this.filename = filename;
        this.setVisible(true);
        this.setLayout(new GridLayout(2, 1));
        imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon("file.jpg"));
        JLabel nameLabel = new JLabel(filename);
        this.add(imageLabel);
        this.add(nameLabel);
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imageLabel.setIcon(new ImageIcon("file.jpg"));
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

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
