import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.*;

import java.awt.*;
import javax.swing.*;

//import javafx.scene.paint.Color;

public class user extends JFrame implements ActionListener {
    public DatagramSocket ds = null;
    public InetAddress IPaddrs;
    public String MACaddrs;
    public InetAddress ip;
    public int myPort;
    static JTextField t1, t2, t4;
    // JFrame
    static JFrame f;
    static JPanel p1, p2, p3;
    // JButton
    static JButton b1, b2;
    // label to display text
    static JLabel l1, l2, l3;

    user() {
        try {
            ds = new DatagramSocket(myPort);
            ip = InetAddress.getLocalHost();
            //MACaddrs = "A2-E7-0B-8A-6B-32";
            f = new JFrame("Client");
            f.setLayout(null);
            // create a label to display text
            l1 = new JLabel("Enter your port number");
            l1.setBounds(20, 20, 150, 30);
            l2 = new JLabel("Enter your MAC address");
            l2.setBounds(20, 70, 150, 30);

            // create a new button
            b1 = new JButton("Connect");
            b1.setBounds(60, 300, 100, 40);
            b1.setBackground(new java.awt.Color(204, 255, 229));
            b2 = new JButton("Exit");
            b2.setBounds(270, 300, 100, 40);
            b2.setBackground(new java.awt.Color(255, 204, 229));         // create a object of JTextField
            t1 = new JTextField(4);
            t1.setBounds(210, 20, 190, 30);
            t2 = new JTextField(18);
            t2.setBounds(210, 70, 190, 30);

            l3 = new JLabel("IP ADDRESS : ");
            l3.setBounds(20, 170, 100, 40);
            t4 = new JTextField(50);
            t4.setBounds(160, 170, 200, 40);
            f.add(l1);
            f.add(l2);
            f.add(t1);
            f.add(t2);
            f.add(t4);
            f.add(b1);
            f.add(b2);
            f.add(l3);
            // addActionListener to button
            b1.addActionListener(this);
            b2.addActionListener(this);
            // f.setUndecorated(true);
            f.setSize(440, 400);
            // f.getRootPane().setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8,Color.BLUE));
            f.show();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception E) {
            System.out.println(E);
            E.getMessage();
            E.getStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        try {
            if (s.equals("Connect")) {
                // set the text of the label to the text of the field
                myPort = Integer.parseInt(t1.getText());

                MACaddrs = t2.getText();
                String msg = "connect ";
                msg += MACaddrs;
                DatagramPacket dp2 = new DatagramPacket(msg.getBytes(), msg.length(), ip, 2001);
                ds.send(dp2);
                byte[] mes = new byte[1024];
                DatagramPacket dp1 = new DatagramPacket(mes, mes.length);
                ds.receive(dp1);
                String tf = new String(dp1.getData());
                System.out.println(tf);
                t4.setText(tf);
            }
            if (s.equals("Exit")) {
                System.out.println("retrieved");
                Socket sen = new Socket("127.0.0.1", 2001);
                DataOutputStream dout = new DataOutputStream(sen.getOutputStream());
                dout.writeUTF(MACaddrs);
                sen.close();
                System.exit(0);
            }
        } catch (IOException E) {
            System.out.println(E);
        }
    }

    public static void main(String args[]) throws IOException {
        user u1 = new user();
    }
}