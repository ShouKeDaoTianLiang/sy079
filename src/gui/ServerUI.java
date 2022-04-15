package gui;

import java.io.OutputStream;
import java.awt.Dimension;
import java.io.PrintStream;
import java.awt.Toolkit;
import java.awt.EventQueue;
import handling.login.handler.CharLoginHandler;
import handling.world.World.Broadcast;
import server.Timer.EventTimer;
import client.inventory.MaplePet;
import client.inventory.ItemFlag;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import constants.GameConstants;
import server.CashItemFactory;
import server.life.MapleMonsterInformationProvider;
import server.MapleShopFactory;
import client.MapleClient;
import server.MapleItemInformationProvider;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import client.LoginCrypto;
import database.DatabaseConnection;
import handling.login.handler.AutoRegister;
import handling.world.World.Find;
import server.ServerProperties;
import server.Timer.WorldTimer;
import tools.MaplePacketCreator;
import constants.ServerConstants;
import java.util.Iterator;
import client.MapleCharacter;
import handling.channel.ChannelServer;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.Alignment;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.GroupLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import java.awt.Cursor;
import server.ShutdownServer;
import server.Start;
import java.awt.Component;
import javax.swing.JOptionPane;
import handling.login.LoginServer;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import java.util.HashMap;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.util.concurrent.ScheduledFuture;
import java.util.Map;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import javax.swing.JFrame;

public class ServerUI extends JFrame
{
    private Thread server;
    private boolean searchServer;
    private ArrayList<Tools> tools;
    private boolean writeChatLog;
    private static ServerUI instance;
    private ImageIcon icon;
    private Map<Windows, JFrame> windows;
    private boolean charInitFinished;
    protected static Thread t;
    private static ScheduledFuture<?> ts;
    private int minutesLeft;
    protected boolean hellban;
    private JTextPane chatLog;
    private JButton jButton1;
    private JButton jButton11;
    private JButton jButton13;
    private JButton jButton14;
    private JButton jButton15;
    private JButton jButton16;
    private JButton jButton17;
    private JButton jButton18;
    private JButton jButton2;
    private JButton jButton20;
    private JButton jButton21;
    private JButton jButton22;
    private JButton jButton23;
    private JButton jButton24;
    private JButton jButton25;
    private JButton jButton26;
    private JButton jButton27;
    private JButton jButton28;
    private JButton jButton29;
    private JButton jButton3;
    private JButton jButton30;
    private JButton jButton31;
    private JButton jButton32;
    private JButton jButton33;
    private JButton jButton34;
    private JButton jButton36;
    private JButton jButton4;
    private JButton jButton41;
    private JButton jButton43;
    private JButton jButton44;
    private JButton jButton45;
    private JButton jButton46;
    private JButton jButton47;
    private JButton jButton5;
    private JButton jButton7;
    private JButton jButton8;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel45;
    private JLabel jLabel5;
    private JLabel jLabel57;
    private JLabel jLabel58;
    private JLabel jLabel59;
    private JLabel jLabel6;
    private JLabel jLabel60;
    private JLabel jLabel61;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel14;
    private JPanel jPanel15;
    private JPanel jPanel3;
    private JPanel jPanel35;
    private JPanel jPanel36;
    private JPanel jPanel37;
    private JPanel jPanel38;
    private JPanel jPanel39;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPanel jPanel8;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane10;
    private JScrollPane jScrollPane11;
    private JScrollPane jScrollPane12;
    private JScrollPane jScrollPane13;
    private JScrollPane jScrollPane9;
    private JTabbedPane jTabbedPane1;
    private JTabbedPane jTabbedPane3;
    private JTextField jTextField1;
    private JTextField jTextField10;
    private JTextField jTextField11;
    private JTextField jTextField12;
    private JTextField jTextField13;
    private JTextField jTextField14;
    private JTextField jTextField15;
    private JTextField jTextField16;
    private JTextField jTextField17;
    private JTextField jTextField18;
    private JTextField jTextField19;
    private JTextField jTextField2;
    private JTextField jTextField20;
    private JTextField jTextField21;
    private JTextField jTextField22;
    private JTextField jTextField23;
    private JTextField jTextField24;
    private JTextField jTextField26;
    private JTextField jTextField27;
    private JTextField jTextField28;
    private JTextField jTextField29;
    private JTextField jTextField3;
    private JTextField jTextField30;
    private JTextField jTextField31;
    private JTextField jTextField32;
    private JTextField jTextField33;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JTextField jTextField6;
    private JTextField jTextField7;
    private JTextField jTextField8;
    private JTextField jTextField9;
    public static JTextPane output_err_jTextPane1;
    public static JTextPane output_jTextPane1;
    public static JTextPane output_notice_jTextPane1;
    public static JTextPane output_out_jTextPane1;
    public static JTextPane output_packet_jTextPane1;
    public static GUIPrintStream out;
    public static GUIPrintStream err;
    public static GUIPrintStream notice;
    public static GUIPrintStream packet;
    
    public static final ServerUI getInstance() {
        return ServerUI.instance;
    }
    
    public ServerUI() {
        this.server = null;
        this.searchServer = false;
        this.tools = new ArrayList<Tools>();
        this.writeChatLog = true;
        this.icon = new ImageIcon(this.getClass().getClassLoader().getResource("gui/Icon.png"));
        this.windows = new HashMap<Windows, JFrame>();
        this.charInitFinished = false;
        this.minutesLeft = 0;
        this.hellban = false;
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex2) {
            Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (IllegalAccessException ex3) {
            Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (UnsupportedLookAndFeelException ex4) {
            Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex4);
        }
        this.initComponents();
    }
    
    private void startServer() {
        if (LoginServer.isShutdown() && this.server == null) {
            (this.server = new Thread() {
                @Override
                public void run() {
                    try {
                        JOptionPane.showMessageDialog(null, "服务器启动需要时间,请点击确定继续。");
                        Start.main(null);
                        JOptionPane.showMessageDialog(null, "服务器启动完成。");
                    }
                    catch (InterruptedException ex) {
                        Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }
        else {
            JOptionPane.showMessageDialog(null, "服务器已在运行中。");
        }
    }
    
    private void reStartServer() {
        if (LoginServer.isShutdown() || this.server == null) {
            JOptionPane.showMessageDialog(null, "服务器未运行。");
        }
        else {
            JOptionPane.showMessageDialog(null, "正在重新启动服务器,请点击确定继续。");
            ShutdownServer.getInstance().shutdown();
            this.server = null;
            this.startServer();
        }
    }
    
    private void printChatLog(final String str) {
        if (this.writeChatLog) {
            this.chatLog.setText(this.chatLog.getText() + str + "\r\n");
        }
    }
    
    public void openWindow(final Windows w) {
        if (!this.windows.containsKey(w)) {
            switch (w) {
                case BuffStatusCalculator: {
                    break;
                }
                case SearchGenerator: {
                    break;
                }
                default: {
                    return;
                }
            }
            (this.windows.get(w)).setDefaultCloseOperation(1);
        }
        (this.windows.get(w)).setVisible(true);
    }
    
    private void initComponents() {
        this.jLabel45 = new JLabel();
        this.jTabbedPane1 = new JTabbedPane();
        this.jPanel1 = new JPanel();
        this.jPanel15 = new JPanel();
        this.jLabel1 = new JLabel();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.jButton16 = new JButton();
        this.jPanel14 = new JPanel();
        this.jTextField22 = new JTextField();
        this.jLabel10 = new JLabel();
        this.jTabbedPane3 = new JTabbedPane();
        this.jPanel35 = new JPanel();
        this.jScrollPane9 = new JScrollPane();
        ServerUI.output_jTextPane1 = new JTextPane();
        this.jPanel7 = new JPanel();
        this.jButton8 = new JButton();
        this.jLabel3 = new JLabel();
        this.jButton18 = new JButton();
        this.jButton20 = new JButton();
        this.jButton21 = new JButton();
        this.jButton22 = new JButton();
        this.jButton26 = new JButton();
        this.jButton44 = new JButton();
        this.jButton45 = new JButton();
        this.jButton46 = new JButton();
        this.jButton34 = new JButton();
        this.jButton47 = new JButton();
        this.jButton43 = new JButton();
        this.jLabel11 = new JLabel();
        this.jLabel12 = new JLabel();
        this.jLabel13 = new JLabel();
        this.jButton4 = new JButton();
        this.jButton5 = new JButton();
        this.jButton25 = new JButton();
        this.jButton7 = new JButton();
        this.jButton17 = new JButton();
        this.jButton3 = new JButton();
        this.jPanel36 = new JPanel();
        this.jScrollPane10 = new JScrollPane();
        ServerUI.output_packet_jTextPane1 = new JTextPane();
        this.jPanel37 = new JPanel();
        this.jScrollPane11 = new JScrollPane();
        ServerUI.output_notice_jTextPane1 = new JTextPane();
        this.jPanel38 = new JPanel();
        this.jScrollPane12 = new JScrollPane();
        ServerUI.output_err_jTextPane1 = new JTextPane();
        this.jPanel39 = new JPanel();
        this.jScrollPane13 = new JScrollPane();
        ServerUI.output_out_jTextPane1 = new JTextPane();
        this.jPanel6 = new JPanel();
        this.jPanel5 = new JPanel();
        this.jButton27 = new JButton();
        this.jTextField26 = new JTextField();
        this.jLabel57 = new JLabel();
        this.jLabel58 = new JLabel();
        this.jTextField27 = new JTextField();
        this.jTextField28 = new JTextField();
        this.jTextField29 = new JTextField();
        this.jLabel59 = new JLabel();
        this.jLabel60 = new JLabel();
        this.jButton28 = new JButton();
        this.jButton29 = new JButton();
        this.jButton41 = new JButton();
        this.jPanel8 = new JPanel();
        this.jTextField30 = new JTextField();
        this.jButton30 = new JButton();
        this.jTextField31 = new JTextField();
        this.jButton31 = new JButton();
        this.jButton32 = new JButton();
        this.jTextField32 = new JTextField();
        this.jTextField33 = new JTextField();
        this.jButton33 = new JButton();
        this.jButton36 = new JButton();
        this.jTextField23 = new JTextField();
        this.jTextField24 = new JTextField();
        this.jButton23 = new JButton();
        this.jButton15 = new JButton();
        this.jTextField21 = new JTextField();
        this.jTextField20 = new JTextField();
        this.jLabel61 = new JLabel();
        this.jLabel6 = new JLabel();
        this.jLabel7 = new JLabel();
        this.jTextField1 = new JTextField();
        this.jButton11 = new JButton();
        this.jButton24 = new JButton();
        this.jPanel3 = new JPanel();
        this.jTextField3 = new JTextField();
        this.jTextField4 = new JTextField();
        this.jButton14 = new JButton();
        this.jTextField5 = new JTextField();
        this.jTextField6 = new JTextField();
        this.jTextField7 = new JTextField();
        this.jTextField8 = new JTextField();
        this.jTextField9 = new JTextField();
        this.jTextField10 = new JTextField();
        this.jTextField11 = new JTextField();
        this.jTextField12 = new JTextField();
        this.jTextField13 = new JTextField();
        this.jTextField14 = new JTextField();
        this.jTextField15 = new JTextField();
        this.jTextField16 = new JTextField();
        this.jTextField17 = new JTextField();
        this.jTextField18 = new JTextField();
        this.jTextField19 = new JTextField();
        this.jLabel4 = new JLabel();
        this.jTextField2 = new JTextField();
        this.jButton13 = new JButton();
        this.jLabel5 = new JLabel();
        this.jLabel8 = new JLabel();
        this.jLabel9 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.chatLog = new JTextPane();
        this.setDefaultCloseOperation(3);
        this.setTitle("岁月工作室V0.79服务器端");
        this.setCursor(new Cursor(0));
        this.setIconImage(this.icon.getImage());
        this.setResizable(false);
        this.jLabel45.setHorizontalAlignment(0);
        this.jLabel45.setText("Generated Code write by NetBeans IDE.This ServerManager that made for ZZMS");
        this.jTabbedPane1.setBorder(new SoftBevelBorder(0));
        this.jTabbedPane1.setAutoscrolls(true);
        this.jTabbedPane1.setCursor(new Cursor(0));
        this.jTabbedPane1.setDoubleBuffered(true);
        this.jTabbedPane1.setFocusCycleRoot(true);
        this.jPanel15.setBorder(new SoftBevelBorder(0));
        this.jPanel15.setToolTipText("");
        this.jPanel15.setName("");
        this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 0x1));
        this.jLabel1.setText("服务器操作");
        this.jButton1.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01003824.png")));
        this.jButton1.setText("启动服务器");
        this.jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton1ActionPerformed(evt);
            }
        });
        this.jButton2.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01003112.png")));
        this.jButton2.setText("重新启动服务器");
        this.jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton2ActionPerformed(evt);
            }
        });
        this.jButton16.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/QQ图片20180805165204.png")));
        this.jButton16.setText("关闭服务器");
        this.jButton16.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton16ActionPerformed(evt);
            }
        });
        final GroupLayout jPanel14Layout = new GroupLayout(this.jPanel14);
        this.jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(jPanel14Layout.createParallelGroup(Alignment.LEADING).addGap(0, 13, 32767));
        jPanel14Layout.setVerticalGroup(jPanel14Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
        this.jTextField22.setText("关闭服务器倒数时间");
        this.jTextField22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField22ActionPerformed(evt);
            }
        });
        this.jLabel10.setFont(this.jLabel10.getFont().deriveFont(this.jLabel10.getFont().getStyle() | 0x1));
        this.jLabel10.setText("程序员偷懒美化什么的,,祭天了");
        ServerUI.output_jTextPane1.setEditable(false);
        this.jScrollPane9.setViewportView(ServerUI.output_jTextPane1);
        this.jButton8.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/ic45.png")));
        this.jButton8.setText("保存雇佣");
        this.jButton8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton8ActionPerformed(evt);
            }
        });
        this.jLabel3.setText("创建角色：");
        this.jButton18.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/1.png")));
        this.jButton18.setText("破功系统");
        this.jButton18.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton18ActionPerformed(evt);
            }
        });
        this.jButton20.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/冒险岛-集成电路01(ic01)_爱给网_aigei_com.png")));
        this.jButton20.setText("冒险家");
        this.jButton20.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton20ActionPerformed(evt);
            }
        });
        this.jButton21.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/冒险岛-集成电路02(ic02)_爱给网_aigei_com.png")));
        this.jButton21.setText("骑士团");
        this.jButton21.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton21ActionPerformed(evt);
            }
        });
        this.jButton22.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/冒险岛-集成电路03(ic03)_爱给网_aigei_com.png")));
        this.jButton22.setText("战神");
        this.jButton22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton22ActionPerformed(evt);
            }
        });
        this.jButton26.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/ic41.png")));
        this.jButton26.setText("自动注册");
        this.jButton26.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton26ActionPerformed(evt);
            }
        });
        this.jButton44.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/ic37.png")));
        this.jButton44.setText("万能限制");
        this.jButton44.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton44ActionPerformed(evt);
            }
        });
        this.jButton45.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/ic40.png")));
        this.jButton45.setText("限制登陆");
        this.jButton45.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton45ActionPerformed(evt);
            }
        });
        this.jButton46.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/item39.png")));
        this.jButton46.setText("允许顶号");
        this.jButton46.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton46ActionPerformed(evt);
            }
        });
        this.jButton34.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/2.png")));
        this.jButton34.setText("重载SQL连接");
        this.jButton34.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton34ActionPerformed(evt);
            }
        });
        this.jButton47.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01032019.png")));
        this.jButton47.setText("使用命令");
        this.jButton47.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton47ActionPerformed(evt);
            }
        });
        this.jButton43.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/冒险岛-项目02(item02)_爱给网_aigei_com.png")));
        this.jButton43.setText("重新数据");
        this.jButton43.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton43ActionPerformed(evt);
            }
        });
        this.jLabel11.setFont(this.jLabel11.getFont().deriveFont(this.jLabel11.getFont().getStyle() | 0x1));
        this.jLabel11.setText("岁月工作室v079.3服务端");
        this.jLabel12.setFont(this.jLabel12.getFont().deriveFont(this.jLabel12.getFont().getStyle() | 0x1));
        this.jLabel12.setText("      一条龙联系QQ:947039454");
        this.jLabel13.setFont(this.jLabel13.getFont().deriveFont(this.jLabel13.getFont().getStyle() | 0x1));
        this.jLabel13.setText("    网站制作,脚本定制,登录器制作");
        this.jButton4.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/2.png")));
        this.jButton4.setText("重载商店");
        this.jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton4ActionPerformed(evt);
            }
        });
        this.jButton5.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/2.png")));
        this.jButton5.setText("重载爆率");
        this.jButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton5ActionPerformed(evt);
            }
        });
        this.jButton25.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/冒险岛-集成电路03(ic03)_爱给网_aigei_com.png")));
        this.jButton25.setText("商城开关");
        this.jButton25.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton25ActionPerformed(evt);
            }
        });
        final GroupLayout jPanel7Layout = new GroupLayout(this.jPanel7);
        this.jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jButton20).addGap(46, 46, 46).addComponent(this.jLabel11, -2, 160, -2)).addComponent(this.jLabel3, -2, 70, -2).addGroup(jPanel7Layout.createSequentialGroup().addGroup(jPanel7Layout.createParallelGroup(Alignment.TRAILING, false).addComponent(this.jButton45, -1, 117, 32767).addComponent(this.jButton44, -1, -1, 32767).addComponent(this.jButton26, Alignment.LEADING, -1, -1, 32767).addComponent(this.jButton8, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton46, -1, -1, 32767).addComponent(this.jButton18, -1, -1, 32767).addComponent(this.jButton43, -1, -1, 32767).addComponent(this.jButton47, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton34, -1, -1, 32767).addComponent(this.jButton5, -1, -1, 32767).addComponent(this.jButton4, -1, -1, 32767).addComponent(this.jButton25, -1, -1, 32767))).addGroup(jPanel7Layout.createSequentialGroup().addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton21, -1, -1, 32767).addComponent(this.jButton22, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel12, -2, 226, -2).addComponent(this.jLabel13, -2, 220, -2)))).addContainerGap(27, 32767)));
        jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton44, Alignment.TRAILING, -1, -1, 32767).addComponent(this.jButton34, -2, 33, -2).addComponent(this.jButton46, Alignment.TRAILING, -2, 0, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addGroup(jPanel7Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton43, -2, 0, 32767).addComponent(this.jButton4)).addComponent(this.jButton45, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton26).addComponent(this.jButton18, -2, 36, -2).addComponent(this.jButton5, -2, 0, 32767)).addGap(7, 7, 7).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING, false).addGroup(jPanel7Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton47, -2, 36, -2).addComponent(this.jButton25)).addComponent(this.jButton8, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED, 13, 32767).addComponent(this.jLabel3, -2, 15, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.TRAILING).addComponent(this.jButton20).addComponent(this.jLabel11, -2, 30, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jButton21, -2, 31, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jButton22)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel13, -2, 24, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel12, -2, 27, -2))).addGap(9, 9, 9)));
        this.jButton7.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/03010697.png")));
        this.jButton7.setText("保存数据");
        this.jButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton7ActionPerformed(evt);
            }
        });
        this.jButton17.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/03010676.png")));
        this.jButton17.setText("调试模式");
        this.jButton17.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton17ActionPerformed(evt);
            }
        });
        this.jButton3.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/item39.png")));
        this.jButton3.setText("重载商城(每次修改完必须更新)");
        this.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton3ActionPerformed(evt);
            }
        });
        final GroupLayout jPanel35Layout = new GroupLayout(this.jPanel35);
        this.jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(jPanel35Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel35Layout.createSequentialGroup().addComponent(this.jScrollPane9, -2, 414, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel35Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel7, -1, -1, 32767).addGroup(jPanel35Layout.createSequentialGroup().addGroup(jPanel35Layout.createParallelGroup(Alignment.TRAILING, false).addComponent(this.jButton3, Alignment.LEADING, -1, -1, 32767).addGroup(jPanel35Layout.createSequentialGroup().addComponent(this.jButton7).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton17, -2, 121, -2))).addGap(0, 0, 32767))).addContainerGap()));
        jPanel35Layout.setVerticalGroup(jPanel35Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel35Layout.createSequentialGroup().addComponent(this.jPanel7, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED, 39, 32767).addComponent(this.jButton3).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel35Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton7).addComponent(this.jButton17, -2, 44, -2))).addComponent(this.jScrollPane9));
        this.jTabbedPane3.addTab("全部", this.jPanel35);
        ServerUI.output_packet_jTextPane1.setEditable(false);
        this.jScrollPane10.setViewportView(ServerUI.output_packet_jTextPane1);
        final GroupLayout jPanel36Layout = new GroupLayout(this.jPanel36);
        this.jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(jPanel36Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel36Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane10, -2, 408, -2).addContainerGap(-1, 32767)));
        jPanel36Layout.setVerticalGroup(jPanel36Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel36Layout.createSequentialGroup().addComponent(this.jScrollPane10, -2, 439, -2).addGap(0, 0, 32767)));
        this.jTabbedPane3.addTab("数据包", this.jPanel36);
        ServerUI.output_notice_jTextPane1.setEditable(false);
        this.jScrollPane11.setViewportView(ServerUI.output_notice_jTextPane1);
        final GroupLayout jPanel37Layout = new GroupLayout(this.jPanel37);
        this.jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(jPanel37Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane11));
        jPanel37Layout.setVerticalGroup(jPanel37Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane11, -1, 466, 32767));
        this.jTabbedPane3.addTab("提示", this.jPanel37);
        ServerUI.output_err_jTextPane1.setEditable(false);
        this.jScrollPane12.setViewportView(ServerUI.output_err_jTextPane1);
        final GroupLayout jPanel38Layout = new GroupLayout(this.jPanel38);
        this.jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(jPanel38Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane12));
        jPanel38Layout.setVerticalGroup(jPanel38Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane12, -1, 466, 32767));
        this.jTabbedPane3.addTab("报错", this.jPanel38);
        ServerUI.output_out_jTextPane1.setEditable(false);
        this.jScrollPane13.setViewportView(ServerUI.output_out_jTextPane1);
        final GroupLayout jPanel39Layout = new GroupLayout(this.jPanel39);
        this.jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(jPanel39Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane13));
        jPanel39Layout.setVerticalGroup(jPanel39Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane13, -1, 466, 32767));
        this.jTabbedPane3.addTab("其他", this.jPanel39);
        final GroupLayout jPanel15Layout = new GroupLayout(this.jPanel15);
        this.jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel15Layout.createSequentialGroup().addContainerGap().addGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel15Layout.createSequentialGroup().addGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel1).addGroup(jPanel15Layout.createSequentialGroup().addComponent(this.jButton1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton2, -2, 163, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton16, -2, 131, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField22, -2, 137, -2).addGap(18, 18, 18).addComponent(this.jLabel10, -2, 237, -2))).addContainerGap(17, 32767)).addGroup(Alignment.TRAILING, jPanel15Layout.createSequentialGroup().addComponent(this.jTabbedPane3).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel14, -1, -1, 32767)))));
        jPanel15Layout.setVerticalGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel15Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jButton1, -1, -1, 32767).addGroup(jPanel15Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton16, -1, -1, 32767).addComponent(this.jTextField22).addComponent(this.jLabel10).addComponent(this.jButton2, -1, -1, 32767))).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel15Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel14, -1, -1, 32767).addGroup(jPanel15Layout.createSequentialGroup().addComponent(this.jTabbedPane3, -2, 495, -2).addGap(0, 0, 32767))).addContainerGap()));
        final GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel15, -1, -1, 32767).addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel15, -1, -1, 32767).addContainerGap()));
        this.jTabbedPane1.addTab("服务器", this.jPanel1);
        this.jButton27.setText("设置经验倍率");
        this.jButton27.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton27ActionPerformed(evt);
            }
        });
        this.jTextField26.setText("0");
        this.jTextField26.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField26ActionPerformed(evt);
            }
        });
        this.jLabel57.setText("倍率");
        this.jLabel58.setText(" 分钟");
        this.jTextField27.setText("0");
        this.jTextField27.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField27ActionPerformed(evt);
            }
        });
        this.jTextField28.setText("0");
        this.jTextField28.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField28ActionPerformed(evt);
            }
        });
        this.jTextField29.setText("0");
        this.jTextField29.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField29ActionPerformed(evt);
            }
        });
        this.jLabel59.setText(" 小时");
        this.jLabel60.setText(" 秒");
        this.jButton28.setText("设置掉落倍率");
        this.jButton28.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton28ActionPerformed(evt);
            }
        });
        this.jButton29.setText("设置掉落抵用");
        this.jButton29.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton29ActionPerformed(evt);
            }
        });
        this.jButton41.setText("设置掉落点卷");
        this.jButton41.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton41ActionPerformed(evt);
            }
        });
        final GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
        this.jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addComponent(this.jButton27).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton28)).addGroup(jPanel5Layout.createSequentialGroup().addComponent(this.jButton41).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton29)).addGroup(jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout.createParallelGroup(Alignment.TRAILING, false).addGroup(Alignment.LEADING, jPanel5Layout.createSequentialGroup().addComponent(this.jLabel57, -2, 28, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel59, -1, -1, 32767)).addGroup(Alignment.LEADING, jPanel5Layout.createSequentialGroup().addComponent(this.jTextField26, -2, 33, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField27, -2, 33, -2))).addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField28, -2, 33, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField29, -2, 33, -2)).addGroup(Alignment.TRAILING, jPanel5Layout.createSequentialGroup().addGap(2, 2, 2).addComponent(this.jLabel58, -2, 33, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel60, -2, 31, -2))))).addGap(0, 7, 32767)));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton27).addComponent(this.jButton28)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton41).addComponent(this.jButton29)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField26, -2, -1, -2).addComponent(this.jTextField27, -2, -1, -2).addComponent(this.jTextField28, -2, -1, -2).addComponent(this.jTextField29, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel57, -2, 23, -2).addComponent(this.jLabel59, -2, 23, -2).addComponent(this.jLabel58, -2, 23, -2).addComponent(this.jLabel60)).addContainerGap(23, 32767)));
        this.jTextField30.setText("输入玩家名字");
        this.jTextField30.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField30ActionPerformed(evt);
            }
        });
        this.jButton30.setText("充值点卷");
        this.jButton30.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton30ActionPerformed(evt);
            }
        });
        this.jTextField31.setText("数量");
        this.jTextField31.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField31ActionPerformed(evt);
            }
        });
        this.jButton31.setText("充值元宝");
        this.jButton31.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton31ActionPerformed(evt);
            }
        });
        this.jButton32.setText("注册帐号");
        this.jButton32.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton32ActionPerformed(evt);
            }
        });
        this.jTextField32.setText("账号");
        this.jTextField32.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField32ActionPerformed(evt);
            }
        });
        this.jTextField33.setText("密码");
        this.jTextField33.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField33ActionPerformed(evt);
            }
        });
        this.jButton33.setText("修改密码");
        this.jButton33.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton33ActionPerformed(evt);
            }
        });
        this.jButton36.setText("充值抵用卷");
        this.jButton36.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton36ActionPerformed(evt);
            }
        });
        this.jTextField23.setText("道具代码");
        this.jTextField23.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField23ActionPerformed(evt);
            }
        });
        this.jTextField24.setText("输入数量");
        this.jTextField24.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField24ActionPerformed(evt);
            }
        });
        this.jButton23.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01003777.png")));
        this.jButton23.setText("发放全服道具");
        this.jButton23.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton23ActionPerformed(evt);
            }
        });
        this.jButton15.setText("发放全服点卷/抵用卷/金币/经验");
        this.jButton15.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton15ActionPerformed(evt);
            }
        });
        this.jTextField21.setText("1");
        this.jTextField21.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField21ActionPerformed(evt);
            }
        });
        this.jTextField20.setText("输入数量");
        this.jTextField20.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField20ActionPerformed(evt);
            }
        });
        this.jLabel61.setHorizontalAlignment(0);
        this.jLabel61.setText("1点卷/2抵用/3金币/4经验");
        this.jLabel6.setFont(this.jLabel6.getFont().deriveFont(this.jLabel6.getFont().getStyle() | 0x1));
        this.jLabel6.setText("游戏注册功能");
        this.jLabel7.setFont(this.jLabel7.getFont().deriveFont(this.jLabel7.getFont().getStyle() | 0x1));
        this.jLabel7.setText("游戏发放物品功能");
        final GroupLayout jPanel8Layout = new GroupLayout(this.jPanel8);
        this.jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addContainerGap().addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addComponent(this.jTextField30, -2, 97, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField31, -2, 92, -2)).addGroup(jPanel8Layout.createSequentialGroup().addComponent(this.jButton31, -2, 97, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton30, -2, 92, -2))).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addComponent(this.jButton36).addGap(145, 145, 145)).addGroup(jPanel8Layout.createSequentialGroup().addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addComponent(this.jTextField23, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jTextField24, -2, 67, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton23)).addGroup(jPanel8Layout.createSequentialGroup().addComponent(this.jTextField20, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jTextField21, -2, 67, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton15)).addComponent(this.jLabel61, -2, 146, -2)).addContainerGap(-1, 32767)).addGroup(jPanel8Layout.createSequentialGroup().addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addGroup(jPanel8Layout.createParallelGroup(Alignment.TRAILING, false).addComponent(this.jTextField32, Alignment.LEADING).addComponent(this.jButton32, Alignment.LEADING, -1, 97, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton33, -1, 92, 32767).addComponent(this.jTextField33))).addComponent(this.jLabel6, -2, 88, -2).addComponent(this.jLabel7, -2, 116, -2)).addGap(0, 0, 32767)))));
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addContainerGap().addGroup(jPanel8Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton36).addComponent(this.jButton31).addComponent(this.jButton30)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField30, -2, -1, -2).addComponent(this.jTextField31, -2, -1, -2)).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel6, -2, 31, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jButton33).addComponent(this.jButton32, Alignment.TRAILING)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField33, -2, -1, -2).addComponent(this.jTextField32, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addComponent(this.jLabel7, -2, 32, -2).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel8Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField23, -2, -1, -2).addComponent(this.jTextField24, -2, -1, -2).addComponent(this.jButton23)).addGap(18, 18, 18).addGroup(jPanel8Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField20, -2, -1, -2).addComponent(this.jTextField21, -2, -1, -2).addComponent(this.jButton15)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel61).addGap(18, 18, 18)));
        this.jTextField1.setText("输入玩家名字");
        this.jTextField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField1ActionPerformed(evt);
            }
        });
        this.jButton11.setText("解卡玩家");
        this.jButton11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton11ActionPerformed(evt);
            }
        });
        this.jButton24.setText("解封玩家");
        this.jButton24.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton24ActionPerformed(evt);
            }
        });
        this.jTextField3.setText("玩家名字");
        this.jTextField4.setText("物品ID");
        this.jButton14.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01432103.png")));
        this.jButton14.setText("给予物品");
        this.jButton14.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton14ActionPerformed(evt);
            }
        });
        this.jTextField5.setText("数量");
        this.jTextField6.setText("力量");
        this.jTextField7.setText("敏捷");
        this.jTextField8.setText("智力");
        this.jTextField9.setText("运气");
        this.jTextField10.setText("HP设置");
        this.jTextField11.setText("MP设置");
        this.jTextField12.setText("加卷次数");
        this.jTextField13.setText("制作人");
        this.jTextField14.setText("给予物品时间");
        this.jTextField15.setText("0");
        this.jTextField15.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField15ActionPerformed(evt);
            }
        });
        this.jTextField16.setText("攻击力");
        this.jTextField17.setText("魔法力");
        this.jTextField18.setText("物理防御");
        this.jTextField19.setText("魔法防御");
        this.jLabel4.setText("装备是否可以交易(默认0可以交易,1是不可以交易)");
        final GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jTextField15, -2, 70, -2).addGap(18, 18, 18).addComponent(this.jButton14)).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jTextField3, -2, 92, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField4, -2, 77, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField5, -2, 52, -2)).addGroup(jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING, false).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jTextField9, -2, 58, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField13)).addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addGroup(jPanel3Layout.createParallelGroup(Alignment.TRAILING).addComponent(this.jTextField8).addComponent(this.jTextField7)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jTextField11, -2, 79, -2).addComponent(this.jTextField12, -2, 79, -2))).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jTextField6, -2, 58, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField10, -2, 79, -2))).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jTextField16).addComponent(this.jTextField14, -1, 90, 32767).addComponent(this.jTextField17)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jTextField18, -1, 81, 32767).addComponent(this.jTextField19))).addComponent(this.jLabel4)).addContainerGap(-1, 32767)));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addGap(0, 0, 0).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField3, -2, -1, -2).addComponent(this.jTextField4, -2, -1, -2).addComponent(this.jTextField5, -2, -1, -2)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField6, -2, -1, -2).addComponent(this.jTextField10, -2, -1, -2).addComponent(this.jTextField14, -2, -1, -2).addComponent(this.jTextField18, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField7, -2, -1, -2).addComponent(this.jTextField11, -2, -1, -2).addComponent(this.jTextField19, -2, -1, -2).addComponent(this.jTextField16, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField8, -2, -1, -2).addComponent(this.jTextField12, -2, -1, -2).addComponent(this.jTextField17, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField9, -2, -1, -2).addComponent(this.jTextField13, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel4).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jButton14, -2, 32, -2).addComponent(this.jTextField15, -2, -1, -2)).addContainerGap(22, 32767)));
        this.jTextField2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jTextField2ActionPerformed(evt);
            }
        });
        this.jButton13.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/01102338.png")));
        this.jButton13.setText("公告发布");
        this.jButton13.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerUI.this.jButton13ActionPerformed(evt);
            }
        });
        this.jLabel5.setFont(this.jLabel5.getFont().deriveFont(this.jLabel5.getFont().getStyle() | 0x1));
        this.jLabel5.setText("游戏功能设置");
        this.jLabel8.setFont(this.jLabel8.getFont().deriveFont(this.jLabel8.getFont().getStyle() | 0x1));
        this.jLabel8.setText("游戏活动设置");
        this.jLabel9.setFont(this.jLabel9.getFont().deriveFont(this.jLabel9.getFont().getStyle() | 0x1));
        this.jLabel9.setText("物品制作功能");
        this.jScrollPane1.setViewportView(this.chatLog);
        final GroupLayout jPanel6Layout = new GroupLayout(this.jPanel6);
        this.jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel5, -2, -1, -2).addComponent(this.jLabel8, -2, 92, -2)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField1, -2, 124, -2).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jButton11).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton24)))).addGroup(jPanel6Layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.jLabel5, -2, 92, -2)).addComponent(this.jPanel8, -2, 405, -2)).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGap(44, 44, 44).addComponent(this.jLabel9, -2, 94, -2)).addGroup(jPanel6Layout.createSequentialGroup().addGap(21, 21, 21).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jPanel3, -1, -1, 32767).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jTextField2, -2, 229, -2).addGap(18, 18, 18).addComponent(this.jButton13, -1, -1, 32767))))).addGap(0, 66, 32767)).addGroup(Alignment.TRAILING, jPanel6Layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jScrollPane1))).addContainerGap()));
        jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel8, -2, 26, -2).addComponent(this.jLabel9, -2, 26, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton11).addComponent(this.jButton24)).addGap(8, 8, 8).addComponent(this.jTextField1, -2, -1, -2)).addComponent(this.jPanel3, -2, -1, -2)).addGap(18, 18, 18).addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton13, -2, 39, 32767).addComponent(this.jTextField2, -2, -1, -2)).addGap(18, 18, 18).addComponent(this.jScrollPane1, -2, 278, -2)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jPanel5, -2, -1, -2).addGap(0, 0, 32767)).addGroup(jPanel6Layout.createSequentialGroup().addGap(156, 156, 156).addComponent(this.jLabel5, -2, 26, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel8, -1, -1, 32767))).addContainerGap()));
        this.jTabbedPane1.addTab("游戏功能", this.jPanel6);
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jTabbedPane1).addContainerGap()).addComponent(this.jLabel45, -1, -1, 32767))));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jTabbedPane1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel45)));
        this.getAccessibleContext().setAccessibleName("ZZMS");
        this.pack();
    }
    
    private void jButton1ActionPerformed(final ActionEvent evt) {
        this.startServer();
    }
    
    private void jButton2ActionPerformed(final ActionEvent evt) {
        this.reStartServer();
    }
    
    private void jButton16ActionPerformed(final ActionEvent evt) {
        this.重启服务器();
    }
    
    private void jButton7ActionPerformed(final ActionEvent evt) {
        int p = 0;
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                ++p;
                chr.saveToDB(true, true);
            }
        }
        final String 输出 = "正在保存 - 玩家信息保存" + p + "个成功。";
        JOptionPane.showMessageDialog(null, 输出);
        System.out.println(输出);
    }
    
    private void jButton8ActionPerformed(final ActionEvent evt) {
        int p = 0;
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            ++p;
            cserv.closeAllMerchant();
        }
        final String 输出 = "正在保存 - 雇佣商人保存" + p + "个频道成功。";
        JOptionPane.showMessageDialog(null, "雇佣商人保存" + p + "个频道成功。");
        System.out.println(输出);
    }
    
    private void jButton18ActionPerformed(final ActionEvent evt) {
        final String 输出 = "正在启动 - 破功系统当前状态 :" + (ServerConstants.get破功() ? "开启" : "关闭");
        ServerConstants.破功 = !ServerConstants.破功;
        System.out.println(输出);
        JOptionPane.showMessageDialog(null, "破功系统启动成功。");
    }
    
    private void jButton20ActionPerformed(final ActionEvent evt) {
        this.冒险家();
        JOptionPane.showMessageDialog(null, "启动成功。");
    }
    
    private void jButton21ActionPerformed(final ActionEvent evt) {
        this.骑士团();
        JOptionPane.showMessageDialog(null, "启动成功。");
    }
    
    private void jButton22ActionPerformed(final ActionEvent evt) {
        this.战神();
        JOptionPane.showMessageDialog(null, "启动成功。");
    }
    
    private void jButton26ActionPerformed(final ActionEvent evt) {
        final String 输出 = "正在启动 - 服务器禁止登陆 :" + (ServerConstants.getAutoReg() ? "关闭" : "开启");
        System.out.println(输出);
        ServerConstants.ChangeAutoReg();
        JOptionPane.showMessageDialog(null, "服务器自动注册。");
    }
    
    private void 生怪() {
        ChannelServer.getAllInstances().stream().forEach(cserv -> cserv.getPlayerStorage().getAllCharacters().stream().forEach(chr -> chr.getMap().respawn(true)));
    }
    
    private void jButton44ActionPerformed(final ActionEvent evt) {
        ServerConstants.限制登陆 = !ServerConstants.限制登陆;
        final String 输出 = "正在启动 - 服务器禁止登陆 :" + (ServerConstants.get限制登陆() ? "开启" : "关闭");
        System.out.println(输出);
        JOptionPane.showMessageDialog(null, "服务器禁止万能登陆。");
    }
    
    private void jButton45ActionPerformed(final ActionEvent evt) {
        this.限制登陆();
        JOptionPane.showMessageDialog(null, "服务器禁止登陆。");
    }
    
    private void jButton46ActionPerformed(final ActionEvent evt) {
        this.是否允许顶号();
        JOptionPane.showMessageDialog(null, "是否允许顶号登陆。");
    }
    
    private void jButton43ActionPerformed(final ActionEvent evt) {
        JOptionPane.showMessageDialog(null, "从新载入数据会卡住比较久, 请点击确定继续。");
        JOptionPane.showMessageDialog(null, "数据载入完成。");
    }
    
    private void jButton34ActionPerformed(final ActionEvent evt) {
        System.gc();
        JOptionPane.showMessageDialog(null, "SQL连接清理成功。");
        System.out.println("正在清理 - 数据库SQL连接清理成功");
    }
    
    private void jButton17ActionPerformed(final ActionEvent evt) {
        ServerConstants.调试输出封包 = !ServerConstants.调试输出封包;
        final String 输出 = "正在启动 - 调试游戏模式 :" + (ServerConstants.get调试封包() ? "开启" : "关闭");
        System.out.println(输出);
        JOptionPane.showMessageDialog(null, "调试游戏模式启动。");
    }
    
    private void jButton47ActionPerformed(final ActionEvent evt) {
        ServerConstants.是否允许使用管理员命令 = !ServerConstants.是否允许使用管理员命令;
        final String 输出 = "正在启动 - 管理员命令 :" + (ServerConstants.is是否允许使用管理员命令() ? "开启" : "关闭");
        System.out.println(输出);
        JOptionPane.showMessageDialog(null, "开启成功。");
    }
    
    private void jTextField22ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton27ActionPerformed(final ActionEvent evt) {
        try {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    final int 经验 = Integer.parseInt(this.jTextField26.getText());
                    final int 秒 = Integer.parseInt(this.jTextField29.getText());
                    final int 分钟 = Integer.parseInt(this.jTextField28.getText());
                    final int 小时 = Integer.parseInt(this.jTextField27.getText());
                    final int time = 秒 + 分钟 * 60 + 小时 * 60 * 60;
                    cserv.setExpRate(经验);
                    cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "经验已经成功修改為 " + 经验 + "倍率 ,活动将在" + 小时 + "小時:" + 分钟 + "分:" + 秒 + "秒后結束！"));
                    WorldTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            cserv.setExpRate(Integer.parseInt(ServerProperties.getProperty("tms.Exp")));
                            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "双倍经验活动已结束。系统已成功切换正常游戏模式！"));
                        }
                    }, time * 1000);
                }
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "双倍活动开启成功。.");
    }
    
    private void jTextField26ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField27ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField28ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField29ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton28ActionPerformed(final ActionEvent evt) {
        try {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    final int 掉落 = Integer.parseInt(this.jTextField26.getText());
                    final int 秒 = Integer.parseInt(this.jTextField29.getText());
                    final int 分钟 = Integer.parseInt(this.jTextField28.getText());
                    final int 小时 = Integer.parseInt(this.jTextField27.getText());
                    final int time = 秒 + 分钟 * 60 + 小时 * 60 * 60;
                    cserv.setDropRate(掉落);
                    cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "物品掉落已经成功修改為 " + 掉落 + "倍率 ,活动将在" + 小时 + "小時:" + 分钟 + "分:" + 秒 + "秒后結束！"));
                    WorldTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            cserv.setDropRate(Integer.parseInt(ServerProperties.getProperty("tms.Drop")));
                            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "双倍爆率活动已结束。系统已成功切换正常游戏模式！"));
                        }
                    }, time * 1000);
                }
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "双倍活动开启成功。.");
        this.printChatLog("[服务器]双倍活动开启成功。.");
    }
    
    private void jButton29ActionPerformed(final ActionEvent evt) {
        try {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    final int 掉落 = Integer.parseInt(this.jTextField26.getText());
                    final int 秒 = Integer.parseInt(this.jTextField29.getText());
                    final int 分钟 = Integer.parseInt(this.jTextField28.getText());
                    final int 小时 = Integer.parseInt(this.jTextField27.getText());
                    final int time = 秒 + 分钟 * 60 + 小时 * 60 * 60;
                    ServerConstants.怪物掉落抵用卷 = true;
                    cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "管理员开启了全地图打怪掉落点卷活动,活動將在" + 小时 + "小時:" + 分钟 + "分:" + 秒 + "秒後結束！"));
                    System.out.println("点卷掉落: " + (ServerConstants.get怪物掉落抵用卷() ? "开启" : "关闭"));
                    WorldTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            ServerConstants.怪物掉落抵用卷 = false;
                            System.out.println("点卷掉落: " + (ServerConstants.get怪物掉落抵用卷() ? "开启" : "关闭"));
                            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "点卷系統活动已经結束。系統已成功自動切換為正常遊戲模式！"));
                        }
                    }, time * 1000);
                }
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "点卷活动开启成功。.");
        this.printChatLog("[服务器]打怪掉落点卷活动开启成功。.");
    }
    
    private void jButton41ActionPerformed(final ActionEvent evt) {
        try {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    final int 掉落 = Integer.parseInt(this.jTextField26.getText());
                    final int 秒 = Integer.parseInt(this.jTextField29.getText());
                    final int 分钟 = Integer.parseInt(this.jTextField28.getText());
                    final int 小时 = Integer.parseInt(this.jTextField27.getText());
                    final int time = 秒 + 分钟 * 60 + 小时 * 60 * 60;
                    ServerConstants.怪物掉落点卷 = true;
                    cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "管理员开启了全地图打怪掉落点卷活动,活動將在" + 小时 + "小時:" + 分钟 + "分:" + 秒 + "秒後結束！"));
                    System.out.println("点卷掉落: " + (ServerConstants.get怪物掉落点卷() ? "开启" : "关闭"));
                    WorldTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            ServerConstants.怪物掉落点卷 = false;
                            System.out.println("点卷掉落: " + (ServerConstants.get怪物掉落点卷() ? "开启" : "关闭"));
                            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "点卷系統活动已经結束。系統已成功自動切換為正常遊戲模式！"));
                        }
                    }, time * 1000);
                }
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "点卷活动开启成功。.");
        this.printChatLog("[服务器]打怪掉落点卷活动开启成功。.");
    }
    
    private void jTextField30ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton30ActionPerformed(final ActionEvent evt) {
        try {
            String 输出 = "";
            final String str = this.jTextField30.getText();
            final int 数量 = Integer.parseInt(this.jTextField31.getText());
            final int ch = Find.findChannel(str);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(str);
            if (victim == null) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            victim.modifyCSPoints(1, 数量, true);
            victim.dropMessage(6, "您成功充值点卷: 『" + 数量 + "』点卷.");
            输出 = "[成功充值公告]:玩家 [" + str + "] \n\r充值点卷:[" + 数量 + "]点卷.";
            this.printChatLog(输出);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "奖励发放成功。.");
    }
    
    private void jTextField31ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton31ActionPerformed(final ActionEvent evt) {
        try {
            String 输出 = "";
            final String str = this.jTextField30.getText();
            final int 数量 = Integer.parseInt(this.jTextField31.getText());
            final int ch = Find.findChannel(str);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(str);
            if (victim == null) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            victim.setzb(数量);
            victim.dropMessage(6, "您成功充值积分: 『" + 数量 + "』积分.");
            输出 = "[成功充值公告]:玩家 [" + str + "] \n\r充值元宝:[" + 数量 + "]积分.";
            this.printChatLog(输出);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "奖励发放成功。.");
    }
    
    private void 注册帐号() {
        String acc = null;
        String password = null;
        try {
            acc = this.jTextField32.getText();
            password = this.jTextField33.getText();
        }
        catch (Exception ex3) {}
        if (acc == null || password == null) {
            JOptionPane.showMessageDialog(null, "账号或密码异常。.");
            return;
        }
        final boolean ACCexist = AutoRegister.getAccountExists(acc);
        if (ACCexist) {
            JOptionPane.showMessageDialog(null, "帐号已被使用。.");
            return;
        }
        if (acc.length() >= 12) {
            JOptionPane.showMessageDialog(null, "密码长度过长。.");
            return;
        }
        Connection con;
        try {
             con = DatabaseConnection.getConnection();
        }
        catch (Exception ex) {
            System.out.println(ex);
            return;
        }
       
        try (final PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password) VALUES (?, ?)")) {
            ps.setString(1, acc);
            ps.setString(2, LoginCrypto.hexSha1(password));
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex2) {
            System.out.println(ex2);
            return;
        }
        this.printChatLog("[注册完成]账号: " + acc + " 密码: " + password);
    }
    
    private void jButton32ActionPerformed(final ActionEvent evt) {
        this.注册帐号();
    }
    
    private void jTextField32ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField33ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton33ActionPerformed(final ActionEvent evt) {
        this.ChangePassWord();
    }
    
    private void ChangePassWord() {
        final String account = this.jTextField32.getText();
        final String password = this.jTextField33.getText();
        if (password.length() > 12) {
            JOptionPane.showMessageDialog(null, "密码过长");
            return;
        }
        if (!AutoRegister.getAccountExists(account)) {
            JOptionPane.showMessageDialog(null, "账号不存在");
            return;
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("Update accounts set password = ? Where name = ?");
            ps.setString(1, LoginCrypto.hexSha1(password));
            ps.setString(2, account);
            ps.execute();
            ps.close();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + ex);
        }
        this.printChatLog("更改账号: " + account + "的密码为 " + password);
    }
    
    private void jButton36ActionPerformed(final ActionEvent evt) {
        try {
            String 输出 = "";
            final String str = this.jTextField30.getText();
            final int 数量 = Integer.parseInt(this.jTextField31.getText());
            final int ch = Find.findChannel(str);
            if (ch <= 0) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(str);
            if (victim == null) {
                JOptionPane.showMessageDialog(null, "玩家不在线。.");
                return;
            }
            victim.modifyCSPoints(2, 数量, true);
            victim.dropMessage(6, "您成功充值抵用卷: 『" + 数量 + "』抵用卷.");
            输出 = "[成功充值公告]:玩家 [" + str + "] \n\r充值抵用卷:[" + 数量 + "]抵用卷.";
            this.printChatLog(输出);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "奖励发放成功。.");
    }
    
    private void jTextField23ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField24ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton23ActionPerformed(final ActionEvent evt) {
        try {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int 人数 = 0;
            final int 道具 = Integer.parseInt(this.jTextField23.getText());
            final String 数量 = this.jTextField24.getText();
            short quantity = 1;
            quantity = Short.parseShort(数量);
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.gainItem(道具, quantity, "发放全服奖励获得");
                    final String name = MapleItemInformationProvider.getInstance().getName(道具);
                    mch.startMapEffect("获得了来自游戏管理员『" + name + "x" + quantity + "』的道具奖励.", 5121009);
                    ++人数;
                }
            }
            final String 输出 = "发放『" + ii.getName(道具) + "』的道具奖励!一共发放给了" + 人数 + "人！";
            this.printChatLog(输出);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "输入错误.");
            return;
        }
        JOptionPane.showMessageDialog(null, "奖励发放成功。.");
    }
    
    private void 给全服点卷() {
        try {
            int 数量;
            if ("输入数量".equals(this.jTextField20.getText())) {
                数量 = 0;
            }
            else {
                数量 = Integer.parseInt(this.jTextField20.getText());
            }
            int 类型;
            if ("1点卷/2抵用/3金币/4经验".equals(this.jTextField21.getText())) {
                类型 = 0;
            }
            else {
                类型 = Integer.parseInt(this.jTextField21.getText());
            }
            if (数量 <= 0 || 类型 <= 0) {
                return;
            }
            String 输出 = "";
            int ret = 0;
            switch (类型) {
                case 1:
                case 2: {
                    for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                            mch.modifyCSPoints(类型, 数量);
                            String cash = null;
                            if (类型 == 1) {
                                cash = "点卷";
                            }
                            else if (类型 == 2) {
                                cash = "抵用卷";
                            }
                            mch.startMapEffect("管理员发放" + 数量 + cash + "给在线的所以玩家！快感谢管理员吧！", 5121009);
                            ++ret;
                        }
                    }
                    break;
                }
                case 3: {
                    for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                            mch.gainMeso(数量, true);
                            mch.startMapEffect("管理员发放" + 数量 + "冒险币给在线的所以玩家！快感谢管理员吧！", 5121009);
                            ++ret;
                        }
                    }
                    break;
                }
                case 4: {
                    for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                            mch.gainExp(数量, true, false, true);
                            mch.startMapEffect("管理员发放" + 数量 + "经验给在线的所以玩家！快感谢管理员吧！", 5121009);
                            ++ret;
                        }
                    }
                    break;
                }
            }
            String 类型A = "";
            switch (类型) {
                case 1: {
                    类型A = "点卷";
                    break;
                }
                case 2: {
                    类型A = "抵用卷";
                    break;
                }
                case 3: {
                    类型A = "金币";
                    break;
                }
                case 4: {
                    类型A = "经验";
                    break;
                }
            }
            输出 = "一个发放[" + 数量 * ret + "]." + 类型A + "!一共发放给了" + ret + "人！";
            this.jTextField20.setText("输入数量");
            this.jTextField21.setText("1点卷/2抵用/3金币/4经验");
            this.printChatLog(输出);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }
    
    private void jButton15ActionPerformed(final ActionEvent evt) {
        this.给全服点卷();
    }
    
    private void jTextField21ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField20ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField1ActionPerformed(final ActionEvent evt) {
    }
    
    private void sendNotice(final int type) {
        try {
            final String str = this.jTextField1.getText();
            final byte[] p = null;
            String 输出 = "";
            if (type == 0) {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr.getName().equals(str) && chr.getMapId() != 0) {
                            chr.getClient().getSession().close();
                            chr.getClient().disconnect(true, false);
                            输出 = "[解卡系统] 成功断开" + str + "玩家！";
                        }
                        else {
                            输出 = "[解卡系统] 玩家名字输入错误或者该玩家没有在线！";
                        }
                    }
                }
            }
            this.jTextField1.setText("");
            this.printChatLog(输出);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }
    
    private void jButton11ActionPerformed(final ActionEvent evt) {
        this.sendNotice(0);
    }
    
    private String getCommand() {
        return "UnBan";
    }
    
    private int 解封帐号() {
        String 输出 = "";
        byte ret;
        if (this.hellban) {
            ret = MapleClient.unHellban(this.jTextField1.getText());
        }
        else {
            ret = MapleClient.unban(this.jTextField1.getText());
        }
        if (ret == -2) {
            return 0;
        }
        if (ret == -1) {
            return 0;
        }
        输出 = "[" + this.getCommand() + "] Successfully unbanned!";
        final byte ret_ = MapleClient.unbanIPMacs(this.jTextField1.getText());
        if (ret_ == -2) {
            输出 = "[unbanip] SQL 错误.";
        }
        else if (ret_ == -1) {
            输出 = "[unbanip] 角色不存在.";
        }
        else if (ret_ == 0) {
            输出 = "[unbanip] No IP or Mac with that character exists!";
        }
        else if (ret_ == 1) {
            输出 = "[unbanip] IP或Mac已解锁其中一個.";
        }
        else if (ret_ == 2) {
            输出 = "[unbanip] IP以及Mac已成功解锁.";
        }
        this.printChatLog(输出);
        return 1;
    }
    
    private void jButton24ActionPerformed(final ActionEvent evt) {
        this.解封帐号();
    }
    
    private void jButton14ActionPerformed(final ActionEvent evt) {
        this.刷物品();
    }
    
    private void jTextField15ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField2ActionPerformed(final ActionEvent evt) {
    }
    
    private void jButton13ActionPerformed(final ActionEvent evt) {
        this.sendNoticeGG();
    }
    
    private void jButton4ActionPerformed(final ActionEvent evt) {
        MapleShopFactory.getInstance().clear();
        JOptionPane.showMessageDialog(null, "商店重载成功。");
        System.out.println("正在重载 - 商店重载成功");
    }
    
    private void jButton5ActionPerformed(final ActionEvent evt) {
        MapleMonsterInformationProvider.getInstance().clearDrops();
        JOptionPane.showMessageDialog(null, "爆率重载成功。");
        System.out.println("正在重载 - 爆率重载成功成功");
    }
    
    private void jButton3ActionPerformed(final ActionEvent evt) {
        CashItemFactory.getInstance().clearCashShop();
        final String 输出 = "[重载系统] 商城重载成功。";
        JOptionPane.showMessageDialog(null, "商城重载成功。");
        this.printChatLog(输出);
    }
    
    private void jButton25ActionPerformed(final ActionEvent evt) {
        ServerConstants.给怪物BUFF = !ServerConstants.给怪物BUFF;
        final String 输出 = "正在启动 - 开启商城 :" + (ServerConstants.get商城开关() ? "开启" : "关闭");
        System.out.println(输出);
    }
    
    private void 刷物品() {
        try {
            String 名字;
            if ("玩家名字".equals(this.jTextField3.getText())) {
                名字 = "";
            }
            else {
                名字 = this.jTextField3.getText();
            }
            int 物品ID;
            if ("物品ID".equals(this.jTextField4.getText())) {
                物品ID = 0;
            }
            else {
                物品ID = Integer.parseInt(this.jTextField4.getText());
            }
            int 数量;
            if ("数量".equals(this.jTextField5.getText())) {
                数量 = 0;
            }
            else {
                数量 = Integer.parseInt(this.jTextField5.getText());
            }
            int 力量;
            if ("力量".equals(this.jTextField6.getText())) {
                力量 = 0;
            }
            else {
                力量 = Integer.parseInt(this.jTextField6.getText());
            }
            int 敏捷;
            if ("敏捷".equals(this.jTextField7.getText())) {
                敏捷 = 0;
            }
            else {
                敏捷 = Integer.parseInt(this.jTextField7.getText());
            }
            int 智力;
            if ("智力".equals(this.jTextField8.getText())) {
                智力 = 0;
            }
            else {
                智力 = Integer.parseInt(this.jTextField8.getText());
            }
            int 运气;
            if ("运气".equals(this.jTextField9.getText())) {
                运气 = 0;
            }
            else {
                运气 = Integer.parseInt(this.jTextField9.getText());
            }
            int HP;
            if ("HP设置".equals(this.jTextField10.getText())) {
                HP = 0;
            }
            else {
                HP = Integer.parseInt(this.jTextField10.getText());
            }
            int MP;
            if ("MP设置".equals(this.jTextField11.getText())) {
                MP = 0;
            }
            else {
                MP = Integer.parseInt(this.jTextField11.getText());
            }
            int 可加卷次数;
            if ("加卷次数".equals(this.jTextField12.getText())) {
                可加卷次数 = 0;
            }
            else {
                可加卷次数 = Integer.parseInt(this.jTextField12.getText());
            }
            String 制作人名字;
            if ("制作人".equals(this.jTextField13.getText())) {
                制作人名字 = "";
            }
            else {
                制作人名字 = this.jTextField13.getText();
            }
            int 给予时间;
            if ("给予物品时间".equals(this.jTextField14.getText())) {
                给予时间 = 0;
            }
            else {
                给予时间 = Integer.parseInt(this.jTextField14.getText());
            }
            int 是否可以交易;
            if ("是否可以交易".equals(this.jTextField15.getText())) {
                是否可以交易 = 0;
            }
            else {
                是否可以交易 = Integer.parseInt(this.jTextField15.getText());
            }
            int 攻击力;
            if ("攻击力".equals(this.jTextField16.getText())) {
                攻击力 = 0;
            }
            else {
                攻击力 = Integer.parseInt(this.jTextField16.getText());
            }
            int 魔法力;
            if ("魔法力".equals(this.jTextField17.getText())) {
                魔法力 = 0;
            }
            else {
                魔法力 = Integer.parseInt(this.jTextField17.getText());
            }
            int 物理防御;
            if ("物理防御".equals(this.jTextField18.getText())) {
                物理防御 = 0;
            }
            else {
                物理防御 = Integer.parseInt(this.jTextField18.getText());
            }
            int 魔法防御;
            if ("魔法防御".equals(this.jTextField19.getText())) {
                魔法防御 = 0;
            }
            else {
                魔法防御 = Integer.parseInt(this.jTextField19.getText());
            }
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            String 输出A = "";
            final String 输出 = "玩家名字：" + 名字 + " 物品ID：" + 物品ID + " 数量：" + 数量 + " 力量:" + 力量 + " 敏捷:" + 敏捷 + " 智力:" + 智力 + " 运气:" + 运气 + " HP:" + HP + " MP:" + MP + " 可加卷次数:" + 可加卷次数 + " 制作人名字:" + 制作人名字 + " 给予时间:" + 给予时间 + " 是否可以交易:" + 是否可以交易 + " 攻击力:" + 攻击力 + " 魔法力:" + 魔法力 + " 物理防御:" + 物理防御 + " 魔法防御:" + 魔法防御 + "\r\n";
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (mch.getName().equals(名字)) {
                        if (数量 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 数量, "")) {
                                return;
                            }
                            if ((type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(物品ID) && !GameConstants.isBullet(物品ID)) || (type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100)) {
                                final Equip item = (Equip)ii.getEquipById(物品ID);
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                if (力量 > 0 && 力量 <= 32767) {
                                    item.setStr((short)力量);
                                }
                                if (敏捷 > 0 && 敏捷 <= 32767) {
                                    item.setDex((short)敏捷);
                                }
                                if (智力 > 0 && 智力 <= 32767) {
                                    item.setInt((short)智力);
                                }
                                if (运气 > 0 && 运气 <= 32767) {
                                    item.setLuk((short)运气);
                                }
                                if (攻击力 > 0 && 攻击力 <= 32767) {
                                    item.setWatk((short)攻击力);
                                }
                                if (魔法力 > 0 && 魔法力 <= 32767) {
                                    item.setMatk((short)魔法力);
                                }
                                if (物理防御 > 0 && 物理防御 <= 32767) {
                                    item.setWdef((short)物理防御);
                                }
                                if (魔法防御 > 0 && 魔法防御 <= 32767) {
                                    item.setMdef((short)魔法防御);
                                }
                                if (HP > 0 && HP <= 30000) {
                                    item.setHp((short)HP);
                                }
                                if (MP > 0 && MP <= 30000) {
                                    item.setMp((short)MP);
                                }
                                if (是否可以交易 > 0) {
                                    short flag = item.getFlag();
                                    flag |= (short)ItemFlag.LOCK.getValue();
                                    item.setFlag(flag);
                                }
                                if (给予时间 > 0) {
                                    item.setExpiration(System.currentTimeMillis() + 给予时间 * 24 * 60 * 60 * 1000);
                                }
                                if (可加卷次数 > 0) {
                                    item.setUpgradeSlots((byte)可加卷次数);
                                }
                                if (制作人名字 != null) {
                                    item.setOwner(制作人名字);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) {
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            }
                            else {
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short)数量, "", null, 给予时间, (byte)0);
                            }
                        }
                        else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -数量, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short)数量, true));
                        输出A = "[刷物品]:" + 输出;
                    }
                }
            }
            this.jTextField3.setText("玩家名字");
            this.jTextField4.setText("物品ID");
            this.jTextField5.setText("数量");
            this.jTextField6.setText("力量");
            this.jTextField7.setText("敏捷");
            this.jTextField8.setText("智力");
            this.jTextField9.setText("运气");
            this.jTextField10.setText("HP设置");
            this.jTextField11.setText("MP设置");
            this.jTextField12.setText("加卷次数");
            this.jTextField13.setText("制作人");
            this.jTextField14.setText("给予物品时间");
            this.jTextField15.setText("0");
            this.jTextField16.setText("攻击力");
            this.jTextField17.setText("魔法力");
            this.jTextField18.setText("物理防御");
            this.jTextField19.setText("魔法防御");
            this.printChatLog(输出A);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }
    
    private void sendNoticeGG() {
        try {
            final String str = this.jTextField2.getText();
            String 输出 = "";
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect(str, 5121009);
                    输出 = "[公告]:" + str;
                }
            }
            this.jTextField2.setText("");
            this.printChatLog(输出);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }
    
    private void 重启服务器() {
        try {
            final String 输出 = "关闭服务器倒数时间";
            ServerConstants.限制登陆1 = !ServerConstants.限制登陆1;
            this.minutesLeft = Integer.parseInt(this.jTextField22.getText());
            if (ServerUI.ts == null && (ServerUI.t == null || !ServerUI.t.isAlive())) {
                ServerUI.t = new Thread(ShutdownServer.getInstance());
                ServerUI.ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "服务器将在 " + minutesLeft + "分钟后重启.请各位玩家提前下线.").getBytes());
                        System.out.println("服务器将在 " + minutesLeft + "分钟后重启.请各位玩家提前下线.");
                        minutesLeft--;
                    }
                }, 60000L);
            }
            this.jTextField22.setText("关闭服务器倒数时间");
            this.printChatLog(输出);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + e);
        }
    }
    
    private void 是否允许顶号() {
        ServerConstants.是否允许顶号 = !ServerConstants.是否允许顶号;
        final String 输出 = "正在启动 - 顶号登陆状态 :" + (ServerConstants.is是否允许顶号() ? "开启" : "关闭");
        System.out.println(输出);
    }
    
    private void 限制登陆() {
        ServerConstants.限制登陆1 = !ServerConstants.限制登陆1;
        final String 输出 = "正在启动 - 服务器禁止登陆 :" + (ServerConstants.get限制登陆1() ? "开启" : "关闭");
        System.out.println(输出);
    }
    
    private void 冒险家() {
        CharLoginHandler.冒险家 = !CharLoginHandler.冒险家;
    }
    
    private void 骑士团() {
        CharLoginHandler.骑士团 = !CharLoginHandler.骑士团;
    }
    
    private void 战神() {
        CharLoginHandler.战神 = !CharLoginHandler.战神;
    }
    
    private void 天气功能() {
        final String 输出 = "目前天气系统已经" + (ServerConstants.get天气() ? "开启" : "关闭");
        ServerConstants.天气功能 = !ServerConstants.天气功能;
        this.printChatLog(输出);
    }
    
    private void get38记录() {
        final String 输出 = "开启38文件记录" + (ServerConstants.get38记录() ? "关闭" : "开启");
        ServerConstants.Change38记录();
        this.printChatLog(输出);
    }
    
    public static void main(final String[] args) {
        System.setProperty("path", "");
        System.setProperty("wzpath", ServerProperties.getProperty("net.sf.odinms.wzpath", "wz"));
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ServerUI.getInstance().setVisible(true);
            }
        });
    }
    
    @Override
    public void setVisible(final boolean bln) {
        final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int)(size.getWidth() - this.getWidth()) / 2, (int)(size.getHeight() - this.getHeight()) / 2);
        super.setVisible(bln);
        System.setOut(ServerUI.out);
        System.setErr(ServerUI.err);
    }
    
    static {
        ServerUI.instance = new ServerUI();
        ServerUI.t = null;
        ServerUI.ts = null;
        ServerUI.out = new GUIPrintStream(System.out, ServerUI.output_jTextPane1, ServerUI.output_out_jTextPane1, 0);
        ServerUI.err = new GUIPrintStream(System.err, ServerUI.output_jTextPane1, ServerUI.output_err_jTextPane1, 1);
        ServerUI.notice = new GUIPrintStream(System.out, ServerUI.output_jTextPane1, ServerUI.output_notice_jTextPane1, 2);
        ServerUI.packet = new GUIPrintStream(System.out, ServerUI.output_jTextPane1, ServerUI.output_packet_jTextPane1, 3);
    }
    
    public enum Tools
    {
        DumpItems, 
        DumpQuests, 
        DumpMobSkills;
    }
    
    public enum Windows
    {
        BuffStatusCalculator, 
        SearchGenerator;
    }
    
    private enum ServerModifyType
    {
        EXP, 
        MESO, 
        DROP, 
        FLAG, 
        SHOW, 
        AVAILABLE, 
        CHANNELS, 
        WORLD_TIP;
    }
}
