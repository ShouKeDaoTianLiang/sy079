package server;

import client.inventory.MapleInventoryType;
import tools.packet.PlayerShopPacket;
import handling.channel.ChannelServer;
import tools.FileoutputUtil;
import client.MapleClient;
import java.util.Iterator;
import tools.MaplePacketCreator;
import tools.FileoutputUtil1;
import constants.GameConstants;
import client.inventory.ItemFlag;
import java.util.LinkedList;
import client.MapleCharacter;
import java.lang.ref.WeakReference;
import client.inventory.Item;
import java.util.List;

public class MapleTrade
{
    private MapleTrade partner;
    private final List<Item> items;
    private List<Item> exchangeItems;
    private int meso;
    private int exchangeMeso;
    private boolean locked;
    private final WeakReference<MapleCharacter> chr;
    private final byte tradingslot;
    
    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
        this.partner = null;
        this.items = new LinkedList<Item>();
        this.meso = 0;
        this.exchangeMeso = 0;
        this.locked = false;
        this.tradingslot = tradingslot;
        this.chr = new WeakReference<MapleCharacter>(chr);
    }
    
    public final void CompleteTrade() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        String Trade = "";
        Trade = "[交易记录开始] -------------------------------------------------------------------------- \r\n";
        if (this.exchangeItems != null) {
            for (final Item item : this.exchangeItems) {
                final short flag = item.getFlag();
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                }
                else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                }
                Trade = "[交易] " + this.chr.get().getName() + " 交易获得道具: " + item.getItemId() + " x " + item.getQuantity() + " - " + ii.getName(item.getItemId()) + "\r\n";
                MapleInventoryManipulator.addFromDrop(this.chr.get().getClient(), item, false);
            }
            this.exchangeItems.clear();
        }
        if (this.exchangeMeso > 0) {
            this.chr.get().gainMeso(this.exchangeMeso - GameConstants.getTaxAmount(this.exchangeMeso), false, true, false);
            Trade = "[交易] " + this.chr.get().getName() + " 交易获得金币: " + this.exchangeMeso + "\r\n";
        }
        FileoutputUtil1.玩家交易("" + this.chr.get().getName() + "交易.txt", Trade);
        this.exchangeMeso = 0;
        this.chr.get().getClient().getSession().write(MaplePacketCreator.TradeMessage(this.tradingslot, (byte)8));
    }
    
    public final void cancel(final MapleClient c) {
        this.cancel(c, 0);
    }
    
    public final void cancel(final MapleClient c, final int unsuccessful) {
        if (this.items != null) {
            for (final Item item : this.items) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            this.items.clear();
        }
        if (this.meso > 0) {
            c.getPlayer().gainMeso(this.meso, false, true, false);
        }
        this.meso = 0;
        c.getSession().write(MaplePacketCreator.getTradeCancel(this.tradingslot, unsuccessful));
    }
    
    public final boolean isLocked() {
        return this.locked;
    }
    
    public final void setMeso(final int meso) {
        if (this.locked || this.partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if ((this.chr.get().getGMLevel() == 1 && !this.partner.getChr().isGM()) || (!this.chr.get().isGM() && this.partner.getChr().getGMLevel() > 0 && this.partner.getChr().getGMLevel() < 1000)) {
            this.chr.get().dropMessage(1, "肥水不流外人田。所以你的金币不能交易给玩家。");
            this.chr.get().getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (this.chr.get().getMeso() >= meso) {
            this.chr.get().gainMeso(-meso, false, true, false);
            this.meso += meso;
            this.chr.get().getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte)0, this.meso));
            if (this.partner != null) {
                this.partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte)1, this.meso));
            }
        }
    }
    
    public final void addItem(final Item item) {
        if (this.locked || this.partner == null) {
            return;
        }
        this.items.add(item);
        this.chr.get().getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte)0, item));
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte)1, item));
        }
    }
    
    public final void chat(final String message) {
        final String sb = "[交易聊天偷听] 『" + this.chr.get().getName() + "』对『" + this.partner.getChr().getName() + "』的聊天：  " + message;
        this.chr.get().dropMessage(-2, this.chr.get().getName() + " : " + message);
        FileoutputUtil1.玩家谈话("" + this.chr.get().getName() + "交易对话.txt", "玩家: " + this.chr.get().getName() + " 对玩家: " + this.partner.getChr().getName() + " 在交易中说: " + message + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr_.get玩家私聊1() && chr_.isGM()) {
                    chr_.dropMessage(sb);
                }
            }
        }
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(PlayerShopPacket.shopChat(this.chr.get().getName() + " : " + message, 1));
        }
    }
    
    public final MapleTrade getPartner() {
        return this.partner;
    }
    
    public final void setPartner(final MapleTrade partner) {
        if (this.locked) {
            return;
        }
        this.partner = partner;
    }
    
    public final MapleCharacter getChr() {
        return this.chr.get();
    }
    
    public final int getNextTargetSlot() {
        if (this.items.size() >= 9) {
            return -1;
        }
        int ret = 1;
        for (final Item item : this.items) {
            if (item.getPosition() == ret) {
                ++ret;
            }
        }
        return ret;
    }
    
    public final boolean setItems(final MapleClient c, final Item item, byte targetSlot, final int quantity) {
        final int target = this.getNextTargetSlot();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (target == -1 || GameConstants.isPet(item.getItemId()) || this.isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.CASH && quantity != 1) || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        final short flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) && !ItemFlag.KARMA_EQ.check(flag) && !ItemFlag.KARMA_USE.check(flag)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        final Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        }
        else {
            tradeItem.setQuantity((short)quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short)quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte)target;
        }
        else {
            for (final Item itemz : this.items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte)target;
                    break;
                }
            }
        }
        tradeItem.setPosition(targetSlot);
        this.addItem(tradeItem);
        return true;
    }
    
    private final int check() {
        if (this.chr.get().getMeso() + this.exchangeMeso < 0) {
            return 1;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte eq = 0;
        byte use = 0;
        byte setup = 0;
        byte etc = 0;
        byte cash = 0;
        for (final Item item : this.exchangeItems) {
            switch (GameConstants.getInventoryType(item.getItemId())) {
                case EQUIP: {
                    ++eq;
                    break;
                }
                case USE: {
                    ++use;
                    break;
                }
                case SETUP: {
                    ++setup;
                    break;
                }
                case ETC: {
                    ++etc;
                    break;
                }
                case CASH: {
                    ++cash;
                    break;
                }
            }
            if (ii.isPickupRestricted(item.getItemId()) && this.chr.get().getInventory(GameConstants.getInventoryType(item.getItemId())).findById(item.getItemId()) != null) {
                return 2;
            }
        }
        if (this.chr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || this.chr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || this.chr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || this.chr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || this.chr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return 1;
        }
        return 0;
    }
    
    public static final void completeTrade(final MapleCharacter c) {
        final MapleTrade local = c.getTrade();
        final MapleTrade partner = local.getPartner();
        if (partner == null || local.locked) {
            return;
        }
        local.locked = true;
        partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeConfirmation());
        partner.exchangeItems = local.items;
        partner.exchangeMeso = local.meso;
        if (partner.isLocked()) {
            final int lz = local.check();
            final int lz2 = partner.check();
            String Trade = "";
            if (lz == 0 && lz2 == 0) {
                local.CompleteTrade();
                partner.CompleteTrade();
                Trade = "[交易记录完成] " + local.getChr().getName() + " 和 " + partner.getChr().getName() + " 交易完成。\r\n\r\n";
                FileoutputUtil1.玩家交易("" + local.getChr().getName() + "交易.log", Trade);
                FileoutputUtil1.玩家交易("" + partner.getChr().getName() + "交易.log", Trade);
            }
            else {
                partner.cancel(partner.getChr().getClient(), (lz == 0) ? lz2 : lz);
                local.cancel(c.getClient(), (lz == 0) ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            c.setTrade(null);
        }
    }
    
    public static final void cancelTrade(final MapleTrade Localtrade, final MapleClient c) {
        Localtrade.cancel(c);
        final MapleTrade partner = Localtrade.getPartner();
        if (partner != null) {
            partner.cancel(partner.getChr().getClient());
            partner.getChr().setTrade(null);
        }
        if (Localtrade.chr.get() != null) {
            Localtrade.chr.get().setTrade(null);
        }
    }
    
    public static final void startTrade(final MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte)0, c));
            c.getClient().getSession().write(MaplePacketCreator.getTradeStart(c.getClient(), c.getTrade(), (byte)0, false));
        }
        else {
            c.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "不能同时做多件事情。"));
        }
    }
    
    public static final void start现金交易(final MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte)0, c));
            c.getClient().getSession().write(MaplePacketCreator.getTradeStart(c.getClient(), c.getTrade(), (byte)0, true));
        }
        else {
            c.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "不能同时做多件事情。"));
        }
    }
    
    public static final void inviteTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1 == null || c1.getTrade() == null) {
            return;
        }
        if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte)1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().write(MaplePacketCreator.getTradeInvite(c1, false));
        }
        else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "对方正在和其他玩家进行交易中。"));
            cancelTrade(c1.getTrade(), c1.getClient());
        }
    }
    
    public static final void invite现金交易(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1 == null || c1.getTrade() == null) {
            return;
        }
        if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte)1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().write(MaplePacketCreator.getTradeInvite(c1, true));
        }
        else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "对方正在和其他玩家进行交易中。"));
            cancelTrade(c1.getTrade(), c1.getClient());
        }
    }
    
    public static final void visit现金交易(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            c2.getClient().getSession().write(MaplePacketCreator.getTradePartnerAdd(c1));
            c1.getClient().getSession().write(MaplePacketCreator.getTradeStart(c1.getClient(), c1.getTrade(), (byte)1, true));
        }
        else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "对方已经取消了交易。"));
        }
    }
    
    public static final void visitTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            c2.getClient().getSession().write(MaplePacketCreator.getTradePartnerAdd(c1));
            c1.getClient().getSession().write(MaplePacketCreator.getTradeStart(c1.getClient(), c1.getTrade(), (byte)1, false));
        }
        else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "对方已经取消了交易。"));
        }
    }
    
    public static final void declineTrade(final MapleCharacter c) {
        final MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                final MapleCharacter other = trade.getPartner().getChr();
                other.getTrade().cancel(other.getClient());
                other.setTrade(null);
                other.dropMessage(5, c.getName() + " 拒绝了你的交易邀请。");
            }
            trade.cancel(c.getClient());
            c.setTrade(null);
        }
    }
}
