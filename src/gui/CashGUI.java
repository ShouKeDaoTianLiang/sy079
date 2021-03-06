package gui;

import server.Start;
import handling.login.LoginServer;
import java.awt.EventQueue;
import server.CashItemInfo;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.RowFilter;
import java.util.ArrayList;
import javax.swing.table.TableRowSorter;
import java.sql.Connection;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import database.DatabaseConnection;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.Alignment;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Cursor;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.ImageIcon;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import provider.MapleDataProviderFactory;
import java.io.File;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;
import server.MapleItemInformationProvider;
import provider.MapleData;
import provider.MapleDataProvider;
import javax.swing.JFrame;

public class CashGUI extends JFrame
{
    private static final MapleDataProvider Mobdata;
    private static final MapleDataProvider npcData;
    private static final MapleDataProvider stringDataWZ;
    private static final MapleDataProvider etcDataWZ;
    private static final MapleData mobStringData;
    private static final MapleData npcStringData;
    private static final MapleData npclocData;
    MapleDataProvider stringProvider;
    MapleItemInformationProvider ii;
    MapleData cash;
    MapleData consume;
    MapleData eqp;
    MapleData etc;
    MapleData ins;
    MapleData pet;
    MapleData map;
    MapleData mob;
    MapleData skill;
    MapleData npc;
    private final MapleDataProvider data;
    private final MapleData commodities;
    private Thread server;
    private JTable charTable;
    private JButton jButton1;
    private JButton jButton13;
    private JButton jButton23;
    private JButton jButton24;
    private JButton jButton25;
    private JButton jButton26;
    private JButton jButton27;
    private JButton jButton28;
    private JButton jButton29;
    private JButton jButton30;
    private JButton jButton31;
    private JButton jButton32;
    private JButton jButton33;
    private JButton jButton34;
    private JButton jButton35;
    private JButton jButton36;
    private JButton jButton37;
    private JButton jButton38;
    private JButton jButton39;
    private JButton jButton40;
    private JButton jButton41;
    private JButton jButton42;
    private JButton jButton43;
    private JButton jButton44;
    private JButton jButton45;
    private JButton jButton46;
    private JButton jButton47;
    private JButton jButton48;
    private JButton jButton49;
    private JButton jButton50;
    private JButton jButton51;
    private JButton jButton52;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel29;
    private JLabel jLabel30;
    private JLabel jLabel31;
    private JLabel jLabel32;
    private JLabel jLabel33;
    private JLabel jLabel34;
    private JLabel jLabel35;
    private JLabel jLabel36;
    private JLabel jLabel37;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JScrollPane jScrollPane5;
    private JTabbedPane jTabbedPane1;
    private JTextField jTextField1;
    private JTextField jTextField10;
    private JTextField jTextField11;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JTextField jTextField6;
    private JTextField jTextField7;
    private JTextField jTextField8;
    private JTextField jTextField9;
    private JButton ????????????;
    private JButton ????????????1;
    
    public CashGUI() {
        this.stringProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
        this.ii = MapleItemInformationProvider.getInstance();
        this.cash = this.stringProvider.getData("Cash.img");
        this.consume = this.stringProvider.getData("Consume.img");
        this.eqp = this.stringProvider.getData("Eqp.img").getChildByPath("Eqp");
        this.etc = this.stringProvider.getData("Etc.img").getChildByPath("Etc");
        this.ins = this.stringProvider.getData("Ins.img");
        this.pet = this.stringProvider.getData("Pet.img");
        this.map = this.stringProvider.getData("Map.img");
        this.mob = this.stringProvider.getData("Mob.img");
        this.skill = this.stringProvider.getData("Skill.img");
        this.npc = this.stringProvider.getData("Npc.img");
        this.data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
        this.commodities = this.data.getData("Commodity.img");
        this.server = null;
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex2) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (IllegalAccessException ex3) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (UnsupportedLookAndFeelException ex4) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex4);
        }
        final ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("gui/Icon.png"));
        this.setIconImage(icon.getImage());
        this.setTitle("????????????????????? ???????????????v079");
        this.initComponents();
        this.initCharacterPannel();
    }
    
    private void initComponents() {
        this.jTabbedPane1 = new JTabbedPane();
        this.jPanel1 = new JPanel();
        this.jLabel9 = new JLabel();
        this.jScrollPane5 = new JScrollPane();
        this.charTable = new JTable();
        this.jTextField3 = new JTextField();
        this.jTextField1 = new JTextField();
        this.jTextField2 = new JTextField();
        this.jButton24 = new JButton();
        this.jButton25 = new JButton();
        this.jButton26 = new JButton();
        this.jLabel30 = new JLabel();
        this.jLabel29 = new JLabel();
        this.jLabel31 = new JLabel();
        this.jTextField4 = new JTextField();
        this.jTextField5 = new JTextField();
        this.jTextField6 = new JTextField();
        this.jTextField7 = new JTextField();
        this.jTextField8 = new JTextField();
        this.jLabel32 = new JLabel();
        this.jLabel33 = new JLabel();
        this.jLabel34 = new JLabel();
        this.jLabel35 = new JLabel();
        this.jLabel36 = new JLabel();
        this.jTextField9 = new JTextField();
        this.jLabel1 = new JLabel();
        this.jTextField11 = new JTextField();
        this.jLabel2 = new JLabel();
        this.jButton13 = new JButton();
        this.jButton27 = new JButton();
        this.jButton1 = new JButton();
        this.jButton28 = new JButton();
        this.jButton29 = new JButton();
        this.jButton30 = new JButton();
        this.jButton31 = new JButton();
        this.jButton32 = new JButton();
        this.jButton33 = new JButton();
        this.jButton34 = new JButton();
        this.jButton35 = new JButton();
        this.jButton36 = new JButton();
        this.jButton37 = new JButton();
        this.jButton38 = new JButton();
        this.jButton39 = new JButton();
        this.jButton42 = new JButton();
        this.jButton44 = new JButton();
        this.jButton23 = new JButton();
        this.jButton40 = new JButton();
        this.jButton41 = new JButton();
        this.jButton43 = new JButton();
        this.jButton45 = new JButton();
        this.jButton46 = new JButton();
        this.jButton47 = new JButton();
        this.jButton48 = new JButton();
        this.jButton49 = new JButton();
        this.jButton50 = new JButton();
        this.jButton51 = new JButton();
        this.jButton52 = new JButton();
        this.???????????? = new JButton();
        this.????????????1 = new JButton();
        this.jTextField10 = new JTextField();
        this.jLabel37 = new JLabel();
        this.setDefaultCloseOperation(3);
        this.setCursor(new Cursor(0));
        this.setName("frame");
        this.jTabbedPane1.setBorder(BorderFactory.createCompoundBorder());
        this.jTabbedPane1.setToolTipText("");
        this.jTabbedPane1.setCursor(new Cursor(0));
        this.jTabbedPane1.setMaximumSize(new Dimension(184, 415));
        this.jTabbedPane1.setName("?????????????????????");
        this.charTable.setModel(new DefaultTableModel(new Object[0][], new String[] { "SN", "??????ID", "????????????", "??????", "??????", "??????", "??????", "??????", "??????", "??????(0/1)" }));
        this.jScrollPane5.setViewportView(this.charTable);
        if (this.charTable.getColumnModel().getColumnCount() > 0) {
            this.charTable.getColumnModel().getColumn(1).setPreferredWidth(60);
            this.charTable.getColumnModel().getColumn(3).setPreferredWidth(40);
            this.charTable.getColumnModel().getColumn(4).setPreferredWidth(50);
            this.charTable.getColumnModel().getColumn(5).setPreferredWidth(30);
            this.charTable.getColumnModel().getColumn(6).setPreferredWidth(30);
            this.charTable.getColumnModel().getColumn(7).setPreferredWidth(30);
            this.charTable.getColumnModel().getColumn(8).setPreferredWidth(30);
        }
        this.jTextField3.setText("1");
        this.jTextField2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jTextField2ActionPerformed(evt);
            }
        });
        this.jButton24.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/?????????-????????????03(ic03)_?????????_aigei_com.png")));
        this.jButton24.setText("????????????");
        this.jButton24.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton24ActionPerformed(evt);
            }
        });
        this.jButton25.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/?????????-????????????02(ic02)_?????????_aigei_com.png")));
        this.jButton25.setText("????????????");
        this.jButton25.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton25ActionPerformed(evt);
            }
        });
        this.jButton26.setText("??????SN");
        this.jButton26.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton26ActionPerformed(evt);
            }
        });
        this.jLabel30.setText("??????");
        this.jLabel29.setText("??????ID");
        this.jLabel31.setText("??????SN");
        this.jTextField5.setText("0");
        this.jTextField5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jTextField5ActionPerformed(evt);
            }
        });
        this.jTextField8.setText("-1");
        this.jTextField8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jTextField8ActionPerformed(evt);
            }
        });
        this.jLabel32.setText("??????");
        this.jLabel33.setText("??????");
        this.jLabel34.setText("??????");
        this.jLabel35.setText("??????");
        this.jLabel36.setText("??????");
        this.jTextField9.setText("1");
        this.jTextField9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jTextField9ActionPerformed(evt);
            }
        });
        this.jLabel1.setText("????????????(0???/1???)");
        this.jButton13.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/?????????-????????????05(ic05)_?????????_aigei_com.png")));
        this.jButton13.setText("????????????");
        this.jButton13.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton13ActionPerformed(evt);
            }
        });
        this.jButton27.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/?????????-????????????06(ic06)_?????????_aigei_com.png")));
        this.jButton27.setText("????????????");
        this.jButton27.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton27ActionPerformed(evt);
            }
        });
        this.jButton1.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/item39.png")));
        this.jButton1.setText("????????????");
        this.jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton1ActionPerformed(evt);
            }
        });
        this.jButton28.setText("??????");
        this.jButton28.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton28ActionPerformed(evt);
            }
        });
        this.jButton29.setText("??????");
        this.jButton29.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton29ActionPerformed(evt);
            }
        });
        this.jButton30.setText("??????");
        this.jButton30.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton30ActionPerformed(evt);
            }
        });
        this.jButton31.setText("??????");
        this.jButton31.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton31ActionPerformed(evt);
            }
        });
        this.jButton32.setText("??????");
        this.jButton32.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton32ActionPerformed(evt);
            }
        });
        this.jButton33.setText("??????");
        this.jButton33.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton33ActionPerformed(evt);
            }
        });
        this.jButton34.setText("??????");
        this.jButton34.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton34ActionPerformed(evt);
            }
        });
        this.jButton35.setText("??????");
        this.jButton35.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton35ActionPerformed(evt);
            }
        });
        this.jButton36.setText("??????");
        this.jButton36.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton36ActionPerformed(evt);
            }
        });
        this.jButton37.setText("??????");
        this.jButton37.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton37ActionPerformed(evt);
            }
        });
        this.jButton38.setText("??????");
        this.jButton38.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton38ActionPerformed(evt);
            }
        });
        this.jButton39.setText("??????");
        this.jButton39.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton39ActionPerformed(evt);
            }
        });
        this.jButton42.setText("????????????");
        this.jButton42.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton42ActionPerformed(evt);
            }
        });
        this.jButton44.setText("????????????");
        this.jButton44.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton44ActionPerformed(evt);
            }
        });
        this.jButton23.setIcon(new ImageIcon(this.getClass().getResource("/gui/Icon/?????????-????????????01(ic01)_?????????_aigei_com.png")));
        this.jButton23.setText("????????????");
        this.jButton23.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton23ActionPerformed(evt);
            }
        });
        this.jButton40.setText("??????");
        this.jButton40.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton40ActionPerformed(evt);
            }
        });
        this.jButton41.setText("??????");
        this.jButton41.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton41ActionPerformed(evt);
            }
        });
        this.jButton43.setText("??????");
        this.jButton43.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton43ActionPerformed(evt);
            }
        });
        this.jButton45.setText("??????");
        this.jButton45.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton45ActionPerformed(evt);
            }
        });
        this.jButton46.setText("??????");
        this.jButton46.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton46ActionPerformed(evt);
            }
        });
        this.jButton47.setText("??????");
        this.jButton47.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton47ActionPerformed(evt);
            }
        });
        this.jButton48.setText("??????");
        this.jButton48.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton48ActionPerformed(evt);
            }
        });
        this.jButton49.setText("??????");
        this.jButton49.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton49ActionPerformed(evt);
            }
        });
        this.jButton50.setText("?????????");
        this.jButton50.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton50ActionPerformed(evt);
            }
        });
        this.jButton51.setText("?????????");
        this.jButton51.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton51ActionPerformed(evt);
            }
        });
        this.jButton52.setText("????????????");
        this.jButton52.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.jButton52ActionPerformed(evt);
            }
        });
        this.????????????.setText("????????????");
        this.????????????.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.????????????ActionPerformed(evt);
            }
        });
        this.????????????1.setText("??????");
        this.????????????1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                CashGUI.this.????????????1ActionPerformed(evt);
            }
        });
        this.jLabel37.setText("????????????");
        final GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane5).addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(0, 6, 32767).addComponent(this.jLabel2).addGap(618, 618, 618).addComponent(this.jLabel9, -2, 232, -2).addGap(20, 20, 20)).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jButton40, -2, 66, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton35, -2, 66, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton45, -2, 65, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton46, -1, -1, 32767)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jButton28, -2, 66, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton29, -2, 66, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton30, -2, 65, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton31, -2, 67, -2))).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton47, -1, 66, 32767).addComponent(this.jButton32, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton48, -1, 67, 32767).addComponent(this.jButton33, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton34, -1, 64, 32767).addComponent(this.jButton49, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jButton43, -1, 70, 32767).addComponent(this.jButton41, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton36, -2, 70, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jButton37, -2, 69, -2).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addComponent(this.jButton38, -2, 70, -2).addGap(6, 6, 6).addComponent(this.jButton39, -2, 66, -2)).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jButton23).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton25).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton24).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton13).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton27).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton1)).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jButton52).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.????????????1, -2, 62, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton51).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jButton50).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.????????????).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton44, -2, 89, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton42, -2, 86, -2)).addGroup(jPanel1Layout.createSequentialGroup().addGap(73, 73, 73).addComponent(this.jLabel29, -2, 36, -2).addGap(56, 56, 56).addComponent(this.jLabel30).addGap(60, 60, 60).addComponent(this.jLabel32).addGap(60, 60, 60).addComponent(this.jLabel33).addGap(60, 60, 60).addComponent(this.jLabel34)))).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField1, -2, 78, -2).addComponent(this.jLabel31)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel37).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jTextField10, -2, 70, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField2, -2, 86, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField3, -2, 78, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField4, -2, 78, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField5, -2, 78, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jTextField6, -2, 78, -2))))).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel35).addComponent(this.jTextField7, -2, 78, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField8, -2, 78, -2).addComponent(this.jLabel36, -2, 35, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField9, -2, 78, -2).addComponent(this.jLabel1, -2, 104, -2))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jTextField11, -2, 78, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton26))).addGap(0, 0, 32767))).addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(31, 31, 31).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel9).addComponent(this.jLabel2)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton26).addComponent(this.jTextField11, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jScrollPane5, -2, 353, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton28).addComponent(this.jButton29).addComponent(this.jButton30).addComponent(this.jButton31).addComponent(this.jButton32).addComponent(this.jButton33).addComponent(this.jButton34).addComponent(this.jButton36).addComponent(this.jButton37).addComponent(this.jButton38).addComponent(this.jButton39).addComponent(this.jButton43)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton40).addComponent(this.jButton45).addComponent(this.jButton46).addComponent(this.jButton47).addComponent(this.jButton48).addComponent(this.jButton49).addComponent(this.jButton35).addComponent(this.jButton41)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton52, -2, 23, -2).addComponent(this.????????????1).addComponent(this.jButton51).addComponent(this.jButton50).addComponent(this.????????????).addComponent(this.jButton44).addComponent(this.jButton42)).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel31).addComponent(this.jLabel37).addComponent(this.jLabel29).addComponent(this.jLabel30).addComponent(this.jLabel32).addComponent(this.jLabel33).addComponent(this.jLabel34).addComponent(this.jLabel35).addComponent(this.jLabel36).addComponent(this.jLabel1)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jTextField1, -2, -1, -2).addComponent(this.jTextField10, -2, -1, -2).addComponent(this.jTextField2, -2, -1, -2).addComponent(this.jTextField3, -2, -1, -2).addComponent(this.jTextField4, -2, -1, -2).addComponent(this.jTextField5, -2, -1, -2).addComponent(this.jTextField6, -2, -1, -2).addComponent(this.jTextField7, -2, -1, -2).addComponent(this.jTextField8, -2, -1, -2).addComponent(this.jTextField9, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED, 26, 32767).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton23).addComponent(this.jButton25).addComponent(this.jButton24).addComponent(this.jButton13).addComponent(this.jButton27)).addComponent(this.jButton1, Alignment.TRAILING, -2, 39, -2)).addGap(12, 12, 12)));
        this.jTabbedPane1.addTab("?????????????????????", new ImageIcon(this.getClass().getResource("/gui/Icon.png")), this.jPanel1);
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jTabbedPane1, -2, -1, -2).addContainerGap(-1, 32767)));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jTabbedPane1, -2, 701, -2).addContainerGap(-1, 32767)));
        this.jTabbedPane1.getAccessibleContext().setAccessibleName("?????????????????????");
        this.getAccessibleContext().setAccessibleDescription("");
        this.pack();
    }
    
    private void jButton24ActionPerformed(final ActionEvent evt) {
        String ?????? = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        final boolean result = this.jTextField1.getText().matches("[0-9]+");
        if (result) {
            int ??????SN?????? = Integer.parseInt(this.jTextField1.getText());
            if ((??????SN?????? >= 10000000 && ??????SN?????? < 40000000) || (??????SN?????? >= 50000000 && ??????SN?????? < 80000000)) {
                ??????SN?????? = Integer.parseInt(this.jTextField1.getText());
            }
            else {
                ??????SN?????? = 0;
            }
            try {
                for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
                    ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
                }
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                ps1.setInt(1, ??????SN??????);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    final String sqlstr = " delete from cashshop_modified_items where serial =" + ??????SN?????? + ";";
                    ps1.executeUpdate(sqlstr);
                    System.out.println("????????????");
                    this.initCharacterPannel();
                    ?????? = "???????????????SN?????????";
                }
                else {
                    ?????? = "??????????????????SN????????????????????????";
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, ??????);
        }
        else {
            ?????? = "????????????????????????????????????";
            JOptionPane.showMessageDialog(null, ??????);
        }
    }
    
    private void jButton25ActionPerformed(final ActionEvent evt) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        String ?????? = "";
        String ???????????? = "";
        final boolean result = this.jTextField1.getText().matches("[0-9]+");
        final boolean resultA = this.jTextField2.getText().matches("[0-9]+");
        final boolean resultB = this.jTextField4.getText().matches("[0-9]+");
        final boolean resultC = this.jTextField7.getText().matches("[0-9]+");
        final boolean resultD = this.jTextField6.getText().matches("[0-9]+");
        final boolean resultE = this.jTextField5.getText().matches("[0-9]+");
        final boolean resultF = this.jTextField3.getText().matches("[0-9]+");
        final boolean resultG = this.jTextField8.getText().matches("[0-9]+");
        final boolean resultI = this.jTextField9.getText().matches("[0-9]+");
        final boolean resultJ = this.jTextField9.getText().matches("[0-9]+");
        if (result && resultA && resultB && resultC && resultD && resultE && resultF && resultG && resultI && resultJ) {
            int ??????SN??????;
            if ((Integer.parseInt(this.jTextField1.getText()) >= 10000000 && Integer.parseInt(this.jTextField1.getText()) < 40000000) || (Integer.parseInt(this.jTextField1.getText()) >= 50000000 && Integer.parseInt(this.jTextField1.getText()) < 80000000)) {
                ??????SN?????? = Integer.parseInt(this.jTextField1.getText());
            }
            else {
                ??????SN?????? = 0;
            }
            int ????????????ID;
            if (ii.itemExists(Integer.parseInt(this.jTextField2.getText()))) {
                ????????????ID = Integer.parseInt(this.jTextField2.getText());
            }
            else {
                ????????????ID = 0;
            }
            if (this.jTextField10.getText() != "" || this.jTextField10.getText() != null) {
                ???????????? = this.jTextField10.getText();
            }
            else {
                ???????????? = "";
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField4.getText()) > 0 && Integer.parseInt(this.jTextField4.getText()) <= Integer.MAX_VALUE) {
                ???????????? = Integer.parseInt(this.jTextField4.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField7.getText()) >= 0 && Integer.parseInt(this.jTextField7.getText()) <= 999) {
                ???????????? = Integer.parseInt(this.jTextField7.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField6.getText()) >= 0 && Integer.parseInt(this.jTextField6.getText()) <= 2) {
                ???????????? = Integer.parseInt(this.jTextField6.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField5.getText()) >= 0 && Integer.parseInt(this.jTextField5.getText()) <= 36500) {
                ???????????? = Integer.parseInt(this.jTextField5.getText());
            }
            else {
                ???????????? = 0;
            }
            int ??????;
            if (Integer.parseInt(this.jTextField3.getText()) >= 0 && Integer.parseInt(this.jTextField3.getText()) <= 10000) {
                if (GameConstants.getInventoryType(Integer.parseInt(this.jTextField2.getText())) == MapleInventoryType.EQUIP) {
                    ?????? = 1;
                }
                else {
                    ?????? = Integer.parseInt(this.jTextField3.getText());
                }
            }
            else {
                ?????? = 0;
            }
            int ??????;
            if (Integer.parseInt(this.jTextField8.getText()) >= 0 && Integer.parseInt(this.jTextField8.getText()) <= 3) {
                ?????? = Integer.parseInt(this.jTextField8.getText());
            }
            else {
                ?????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField9.getText()) >= 0 && Integer.parseInt(this.jTextField9.getText()) <= 2) {
                ???????????? = Integer.parseInt(this.jTextField9.getText());
            }
            else {
                ???????????? = 0;
            }
            final byte class_ = 0;
            if (??????SN?????? == 0 || ????????????ID == 0 || ?????? == 0) {
                if (??????SN?????? == 0) {
                    ?????? = "??????SN??????[???????????????8??????] ?????????????????????????????????????????????";
                }
                else if (????????????ID == 0) {
                    ?????? = "????????????ID ?????????????????????????????????????????????";
                }
                else if (?????? == 0) {
                    ?????? = "?????? ?????????????????????????????????????????????";
                }
            }
            else {
                PreparedStatement ps = null;
                PreparedStatement ps2 = null;
                ResultSet rs = null;
                try {
                    for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
                        ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
                    }
                    ps = DatabaseConnection.getConnection().prepareStatement("UPDATE cashshop_modified_items SET showup = ?, name = ?, itemid = ?, priority = ?, period = ?, gender = ?, count = ?, meso = ?, discount_price = ?, mark = ?, unk_1 = ?, unk_2 = ?, unk_3 = ? WHERE serial = ?");
                    ps2 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                    ps2.setInt(1, ??????SN??????);
                    rs = ps2.executeQuery();
                    if (rs.next()) {
                        String sqlString = null;
                        String sqlString2 = null;
                        String sqlString3 = null;
                        String sqlString4 = null;
                        String sqlString5 = null;
                        String sqlString6 = null;
                        String sqlString7 = null;
                        String sqlString8 = null;
                        String sqlString9 = null;
                        sqlString = "update cashshop_modified_items set showup='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement showup = DatabaseConnection.getConnection().prepareStatement(sqlString);
                        showup.executeUpdate(sqlString);
                        sqlString2 = "update cashshop_modified_items set itemid='" + ????????????ID + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                        itemid.executeUpdate(sqlString2);
                        sqlString3 = "update cashshop_modified_items set priority='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement priority = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                        priority.executeUpdate(sqlString3);
                        sqlString4 = "update cashshop_modified_items set period='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement period = DatabaseConnection.getConnection().prepareStatement(sqlString4);
                        period.executeUpdate(sqlString4);
                        sqlString5 = "update cashshop_modified_items set gender='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement gender = DatabaseConnection.getConnection().prepareStatement(sqlString5);
                        gender.executeUpdate(sqlString5);
                        sqlString6 = "update cashshop_modified_items set count='" + ?????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement count = DatabaseConnection.getConnection().prepareStatement(sqlString6);
                        count.executeUpdate(sqlString6);
                        sqlString7 = "update cashshop_modified_items set discount_price='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement discount_price = DatabaseConnection.getConnection().prepareStatement(sqlString7);
                        discount_price.executeUpdate(sqlString7);
                        sqlString8 = "update cashshop_modified_items set mark='" + ?????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement mark = DatabaseConnection.getConnection().prepareStatement(sqlString8);
                        mark.executeUpdate(sqlString8);
                        sqlString9 = "update cashshop_modified_items set name='" + ???????????? + "' where serial=" + ??????SN?????? + ";";
                        final PreparedStatement vip = DatabaseConnection.getConnection().prepareStatement(sqlString9);
                        vip.executeUpdate(sqlString9);
                        this.initCharacterPannel();
                        ?????? = "???????????????????????????";
                    }
                    else {
                        ?????? = "???????????????????????????????????????SN???????????????????????????";
                    }
                }
                catch (SQLException ex) {
                    Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            ?????? = "?????????????????????????????????????????????";
        }
        JOptionPane.showMessageDialog(null, ??????);
    }
    
    private void jButton23ActionPerformed(final ActionEvent evt) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        String ?????? = "";
        String ???????????? = "";
        final boolean result = this.jTextField1.getText().matches("[0-9]+");
        final boolean resultA = this.jTextField2.getText().matches("[0-9]+");
        final boolean resultB = this.jTextField4.getText().matches("[0-9]+");
        final boolean resultC = this.jTextField7.getText().matches("[0-9]+");
        final boolean resultD = this.jTextField6.getText().matches("[0-9]+");
        final boolean resultE = this.jTextField5.getText().matches("[0-9]+");
        final boolean resultF = this.jTextField3.getText().matches("[0-9]+");
        final boolean resultG = this.jTextField8.getText().matches("[0-9]+");
        final boolean resultI = this.jTextField9.getText().matches("[0-9]+");
        if (result && resultA && resultB && resultC && resultD && resultE && resultF && resultG && resultI) {
            int ??????SN??????;
            if ((Integer.parseInt(this.jTextField1.getText()) >= 10000000 && Integer.parseInt(this.jTextField1.getText()) < 40000000) || (Integer.parseInt(this.jTextField1.getText()) >= 50000000 && Integer.parseInt(this.jTextField1.getText()) < 80000000)) {
                ??????SN?????? = Integer.parseInt(this.jTextField1.getText());
            }
            else {
                ??????SN?????? = 0;
            }
            if (this.jTextField10.getText() != "" || this.jTextField10.getText() != null) {
                ???????????? = this.jTextField10.getText();
            }
            else {
                ???????????? = "";
            }
            int ????????????ID;
            if (ii.itemExists(Integer.parseInt(this.jTextField2.getText()))) {
                ????????????ID = Integer.parseInt(this.jTextField2.getText());
            }
            else {
                ????????????ID = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField4.getText()) > 0 && Integer.parseInt(this.jTextField4.getText()) <= Integer.MAX_VALUE) {
                ???????????? = Integer.parseInt(this.jTextField4.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField7.getText()) >= 0 && Integer.parseInt(this.jTextField7.getText()) <= 999) {
                ???????????? = Integer.parseInt(this.jTextField7.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField6.getText()) >= 0 && Integer.parseInt(this.jTextField6.getText()) <= 2) {
                ???????????? = Integer.parseInt(this.jTextField6.getText());
            }
            else {
                ???????????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField5.getText()) >= 0 && Integer.parseInt(this.jTextField5.getText()) <= 36500) {
                ???????????? = Integer.parseInt(this.jTextField5.getText());
            }
            else {
                ???????????? = 0;
            }
            int ??????;
            if (Integer.parseInt(this.jTextField3.getText()) >= 0 && Integer.parseInt(this.jTextField3.getText()) <= 10000) {
                if (GameConstants.getInventoryType(Integer.parseInt(this.jTextField2.getText())) == MapleInventoryType.EQUIP) {
                    ?????? = 1;
                }
                else {
                    ?????? = Integer.parseInt(this.jTextField3.getText());
                }
            }
            else {
                ?????? = 0;
            }
            int ??????;
            if (Integer.parseInt(this.jTextField8.getText()) >= 0 && Integer.parseInt(this.jTextField8.getText()) <= 3) {
                ?????? = Integer.parseInt(this.jTextField8.getText());
            }
            else {
                ?????? = 0;
            }
            int ????????????;
            if (Integer.parseInt(this.jTextField9.getText()) >= 0 && Integer.parseInt(this.jTextField9.getText()) <= 1) {
                ???????????? = Integer.parseInt(this.jTextField9.getText());
            }
            else {
                ???????????? = 0;
            }
            if (??????SN?????? == 0 || ????????????ID == 0 || ?????? == 0) {
                if (??????SN?????? == 0) {
                    ?????? = "??????SN??????[???????????????8??????] ?????????????????????????????????????????????";
                }
                else if (????????????ID == 0) {
                    ?????? = "????????????ID ?????????????????????????????????????????????";
                }
                else if (?????? == 0) {
                    ?????? = "?????? ?????????????????????????????????????????????";
                }
            }
            else {
                for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
                    ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
                }
                PreparedStatement ps1 = null;
                ResultSet rs = null;
                try {
                    ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                    ps1.setInt(1, ??????SN??????);
                    rs = ps1.executeQuery();
                    if (!rs.next()) {
                        try (final Connection con = DatabaseConnection.getConnection();
                             final PreparedStatement ps2 = con.prepareStatement("INSERT INTO cashshop_modified_items (serial, showup,itemid,name,priority,period,gender,count,meso,discount_price,mark, unk_1, unk_2, unk_3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                            ps2.setInt(1, ??????SN??????);
                            ps2.setInt(2, ????????????);
                            ps2.setInt(3, ????????????ID);
                            ps2.setString(4, ????????????);
                            ps2.setInt(5, ????????????);
                            ps2.setInt(6, ????????????);
                            ps2.setInt(7, ????????????);
                            ps2.setInt(8, (?????? >= 1) ? ?????? : 0);
                            ps2.setInt(9, 0);
                            ps2.setInt(10, ????????????);
                            ps2.setInt(11, ??????);
                            ps2.setInt(12, 0);
                            ps2.setInt(13, 0);
                            ps2.setInt(14, 0);
                            ps2.executeUpdate();
                        }
                        catch (SQLException ex) {
                            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        this.initCharacterPannel();
                        ?????? = "????????????????????????";
                    }
                    else {
                        ?????? = "????????????SN???????????????????????????\r\n?????????????????????????????????SN?????????\r\n???????????? XXX90000 ??????";
                    }
                }
                catch (SQLException ex) {
                    Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            ?????? = "?????????????????????????????????????????????";
        }
        JOptionPane.showMessageDialog(null, ??????);
    }
    
    private void jButton26ActionPerformed(final ActionEvent evt) {
        if (this.jTextField11.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "?????????ID", "??????", 0);
        }
        else {
            final TableRowSorter sorter = new TableRowSorter(this.charTable.getModel());
            final List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
            final String filterString = this.jTextField11.getText().trim();
            if (filterString.length() > 0) {
                filters.add(RowFilter.regexFilter(filterString, 0));
            }
            if (filters.size() > 0) {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
            else {
                sorter.setRowFilter(null);
            }
            this.charTable.setRowSorter(sorter);
        }
    }
    
    private void jButton13ActionPerformed(final ActionEvent evt) {
        final int n = JOptionPane.showConfirmDialog(this, "?????????[ " + this.jTextField1.getText() + " ??????]    ???????", "????????????????????????", 0);
        if (n == 0) {
            this.??????();
        }
        else if (n == 1) {
            JOptionPane.showMessageDialog(this, "bug");
        }
    }
    
    private void jButton27ActionPerformed(final ActionEvent evt) {
        final int n = JOptionPane.showConfirmDialog(this, "?????????[ " + this.jTextField1.getText() + " ??????]    ???????", "????????????????????????", 0);
        if (n == 0) {
            this.??????();
        }
        else if (n == 1) {
            JOptionPane.showMessageDialog(this, "bug");
        }
    }
    
    private void jButton1ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel();
        JOptionPane.showMessageDialog(null, "??????????????????????????????");
    }
    
    private void jTextField2ActionPerformed(final ActionEvent evt) {
    }
    
    public void initCharacterPannel(final int i) {
        for (int ii = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; ii >= 0; --ii) {
            ((DefaultTableModel)this.charTable.getModel()).removeRow(ii);
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("serial") / 100000 == i) {
                    ((DefaultTableModel)this.charTable.getModel()).insertRow(this.charTable.getRowCount(), new Object[] { rs.getInt("serial"), rs.getInt("itemid"), rs.getString("name"), rs.getInt("count"), rs.getInt("discount_price"), rs.getInt("period"), rs.getInt("gender"), rs.getInt("priority"), rs.getInt("mark"), rs.getInt("showup") });
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(null, "???????????????");
        this.charTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final int i = charTable.getSelectedRow();
                final String a1 = charTable.getValueAt(i, 0).toString();
                final String a2 = charTable.getValueAt(i, 1).toString();
                final String a3 = charTable.getValueAt(i, 3).toString();
                final String a4 = charTable.getValueAt(i, 4).toString();
                final String a5 = charTable.getValueAt(i, 5).toString();
                final String a6 = charTable.getValueAt(i, 6).toString();
                final String a7 = charTable.getValueAt(i, 7).toString();
                final String a8 = charTable.getValueAt(i, 8).toString();
                final String a9 = charTable.getValueAt(i, 9).toString();
                final String a10 = charTable.getValueAt(i, 2).toString();
                jTextField1.setText(a1);
                jTextField2.setText(a2);
                jTextField3.setText(a3);
                jTextField4.setText(a4);
                jTextField5.setText(a5);
                jTextField6.setText(a6);
                jTextField7.setText(a7);
                jTextField8.setText(a8);
                jTextField9.setText(a9);
                jTextField10.setText(a10);
            }
        });
    }
    
    private void jButton28ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(100);
    }
    
    private void jButton29ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(200);
    }
    
    private void jButton30ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(201);
    }
    
    private void jButton31ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(202);
    }
    
    private void jButton32ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(203);
    }
    
    private void jButton33ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(204);
    }
    
    private void jButton34ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(205);
    }
    
    private void jButton35ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(206);
    }
    
    private void jButton36ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(207);
    }
    
    private void jButton37ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(208);
    }
    
    private void jButton38ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(209);
    }
    
    private void jButton39ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(210);
    }
    
    private void jButton42ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(601);
    }
    
    private void jButton44ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(602);
    }
    
    private void jButton40ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(211);
    }
    
    private void jButton41ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(600);
    }
    
    private void jButton43ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(212);
    }
    
    private void jButton45ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(201);
    }
    
    private void jButton46ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(501);
    }
    
    private void jButton47ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(504);
    }
    
    private void jButton48ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(505);
    }
    
    private void jButton49ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(502);
    }
    
    private void jButton50ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(503);
    }
    
    private void jButton51ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(500);
    }
    
    private void jButton52ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(301);
    }
    
    private void ????????????ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(300);
    }
    
    private void ????????????1ActionPerformed(final ActionEvent evt) {
        this.initCharacterPannel(700);
    }
    
    private void jTextField8ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField5ActionPerformed(final ActionEvent evt) {
    }
    
    private void jTextField9ActionPerformed(final ActionEvent evt) {
    }
    
    public void ??????() {
        try {
            final int SN_ = Integer.parseInt(String.valueOf(this.charTable.getValueAt(this.charTable.getSelectedRow(), 0)));
            for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
                ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
            }
            final int OnSale_ = 1;
            final CashItemInfo merchandise = new CashItemInfo(SN_, OnSale_);
            final int success = update?????????(merchandise);
            if (success == 0) {
                JOptionPane.showMessageDialog(this, "????????????");
            }
            else {
                this.initCharacterPannel();
                JOptionPane.showMessageDialog(this, "????????????.");
            }
        }
        catch (NumberFormatException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(this, "????????????,??????????????????????????????,");
        }
    }
    
    public void ??????() {
        try {
            final int SN_ = Integer.parseInt(String.valueOf(this.charTable.getValueAt(this.charTable.getSelectedRow(), 0)));
            for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
                ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
            }
            final int OnSale_ = 0;
            final CashItemInfo merchandise = new CashItemInfo(SN_, OnSale_);
            final int success = update?????????(merchandise);
            if (success == 0) {
                JOptionPane.showMessageDialog(this, "????????????");
            }
            else {
                this.initCharacterPannel();
                JOptionPane.showMessageDialog(this, "????????????");
            }
        }
        catch (NumberFormatException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(this, "????????????,??????????????????????????????");
        }
    }
    
    public static int update?????????(final CashItemInfo merchandise) {
        PreparedStatement ps = null;
        int resulet = 0;
        final Connection conn = DatabaseConnection.getConnection();
        int i = 0;
        try {
            ps = conn.prepareStatement("update cashshop_modified_items set showup = ? where serial = ?");
            ps.setInt(++i, merchandise.getOnSale());
            ps.setInt(++i, merchandise.getSN());
            resulet = ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resulet;
    }
    
    public static void main(final String[] args) {
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex3) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex3);
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CashGUI().setVisible(true);
            }
        });
    }
    
    private void startServer() {
        if (LoginServer.isShutdown() && this.server == null) {
            (this.server = new Thread() {
                @Override
                public void run() {
                    try {
                        JOptionPane.showMessageDialog(null, "???????????????????????????,????????????????????????");
                        Start.main(null);
                        JOptionPane.showMessageDialog(null, "????????????????????????");
                    }
                    catch (InterruptedException ex) {
                        Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }
        else {
            JOptionPane.showMessageDialog(null, "???????????????????????????");
        }
    }
    
    public void initCharacterPannel() {
        for (int i = ((DefaultTableModel)this.charTable.getModel()).getRowCount() - 1; i >= 0; --i) {
            ((DefaultTableModel)this.charTable.getModel()).removeRow(i);
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel)this.charTable.getModel()).insertRow(this.charTable.getRowCount(), new Object[] { rs.getInt("serial"), rs.getInt("itemid"), rs.getString("name"), rs.getInt("count"), rs.getInt("discount_price"), rs.getInt("period"), rs.getInt("gender"), rs.getInt("priority"), rs.getInt("mark"), rs.getInt("showup") });
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(CashGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.charTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final int i = charTable.getSelectedRow();
                final String a1 = charTable.getValueAt(i, 0).toString();
                final String a2 = charTable.getValueAt(i, 1).toString();
                final String a3 = charTable.getValueAt(i, 2).toString();
                final String a4 = charTable.getValueAt(i, 3).toString();
                final String a5 = charTable.getValueAt(i, 4).toString();
                final String a6 = charTable.getValueAt(i, 5).toString();
                final String a7 = charTable.getValueAt(i, 6).toString();
                final String a8 = charTable.getValueAt(i, 7).toString();
                final String a9 = charTable.getValueAt(i, 8).toString();
                final String a10 = charTable.getValueAt(i, 9).toString();
                jTextField1.setText(a1);
                jTextField2.setText(a2);
                jTextField3.setText(a4);
                jTextField4.setText(a5);
                jTextField5.setText(a6);
                jTextField6.setText(a7);
                jTextField7.setText(a8);
                jTextField8.setText(a9);
                jTextField9.setText(a10);
                jTextField10.setText(a3);
            }
        });
    }
    
    static {
        Mobdata = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Mob.wz"));
        npcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Npc.wz"));
        stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
        etcDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
        mobStringData = CashGUI.stringDataWZ.getData("Mob.img");
        npcStringData = CashGUI.stringDataWZ.getData("Npc.img");
        npclocData = CashGUI.etcDataWZ.getData("NpcLocation.img");
    }
}
