package scripting;

import org.slf4j.LoggerFactory;
import server.life.MapleLifeFactory;
import server.MapleItemInformationProvider;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import database.DatabaseConnection;
import client.MapleCharacter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.apache.mina.core.session.IoSession;
import java.util.Set;
import constants.MapleItemType;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class ServerManager
{
    private static Logger log;
    private int consignmentItemIdNext;
    private boolean startLog;
    private long last_write_log_file;
    private Map<String, List<String>> log_file_context_map;
    private static SimpleDateFormat simpleDateFormat;
    private long systemTimeFormatLong;
    private long lastUpdateSystemTimeFormatLong;
    private Map<Integer, Map<String, Map<Integer, Integer>>> bosslogMap;
    private Map<String, String> serverlog;
    private Map<MapleItemType, Map<Integer, Set<Integer>>> dropsFromMonsters;
    private Map<Integer, IoSession> leaveShopSessions;
    private Map<Integer, Integer> accountInSave;
    
    public ServerManager() {
        this.consignmentItemIdNext = 0;
        this.startLog = false;
        this.last_write_log_file = 0L;
        this.log_file_context_map = new HashMap<String, List<String>>();
        this.systemTimeFormatLong = -1L;
        this.lastUpdateSystemTimeFormatLong = -1L;
        this.bosslogMap = new ConcurrentHashMap<Integer, Map<String, Map<Integer, Integer>>>();
        this.serverlog = new HashMap<String, String>();
        this.dropsFromMonsters = new HashMap<MapleItemType, Map<Integer, Set<Integer>>>();
        this.leaveShopSessions = new ConcurrentHashMap<Integer, IoSession>();
        this.accountInSave = new ConcurrentHashMap<Integer, Integer>();
    }
    
    public static ServerManager getInstance() {
        return InstanceHolder.instance;
    }
    
    public synchronized int getConsignmentItemIdNext() {
        return this.consignmentItemIdNext++;
    }
    
    public void setConsignmentItemIdNext(final int consignmentItemIdNext) {
        this.consignmentItemIdNext = consignmentItemIdNext;
    }
    
    public boolean isStartLog() {
        return this.startLog;
    }
    
    public void setStartLog(final boolean startLog) {
        this.startLog = startLog;
    }
    
    public void writeLog(final String filename, final String context) {
        if (System.currentTimeMillis() - this.last_write_log_file > 600000L) {
            final Map log_file_context_map_temp = this.log_file_context_map;
            this.log_file_context_map = new HashMap<String, List<String>>();
            this.last_write_log_file = System.currentTimeMillis();
            this.writeLogToFile_Map(log_file_context_map_temp);
        }
        if (!this.log_file_context_map.containsKey(filename)) {
            this.log_file_context_map.put(filename, new ArrayList<String>());
        }
        (this.log_file_context_map.get(filename)).add(context);
    }
    
    public void writeLogToFile_Map(final Map<String, List<String>> log_file_context_map) {
        if (log_file_context_map == null || log_file_context_map.isEmpty()) {
            return;
        }
        for (final Entry<String, List<String>> entry : log_file_context_map.entrySet()) {
            final String fileName = entry.getKey();
            final List context_list = entry.getValue();
            if (context_list != null && !context_list.isEmpty()) {
                String context = "";
                for (final Object txt : context_list) {
                    context = context + txt + "\r\n";
                }
                this.writeLogToFile(fileName, context);
            }
        }
    }
    
    public void writeLogToFile(final String filename, final String context) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filename, "rw");
            final int num = (int)file.length();
            file.seek(num);
            file.write(context.getBytes());
            file.close();
            ServerManager.log.info("writelog[" + filename + "]:" + context);
        }
        catch (IOException ex) {
            ServerManager.log.error("ex:", (Throwable)ex);
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public void chatLog(final MapleCharacter player, final String text, final int type) {
        if (!this.startLog) {
            return;
        }
        String txtfile = "chatLog.txt";
        if (type == 1) {
            txtfile = "general.txt";
        }
        else if (type == 2) {
            txtfile = "friend.txt";
        }
        else if (type == 3) {
            txtfile = "party.txt";
        }
        else if (type == 4) {
            txtfile = "famliy.txt";
        }
        else if (type == 5) {
            txtfile = "whisper.txt";
        }
        else if (type == 6) {
            txtfile = "shopchat.txt";
        }
        this.writeLog(txtfile, player.getName() + "[" + ServerManager.simpleDateFormat.format(System.currentTimeMillis()) + "] " + text);
    }
    
    public void itemMoveLog(final MapleCharacter player, final String text, final int type) {
        if (!this.startLog) {
            return;
        }
        String txtfile = "itemMoveLog.txt";
        if (type == 2) {
            txtfile = "mapleShopLog.txt";
        }
        this.writeLog(txtfile, player.getName() + "[" + ServerManager.simpleDateFormat.format(System.currentTimeMillis()) + "] " + text);
    }
    
    public long getSystemTimeFormat(final String pattern) {
        final long now = System.currentTimeMillis();
        if (now - this.lastUpdateSystemTimeFormatLong > 60000L) {
            this.lastUpdateSystemTimeFormatLong = now;
            ServerManager.simpleDateFormat.applyPattern(pattern);
            this.systemTimeFormatLong = Long.parseLong(ServerManager.simpleDateFormat.format(System.currentTimeMillis()));
        }
        return this.systemTimeFormatLong;
    }
    
    public Map<Integer, Map<String, Map<Integer, Integer>>> getBosslogMap() {
        return this.bosslogMap;
    }
    
    public void setBosslogMap(final Map<Integer, Map<String, Map<Integer, Integer>>> bosslogMap) {
        this.bosslogMap = bosslogMap;
    }
    
    public Map<String, String> getServerlog() {
        return this.serverlog;
    }
    
    public void setServerlog(final Map<String, String> serverlog) {
        this.serverlog = serverlog;
    }
    
    public static Logger getLog() {
        return ServerManager.log;
    }
    
    public static void setLog(Logger log) {
        log = log;
    }
    
    public static SimpleDateFormat getSimpleDateFormat() {
        return ServerManager.simpleDateFormat;
    }
    
    public static void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        simpleDateFormat = simpleDateFormat;
    }
    
    public long getSystemTimeFormatLong() {
        return this.systemTimeFormatLong;
    }
    
    public void setSystemTimeFormatLong(final long systemTimeFormatLong) {
        this.systemTimeFormatLong = systemTimeFormatLong;
    }
    
    public long getLastUpdateSystemTimeFormatLong() {
        return this.lastUpdateSystemTimeFormatLong;
    }
    
    public void setLastUpdateSystemTimeFormatLong(final long lastUpdateSystemTimeFormatLong) {
        this.lastUpdateSystemTimeFormatLong = lastUpdateSystemTimeFormatLong;
    }
    
    public void loadDropsFromMonstersFromDB() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * from monsterdrops");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int itemid = rs.getInt("itemid");
                final int monsterid = rs.getInt("monsterid");
                final MapleItemType itemType = MapleItemType.getItemTypeByItemId(itemid);
                if (itemType != null) {
                    if (this.dropsFromMonsters.get(itemType) == null) {
                        this.dropsFromMonsters.put(itemType, new HashMap<Integer, Set<Integer>>());
                    }
                    if ((this.dropsFromMonsters.get(itemType)).get(itemid) == null) {
                        (this.dropsFromMonsters.get(itemType)).put(itemid, new HashSet<Integer>());
                    }
                    (this.dropsFromMonsters.get(itemType)).get(itemid).add(monsterid);
                }
            }
            rs.close();
            ps.close();
        }
        catch (Exception Ex) {
            ServerManager.log.error("Error while loadDropsFromMonstersFromDB.", (Throwable)Ex);
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
            catch (SQLException ex) {
                ServerManager.log.error("Error while loadDropsFromMonstersFromDB finally close.", (Throwable)ex);
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
                ServerManager.log.error("Error while loadDropsFromMonstersFromDB finally close.", (Throwable)ex2);
            }
        }
    }
    
    public void loadDropsFromMonsters() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        getInstance().loadDropsFromMonstersFromDB();
        final Map tempMap = new HashMap();
        for (final Entry<MapleItemType, Map<Integer, Set<Integer>>> entry : this.dropsFromMonsters.entrySet()) {
            final MapleItemType itemType = entry.getKey();
            if (itemType != null) {
                for (final Entry<Integer, Set<Integer>> entry2 : entry.getValue().entrySet()) {
                    final int itemid = entry2.getKey();
                    if (ii.getName(itemid) != null) {
                        for (final Integer monsterid : entry2.getValue()) {
                            if (MapleLifeFactory.getMonster(monsterid) != null) {
                                if (tempMap.get(itemType) == null) {
                                    tempMap.put(itemType, new HashMap());
                                }
                                if (((java.util.Map)tempMap.get(itemType)).get(itemid) == null) {
                                    ((java.util.Map)tempMap.get(itemType)).put(itemid, new HashSet());
                                }
                                ((HashSet)((java.util.Map)tempMap.get(itemType)).get(itemid)).add(monsterid);
                            }
                        }
                    }
                }
            }
        }
        this.dropsFromMonsters.clear();
        this.dropsFromMonsters = (Map<MapleItemType, Map<Integer, Set<Integer>>>)tempMap;
    }
    
    public Map<MapleItemType, Map<Integer, Set<Integer>>> getDropsFromMonsters() {
        if (this.dropsFromMonsters == null || this.dropsFromMonsters.size() < 1) {
            this.loadDropsFromMonsters();
        }
        return this.dropsFromMonsters;
    }
    
    public void setDropsFromMonsters(final Map<MapleItemType, Map<Integer, Set<Integer>>> dropsFromMonsters) {
        this.dropsFromMonsters = dropsFromMonsters;
    }
    
    public Map<Integer, IoSession> getLeaveShopSessions() {
        return this.leaveShopSessions;
    }
    
    public void setLeaveShopSessions(final Map<Integer, IoSession> leaveShopSessions) {
        this.leaveShopSessions = leaveShopSessions;
    }
    
    public void addLeaveSession(final int charId, final IoSession session) {
        this.closeLeaveSession(charId);
        this.leaveShopSessions.put(charId, session);
    }
    
    public void removeLeaveSession(final int charId) {
        this.closeLeaveSession(charId);
        this.leaveShopSessions.remove(charId);
    }
    
    private void closeLeaveSession(final int charId) {
        if (this.leaveShopSessions.containsKey(charId) && this.leaveShopSessions.get(charId) != null) {
            (this.leaveShopSessions.get(charId)).close(true);
        }
    }
    
    public Map<Integer, Integer> getAccountInSave() {
        return this.accountInSave;
    }
    
    public void setAccountInSave(final Map<Integer, Integer> accountInSave) {
        this.accountInSave = accountInSave;
    }
    
    static {
        ServerManager.log = LoggerFactory.getLogger(ServerManager.class);
        ServerManager.simpleDateFormat = new SimpleDateFormat();
    }
    
    private static class InstanceHolder
    {
        public static final ServerManager instance;
        
        static {
            instance = new ServerManager();
        }
    }
}
