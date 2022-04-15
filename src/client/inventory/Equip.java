package client.inventory;

import java.util.Iterator;
import java.util.List;
import tools.Pair;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import client.MapleClient;
import server.Randomizer;
import constants.GameConstants;
import java.io.Serializable;

public class Equip extends Item implements Serializable
{
    public static final int ARMOR_RATIO = 350000;
    public static final int WEAPON_RATIO = 700000;
    private byte upgradeSlots;
    private byte level;
    private byte vicioushammer;
    private byte enhance;
    private short str;
    private short dex;
    private short _int;
    private short luk;
    private short hp;
    private short mp;
    private short watk;
    private short matk;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short hands;
    private short speed;
    private short jump;
    private short potential1;
    private short potential2;
    private short potential3;
    private short hpR;
    private short mpR;
    private int itemEXP;
    private int durability;
    private byte itemLevel;
    
    public Equip(final int id, final short position) {
        super(id, position, (short)1, (short)0);
        this.upgradeSlots = 0;
        this.level = 0;
        this.vicioushammer = 0;
        this.enhance = 0;
        this.str = 0;
        this.dex = 0;
        this._int = 0;
        this.luk = 0;
        this.hp = 0;
        this.mp = 0;
        this.watk = 0;
        this.matk = 0;
        this.wdef = 0;
        this.mdef = 0;
        this.acc = 0;
        this.avoid = 0;
        this.hands = 0;
        this.speed = 0;
        this.jump = 0;
        this.potential1 = 0;
        this.potential2 = 0;
        this.potential3 = 0;
        this.hpR = 0;
        this.mpR = 0;
        this.itemEXP = 0;
        this.durability = -1;
    }
    
    public Equip(final int id, final short position, final short flag) {
        super(id, position, (short)1, flag);
        this.upgradeSlots = 0;
        this.level = 0;
        this.vicioushammer = 0;
        this.enhance = 0;
        this.str = 0;
        this.dex = 0;
        this._int = 0;
        this.luk = 0;
        this.hp = 0;
        this.mp = 0;
        this.watk = 0;
        this.matk = 0;
        this.wdef = 0;
        this.mdef = 0;
        this.acc = 0;
        this.avoid = 0;
        this.hands = 0;
        this.speed = 0;
        this.jump = 0;
        this.potential1 = 0;
        this.potential2 = 0;
        this.potential3 = 0;
        this.hpR = 0;
        this.mpR = 0;
        this.itemEXP = 0;
        this.durability = -1;
    }
    
    public Equip(final int id, final short position, final int uniqueid, final short flag) {
        super(id, position, (short)1, flag, uniqueid);
        this.upgradeSlots = 0;
        this.level = 0;
        this.vicioushammer = 0;
        this.enhance = 0;
        this.str = 0;
        this.dex = 0;
        this._int = 0;
        this.luk = 0;
        this.hp = 0;
        this.mp = 0;
        this.watk = 0;
        this.matk = 0;
        this.wdef = 0;
        this.mdef = 0;
        this.acc = 0;
        this.avoid = 0;
        this.hands = 0;
        this.speed = 0;
        this.jump = 0;
        this.potential1 = 0;
        this.potential2 = 0;
        this.potential3 = 0;
        this.hpR = 0;
        this.mpR = 0;
        this.itemEXP = 0;
        this.durability = -1;
    }
    
    @Override
    public Item copy() {
        final Equip ret = new Equip(this.getItemId(), this.getPosition(), this.getUniqueId(), this.getFlag());
        ret.str = this.str;
        ret.dex = this.dex;
        ret._int = this._int;
        ret.luk = this.luk;
        ret.hp = this.hp;
        ret.mp = this.mp;
        ret.matk = this.matk;
        ret.mdef = this.mdef;
        ret.watk = this.watk;
        ret.wdef = this.wdef;
        ret.acc = this.acc;
        ret.avoid = this.avoid;
        ret.hands = this.hands;
        ret.speed = this.speed;
        ret.jump = this.jump;
        ret.enhance = this.enhance;
        ret.upgradeSlots = this.upgradeSlots;
        ret.level = this.level;
        ret.itemEXP = this.itemEXP;
        ret.durability = this.durability;
        ret.vicioushammer = this.vicioushammer;
        ret.potential1 = this.potential1;
        ret.potential2 = this.potential2;
        ret.potential3 = this.potential3;
        ret.hpR = this.hpR;
        ret.mpR = this.mpR;
        ret.itemLevel = this.itemLevel;
        ret.setGiftFrom(this.getGiftFrom());
        ret.setOwner(this.getOwner());
        ret.setQuantity(this.getQuantity());
        ret.setExpiration(this.getExpiration());
        ret.setEquipOnlyId(this.getEquipOnlyId());
        return ret;
    }
    
    @Override
    public byte getType() {
        return 1;
    }
    
    public byte getUpgradeSlots() {
        return this.upgradeSlots;
    }
    
    public short getStr() {
        return this.str;
    }
    
    public short getDex() {
        return this.dex;
    }
    
    public short getInt() {
        return this._int;
    }
    
    public short getLuk() {
        return this.luk;
    }
    
    public short getHp() {
        return this.hp;
    }
    
    public short getMp() {
        return this.mp;
    }
    
    public short getWatk() {
        return this.watk;
    }
    
    public short getMatk() {
        return this.matk;
    }
    
    public short getWdef() {
        return this.wdef;
    }
    
    public short getMdef() {
        return this.mdef;
    }
    
    public short getAcc() {
        return this.acc;
    }
    
    public short getAvoid() {
        return this.avoid;
    }
    
    public short getHands() {
        return this.hands;
    }
    
    public short getSpeed() {
        return this.speed;
    }
    
    public short getJump() {
        return this.jump;
    }
    
    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }
    
    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }
    
    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }
    
    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }
    
    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }
    
    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }
    
    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }
    
    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }
    
    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }
    
    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }
    
    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }
    
    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }
    
    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }
    
    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }
    
    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }
    
    public void setUpgradeSlots(final byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }
    
    public void setUpgradeSlots(final int i) {
        this.upgradeSlots = (byte)i;
    }
    
    public byte getLevel() {
        return this.level;
    }
    
    public void setLevel(final byte level) {
        this.level = level;
    }
    
    public byte getViciousHammer() {
        return this.vicioushammer;
    }
    
    public void setViciousHammer(final byte ham) {
        this.vicioushammer = ham;
    }
    
    public int getItemEXP() {
        return this.itemEXP;
    }
    
    public void setItemEXP(int itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }
    
    public int getEquipExp() {
        if (this.itemEXP <= 0) {
            return 0;
        }
        if (GameConstants.isWeapon(this.getItemId())) {
            return this.itemEXP / 700000;
        }
        return this.itemEXP / 350000;
    }
    
    public int getEquipExpForLevel() {
        if (this.getEquipExp() <= 0) {
            return 0;
        }
        int expz = this.getEquipExp();
        for (int i = this.getBaseLevel(); i <= GameConstants.getMaxLevel(this.getItemId()) && expz >= GameConstants.getExpForLevel(i, this.getItemId()); expz -= GameConstants.getExpForLevel(i, this.getItemId()), ++i) {}
        return expz;
    }
    
    public int getExpPercentage() {
        return this.itemEXP;
    }
    
    public int getEquipLevels() {
        if (GameConstants.getMaxLevel(this.getItemId()) <= 0) {
            return 0;
        }
        if (this.getEquipExp() <= 0) {
            return this.getBaseLevel();
        }
        int levelz = this.getBaseLevel();
        int expz = this.getEquipExp();
        int i = levelz;
        while (true) {
            if (GameConstants.getStatFromWeapon(this.getItemId()) == null) {
                if (i > GameConstants.getMaxLevel(this.getItemId())) {
                    break;
                }
            }
            else if (i >= GameConstants.getMaxLevel(this.getItemId())) {
                break;
            }
            if (expz < GameConstants.getExpForLevel(i, this.getItemId())) {
                break;
            }
            ++levelz;
            expz -= GameConstants.getExpForLevel(i, this.getItemId());
            ++i;
        }
        return levelz;
    }
    
    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(this.getItemId()) == null) ? 1 : 0;
    }
    
    @Override
    public void setQuantity(final short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + this.getItemId() + ")");
        }
        super.setQuantity(quantity);
    }
    
    public int getDurability() {
        return this.durability;
    }
    
    public void setDurability(final int dur) {
        this.durability = dur;
    }
    
    public byte getEnhance() {
        return this.enhance;
    }
    
    public void setEnhance(final byte en) {
        this.enhance = en;
    }
    
    public short getPotential1() {
        return this.potential1;
    }
    
    public void setPotential1(final short en) {
        this.potential1 = en;
    }
    
    public short getPotential2() {
        return this.potential2;
    }
    
    public void setPotential2(final short en) {
        this.potential2 = en;
    }
    
    public short getPotential3() {
        return this.potential3;
    }
    
    public void setPotential3(final short en) {
        this.potential3 = en;
    }
    
    public byte getState() {
        final int pots = this.potential1 + this.potential2 + this.potential3;
        if (this.potential1 >= 30000 || this.potential2 >= 30000 || this.potential3 >= 30000) {
            return 7;
        }
        if (this.potential1 >= 20000 || this.potential2 >= 20000 || this.potential3 >= 20000) {
            return 6;
        }
        if (pots >= 1) {
            return 5;
        }
        if (pots < 0) {
            return 1;
        }
        return 0;
    }
    
    public void resetPotential() {
        final int rank = (Randomizer.nextInt(100) < 4) ? ((Randomizer.nextInt(100) < 4) ? -7 : -6) : -5;
        this.setPotential1((short)rank);
        this.setPotential2((short)((Randomizer.nextInt(10) == 1) ? rank : 0));
        this.setPotential3((short)0);
    }
    
    public void renewPotential() {
        final int rank = (Randomizer.nextInt(100) < 4 && this.getState() != 7) ? (-(this.getState() + 1)) : (-this.getState());
        this.setPotential1((short)rank);
        this.setPotential2((short)((this.getPotential3() > 0) ? rank : 0));
        this.setPotential3((short)0);
    }
    
    public short getHpR() {
        return this.hpR;
    }
    
    public void setHpR(final short hp) {
        this.hpR = hp;
    }
    
    public short getMpR() {
        return this.mpR;
    }
    
    public void setMpR(final short mp) {
        this.mpR = mp;
    }
    
    public void gainItemLevel() {
        ++this.itemLevel;
    }
    
    public void gainItemExp(final MapleClient c, final int gain, final boolean timeless) {
        this.itemEXP += gain;
        int expNeeded = 0;
        if (timeless) {
            expNeeded = ExpTable.getTimelessItemExpNeededForLevel(this.itemLevel + 1);
        }
        else {
            expNeeded = ExpTable.getReverseItemExpNeededForLevel(this.itemLevel + 1);
        }
        if (this.itemEXP >= expNeeded) {
            this.gainItemLevel(c, timeless);
            c.sendPacket(MaplePacketCreator.showItemLevelup());
        }
    }
    
    public void gainItemLevel(final MapleClient c, final boolean timeless) {
        final List<Pair<String, Integer>> stats = MapleItemInformationProvider.getInstance().getItemLevelupStats(this.getItemId(), this.itemLevel, timeless);
        for (final Pair<String, Integer> stat : stats) {
            final String s = stat.getLeft();
            switch (s) {
                case "incDEX": {
                    this.dex += stat.getRight();
                    continue;
                }
                case "incSTR": {
                    this.str += stat.getRight();
                    continue;
                }
                case "incINT": {
                    this._int += stat.getRight();
                    continue;
                }
                case "incLUK": {
                    this.luk += stat.getRight();
                    continue;
                }
                case "incMHP": {
                    this.hp += stat.getRight();
                    continue;
                }
                case "incMMP": {
                    this.mp += stat.getRight();
                    continue;
                }
                case "incPAD": {
                    this.watk += stat.getRight();
                    continue;
                }
                case "incMAD": {
                    this.matk += stat.getRight();
                    continue;
                }
                case "incPDD": {
                    this.wdef += stat.getRight();
                    continue;
                }
                case "incMDD": {
                    this.mdef += stat.getRight();
                    continue;
                }
                case "incEVA": {
                    this.avoid += stat.getRight();
                    continue;
                }
                case "incACC": {
                    this.acc += stat.getRight();
                    continue;
                }
                case "incSpeed": {
                    this.speed += stat.getRight();
                    continue;
                }
                case "incJump": {
                    this.jump += stat.getRight();
                    continue;
                }
            }
        }
        ++this.itemLevel;
        c.getPlayer().getClient().getSession().write(MaplePacketCreator.showEquipmentLevelUp());
        c.getPlayer().getClient().getSession().write(MaplePacketCreator.updateSpecialItemUse(this, this.getType()));
        c.getPlayer().getClient().getSession().write(MaplePacketCreator.getCharInfo(c.getPlayer()));
    }
    
    @Override
    public void setEquipLevel(final byte gf) {
        this.itemLevel = gf;
    }
    
    @Override
    public byte getEquipLevel() {
        return this.itemLevel;
    }
    
    public enum ScrollResult
    {
        成功, 
        失败, 
        消失;
    }
}
