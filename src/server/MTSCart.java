package server;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import database.DatabaseConnection;
import constants.GameConstants;
import java.sql.SQLException;
import java.util.Iterator;
import client.inventory.MapleInventoryType;
import tools.Pair;
import client.inventory.ItemLoader;
import java.util.ArrayList;
import client.inventory.Item;
import java.util.List;
import java.io.Serializable;

public final class MTSCart implements Serializable
{
    private static final long serialVersionUID = 231541893513373578L;
    private int characterId;
    private int tab;
    private int type;
    private int page;
    private final List<Item> transfer;
    private final List<Integer> cart;
    private final List<Integer> notYetSold;
    private int owedNX;
    
    public MTSCart(final int characterId) throws SQLException {
        this.tab = 1;
        this.type = 0;
        this.page = 0;
        this.transfer = new ArrayList<Item>();
        this.cart = new ArrayList<Integer>();
        this.notYetSold = new ArrayList<Integer>(10);
        this.owedNX = 0;
        this.characterId = characterId;
        for (final Pair<Item, MapleInventoryType> item : ItemLoader.MTS_TRANSFER.loadItems(false, characterId).values()) {
            this.transfer.add(item.getLeft());
        }
        this.loadCart();
        this.loadNotYetSold();
    }
    
    public List<Item> getInventory() {
        return this.transfer;
    }
    
    public void addToInventory(final Item item) {
        this.transfer.add(item);
    }
    
    public void removeFromInventory(final Item item) {
        this.transfer.remove(item);
    }
    
    public List<Integer> getCart() {
        return this.cart;
    }
    
    public boolean addToCart(final int car) {
        if (!this.cart.contains(car)) {
            this.cart.add(car);
            return true;
        }
        return false;
    }
    
    public void removeFromCart(final int car) {
        for (int i = 0; i < this.cart.size(); ++i) {
            if (this.cart.get(i) == car) {
                this.cart.remove(i);
            }
        }
    }
    
    public List<Integer> getNotYetSold() {
        return this.notYetSold;
    }
    
    public void addToNotYetSold(final int car) {
        this.notYetSold.add(car);
    }
    
    public void removeFromNotYetSold(final int car) {
        for (int i = 0; i < this.notYetSold.size(); ++i) {
            if (this.notYetSold.get(i) == car) {
                this.notYetSold.remove(i);
            }
        }
    }
    
    public final int getSetOwedNX() {
        final int on = this.owedNX;
        this.owedNX = 0;
        return on;
    }
    
    public void increaseOwedNX(final int newNX) {
        this.owedNX += newNX;
    }
    
    public void save() throws SQLException {
        final List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
        for (final Item item : this.getInventory()) {
            itemsWithType.add(new Pair<Item, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        ItemLoader.MTS_TRANSFER.saveItems(itemsWithType, this.characterId);
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM mts_cart WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        ps.execute();
        ps.close();
        ps = con.prepareStatement("INSERT INTO mts_cart VALUES(DEFAULT, ?, ?)");
        ps.setInt(1, this.characterId);
        for (final int i : this.cart) {
            ps.setInt(2, i);
            ps.executeUpdate();
        }
        if (this.owedNX > 0) {
            ps.setInt(2, -this.owedNX);
            ps.executeUpdate();
        }
        ps.close();
    }
    
    public void loadCart() throws SQLException {
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM mts_cart WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final int iId = rs.getInt("itemid");
            if (iId < 0) {
                this.owedNX -= iId;
            }
            else {
                if (!MTSStorage.getInstance().check(iId)) {
                    continue;
                }
                this.cart.add(iId);
            }
        }
        rs.close();
        ps.close();
    }
    
    public void loadNotYetSold() throws SQLException {
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM mts_items WHERE characterid = ?");
        ps.setInt(1, this.characterId);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final int pId = rs.getInt("id");
            if (MTSStorage.getInstance().check(pId)) {
                this.notYetSold.add(pId);
            }
        }
        rs.close();
        ps.close();
    }
    
    public void changeInfo(final int tab, final int type, final int page) {
        this.tab = tab;
        this.type = type;
        this.page = page;
    }
    
    public int getTab() {
        return this.tab;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getPage() {
        return this.page;
    }
}
