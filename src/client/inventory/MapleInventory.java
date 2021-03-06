package client.inventory;

import tools.MaplePacketCreator;
import client.MapleCharacter;
import constants.GameConstants;
import server.MapleItemInformationProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;

public class MapleInventory implements Iterable<Item>, Serializable
{
    private Map<Short, Item> inventory;
    private byte slotLimit;
    private MapleInventoryType type;
    
    public MapleInventory(final MapleInventoryType type, final byte slotLimit) {
        this.slotLimit = 0;
        this.inventory = new LinkedHashMap<Short, Item>();
        this.slotLimit = slotLimit;
        this.type = type;
    }
    
    public void addSlot(final byte slot) {
        this.slotLimit += slot;
        if (this.slotLimit > 96) {
            this.slotLimit = 96;
        }
    }
    
    public byte getSlotLimit() {
        return this.slotLimit;
    }
    
    public void setSlotLimit(byte slot) {
        if (slot > 96) {
            slot = 96;
        }
        this.slotLimit = slot;
    }
    
    public Item findById(final int itemId) {
        for (final Item item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public Item findByUniqueId(final int itemId) {
        for (final Item item : this.inventory.values()) {
            if (item.getUniqueId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public int countById(final int itemId) {
        int possesed = 0;
        for (final Item item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                possesed += item.getQuantity();
            }
        }
        return possesed;
    }
    
    public List<Item> listById(final int itemId) {
        final List<Item> ret = new ArrayList<Item>();
        for (final Item item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }
        if (ret.size() > 1) {
            Collections.sort(ret);
        }
        return ret;
    }
    
    public List listByEquipOnlyId(final int equipOnlyId) {
        final List ret = new ArrayList();
        for (final Item item : this.inventory.values()) {
            if (item.getEquipOnlyId() > 0 && item.getEquipOnlyId() == equipOnlyId) {
                ret.add(item);
            }
        }
        if (ret.size() > 1) {
            Collections.sort((List<Comparable>)ret);
        }
        return ret;
    }
    
    public Collection<Item> list() {
        return this.inventory.values();
    }
    
    public short addItem(final Item item) {
        final short slotId = this.getNextFreeSlot();
        if (slotId < 0) {
            return -1;
        }
        this.inventory.put(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }
    
    public void addFromDB(final Item item) {
        if (item.getPosition() < 0 && !this.type.equals(MapleInventoryType.EQUIPPED)) {
            return;
        }
        this.inventory.put(item.getPosition(), item);
    }
    
    public boolean move2(final byte sSlot, final byte dSlot, final short slotMax) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item source = (client.inventory.Item)(client.inventory.Item)this.inventory.get(sSlot);
        final Item target = (client.inventory.Item)(client.inventory.Item)this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            source.setPosition(dSlot);
            this.inventory.put((short)dSlot, source);
            this.inventory.remove(sSlot);
        }
        else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId())) {
            if (this.type.getType() == MapleInventoryType.EQUIP.getType()) {
                this.swap(target, source);
            }
            if (source.getQuantity() + target.getQuantity() > slotMax) {
                final short rest = (short)(source.getQuantity() + target.getQuantity() - slotMax);
                if (rest + slotMax != source.getQuantity() + target.getQuantity()) {
                    return false;
                }
                source.setQuantity(rest);
                target.setQuantity(slotMax);
            }
            else {
                target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        }
        else {
            this.swap(target, source);
        }
        return true;
    }
    
    public void move(final short sSlot, final short dSlot, final short slotMax) {
        if (dSlot > this.slotLimit) {
            return;
        }
        final Item source = (client.inventory.Item)(client.inventory.Item)this.inventory.get(sSlot);
        final Item target = (client.inventory.Item)(client.inventory.Item)this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            source.setPosition(dSlot);
            this.inventory.put(dSlot, source);
            this.inventory.remove(sSlot);
        }
        else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration()) {
            if (this.type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType()) {
                this.swap(target, source);
            }
            else if (source.getQuantity() + target.getQuantity() > slotMax) {
                source.setQuantity((short)(source.getQuantity() + target.getQuantity() - slotMax));
                target.setQuantity(slotMax);
            }
            else {
                target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        }
        else {
            this.swap(target, source);
        }
    }
    
    private void swap(final Item source, final Item target) {
        this.inventory.remove(source.getPosition());
        this.inventory.remove(target.getPosition());
        final short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        this.inventory.put(source.getPosition(), source);
        this.inventory.put(target.getPosition(), target);
    }
    
    public Item getItem(final short slot) {
        return this.inventory.get(slot);
    }
    
    public void removeItem(final short slot) {
        this.removeItem(slot, (short)1, false);
    }
    
    public void removeItem(final short slot, final short quantity, final boolean allowZero) {
        this.removeItem(slot, quantity, allowZero, null);
    }
    
    public void removeItem(final short slot, final short quantity, final boolean allowZero, final MapleCharacter chr) {
        final Item item = (client.inventory.Item)(client.inventory.Item)this.inventory.get(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short)(item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short)0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            this.removeSlot(slot);
        }
        if (chr != null) {
            chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(3, item)));
            chr.dropMessage(5, "???????????????[" + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "]??????????????????");
        }
    }
    
    public void removeSlot(final short slot) {
        this.inventory.remove(slot);
    }
    
    public boolean isFull() {
        return this.inventory.size() >= this.slotLimit;
    }
    
    public boolean isFull(final int margin) {
        return this.inventory.size() + margin >= this.slotLimit;
    }
    
    public short getNextFreeSlot() {
        if (this.isFull()) {
            return -1;
        }
        for (short i = 1; i <= this.slotLimit; ++i) {
            if (!this.inventory.keySet().contains(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public short getNumFreeSlot() {
        if (this.isFull()) {
            return 0;
        }
        byte free = 0;
        for (short i = 1; i <= this.slotLimit; ++i) {
            if (!this.inventory.keySet().contains(i)) {
                ++free;
            }
        }
        return free;
    }
    
    public MapleInventoryType getType() {
        return this.type;
    }
    
    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(this.inventory.values()).iterator();
    }
    
    public Item findByEquipOnlyId(final long onlyId, final int itemId) {
        for (final Item item : this.inventory.values()) {
            if (item.getEquipOnlyId() == onlyId && item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
}
