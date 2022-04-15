package server.maps;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import database.DatabaseConnection;
import server.Randomizer;
import server.life.MapleLifeFactory;
import java.awt.Point;
import server.Timer.EventTimer;
import tools.MaplePacketCreator;
import server.ServerProperties;
import java.util.Iterator;
import handling.channel.ChannelServer;
import client.MapleCharacter;
import org.slf4j.Logger;

public class AramiaFireWorks1
{
    private static Logger log;
    public static final int KEG_ID = 4031875;
    public static final int SUN_ID = 4001246;
    public static final int DEC_ID = 4001473;
    public static final int MAX_KEGS = 10000;
    public static final int MAX_SUN = 14000;
    public static final int MAX_DEC = 18000;
    private static final AramiaFireWorks1 instance;
    private static final int[] arrayMob;
    private static final int[] arrayX;
    private static final int[] arrayY;
    private static final int[] array_X;
    private static final int[] array_Y;
    private static final int flake_Y = 149;
    private short kegss;
    private short kegs;
    private short sunshines;
    private short decorations;
    
    public AramiaFireWorks1() {
        this.kegss = 0;
        this.kegs = 0;
        this.sunshines = 2333;
        this.decorations = 3000;
    }
    
    public static AramiaFireWorks1 getInstance() {
        return AramiaFireWorks1.instance;
    }
    
    public void giveKegss(final MapleCharacter c, int kegs) {
        this.kegs += (short)kegs;
        if (this.kegs >= 10000) {
            kegs = 0;
            this.broadcastEvent(c);
        }
    }
    
    public void giveKegs(final MapleCharacter c, final int kegs, final int i) {
        this.gainAramiaFireWorks(kegs, i);
        if (this.getAramiaFireWorks(i) >= this.getAramiaFireWorkssl(i)) {
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mapleCharacter : cserv1.getPlayerStorage().getAllCharacters()) {}
            }
        }
    }
    
    public void give全服奖励(final MapleCharacter c, final int kegs, final int i) {
        this.gainAramiaFireWorks(kegs, i);
        final int 抵用点卷 = Integer.parseInt(ServerProperties.getProperty("channel.抵用卷"));
        final int 金币 = Integer.parseInt(ServerProperties.getProperty("channel.金币"));
        final int 经验 = Integer.parseInt(ServerProperties.getProperty("channel.经验"));
        final int 道具 = Integer.parseInt(ServerProperties.getProperty("channel.道具"));
        final int 道具数量 = Integer.parseInt(ServerProperties.getProperty("channel.道具数量"));
        if (this.getAramiaFireWorks(i) >= this.getAramiaFireWorkssl(i)) {
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.modifyCSPoints(2, 抵用点卷);
                    mch.gainMeso(金币, true);
                    mch.gainExp(经验, true, true, true);
                    mch.gainIten(道具, 道具数量);
                    mch.startMapEffect("" + ServerProperties.getProperty("channel.ServerMessage") + "", 5121013);
                }
            }
            c.getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "" + ServerProperties.getProperty("channel.ServerMessage") + ""));
        }
    }
    
    public void give召唤怪物(final MapleCharacter c, final int kegs, final int i) {
        this.gainAramiaFireWorks(kegs, i);
        if (this.getAramiaFireWorks(i) >= this.getAramiaFireWorkssl(i)) {
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect("勇士们！<" + c.getName() + ">玩家成功解开时间裂缝的封印怪物即将开始攻城", 5121013);
                }
            }
            c.getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "勇士们！<" + c.getName() + ">玩家成功解开时间裂缝的封印怪物即将开始攻城"));
            this.broadcastEvent(c);
        }
    }
    
    public void give冲级奖励(final MapleCharacter c, final int kegs, final int i) {
        this.gainAramiaFireWorks(kegs, i);
        if (this.getAramiaFireWorks(i) >= this.getAramiaFireWorkssl(i)) {
            this.broadcastEvent(c);
        }
    }
    
    public void giveKegsCall(final int i) {
        this.CleanAramiaFireWorks(i);
    }
    
    private void broadcastServer(final MapleCharacter c, final int itemid) {
        c.getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "勇士们！<" + c.getName() + ">玩家已将最后一块记忆拼图交与#XX#NPC，该地区已解除封印2"));
    }
    
    public short getKegsPercentages() {
        return (short)(this.kegs / 10000 * 10000);
    }
    
    public long getKegsPercentage(final int i) {
        return 100 * this.getAramiaFireWorks(i) / this.getAramiaFireWorkssl(i);
    }
    
    public long getKegs(final int i) {
        return this.getAramiaFireWorks(i);
    }
    
    private final void broadcastEvent(final MapleCharacter c) {
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public final void run() {
                AramiaFireWorks1.this.startEvent(c.getClient().getChannelServer().getMapFactory().getMap(910000000));
            }
        }, 10000L);
    }
    
    private final void startEvent(final MapleMap map) {
        map.startMapEffect("战斗已经开始,请大家做好准备", 5120000);
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public final void run() {
                AramiaFireWorks1.this.spawnMonster(map);
            }
        }, 5000L);
    }
    
    private final void spawnMonster(final MapleMap map) {
        for (int i = 0; i < AramiaFireWorks1.arrayMob.length; ++i) {
            final Point pos = new Point(AramiaFireWorks1.arrayX[i], AramiaFireWorks1.arrayY[i]);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400708), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400014), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9600025), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8220005), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8220006), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9600065), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9600063), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8150000), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400014), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9600025), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8220005), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8220006), pos);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9600065), pos);
        }
    }
    
    public void giveSuns(final MapleCharacter c, final int kegs) {
        this.sunshines += (short)kegs;
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        final MapleReactor reactor = map.getReactorByName("XmasTree");
        for (int gogo = kegs + 2333; gogo > 0; gogo -= 2333) {
            switch (reactor.getState()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4: {
                    if (this.sunshines >= 2333 * (2 + reactor.getState())) {
                        reactor.setState((byte)(reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(MaplePacketCreator.triggerReactor(reactor, reactor.getState()));
                        break;
                    }
                    break;
                }
                default: {
                    if (this.sunshines >= 2333) {
                        map.resetReactors();
                        break;
                    }
                    break;
                }
            }
        }
        if (this.sunshines >= 14000) {
            this.sunshines = 0;
            this.broadcastSun(c);
        }
    }
    
    public short getSunsPercentage() {
        return (short)(this.sunshines / 14000 * 10000);
    }
    
    private void broadcastSun(final MapleCharacter c) {
        this.broadcastServer(c, 4001246);
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                AramiaFireWorks1.this.startSun(c.getClient().getChannelServer().getMapFactory().getMap(970010000));
            }
        }, 10000L);
    }
    
    private void startSun(final MapleMap map) {
        map.startMapEffect("The tree is bursting with sunshine!", 5121010);
        for (int i = 0; i < 3; ++i) {
            EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    AramiaFireWorks1.this.spawnItem(map);
                }
            }, 5000 + i * 10000);
        }
    }
    
    private void spawnItem(final MapleMap map) {
        for (int i = 0; i < Randomizer.nextInt(5) + 10; ++i) {
            final Point pos = new Point(AramiaFireWorks1.array_X[i], AramiaFireWorks1.array_Y[i]);
        }
    }
    
    public void giveDecs(final MapleCharacter c, final int kegs) {
        this.decorations += (short)kegs;
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        final MapleReactor reactor = map.getReactorByName("XmasTree");
        for (int gogo = kegs + 3000; gogo > 0; gogo -= 3000) {
            switch (reactor.getState()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4: {
                    if (this.decorations >= 3000 * (2 + reactor.getState())) {
                        reactor.setState((byte)(reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(MaplePacketCreator.triggerReactor(reactor, reactor.getState()));
                        break;
                    }
                    break;
                }
                default: {
                    if (this.decorations >= 3000) {
                        map.resetReactors();
                        break;
                    }
                    break;
                }
            }
        }
        if (this.decorations >= 18000) {
            this.decorations = 0;
            this.broadcastDec(c);
        }
    }
    
    public short getDecsPercentage() {
        return (short)(this.decorations / 18000 * 10000);
    }
    
    private void broadcastDec(final MapleCharacter c) {
        this.broadcastServer(c, 4001473);
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                AramiaFireWorks1.this.startDec(c.getClient().getChannelServer().getMapFactory().getMap(555000000));
            }
        }, 10000L);
    }
    
    private void startDec(final MapleMap map) {
        map.startMapEffect("The tree is bursting with snow!", 5120000);
        for (int i = 0; i < 3; ++i) {
            EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    AramiaFireWorks1.this.spawnDec(map);
                }
            }, 5000 + i * 10000);
        }
    }
    
    private void spawnDec(final MapleMap map) {
        for (int i = 0; i < Randomizer.nextInt(10) + 40; ++i) {
            final Point pos = new Point(Randomizer.nextInt(800) - 400, 149);
        }
    }
    
    public int getAramiaFireWorkssl(final int type) {
        int pay = 0;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("select * from AramiaFireWorks where accname = ?");
            ps.setInt(1, type);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pay = rs.getInt("sl");
            }
            ps.close();
            rs.close();
            return pay;
        }
        catch (SQLException ex) {
            System.err.println("获取AramiaFireWorks信息发生错误: " + ex);
            return pay;
        }
    }
    
    public int getAramiaFireWorks(final int type) {
        int pay = 0;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("select * from AramiaFireWorks where accname = ?");
            ps.setInt(1, type);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pay = rs.getInt("jf");
            }
            ps.close();
            rs.close();
            return pay;
        }
        catch (SQLException ex) {
            System.err.println("获取AramiaFireWorks信息发生错误: " + ex);
            return pay;
        }
    }
    
    public void gainAramiaFireWorks(final int hypay, final int i) {
        final int pay = this.getAramiaFireWorks(i);
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE AramiaFireWorks SET jf = ? where accname = ?");
            ps.setInt(1, pay + hypay);
            ps.setInt(2, i);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            AramiaFireWorks1.log.error("加减AramiaFireWorks信息发生错误A: " + ex);
        }
    }
    
    public void CleanAramiaFireWorks(final int i) {
        PreparedStatement ps = null;
        Connection con = null;
        final ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE AramiaFireWorks SET jf = ? where accname = ?");
            ps.setInt(1, 0);
            ps.setInt(2, i);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            AramiaFireWorks1.log.error("加减AramiaFireWorks信息发生错误B", ex);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (SQLException ex2) {
                AramiaFireWorks1.log.error("SQL:", ex2);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (SQLException ex2) {
                AramiaFireWorks1.log.error("SQL:", ex2);
            }
        }
    }
    
    static {
        AramiaFireWorks1.log = LoggerFactory.getLogger(AramiaFireWorks1.class);
        instance = new AramiaFireWorks1();
        arrayMob = new int[] { 9400708, 8150000, 9400014, 9600025, 8220005, 8220006, 9600065, 9600063 };
        arrayX = new int[] { 100 };
        arrayY = new int[] { 34 };
        array_X = new int[] { 720, 180, 630, 270, 360, 540, 450, 142, 142, 218, 772, 810, 848, 232, 308, 142 };
        array_Y = new int[] { 1234, 1234, 1174, 1234, 1174, 1174, 1174, 1260, 1234, 1234, 1234, 1234, 1234, 1114, 1114, 1140 };
    }
}
