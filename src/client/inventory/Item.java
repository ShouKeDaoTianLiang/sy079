package client.inventory;

import server.MapleItemInformationProvider;
import constants.GameConstants;
import java.io.Serializable;

public class Item implements Comparable<Item>, Serializable
{
    private final int id;
    private short position;
    private short quantity;
    private short flag;
    private long expiration;
    private MaplePet pet;
    private int uniqueid;
    private String owner;
    private String GameMaster_log;
    private String giftFrom;
    protected MapleRing ring;
    private byte itemLevel;
    private int equipOnlyId;
    
    public Item(final int id, final short position, final short quantity, final short flag, final int uniqueid) {
        this.expiration = -1L;
        this.pet = null;
        this.uniqueid = -1;
        this.owner = "";
        this.GameMaster_log = null;
        this.giftFrom = "";
        this.ring = null;
        this.equipOnlyId = -1;
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
        this.equipOnlyId = -1;
    }
    
    public Item(final int id, final short position, final short quantity, final short flag) {
        this.expiration = -1L;
        this.pet = null;
        this.uniqueid = -1;
        this.owner = "";
        this.GameMaster_log = null;
        this.giftFrom = "";
        this.ring = null;
        this.equipOnlyId = -1;
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.equipOnlyId = -1;
    }
    
    public Item(final int id, final byte position, final short quantity) {
        this.expiration = -1L;
        this.pet = null;
        this.uniqueid = -1;
        this.owner = "";
        this.GameMaster_log = null;
        this.giftFrom = "";
        this.ring = null;
        this.equipOnlyId = -1;
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.itemLevel = 1;
        this.equipOnlyId = -1;
    }
    
    public Item copy() {
        final Item ret = new Item(this.id, this.position, this.quantity, this.flag, this.uniqueid);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        ret.equipOnlyId = this.equipOnlyId;
        return ret;
    }
    
    public final void setPosition(final short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setInventoryPosition(position);
        }
    }
    
    public void setQuantity(final short quantity) {
        this.quantity = quantity;
    }
    
    public final int getItemId() {
        return this.id;
    }
    
    public final short getPosition() {
        return this.position;
    }
    
    public final boolean getLocked() {
        return this.flag == ItemFlag.LOCK.getValue();
    }
    
    public final short getQuantity() {
        return this.quantity;
    }
    
    public byte getType() {
        return 2;
    }
    
    public final String getOwner() {
        return this.owner;
    }
    
    public final void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public short getFlag() {
        return this.flag;
    }
    
    public void setFlag(final short flag) {
        this.flag = flag;
    }
    
    public final void setLocked(final byte flag) {
        if (flag == 1) {
            this.setFlag((byte)ItemFlag.LOCK.getValue());
        }
        else if (flag == 0) {
            this.setFlag((short)(this.getFlag() - ItemFlag.LOCK.getValue()));
        }
    }
    
    public final long getExpiration() {
        return this.expiration;
    }
    
    public final void setExpiration(final long expire) {
        this.expiration = expire;
    }
    
    public final String getGMLog() {
        return this.GameMaster_log;
    }
    
    public void setGMLog(final String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }
    
    public final int getUniqueId() {
        return this.uniqueid;
    }
    
    public final void setUniqueId(final int id) {
        this.uniqueid = id;
    }
    
    public final MaplePet getPet() {
        return this.pet;
    }
    
    public final void setPet(final MaplePet pet) {
        this.pet = pet;
    }
    
    public void setGiftFrom(final String gf) {
        this.giftFrom = gf;
    }
    
    public String getGiftFrom() {
        return this.giftFrom;
    }
    
    public void setEquipLevel(final byte gf) {
        this.itemLevel = gf;
    }
    
    public byte getEquipLevel() {
        return this.itemLevel;
    }
    
    @Override
    public int compareTo(final Item other) {
        if (Math.abs(this.position) < Math.abs(other.getPosition())) {
            return -1;
        }
        if (Math.abs(this.position) == Math.abs(other.getPosition())) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        final Item ite = (Item)obj;
        return this.uniqueid == ite.getUniqueId() && this.id == ite.getItemId() && this.quantity == ite.getQuantity() && Math.abs(this.position) == Math.abs(ite.getPosition());
    }
    
    @Override
    public String toString() {
        return "Item: " + this.id + " quantity: " + this.quantity;
    }
    
    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(this.id) || this.getUniqueId() <= 0) {
            return null;
        }
        if (this.ring == null) {
            this.ring = MapleRing.loadFromDb(this.getUniqueId(), this.position < 0);
        }
        return this.ring;
    }
    
    public void setRing(final MapleRing ring) {
        this.ring = ring;
    }
    
    public boolean hasSetOnlyId() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return this.uniqueid <= 0 && !ii.isCash(this.id) && this.id / 1000000 == 1 && this.equipOnlyId <= 0;
    }
    
    public int getEquipOnlyId() {
        return this.equipOnlyId;
    }
    
    public void setEquipOnlyId(final int OnlyId) {
        this.equipOnlyId = OnlyId;
    }
}
