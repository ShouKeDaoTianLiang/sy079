package handling.channel.handler;

import client.RockPaperScissors;
import client.MapleQuestStatus;
import tools.Pair;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import client.inventory.Equip;
import tools.ArrayMap;
import scripting.NPCConversationManager;
import client.inventory.Item;
import server.MapleStorage;
import client.inventory.ItemFlag;
import server.MapleInventoryManipulator;
import tools.FileoutputUtil;
import client.inventory.MapleInventoryType;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import server.MapleShop;
import constants.GameConstants;
import client.MapleCharacter;
import server.life.MapleNPC;
import server.maps.MapleMap;
import handling.SendPacketOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import server.maps.MapleMapObject;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class NPCHandler
{
    public static final void NPCAnimation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int length = (int)slea.available();
        if (length < 4) {
            return;
        }
        final MapleMap map = c.getPlayer().getMap();
        if (map == null) {
            return;
        }
        final int oid = slea.readInt();
        final MapleNPC npc = map.getNPCByOid(oid);
        if (npc == null) {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage("NPC OID =" + oid);
            }
            return;
        }
        switch (npc.getId()) {
            case 2103:
            case 10000:
            case 1010100:
            case 1012003:
            case 1012106:
            case 1032004:
            case 1052103:
            case 1061100: {}
            default: {
                if (!c.getPlayer().isMapObjectVisible(npc)) {
                    return;
                }
                final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
                mplew.writeInt(oid);
                if (length == 6) {
                    mplew.writeShort(slea.readShort());
                }
                else {
                    if (length <= 9) {
                        if (c.getPlayer().isAdmin()) {
                            c.getPlayer().dropMessage("NPC, Packet:" + slea.toString());
                        }
                        return;
                    }
                    mplew.write(slea.read(length - 13));
                }
                c.sendPacket(mplew.getPacket());
            }
        }
    }
    
    public static void NPCShop(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte bmode = slea.readByte();
        if (chr == null) {
            return;
        }
        switch (bmode) {
            case 0: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                slea.skip(2);
                final int itemId = slea.readInt();
                final short quantity = slea.readShort();
                shop.buy(c, itemId, quantity);
                break;
            }
            case 1: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                final byte slot = (byte)slea.readShort();
                final int itemId2 = slea.readInt();
                final short quantity2 = slea.readShort();
                shop.sell(c, GameConstants.getInventoryType(itemId2), slot, quantity2);
                break;
            }
            case 2: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                final byte slot = (byte)slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            default: {
                chr.setConversation(0);
                break;
            }
        }
    }
    
    public static void NPCTalk(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleNPC npc = chr.getMap().getNPCByOid(slea.readInt());
        slea.readInt();
        if (npc == null) {
            return;
        }
        if (chr.getConversation() != 0) {
            chr.dropMessage(5, "你已经假死,请点击右下角聊天解除假死");
            return;
        }
        if (npc.hasShop()) {
            c.sendPacket(MaplePacketCreator.confirmShopTransaction((byte)20));
            chr.setConversation(1);
            npc.sendShop(c);
        }
        else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
    }
    
    public static final void QuestAction(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte action = slea.readByte();
        short quest = slea.readShort();
        if (quest < 0) {
            quest += 65536;
        }
        if (chr == null) {
            return;
        }
        if (!chr.canQuestAction()) {
            chr.dropMessage(1, "提交操作过快请稍后！");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: {
                chr.updateTick(slea.readInt());
                final int itemid = slea.readInt();
                MapleQuest.getInstance(quest).RestoreLostItem(chr, itemid);
                break;
            }
            case 1: {
                final int npc = slea.readInt();
                q.start(chr, npc);
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage("开始任务[" + quest + "] NPC: " + npc);
                    break;
                }
                break;
            }
            case 2: {
                final int npc = slea.readInt();
                chr.updateTick(slea.readInt());
                if (slea.available() >= 4L) {
                    q.complete(chr, npc, slea.readInt());
                }
                else {
                    q.complete(chr, npc);
                }
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage("完成任务[" + quest + "] NPC: " + npc);
                    break;
                }
                break;
            }
            case 3: {
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                    break;
                }
                chr.dropMessage(1, "You may not forfeit this quest.");
                break;
            }
            case 4: {
                final int npc = slea.readInt();
                slea.readInt();
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage("脚本开始任务[" + quest + "] NPC: " + npc);
                    break;
                }
                break;
            }
            case 5: {
                final int npc = slea.readInt();
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.getPlayer().getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(0, 10));
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showBuffeffect(c.getPlayer().getId(), 0, 10, (byte)3), false);
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage("脚本完成任务[" + quest + "] NPC: " + npc);
                    break;
                }
                break;
            }
        }
    }
    
    public static final void Storage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte mode = slea.readByte();
        if (chr == null) {
            return;
        }
        final MapleStorage storage = chr.getStorage();
        final String logsss = "IP[" + c.getSession().getRemoteAddress() + "]account[(" + c.getAccID() + ")(" + c.getAccountName() + ")] | character[(" + c.getPlayer().getId() + ")(" + c.getPlayer().getName() + ")]";
        switch (mode) {
            case 4: {
                final byte type = slea.readByte();
                final byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
                final Item item = storage.takeOut(slot);
                if (ii.isCash(item.getItemId())) {
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                if (item != null) {
                    FileoutputUtil.log("仓库日志.txt", logsss + " 取_OK[(" + item.getItemId() + ")(" + ii.getName(item.getItemId()) + ")(" + item.getQuantity() + ")]");
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "你的物品栏已经满了..");
                        FileoutputUtil.log("仓库日志.txt", logsss + " 取_包满[(" + item.getItemId() + ")(" + ii.getName(item.getItemId()) + ")(" + item.getQuantity() + ")]");
                    }
                    else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                    }
                    storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    break;
                }
                FileoutputUtil.log("仓库日志.txt", logsss + " 取_null[(" + item.getItemId() + ")(" + ii.getName(item.getItemId()) + ")(" + item.getQuantity() + ")]");
                c.getSession().close();
            }
            case 5: {
                final byte slot2 = (byte)slea.readShort();
                final int itemId = slea.readInt();
                short quantity = slea.readShort();
                if (c.getPlayer().getGMLevel() == 1) {
                    c.getPlayer().dropMessage("肥水不流外人田。所以你的东西不存库。。。");
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                if (chr.getMeso() < 500) {
                    c.getPlayer().dropMessage("你的钱不够哦。。。");
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                if (quantity < 1) {
                    c.getSession().close();
                    FileoutputUtil.log("仓库日志.txt", logsss + " 存_数量少于1[(" + itemId + ")(" + ii.getName(itemId) + ")(" + quantity + ")]");
                    return;
                }
                if (storage.isFull()) {
                    c.sendPacket(MaplePacketCreator.getStorageFull());
                    FileoutputUtil.log("仓库日志.txt", logsss + " 存_仓库满[(" + itemId + ")(" + ii.getName(itemId) + ")(" + quantity + ")]");
                    return;
                }
                final MapleInventoryType type2 = ii.getInventoryType(itemId);
                if (c.getPlayer().getInventory(type2).getItem(slot2) == null) {
                    c.sendPacket(MaplePacketCreator.enableActions());
                    FileoutputUtil.log("仓库日志.txt", logsss + " 存_身上null[(" + itemId + ")(" + ii.getName(itemId) + ")(" + quantity + ")]");
                    return;
                }
                if (chr.getMeso() < 100) {
                    chr.dropMessage(1, "你沒有足够的金币买这个道具.");
                    FileoutputUtil.log("仓库日志.txt", logsss + " 存_金币不足100[(" + itemId + ")(" + ii.getName(itemId) + ")(" + quantity + ")]");
                }
                else {
                    final MapleInventoryType type3 = GameConstants.getInventoryType(itemId);
                    final Item item2 = chr.getInventory(type3).getItem(slot2).copy();
                    if (ii.isCash(item2.getItemId())) {
                        c.getPlayer().dropMessage("该道具无法存储到仓库cash。");
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    if (GameConstants.isPet(item2.getItemId())) {
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    final short flag = item2.getFlag();
                    if (ii.isPickupRestricted(item2.getItemId()) && storage.findById(item2.getItemId()) != null) {
                        c.sendPacket(MaplePacketCreator.enableActions());
                        return;
                    }
                    if (item2.getItemId() != itemId || (item2.getQuantity() < quantity && !GameConstants.isThrowingStar(itemId) && !GameConstants.isBullet(itemId))) {
                        FileoutputUtil.log("仓库日志.txt", logsss + " 存_error[(" + itemId + ")(" + ii.getName(itemId) + ")(" + quantity + ")||(" + item2.getItemId() + ")(" + ii.getName(item2.getItemId()) + ")(" + item2.getQuantity() + ")]");
                        c.getSession().close();
                        return;
                    }
                    if (ii.isDropRestricted(item2.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item2.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                        }
                        else {
                            if (!ItemFlag.KARMA_USE.check(flag)) {
                                c.sendPacket(MaplePacketCreator.enableActions());
                                return;
                            }
                            item2.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                        }
                    }
                    if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                        quantity = item2.getQuantity();
                    }
                    chr.gainMeso(-100, false, true, false);
                    MapleInventoryManipulator.removeFromSlot(c, type3, slot2, quantity, false);
                    item2.setQuantity(quantity);
                    FileoutputUtil.log("仓库日志.txt", logsss + " 存_OK[(" + item2.getItemId() + ")(" + ii.getName(item2.getItemId()) + ")(" + item2.getQuantity() + ")]");
                    storage.store(item2);
                }
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            }
            case 7: {
                int meso = slea.readInt();
                final int storageMesos = storage.getMeso();
                final int playerMesos = chr.getMeso();
                if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
                    if (meso < 0 && storageMesos - meso < 0) {
                        meso = -(Integer.MAX_VALUE - storageMesos);
                        if (-meso > playerMesos) {
                            return;
                        }
                    }
                    else if (meso > 0 && playerMesos + meso < 0) {
                        meso = Integer.MAX_VALUE - playerMesos;
                        if (meso > storageMesos) {
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, true, false);
                    FileoutputUtil.log("仓库日志.txt", logsss + " 金币_ok[storageMesos(" + storageMesos + ") meso(" + meso + ")]");
                    storage.sendMeso(c);
                    break;
                }
                FileoutputUtil.log("仓库日志.txt", logsss + " 金币_error[storageMesos(" + storageMesos + ") meso(" + meso + ")]");
                c.getSession().close();
            }
            case 8: {
                storage.close();
                FileoutputUtil.log("仓库日志.txt", logsss + " 关闭");
                chr.setConversation(0);
                break;
            }
        }
    }
    
    public static final void MarrageNpc(final MapleClient c) {
        if (c != null && c.getPlayer() != null && c.getPlayer().getMapId() == 700000100) {
            c.getPlayer().changeMap(700000200);
        }
    }
    
    public static final void NPCMoreTalk(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte lastMsg = slea.readByte();
        final byte action = slea.readByte();
        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if (cm == null || c.getPlayer().getConversation() == 0 || cm.getLastMsg() != lastMsg) {
            return;
        }
        cm.setLastMsg((byte)(-1));
        if (lastMsg == 2) {
            if (action != 0) {
                cm.setGetText(slea.readMapleAsciiString());
                switch (cm.getType()) {
                    case 0: {
                        NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                        break;
                    }
                    case 1: {
                        NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                        break;
                    }
                    default: {
                        NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                        break;
                    }
                }
            }
            else {
                cm.dispose();
            }
        }
        else {
            int selection = -1;
            if (slea.available() >= 4L) {
                selection = slea.readInt();
            }
            else if (slea.available() > 0L) {
                selection = slea.readByte();
            }
            if (lastMsg == 4 && selection == -1) {
                cm.dispose();
                return;
            }
            if (selection >= -1 && action != -1) {
                switch (cm.getType()) {
                    case 0: {
                        NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                        break;
                    }
                    case 1: {
                        NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                        break;
                    }
                    default: {
                        NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                        break;
                    }
                }
            }
            else {
                cm.dispose();
            }
        }
    }
    
    public static final void repairAll(final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000) {
            return;
        }
        int price = 0;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<Equip, Integer> eqs = new ArrayMap<Equip, Integer>();
        final MapleInventoryType[] array;
        final MapleInventoryType[] types = array = new MapleInventoryType[] { MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED };
        for (final MapleInventoryType type : array) {
            for (final Item item : c.getPlayer().getInventory(type)) {
                if (item instanceof Equip) {
                    final Equip eq = (Equip)item;
                    if (eq.getDurability() < 0) {
                        continue;
                    }
                    final Map<String, Integer> eqStats = ii.getEquipStats(eq.getItemId());
                    if (eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
                        continue;
                    }
                    final double rPercentage = 100.0 - Math.ceil(eq.getDurability() * 1000.0 / (eqStats.get("durability") * 10.0));
                    eqs.put(eq, eqStats.get("durability"));
                    price += (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / ((ii.getReqLevel(eq.getItemId()) < 70) ? 100.0 : 1.0));
                }
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        for (final Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            final Equip ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), (ez.getPosition() < 0) ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }
    
    public static final void repair(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000 || slea.available() < 4L) {
            return;
        }
        final int position = slea.readInt();
        final MapleInventoryType type = (position < 0) ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        final Item item = c.getPlayer().getInventory(type).getItem((short)position);
        if (item == null) {
            return;
        }
        final Equip eq = (Equip)item;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        final double rPercentage = 100.0 - Math.ceil(eq.getDurability() * 1000.0 / (eqStats.get("durability") * 10.0));
        final int price = (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / ((ii.getReqLevel(eq.getItemId()) < 70) ? 100.0 : 1.0));
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }
    
    public static final void UpdateQuest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }
    
    public static final void UseItemQuest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final short slot = slea.readShort();
        final int itemId = slea.readInt();
        final Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        final short qid = slea.readShort();
        slea.readShort();
        final MapleQuest quest = MapleQuest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (final Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break;
                }
                continue;
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = slea.readInt();
            final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short)1, false);
            }
        }
    }
    
    public static final void RPSGame(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() == 0L || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        final byte mode = slea.readByte();
        switch (mode) {
            case 0:
            case 5: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                    break;
                }
                c.sendPacket(MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
                break;
            }
            case 1: {
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().answer(c, slea.readByte())) {
                    c.sendPacket(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                    break;
                }
                break;
            }
            case 2: {
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().timeOut(c)) {
                    c.sendPacket(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                    break;
                }
                break;
            }
            case 3: {
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().nextRound(c)) {
                    c.sendPacket(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                    break;
                }
                break;
            }
            case 4: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                    break;
                }
                c.sendPacket(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
        }
    }
}
