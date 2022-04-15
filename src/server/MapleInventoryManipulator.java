package server;

import tools.FileoutputUtil;
import handling.world.World.Broadcast;
import server.maps.MapleMapObject;
import java.awt.Point;
import java.util.Map;
import client.PlayerStats;
import client.MapleBuffStat;
import client.inventory.ModifyInventory;
import java.util.ArrayList;
import client.inventory.Equip;
import client.inventory.ItemFlag;
import java.util.Iterator;
import java.util.List;
import client.inventory.InventoryException;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MaplePet;
import client.inventory.MapleInventoryType;
import client.inventory.MapleEquipOnlyId;
import tools.MaplePacketCreator;
import constants.GameConstants;
import client.MapleClient;
import client.inventory.Item;
import tools.packet.MTSCSPacket;
import client.MapleCharacter;

public class MapleInventoryManipulator
{
    public static void addRing(final MapleCharacter chr, final int itemId, final int ringId, final int sn) {
        final CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        final Item ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().getSession().write(MTSCSPacket.showBoughtCSItem(ring, sn, chr.getClient().getAccID()));
    }
    
    public static boolean addbyItem(final MapleClient c, final Item item) {
        return addbyItem(c, item, false) >= 0;
    }
    
    public static short addbyItem(final MapleClient c, final Item item, final boolean fromcs) {
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
            }
            return newSlot;
        }
        if (!fromcs) {
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, item));
        }
        if (item.hasSetOnlyId()) {
            item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }
    
    public static int getUniqueId(final int itemId, final MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            }
            else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        }
        else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }
    
    public static boolean 输出道具1(final MapleClient c, final int itemId, final short quantity, final String gmLog) {
        return 查询道具(c, itemId, quantity, null, null, 0L, 0, gmLog);
    }
    
    public static boolean 输出道具(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final long period, final String gmLog) {
        return 查询道具(c, itemId, quantity, owner, pet, period, 0, gmLog);
    }
    
    public static boolean 查询道具(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final long period, final int state, final String gmLog) {
        return 查询(c, itemId, quantity, owner, pet, period, state, gmLog) >= 0;
    }
    
    public static byte 查询(final MapleClient c, final int itemId, short quantity, final String owner, final MaplePet pet, final long period, final int state, final String gmLog) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        final int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1L) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.sendPacket(MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    if (newQ2 == 0) {
                        c.getPlayer().havePartyQuest(itemId);
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return (byte)newSlot;
                    }
                    quantity -= newQ2;
                    final Item nItem = new Item(itemId, (short)0, newQ2, (short)0, uniqueid);
                    newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        return -1;
                    }
                    if (owner != null) {
                        nItem.setOwner(owner);
                    }
                    if (gmLog != null) {
                        nItem.setGMLog(gmLog);
                    }
                    if (period > 0L) {
                        nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                    }
                    if (pet != null) {
                        nItem.setPet(pet);
                        pet.setInventoryPosition(newSlot);
                        c.getPlayer().addPet(pet);
                    }
                    c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                    if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                    }
                }
            }
            else {
                final Item nItem = new Item(itemId, (short)0, quantity, (short)0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
            final Item nEquip = ii.getEquipById(itemId);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            nEquip.setUniqueId(uniqueid);
            if (gmLog != null) {
                nEquip.setGMLog(gmLog);
            }
            if (period > 0L) {
                nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
            if (newSlot == -1) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                return -1;
            }
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, nEquip));
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte)newSlot;
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final byte Flag) {
        return addById(c, itemId, quantity, null, null, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final byte Flag) {
        return addById(c, itemId, quantity, owner, null, 0L, Flag);
    }
    
    public static byte addId(final MapleClient c, final int itemId, final short quantity, final String owner, final byte Flag) {
        return addId(c, itemId, quantity, owner, null, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final byte Flag) {
        return addById(c, itemId, quantity, owner, pet, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final long period, final byte Flag) {
        return addId(c, itemId, quantity, owner, pet, period, Flag) >= 0;
    }
    
    public static byte addId(final MapleClient c, final int itemId, short quantity, final String owner, final MaplePet pet, final long period, final byte Flag) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        final int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1L) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.sendPacket(MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    if (newQ2 == 0) {
                        c.getPlayer().havePartyQuest(itemId);
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return (byte)newSlot;
                    }
                    quantity -= newQ2;
                    final Item nItem = new Item(itemId, (short)0, newQ2, (short)0, uniqueid);
                    newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        return -1;
                    }
                    if (owner != null) {
                        nItem.setOwner(owner);
                    }
                    if (Flag > 0 && ii.isCash(nItem.getItemId())) {
                        short flag = nItem.getFlag();
                        flag |= (short)ItemFlag.KARMA_EQ.getValue();
                        nItem.setFlag(flag);
                    }
                    if (period > 0L) {
                        if (period < 1000L) {
                            nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                        }
                        else {
                            nItem.setExpiration(System.currentTimeMillis() + period);
                        }
                    }
                    if (pet != null) {
                        nItem.setPet(pet);
                        pet.setInventoryPosition(newSlot);
                        c.getPlayer().addPet(pet);
                    }
                    c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                    if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                    }
                }
            }
            else {
                final Item nItem = new Item(itemId, (short)0, quantity, (short)0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
            final Item nEquip = ii.getEquipById(itemId);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            nEquip.setUniqueId(uniqueid);
            if (Flag > 0 && ii.isCash(nEquip.getItemId())) {
                short flag2 = nEquip.getFlag();
                flag2 |= (short)ItemFlag.KARMA_USE.getValue();
                nEquip.setFlag(flag2);
            }
            if (period > 0L) {
                nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            if (nEquip.hasSetOnlyId()) {
                nEquip.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
            }
            newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
            if (newSlot == -1) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                return -1;
            }
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, nEquip));
            c.getPlayer().checkCopyItems();
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte)newSlot;
    }
    
    public static Item addbyId_Gachapon(final MapleClient c, final int itemId, short quantity) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.showItemUnavailable());
            return null;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                Item nItem = null;
                boolean recieved = false;
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        nItem = i.next();
                        final short oldQ = nItem.getQuantity();
                        if (oldQ < slotMax) {
                            recieved = true;
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            nItem.setQuantity(newQ);
                            c.sendPacket(MaplePacketCreator.updateInventorySlot(type, nItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    if (newQ2 == 0) {
                        break;
                    }
                    quantity -= newQ2;
                    nItem = new Item(itemId, (short)0, newQ2, (short)0);
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1 && recieved) {
                        return nItem;
                    }
                    if (newSlot == -1) {
                        return null;
                    }
                    recieved = true;
                    c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                    if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                    }
                }
                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
                return null;
            }
            else {
                final Item nItem = new Item(itemId, (short)0, quantity, (short)0);
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot2 == -1) {
                    return null;
                }
                c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem));
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        }
        else {
            if (quantity != 1) {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
            final Item nEquip = ii.randomizeStats((Equip)ii.getEquipById(itemId));
            final Item item = ii.randomizeStats((Equip)ii.getEquipById(itemId));
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                return null;
            }
            if (nEquip.hasSetOnlyId()) {
                nEquip.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
            }
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, item, true));
            c.getPlayer().havePartyQuest(item.getItemId());
            c.getPlayer().checkCopyItems();
            return item;
        }
    }
    
    public static boolean addFromDrop(final MapleClient c, final Item item, final boolean show) {
        return addFromDrop(c, item, show, false);
    }
    
    public static boolean addFromDrop(final MapleClient c, Item item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            c.getSession().write(MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.getSession().write(MaplePacketCreator.updateInventorySlot(type, eItem, true));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write(MaplePacketCreator.getInventoryFull());
                        c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem2));
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            if (item.hasSetOnlyId()) {
                item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                c.getSession().write(MaplePacketCreator.getInventoryFull());
                c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.getPlayer().checkCopyItems();
            c.getSession().write(MaplePacketCreator.addInventorySlot(type, item, true));
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "你已經獲得了一個 永恆的雪花， 可以到弓箭手村尋找阿拉米亞對話。");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "你已經獲得了一個 溫暖陽光， 可以透過 @joyce 指令到楓葉樹下裝飾楓樹。");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "你已經獲得了一個 聖誕樹裝飾， 可以透過 @joyce 指令到幸福村來裝飾聖誕樹。");
                    break;
                }
                case 4000516: {
                    c.getPlayer().dropMessage(5, "你已經獲得了一個 香爐， 可以到不夜城尋找龍山寺師父對話。");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write(MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    public static boolean 商店防止复制(final MapleClient c, final Item item, final boolean show) {
        return 商店防止复制(c, item, show, false);
    }
    
    public static boolean 商店防止复制(final MapleClient c, Item item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration() && slotMax <= slotMax - oldQ) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.sendPacket(MaplePacketCreator.updateInventorySlot(type, eItem, true));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem2));
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, item, true));
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.sendPacket(MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    public static boolean pet_addFromDrop(final MapleClient c, Item item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.sendPacket(MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem, false));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.sendPacket(MaplePacketCreator.addInventorySlot(type, nItem2));
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (item.hasSetOnlyId()) {
                item.setEquipOnlyId(MapleEquipOnlyId.getInstance().getNextEquipOnlyId());
            }
            if (newSlot3 == -1) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.sendPacket(MaplePacketCreator.addInventorySlot(type, item, false));
            c.getPlayer().checkCopyItems();
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.sendPacket(MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    private static final Item checkEnhanced(final Item before, final MapleCharacter chr) {
        if (before instanceof Equip) {
            final Equip eq = (Equip)before;
            if (eq.getState() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && Randomizer.nextInt(100) > 80) {
                eq.resetPotential();
            }
        }
        return before;
    }
    
    private static int rand(final int min, final int max) {
        return Math.abs(Randomizer.rand(min, max));
    }
    
    public static boolean checkSpace(final MapleClient c, final int itemid, int quantity, final String owner) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) || !ii.itemExists(itemid)) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (c == null || c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemid);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid) && existing.size() > 0) {
                for (final Item eItem : existing) {
                    final short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                    }
                    if (quantity <= 0) {
                        break;
                    }
                }
            }
            int numSlotsNeeded;
            if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                numSlotsNeeded = (int)Math.ceil(quantity / (double)slotMax);
            }
            else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !c.getPlayer().getInventory(type).isFull();
    }
    
    public static void removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop) {
        removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }
    
    public static void removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (item.getQuantity() == 0 && !allowZero) {
                c.sendPacket(MaplePacketCreator.clearInventoryItem(type, item.getPosition(), fromDrop));
            }
            else {
                c.sendPacket(MaplePacketCreator.updateInventorySlot(type, item, fromDrop));
            }
        }
    }
    
    public static boolean removeById(final MapleClient c, final MapleInventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        for (final Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (remremove <= item.getQuantity()) {
                removeFromSlot(c, type, item.getPosition(), (short)remremove, fromDrop, consume);
                remremove = 0;
                break;
            }
            remremove -= item.getQuantity();
            removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
        }
        return remremove <= 0;
    }
    
    public static void move(final MapleClient c, final MapleInventoryType type, final short src, final short dst) {
        if (src < 0 || dst < 0) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        final Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(c, source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        final List<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null && initialTarget.getItemId() == source.getItemId() && !GameConstants.isRechargable(source.getItemId())) {
            if (olddstQ + oldsrcQ > slotMax) {
                mods.add(new ModifyInventory(1, source));
                mods.add(new ModifyInventory(1, initialTarget));
            }
            else {
                mods.add(new ModifyInventory(3, source));
                mods.add(new ModifyInventory(1, initialTarget));
            }
        }
        else {
            mods.add(new ModifyInventory(2, source, src));
        }
        c.sendPacket(MaplePacketCreator.modifyInventory(true, mods));
    }
    
    public static void equip(final MapleClient c, final short src, short dst) {
        boolean itemChanged = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        final PlayerStats statst = c.getPlayer().getStat();
        Equip source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null || source.getDurability() == 0) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().isUntradeableOnEquip(source.getItemId())) {
            source.setFlag((byte)ItemFlag.UNTRADEABLE.getValue());
            itemChanged = true;
        }
        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
        if (ii.isCash(source.getItemId()) && source.getUniqueId() <= 0) {
            source.setUniqueId(1);
            c.sendPacket(MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && !GameConstants.is豆豆装备(source.getItemId())) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (dst >= -999 && dst < -99 && stats.get("cash") == 0 && !GameConstants.is豆豆装备(source.getItemId())) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus)) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11) {
            AutobanManager.getInstance().autoban(c, "Equipment hack, itemid " + source.getItemId() + " to slot " + dst);
            return;
        }
        if (GameConstants.isKatara(source.getItemId())) {
            dst = -10;
        }
        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        switch (dst) {
            case -6: {
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-5));
                if (top == null || !GameConstants.isOverall(top.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-5), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -5: {
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-5));
                final Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-6));
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull((bottom != null && GameConstants.isOverall(source.getItemId())) ? 1 : 0)) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short)(-5), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom == null || !GameConstants.isOverall(source.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-6), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -10: {
                final Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-11));
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    break;
                }
                else {
                    if (weapon == null || !GameConstants.isTwoHanded(weapon.getItemId())) {
                        break;
                    }
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.sendPacket(MaplePacketCreator.getInventoryFull());
                        c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short)(-11), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                    break;
                }
            }
            case -11: {
                final Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-10));
                if (shield == null || !GameConstants.isTwoHanded(source.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.sendPacket(MaplePacketCreator.getInventoryFull());
                    c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-10), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
        }
        source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        short flag = source.getFlag();
        if (stats.get("equipTradeBlock") == 1) {
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= (short)ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                c.sendPacket(MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
            }
        }
        else if (ItemFlag.KARMA_EQ.check(flag)) {
            source.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
            c.sendPacket(MaplePacketCreator.updateSpecialItemUse(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        else if (ItemFlag.KARMA_USE.check(flag)) {
            source.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
            c.sendPacket(MaplePacketCreator.updateSpecialItemUse(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        final List<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        if (itemChanged) {
            mods.add(new ModifyInventory(3, source));
            mods.add(new ModifyInventory(0, source.copy()));
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                chr.cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
            if (chr.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.LIGHTNING_CHARGE);
            }
        }
        if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }
        mods.add(new ModifyInventory(2, source, src));
        c.sendPacket(MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)2));
        chr.equipChanged();
        c.sendPacket(MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
    }
    
    public static void unequip(final MapleClient c, final short src, final short dst) {
        final Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        final Equip target = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        if (dst < 0 || source == null) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (target != null && src <= 0) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return;
        }
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (c.getPlayer().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
        }
        if (source.getItemId() == 1122017) {
            c.getPlayer().cancelFairySchedule(true);
        }
        c.sendPacket(MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)1));
        c.getPlayer().equipChanged();
    }
    
    public static boolean drop(final MapleClient c, final MapleInventoryType type, final short src, final short quantity) {
        return drop(c, type, src, quantity, false);
    }
    
    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, final short quantity, final boolean npcInduced) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null) {
            return false;
        }
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        if (source == null || (!npcInduced && GameConstants.isPet(source.getItemId()))) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        if (ii.isCash(source.getItemId()) || source.getExpiration() > 0L) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(6, "[丟棄物品] 原始位置:" + src + " 種類:" + type + " 物品ID:" + source.getItemId() + " 物品名稱:" + MapleItemInformationProvider.getInstance().getName(source.getItemId()));
        }
        final short flag = source.getFlag();
        if (quantity > source.getQuantity()) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        if (System.currentTimeMillis() - c.getLasttime() < c.getDeadtime()) {
            c.getPlayer().dropMessage("悠着点，太快会掉线的。");
            c.sendPacket(MaplePacketCreator.enableActions());
            return false;
        }
        c.setLasttime(System.currentTimeMillis());
        final Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short)(source.getQuantity() - quantity));
            c.sendPacket(MaplePacketCreator.dropInventoryItemUpdate(type, source));
            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
                else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
                else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            }
            else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
            }
            else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
            }
        }
        else {
            c.getPlayer().getInventory(type).removeSlot(src);
            c.sendPacket(MaplePacketCreator.dropInventoryItem((src < 0) ? MapleInventoryType.EQUIP : type, src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
                else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
                else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            }
            else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
            }
            else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
            }
        }
        return true;
    }
    
    public static void removeAllByEquipOnlyId(final MapleClient c, final int equipOnlyId) {
        if (c.getPlayer() == null) {
            return;
        }
        boolean locked = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final List<Item> copyEquipItems = (List<Item>)c.getPlayer().getInventory(MapleInventoryType.EQUIP).listByEquipOnlyId(equipOnlyId);
        for (final Item item : copyEquipItems) {
            if (item != null) {
                if (!locked) {
                    item.setOwner("复制装备");
                    final String msgtext = "玩家[A] :" + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等级 " + c.getPlayer().getLevel() + ") 地图: " + c.getPlayer().getMapId() + " 在玩家背包中发现复制装备[" + ii.getName(item.getItemId()) + "-" + item.getItemId() + "]已经将其锁定。";
                    Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM消息] " + msgtext).getBytes());
                    System.out.print(msgtext + " 道具唯一ID: " + item.getEquipOnlyId());
                    FileoutputUtil.log("日志/Logs/装备复制.txt", msgtext + " 道具唯一ID: " + item.getEquipOnlyId());
                    FileoutputUtil.log("日志/复制装备.txt", msgtext, true);
                    locked = true;
                }
                else {
                    removeFromSlot(c, MapleInventoryType.EQUIP, item.getPosition(), item.getQuantity(), true, false);
                    c.getPlayer().dropMessage(5, "在背包中发现复制装备[" + ii.getName(item.getItemId()) + "]已经将其删除。");
                    c.getPlayer().equipChanged();
                }
            }
        }
        final List<Item> copyEquipedItems = (List<Item>)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).listByEquipOnlyId(equipOnlyId);
        for (final Item item2 : copyEquipedItems) {
            if (item2 != null) {
                if (!locked) {
                    item2.setOwner("复制装备");
                    final String msgtext2 = "玩家[B]: " + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等级 " + c.getPlayer().getLevel() + ") 地图: " + c.getPlayer().getMapId() + " 在玩家穿戴中发现复制装备[" + ii.getName(item2.getItemId()) + "-" + item2.getItemId() + "]已经将其锁定。";
                    Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM消息] " + msgtext2).getBytes());
                    System.out.print(msgtext2 + " 道具唯一ID: " + item2.getEquipOnlyId());
                    FileoutputUtil.log("日志/Logs/装备复制1.txt", msgtext2 + " 道具唯一ID: " + item2.getEquipOnlyId());
                    locked = true;
                }
                else {
                    removeFromSlot(c, MapleInventoryType.EQUIPPED, item2.getPosition(), item2.getQuantity(), true, false);
                    c.getPlayer().dropMessage(5, "在穿戴中发现复制装备[" + ii.getName(item2.getItemId()) + "]已经将其删除。");
                    c.getPlayer().equipChanged();
                }
            }
        }
    }
}
