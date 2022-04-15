package handling.channel.handler;

import tools.packet.PlayerShopPacket;
import server.shops.IMaplePlayerShop;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import provider.MapleDataProvider;
import java.sql.SQLException;
import database.DatabaseConnection;
import provider.MapleDataTool;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import java.io.File;
import java.util.concurrent.locks.Lock;
import server.maps.MapleMapObject;
import java.awt.Point;
import handling.world.MaplePartyCharacter;
import client.anticheat.CheatingOffense;
import java.awt.geom.Point2D;
import server.maps.MapleMapItem;
import server.maps.MapleMapObjectType;
import handling.world.World;
import server.shops.HiredMerchant;
import client.inventory.MaplePet;
import client.ISkill;
import client.PlayerStats;
import handling.channel.ChannelServer;
import server.MapleShopFactory;
import server.maps.MapleMist;
import java.awt.Rectangle;
import tools.packet.MTSCSPacket;
import tools.packet.PetPacket;
import client.inventory.MaplePet.PetFlag;
import server.maps.MapleLove;
import client.inventory.ItemFlag;
import client.MapleStat;
import handling.world.World.Broadcast;
import server.RandomRewards;
import tools.data.input.LittleEndianAccessor;
import server.life.MapleLifeFactory;
import java.util.concurrent.ThreadLocalRandom;
import constants.OtherSettings1;
import server.maps.SavedLocationType;
import scripting.NPCScriptManager;
import client.inventory.MapleMount;
import server.maps.MapleMap;
import server.life.MapleMonster;
import client.inventory.Equip.ScrollResult;
import server.AutobanManager;
import client.SkillFactory;
import java.util.Collection;
import server.StructPotentialItem;
import client.inventory.Equip;
import server.maps.FieldLimitType;
import server.quest.MapleQuest;
import tools.Pair;
import server.Randomizer;
import server.StructRewardItem;
import server.MapleItemInformationProvider;
import constants.GameConstants;
import client.MapleCharacter;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import client.inventory.Item;
import java.util.LinkedList;
import client.inventory.MapleInventory;
import constants.OtherSettings;
import server.MapleInventoryManipulator;
import tools.MaplePacketCreator;
import client.inventory.MapleInventoryType;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class InventoryHandler
{
    public static final int OWL_ID = 2;
    
    public static void ItemMove(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getPlayerShop() != null || c.getPlayer().getConversation() > 0 || c.getPlayer().getTrade() != null) {
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
        final MapleInventory inventory = c.getPlayer().getInventory(type);
        final short src = slea.readShort();
        final short dst = slea.readShort();
        final short quantity = slea.readShort();
        if (src < 0 && dst > 0) {
            if (System.currentTimeMillis() - c.getPlayer().getLasttime() < c.getPlayer().get防止复制时间()) {
                c.getPlayer().dropMessage(5, "请慢点使用.不然会掉线哟！");
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            MapleInventoryManipulator.unequip(c, src, dst);
        }
        else if (dst < 0) {
            if (System.currentTimeMillis() - c.getLasttime() < c.getDeadtime()) {
                c.getPlayer().dropMessage("悠着点，太快会掉线的。");
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            MapleInventoryManipulator.equip(c, src, dst);
        }
        else if (dst == 0) {
            if (quantity < 0) {
                c.getPlayer().dropMessage(1, "Either you're Tryst, or you're a hacker. So GT*O here.\r\n(P.S. If you're Tryst, I ask you to **** OFF. Either way, you're a hacker.)");
                return;
            }
            final int itemid = inventory.getItem(src).getItemId();
            final OtherSettings item_id = new OtherSettings();
            final String[] itembp_id = item_id.get禁止丢弃_id();
            for (int i = 0; i < itembp_id.length; ++i) {
                if (itemid == Integer.parseInt(itembp_id[i])) {
                    c.getPlayer().dropMessage(1, "该道具无法丢弃。");
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
            }
            if (c.getPlayer().getInventory(type).getItem(src) == null) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (System.currentTimeMillis() - c.getLasttime() < c.getDeadtime()) {
                c.getPlayer().dropMessage("悠着点，太快会掉线的。");
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            MapleInventoryManipulator.drop(c, type, src, quantity);
        }
        else {
            if (c.getPlayer().getGMLevel() > 0) {
                final int itemided = c.getPlayer().getInventory(type).getItem(src).getItemId();
                c.getPlayer().dropMessage("此物品的ID是:" + itemided);
            }
            MapleInventoryManipulator.move(c, type, src, dst);
        }
    }
    
    public static final void ItemSort(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        final MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
        if (pInvType == MapleInventoryType.UNDEFINED) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final MapleInventory pInv = c.getPlayer().getInventory(pInvType);
        boolean sorted = false;
        while (!sorted) {
            final byte freeSlot = (byte)pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte)(freeSlot + 1); i <= pInv.getSlotLimit(); ++i) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                }
                else {
                    sorted = true;
                }
            }
            else {
                sorted = true;
            }
        }
        c.sendPacket(MaplePacketCreator.finishedSort(pInvType.getType()));
        c.sendPacket(MaplePacketCreator.enableActions());
        c.sendPacket(MaplePacketCreator.serverNotice(1, "道具集合完毕!"));
    }
    
    public static final void ItemGather(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        final byte mode = slea.readByte();
        if (mode == 5) {
            c.getPlayer().dropMessage(1, "特殊栏道具暂不开放以种类排列.");
            c.sendPacket(MaplePacketCreator.finishedGather(mode));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final MapleInventoryType invType = MapleInventoryType.getByType(mode);
        final MapleInventory Inv = c.getPlayer().getInventory(invType);
        final List<Item> itemMap = new LinkedList<Item>();
        for (final Item item : Inv.list()) {
            itemMap.add(item.copy());
        }
        for (final Item itemStats : itemMap) {
            MapleInventoryManipulator.removeById(c, invType, itemStats.getItemId(), itemStats.getQuantity(), true, false);
        }
        final List<Item> sortedItems = sortItems(itemMap);
        for (final Item item2 : sortedItems) {
            MapleInventoryManipulator.addFromDrop(c, item2, false);
        }
        c.sendPacket(MaplePacketCreator.finishedGather(mode));
        c.sendPacket(MaplePacketCreator.enableActions());
        itemMap.clear();
        sortedItems.clear();
        c.sendPacket(MaplePacketCreator.serverNotice(1, "以种类排序完毕!"));
    }
    
    private static final List<Item> sortItems(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<Integer>();
        for (final Item item : passedMap) {
            itemIds.add(item.getItemId());
        }
        Collections.sort(itemIds);
        final List<Item> sortedList = new LinkedList<Item>();
        for (final Integer val : itemIds) {
            for (final Item item2 : passedMap) {
                if (val == item2.getItemId()) {
                    sortedList.add(item2);
                    passedMap.remove(item2);
                    break;
                }
            }
        }
        return sortedList;
    }
    
    public static final boolean UseRewardItem(final byte slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.sendPacket(MaplePacketCreator.enableActions());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);
                if (rewards != null && rewards.getLeft() > 0) {
                    boolean rewarded = false;
                    while (!rewarded) {
                        for (final StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) {
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0L) {
                                        item.setExpiration(System.currentTimeMillis() + reward.period * 60L * 60L * 10L);
                                    }
                                    MapleInventoryManipulator.addbyItem(c, item);
                                }
                                else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity, (byte)0);
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                                rewarded = true;
                                return true;
                            }
                        }
                    }
                }
                else {
                    chr.dropMessage(6, "Unknown error.");
                }
            }
            else {
                chr.dropMessage(6, "你有一個背包栏位满了 请空出来再打开");
            }
        }
        return false;
    }
    
    public static final void QuestKJ(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getCSPoints(2) < 200) {
            chr.dropMessage(1, "你没有足够的抵用卷！");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final byte action = (byte)(slea.readByte() + 1);
        short quest = slea.readShort();
        if (quest < 0) {
            quest += 65536;
        }
        if (chr == null) {
            return;
        }
        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 2: {
                final int npc = slea.readInt();
                q.complete(chr, npc);
                break;
            }
        }
        chr.modifyCSPoints(2, -200);
    }
    
    public static final void UseItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) || chr.getMapId() == 610030600) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + chr.getMap().getConsumeItemCoolTime() * 1000);
                }
            }
        }
        else {
            c.sendPacket(MaplePacketCreator.enableActions());
        }
    }
    
    public static final void UseReturnScroll(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getMapId() == 749040100) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
        }
        else {
            c.sendPacket(MaplePacketCreator.enableActions());
        }
    }
    
    public static final void UseMagnify(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        final Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte)slea.readShort());
        final Item toReveal = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte)slea.readShort());
        if (magnify == null || toReveal == null) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip)toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1 && (magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12) || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<StructPotentialItem>> pots = new LinkedList<List<StructPotentialItem>>(ii.getAllPotentialInfo().values());
            int new_state = Math.abs(eqq.getPotential1());
            if (new_state > 7 || new_state < 5) {
                new_state = 5;
            }
            final int lines = (eqq.getPotential2() != 0) ? 3 : 2;
            while (eqq.getState() != new_state) {
                for (int i = 0; i < lines; ++i) {
                    for (boolean rewarded = false; !rewarded; rewarded = true) {
                        final StructPotentialItem pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId()) && GameConstants.potentialIDFits(pot.potentialID, new_state, i)) {
                            switch (i) {
                                case 0: {
                                    eqq.setPotential1(pot.potentialID);
                                    break;
                                }
                                case 1: {
                                    eqq.setPotential2(pot.potentialID);
                                    break;
                                }
                                case 2: {
                                    eqq.setPotential3(pot.potentialID);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            c.sendPacket(MaplePacketCreator.scrolledItem(magnify, toReveal, false, true));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short)1, false);
            return;
        }
        c.sendPacket(MaplePacketCreator.getInventoryFull());
    }
    
    public static final boolean UseUpgradeScroll(final byte slot, final byte dst, final byte ws, final MapleClient c, final MapleCharacter chr) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0);
    }
    
    public static final boolean UseUpgradeScroll(final byte slot, final byte dst, final byte ws, final MapleClient c, final MapleCharacter chr, final int vegas) {
        boolean whiteScroll = false;
        boolean legendarySpirit = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ws & 0x2) == 0x2) {
            whiteScroll = true;
        }
        Equip toScroll;
        if (dst < 0) {
            toScroll = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        }
        else {
            legendarySpirit = true;
            toScroll = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        if (toScroll == null) {
            return false;
        }
        final byte oldLevel = toScroll.getLevel();
        final byte oldEnhance = toScroll.getEnhance();
        final byte oldState = toScroll.getState();
        final short oldFlag = toScroll.getFlag();
        final byte oldSlots = toScroll.getUpgradeSlots();
        final boolean checkIfGM = c.getPlayer().isGM();
        final Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                return false;
            }
        }
        else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                return false;
            }
        }
        else if (GameConstants.isPotentialScroll(scroll.getItemId()) && (toScroll.getState() >= 1 || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0) || vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if (!GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() >= 0) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        Item wscroll = null;
        final List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            return false;
        }
        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (scroll.getItemId() == 2049115 && toScroll.getItemId() != 1003068) {
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0: {
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                }
                case 1: {
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                }
                case 2: {
                    if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                }
                case 3: {
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                }
            }
        }
        else if (!GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId()) && !ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
            return false;
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            return false;
        }
        if (scroll.getQuantity() <= 0) {
            return false;
        }
        if (legendarySpirit && vegas == 0 && chr.getSkillLevel(SkillFactory.getSkill(1003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(10001003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(20001003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(20011003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(30001003)) <= 0) {
            AutobanManager.getInstance().addPoints(c, 50, 120000L, "Using the Skill 'Legendary Spirit' without having it.");
            return false;
        }
        final Equip scrolled = (Equip)ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas, checkIfGM);
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            scrollSuccess = ScrollResult.消失;
        }
        else if (scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState || scrolled.getFlag() > oldFlag) {
            scrollSuccess = ScrollResult.成功;
        }
        else if (GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots) {
            scrollSuccess = ScrollResult.成功;
        }
        else {
            scrollSuccess = ScrollResult.失败;
        }
        chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short)1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short)1, false, false);
        }
        if (scrollSuccess == ScrollResult.消失) {
            c.sendPacket(MaplePacketCreator.scrolledItem(scroll, toScroll, true, false));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            }
            else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        }
        else if (vegas == 0) {
            c.sendPacket(MaplePacketCreator.scrolledItem(scroll, scrolled, false, false));
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit), vegas == 0);
        if (dst < 0 && (scrollSuccess == ScrollResult.成功 || scrollSuccess == ScrollResult.消失) && vegas == 0) {
            chr.equipChanged();
        }
        return true;
    }
    
    public static final void UseCatchItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null) {
            switch (itemid) {
                case 2270004: {
                    final MapleMap map = chr.getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2L) {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)1));
                        map.killMonster(mob, chr, true, false, (byte)0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                        MapleInventoryManipulator.addById(c, 4001169, (short)1, (byte)0);
                        break;
                    }
                    map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)0));
                    chr.dropMessage(5, "怪物的生命力还很强大,无法捕捉.");
                    break;
                }
                case 2270002: {
                    final MapleMap map = chr.getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2L) {
                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)1));
                        map.killMonster(mob, chr, true, false, (byte)0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                        c.getPlayer().setAPQScore(c.getPlayer().getAPQScore() + 1);
                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.updateAriantPQRanking(c.getPlayer().getName(), c.getPlayer().getAPQScore(), false));
                        break;
                    }
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)0));
                    c.sendPacket(MaplePacketCreator.catchMob(mob.getId(), itemid, (byte)0));
                    break;
                }
                case 2270000: {
                    if (mob.getId() == 9300101) {
                        final MapleMap map = c.getPlayer().getMap();
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)1));
                        map.killMonster(mob, chr, true, false, (byte)0);
                        MapleInventoryManipulator.addById(c, 1902000, (short)1, null, (byte)0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                        break;
                    }
                    break;
                }
                case 2270003: {
                    if (mob.getId() != 9500320) {
                        break;
                    }
                    final MapleMap map = c.getPlayer().getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2L) {
                        map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)1));
                        map.killMonster(mob, chr, true, false, (byte)0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                        break;
                    }
                    map.broadcastMessage(MaplePacketCreator.catchMonster(mob.getId(), itemid, (byte)0));
                    chr.dropMessage(5, "怪物的生命力还很强大,无法捕捉.");
                    break;
                }
            }
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static final void UseMountFood(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemid = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null) {
            final int fatigue = mount.getFatigue();
            boolean levelup = false;
            mount.setFatigue((byte)(-30));
            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1) && level < 31) {
                    mount.setLevel((byte)(level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(MaplePacketCreator.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static final void UseScriptedNPCItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        long expiration_days = 0L;
        int mountid = 0;
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            switch (toUse.getItemId()) {
                case 2430007: {
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                    if (inventory.countById(3994102) >= 20 && inventory.countById(3994103) >= 20 && inventory.countById(3994104) >= 20 && inventory.countById(3994105) >= 20) {
                        MapleInventoryManipulator.addById(c, 2430008, (short)1, (byte)0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    }
                    else {
                        MapleInventoryManipulator.addById(c, 2430007, (short)1, (byte)0);
                    }
                    NPCScriptManager.getInstance().start(c, 2084001);
                    break;
                }
                case 2430008: {
                    chr.saveLocation(SavedLocationType.RICHIE);
                    boolean warped = false;
                    for (int i = 390001000; i <= 390001004; ++i) {
                        final MapleMap map = c.getChannelServer().getMapFactory().getMap(i);
                        if (map.getCharactersSize() == 0) {
                            chr.changeMap(map, map.getPortal(0));
                            warped = true;
                            break;
                        }
                    }
                    if (warped) {
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430008, 1, false, false);
                        break;
                    }
                    c.getPlayer().dropMessage(5, "所有地图都在使用，请稍后再试.");
                    break;
                }
                case 2430112: {
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
                        c.getPlayer().dropMessage(5, "请清理空间.");
                        break;
                    }
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                        if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430112, 25, true, false)) {
                            MapleInventoryManipulator.addById(c, 2049400, (short)1, (byte)0);
                            break;
                        }
                        c.getPlayer().dropMessage(5, "请清理空间.");
                        break;
                    }
                    else {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) < 10) {
                            c.getPlayer().dropMessage(5, "一个潜在的滚动条需要有10个片段，25个潜在的滚动条.");
                            break;
                        }
                        if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430112, 10, true, false)) {
                            MapleInventoryManipulator.addById(c, 2049401, (short)1, (byte)0);
                            break;
                        }
                        c.getPlayer().dropMessage(5, "请清理空间.");
                        break;
                    }
                }
                case 2430036: {
                    mountid = 1027;
                    expiration_days = 1L;
                    break;
                }
                case 2430037: {
                    mountid = 1028;
                    expiration_days = 1L;
                    break;
                }
                case 2430038: {
                    mountid = 1029;
                    expiration_days = 1L;
                    break;
                }
                case 2430039: {
                    mountid = 1030;
                    expiration_days = 1L;
                    break;
                }
                case 2430040: {
                    mountid = 1031;
                    expiration_days = 1L;
                    break;
                }
                case 2430053: {
                    mountid = 1027;
                    expiration_days = 1L;
                    break;
                }
                case 2430054: {
                    mountid = 1028;
                    expiration_days = 30L;
                    break;
                }
                case 2430055: {
                    mountid = 1029;
                    expiration_days = 30L;
                    break;
                }
                case 2430056: {
                    mountid = 1035;
                    expiration_days = 30L;
                    break;
                }
                case 2430072: {
                    mountid = 1034;
                    expiration_days = 7L;
                    break;
                }
                case 2430073: {
                    mountid = 1036;
                    expiration_days = 15L;
                    break;
                }
                case 2430074: {
                    mountid = 1037;
                    expiration_days = 15L;
                    break;
                }
                case 2430075: {
                    mountid = 1038;
                    expiration_days = 15L;
                    break;
                }
                case 2430076: {
                    mountid = 1039;
                    expiration_days = 15L;
                    break;
                }
                case 2430077: {
                    mountid = 1040;
                    expiration_days = 15L;
                    break;
                }
                case 2430080: {
                    mountid = 1042;
                    expiration_days = 20L;
                    break;
                }
                case 2430082: {
                    mountid = 1044;
                    expiration_days = 7L;
                    break;
                }
                case 2430091: {
                    mountid = 1049;
                    expiration_days = 10L;
                    break;
                }
                case 2430092: {
                    mountid = 1050;
                    expiration_days = 10L;
                    break;
                }
                case 2430093: {
                    mountid = 1051;
                    expiration_days = 10L;
                    break;
                }
                case 2430101: {
                    mountid = 1052;
                    expiration_days = 10L;
                    break;
                }
                case 2430102: {
                    mountid = 1053;
                    expiration_days = 10L;
                    break;
                }
                case 2430103: {
                    mountid = 1054;
                    expiration_days = 30L;
                    break;
                }
                case 2430117: {
                    mountid = 1036;
                    expiration_days = 365L;
                    break;
                }
                case 2430118: {
                    mountid = 1039;
                    expiration_days = 365L;
                    break;
                }
                case 2430119: {
                    mountid = 1040;
                    expiration_days = 365L;
                    break;
                }
                case 2430120: {
                    mountid = 1037;
                    expiration_days = 365L;
                    break;
                }
                case 2430136: {
                    mountid = 1069;
                    expiration_days = 30L;
                    break;
                }
                case 2430137: {
                    mountid = 1069;
                    expiration_days = 365L;
                    break;
                }
                case 2430201: {
                    mountid = 1096;
                    expiration_days = 60L;
                    break;
                }
                case 2430228: {
                    mountid = 1101;
                    expiration_days = 60L;
                    break;
                }
                case 2430229: {
                    mountid = 1102;
                    expiration_days = 60L;
                    break;
                }
            }
        }
        if (mountid > 0) {
            mountid += (GameConstants.isResist(c.getPlayer().getJob()) ? 30000000 : (GameConstants.isKOC(c.getPlayer().getJob()) ? 10000000 : (GameConstants.isEvan(c.getPlayer().getJob()) ? 20010000 : (GameConstants.isAran(c.getPlayer().getJob()) ? 20000000 : 0))));
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "你已经拥有了这个技能.");
            }
            else if (expiration_days > 0L) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(mountid), (byte)1, (byte)1, System.currentTimeMillis() + expiration_days * 24L * 60L * 60L * 1000L);
                c.getPlayer().dropMessage(5, "已经达到的技能.");
            }
        }
        if (itemId >= 2022570 && itemId <= 2022573 && itemId >= 2022575 && itemId <= 2022578 && itemId >= 2022580 && itemId <= 2022583 && c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            c.getPlayer().dropMessage(1, "背包有");
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static void UsePenguinBox(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final List<Integer> gift = new ArrayList<Integer>();
        final byte slot = (byte)slea.readShort();
        final int item = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse.getItemId() != item) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() <= 2) {
            c.getPlayer().dropMessage(1, "背包已满，无法获得物品\r\n装备栏最少留下3个空格");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() <= 2) {
            c.getPlayer().dropMessage(1, "背包已满\r\n消耗栏最少留下3个空格");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() <= 2) {
            c.getPlayer().dropMessage(1, "背包已满\r\n设置栏最少留下3个空格");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() <= 2) {
            c.getPlayer().dropMessage(1, "背包已满\r\n其他栏最少留下3个空格");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() <= 2) {
            c.getPlayer().dropMessage(1, "背包已满\r\n特殊栏最少留下3个空格");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final OtherSettings1 item_id = new OtherSettings1();
        final String[] itembp_A = item_id.getItempb_A();
        final String[] itembp_B = item_id.getItempb_B();
        final String[] itembp_C = item_id.getItempb_C();
        final String[] itembp_D = item_id.getItempb_D();
        final String[] itembp_E = item_id.getItempb_E();
        final int A = 0;
        final int B = 0;
        final int C = 0;
        final int D = 0;
        final int E = 0;
        switch (item) {
            case 2022570: {
                gift.add(1302119);
                gift.add(1312045);
                gift.add(1322073);
                break;
            }
            case 2022571: {
                gift.add(1372053);
                gift.add(1382070);
                break;
            }
            case 2022572: {
                gift.add(1462066);
                gift.add(1452073);
                break;
            }
            case 2022573: {
                gift.add(1332088);
                gift.add(1472089);
                break;
            }
            case 2022575: {
                gift.add(1040145);
                gift.add(1041148);
                break;
            }
            case 2022576: {
                gift.add(1050155);
                gift.add(1051191);
                break;
            }
            case 2022577: {
                gift.add(1040146);
                gift.add(1041149);
                break;
            }
            case 2022578: {
                gift.add(1040147);
                gift.add(1041150);
                break;
            }
            case 2022580: {
                gift.add(1072399);
                gift.add(1060134);
                gift.add(1061156);
                break;
            }
            case 2022581: {
                gift.add(1072400);
                break;
            }
            case 2022582: {
                gift.add(1072401);
                gift.add(1060135);
                gift.add(1061157);
                break;
            }
            case 2022583: {
                gift.add(1072402);
                gift.add(1060136);
                gift.add(1061158);
                break;
            }
            case 2022615: {
                NPCScriptManager.getInstance().start(c, Integer.parseInt(itembp_A[A]));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2022670: {
                NPCScriptManager.getInstance().start(c, Integer.parseInt(itembp_B[B]));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2022336: {
                NPCScriptManager.getInstance().start(c, Integer.parseInt(itembp_C[C]));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2022465: {
                NPCScriptManager.getInstance().start(c, Integer.parseInt(itembp_D[D]));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2022613: {
                NPCScriptManager.getInstance().start(c, Integer.parseInt(itembp_E[E]));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2290285: {
                gift.add(2290096);
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290014);
                gift.add(2290015);
                gift.add(2290006);
                gift.add(2290007);
                gift.add(2290016);
                gift.add(2290008);
                gift.add(2290010);
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290014);
                gift.add(2290015);
                gift.add(2290006);
                gift.add(2290007);
                gift.add(2290012);
                gift.add(2290018);
                gift.add(2290019);
                gift.add(2290020);
                gift.add(2290021);
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290022);
                gift.add(2290006);
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290038);
                gift.add(2290039);
                gift.add(2290040);
                gift.add(2290030);
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290032);
                gift.add(2290042);
                gift.add(2290043);
                gift.add(2290044);
                gift.add(2290045);
                gift.add(2290046);
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290034);
                gift.add(2290035);
                gift.add(2290050);
                gift.add(2290048);
                gift.add(2290052);
                gift.add(2290054);
                gift.add(2290055);
                gift.add(2290056);
                gift.add(2290057);
                gift.add(2290058);
                gift.add(2290059);
                gift.add(2290060);
                gift.add(2290062);
                gift.add(2290063);
                gift.add(2290064);
                gift.add(2290052);
                gift.add(2290054);
                gift.add(2290055);
                gift.add(2290066);
                gift.add(2290067);
                gift.add(2290058);
                gift.add(2290059);
                gift.add(2290068);
                gift.add(2290069);
                gift.add(2290070);
                gift.add(2290071);
                gift.add(2290072);
                gift.add(2290073);
                gift.add(2290074);
                gift.add(2290075);
                gift.add(2290076);
                gift.add(2290078);
                gift.add(2290079);
                gift.add(2290080);
                gift.add(2290081);
                gift.add(2290082);
                gift.add(2290083);
                gift.add(2290085);
                gift.add(2290086);
                gift.add(2290087);
                gift.add(2290088);
                gift.add(2290076);
                gift.add(2290078);
                gift.add(2290079);
                gift.add(2290080);
                gift.add(2290081);
                gift.add(2290082);
                gift.add(2290083);
                gift.add(2290090);
                gift.add(2290091);
                gift.add(2290092);
                gift.add(2290093);
                gift.add(2290094);
                gift.add(2290095);
                gift.add(2290097);
                gift.add(2290098);
                gift.add(2290099);
                gift.add(2290100);
                gift.add(2290101);
                gift.add(2290102);
                gift.add(2290103);
                gift.add(2290104);
                gift.add(2290105);
                gift.add(2290091);
                gift.add(2290108);
                gift.add(2290110);
                gift.add(2290111);
                gift.add(2290095);
                gift.add(2290106);
                gift.add(2290107);
                gift.add(2290112);
                gift.add(2290113);
                gift.add(2290124);
                gift.add(2290114);
                gift.add(2290117);
                gift.add(2290118);
                gift.add(2290123);
                gift.add(2290121);
                gift.add(2290122);
                gift.add(2290115);
                gift.add(2290116);
                gift.add(2290119);
                gift.add(2290120);
                gift.add(2290126);
                gift.add(2290127);
                gift.add(2290128);
                gift.add(2290129);
                gift.add(2290132);
                gift.add(2290133);
                gift.add(2290130);
                gift.add(2290131);
                gift.add(2290134);
                gift.add(2290135);
                gift.add(2290136);
                gift.add(2290137);
                gift.add(2290138);
                gift.add(2290139);
                break;
            }
            case 2290448: {
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290014);
                gift.add(2290015);
                gift.add(2290006);
                gift.add(2290007);
                gift.add(2290016);
                gift.add(2290008);
                gift.add(2290010);
                break;
            }
            case 2290449: {
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290014);
                gift.add(2290015);
                gift.add(2290006);
                gift.add(2290007);
                gift.add(2290012);
                gift.add(2290018);
                gift.add(2290019);
                gift.add(2290020);
                gift.add(2290021);
                break;
            }
            case 2290450: {
                gift.add(2290004);
                gift.add(2290005);
                gift.add(2290000);
                gift.add(2290001);
                gift.add(2290002);
                gift.add(2290003);
                gift.add(2290022);
                gift.add(2290006);
                break;
            }
            case 2290451: {
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290038);
                gift.add(2290039);
                gift.add(2290040);
                gift.add(2290030);
                break;
            }
            case 2290452: {
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290032);
                gift.add(2290042);
                gift.add(2290043);
                gift.add(2290044);
                gift.add(2290045);
                gift.add(2290046);
                break;
            }
            case 2290453: {
                gift.add(2290024);
                gift.add(2290025);
                gift.add(2290026);
                gift.add(2290027);
                gift.add(2290028);
                gift.add(2290029);
                gift.add(2290034);
                gift.add(2290035);
                gift.add(2290050);
                gift.add(2290048);
                break;
            }
            case 2290454: {
                gift.add(2290052);
                gift.add(2290054);
                gift.add(2290055);
                gift.add(2290056);
                gift.add(2290057);
                gift.add(2290058);
                gift.add(2290059);
                gift.add(2290060);
                gift.add(2290062);
                gift.add(2290063);
                gift.add(2290064);
                break;
            }
            case 2290455: {
                gift.add(2290052);
                gift.add(2290054);
                gift.add(2290055);
                gift.add(2290066);
                gift.add(2290067);
                gift.add(2290058);
                gift.add(2290059);
                gift.add(2290068);
                gift.add(2290069);
                gift.add(2290070);
                gift.add(2290071);
                gift.add(2290072);
                gift.add(2290073);
                gift.add(2290074);
                gift.add(2290075);
                break;
            }
            case 2290456: {
                gift.add(2290076);
                gift.add(2290078);
                gift.add(2290079);
                gift.add(2290080);
                gift.add(2290081);
                gift.add(2290082);
                gift.add(2290083);
                gift.add(2290085);
                gift.add(2290086);
                gift.add(2290087);
                gift.add(2290088);
                break;
            }
            case 2290457: {
                gift.add(2290076);
                gift.add(2290078);
                gift.add(2290079);
                gift.add(2290080);
                gift.add(2290081);
                gift.add(2290082);
                gift.add(2290083);
                gift.add(2290090);
                gift.add(2290091);
                gift.add(2290092);
                gift.add(2290093);
                gift.add(2290094);
                gift.add(2290095);
                break;
            }
            case 2290459: {
                gift.add(2290097);
                gift.add(2290098);
                gift.add(2290099);
                gift.add(2290100);
                gift.add(2290101);
                gift.add(2290102);
                gift.add(2290103);
                gift.add(2290104);
                gift.add(2290105);
                gift.add(2290091);
                gift.add(2290108);
                gift.add(2290110);
                gift.add(2290111);
                gift.add(2290095);
                gift.add(2290106);
                gift.add(2290107);
                break;
            }
            case 2290460: {
                gift.add(2290112);
                gift.add(2290113);
                gift.add(2290124);
                gift.add(2290114);
                gift.add(2290117);
                gift.add(2290118);
                gift.add(2290123);
                gift.add(2290121);
                gift.add(2290122);
                gift.add(2290115);
                gift.add(2290116);
                gift.add(2290119);
                gift.add(2290120);
                break;
            }
            case 2290462: {
                gift.add(2290126);
                gift.add(2290127);
                gift.add(2290128);
                gift.add(2290129);
                gift.add(2290132);
                gift.add(2290133);
                gift.add(2290130);
                gift.add(2290131);
                gift.add(2290134);
                gift.add(2290135);
                gift.add(2290136);
                gift.add(2290137);
                gift.add(2290138);
                gift.add(2290139);
                break;
            }
        }
        if (!gift.isEmpty()) {
            final int rand = ThreadLocalRandom.current().nextInt(gift.size());
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
            MapleInventoryManipulator.addById(c, gift.get(rand), (short)1, (byte)0);
            gift.clear();
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static void SunziBF(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readInt();
        final byte slot = (byte)slea.readShort();
        final int itemid = slea.readInt();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item == null || item.getItemId() != itemid || c.getPlayer().getLevel() > 255) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final int expGained = ii.getExpCache(itemid) * c.getChannelServer().getExpRate();
        c.getPlayer().gainExp(expGained, true, false, false);
        c.sendPacket(MaplePacketCreator.enableActions());
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
    }
    
    public static final void UseSummonBag(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive()) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (chr.getMapId() >= 910000000 && chr.getMapId() <= 910000022) {
            c.sendPacket(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(5, "市场无法使用召唤包.");
            return;
        }
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
            if (c.getPlayer().isGM() || !FieldLimitType.SummoningBag.check(chr.getMap().getFieldLimit())) {
                final List<Pair<Integer, Integer>> toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(itemId);
                if (toSpawn == null) {
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                final int type = 0;
                for (int i = 0; i < toSpawn.size(); ++i) {
                    if (Randomizer.nextInt(99) <= (toSpawn.get(i)).getRight()) {
                        final MapleMonster ht = MapleLifeFactory.getMonster((toSpawn.get(i)).getLeft().intValue());
                        if (ht.getId() == 9300166) {
                            chr.spawnBomb();
                        }
                        else {
                            chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                        }
                    }
                }
            }
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static void UseTreasureChest(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final short slot = slea.readShort();
        final int itemid = slea.readInt();
        final boolean useCash = slea.readByte() > 0;
        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte)slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid || chr.hasBlockedInventory()) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reward = 0;
        int keyIDforRemoval = 0;
        String box = null;
        String key = null;
        int price = 0;
        switch (toUse.getItemId()) {
            case 4280000: {
                reward = RandomRewards.getInstance().getGoldBoxReward();
                keyIDforRemoval = 5490000;
                box = "永恒的谜之蛋";
                key = "永恒的热度";
                price = 800;
                break;
            }
            case 4280001: {
                reward = RandomRewards.getInstance().getSilverBoxReward();
                keyIDforRemoval = 5490001;
                box = "重生的谜之蛋";
                key = "重生的热度";
                price = 500;
                break;
            }
            default: {
                return;
            }
        }
        int amount = 1;
        switch (reward) {
            case 2000004: {
                amount = 200;
                break;
            }
            case 2000005: {
                amount = 100;
                break;
            }
        }
        if (useCash && chr.getCSPoints(2) < price) {
            chr.dropMessage(1, "抵用券不足" + price + "点，请到商城购买“抵用券兑换包”即可充值抵用券！");
            c.announce(MaplePacketCreator.enableActions());
        }
        else if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) < 0) {
            chr.dropMessage(1, "孵化" + box + "需要" + key + "，请到商城购买！");
            c.announce(MaplePacketCreator.enableActions());
        }
        else if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) > 0 || (useCash && chr.getCSPoints(2) > price)) {
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short)amount);
            if (item == null) {
                chr.dropMessage(1, "孵化失败，请重试一次。");
                c.announce(MaplePacketCreator.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short)1, true);
            if (useCash) {
                chr.modifyCSPoints(2, -price, true);
            }
            else {
                MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);
            }
            c.announce(MaplePacketCreator.getShowItemGain(reward, (short)amount, true));
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), " : 从" + box + "中获得{" + ii.getName(item.getItemId()) + "}！大家一起恭喜他（她）吧！！！！", item, rareness, c.getChannel()));
            }
        }
        else {
            chr.dropMessage(5, "孵化" + box + "失败，进检查是否有" + key + "或者抵用卷大于" + price + "点。");
            c.announce(MaplePacketCreator.enableActions());
        }
    }
    
    public static final void UseCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        boolean used = false;
        boolean cc = false;
        Label_10426: {
            switch (itemId) {
                case 5042000: {
                    c.getPlayer().changeMap(701000200);
                    used = true;
                    break;
                }
                case 5042001: {
                    c.getPlayer().changeMap(741000000);
                    used = true;
                    break;
                }
                case 5043000:
                case 5043001: {
                    final short questid = slea.readShort();
                    final int npcid = slea.readInt();
                    final MapleQuest quest = MapleQuest.getInstance(questid);
                    if (c.getPlayer().getQuest(quest).getStatus() == 1 && quest.canComplete(c.getPlayer(), npcid)) {
                        final int mapId = MapleLifeFactory.getNPCLocation(npcid);
                        if (mapId != -1) {
                            final MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
                            if (map.containsNPC(npcid) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(map.getFieldLimit()) && c.getPlayer().getEventInstance() == null) {
                                c.getPlayer().changeMap(map, map.getPortal(0));
                            }
                            used = true;
                        }
                        else {
                            c.getPlayer().dropMessage(1, "发生未知错误.");
                        }
                        break;
                    }
                    break;
                }
                case 2320000:
                case 5040000:
                case 5040001:
                case 5041000: {
                    if (slea.readByte() == 0) {
                        final MapleMap target = c.getChannelServer().getMapFactory().getMap(slea.readInt());
                        if (target != null && ((itemId == 5041000 && c.getPlayer().isRockMap(target.getId())) || (itemId != 5041000 && c.getPlayer().isRegRockMap(target.getId()))) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && c.getPlayer().getEventInstance() == null) {
                            c.getPlayer().changeMap(target, target.getPortal(0));
                            used = true;
                        }
                        break;
                    }
                    final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                    if (victim != null && !victim.isGM() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && (itemId == 5041000 || victim.getMapId() / 100000000 == c.getPlayer().getMapId() / 100000000)) {
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                        used = true;
                    }
                    break;
                }
                case 5050000: {
                    final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
                    final int apto = slea.readInt();
                    final int apfrom = slea.readInt();
                    if (apto == apfrom) {
                        break;
                    }
                    final int job = c.getPlayer().getJob();
                    final PlayerStats playerst = c.getPlayer().getStat();
                    used = true;
                    if (apfrom == 8192 && apto != 32768) {
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    if (apfrom == 32768 && apto != 8192) {
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    switch (apto) {
                        case 256: {
                            if (playerst.getStr() >= 999) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 512: {
                            if (playerst.getDex() >= 999) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 1024: {
                            if (playerst.getInt() >= 999) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 2048: {
                            if (playerst.getLuk() >= 999) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 8192: {
                            if (playerst.getMaxHp() >= 30000) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 32768: {
                            if (playerst.getMaxMp() >= 30000) {
                                used = false;
                                break;
                            }
                            break;
                        }
                    }
                    switch (apfrom) {
                        case 256: {
                            if (playerst.getStr() <= 4) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 512: {
                            if (playerst.getDex() <= 4) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 1024: {
                            if (playerst.getInt() <= 4) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 2048: {
                            if (playerst.getLuk() <= 4) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 8192: {
                            if (playerst.getMaxHp() >= 30000) {
                                used = false;
                                break;
                            }
                            break;
                        }
                        case 32768: {
                            if (playerst.getMaxMp() >= 30000) {
                                used = false;
                                break;
                            }
                            break;
                        }
                    }
                    if (used) {
                        switch (apto) {
                            case 256: {
                                final int toSet = playerst.getStr() + 1;
                                playerst.setStr((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, toSet));
                                break;
                            }
                            case 512: {
                                final int toSet = playerst.getDex() + 1;
                                playerst.setDex((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, toSet));
                                break;
                            }
                            case 1024: {
                                final int toSet = playerst.getInt() + 1;
                                playerst.setInt((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, toSet));
                                break;
                            }
                            case 2048: {
                                final int toSet = playerst.getLuk() + 1;
                                playerst.setLuk((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, toSet));
                                break;
                            }
                            case 8192: {
                                short maxhp = playerst.getMaxHp();
                                if (job == 0) {
                                    maxhp += (short)Randomizer.rand(8, 12);
                                }
                                else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212)) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp += (short)Randomizer.rand(20, 25);
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp += (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if ((job >= 200 && job <= 232) || GameConstants.isEvan(job)) {
                                    maxhp += (short)Randomizer.rand(10, 20);
                                }
                                else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312)) {
                                    maxhp += (short)Randomizer.rand(16, 20);
                                }
                                else if ((job >= 500 && job <= 522) || (job >= 3500 && job <= 3512)) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp += (short)Randomizer.rand(18, 22);
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp += (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1500 && job <= 1512) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp += (short)Randomizer.rand(18, 22);
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp += (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1100 && job <= 1112) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp += (short)Randomizer.rand(36, 42);
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp += (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1200 && job <= 1212) {
                                    maxhp += (short)Randomizer.rand(15, 21);
                                }
                                else if (job >= 2000 && job <= 2112) {
                                    maxhp += (short)Randomizer.rand(40, 50);
                                }
                                else {
                                    maxhp += (short)Randomizer.rand(50, 100);
                                }
                                maxhp = (short)Math.min(30000, Math.abs(maxhp));
                                c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
                                playerst.setMaxHp(maxhp);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, (int)maxhp));
                                break;
                            }
                            case 32768: {
                                short maxmp = playerst.getMaxMp();
                                if (job == 0) {
                                    maxmp += (short)Randomizer.rand(6, 8);
                                }
                                else if (job >= 100 && job <= 132) {
                                    maxmp += (short)Randomizer.rand(5, 7);
                                }
                                else if ((job >= 200 && job <= 232) || GameConstants.isEvan(job) || (job >= 3200 && job <= 3212)) {
                                    final ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                                    final int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                    maxmp += (short)Randomizer.rand(18, 20);
                                    if (improvingMaxMPLevel >= 1) {
                                        maxmp += (short)(improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2);
                                    }
                                }
                                else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 522) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)) {
                                    maxmp += (short)Randomizer.rand(10, 12);
                                }
                                else if (job >= 1100 && job <= 1112) {
                                    maxmp += (short)Randomizer.rand(6, 9);
                                }
                                else if (job >= 1200 && job <= 1212) {
                                    final ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                                    final int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                    maxmp += (short)Randomizer.rand(18, 20);
                                    if (improvingMaxMPLevel >= 1) {
                                        maxmp += (short)(improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2);
                                    }
                                }
                                else if (job >= 2000 && job <= 2112) {
                                    maxmp += (short)Randomizer.rand(6, 9);
                                }
                                else {
                                    maxmp += (short)Randomizer.rand(50, 100);
                                }
                                maxmp = (short)Math.min(30000, Math.abs(maxmp));
                                c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
                                playerst.setMaxMp(maxmp);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, (int)maxmp));
                                break;
                            }
                        }
                        switch (apfrom) {
                            case 256: {
                                final int toSet = playerst.getStr() - 1;
                                playerst.setStr((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, toSet));
                                break;
                            }
                            case 512: {
                                final int toSet = playerst.getDex() - 1;
                                playerst.setDex((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, toSet));
                                break;
                            }
                            case 1024: {
                                final int toSet = playerst.getInt() - 1;
                                playerst.setInt((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, toSet));
                                break;
                            }
                            case 2048: {
                                final int toSet = playerst.getLuk() - 1;
                                playerst.setLuk((short)toSet);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, toSet));
                                break;
                            }
                            case 8192: {
                                short maxhp = playerst.getMaxHp();
                                if (job == 0) {
                                    maxhp -= 12;
                                }
                                else if (job >= 100 && job <= 132) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp -= 24;
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp -= (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 200 && job <= 232) {
                                    maxhp -= 10;
                                }
                                else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)) {
                                    maxhp -= 15;
                                }
                                else if (job >= 500 && job <= 522) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp -= 15;
                                    if (improvingMaxHPLevel > 0) {
                                        maxhp -= (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1500 && job <= 1512) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp -= 15;
                                    if (improvingMaxHPLevel > 0) {
                                        maxhp -= (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1100 && job <= 1112) {
                                    final ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                                    final int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                    maxhp -= 27;
                                    if (improvingMaxHPLevel >= 1) {
                                        maxhp -= (short)improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                    }
                                }
                                else if (job >= 1200 && job <= 1212) {
                                    maxhp -= 12;
                                }
                                else if ((job >= 2000 && job <= 2112) || (job >= 3200 && job <= 3212)) {
                                    maxhp -= 40;
                                }
                                else {
                                    maxhp -= 20;
                                }
                                c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() - 1));
                                playerst.setHp(maxhp);
                                playerst.setMaxHp(maxhp);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.HP, (int)maxhp));
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, (int)maxhp));
                                break;
                            }
                            case 32768: {
                                short maxmp = playerst.getMaxMp();
                                if (job == 0) {
                                    maxmp -= 8;
                                }
                                else if (job >= 100 && job <= 132) {
                                    maxmp -= 4;
                                }
                                else if (job >= 200 && job <= 232) {
                                    final ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                                    final int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                    maxmp -= 20;
                                    if (improvingMaxMPLevel >= 1) {
                                        maxmp -= (short)improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                                    }
                                }
                                else if ((job >= 500 && job <= 522) || (job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)) {
                                    maxmp -= 10;
                                }
                                else if (job >= 1100 && job <= 1112) {
                                    maxmp -= 6;
                                }
                                else if (job >= 1200 && job <= 1212) {
                                    final ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                                    final int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                    maxmp -= 25;
                                    if (improvingMaxMPLevel >= 1) {
                                        maxmp -= (short)improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                                    }
                                }
                                else if (job >= 2000 && job <= 2112) {
                                    maxmp -= 5;
                                }
                                else {
                                    maxmp -= 20;
                                }
                                c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() - 1));
                                playerst.setMp(maxmp);
                                playerst.setMaxMp(maxmp);
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MP, (int)maxmp));
                                statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, (int)maxmp));
                                break;
                            }
                        }
                        c.sendPacket(MaplePacketCreator.updatePlayerStats(statupdate, true, c.getPlayer().getJob()));
                        break;
                    }
                    break;
                }
                case 5050001:
                case 5050002:
                case 5050003:
                case 5050004: {
                    final int skill1 = slea.readInt();
                    final int skill2 = slea.readInt();
                    final ISkill skillSPTo = SkillFactory.getSkill(skill1);
                    final ISkill skillSPFrom = SkillFactory.getSkill(skill2);
                    if (skillSPTo.isBeginnerSkill()) {
                        break;
                    }
                    if (skillSPFrom.isBeginnerSkill()) {
                        break;
                    }
                    if (c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel() && c.getPlayer().getSkillLevel(skillSPFrom) > 0) {
                        c.getPlayer().changeSkillLevel(skillSPFrom, (byte)(c.getPlayer().getSkillLevel(skillSPFrom) - 1), c.getPlayer().getMasterLevel(skillSPFrom));
                        c.getPlayer().changeSkillLevel(skillSPTo, (byte)(c.getPlayer().getSkillLevel(skillSPTo) + 1), c.getPlayer().getMasterLevel(skillSPTo));
                        used = true;
                        break;
                    }
                    break;
                }
                case 5060000: {
                    Item 道具取名 = null;
                    final int 装备槽 = slea.readShort();
                    道具取名 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)装备槽);
                    c.sendPacket(MaplePacketCreator.serverNotice(5, "请将道具直接点在你需要刻名的装备上."));
                    if (道具取名 == null) {
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    道具取名.setOwner(c.getPlayer().getName());
                    c.sendPacket(MaplePacketCreator.updateEquipSlot(道具取名));
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                    c.sendPacket(MaplePacketCreator.serverNotice(5, "道具刻名成功~！"));
                    break;
                }
                case 5060001: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && item.getExpiration() == -1L) {
                        short flag = item.getFlag();
                        flag |= (short)ItemFlag.LOCK.getValue();
                        item.setFlag(flag);
                        c.getPlayer().forceReAddItem_Flag(item, type);
                        used = true;
                        break;
                    }
                    break;
                }
                case 5061000: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && item.getExpiration() == -1L) {
                        short flag = item.getFlag();
                        flag |= (short)ItemFlag.LOCK.getValue();
                        item.setFlag(flag);
                        item.setExpiration(System.currentTimeMillis() + 604800000L);
                        c.getPlayer().forceReAddItem_Flag(item, type);
                        used = true;
                        break;
                    }
                    break;
                }
                case 5061001: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && item.getExpiration() == -1L) {
                        short flag = item.getFlag();
                        flag |= (short)ItemFlag.LOCK.getValue();
                        item.setFlag((byte)flag);
                        long days = 0L;
                        switch (itemId) {
                            case 5061001: {
                                days = 30L;
                                break;
                            }
                        }
                        if (days > 0L) {
                            item.setExpiration(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
                        }
                        c.getPlayer().forceUpdateItem(type, item);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                        c.getPlayer().dropMessage(5, "使用封印之锁 物品ID: " + itemId + " 天数: " + days);
                        c.sendPacket(MaplePacketCreator.enableActions());
                        break;
                    }
                    c.getPlayer().dropMessage(1, "使用道具出现错误.");
                    break;
                }
                case 5061002: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && item.getExpiration() == -1L) {
                        short flag = item.getFlag();
                        flag |= (short)ItemFlag.LOCK.getValue();
                        item.setFlag((byte)flag);
                        long days = 0L;
                        switch (itemId) {
                            case 5061002: {
                                days = 90L;
                                break;
                            }
                        }
                        if (days > 0L) {
                            item.setExpiration(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
                        }
                        c.getPlayer().forceUpdateItem(type, item);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                        c.getPlayer().dropMessage(5, "使用封印之锁 物品ID: " + itemId + " 天数: " + days);
                        c.sendPacket(MaplePacketCreator.enableActions());
                        break;
                    }
                    c.getPlayer().dropMessage(1, "使用道具出现错误.");
                    break;
                }
                case 5061003: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && item.getExpiration() == -1L) {
                        short flag = item.getFlag();
                        flag |= (short)ItemFlag.LOCK.getValue();
                        item.setFlag((byte)flag);
                        long days = 0L;
                        switch (itemId) {
                            case 5061003: {
                                days = 365L;
                                break;
                            }
                        }
                        if (days > 0L) {
                            item.setExpiration(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
                        }
                        c.getPlayer().forceUpdateItem(type, item);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                        c.getPlayer().dropMessage(5, "使用封印之锁 物品ID: " + itemId + " 天数: " + days);
                        c.sendPacket(MaplePacketCreator.enableActions());
                        break;
                    }
                    c.getPlayer().dropMessage(1, "使用道具出现错误.");
                    break;
                }
                case 5062000: {
                    final Item item2 = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)slea.readInt());
                    if (item2 != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        final Equip eq = (Equip)item2;
                        if (eq.getState() >= 5) {
                            eq.renewPotential();
                            c.sendPacket(MaplePacketCreator.scrolledItem(toUse, item2, false, true));
                            c.getPlayer().forceReAddItem_NoUpdate(item2, MapleInventoryType.EQUIP);
                            MapleInventoryManipulator.addById(c, 2430112, (short)1, (byte)0);
                            used = true;
                        }
                        else {
                            c.getPlayer().dropMessage(5, "确保你的设备有潜力.");
                        }
                        break;
                    }
                    c.getPlayer().dropMessage(5, "确保你有一个片段的空间.");
                    break;
                }
                case 5080000:
                case 5080001:
                case 5080002:
                case 5080003: {
                    final MapleLove love = new MapleLove(c.getPlayer(), c.getPlayer().getPosition(), c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId(), slea.readMapleAsciiString(), itemId);
                    c.getPlayer().getMap().spawnLove(love);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                    break;
                }
                case 5520000: {
                    final MapleInventoryType type = MapleInventoryType.getByType((byte)slea.readInt());
                    final Item item = c.getPlayer().getInventory(type).getItem((short)slea.readInt());
                    if (item != null && !ItemFlag.KARMA_EQ.check(item.getFlag()) && !ItemFlag.KARMA_USE.check(item.getFlag()) && ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) || MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                        short flag = item.getFlag();
                        if (type == MapleInventoryType.EQUIP) {
                            flag = (byte)(flag | ItemFlag.KARMA_EQ.getValue());
                        }
                        else {
                            flag = (byte)(flag | ItemFlag.KARMA_USE.getValue());
                        }
                        item.setFlag(flag);
                        c.getPlayer().forceReAddItem_Flag(item, type);
                        used = true;
                        break;
                    }
                    break;
                }
                case 5570000: {
                    slea.readInt();
                    final Equip item3 = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte)slea.readInt());
                    if (item3 == null) {
                        break;
                    }
                    if (GameConstants.canHammer(item3.getItemId()) && MapleItemInformationProvider.getInstance().getSlots(item3.getItemId()) > 0 && item3.getViciousHammer() <= 2) {
                        item3.setViciousHammer((byte)(item3.getViciousHammer() + 1));
                        item3.setUpgradeSlots((byte)(item3.getUpgradeSlots() + 1));
                        c.getPlayer().forceReAddItem(item3, MapleInventoryType.EQUIP);
                        used = true;
                        cc = true;
                        break;
                    }
                    c.getPlayer().dropMessage(5, "你不得在这个物品上使用它.");
                    cc = true;
                    break;
                }
                case 5610000:
                case 5610001: {
                    slea.readInt();
                    final byte dst = (byte)slea.readInt();
                    slea.readInt();
                    final byte src = (byte)slea.readInt();
                    used = (cc = UseUpgradeScroll(src, dst, (byte)2, c, c.getPlayer(), itemId));
                    break;
                }
                case 5060003: {
                    final Item item2 = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(4170023);
                    if (item2 == null || item2.getQuantity() <= 0) {
                        return;
                    }
                    if (getIncubatedItems(c)) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item2.getPosition(), (short)1, false);
                        used = true;
                        break;
                    }
                    break;
                }
                case 5070000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "必须等级10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    c.getPlayer().gain喇叭();
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);
                    final boolean ear = slea.readByte() != 0;
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, sb.toString()));
                    used = true;
                    break;
                }
                case 5071000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    final boolean ear2 = slea.readByte() != 0;
                    final StringBuilder sb2 = new StringBuilder();
                    c.getPlayer().gain喇叭();
                    addMedalString(c.getPlayer(), sb2);
                    sb2.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb2.append(c.getPlayer().getName());
                    sb2.append(" : ");
                    sb2.append(message);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, sb2.toString()));
                    used = true;
                    break;
                }
                case 5077000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final byte numLines = slea.readByte();
                    if (numLines > 3) {
                        return;
                    }
                    final List<String> messages = new LinkedList<String>();
                    for (int i = 0; i < numLines; ++i) {
                        final String message2 = slea.readMapleAsciiString();
                        if (message2.length() > 65) {
                            break;
                        }
                        c.getPlayer().gain喇叭();
                        messages.add("[第" + c.getPlayer().get喇叭() + "次喇叭]" + c.getPlayer().getName() + " : " + message2);
                    }
                    final boolean ear3 = slea.readByte() > 0;
                    Broadcast.broadcastSmega(MaplePacketCreator.tripleSmega(messages, ear3, c.getChannel()).getBytes());
                    used = true;
                    break;
                }
                case 5073000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    c.getPlayer().gain喇叭();
                    sb.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);
                    final boolean ear = slea.readByte() != 0;
                    Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(11, c.getChannel(), sb.toString(), ear).getBytes());
                    System.out.println("[玩家喇叭信息 " + c.getPlayer().getName() + "] : " + message);
                    used = true;
                    break;
                }
                case 5074000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    c.getPlayer().gain喇叭();
                    sb.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);
                    final boolean ear = slea.readByte() != 0;
                    if ((c.getPlayer().isPlayer() && message.indexOf("幹") != -1) || message.indexOf("豬") != -1 || message.indexOf("笨") != -1 || message.indexOf("靠") != -1 || message.indexOf("腦包") != -1 || message.indexOf("腦") != -1 || message.indexOf("智障") != -1 || message.indexOf("白目") != -1 || message.indexOf("白吃") != -1) {
                        c.getPlayer().dropMessage("說髒話是不禮貌的，請勿說髒話。");
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(12, c.getChannel(), sb.toString(), ear).getBytes());
                    System.out.println("[玩家喇叭信息 " + c.getPlayer().getName() + "] : " + message);
                    used = true;
                    break;
                }
                case 5072000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10级以上才能使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    c.getPlayer().gain喇叭();
                    sb.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);
                    final boolean ear = slea.readByte() != 0;
                    Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, c.getChannel(), sb.toString(), ear).getBytes());
                    System.out.println("[玩家喇叭信息 " + c.getPlayer().getName() + "] : " + message);
                    used = true;
                    break;
                }
                case 5076000: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
                        break;
                    }
                    final String message = slea.readMapleAsciiString();
                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    c.getPlayer().gain喇叭();
                    sb.append("[第").append(c.getPlayer().get喇叭()).append("次喇叭]");
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);
                    final boolean ear = slea.readByte() > 0;
                    Item item4 = null;
                    if (slea.readByte() == 1) {
                        final byte invType = (byte)slea.readInt();
                        final byte pos = (byte)slea.readInt();
                        item4 = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                    }
                    Broadcast.broadcastSmega(MaplePacketCreator.itemMegaphone(sb.toString(), ear, c.getChannel(), item4).getBytes());
                    System.out.println("[玩家喇叭信息 " + c.getPlayer().getName() + "] : " + message);
                    used = true;
                    break;
                }
                case 5075000:
                case 5075001:
                case 5075002: {
                    c.getPlayer().dropMessage(5, "没有mapletvs广播消息.");
                    break;
                }
                case 5075003:
                case 5075004:
                case 5075005: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "必須等級10級以上才可以使用.");
                        break;
                    }
                    final int tvType = itemId % 10;
                    if (tvType == 3) {
                        slea.readByte();
                    }
                    final boolean ear2 = tvType != 1 && tvType != 2 && slea.readByte() > 1;
                    MapleCharacter victim2 = (tvType == 1 || tvType == 4) ? null : c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                    if (tvType == 0 || tvType == 3) {
                        victim2 = null;
                    }
                    else if (victim2 == null) {
                        c.getPlayer().dropMessage(1, "That character is not in the channel.");
                        break;
                    }
                    c.getPlayer().gain喇叭();
                    final String message3 = slea.readMapleAsciiString();
                    Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, c.getChannel(), "[第" + c.getPlayer().get喇叭() + "次喇叭]" + c.getPlayer().getName() + " : " + message3, ear2).getBytes());
                    break;
                }
                case 5090000:
                case 5090100: {
                    final String sendTo = slea.readMapleAsciiString();
                    final String msg = slea.readMapleAsciiString();
                    c.getPlayer().sendNote(sendTo, msg);
                    used = true;
                    break;
                }
                case 5152049:
                case 5152100:
                case 5152101:
                case 5152102:
                case 5152103:
                case 5152104:
                case 5152105:
                case 5152106:
                case 5152107: {
                    final MapleCharacter chr = c.getPlayer();
                    final int color = (itemId - 5152100) * 100;
                    if (chr.isGM()) {
                        System.out.println("使用一次性隐形眼镜 - 道具: " + itemId + " 颜色: " + color);
                    }
                    if (color >= 0) {
                        changeFace(chr, color);
                        used = true;
                        break;
                    }
                    chr.dropMessage(1, "使用一次性隐形眼镜出现错误.");
                    break;
                }
                case 5190000:
                case 5190001:
                case 5190002:
                case 5190003:
                case 5190004:
                case 5190005:
                case 5190006:
                case 5190007:
                case 5190008: {
                    final int uniqueid = (int)slea.readLong();
                    MaplePet pet = c.getPlayer().getPet(0);
                    int slo = 0;
                    if (pet == null) {
                        break;
                    }
                    if (pet.getUniqueId() != uniqueid) {
                        pet = c.getPlayer().getPet(1);
                        slo = 1;
                        if (pet == null) {
                            break;
                        }
                        if (pet.getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet == null) {
                                break;
                            }
                            if (pet.getUniqueId() != uniqueid) {
                                break;
                            }
                        }
                    }
                    final PetFlag zz = PetFlag.getByAddId(itemId);
                    if (zz != null && !zz.check(pet.getFlags())) {
                        pet.setFlags(pet.getFlags() | zz.getValue());
                        c.sendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition()), true));
                        c.sendPacket(MaplePacketCreator.enableActions());
                        c.sendPacket(MTSCSPacket.changePetFlag(uniqueid, true, zz.getValue()));
                        used = true;
                        break;
                    }
                    break;
                }
                case 5191000:
                case 5191001:
                case 5191002:
                case 5191003:
                case 5191004: {
                    final int uniqueid = (int)slea.readLong();
                    MaplePet pet = c.getPlayer().getPet(0);
                    int slo = 0;
                    if (pet == null) {
                        break;
                    }
                    if (pet.getUniqueId() != uniqueid) {
                        pet = c.getPlayer().getPet(1);
                        slo = 1;
                        if (pet == null) {
                            break;
                        }
                        if (pet.getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet == null) {
                                break;
                            }
                            if (pet.getUniqueId() != uniqueid) {
                                break;
                            }
                        }
                    }
                    final PetFlag zz = PetFlag.getByDelId(itemId);
                    if (zz != null && zz.check(pet.getFlags())) {
                        pet.setFlags(pet.getFlags() - zz.getValue());
                        c.sendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition()), true));
                        c.sendPacket(MaplePacketCreator.enableActions());
                        c.sendPacket(MTSCSPacket.changePetFlag(uniqueid, false, zz.getValue()));
                        used = true;
                        break;
                    }
                    break;
                }
                case 5170000: {
                    final MaplePet pet2 = c.getPlayer().getPet(0);
                    final int slo2 = 0;
                    if (pet2 == null) {
                        break;
                    }
                    final String nName = slea.readMapleAsciiString();
                    pet2.setName(nName);
                    c.sendPacket(PetPacket.updatePet(pet2, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet2.getInventoryPosition()), true));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    c.getPlayer().getMap().broadcastMessage(MTSCSPacket.changePetName(c.getPlayer(), nName, slo2));
                    used = true;
                    break;
                }
                case 5240000:
                case 5240001:
                case 5240002:
                case 5240003:
                case 5240004:
                case 5240005:
                case 5240006:
                case 5240007:
                case 5240008:
                case 5240009:
                case 5240010:
                case 5240011:
                case 5240012:
                case 5240013:
                case 5240014:
                case 5240015:
                case 5240016:
                case 5240017:
                case 5240018:
                case 5240019:
                case 5240020:
                case 5240021:
                case 5240022:
                case 5240023:
                case 5240024:
                case 5240025:
                case 5240026:
                case 5240027:
                case 5240028: {
                    MaplePet pet2 = c.getPlayer().getPet(0);
                    if (pet2 == null) {
                        break;
                    }
                    if (!pet2.canConsume(itemId)) {
                        pet2 = c.getPlayer().getPet(1);
                        if (pet2 == null) {
                            break;
                        }
                        if (!pet2.canConsume(itemId)) {
                            pet2 = c.getPlayer().getPet(2);
                            if (pet2 == null) {
                                break;
                            }
                            if (!pet2.canConsume(itemId)) {
                                break;
                            }
                        }
                    }
                    final byte petindex = c.getPlayer().getPetIndex(pet2);
                    pet2.setFullness(100);
                    if (pet2.getCloseness() < 30000) {
                        if (pet2.getCloseness() + 100 > 30000) {
                            pet2.setCloseness(30000);
                        }
                        else {
                            pet2.setCloseness(pet2.getCloseness() + 100);
                        }
                        if (pet2.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet2.getLevel() + 1)) {
                            pet2.setLevel(pet2.getLevel() + 1);
                            c.sendPacket(PetPacket.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet2)));
                            c.getPlayer().getMap().broadcastMessage(PetPacket.showPetLevelUp(c.getPlayer(), petindex));
                        }
                    }
                    c.sendPacket(PetPacket.updatePet(pet2, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet2.getInventoryPosition()), true));
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte)1, petindex, true, true), true);
                    used = true;
                    break;
                }
                case 5230000: {
                    final int itemSearch = slea.readInt();
                    final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
                    if (hms.size() > 0) {
                        c.sendPacket(MaplePacketCreator.getOwlSearched(itemSearch, hms));
                        used = true;
                        break;
                    }
                    c.getPlayer().dropMessage(1, "找不到物品");
                    break;
                }
                case 5280001:
                case 5281000:
                case 5281001: {
                    final Rectangle bounds = new Rectangle((int)c.getPlayer().getPosition().getX(), (int)c.getPlayer().getPosition().getY(), 1, 1);
                    final MapleMist mist = new MapleMist(bounds, c.getPlayer());
                    c.getPlayer().getMap().spawnMist(mist, 10000, true);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), "Oh no, I farted!", false, 1));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    used = true;
                    break;
                }
                case 5370000: {
                    if (c.getPlayer().getMapId() / 1000000 == 109) {
                        c.getPlayer().dropMessage(1, "请勿在活动地图使用黑板");
                        break;
                    }
                    c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                    break;
                }
                case 5370001: {
                    if (c.getPlayer().getMapId() / 1000000 == 109) {
                        c.getPlayer().dropMessage(1, "请勿在活动地图使用黑板");
                        break;
                    }
                    c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                    break;
                }
                case 5390000:
                case 5390001:
                case 5390002:
                case 5390003:
                case 5390004:
                case 5390005:
                case 5390006: {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(5, "必須等級10級以上才可以使用.");
                        break;
                    }
                    if (!c.getPlayer().getCheatTracker().canAvatarSmega2()) {
                        c.getPlayer().dropMessage(6, "很抱歉為了防止刷廣,所以你每10秒只能用一次.");
                        break;
                    }
                    if (c.getChannelServer().getMegaphoneMuteState()) {
                        break;
                    }
                    final String text = slea.readMapleAsciiString();
                    if (text.length() > 55) {
                        break;
                    }
                    final boolean ear2 = slea.readByte() != 0;
                    Broadcast.broadcastSmega(MaplePacketCreator.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, text, ear2).getBytes());
                    used = true;
                    break;
                }
                case 5450000: {
                    MapleShopFactory.getInstance().getShop(61).sendShop(c);
                    used = true;
                    break;
                }
                case 5530000: {
                    NPCScriptManager.getInstance().start(c, 9900007);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 0, true, false);
                    c.sendPacket(MaplePacketCreator.enableActions());
                    break;
                }
                case 5500001:
                case 5500002: {
                    final Item item2 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final int days2 = 20;
                    if (item2 != null && !GameConstants.isAccessory(item2.getItemId()) && item2.getExpiration() > -1L && !ii.isCash(item2.getItemId()) && System.currentTimeMillis() + 8640000000L > item2.getExpiration() + days2 * 24 * 60 * 60 * 1000L) {
                        boolean change = true;
                        for (final String z : GameConstants.RESERVED) {
                            if (c.getPlayer().getName().indexOf(z) != -1 || item2.getOwner().indexOf(z) != -1) {
                                change = false;
                            }
                        }
                        if (change) {
                            item2.setExpiration(item2.getExpiration() + days2 * 24 * 60 * 60 * 1000);
                            c.getPlayer().forceReAddItem(item2, MapleInventoryType.EQUIPPED);
                            used = true;
                        }
                        else {
                            c.getPlayer().dropMessage(1, "此装备无法使用.");
                        }
                        break;
                    }
                    break;
                }
                default: {
                    switch (itemId / 10000) {
                        case 512: {
                            final MapleItemInformationProvider ii2 = MapleItemInformationProvider.getInstance();
                            final String msg = ii2.getMsg(itemId).replaceFirst("%s", c.getPlayer().getName()).replaceFirst("%s", slea.readMapleAsciiString());
                            c.getPlayer().getMap().startMapEffect(msg, itemId);
                            final int buff = ii2.getStateChangeItem(itemId);
                            if (buff != 0) {
                                for (final MapleCharacter mChar : c.getPlayer().getMap().getCharactersThreadsafe()) {
                                    ii2.getItemEffect(buff).applyTo(mChar);
                                }
                            }
                            used = true;
                            break Label_10426;
                        }
                        case 510: {
                            c.getPlayer().getMap().startJukebox(c.getPlayer().getName(), itemId);
                            used = true;
                            break Label_10426;
                        }
                        case 520: {
                            final int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
                            if (mesars <= 0 || c.getPlayer().getMeso() >= Integer.MAX_VALUE - mesars) {
                                break Label_10426;
                            }
                            used = true;
                            if (Math.random() > 0.1) {
                                final int gainmes = Randomizer.nextInt(mesars);
                                c.getPlayer().gainMeso(gainmes, false);
                                c.sendPacket(MTSCSPacket.sendMesobagSuccess(gainmes));
                                break Label_10426;
                            }
                            c.sendPacket(MTSCSPacket.sendMesobagFailed());
                            break Label_10426;
                        }
                        case 553: {
                            UseRewardItem(slot, itemId, c, c.getPlayer());
                            break Label_10426;
                        }
                        default: {
                            System.out.println(slea.toString(true));
                            break Label_10426;
                        }
                    }
                }
            }
        }
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short)1, false, true);
        }
        c.sendPacket(MaplePacketCreator.enableActions());
        if (cc) {
            if (!c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null || FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit())) {
                c.getPlayer().dropMessage(1, "自动换频道失败.");
                return;
            }
            c.getPlayer().dropMessage(5, "自动换频道。请等待.");
            c.getPlayer().changeChannel((c.getChannel() == ChannelServer.getChannelCount()) ? 1 : (c.getChannel() + 1));
        }
    }
    
    public static final void Pickup_Player(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer().getPlayerShop() != null || c.getPlayer().getConversation() > 0 || c.getPlayer().getTrade() != null) {
            return;
        }
        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器重启中，目前无法捡取物品。");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        chr.updateTick(slea.readInt());
        slea.skip(1);
        final Point Client_Reportedpos = slea.readPos();
        if (chr == null) {
            return;
        }
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);
        if (ob == null) {
            c.sendPacket(MaplePacketCreator.getInventoryFull());
            c.sendPacket(MaplePacketCreator.getShowInventoryFull());
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem)ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 2500.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.全图吸物_客户端, String.valueOf(Distance));
            }
            else if (chr.getPosition().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.全图吸物_服务端);
            }
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    for (final MaplePartyCharacter z : chr.getParty().getMembers()) {
                        final MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null) {
                        	toGive.add(m);
                        }
                    }
                    for (final MapleCharacter i : toGive) {
                        i.gainMeso(mapitem.getMeso() / toGive.size() + (i.getStat().hasPartyBonus ? ((int)(mapitem.getMeso() / 20.0)) : 0), true, true);
                    }
                }
                else {
                    chr.gainMeso(mapitem.getMeso(), true, true);
                }
                removeItem(chr, mapitem, ob);
            }
            else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId())) {
                c.sendPacket(MaplePacketCreator.enableActions());
                c.getPlayer().dropMessage(5, "这个项目不能被选上.");
            }
            else if (useItem(c, mapitem.getItemId())) {
                removeItem(c.getPlayer(), mapitem, ob);
            }
            else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                if (mapitem.getItem().getQuantity() >= 50 && GameConstants.isUpgradeScroll(mapitem.getItem().getItemId())) {
                    c.setMonitored(true);
                }
                if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster)) {
                    removeItem(chr, mapitem, ob);
                }
            }
            else {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                c.sendPacket(MaplePacketCreator.getShowInventoryFull());
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    public static final void Pickup_Pet(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final byte petz = c.getPlayer().getPetIndex((int)slea.readLong());
        final MaplePet pet = chr.getPet(petz);
        slea.skip(1);
        chr.updateTick(slea.readInt());
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);
        if (ob == null || pet == null) {
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem)ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.sendPacket(MaplePacketCreator.getInventoryFull());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (mapitem.isPlayerDrop() && mapitem.getDropType() == 2 && mapitem.getOwner() == chr.getId()) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            if (mapitem.isPlayerDrop() && mapitem.getDropType() == 0 && mapitem.getOwner() == chr.getId() && mapitem.getMeso() != 0) {
                c.sendPacket(MaplePacketCreator.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 10000.0 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.宠物全图吸物_客户端, String.valueOf(Distance));
            }
            else if (pet.getPos().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.宠物全图吸物_服务端);
            }
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (final MaplePartyCharacter z : chr.getParty().getMembers()) {
                        final MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                        	toGive.add(m);
                        }
                    }
                    for (final MapleCharacter i : toGive) {
                        i.gainMeso(splitMeso / toGive.size() + (i.getStat().hasPartyBonus ? ((int)(mapitem.getMeso() / 20.0)) : 0), true);
                    }
                    chr.gainMeso(mapitem.getMeso() - splitMeso, true);
                }
                else {
                    chr.gainMeso(mapitem.getMeso(), true);
                }
                removeItem_Pet(chr, mapitem, petz);
            }
            else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                c.sendPacket(MaplePacketCreator.enableActions());
            }
            else if (useItem(c, mapitem.getItemId())) {
                removeItem_Pet(chr, mapitem, petz);
            }
            else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                    c.setMonitored(true);
                }
                MapleInventoryManipulator.pet_addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                removeItem_Pet(chr, mapitem, petz);
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final byte consumeval = ii.isConsumeOnPickup(id);
            if (consumeval > 0) {
                if (consumeval == 2) {
                    if (c.getPlayer().getParty() != null) {
                        for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                            if (chr != null) {
                                ii.getItemEffect(id).applyTo(chr);
                            }
                        }
                    }
                    else {
                        ii.getItemEffect(id).applyTo(c.getPlayer());
                    }
                }
                else {
                    ii.getItemEffect(id).applyTo(c.getPlayer());
                }
                c.sendPacket(MaplePacketCreator.getShowItemGain(id, (short)1));
                return true;
            }
        }
        return false;
    }
    
    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, final int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet), mapitem.getPosition());
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }
    
    private static final void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }
    
    private static final void addMedalString(final MapleCharacter c, final StringBuilder sb) {
        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-26));
        if (medal != null) {
            sb.append("<");
            sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            sb.append("> ");
        }
    }
    
    private static final boolean getIncubatedItems(final MapleClient c) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
            c.getPlayer().dropMessage(5, "请在您的库存中清理空间.");
            return false;
        }
        int[] ids;
        int[] chances;
        int z;
        for (ids = new int[] { 2430091, 2430092, 2430093, 2430101, 2430102, 2340000, 1152000, 1152001, 1152004, 1152005, 1152006, 1152007, 1152008, 1000040, 1102246, 1082276, 1050169, 1051210, 1072447, 1442106, 3010019, 1001060, 1002391, 1102004, 1050039, 1102040, 1102041, 1102042, 1102043, 1082145, 1082146, 1082147, 1082148, 1082149, 1082150, 2043704, 2040904, 2040409, 2040307, 2041030, 2040015, 2040109, 2041035, 2041036, 2040009, 2040511, 2040408, 2043804, 2044105, 2044903, 2044804, 2043009, 2043305, 2040610, 2040716, 2041037, 2043005, 2041032, 2040305, 2040211, 2040212, 1022097, 2049000, 2049001, 2049002, 2049003, 1012058, 1012059, 1012060, 1012061, 1332100, 1382058, 1402073, 1432066, 1442090, 1452058, 1462076, 1472069, 1482051, 1492024, 1342009, 2049400, 2049401, 2049301 }, chances = new int[] { 100, 100, 100, 100, 100, 1, 10, 10, 10, 10, 10, 10, 10, 5, 5, 5, 5, 5, 5, 5, 2, 10, 10, 10, 10, 10, 10, 10, 10, 5, 5, 5, 5, 5, 5, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 5, 5, 10, 10, 10, 10, 10, 5, 5, 5, 5, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2 }, z = Randomizer.nextInt(ids.length); chances[z] < Randomizer.nextInt(1000); z = Randomizer.nextInt(ids.length)) {}
        int z_2;
        for (z_2 = Randomizer.nextInt(ids.length); z_2 == z || chances[z_2] < Randomizer.nextInt(1000); z_2 = Randomizer.nextInt(ids.length)) {}
        c.sendPacket(MaplePacketCreator.getPeanutResult(ids[z], (short)1, ids[z_2], (short)1));
        return MapleInventoryManipulator.addById(c, ids[z], (short)1, (byte)0) && MapleInventoryManipulator.addById(c, ids[z_2], (short)1, (byte)0);
    }
    
    public static final void OwlMinerva(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.skip(6);
        final int itemId = slea.readInt();
        final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath", "wz") + "/String.wz"));
        final MapleData data = dataProvider.getData("Mob.img");
        final List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
        for (final MapleData mobIdData : data.getChildren()) {
            mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
        }
        final StringBuilder sb = new StringBuilder();
        try {
            final Connection con = DatabaseConnection.getConnection();
            try (final PreparedStatement ps = con.prepareStatement("SELECT * FROM monsterdrops WHERE itemid = ? ORDER BY monsterid")) {
                ps.setInt(1, itemId);
                final ResultSet rs = ps.executeQuery();
                int mobId = 0;
                while (rs.next()) {
                    if (mobId != rs.getInt("monsterid")) {
                        if (sb.length() > 10000) {
                            sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                            break;
                        }
                        mobId = rs.getInt("monsterid");
                        for (final Pair<Integer, String> mobPair : mobPairList) {
                            if (mobPair.getLeft() == mobId) {
                                if (c.getPlayer().isGM()) {
                                    sb.append("\r\n怪物名称：#e").append(mobPair.getRight()).append("#n(").append(mobPair.getLeft()).append(") 爆率：#e").append(rs.getInt("chance")).append("#n");
                                }
                                else {
                                    sb.append("\r\n怪物名称：#e").append(mobPair.getRight()).append("#n 爆率：#e").append(rs.getInt("chance") / 1000.0).append("%#n");
                                }
                            }
                        }
                    }
                }
                rs.close();
                ps.close();
            }
        }
        catch (SQLException ex) {}
        if (sb.length() > 0) {
            c.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, "搜索完成, 有以下怪物爆此物品：\r\n" + sb.toString(), "00 00", (byte)0));
        }
        else {
            c.getPlayer().dropMessage(1, "没有怪物爆此物品。");
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
    
    public static final void Owl(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().haveItem(5230000, 1, true, true) || c.getPlayer().haveItem(2310000, 1, true, true)) {
            if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
                c.sendPacket(MaplePacketCreator.getOwlOpen());
                System.out.println("TEST");
            }
            else {
                c.getPlayer().dropMessage(5, "只能在自由市場使用");
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
    }
    
    public static final void UseSkillBook(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(4);
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            return;
        }
        final Map skilldata = MapleItemInformationProvider.getInstance().getSkillStats(toUse.getItemId());
        if (skilldata == null) {
            return;
        }
        boolean canuse = false;
        boolean success = false;
        final int skill = 0;
        final int maxlevel = 0;
        final int SuccessRate = (int)skilldata.get("success");
        final int ReqSkillLevel = (int)skilldata.get("reqSkillLevel");
        final int MasterLevel = (int)skilldata.get("masterLevel");
        byte i = 0;
        while (true) {
            final Integer CurrentLoopedSkillId = (Integer)skilldata.get("skillid" + i);
            ++i;
            if (CurrentLoopedSkillId == null) {
                break;
            }
            if (Math.floor(CurrentLoopedSkillId / 10000) != chr.getJob()) {
                continue;
            }
            final ISkill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (Randomizer.nextInt(99) <= SuccessRate && SuccessRate != 0) {
                    success = true;
                    final ISkill skill2 = CurrSkillData;
                    chr.changeSkillLevel(skill2, chr.getSkillLevel(skill2), (byte)MasterLevel);
                }
                else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                break;
            }
            canuse = false;
        }
        c.sendPacket(MaplePacketCreator.useSkillBook(chr, skill, maxlevel, canuse, success));
    }
    
    public static final void OwlWarp(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.sendPacket(MaplePacketCreator.enableActions());
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022 && c.getPlayer().getPlayerShop() == null) {
            final int id = slea.readInt();
            final int map = slea.readInt();
            if (map >= 910000001 && map <= 910000022) {
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                switch (2) {
                    case 0: {
                        final List<MapleMapObject> objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (final MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop)ob;
                                if (!(ips instanceof HiredMerchant)) {
                                    continue;
                                }
                                final HiredMerchant merch = (HiredMerchant)ips;
                                if (merch.getOwnerId() == id) {
                                    merchant = merch;
                                    break;
                                }
                                continue;
                            }
                        }
                        break;
                    }
                    case 1: {
                        final List<MapleMapObject> objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (final MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop)ob;
                                if (!(ips instanceof HiredMerchant)) {
                                    continue;
                                }
                                final HiredMerchant merch = (HiredMerchant)ips;
                                if (merch.getStoreId() == id) {
                                    merchant = merch;
                                    break;
                                }
                                continue;
                            }
                        }
                        break;
                    }
                    default: {
                        final MapleMapObject ob2 = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (!(ob2 instanceof IMaplePlayerShop)) {
                            break;
                        }
                        final IMaplePlayerShop ips2 = (IMaplePlayerShop)ob2;
                        if (ips2 instanceof HiredMerchant) {
                            merchant = (HiredMerchant)ips2;
                            break;
                        }
                        break;
                    }
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors(16, 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.sendPacket(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    }
                    else if (!merchant.isOpen() || !merchant.isAvailable()) {
                        c.getPlayer().dropMessage(1, "This shop is in maintenance, please come by later.");
                    }
                    else if (merchant.getFreeSlot() == -1) {
                        c.getPlayer().dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                    }
                    else if (merchant.isInBlackList(c.getPlayer().getName())) {
                        c.getPlayer().dropMessage(1, "You have been banned from this store.");
                    }
                    else {
                        c.getPlayer().setPlayerShop(merchant);
                        merchant.addVisitor(c.getPlayer());
                        c.sendPacket(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    }
                }
                else {
                    c.getPlayer().dropMessage(1, "This shop is in maintenance, please come by later.");
                }
            }
        }
    }
    
    private static void changeFace(final MapleCharacter player, final int color) {
        if (player.getFace() % 1000 < 100) {
            player.setFace(player.getFace() + color);
        }
        else if (player.getFace() % 1000 >= 100 && player.getFace() % 1000 < 200) {
            player.setFace(player.getFace() - 100 + color);
        }
        else if (player.getFace() % 1000 >= 200 && player.getFace() % 1000 < 300) {
            player.setFace(player.getFace() - 200 + color);
        }
        else if (player.getFace() % 1000 >= 300 && player.getFace() % 1000 < 400) {
            player.setFace(player.getFace() - 300 + color);
        }
        else if (player.getFace() % 1000 >= 400 && player.getFace() % 1000 < 500) {
            player.setFace(player.getFace() - 400 + color);
        }
        else if (player.getFace() % 1000 >= 500 && player.getFace() % 1000 < 600) {
            player.setFace(player.getFace() - 500 + color);
        }
        else if (player.getFace() % 1000 >= 600 && player.getFace() % 1000 < 700) {
            player.setFace(player.getFace() - 600 + color);
        }
        else if (player.getFace() % 1000 >= 700 && player.getFace() % 1000 < 800) {
            player.setFace(player.getFace() - 700 + color);
        }
        player.updateSingleStat(MapleStat.FACE, player.getFace());
        player.equipChanged();
    }
}
