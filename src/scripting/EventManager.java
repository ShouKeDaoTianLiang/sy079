package scripting;

import server.events.MapleEvent;
import server.Randomizer;
import server.events.MapleEventType;
import tools.MaplePacketCreator;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import java.util.ArrayList;
import server.life.OverrideMonsterStats;
import server.maps.MapleMapFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.DatabaseConnection;
import server.maps.MapleMapObject;
import java.util.List;
import java.util.Iterator;
import server.maps.MapleMap;
import handling.world.MapleParty;
import client.MapleCharacter;
import server.MapleSquad;
import java.util.Collections;
import java.util.Collection;
import javax.script.ScriptException;
import server.Timer.EventTimer;
import java.util.concurrent.ScheduledFuture;
import tools.FileoutputUtil;
import java.util.WeakHashMap;
import handling.channel.ChannelServer;
import java.util.Properties;
import java.util.Map;
import javax.script.Invocable;

public class EventManager
{
    private static final int[] eventChannel;
    private final Invocable iv;
    private final int channel;
    private final Map<String, EventInstanceManager> instances;
    private final Properties props;
    private final String name;
    
    public EventManager(final ChannelServer cserv, final Invocable iv, final String name) {
        this.instances = new WeakHashMap<String, EventInstanceManager>();
        this.props = new Properties();
        this.iv = iv;
        this.channel = cserv.getChannel();
        this.name = name;
    }
    
    public void cancel() {
        try {
            this.iv.invokeFunction("cancelSchedule", (Object) null);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : cancelSchedule:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : cancelSchedule:\n" + ex);
        }
    }
    
    public ScheduledFuture<?> schedule(final String methodName, final long delay) {
        return EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                }
                catch (Exception ex) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }
    
    public ScheduledFuture<?> schedule(final String methodName, final long delay, final EventInstanceManager eim) {
        return EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, eim);
                }
                catch (Exception ex) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }
    
    public ScheduledFuture<?> schedule(final String methodName, final EventInstanceManager eim, final long delay) {
        return EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, eim);
                }
                catch (Exception ex) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }
    
    public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, final long timestamp) {
        return EventTimer.getInstance().scheduleAtTimestamp(new Runnable() {
            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                }
                catch (ScriptException ex) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                }
                catch (NoSuchMethodException ex2) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex2);
                }
            }
        }, timestamp);
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }
    
    public EventInstanceManager getInstance(final String name) {
        return this.instances.get(name);
    }
    
    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(this.instances.values());
    }
    
    public EventInstanceManager newInstance(final String name) {
        final EventInstanceManager ret = new EventInstanceManager(this, name, this.channel);
        this.instances.put(name, ret);
        return ret;
    }
    
    public void disposeInstance(final String name) {
        this.instances.remove(name);
        if (this.getProperty("state") != null && this.instances.isEmpty()) {
            this.setProperty("state", "0");
        }
        if (this.getProperty("leader") != null && this.instances.isEmpty() && this.getProperty("leader").equals("false")) {
            this.setProperty("leader", "true");
        }
        if (this.name.equals("CWKPQ")) {
            final MapleSquad squad = ChannelServer.getInstance(this.channel).getMapleSquad("CWKPQ");
            if (squad != null) {
                squad.clear();
            }
        }
    }
    
    public Invocable getIv() {
        return this.iv;
    }
    
    public void setProperty(final String key, final String value) {
        this.props.setProperty(key, value);
    }
    
    public String getProperty(final String key) {
        return this.props.getProperty(key);
    }
    
    public final Properties getProperties() {
        return this.props;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void startInstance() {
        try {
            this.iv.invokeFunction("setup", (Object) null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }
    
    public void startInstance(final String mapid, final MapleCharacter chr) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
            eim.registerCarnivalParty(chr, chr.getMap(), (byte)0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }
    
    public void startInstance_Party(final String mapid, final MapleCharacter chr) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
            eim.registerParty(chr.getParty(), chr.getMap());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }
    
    public void startInstance(final MapleCharacter character, final String leader) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", (Object) null);
            eim.registerPlayer(character);
            eim.setProperty("leader", leader);
            eim.setProperty("guildid", String.valueOf(character.getGuildId()));
            this.setProperty("guildid", String.valueOf(character.getGuildId()));
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-Guild:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-Guild:\n" + ex);
        }
    }
    
    public void startInstance_CharID(final MapleCharacter character) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", character.getId());
            eim.registerPlayer(character);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-CharID:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-CharID:\n" + ex);
        }
    }
    
    public void startInstance(final MapleCharacter character) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", (Object) null);
            eim.registerPlayer(character);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-character:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-character:\n" + ex);
        }
    }
    
    public void startInstance(final MapleParty party, final MapleMap map) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", party.getId());
            eim.registerParty(party, map);
        }
        catch (ScriptException ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-partyid:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-partyid:\n" + ex);
        }
        catch (Exception ex2) {
            this.startInstance_NoID(party, map, ex2);
        }
    }
    
    public void startInstance_NoID(final MapleParty party, final MapleMap map) {
        this.startInstance_NoID(party, map, null);
    }
    
    public void startInstance_NoID(final MapleParty party, final MapleMap map, final Exception old) {
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", (Object) null);
            eim.registerParty(party, map);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-party:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-party:\n" + ex + "\n" + ((old == null) ? "no old exception" : old));
        }
    }
    
    public void startInstance(final EventInstanceManager eim, final String leader) {
        try {
            this.iv.invokeFunction("setup", eim);
            eim.setProperty("leader", leader);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-leader:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-leader:\n" + ex);
        }
    }
    
    public void startInstance(final MapleSquad squad, final MapleMap map) {
        this.startInstance(squad, map, -1);
    }
    
    public void startInstance(final MapleSquad squad, final MapleMap map, final int questID) {
        if (squad.getStatus() == 0) {
            return;
        }
        if (!squad.getLeader().isGM()) {
            if (squad.getMembers().size() < squad.getType().i) {
                squad.getLeader().dropMessage(5, "这个远征队至少要有 " + squad.getType().i + " 人以上才可以开战.");
                return;
            }
            if (this.name.equals("CWKPQ") && squad.getJobs().size() < 5) {
                squad.getLeader().dropMessage(5, "The squad requires members from every type of job.");
                return;
            }
        }
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", squad.getLeaderName());
            eim.registerSquad(squad, map, questID);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
            FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
        }
    }
    
    public void startInstance(final MapleSquad squad, final MapleMap map, final String bossid) {
        if (squad.getStatus() == 0) {
            return;
        }
        if (!squad.getLeader().isGM()) {
            final int mapid = map.getId();
            int chrSize = 0;
            for (final String chr : squad.getMembers()) {
                final MapleCharacter player = squad.getChar(chr);
                if (player != null && player.getMapId() == mapid) {
                    ++chrSize;
                }
            }
            if (chrSize < squad.getType().i) {
                squad.getLeader().dropMessage(5, "远征队中人员少于 " + squad.getType().i + " 人，无法开始远征任务。注意必须队伍中的角色在线且在同一地图。当前人数: " + chrSize);
                return;
            }
            if (this.name.equals("CWKPQ") && squad.getJobs().size() < 5) {
                squad.getLeader().dropMessage(5, "远征队中成员职业的类型小于5种，无法开始远征任务。");
                return;
            }
        }
        try {
            final EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", squad.getLeaderName());
            eim.registerSquad(squad, map, Integer.parseInt(bossid));
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
            FileoutputUtil.log("log\\Script_Except.log", "Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
        }
    }
    
    public void warpAllPlayer(final int from, final int to) {
        final MapleMap tomap = this.getMapFactory().getMap(to);
        final MapleMap frommap = this.getMapFactory().getMap(from);
        final List<MapleCharacter> list = frommap.getCharactersThreadsafe();
        if (tomap != null && frommap != null && list != null && frommap.getCharactersSize() > 0) {
            for (final MapleMapObject mmo : list) {
                ((MapleCharacter)mmo).changeMap(tomap, tomap.getPortal(0));
            }
        }
    }
    
    public int online() {
        final Connection con = DatabaseConnection.getConnection();
        int count = 0;
        try {
            final PreparedStatement ps = con.prepareStatement("SELECT count(*) as cc FROM accounts WHERE loggedin = 2");
            final ResultSet re = ps.executeQuery();
            while (re.next()) {
                count = re.getInt("cc");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(EventInstanceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    public MapleMapFactory getMapFactory() {
        return this.getChannelServer().getMapFactory();
    }
    
    public OverrideMonsterStats newMonsterStats() {
        return new OverrideMonsterStats();
    }
    
    public List<MapleCharacter> newCharList() {
        return new ArrayList<MapleCharacter>();
    }
    
    public MapleMonster getMonster(final int id) {
        return MapleLifeFactory.getMonster(id);
    }
    
    public void broadcastShip(final int mapid, final int effect) {
        this.getMapFactory().getMap(mapid).broadcastMessage(MaplePacketCreator.boatPacket(effect));
    }
    
    public void sendMonsterBoat(final boolean isEnter) {
        MaplePacketCreator.MonsterBoat(isEnter);
    }
    
    public void broadcastChangeMusic(final int mapid) {
        this.getMapFactory().getMap(mapid).broadcastMessage(MaplePacketCreator.musicChange("Bgm04/ArabPirate"));
    }
    
    public void broadcastYellowMsg(final String msg) {
        this.getChannelServer().broadcastPacket(MaplePacketCreator.yellowChat(msg));
    }
    
    public void broadcastServerMsg(final int type, final String msg, final boolean weather) {
        if (!weather) {
            this.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(type, msg));
        }
        else {
            for (final MapleMap load : this.getMapFactory().getAllMaps()) {
                if (load.getCharactersSize() > 0) {
                    load.startMapEffect(msg, type);
                }
            }
        }
    }
    
    public boolean scheduleRandomEvent() {
        boolean omg = false;
        for (int i = 0; i < EventManager.eventChannel.length; ++i) {
            omg |= this.scheduleRandomEventInChannel(EventManager.eventChannel[i]);
        }
        return omg;
    }
    
    public boolean scheduleRandomEventInChannel(final int chz) {
        final ChannelServer cs = ChannelServer.getInstance(chz);
        if (cs == null || cs.getEvent() > -1) {
            return false;
        }
        MapleEventType t;
        MapleEventType x = null;
        for (t = null; t == null; t = x) {
            final MapleEventType[] values = MapleEventType.values();
            for (int length = values.length, i = 0; i < length; ++i) {
                x = values[i];
                if (Randomizer.nextInt(MapleEventType.values().length) == 0) {
                    break;
                }
            }
        }
        final String msg = MapleEvent.scheduleEvent(t, cs);
        if (msg.length() > 0) {
            this.broadcastYellowMsg(msg);
            return false;
        }
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (cs.getEvent() >= 0) {
                    MapleEvent.setEvent(cs, true);
                }
            }
        }, 180000L);
        return true;
    }
    
    public void setWorldEvent() {
        for (int i = 0; i < EventManager.eventChannel.length; ++i) {
            EventManager.eventChannel[i] = Randomizer.nextInt(ChannelServer.getAllInstances().size()) + i;
        }
    }
    
    static {
        eventChannel = new int[2];
    }
}
