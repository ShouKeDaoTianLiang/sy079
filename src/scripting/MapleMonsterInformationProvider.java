package scripting;

import org.slf4j.LoggerFactory;
import server.life.MapleMonster;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import server.life.MapleLifeFactory;
import server.MapleItemInformationProvider;
import database.DatabaseConnection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import java.util.List;
import java.util.Map;

public class MapleMonsterInformationProvider
{
    public static final int APPROX_FADE_DELAY = 90;
    private Map<Integer, List<DropEntry>> drops;
    private static final Logger log;
    
    public MapleMonsterInformationProvider() {
        this.drops = new ConcurrentHashMap<Integer, List<DropEntry>>();
    }
    
    public static MapleMonsterInformationProvider getInstance() {
        return InstanceHolder.instance;
    }
    
    public List<DropEntry> retrieveDropChances(final int monsterid) {
        if (this.drops.containsKey(monsterid)) {
            return this.drops.get(monsterid);
        }
        final List<DropEntry> ret = new LinkedList<DropEntry>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT itemid, chance, monsterid, questid FROM monsterdrops WHERE monsterid = ?");
            ps.setInt(1, monsterid);
            rs = ps.executeQuery();
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleMonster theMonster = null;
            while (rs.next()) {
                final int rowmonsterid = rs.getInt("monsterid");
                int chance = rs.getInt("chance");
                final int questid = rs.getInt("questid");
                final int itemid = rs.getInt("itemid");
                if (ii.getName(itemid) != null) {
                    if (rowmonsterid != monsterid && rowmonsterid != 0) {
                        if (theMonster == null) {
                            theMonster = MapleLifeFactory.getMonster(monsterid);
                        }
                        chance += theMonster.getLevel() * rowmonsterid;
                    }
                    ret.add(new DropEntry(itemid, chance, questid));
                }
            }
            this.drops.put(monsterid, ret);
        }
        catch (Exception e) {
            MapleMonsterInformationProvider.log.error("Error retrieving drop", (Throwable)e);
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex2);
            }
        }
        return ret;
    }
    
    public void clearDrops() {
        this.drops.clear();
    }
    
    static {
        log = LoggerFactory.getLogger(MapleMonsterInformationProvider.class);
    }
    
    public static class DropEntry
    {
        public int itemid;
        public int chance;
        public int questid;
        public int assignedRangeStart;
        public int assignedRangeLength;
        
        public DropEntry(final int itemid, final int chance, final int questid) {
            this.itemid = itemid;
            this.chance = chance;
            this.questid = questid;
        }
        
        public DropEntry(final int itemid, final int chance) {
            this.itemid = itemid;
            this.chance = chance;
            this.questid = 0;
        }
        
        @Override
        public String toString() {
            return this.itemid + " chance: " + this.chance;
        }
    }
    
    private static class InstanceHolder
    {
        public static final MapleMonsterInformationProvider instance;
        
        static {
            instance = new MapleMonsterInformationProvider();
        }
    }
}
