package client;

import java.util.HashMap;
import tools.Pair;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.DatabaseConnection;

public class MapleUnlimitSlots
{
    protected int character_id;
    
    MapleUnlimitSlots(final int character_id) {
        this.character_id = character_id;
    }
    
    public void addSlots() {
        this.addSlots(1);
    }
    
    public void addSlots(final int n) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("update characters set unlimit_slots = unlimit_slots + ? where id = ?");
            ps.setInt(1, n);
            ps.setInt(2, this.character_id);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setSlots(final int slots) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("update characters set unlimit_slots = ? where id = ?");
            ps.setInt(1, slots);
            ps.setInt(2, this.character_id);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getSlots() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("select unlimit_slots from characters where id = ?");
            ps.setInt(1, this.character_id);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int getUsedSlots() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("select count(1) as used_unlimit_slots from unlimit_slots_items where character_id = ?");
            ps.setInt(1, this.character_id);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public Map<Integer, Pair<Integer, Integer>> getItems() {
        final Map res = new HashMap();
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("select * from unlimit_slots_items where character_id = ?");
            ps.setInt(1, this.character_id);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("id");
                final int iid = rs.getInt("item_id");
                final int c = rs.getInt("count");
                res.put(id, new Pair<Integer, Integer>(iid, c));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (Map<Integer, Pair<Integer, Integer>>)res;
    }
    
    public void addItem(final int item_id, final int count) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("insert into unlimit_slots_items (character_id, item_id, count) VALUES (?,?,?)");
            ps.setInt(1, this.character_id);
            ps.setInt(2, item_id);
            ps.setInt(3, count);
            ps.execute();
            ps.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteItem(final int id) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("delete from unlimit_slots_items where id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Pair<Integer, Integer> getItemById(final int id) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ps = con.prepareStatement("select * from unlimit_slots_items where id = ?");
            ps.setInt(1, id);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final int cid = rs.getInt("character_id");
                final int iid = rs.getInt("item_id");
                final int c = rs.getInt("count");
                if (cid == this.character_id) {
                    return new Pair<Integer, Integer>(iid, c);
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(MapleUnlimitSlots.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
