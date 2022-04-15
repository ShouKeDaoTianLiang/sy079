package constants;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public enum MapleItemType
{
    帽子(100, "帽子"), 
    脸饰(101, "脸饰"), 
    眼饰(102, "眼饰"), 
    耳环(103, "耳环"), 
    上衣(104, "上衣"), 
    套服(105, "套服"), 
    下衣(106, "下衣"), 
    鞋子(107, "鞋子"), 
    手套(108, "手套"), 
    披风(110, "披风"), 
    戒指(111, "戒指"), 
    项链(112, "项链"), 
    腰带(113, "腰带"), 
    勋章(114, "勋章"), 
    新飞侠_null(124, "新飞侠_null"), 
    单手剑(130, "单手剑"), 
    单手斧(131, "单手斧"), 
    单手锤(132, "单手锤"), 
    短刀(133, "短刀"), 
    手杖(136, "手杖"), 
    短杖(137, "短杖"), 
    长杖(138, "长杖"), 
    透明_null武器(139, "透明_null武器"), 
    双手剑(140, "双手剑"), 
    双手斧(141, "双手斧"), 
    双手锤(142, "双手锤"), 
    枪(143, "枪"), 
    矛(144, "矛"), 
    弓(145, "弓"), 
    弩(146, "弩"), 
    拳套(147, "拳套"), 
    指节(148, "指节"), 
    手铳(149, "手铳"), 
    技能名字显示(160, "技能名字显示"), 
    点装武器(170, "点装武器"), 
    宠物装备(180, "宠物装备"), 
    龙神尾巴(197, "龙神尾巴"), 
    药水(200, "药水"), 
    回城卷(203, "回城卷"), 
    卷轴(204, "卷轴"), 
    治疗药剂(205, "治疗药剂"), 
    弓箭(206, "弓箭"), 
    飞镖(207, "飞镖"), 
    技能书(228, "技能书"), 
    能手册(229, "能手册"), 
    子弹(233, "子弹"), 
    幸运的狩猎(245, "幸运的狩猎"), 
    靠垫(301, "靠垫"), 
    怪物道具(400, "怪物道具"), 
    矿石(401, "矿石"), 
    宝石(402, "宝石"), 
    特殊道具(403, "特殊道具"), 
    表情(404, "表情"), 
    辅助剂(413, "辅助剂"), 
    蛋(417, "蛋"), 
    御守(420, "御守"), 
    宝石成品(425, "宝石成品"), 
    怪物结晶(426, "怪物结晶"), 
    谜之蛋(428, "谜之蛋"), 
    新年贺卡(430, "新年贺卡");
    
    private final int typeId;
    private final String typeName;
    private static final Logger log;
    
    private MapleItemType(final int typeid, final String stringName) {
        this.typeId = typeid;
        this.typeName = stringName;
    }
    
    public int getTypeId() {
        return this.typeId;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public static MapleItemType getItemTypeByTypeId(final int typeid) {
        for (final MapleItemType et : values()) {
            if (et.typeId == typeid) {
                return et;
            }
        }
        return null;
    }
    
    public static MapleItemType getItemTypeByItemId(final int itemid) {
        for (final MapleItemType et : values()) {
            if (et.typeId == itemid / 10000) {
                return et;
            }
        }
        return null;
    }
    
    public static boolean is装备(final int itemid) {
        boolean ret = false;
        if (itemid / 10000 >= MapleItemType.帽子.getTypeId() && itemid / 10000 <= MapleItemType.龙神尾巴.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    public static boolean is消耗(final int itemid) {
        boolean ret = false;
        if (itemid / 10000 >= MapleItemType.药水.getTypeId() && itemid / 10000 <= MapleItemType.幸运的狩猎.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    public static boolean is道具(final int itemid) {
        boolean ret = false;
        if (itemid / 10000 >= MapleItemType.怪物道具.getTypeId() && itemid / 10000 <= MapleItemType.新年贺卡.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    public boolean is装备() {
        boolean ret = false;
        if (this.typeId >= MapleItemType.帽子.getTypeId() && this.typeId <= MapleItemType.龙神尾巴.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    public boolean is消耗() {
        boolean ret = false;
        if (this.typeId >= MapleItemType.药水.getTypeId() && this.typeId <= MapleItemType.幸运的狩猎.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    public boolean is道具() {
        boolean ret = false;
        if (this.typeId >= MapleItemType.怪物道具.getTypeId() && this.typeId <= MapleItemType.新年贺卡.getTypeId()) {
            ret = true;
        }
        return ret;
    }
    
    static {
        log = LoggerFactory.getLogger(MapleItemType.class);
    }
}
