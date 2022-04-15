package server.life;

import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DatabaseConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapleMonsterInformationProvider
{
    private static final MapleMonsterInformationProvider instance;
    private final Map<Integer, List<MonsterDropEntry>> drops;
    private final List<MonsterGlobalDropEntry> globaldrops;
    private Map<Integer, List<DropEntry>> drops1;
    
    protected MapleMonsterInformationProvider() {
        this.drops = new HashMap<Integer, List<MonsterDropEntry>>();
        this.globaldrops = new ArrayList<MonsterGlobalDropEntry>();
        this.drops1 = new ConcurrentHashMap<Integer, List<DropEntry>>();
        this.retrieveGlobal();
    }
    
    public static final MapleMonsterInformationProvider getInstance() {
        return MapleMonsterInformationProvider.instance;
    }
    
    public final List<MonsterGlobalDropEntry> getGlobalDrop() {
        return this.globaldrops;
    }
    
    public final void retrieveGlobal() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0");
            rs = ps.executeQuery();
            while (rs.next()) {
                this.globaldrops.add(new MonsterGlobalDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("continent"), rs.getByte("dropType"), rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getShort("questid")));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException ex) {}
        }
    }
    
    public final List<MonsterDropEntry> retrieveDrop(final int dropperid) {
        if (this.drops.containsKey(dropperid)) {
            return this.drops.get(dropperid);
        }
        final List<MonsterDropEntry> ret = new LinkedList<MonsterDropEntry>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterdrops WHERE monsterid = ?");
            ps.setInt(1, dropperid);
            rs = ps.executeQuery();
            while (rs.next()) {
                final int itemid = rs.getInt("itemid");
                int chance = rs.getInt("chance");
                if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
                    chance /= 3;
                }
                ret.add(new MonsterDropEntry(itemid, chance, rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getShort("questid")));
            }
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException ignore) {
                return ret;
            }
        }
        catch (SQLException e) {
            return ret;
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException ignore2) {
                return ret;
            }
        }
        this.drops.put(dropperid, ret);
        return ret;
    }
    
    public final void clearDrops() {
        this.drops.clear();
        this.globaldrops.clear();
        this.retrieveGlobal();
    }
    
    static {
        instance = new MapleMonsterInformationProvider();
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
}
