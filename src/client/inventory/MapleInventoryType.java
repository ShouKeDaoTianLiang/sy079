package client.inventory;

public enum MapleInventoryType
{
    UNDEFINED(0), 
    EQUIP(1), 
    USE(2), 
    SETUP(3), 
    ETC(4), 
    其他栏(4), 
    CASH(5), 
    EQUIPPED(-1);
    
    final byte type;
    
    private MapleInventoryType(final int type) {
        this.type = (byte)type;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public short getBitfieldEncoding() {
        return (short)(2 << this.type);
    }
    
    public static MapleInventoryType getByType(final byte type) {
        for (final MapleInventoryType l : values()) {
            if (l.getType() == type) {
                return l;
            }
        }
        return null;
    }
    
    public static MapleInventoryType getByWZName(final String name) {
        switch (name) {
            case "Install": {
                return MapleInventoryType.SETUP;
            }
            case "Consume": {
                return MapleInventoryType.USE;
            }
            case "Etc": {
                return MapleInventoryType.ETC;
            }
            case "Cash": {
                return MapleInventoryType.CASH;
            }
            case "Pet": {
                return MapleInventoryType.CASH;
            }
            default: {
                return MapleInventoryType.UNDEFINED;
            }
        }
    }
}
