package handling.cashshop.handler;

import java.util.Iterator;
import java.util.List;
import client.inventory.MaplePet;
import handling.world.World.Find;
import client.inventory.MapleRing;
import client.inventory.MapleInventoryIdentifier;
import tools.Pair;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import tools.FileoutputUtil;
import server.MapleItemInformationProvider;
import constants.OtherSettings;
import server.CashItemInfo;
import java.util.Map;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.CashItemFactory;
import client.inventory.Item;
import java.util.HashMap;
import java.sql.SQLException;
import client.MapleCharacterUtil;
import tools.packet.MTSCSPacket;
import constants.ServerConstants;
import java.net.UnknownHostException;
import tools.MaplePacketCreator;
import java.net.InetAddress;
import handling.world.World;
import handling.world.CharacterTransfer;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import tools.FileoutputUtil1;
import handling.channel.ChannelServer;
import client.MapleCharacter;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class CashShopOperation
{
    public static void LeaveCS(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final int channel = c.getChannel();
        final ChannelServer toch = ChannelServer.getInstance(channel);
        final String Trade = "[玩家] " + chr.getName() + " 从商城离开发生错误.找不到频道[" + channel + "]的信息.\r\n\r\n";
        if (toch == null) {
            System.out.println("玩家: " + chr.getName() + " 从商城离开发生错误.找不到频道[" + channel + "]的信息.");
            FileoutputUtil1.离开商城("" + chr.getName() + "商城.txt", Trade);
            c.getSession().close();
            return;
        }
        final String[] socket = c.getChannelServer().getIP().split(":");
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        final String ip = c.getSessionIPAddress();
        LoginServer.putLoginAuth(chr.getId(), ip.substring(ip.indexOf(47) + 1, ip.length()), c.getTempIP(), channel);
        c.updateLoginState(1, ip);
        try {
            chr.saveToDB(false, true);
            c.setReceiving(false);
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), channel);
            c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(ChannelServer.getInstance(channel).getIP().split(":")[1])));
        }
        catch (UnknownHostException | NumberFormatException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    public static void 进入商城(final int playerid, final MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                c.getSession().close();
                return;
            }
        }
        final MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) {
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if ((state == 1 || state == 6) && !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        if (ServerConstants.get商城开关()) {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write(MTSCSPacket.warpCSS(c));
            CSUpdate(c);
        }
        else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write(MTSCSPacket.warpCS(c));
            CSUpdate(c);
        }
    }
    
    public static void CSUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashInventory(c));
        c.sendPacket(MTSCSPacket.sendWishList(c.getPlayer(), false));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.getCSGifts(c));
    }
    
    public static void TouchingCashShop(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
    }
    
    public static void CouponCode(final String code, final MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;
        try {
            validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (validcode) {
            try {
                type = MapleCharacterUtil.getNXCodeType(code);
                item = MapleCharacterUtil.getNXCodeItem(code);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            if (type != 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            final Map<Integer, Item> itemz = new HashMap<Integer, Item>();
            int maplePoints = 0;
            int mesos = 0;
            switch (type) {
                case 1:
                case 2: {
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                }
                case 3: {
                    final CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write(MTSCSPacket.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    }
                    final byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short)1, "", (byte)0);
                    if (slot <= -1) {
                        c.getSession().write(MTSCSPacket.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    }
                    itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    break;
                }
                case 4: {
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    break;
                }
                case 5: {
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
                }
            }
            c.getSession().write(MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        }
        else {
            c.getSession().write(MTSCSPacket.sendCSFail(validcode ? 165 : 167));
        }
        doCSPackets(c);
    }
    
    public static void BuyCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final OtherSettings item_id = new OtherSettings();
        final String[] itembp_id = item_id.getItempb_id();
        final String[] itemjy_id = item_id.getItemjy_id();
        final int action = slea.readByte();
        switch (action) {
            case 3: {
                final int useNX = slea.readByte() + 1;
                final int snCS = slea.readInt();
                final CashItemInfo item = CashItemFactory.getInstance().getItem(snCS);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (item != null) {
                    FileoutputUtil1.printError("" + chr.getName() + "商城购买.txt", "玩家: " + chr.getName() + " 物品名称: " + ii.getName(item.getId()) + " 物品代码: " + item.getId() + " 物品价格:" + item.getPrice() + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
                }
                if (item == null) {
                    chr.dropMessage(1, "该物品暂未开放！");
                    doCSPackets(c);
                    return;
                }
                if (item.getId() >= 5010000 && item.getId() <= 5010070) {
                    chr.dropMessage(1, "该物品暂未开放！");
                    doCSPackets(c);
                    return;
                }
                for (int i = 0; i < itembp_id.length; ++i) {
                    if (item.getId() == Integer.parseInt(itembp_id[i])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (item.getPrice() < 100) {
                    c.getPlayer().dropMessage(1, "价格(" + item.getPrice() + ")低于100点卷的物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
                if (item != null && chr.getCSPoints(useNX) >= item.getPrice()) {
                    chr.modifyCSPoints(useNX, -item.getPrice(), false);
                    final Item itemz = chr.getCashInventory().toItem(item);
                    if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                        if (useNX == 1) {
                            short flag = itemz.getFlag();
                            boolean 交易 = true;
                            for (int j = 0; j < itemjy_id.length; ++j) {
                                if (itemz.getItemId() == Integer.parseInt(itemjy_id[j])) {
                                    交易 = false;
                                }
                            }
                            if (交易) {
                                if (itemz.getType() == MapleInventoryType.EQUIP.getType()) {
                                    flag |= (short)ItemFlag.KARMA_EQ.getValue();
                                }
                                else {
                                    flag |= (short)ItemFlag.KARMA_USE.getValue();
                                }
                                itemz.setFlag(flag);
                            }
                        }
                        chr.getCashInventory().addToInventory(itemz);
                        c.sendPacket(MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                    }
                    else {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                    }
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                }
                c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 4:
            case 32: {
                final int 关闭 = 1;
                if (关闭 == 1) {
                    chr.dropMessage(1, "暂不支持。");
                    c.getPlayer().saveToDB(true, true);
                    c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                final int snCS = slea.readInt();
                final int type = slea.readByte() + 1;
                final String recipient = slea.readMapleAsciiString();
                final String message = slea.readMapleAsciiString();
                final CashItemInfo item2 = CashItemFactory.getInstance().getItem(snCS);
                final Item itemz2 = chr.getCashInventory().toItem(item2);
                if (c.getPlayer().isAdmin()) {
                    System.out.println("包裹购买 ID: " + snCS);
                }
                if (item2.getPrice() < 100) {
                    c.getPlayer().dropMessage(1, "价格低于100点卷的物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
                if (itemz2 == null || itemz2.getUniqueId() <= 0 || itemz2.getItemId() != item2.getId() || itemz2.getQuantity() != item2.getCount()) {
                    c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                    doCSPackets(c);
                    break;
                }
                if (item2 == null || c.getPlayer().getCSPoints(type) < item2.getPrice() || message.length() > 73 || message.length() < 1) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                final Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(recipient, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getRight().getLeft() == c.getAccID()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(162));
                    doCSPackets(c);
                    return;
                }
                if (!item2.genderEquals(info.getRight().getRight())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(163));
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), message, item2.getSN(), MapleInventoryIdentifier.getInstance());
                c.getPlayer().modifyCSPoints(type, -item2.getPrice(), false);
                c.sendPacket(MTSCSPacket.sendGift(item2.getId(), item2.getCount(), recipient));
                break;
            }
            case 5: {
                chr.clearWishlist();
                if (slea.available() < 40L) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                final int[] wishlist = new int[10];
                for (int k = 0; k < 10; ++k) {
                    wishlist[k] = slea.readInt();
                }
                chr.setWishlist(wishlist);
                c.sendPacket(MTSCSPacket.sendWishList(chr, true));
                break;
            }
            case 6: {
                final int 余额 = slea.readByte() + 1;
                final boolean 优惠价 = slea.readByte() > 0;
                if (优惠价) {
                    final int snCS2 = slea.readInt();
                    byte types = 1;
                    switch (snCS2) {
                        case 50200018: {
                            types = 1;
                            break;
                        }
                        case 50200019: {
                            types = 2;
                            break;
                        }
                        case 50200020: {
                            types = 3;
                            break;
                        }
                        case 50200021: {
                            types = 4;
                            break;
                        }
                        case 50200043: {
                            types = 5;
                            break;
                        }
                    }
                    final MapleInventoryType type2 = MapleInventoryType.getByType(types);
                    if (chr.getCSPoints(余额) >= 1100 && chr.getInventory(type2).getSlotLimit() < 96) {
                        chr.modifyCSPoints(余额, -1100, false);
                        chr.getInventory(type2).addSlot((byte)8);
                        chr.dropMessage(1, "扩充成功，当前栏位: " + chr.getInventory(type2).getSlotLimit() + " 个。");
                        RefreshCashShop(c);
                        chr.getStorage().saveToDB();
                    }
                    else {
                        chr.dropMessage(1, "您无法继续进行扩充，点卷余额不足或者栏位已超过上限。");
                    }
                    break;
                }
                final MapleInventoryType type3 = MapleInventoryType.getByType(slea.readByte());
                if (chr.getCSPoints(余额) >= 600 && chr.getInventory(type3).getSlotLimit() < 96) {
                    chr.modifyCSPoints(余额, -600, false);
                    chr.getInventory(type3).addSlot((byte)4);
                    chr.dropMessage(1, "背包已增加到 " + chr.getInventory(type3).getSlotLimit() + " 个。");
                    RefreshCashShop(c);
                    chr.getStorage().saveToDB();
                }
                else {
                    chr.dropMessage(1, "扩充失败，点卷余额不足或者栏位已达到上限。");
                    c.sendPacket(MTSCSPacket.sendCSFail(164));
                }
                break;
            }
            case 7: {
                if (chr.getCSPoints(1) >= 600 && chr.getStorage().getSlots() < 45) {
                    chr.modifyCSPoints(1, -600, false);
                    chr.getStorage().increaseSlots((byte)4);
                    chr.getStorage().saveToDB();
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(164));
                }
                RefreshCashShop(c);
                break;
            }
            case 8: {
                final int 关闭2 = 1;
                if (关闭2 == 1) {
                    chr.dropMessage(1, "暂不支持。");
                    c.getPlayer().saveToDB(true, true);
                    c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                final int useNX2 = slea.readByte() + 1;
                final CashItemInfo item3 = CashItemFactory.getInstance().getItem(slea.readInt());
                final int slots = c.getCharacterSlots();
                if (slots > 10) {
                    chr.dropMessage(1, "角色列表已满无法增加！");
                }
                if (item3 == null || c.getPlayer().getCSPoints(useNX2) < item3.getPrice() || slots > 15) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(useNX2, -item3.getPrice(), false);
                if (c.gainCharacterSlot()) {
                    c.sendPacket(MTSCSPacket.increasedStorageSlots(slots + 1));
                    chr.dropMessage(1, "角色列表已增加到：" + c.getCharacterSlots() + "个");
                    break;
                }
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                break;
            }
            case 13: {
                final int uniqueid = slea.readInt();
                slea.readInt();
                slea.readByte();
                final byte type4 = slea.readByte();
                final byte unknown = slea.readByte();
                final Item item4 = c.getPlayer().getCashInventory().findByCashId(uniqueid);
                if (item4 != null && item4.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item4.getItemId(), item4.getQuantity(), item4.getOwner())) {
                    final Item item_ = item4.copy();
                    final byte slot = (byte)MapleInventoryManipulator.addbyItem(c, item_, true);
                    if (slot >= 0) {
                        if (item_.getPet() != null) {
                            item_.getPet().setInventoryPosition(type4);
                            c.getPlayer().addPet(item_.getPet());
                        }
                        c.getPlayer().getCashInventory().removeFromInventory(item4);
                        c.sendPacket(MTSCSPacket.confirmFromCSInventory(item_, type4));
                    }
                    else {
                        c.sendPacket(MaplePacketCreator.serverNotice(1, "您的包裹已满."));
                    }
                    break;
                }
                c.sendPacket(MaplePacketCreator.serverNotice(1, "放入背包错误A."));
                break;
            }
            case 14: {
                final int uniqueid = (int)slea.readLong();
                final MapleInventoryType type5 = MapleInventoryType.getByType(slea.readByte());
                final Item item5 = c.getPlayer().getInventory(type5).findByUniqueId(uniqueid);
                if (item5 != null && item5.getQuantity() > 0 && item5.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                    final Item item_2 = item5.copy();
                    c.getPlayer().getInventory(type5).removeItem(item5.getPosition(), item5.getQuantity(), false);
                    final int sn = CashItemFactory.getInstance().getItemSN(item_2.getItemId());
                    if (item_2.getPet() != null) {
                        c.getPlayer().removePet(item_2.getPet(), false);
                    }
                    item_2.setPosition((short)0);
                    item_2.setGMLog("购物商场购买: " + FileoutputUtil.CurrentReadable_Time());
                    c.getPlayer().getCashInventory().addToInventory(item_2);
                    c.sendPacket(MTSCSPacket.confirmToCSInventory(item5, c.getAccID(), sn));
                }
                else {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                }
                RefreshCashShop(c);
                break;
            }
            case 26: {
                return;
            }
            case 29:
            case 36: {
                int sn2 = slea.readInt();
                if (sn2 == 209000310) {
                    sn2 = 20900026;
                }
                final CashItemInfo item = CashItemFactory.getInstance().getItem(sn2);
                final String partnerName = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                final Item itemz3 = chr.getCashInventory().toItem(item);
                for (int l = 0; l < itembp_id.length; ++l) {
                    if (item.getId() == Integer.parseInt(itembp_id[l])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                    chr.dropMessage(1, "购买戒指错误：\r\n你没有足够的点卷或者该物品不存在。。");
                    doCSPackets(c);
                    return;
                }
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    chr.dropMessage(1, "购买戒指错误：B\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    chr.dropMessage(1, "购买戒指错误：C\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                if (item.getPrice() == 2990) {}
                final Pair<Integer, Pair<Integer, Integer>> info2 = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
                if (info2 == null || info2.getLeft() <= 0 || info2.getLeft() == c.getPlayer().getId()) {
                    chr.dropMessage(1, "购买戒指错误：D\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                if (info2.getRight().getLeft() == c.getAccID()) {
                    chr.dropMessage(1, "购买戒指错误：E\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                if (info2.getRight().getRight() == c.getPlayer().getGender() && action == 29) {
                    chr.dropMessage(1, "购买戒指错误：F\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                final int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info2.getLeft(), item.getSN());
                if (err != 1) {
                    chr.dropMessage(1, "购买戒指错误：G\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                c.getSession().write(MTSCSPacket.商城送礼物(item.getId(), item.getCount(), partnerName));
                chr.sendNote(partnerName, partnerName + " 您已收到" + chr.getName() + "送给您的礼物，请进入现金商城查看！");
                final int chz = Find.findChannel(partnerName);
                if (chz > 0) {
                    final MapleCharacter receiver = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterByName(partnerName);
                    if (receiver != null) {
                        receiver.showNote();
                    }
                }
                doCSPackets(c);
                return;
            }
            case 31: {
                final int 关闭2 = 1;
                if (关闭2 == 1) {
                    chr.dropMessage(1, "礼包暂不支持购买。");
                    c.getPlayer().saveToDB(true, true);
                    c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                final int type = slea.readByte() + 1;
                final int snID = slea.readInt();
                final CashItemInfo item6 = CashItemFactory.getInstance().getItem(snID);
                for (int m = 0; m < itembp_id.length; ++m) {
                    if (snID == Integer.parseInt(itembp_id[m])) {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        return;
                    }
                }
                if (c.getPlayer().isAdmin()) {
                    System.out.println("礼包购买 ID: " + snID);
                }
                switch (snID) {
                    case 10001818: {
                        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                        doCSPackets(c);
                        break;
                    }
                }
                List<CashItemInfo> ccc = null;
                if (item6 != null) {
                    ccc = CashItemFactory.getInstance().getPackageItems(item6.getId());
                }
                if (item6 == null || ccc == null || c.getPlayer().getCSPoints(type) < item6.getPrice()) {
                    chr.dropMessage(1, "购买礼包错误：\r\n你没有足够的点卷或者该物品不存在。");
                    doCSPackets(c);
                    return;
                }
                if (!item6.genderEquals(c.getPlayer().getGender())) {
                    chr.dropMessage(1, "购买礼包错误：B\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getCashInventory().getItemsSize() >= 100 - ccc.size()) {
                    chr.dropMessage(1, "购买礼包错误：C\r\n请联系GM！。");
                    doCSPackets(c);
                    return;
                }
                final Map<Integer, Item> ccz = new HashMap<Integer, Item>();
                for (final CashItemInfo i2 : ccc) {
                    for (final int iz : GameConstants.cashBlock) {
                        if (i2.getId() == iz) {}
                    }
                    final Item itemz4 = chr.getCashInventory().toItem(i2, chr, MapleInventoryManipulator.getUniqueId(i2.getId(), null), "");
                    if (itemz4 != null && itemz4.getUniqueId() > 0) {
                        if (itemz4.getItemId() != i2.getId()) {
                            continue;
                        }
                        ccz.put(i2.getSN(), itemz4);
                        c.getPlayer().getCashInventory().addToInventory(itemz4);
                        c.sendPacket(MTSCSPacket.showBoughtCSItem(itemz4, item6.getSN(), c.getAccID()));
                    }
                }
                chr.modifyCSPoints(type, -item6.getPrice(), false);
                break;
            }
            case 42: {
                final int snCS = slea.readInt();
                if (snCS == 50200031 && c.getPlayer().getCSPoints(1) >= 500) {
                    c.getPlayer().modifyCSPoints(1, -500);
                    c.getPlayer().modifyCSPoints(2, 500);
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "兑换500抵用卷成功"));
                }
                else if (snCS == 50200032 && c.getPlayer().getCSPoints(1) >= 1000) {
                    c.getPlayer().modifyCSPoints(1, -1000);
                    c.getPlayer().modifyCSPoints(2, 1000);
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "兑换抵1000用卷成功"));
                }
                else if (snCS == 50200033 && c.getPlayer().getCSPoints(1) >= 5000) {
                    c.getPlayer().modifyCSPoints(1, -5000);
                    c.getPlayer().modifyCSPoints(2, 5000);
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "兑换5000抵用卷成功"));
                }
                else {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "没有找到这个道具的信息！\r\n或者你点卷不足无法兑换！"));
                }
                c.sendPacket(MTSCSPacket.enableCSorMTS());
                c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 33: {
                final int 关闭2 = 1;
                if (关闭2 == 1) {
                    chr.dropMessage(1, "暂不支持。");
                    c.getPlayer().saveToDB(true, true);
                    c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                    c.sendPacket(MaplePacketCreator.enableActions());
                    return;
                }
                final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getMeso() < item.getPrice()) {
                    c.sendPacket(MTSCSPacket.sendCSFail(184));
                    doCSPackets(c);
                    return;
                }
                if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                    doCSPackets(c);
                    return;
                }
                for (final int iz2 : GameConstants.cashBlock) {
                    if (item.getId() == iz2) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                        doCSPackets(c);
                        return;
                    }
                }
                final byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short)item.getCount(), null, (byte)0);
                if (pos < 0) {
                    c.sendPacket(MTSCSPacket.sendCSFail(177));
                    doCSPackets(c);
                    return;
                }
                chr.gainMeso(-item.getPrice(), false);
                c.sendPacket(MTSCSPacket.showBoughtCSQuestItem(item.getPrice(), (short)item.getCount(), pos, item.getId()));
                break;
            }
            default: {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                break;
            }
        }
        doCSPackets(c);
    }
    
    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200075: {
                return MapleInventoryType.EQUIP;
            }
            case 50200074: {
                return MapleInventoryType.USE;
            }
            case 50200073: {
                return MapleInventoryType.ETC;
            }
            default: {
                return MapleInventoryType.UNDEFINED;
            }
        }
    }
    
    private static final void RefreshCashShop(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashInventory(c));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
    
    private static final void doCSPackets(final MapleClient c) {
        c.sendPacket(MTSCSPacket.getCSInventory(c));
        c.sendPacket(MTSCSPacket.enableCSorMTS());
        c.sendPacket(MTSCSPacket.sendWishList(c.getPlayer(), false));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MaplePacketCreator.enableActions());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
