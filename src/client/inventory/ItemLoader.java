package client.inventory;

import java.sql.Connection;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import constants.GameConstants;
import database.DatabaseConnection;
import java.util.LinkedHashMap;
import tools.Pair;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

public enum ItemLoader
{
    INVENTORY("inventoryitems", "inventoryequipment", 0, new String[] { "characterid" }), 
    STORAGE("inventoryitems", "inventoryequipment", 1, new String[] { "accountid" }), 
    CASHSHOP_EXPLORER("csitems", "csequipment", 2, new String[] { "accountid" }), 
    CASHSHOP_CYGNUS("csitems", "csequipment", 3, new String[] { "accountid" }), 
    CASHSHOP_ARAN("csitems", "csequipment", 4, new String[] { "accountid" }), 
    HIRED_MERCHANT("hiredmerchitems", "hiredmerchequipment", 5, new String[] { "packageid", "accountid", "characterid" }), 
    DUEY("dueyitems", "dueyequipment", 6, new String[] { "packageid" }), 
    CASHSHOP_EVAN("csitems", "csequipment", 7, new String[] { "accountid" }), 
    MTS("mtsitems", "mtsequipment", 8, new String[] { "packageid" }), 
    MTS_TRANSFER("mtstransfer", "mtstransferequipment", 9, new String[] { "characterid" }), 
    CASHSHOP_DB("csitems", "csequipment", 10, new String[] { "accountid" }), 
    CASHSHOP_RESIST("csitems", "csequipment", 11, new String[] { "accountid" });
    
    private int value;
    private String table;
    private String table_equip;
    private List<String> arg;
    
    private ItemLoader(final String table, final String table_equip, final int value, final String[] arg) {
        this.table = table;
        this.table_equip = table_equip;
        this.value = value;
        this.arg = Arrays.asList(arg);
    }
    
    public int getValue() {
        return this.value;
    }
    
    public Map<Integer, Pair<Item, MapleInventoryType>> loadItems_hm(final int packageid, final int accountid) throws SQLException {
        final Map<Integer, Pair<Item, MapleInventoryType>> items = new LinkedHashMap<Integer, Pair<Item, MapleInventoryType>>();
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM `hiredmerchitems` LEFT JOIN `hiredmerchequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `packageid` = ? AND `accountid` = ? ");
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query.toString());
        ps.setInt(1, this.value);
        ps.setInt(2, packageid);
        ps.setInt(3, accountid);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
            if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                final Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getByte("flag"));
                equip.setQuantity((short)1);
                equip.setOwner(rs.getString("owner"));
                equip.setExpiration(rs.getLong("expiredate"));
                equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                equip.setEquipOnlyId(rs.getInt("equipOnlyId"));
                equip.setLevel(rs.getByte("level"));
                equip.setStr(rs.getShort("str"));
                equip.setDex(rs.getShort("dex"));
                equip.setInt(rs.getShort("int"));
                equip.setLuk(rs.getShort("luk"));
                equip.setHp(rs.getShort("hp"));
                equip.setMp(rs.getShort("mp"));
                equip.setWatk(rs.getShort("watk"));
                equip.setMatk(rs.getShort("matk"));
                equip.setWdef(rs.getShort("wdef"));
                equip.setMdef(rs.getShort("mdef"));
                equip.setAcc(rs.getShort("acc"));
                equip.setAvoid(rs.getShort("avoid"));
                equip.setHands(rs.getShort("hands"));
                equip.setSpeed(rs.getShort("speed"));
                equip.setJump(rs.getShort("jump"));
                equip.setViciousHammer(rs.getByte("ViciousHammer"));
                equip.setItemEXP(rs.getInt("itemEXP"));
                equip.setGMLog(rs.getString("GM_Log"));
                equip.setDurability(rs.getInt("durability"));
                equip.setEnhance(rs.getByte("enhance"));
                equip.setPotential1(rs.getShort("potential1"));
                equip.setPotential2(rs.getShort("potential2"));
                equip.setPotential3(rs.getShort("potential3"));
                equip.setHpR(rs.getShort("hpR"));
                equip.setMpR(rs.getShort("mpR"));
                equip.setGiftFrom(rs.getString("sender"));
                equip.setEquipLevel(rs.getByte("itemlevel"));
                if (equip.getUniqueId() > -1 && GameConstants.isEffectRing(rs.getInt("itemid"))) {
                    final MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                    if (ring != null) {
                        equip.setRing(ring);
                    }
                }
                if (equip.hasSetOnlyId()) {
                    equip.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                }
                items.put(rs.getInt("inventoryitemid"), new Pair<Item, MapleInventoryType>(equip.copy(), mit));
            }
            else {
                final Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getByte("flag"));
                item.setUniqueId(rs.getInt("uniqueid"));
                item.setOwner(rs.getString("owner"));
                item.setExpiration(rs.getLong("expiredate"));
                item.setGMLog(rs.getString("GM_Log"));
                item.setGiftFrom(rs.getString("sender"));
                if (GameConstants.isPet(item.getItemId())) {
                    if (item.getUniqueId() > -1) {
                        final MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                        if (pet != null) {
                            item.setPet(pet);
                        }
                    }
                    else {
                        final int new_unique = MapleInventoryIdentifier.getInstance();
                        item.setUniqueId(new_unique);
                        item.setPet(MaplePet.createPet(item.getItemId(), new_unique));
                    }
                }
                items.put(rs.getInt("inventoryitemid"), new Pair<Item, MapleInventoryType>(item.copy(), mit));
            }
        }
        rs.close();
        ps.close();
        return items;
    }
    
    public Map<Integer, Pair<Item, MapleInventoryType>> loadItems(final boolean login, final Integer... id) throws SQLException {
        final List<Integer> lulz = Arrays.asList(id);
        final Map<Integer, Pair<Item, MapleInventoryType>> items = new LinkedHashMap<Integer, Pair<Item, MapleInventoryType>>();
        if (lulz.size() != this.arg.size()) {
            return items;
        }
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM `");
        query.append(this.table);
        query.append("` LEFT JOIN `");
        query.append(this.table_equip);
        query.append("` USING(`inventoryitemid`) WHERE `type` = ?");
        for (final String g : this.arg) {
            query.append(" AND `");
            query.append(g);
            query.append("` = ?");
        }
        if (login) {
            query.append(" AND `inventorytype` = ");
            query.append(MapleInventoryType.EQUIPPED.getType());
        }
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query.toString());
        ps.setInt(1, this.value);
        for (int i = 0; i < lulz.size(); ++i) {
            ps.setInt(i + 2, lulz.get(i));
        }
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
            if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                final Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("uniqueid"), rs.getByte("flag"));
                if (!login) {
                    equip.setQuantity((short)1);
                    equip.setOwner(rs.getString("owner"));
                    equip.setExpiration(rs.getLong("expiredate"));
                    equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                    equip.setEquipOnlyId(rs.getInt("equipOnlyId"));
                    equip.setLevel(rs.getByte("level"));
                    equip.setStr(rs.getShort("str"));
                    equip.setDex(rs.getShort("dex"));
                    equip.setInt(rs.getShort("int"));
                    equip.setLuk(rs.getShort("luk"));
                    equip.setHp(rs.getShort("hp"));
                    equip.setMp(rs.getShort("mp"));
                    equip.setWatk(rs.getShort("watk"));
                    equip.setMatk(rs.getShort("matk"));
                    equip.setWdef(rs.getShort("wdef"));
                    equip.setMdef(rs.getShort("mdef"));
                    equip.setAcc(rs.getShort("acc"));
                    equip.setAvoid(rs.getShort("avoid"));
                    equip.setHands(rs.getShort("hands"));
                    equip.setSpeed(rs.getShort("speed"));
                    equip.setJump(rs.getShort("jump"));
                    equip.setViciousHammer(rs.getByte("ViciousHammer"));
                    equip.setItemEXP(rs.getInt("itemEXP"));
                    equip.setGMLog(rs.getString("GM_Log"));
                    equip.setDurability(rs.getInt("durability"));
                    equip.setEnhance(rs.getByte("enhance"));
                    equip.setPotential1(rs.getShort("potential1"));
                    equip.setPotential2(rs.getShort("potential2"));
                    equip.setPotential3(rs.getShort("potential3"));
                    equip.setHpR(rs.getShort("hpR"));
                    equip.setMpR(rs.getShort("mpR"));
                    equip.setGiftFrom(rs.getString("sender"));
                    equip.setEquipLevel(rs.getByte("itemlevel"));
                    if (equip.getUniqueId() > -1 && GameConstants.isEffectRing(rs.getInt("itemid"))) {
                        final MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                        if (ring != null) {
                            equip.setRing(ring);
                        }
                    }
                }
                if (equip.hasSetOnlyId()) {
                    equip.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
                }
                items.put(rs.getInt("inventoryitemid"), new Pair<Item, MapleInventoryType>(equip.copy(), mit));
            }
            else {
                final Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getByte("flag"));
                item.setUniqueId(rs.getInt("uniqueid"));
                item.setOwner(rs.getString("owner"));
                item.setExpiration(rs.getLong("expiredate"));
                item.setGMLog(rs.getString("GM_Log"));
                item.setGiftFrom(rs.getString("sender"));
                if (GameConstants.isPet(item.getItemId())) {
                    if (item.getUniqueId() > -1) {
                        final MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                        if (pet != null) {
                            item.setPet(pet);
                        }
                    }
                    else {
                        final int new_unique = MapleInventoryIdentifier.getInstance();
                        item.setUniqueId(new_unique);
                        item.setPet(MaplePet.createPet(item.getItemId(), new_unique));
                    }
                }
                items.put(rs.getInt("inventoryitemid"), new Pair<Item, MapleInventoryType>(item.copy(), mit));
            }
        }
        rs.close();
        ps.close();
        return items;
    }
    
    public void saveItems(final List<Pair<Item, MapleInventoryType>> items, final Integer... id) throws SQLException {
        final Connection con = DatabaseConnection.getConnection();
        this.saveItems(items, con, id);
    }
    
    public void saveItems(final List<Pair<Item, MapleInventoryType>> items, final Connection con, final Integer... id) throws SQLException {
        final List<Integer> lulz = Arrays.asList(id);
        if (lulz.size() != this.arg.size()) {
            return;
        }
        final StringBuilder query = new StringBuilder();
        query.append("DELETE FROM `");
        query.append(this.table);
        query.append("` WHERE `type` = ? AND (`");
        query.append((String)(String)this.arg.get(0));
        query.append("` = ?");
        if (this.arg.size() > 1) {
            for (int i = 1; i < this.arg.size(); ++i) {
                query.append(" OR `");
                query.append((String)(String)this.arg.get(i));
                query.append("` = ?");
            }
        }
        query.append(")");
        PreparedStatement ps = con.prepareStatement(query.toString());
        ps.setInt(1, this.value);
        for (int j = 0; j < lulz.size(); ++j) {
            ps.setInt(j + 2, lulz.get(j));
        }
        ps.executeUpdate();
        ps.close();
        if (items == null) {
            return;
        }
        final StringBuilder query_2 = new StringBuilder("INSERT INTO `");
        query_2.append(this.table);
        query_2.append("` (");
        for (final String g : this.arg) {
            query_2.append(g);
            query_2.append(", ");
        }
        query_2.append("itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, equipOnlyId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        for (final String g : this.arg) {
            query_2.append(", ?");
        }
        query_2.append(")");
        ps = con.prepareStatement(query_2.toString(), 1);
        try {
            final PreparedStatement pse = con.prepareStatement("INSERT INTO " + this.table_equip + " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (final Pair<Item, MapleInventoryType> pair : items) {
                final Item item = pair.getLeft();
                final MapleInventoryType mit = pair.getRight();
                try {
                    int k = 1;
                    for (int x = 0; x < lulz.size(); ++x) {
                        ps.setInt(k, lulz.get(x));
                        ++k;
                    }
                    ps.setInt(k, item.getItemId());
                    ps.setInt(k + 1, mit.getType());
                    ps.setInt(k + 2, item.getPosition());
                    ps.setInt(k + 3, item.getQuantity());
                    ps.setString(k + 4, item.getOwner());
                    ps.setString(k + 5, item.getGMLog());
                    ps.setInt(k + 6, item.getUniqueId());
                    ps.setLong(k + 7, item.getExpiration());
                    ps.setShort(k + 8, item.getFlag());
                    ps.setByte(k + 9, (byte)this.value);
                    ps.setString(k + 10, item.getGiftFrom());
                    ps.setInt(k + 11, item.getEquipOnlyId());
                    ps.executeUpdate();
                }
                catch (Exception ex) {
                    System.err.println("GMLOG : " + item.getGMLog() + " Table_equip : " + this.table + " " + ex);
                }
                if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                    final ResultSet rs = ps.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new RuntimeException("Inserting item failed.");
                    }
                    pse.setInt(1, rs.getInt(1));
                    rs.close();
                    final Equip equip = (Equip)item;
                    pse.setInt(2, equip.getUpgradeSlots());
                    pse.setInt(3, equip.getLevel());
                    pse.setInt(4, equip.getStr());
                    pse.setInt(5, equip.getDex());
                    pse.setInt(6, equip.getInt());
                    pse.setInt(7, equip.getLuk());
                    pse.setInt(8, equip.getHp());
                    pse.setInt(9, equip.getMp());
                    pse.setInt(10, equip.getWatk());
                    pse.setInt(11, equip.getMatk());
                    pse.setInt(12, equip.getWdef());
                    pse.setInt(13, equip.getMdef());
                    pse.setInt(14, equip.getAcc());
                    pse.setInt(15, equip.getAvoid());
                    pse.setInt(16, equip.getHands());
                    pse.setInt(17, equip.getSpeed());
                    pse.setInt(18, equip.getJump());
                    pse.setInt(19, equip.getViciousHammer());
                    pse.setInt(20, equip.getItemEXP());
                    pse.setInt(21, equip.getDurability());
                    pse.setByte(22, equip.getEnhance());
                    pse.setInt(23, equip.getPotential1());
                    pse.setInt(24, equip.getPotential2());
                    pse.setInt(25, equip.getPotential3());
                    pse.setInt(26, equip.getHpR());
                    pse.setInt(27, equip.getMpR());
                    pse.setByte(28, equip.getEquipLevel());
                    pse.executeUpdate();
                }
            }
            pse.close();
            ps.close();
        }
        catch (Exception ex2) {
            System.err.println("table_equip: " + this.table_equip + " " + ex2);
        }
    }
}
