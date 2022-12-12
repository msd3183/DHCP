import java.io.*;
import java.net.*;
import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
public class DHCP
{
 DatagramSocket ds;
 static volatile Stack<InetAddress> IpList;
 static volatile HashMap<String, InetAddress> Userlist;
 Object ob = new Object();
 ServerSocket ss;
 Socket s;
 JFrame f;
 JTable t1;
 JTable t2;
 static DefaultTableModel dtm1 = new DefaultTableModel();
 static DefaultTableModel dtm2 = new DefaultTableModel();
 public static Object[][] getTable1() {
 Object o[][] = new Object[6][2];
 int i = 0;
for (Map.Entry<String,InetAddress> entry : Userlist.entrySet()) {
 
 o[i][0] = entry.getValue();
 o[i][1] = entry.getKey();
 i++;
 }
 return o;
 }
 public static Object[][] getTable2() {
 Object o[][] = new Object[6][1];
 int i = 0;
 Iterator it = IpList.iterator();
 while (it.hasNext()) {
 o[i][0] = it.next();
 i++;
 }
 return o;
 }
 public static void updateTable1() {
 Object[][] ob = DHCP.getTable1();
 for(int j=dtm1.getRowCount()-1;j>=0;j--)
 {
 dtm1.removeRow(j);
 }
 
 for(int temp=0;temp<ob.length;temp++)
 {
 dtm1.addRow(ob[temp]);
 }
 }
 public static void updateTable2() {
Object[][] ob = DHCP.getTable2();
 for(int j=dtm2.getRowCount()-1;j>=0;j--)
 {
 dtm2.removeRow(j);
 }
 
 for(int temp=0;temp<ob.length;temp++)
 {
 dtm2.addRow(ob[temp]);
 }
 }
 DHCP() {
 f = new JFrame("Server");
 f.setLayout(new GridLayout(1, 1));
 JPanel p1 = new JPanel();
 p1.setLayout(new GridLayout(1, 1));
 String col[] = { "IP address","MAC Address"};
 dtm1.setColumnIdentifiers(col);
 JPanel p11 = new JPanel(); p11.setLayout(new FlowLayout());
 JLabel l1 = new JLabel("Table1");
 p11.add(l1);
 p1.add(p11);
 t1 = new JTable(dtm1);
 JScrollPane jsp1 = new JScrollPane(t1);
 jsp1.setMaximumSize(new Dimension(500,150));
 t1.setRowHeight(20);
 p1.add(jsp1);
 f.add(p1);
 JPanel p2 = new JPanel();
 p2.setLayout(new GridLayout(1, 1));
 String col2[] = {"Remaining IP addresses"};
 dtm2.setColumnIdentifiers(col2);
JPanel p22 = new JPanel(); p22.setLayout(new FlowLayout());
 JLabel l2 = new JLabel("Table2");
 p22.add(l2);
 p2.add(p22);
 t2 = new JTable(dtm2);
 JScrollPane jsp2 = new JScrollPane(t2);
 jsp2.setMaximumSize(new Dimension(500,150));
 t2.setRowHeight(20);
 p2.add(jsp2);
 f.add(p2);
 //f.getRootPane().setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, Color.BLUE));
 f.setSize(500, 500);
 f.pack(); f.setVisible(true);
 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
//AA-AA-AA-AA-AA-AA

 try {
 ds = new DatagramSocket(2001);
 IpList = new Stack<InetAddress>();
 Userlist = new HashMap<String, InetAddress>();
 ss = new ServerSocket(2001);
 IpList.push(InetAddress.getByName("192.168.0.1"));
 IpList.push(InetAddress.getByName("192.168.0.2"));
 IpList.push(InetAddress.getByName("192.168.0.3"));
 IpList.push(InetAddress.getByName("192.168.0.4"));
 IpList.push(InetAddress.getByName("192.168.0.5"));
 IpList.push(InetAddress.getByName("192.168.0.6"));
 DHCP.updateTable1(); DHCP.updateTable2();
 
 } catch (Exception E) {
 System.out.println("exception");
 }
 }
 class Thread1 extends Thread {
DatagramPacket dp;
 Thread1 (DatagramPacket dp) {
 this.dp = dp;
 }
 public void run() {
 try {
 byte[] b = new byte[20];
 String msg = new String(dp.getData());
 InetAddress temp=null;
 if (msg.startsWith("connect")) {
 
 if (! Userlist.containsKey(msg.substring(8, 25))) {
 synchronized(ob) {
 temp = IpList.pop();
 Userlist.put(new String(msg.substring(8, 25)), temp);
 byte[] addr = temp.getHostAddress().getBytes();
 DatagramPacket MACPacket = new DatagramPacket(addr, addr.length, dp.getAddress(), dp.getPort());
 ds.send(MACPacket);
 DHCP.updateTable1(); DHCP.updateTable2();
 System.out.println(msg.substring(8, 25) + " assigned with " + temp.getHostAddress());
 System.out.println("Mappings are: " + Userlist);
 }
 }
 }
s = ss.accept();
 DataInputStream din = new DataInputStream(s.getInputStream());
 System.out.println("yes");
 String str=din.readUTF();
 
 System.out.println("Exited Client"+str);
 s.close();
 
 synchronized(ob) {
 System.out.println(Userlist.remove(str));
 IpList.push(temp);
 System.out.println("Mappings are: " + Userlist);
 DHCP.updateTable1(); DHCP.updateTable2();
 }
 } catch (Exception e) {
 System.out.println("exited");//TODO: handle exception
 }
 }
 }
 public void startDHCP() throws Exception {
 try {
 byte[] b = new byte[1024];
 while(true)
 { DatagramPacket dp = new DatagramPacket(b, b.length);
 ds.receive(dp);
 System.out.println("yes1");
if (IpList.isEmpty()) {
 System.out.println("NO IP available");
 String ret = "Not available";
 DatagramPacket MACPacket = new DatagramPacket(ret.getBytes(), ret.length(),
dp.getAddress(), dp.getPort());
 ds.send(MACPacket);
 continue;
 }
 Thread1 t1 = new Thread1(dp);
 t1.start();
 //ds.close();
 }
 } catch (Exception E) {
 System.out.println(E);
 }
 }
 public static void main(String[] args) throws Exception {
 DHCP s = new DHCP();
 s.startDHCP();
 }
}
