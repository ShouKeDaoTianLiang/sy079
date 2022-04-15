package server;

import client.MapleCharacter;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import database.DatabaseConnection;
import tools.FileoutputUtil;
import client.inventory.MapleRing;
import client.inventory.Equip;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MaplePet;
import tools.packet.MTSCSPacket;
import constants.GameConstants;
import client.MapleClient;
import java.sql.SQLException;
import java.util.Iterator;
import client.inventory.MapleInventoryType;
import tools.Pair;
import java.util.ArrayList;
import client.inventory.Item;
import java.util.List;
import client.inventory.ItemLoader;
import java.io.Serializable;

public class CashShop implements Serializable
{
    private static final long serialVersionUID = 231541893513373579L;
    private final int accountId;
    private final int characterId;
    private final ItemLoader factory;
    private List<Item> inventory;
    private final List<Integer> uniqueids;
    
    public CashShop(final int accountId, final int characterId, final int jobType) throws SQLException {
        this.inventory = new ArrayList<Item>();
        this.uniqueids = new ArrayList<Integer>();
        this.accountId = accountId;
        this.characterId = characterId;
        if (jobType / 1000 == 1) {
            this.factory = ItemLoader.CASHSHOP_CYGNUS;
        }
        else if ((jobType / 100 == 21 || jobType / 100 == 20) && jobType != 2001) {
            this.factory = ItemLoader.CASHSHOP_ARAN;
        }
        else if (jobType == 2001 || jobType / 100 == 22) {
            this.factory = ItemLoader.CASHSHOP_EVAN;
        }
        else if (jobType >= 3000) {
            this.factory = ItemLoader.CASHSHOP_RESIST;
        }
        else if (jobType / 10 == 43) {
            this.factory = ItemLoader.CASHSHOP_DB;
        }
        else {
            this.factory = ItemLoader.CASHSHOP_EXPLORER;
        }
        for (final Pair<Item, MapleInventoryType> item : this.factory.loadItems(false, accountId).values()) {
            this.inventory.add(item.getLeft());
        }
    }
    
    public int getItemsSize() {
        return this.inventory.size();
    }
    
    public List<Item> getInventory() {
        return this.inventory;
    }
    
    public Item findByCashId(final int cashId) {
        for (final Item item : this.inventory) {
            if (item.getUniqueId() == cashId) {
                return item;
            }
        }
        return null;
    }
    
    public void checkExpire(final MapleClient c) {
        final List<Item> toberemove = new ArrayList<Item>();
        for (final Item item : this.inventory) {
            if (item != null && !GameConstants.isPet(item.getItemId()) && item.getExpiration() > 0L && item.getExpiration() < System.currentTimeMillis()) {
                toberemove.add(item);
            }
        }
        if (toberemove.size() > 0) {
            for (final Item item : toberemove) {
                this.removeFromInventory(item);
                c.sendPacket(MTSCSPacket.cashItemExpired(item.getUniqueId()));
            }
            toberemove.clear();
        }
    }
    
    public Item toItemA(final CashItemInfoA cItem) {
        return this.toItemA(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }
    
    public Item toItemA(final CashItemInfoA cItem, final String gift) {
        return this.toItemA(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }
    
    public Item toItemA(final CashItemInfoA cItem, final int uniqueid) {
        return this.toItemA(cItem, uniqueid, "");
    }
    
    public Item toItemA(final CashItemInfoA cItem, int uniqueid, final String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        if (period <= 0L || GameConstants.isPet(cItem.getId())) {
            period = 45L;
        }
        Item ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(cItem.getId());
            eq.setUniqueId(uniqueid);
            eq.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                final MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        }
        else {
            final Item item = new Item(cItem.getId(), (short)0, (short)cItem.getCount(), (short)0, uniqueid);
            item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                final MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }
    
    public Item toItem(final CashItemInfo cItem) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }
    
    public Item toItem(final CashItemInfo cItem, final String gift) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }
    
    public Item toItem(final CashItemInfo cItem, final int uniqueid) {
        return this.toItem(cItem, uniqueid, "");
    }
    
    public Item toItem(final CashItemInfo cItem, int uniqueid, final String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        if (GameConstants.isPet(cItem.getId())) {
            period = 90L;
        }
        else if (cItem.getId() < 5210000 || cItem.getId() > 5360099 || cItem.getId() == 5220007 || cItem.getId() == 5220008) {
            period = 0L;
        }
        Item ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(cItem.getId());
            eq.setUniqueId(uniqueid);
            if (GameConstants.isPet(cItem.getId()) || period > 0L) {
                eq.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                final MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        }
        else {
            final Item item = new Item(cItem.getId(), (short)0, (short)cItem.getCount(), (short)0, uniqueid);
            if (period > 0L) {
                item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            item.setGMLog("商場购买 - SN:" + cItem.getSN() + " 時間: " + FileoutputUtil.CurrentReadable_Date());
            if (cItem.getId() == 5211047 || cItem.getId() == 5360014) {
                item.setExpiration(System.currentTimeMillis() + 10800000L);
            }
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                final MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }
    
    public void addToInventory(final Item item) {
        this.inventory.add(item);
    }
    
    public void removeFromInventory(final Item item) {
        this.inventory.remove(item);
    }
    
    public void gift(final int recipient, final String from, final String message, final int sn) {
        this.gift(recipient, from, message, sn, 0);
    }
    
    public void gift(final int recipient, final String from, final String message, final int sn, final int uniqueid) {
        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, uniqueid);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    public List<Pair<Item, String>> loadGifts() {
        final List<Pair<Item, String>> gifts = new ArrayList<Pair<Item, String>>();
        final Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"));
                final Item item = this.toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
                gifts.add(new Pair<Item, String>(item, rs.getString("message")));
                this.uniqueids.add(item.getUniqueId());
                final List<CashItemInfo> packages = CashItemFactory.getInstance().getPackageItems(cItem.getId());
                if (packages != null && packages.size() > 0) {
                    for (final CashItemInfo packageItem : packages) {
                        this.addToInventory(this.toItem(packageItem, rs.getString("from")));
                    }
                }
                else {
                    this.addToInventory(item);
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ps.executeUpdate();
            ps.close();
            this.save();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return gifts;
    }
    
    public boolean canSendNote(final int uniqueid) {
        return this.uniqueids.contains(uniqueid);
    }
    
    public void sendedNote(final int uniqueid) {
        for (int i = 0; i < this.uniqueids.size(); ++i) {
            if (this.uniqueids.get(i) == uniqueid) {
                this.uniqueids.remove(i);
            }
        }
    }
    
    public void save() throws SQLException {
        final List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
        for (final Item item : this.inventory) {
            itemsWithType.add(new Pair<Item, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        this.factory.saveItems(itemsWithType, this.accountId);
    }
    
    public Item toItem(final CashItemInfo cItem, final MapleCharacter chr, int uniqueid, final String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        Item ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(cItem.getId());
            eq.setUniqueId(uniqueid);
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                final MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        }
        else {
            final Item item = new Item(cItem.getId(), (short)0, (short)cItem.getCount(), (short)0, uniqueid);
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                final MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }
}
