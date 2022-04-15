package server.shops;

import server.maps.MapleMap;
import java.util.Arrays;
import server.maps.MapleMapObjectType;
import java.awt.Point;
import server.maps.MapleMapObject;
import tools.packet.PlayerShopPacket;
import handling.channel.ChannelServer;
import client.inventory.Item;
import tools.FileoutputUtil1;
import server.MapleItemInformationProvider;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import tools.MaplePacketCreator;
import client.inventory.ItemFlag;
import client.MapleClient;
import java.util.Iterator;
import constants.ServerConstants;
import server.Timer.EtcTimer;
import java.util.LinkedList;
import client.MapleCharacter;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class HiredMerchant extends AbstractPlayerStore
{
    public ScheduledFuture<?> schedule;
    private final List<String> blacklist;
    private int storeid;
    private final long start;
    
    public HiredMerchant(final MapleCharacter owner, final int itemId, final String desc) {
        super(owner, itemId, desc, "", 3);
        this.start = System.currentTimeMillis();
        this.blacklist = new LinkedList<String>();
        this.schedule = EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (HiredMerchant.this.getMCOwner() != null && HiredMerchant.this.getMCOwner().getPlayerShop() == HiredMerchant.this) {
                    HiredMerchant.this.getMCOwner().setPlayerShop(null);
                }
                HiredMerchant.this.removeAllVisitors(-1, -1);
                HiredMerchant.this.closeShop(true, true);
            }
        }, ServerConstants.时间);
    }
    
    @Override
    public byte getShopType() {
        return 1;
    }
    
    public final void setStoreid(final int storeid) {
        this.storeid = storeid;
    }
    
    public List<MaplePlayerShopItem> searchItem(final int itemSearch) {
        final List<MaplePlayerShopItem> itemz = new LinkedList<MaplePlayerShopItem>();
        for (final MaplePlayerShopItem item : this.items) {
            if (item.item.getItemId() == itemSearch && item.bundles > 0) {
                itemz.add(item);
            }
        }
        return itemz;
    }
    
    @Override
    public void buy(final MapleClient c, final int item, final short quantity) {
        final MaplePlayerShopItem pItem = (server.shops.MaplePlayerShopItem)(server.shops.MaplePlayerShopItem)this.items.get(item);
        final Item shopItem = pItem.item;
        final Item newItem = shopItem.copy();
        final short perbundle = newItem.getQuantity();
        final int theQuantity = pItem.price * quantity;
        newItem.setQuantity((short)(quantity * perbundle));
        final short flag = newItem.getFlag();
        if (ItemFlag.KARMA_EQ.check(flag)) {
            newItem.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
        }
        else if (ItemFlag.KARMA_USE.check(flag)) {
            newItem.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
        }
        if (!c.getPlayer().canHold(newItem.getItemId())) {
            c.getPlayer().dropMessage(1, "背包已满");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner())) {
            final int gainmeso = this.getMeso() + theQuantity - GameConstants.EntrustedStoreTax(theQuantity);
            if (gainmeso > 0) {
                this.setMeso(gainmeso);
                final MaplePlayerShopItem tmp167_165 = pItem;
                tmp167_165.bundles -= quantity;
                MapleInventoryManipulator.addFromDrop(c, newItem, false);
                this.bought.add(new BoughtItem(newItem.getItemId(), quantity, theQuantity, c.getPlayer().getName()));
                c.getPlayer().gainMeso(-theQuantity, false);
                this.saveItems();
                final MapleCharacter chr = this.getMCOwnerWorld();
                final String itemText = MapleItemInformationProvider.getInstance().getName(newItem.getItemId()) + " (" + perbundle + ") x " + quantity + " 已经被卖出。 剩余数量: " + pItem.bundles + " 购买者: " + c.getPlayer().getName();
                if (chr != null) {
                    chr.dropMessage(5, "您雇佣商店里面的道具: " + itemText);
                }
                FileoutputUtil1.雇佣购买("雇佣购买记录.txt", "玩家:" + ((chr != null) ? chr.getName() : this.getOwnerName()) + " 雇佣商店卖出: " + newItem.getItemId() + " - " + itemText + " 价格: " + theQuantity + "");
                System.out.println("[雇佣] " + ((chr != null) ? chr.getName() : this.getOwnerName()) + " 雇佣商店卖出: " + newItem.getItemId() + " - " + itemText + " 价格: " + theQuantity);
            }
            else {
                c.getPlayer().dropMessage(1, "金币不足.");
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        }
        else {
            c.getPlayer().dropMessage(1, "背包已满\r\n请留1格以上位置\r\n在进行购买物品\r\n防止非法复制");
            c.sendPacket(MaplePacketCreator.enableActions());
        }
    }
    
    @Override
    public void closeShop(final boolean saveItems, final boolean remove) {
        if (this.schedule != null) {
            this.schedule.cancel(false);
        }
        if (saveItems) {
            this.saveItems();
            this.items.clear();
        }
        if (remove) {
            ChannelServer.getInstance(this.channel).removeMerchant(this);
            this.getMap().broadcastMessage(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
        }
        this.getMap().removeMapObject(this);
        try {
            for (final ChannelServer ch : ChannelServer.getAllInstances()) {
                MapleMap map = null;
                for (int i = 910000001; i <= 910000022; ++i) {
                    map = ch.getMapFactory().getMap(i);
                    if (map != null) {
                        final List<MapleMapObject> HMS = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT));
                        for (final MapleMapObject HM : HMS) {
                            final HiredMerchant HMM = (HiredMerchant)HM;
                            if (HMM.getOwnerId() == this.getOwnerId()) {
                                map.removeMapObject(this);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {}
        this.schedule = null;
    }
    
    public int getTimeLeft() {
        return (int)((System.currentTimeMillis() - this.start) / 1000L);
    }
    
    public final int getStoreId() {
        return this.storeid;
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.HIRED_MERCHANT;
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
        }
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write(PlayerShopPacket.spawnHiredMerchant(this));
        }
    }
    
    public final boolean isInBlackList(final String bl) {
        return this.blacklist.contains(bl);
    }
    
    public final void addBlackList(final String bl) {
        this.blacklist.add(bl);
    }
    
    public final void removeBlackList(final String bl) {
        this.blacklist.remove(bl);
    }
    
    public final void sendBlackList(final MapleClient c) {
        c.sendPacket(PlayerShopPacket.MerchantBlackListView(this.blacklist));
    }
    
    public final void sendVisitor(final MapleClient c) {
        c.sendPacket(PlayerShopPacket.MerchantVisitorView(this.visitors));
    }
}
