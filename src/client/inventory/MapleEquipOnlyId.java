package client.inventory;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DatabaseConnection;
import java.util.concurrent.atomic.AtomicInteger;

public class MapleEquipOnlyId
{
    private final AtomicInteger runningId;
    
    private MapleEquipOnlyId() {
        this.runningId = new AtomicInteger(0);
    }
    
    public static MapleEquipOnlyId getInstance() {
        return SingletonHolder.instance;
    }
    
    public int getNextEquipOnlyId() {
        if (this.runningId.get() <= 0) {
            this.runningId.set(this.initOnlyId());
        }
        else {
            this.runningId.set(this.runningId.get() + 1);
        }
        return this.runningId.get();
    }
    
    public int initOnlyId() {
        int ret = 0;
        try (final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT MAX(equipOnlyId) FROM inventoryitems WHERE equipOnlyId > 0");
             final ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                ret = rs.getInt(1) + 1;
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    private static class SingletonHolder
    {
        protected static final MapleEquipOnlyId instance;
        
        static {
            instance = new MapleEquipOnlyId();
        }
    }
}
