package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;


public class LoginFrame extends JFrame{
    InputPanel username,password,IP;
    JButton button;
    public LoginFrame(){
        username=new InputPanel("username:");
        password=new InputPanel("password:");
        IP=new InputPanel("IP:");
        IP.setText("139.129.147.196");
        username.setText("qxu1608230133");
        password.setText("qxu1608230133");
        button=new JButton("登陆");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4,1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button.setPreferredSize(new Dimension(80,30));
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    MainFrame.login(getText());
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
        add(username);
        add(password);
        add(IP);
        add(button);
        setVisible(true);
    }
    public String[] getText(){
        String s[]={username.getText(),password.getText(),IP.getText()};
        return s;
    }
}
class InputPanel extends JPanel{
    private TextField text;
    public InputPanel(String label){
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(new Label(label));
        text= new TextField(30);
        add(text);
    }
    public String getText(){
        return text.getText();
    }
    public void setText(String t){
        text.setText(t);
    }

}
