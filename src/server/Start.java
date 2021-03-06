package server;

import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamilyBuff;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import server.Timer.BuffTimer;
import server.Timer.CheatTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.MobTimer;
import server.Timer.TimerManager;
import server.Timer.WorldTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import client.MapleCharacter;
import client.SkillFactory;
import constants.OtherSettings;
import database.DatabaseConnection;

public class Start
{
    public static boolean Check;
    public static Start instance;
    private static int maxUsers;
    private static ServerSocket srvSocket;
    private static final int srvPort = 6350;
    public static ArrayList<Integer> unCheckList;
    
    public static void start() {
        WorldTimer.getInstance().register(() -> {}, 120000L);
    }
    
    private static void initializeSetting() {
        try (final Connection con = DatabaseConnection.getConnection();
             final PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `loggedin` = 0")) {
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }
    }
    
    public static void main(final String[] args) throws InterruptedException {
        Start.instance.startServer();
    }
    
    public void startServer() throws InterruptedException {
        Start.Check = false;
        checkSingleInstance();
        if (Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"))) {
            System.out.print("[???????????????]----------[?????????????????????????????????]");
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister"))) {
            System.out.println("[???????????????]----------[?????????????????????]");
        }
        System.out.println("[????????????????????????]: ????????????...");
        initializeSetting();
        System.out.println("[????????????????????????]:  v79.3");
        System.out.println("???????????? - " + FileoutputUtil.CurrentReadable_Time() + "");
        System.out.println("???????????? - ????????????????????????????????????????????????");
        World.init();
        new Thread() {
            @Override
            public void run() {
                WorldTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                EtcTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MobTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                CloneTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                EventTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                BuffTimer.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                TimerManager.getInstance().start();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                LoginInformationProvider.getInstance();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapleQuest.initQuests();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                System.out.println("???????????? - ????????????");
                MapleLifeFactory.loadQuestCounts();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                System.out.println("???????????? - ????????????");
                MapleItemInformationProvider.getInstance().load();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                System.out.println("???????????? - ????????????");
                RandomRewards.getInstance();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                SkillFactory.getSkill(99999999);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapleOxQuizFactory.getInstance().initialize();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapleCarnivalFactory.getInstance();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapleGuildRanking.getInstance().RankingUpdate();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MapleFamilyBuff.getBuffEntry();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                CashItemFactory.getInstance().initialize();
            }
        }.start();
        System.out.println("???????????? - ???????????????");
        final long startCashItemTime = System.currentTimeMillis();
        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();
        System.out.println("");
        CashShopServer.run_startup_configurations();
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000L);
        ????????????(1);
        RandomRewards.getInstance();
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance();
        System.out.println("???????????? - ??????????????????");
        LoginInformationProvider.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        }
        catch (SQLException e) {
            System.out.println("SpeedRunner??????:" + e);
        }
        System.out.println("???????????? - ??????????????????");
        World.registerRespawn();
        LoginServer.setOn();
        MapleMapFactory.loadCustomLife();
        ???????????????(ServerProperties.getProperty("channel.?????????????????????", ""));
        if (Boolean.parseBoolean(ServerProperties.getProperty("channel.????????????", "false"))) {
            ????????????(Integer.parseInt(ServerProperties.getProperty("channel.??????????????????", "10")));
            System.out.println("???????????? - ????????????????????????");
        }
        
        //???????????????
//		Runtime runtime = Runtime.getRuntime();
//		try {
//
//            runtime.exec("c:/dhl.exe");
//
//        } catch (Exception e) {
//           // System.out.println("Error!");
//        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("world.??????????????????", "false"))) {
            System.out.println("???????????? - ??????????????????");
            checkCopyItemFromSql(Integer.parseInt(ServerProperties.getProperty("world.??????????????????")));
        }
        System.out.println("");
        System.out.println("???????????????.????????????????????????.");
        System.err.println("????????????????????????");
        System.out.println("????????????:");
        System.out.println("?????????????????????????????????,????????????????????????,??????????????????????????????");
        System.out.println("???????????????24???????????????,??????????????????????????????????????????,???????????????!");
    }
    
    public static void printSection(String s) {
        for (s = "-[ " + s + " ]"; s.getBytes().length < 79; s = "=" + s) {}
        System.out.println(s);
    }
    
    public static void ???????????????(final String property) {
        try {
            Start.unCheckList.clear();
            final String[] chrids = property.split(",");
            for (int i = 0; i < chrids.length; ++i) {
                Start.unCheckList.add(Integer.parseInt(chrids[i]));
            }
        }
        catch (Exception e) {
            System.out.println("??????????????????" + e.getMessage());
        }
    }
    
    public static void ????????????(final int interval) {
        System.out.println("????????????");
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                System.out.println("??????????????????:" + unCheckList.toString());
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr != null && !unCheckList.contains(chr.getId())) {
                            if (chr.haveItem(2340000, 200) || chr.haveItem(2040805, 200)) {
                                System.out.println("[??????]?????????" + chr.getName() + "????????????????????????????????????");
                                unCheckList.add(chr.getId());
                                chr.ban("???????????????????????????", false, true, false);
                            }
                            else {
                                final Map<Integer, Integer> banitems = new HashMap<Integer, Integer>();
                                final OtherSettings item_id = new OtherSettings();
                                final String[] is??????_id;
                                final String[] itembp_id = is??????_id = item_id.is??????_id();
                                for (final String itembp_id2 : is??????_id) {
                                    banitems.put(Integer.parseInt(itembp_id2), 100);
                                    for (final Entry<Integer, Integer> entry : banitems.entrySet()) {
                                        if (chr.haveItem(entry.getKey(), entry.getValue())) {
                                            System.out.println("[????????????]?????? ???" + chr.getName() + "????????????????????????????????????");
                                            chr.ban("????????????????????????" + entry.getKey() + "??????" + entry.getValue() + "?????????", false, true, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 60000 * interval);
    }
    
    public static void ????????????(final int time) {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                int ppl = 0;
                try {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            ++ppl;
                            chr.saveToDB(false, false);
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }, 60000 * time);
    }
    
    protected static void checkSingleInstance() {
        try {
            Start.srvSocket = new ServerSocket(6350);
        }
        catch (IOException ex) {
            if (ex.getMessage().contains("?????????????????????:JVM_Bind")) {
                System.out.println("????????????????????????????????????????????????(Only one instance allowed)???");
            }
            System.exit(0);
        }
    }
    
    public static void ????????????(final int time) {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                try {
                    for (final ChannelServer chan : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            chr.gainGamePoints(1);
                            chr.expirationTask2(true);
                            if (chr.getGamePoints() >= 5) {
                                continue;
                            }
                            chr.resetGamePointsPD();
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }, 60000 * time);
    }
    
    public static void ????????????(final int time) {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        for (int i = 0; i < 6; ++i) {
                            int mapidA = 100000000 + (i + 1000000 - 2000000);
                            final MapleCharacter player = chr;
                            if (i == 6) {
                                mapidA = 910000000;
                            }
                            final int mapid = mapidA;
                            final MapleMap map = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                                final MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                                final MaplePortal newPor = newMap.getPortal(0);
                                final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters());
                                for (final MapleCharacter m : mcs) {
                                    int x = 0;
                                    while (x < 5) {
                                        try {
                                            m.changeMap(newMap, newPor);
                                        }
                                        catch (Throwable t) {
                                            ++x;
                                            continue;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 60000 * time);
    }
    
    public static void ????????????(final int time) {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                System.gc();
            }
        }, 60000 * time);
    }
    
    protected static void checkCopyItemFromSql(final int time) {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        final ArrayList<Integer> equipOnlyIds = new ArrayList<Integer>();
                        final Map checkItems = new HashMap();
                        try {
                            final Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE equipOnlyId > 0");
                            final ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                final int itemId = rs.getInt("itemId");
                                final int equipOnlyId = rs.getInt("equipOnlyId");
                                if (equipOnlyId > 0) {
                                    if (checkItems.containsKey(equipOnlyId)) {
                                        if ((int)checkItems.get(equipOnlyId) != itemId) {
                                            continue;
                                        }
                                        equipOnlyIds.add(equipOnlyId);
                                    }
                                    else {
                                        checkItems.put(equipOnlyId, itemId);
                                    }
                                }
                            }
                            rs.close();
                            ps.close();
                            Collections.sort(equipOnlyIds);
                            for (final int i : equipOnlyIds) {
                                ps = con.prepareStatement("DELETE FROM inventoryitems WHERE equipOnlyId = ?");
                                ps.setInt(1, i);
                                ps.executeUpdate();
                                ps.close();
                                System.out.println("??????????????????????????? ,??????????????????ID: " + i + " ?????????????????????..\r\n");
                                FileoutputUtil.logToFile("??????/Logs/????????????.log", "\r\n " + FileoutputUtil.NowTime() + "?????????????????? ??????????????????ID: " + i + "");
                            }
                        }
                        catch (SQLException ex) {
                            System.out.println("[EXCEPTION] ??????????????????????????????." + ex);
                        }
                    }
                }
            }
        }, 60000 * time);
    }
    
    static {
        Start.Check = true;
        Start.instance = new Start();
        Start.maxUsers = 0;
        Start.srvSocket = null;
        Start.unCheckList = new ArrayList<Integer>();
    }
    
    public static class Shutdown implements Runnable
    {
        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
