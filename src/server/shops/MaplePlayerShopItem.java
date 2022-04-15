package server.shops;

import client.inventory.Item;

public class MaplePlayerShopItem
{
    public Item item;
    public short bundles;
    public int price;
    public short flag;
    
    public MaplePlayerShopItem(final Item item, final short bundles, final int price, final short flag) {
        this.item = item;
        this.bundles = bundles;
        this.price = price;
        this.flag = flag;
    }
}
