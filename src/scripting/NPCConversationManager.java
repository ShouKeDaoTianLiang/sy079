package scripting;

import client.MapleJob;
import server.MaplePortal;
import java.util.LinkedHashSet;
import server.Timer.MapTimer;
import server.Timer.EventTimer;
import server.maps.MapleMapFactory;
import server.life.MonsterGlobalDropEntry;
import server.life.MonsterDropEntry;
import server.life.MapleMonsterInformationProvider;
import client.MapleQuestStatus;
import handling.world.World.Family;
import server.Timer.CloneTimer;
import server.StructPotentialItem;
import java.util.Collection;
import handling.world.MapleParty;
import handling.world.World.Alliance;
import server.MapleStatEffect;
import java.awt.Point;
import client.inventory.ItemFlag;
import java.util.HashMap;
import server.SpeedRunner;
import server.maps.SpeedRunType;
import java.util.Calendar;
import server.MapleCarnivalChallenge;
import server.MapleCarnivalParty;
import server.maps.AramiaFireWorks;
import server.maps.Event_PyramidSubway;
import server.maps.Event_DojoAgent;
import tools.Pair;
import java.util.ArrayList;
import client.inventory.ItemLoader;
import server.MerchItemPackage;
import tools.packet.PlayerShopPacket;
import server.MapleDueyActions;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import java.util.Arrays;
import server.maps.MapleMapObjectType;
import handling.channel.MapleGuildRanking;
import tools.FileoutputUtil1;
import tools.FileoutputUtil;
import java.rmi.RemoteException;
import handling.world.World.Guild;
import tools.StringUtil;
import server.MapleSquad;
import server.maps.MapleMap;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import client.MapleCharacter;
import client.SkillFactory;
import java.util.Map;
import client.SkillEntry;
import client.ISkill;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.List;
import client.inventory.MapleInventory;
import java.util.LinkedList;
import server.quest.MapleQuest;
import handling.world.World.Broadcast;
import constants.GameConstants;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.Randomizer;
import client.MapleStat;
import client.inventory.Equip;
import client.inventory.Item;
import tools.MaplePacketCreator;
import handling.world.guild.MapleGuild;
import server.maps.AramiaFireWorks1;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import server.MapleInventoryManipulator;
import client.inventory.MapleInventoryType;
import database.DatabaseConnection;
import javax.script.Invocable;
import client.MapleClient;

public class NPCConversationManager extends AbstractPlayerInteraction
{
    private final MapleClient c;
    private final int npc;
    private final int questid;
    private String getText;
    private final byte type;
    private byte lastMsg;
    public boolean pendingDisposal;
    private final Invocable iv;
    private int wh;
    
    public NPCConversationManager(final MapleClient c, final int npc, final int questid, final byte type, final Invocable iv, final int wh) {
        super(c);
        this.lastMsg = -1;
        this.pendingDisposal = false;
        this.wh = 0;
        this.c = c;
        this.npc = npc;
        this.questid = questid;
        this.type = type;
        this.iv = iv;
        this.wh = wh;
    }
    
    public void deleteItem(final int inventorytype) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("Select * from inventoryitems where characterid=? and inventorytype=?");
            ps.setInt(1, this.getPlayer().getId());
            ps.setInt(2, inventorytype);
            final ResultSet re = ps.executeQuery();
            MapleInventoryType type = null;
            switch (inventorytype) {
                case 1: {
                    type = MapleInventoryType.EQUIP;
                    break;
                }
                case 2: {
                    type = MapleInventoryType.USE;
                    break;
                }
                case 3: {
                    type = MapleInventoryType.SETUP;
                    break;
                }
                case 4: {
                    type = MapleInventoryType.ETC;
                    break;
                }
                case 5: {
                    type = MapleInventoryType.CASH;
                    break;
                }
            }
            while (re.next()) {
                MapleInventoryManipulator.removeById(this.getC(), type, re.getInt("itemid"), re.getInt("quantity"), true, true);
            }
            re.close();
            ps.close();
        }
        catch (SQLException ex) {}
    }
    
    public String getRecroNews() throws SQLException {
        final StringBuilder ret = new StringBuilder();
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT title, message, date FROM recronews ORDER BY newsid desc LIMIT 5");
        final ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n#e").append(rs.getString("title")).append(" - (").append(rs.getString("date")).append(")#n\r\n").append(rs.getString("message")).append("\r\n");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return ret.toString();
    }
    
    public long getKegs(final int i) {
        return AramiaFireWorks1.getInstance().getKegsPercentage(i);
    }
    
    public long getKegsMax(final int i) {
        return AramiaFireWorks1.getInstance().getAramiaFireWorkssl(i);
    }
    
    public void giveKegs(final int kegs, final int i) {
        AramiaFireWorks1.getInstance().giveKegs(this.c.getPlayer(), kegs, i);
    }
    
    public void give全服奖励(final int kegs, final int i) {
        AramiaFireWorks1.getInstance().give全服奖励(this.c.getPlayer(), kegs, i);
    }
    
    public void give召唤怪物(final int kegs, final int i) {
        AramiaFireWorks1.getInstance().give召唤怪物(this.c.getPlayer(), kegs, i);
    }
    
    public void giveKegsCall(final int i) {
        AramiaFireWorks1.getInstance().giveKegsCall(i);
    }
    
    public long getKegs数量(final int i) {
        return AramiaFireWorks1.getInstance().getKegs(i);
    }
    
    public int getwh() {
        return this.wh;
    }
    
    public Invocable getIv() {
        return this.iv;
    }
    
    public String serverName() {
        return this.c.getChannelServer().getServerName();
    }
    
    public int getNpc() {
        return this.npc;
    }
    
    public int getQuest() {
        return this.questid;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void 伤害排行榜() {
        MapleGuild.伤害排行(this.getClient(), this.npc);
    }
    
    public void 金币排行() {
        MapleGuild.meso(this.getClient(), this.npc);
    }
    
    public void 家族排名() {
        MapleGuild.displayGuildRanks(this.getClient(), this.npc);
    }
    
    public void 人气排名() {
        MapleGuild.人气排行(this.getClient(), this.npc);
    }
    
    public void 等级排名() {
        MapleGuild.displayLevelRanks(this.getClient(), this.npc);
    }
    
    public void 转生排名() {
        MapleGuild.转生排名(this.getClient(), this.npc);
    }
    
    public void safeDispose() {
        this.pendingDisposal = true;
    }
    
    public void dispose() {
        NPCScriptManager.getInstance().dispose(this.c);
    }
    
    public void askMapSelection(final String sel) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getMapSelection(this.npc, sel));
        this.lastMsg = 13;
    }
    
    public void sendNext(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendNextS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", type));
        this.lastMsg = 0;
    }
    
    public void sendPrev(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendPrev1(final String text, final int speaker) {
        this.getClient().getSession().write(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", (byte)speaker));
    }
    
    public void sendPrevS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", type));
        this.lastMsg = 0;
    }
    
    public void sendNextPrev(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", (byte)0));
        this.lastMsg = 0;
    }
    
    public void PlayerToNpc(final String text) {
        this.sendNextPrevS(text, (byte)3);
    }
    
    public void sendNextPrevS(final String text) {
        this.sendNextPrevS(text, (byte)3);
    }
    
    public void sendNextPrevS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", type));
        this.lastMsg = 0;
    }
    
    public void sendOk(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendOkS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", type));
        this.lastMsg = 0;
    }
    
    public void sendYesNo(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)1, text, "", (byte)0));
        this.lastMsg = 1;
    }
    
    public void sendYesNoS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)1, text, "", type));
        this.lastMsg = 1;
    }
    
    public void sendAcceptDecline(final String text) {
        this.askAcceptDecline(text);
    }
    
    public void sendAcceptDeclineNoESC(final String text) {
        this.askAcceptDeclineNoESC(text);
    }
    
    public void askAcceptDecline(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)11, text, "", (byte)0));
        this.lastMsg = 11;
    }
    
    public void askAcceptDeclineNoESC(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)12, text, "", (byte)0));
        this.lastMsg = 12;
    }
    
    public void askAvatar(final String text, final int card, final int[] args) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalkStyle(this.npc, text, card, args));
        this.lastMsg = 7;
    }
    
    public void sendSimple(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNext(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", (byte)0));
        this.lastMsg = 4;
    }
    
    public void sendSimple1(final String text) {
        this.getClient().getSession().write(MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", (byte)0));
    }
    
    public void sendOk1(final String text) {
        this.getClient().getSession().write(MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", (byte)0));
    }
    
    public void sendSimple(final String text, final int speaker) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNext(text);
            return;
        }
        this.getClient().getSession().write(MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", (byte)speaker));
        this.lastMsg = 4;
    }
    
    public void sendSimpleS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNextS(text, type);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", type));
        this.lastMsg = 4;
    }
    
    public void sendStyle(final String text, final int caid, final int[] styles) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalkStyle(this.npc, text, caid, styles));
        this.lastMsg = 7;
    }
    
    public Item lockitem(final int slot, final boolean lock) {
        byte set = 0;
        final byte eqslot = (byte)slot;
        final Equip nEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(eqslot);
        if (nEquip != null) {
            if (lock) {
                set = 1;
                this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 锁定成功");
            }
            else {
                this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 解除锁定成功");
            }
            nEquip.setFlag(set);
            this.c.sendPacket(MaplePacketCreator.getCharInfo(this.c.getPlayer()));
            this.getMap().removePlayer(this.c.getPlayer());
            this.getMap().addPlayer(this.c.getPlayer());
        }
        else {
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 装备为空.");
        }
        return nEquip;
    }
    
    public Item itemqh(final int slot, final byte lock) {
        final byte eqslot = (byte)slot;
        final Equip nEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(eqslot);
        final byte 等级 = nEquip.getLevel();
        final int 强化次数 = 127;
        if (等级 + nEquip.getUpgradeSlots() + lock <= 强化次数) {
            nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + lock));
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 强化次数成功");
            if (this.c.getPlayer().isGM()) {
                this.c.getPlayer().dropMessage("剩余强化次数：" + (强化次数 - (等级 + nEquip.getUpgradeSlots())));
            }
            this.c.sendPacket(MaplePacketCreator.getCharInfo(this.c.getPlayer()));
            this.getMap().removePlayer(this.c.getPlayer());
            this.getMap().addPlayer(this.c.getPlayer());
        }
        else if (等级 + nEquip.getUpgradeSlots() + lock > 强化次数) {
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 装备的强化次数已经满了.");
        }
        else {
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 装备为空.");
        }
        return nEquip;
    }
    
    public void 随机强化(final byte solo, final boolean tf) {
        this.qianghua1(solo, tf);
    }
    
    public Item qianghua1(final int slot, final boolean lock) {
        byte set = 0;
        final byte eqslot = (byte)slot;
        final Equip nEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(eqslot);
        if (nEquip != null) {
            if (nEquip.getOwner().equals("S级别") || nEquip.getOwner().equals("SS级别") || nEquip.getOwner().equals("SSS级别") || nEquip.getOwner().equals("A级别") || nEquip.getOwner().equals("B级别") || nEquip.getOwner().equals("C级别")) {
                this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 已经洗过一次了！无法进行再次洗练！");
                return nEquip;
            }
            String pz = "";
            final int str = (int)(Math.random() * 20.0);
            final int dex = (int)(Math.random() * 20.0);
            final int luk = (int)(Math.random() * 20.0);
            final int Int = (int)(Math.random() * 20.0);
            final int numbr = str + dex + luk + Int;
            if (numbr >= 75) {
                pz = "SSS级别";
            }
            else if (numbr >= 70 && numbr < 75) {
                pz = "SS级别";
            }
            else if (numbr >= 60 && numbr < 70) {
                pz = "S级别";
            }
            else if (numbr >= 50 && numbr < 60) {
                pz = "A级别";
            }
            else if (numbr >= 40 && numbr < 50) {
                pz = "B级别";
            }
            else if (numbr >= 0 && numbr < 40) {
                pz = "C级别";
            }
            final int str2 = nEquip.getStr() + str;
            final int dex2 = nEquip.getDex() + dex;
            final int luk2 = nEquip.getLuk() + luk;
            final int Int2 = nEquip.getInt() + Int;
            set = 1;
            nEquip.setStr((short)str2);
            nEquip.setDex((short)dex2);
            nEquip.setInt((short)Int2);
            nEquip.setLuk((short)luk2);
            nEquip.setOwner(pz);
            this.c.sendPacket(MaplePacketCreator.getCharInfo(this.c.getPlayer()));
            this.getMap().removePlayer(this.c.getPlayer());
            this.getMap().addPlayer(this.c.getPlayer());
            this.c.sendPacket(MaplePacketCreator.yellowChat("[系统信息]玩家:" + this.c.getPlayer().getName() + "的装备洗练出了 " + pz + " 属性。"));
        }
        else {
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 装备为空.");
        }
        return nEquip;
    }
    
    public void 随机血(final byte solo, final boolean tf) {
        this.qianghua2(solo, tf);
    }
    
    public Item qianghua2(final int slot, final boolean lock) {
        byte set = 0;
        final byte eqslot = (byte)slot;
        final Equip nEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(eqslot);
        if (nEquip != null) {
            if (nEquip.getHp() > 1) {
                this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 已经洗过一次了！无法进行再次洗练！");
                return nEquip;
            }
            final int str = (int)(Math.random() * 500.0);
            final int str2 = nEquip.getHp() + str;
            set = 1;
            this.c.getPlayer().dropMessage("[系统信息] 装备位置： " + slot + " 洗练出了 " + str2 + " 属性。");
            nEquip.setHp((short)str2);
            this.c.sendPacket(MaplePacketCreator.getCharInfo(this.c.getPlayer()));
            this.getMap().removePlayer(this.c.getPlayer());
            this.getMap().addPlayer(this.c.getPlayer());
        }
        else {
            this.c.getPlayer().dropMessage("[系统信息] 物品位置 " + slot + " 装备为空.");
        }
        return nEquip;
    }
    
    public void sendGetNumber(final String text, final int def, final int min, final int max) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalkNum(this.npc, text, def, min, max));
        this.lastMsg = 3;
    }
    
    public void sendGetText(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.sendPacket(MaplePacketCreator.getNPCTalkText(this.npc, text));
        this.lastMsg = 2;
    }
    
    public void setGetText(final String text) {
        this.getText = text;
    }
    
    public String getText() {
        return this.getText;
    }
    
    public void setHair(final int hair) {
        this.getPlayer().setHair(hair);
        this.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        this.getPlayer().equipChanged();
    }
    
    public void setFace(final int face) {
        this.getPlayer().setFace(face);
        this.getPlayer().updateSingleStat(MapleStat.FACE, face);
        this.getPlayer().equipChanged();
    }
    
    public void setSkin(final int color) {
        this.getPlayer().setSkinColor((byte)color);
        this.getPlayer().updateSingleStat(MapleStat.SKIN, color);
        this.getPlayer().equipChanged();
    }
    
    public int setRandomAvatar(final int ticket, final int[] args_all) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)(-1));
        final int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        }
        else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        }
        else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    
    public int setAvatar(final int ticket, final int args) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)(-1));
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        }
        else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        }
        else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    
    public void sendStorage() {
        this.c.getPlayer().setConversation(4);
        this.c.getPlayer().getStorage().sendStorage(this.c, this.npc);
    }
    
    public void openShop(final int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(this.c);
    }
    
    public int gainGachaponItem(final int id, final int quantity) {
        return this.gainGachaponItem(id, quantity, this.c.getPlayer().getMap().getStreetName() + " - " + this.c.getPlayer().getMap().getMapName());
    }
    
    public int gainGachaponItem(final int id, final int quantity, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + this.c.getPlayer().getName(), " : 恭喜获得道具!", item, rareness, this.getPlayer().getClient().getChannel()).getBytes());
            }
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int gainGachaponItem(final int id, final int quantity, final String msg, final int 概率) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            if (概率 > 0) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + this.c.getPlayer().getName(), " : 恭喜获得道具!", item, (byte)0, this.getPlayer().getClient().getChannel()).getBytes());
            }
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void changeJob(final int job) {
        this.c.getPlayer().changeJob(job);
    }
    
    public void changeJobById(final int jobId) {
        this.c.getPlayer().changeJob(jobId);
    }
    
    public void startQuest(final int id) {
        MapleQuest.getInstance(id).start(this.getPlayer(), this.npc);
    }
    
    @Override
    public void completeQuest(final int id) {
        MapleQuest.getInstance(id).complete(this.getPlayer(), this.npc);
    }
    
    public void forfeitQuest(final int id) {
        MapleQuest.getInstance(id).forfeit(this.getPlayer());
    }
    
    public void forceStartQuest() {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), null);
    }
    
    @Override
    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(this.getPlayer(), this.getNpc(), null);
    }
    
    public void forceStartQuest(final String customData) {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), customData);
    }
    
    public void completeQuest() {
        this.forceCompleteQuest();
    }
    
    public void forceCompleteQuest() {
        MapleQuest.getInstance(this.questid).forceComplete(this.getPlayer(), this.getNpc());
    }
    
    @Override
    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(this.getPlayer(), this.getNpc());
    }
    
    public String getQuestCustomData() {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).getCustomData();
    }
    
    public void setQuestCustomData(final String customData) {
        this.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).setCustomData(customData);
    }
    
    @Override
    public int getLevel() {
        return this.getPlayer().getLevel();
    }
    
    public int getMeso() {
        return this.getPlayer().getMeso();
    }
    
    public void gainAp(final int amount) {
        this.c.getPlayer().gainAp((short)amount);
    }
    
    public void expandInventory(final byte type, final int amt) {
        this.c.getPlayer().expandInventory(type, amt);
    }
    
    public void unequipEverything() {
        final MapleInventory equipped = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        final MapleInventory equip = this.getPlayer().getInventory(MapleInventoryType.EQUIP);
        final List<Short> ids = new LinkedList();
        for (final Item item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (final short id : ids) {
            MapleInventoryManipulator.unequip(this.getC(), id, equip.getNextFreeSlot());
        }
    }
    
    public final void clearSkills() {
        final Map<ISkill, SkillEntry> skills = this.getPlayer().getSkills();
        for (final Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            this.getPlayer().changeSkillLevel(skill.getKey(), (byte)0, (byte)0);
        }
    }
    
    public boolean hasSkill(final int skillid) {
        final ISkill theSkill = SkillFactory.getSkill(skillid);
        return theSkill != null && this.c.getPlayer().getSkillLevel(theSkill) > 0;
    }
    
    public void showEffect(final boolean broadcast, final String effect) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
        }
        else {
            this.c.sendPacket(MaplePacketCreator.showEffect(effect));
        }
    }
    
    public void playSound(final boolean broadcast, final String sound) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
        }
        else {
            this.c.sendPacket(MaplePacketCreator.playSound(sound));
        }
    }
    
    public void environmentChange(final boolean broadcast, final String env) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, 2));
        }
        else {
            this.c.sendPacket(MaplePacketCreator.environmentChange(env, 2));
        }
    }
    
    public void updateBuddyCapacity(final int capacity) {
        this.c.getPlayer().setBuddyCapacity((byte)capacity);
    }
    
    public int getBuddyCapacity() {
        return this.c.getPlayer().getBuddyCapacity();
    }
    
    public int partyMembersInMap() {
        int inMap = 0;
        for (final MapleCharacter char2 : this.getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() == this.getPlayer().getParty()) {
                ++inMap;
            }
        }
        return inMap;
    }
    
    public List<MapleCharacter> getPartyMembers() {
        if (this.getPlayer().getParty() == null) {
            return null;
        }
        final List<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            for (final ChannelServer channel : ChannelServer.getAllInstances()) {
                final MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) {
                    chars.add(ch);
                }
            }
        }
        return chars;
    }
    
    public void warpPartyWithExp(final int mapId, final int exp) {
        final MapleMap target = this.getMap(mapId);
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }
    
    public void warpPartyWithExpMeso(final int mapId, final int exp, final int meso) {
        final MapleMap target = this.getMap(mapId);
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }
    
    public MapleSquad getSquad(final String type) {
        return this.c.getChannelServer().getMapleSquad(type);
    }
    
    public int getSquadAvailability(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }
    
    public boolean registerSquad(final String type, final int minutes, final String startText) {
        if (this.c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(this.c.getChannel(), type, this.c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = this.c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = this.c.getPlayer().getMap();
                map.broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.c.getPlayer().getName() + startText));
            }
            else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }
    
    public boolean getSquadList(final String type, final byte type_) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return false;
        }
        if (type_ == 0 || type_ == 3) {
            this.sendNext(squad.getSquadMemberString(type_));
        }
        else if (type_ == 1) {
            this.sendSimple(squad.getSquadMemberString(type_));
        }
        else if (type_ == 2) {
            if (squad.getBannedMemberSize() > 0) {
                this.sendSimple(squad.getSquadMemberString(type_));
            }
            else {
                this.sendNext(squad.getSquadMemberString(type_));
            }
        }
        return true;
    }
    
    public byte isSquadLeader(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getLeader() != null && squad.getLeader().getId() == this.c.getPlayer().getId()) {
            return 1;
        }
        return 0;
    }
    
    public boolean reAdd(final String eim, final String squad) {
        final EventInstanceManager eimz = this.getDisconnected(eim);
        final MapleSquad squadz = this.getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(this.getPlayer());
            eimz.registerPlayer(this.getPlayer());
            return true;
        }
        return false;
    }
    
    public void banMember(final String type, final int pos) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }
    
    public void acceptMember(final String type, final int pos) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }
    
    public String getReadableMillis(final long startMillis, final long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }
    
    public int addMember(final String type, final boolean join) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.addMember(this.c.getPlayer(), join);
        }
        return -1;
    }
    
    public byte isSquadMember(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getMembers().contains(this.c.getPlayer())) {
            return 1;
        }
        if (squad.isBanned(this.c.getPlayer())) {
            return 2;
        }
        return 0;
    }
    
    public void resetReactors() {
        this.getPlayer().getMap().resetReactors();
    }
    
    public void genericGuildMessage(final int code) {
        this.c.sendPacket(MaplePacketCreator.genericGuildMessage((byte)code));
    }
    
    public void disbandGuild() {
        final int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0 || this.c.getPlayer().getGuildRank() != 1) {
            return;
        }
        Guild.disbandGuild(gid);
    }
    
    public void increaseGuildCapacity() {
        if (this.c.getPlayer().getMeso() < 2500000) {
            this.c.sendPacket(MaplePacketCreator.serverNotice(1, "You do not have enough mesos."));
            return;
        }
        final int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        Guild.increaseGuildCapacity(gid);
        this.c.getPlayer().gainMeso(-2500000, true, false, true);
    }
    
    public void 喇叭(final int lx, final String msg) throws RemoteException {
        switch (lx) {
            case 1: {
                Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(19, this.c.getChannel(), this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
            case 2: {
                Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(12, this.c.getChannel(), this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
            case 3: {
                Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, this.c.getChannel(), this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
            case 4: {
                this.c.sendPacket(MaplePacketCreator.yellowChat("[系统信息]" + msg + ""));
                break;
            }
            case 5: {
                this.c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(9, this.c.getChannel(), "" + msg + ""));
                break;
            }
        }
    }
    
    public void 记录(final int lx, final String msg) throws RemoteException {
        switch (lx) {
            case 1: {
                FileoutputUtil1.玩家强化记录("" + this.c.getPlayer().getName() + "强化记录.txt", "" + msg + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
                break;
            }
            case 2: {
                FileoutputUtil1.玩家强化记录("" + this.c.getPlayer().getName() + "提升次数记录.txt", "" + msg + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
                break;
            }
            case 3: {
                FileoutputUtil1.玩家兑换记录("" + this.c.getPlayer().getName() + "兑换记录.txt", "" + msg + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
            }
        }
    }
    
    public void displayGuildRanks() {
        this.c.sendPacket(MaplePacketCreator.showGuildRanks(this.npc, MapleGuildRanking.getInstance().getGuildRank()));
    }
    
    public boolean removePlayerFromInstance() {
        if (this.c.getPlayer().getEventInstance() != null) {
            this.c.getPlayer().getEventInstance().removePlayer(this.c.getPlayer());
            return true;
        }
        return false;
    }
    
    public boolean isPlayerInstance() {
        return this.c.getPlayer().getEventInstance() != null;
    }
    
    public void changeStat(final byte slot, final int type, final short amount) {
        final Equip sel = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0: {
                sel.setStr(amount);
                break;
            }
            case 1: {
                sel.setDex(amount);
                break;
            }
            case 2: {
                sel.setInt(amount);
                break;
            }
            case 3: {
                sel.setLuk(amount);
                break;
            }
            case 4: {
                sel.setHp(amount);
                break;
            }
            case 5: {
                sel.setMp(amount);
                break;
            }
            case 6: {
                sel.setWatk(amount);
                break;
            }
            case 7: {
                sel.setMatk(amount);
                break;
            }
            case 8: {
                sel.setWdef(amount);
                break;
            }
            case 9: {
                sel.setMdef(amount);
                break;
            }
            case 10: {
                sel.setAcc(amount);
                break;
            }
            case 11: {
                sel.setAvoid(amount);
                break;
            }
            case 12: {
                sel.setHands(amount);
                break;
            }
            case 13: {
                sel.setSpeed(amount);
                break;
            }
            case 14: {
                sel.setJump(amount);
                break;
            }
            case 15: {
                sel.setUpgradeSlots((byte)amount);
                break;
            }
            case 16: {
                sel.setViciousHammer((byte)amount);
                break;
            }
            case 17: {
                sel.setLevel((byte)amount);
                break;
            }
            case 18: {
                sel.setEnhance((byte)amount);
                break;
            }
            case 19: {
                sel.setPotential1(amount);
                break;
            }
            case 20: {
                sel.setPotential2(amount);
                break;
            }
            case 21: {
                sel.setPotential3(amount);
                break;
            }
            case 22: {
                sel.setOwner(this.getText());
                break;
            }
        }
        this.c.getPlayer().equipChanged();
    }
    
    public void killAllMonsters() {
        final MapleMap map = this.c.getPlayer().getMap();
        final double range = Double.POSITIVE_INFINITY;
        for (final MapleMapObject monstermo : map.getMapObjectsInRange(this.c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            final MapleMonster mob = (MapleMonster)monstermo;
            if (mob.getStats().isBoss()) {
                map.killMonster(mob, this.c.getPlayer(), false, false, (byte)1);
            }
        }
    }
    
    public void giveMerchantMesos() {
        long mesos = 0L;
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            }
            else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE hiredmerchants SET mesos = 0 WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        this.c.getPlayer().gainMeso((int)mesos, true);
    }
    
    public void dc() {
        final MapleCharacter victim = this.c.getChannelServer().getPlayerStorage().getCharacterByName(this.c.getPlayer().getName());
        victim.getClient().getSession().close();
        victim.getClient().disconnect(true, false);
    }
    
    public long getMerchantMesos() {
        long mesos = 0L;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            }
            else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        return mesos;
    }
    
    public void openDuey() {
        this.c.getPlayer().setConversation(2);
        this.c.sendPacket(MaplePacketCreator.sendDuey((byte)9, null));
    }
    
    public void openMerchantItemStore() {
        this.c.getPlayer().setConversation(3);
        this.c.sendPacket(PlayerShopPacket.merchItemStore((byte)34));
    }
    
    public void openMerchantItemStore1() {
        final MerchItemPackage pack = loadItemFrom_Database(this.c.getPlayer().getId(), this.c.getPlayer().getAccountID());
        this.c.sendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
    }
    
    private static final MerchItemPackage loadItemFrom_Database(final int charid, final int accountid) {
        final Connection con = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accountid);
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            final int packageid = rs.getInt("PackageId");
            final MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));
            ps.close();
            rs.close();
            final Map<Integer, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, charid);
            if (items != null) {
                final List<Item> iters = new ArrayList<Item>();
                for (final Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }
            return pack;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void sendRepairWindow() {
        this.c.sendPacket(MaplePacketCreator.sendRepairWindow(this.npc));
    }
    
    public final int getDojoPoints() {
        return this.c.getPlayer().getDojo();
    }
    
    public final int getDojoRecord() {
        return this.c.getPlayer().getDojoRecord();
    }
    
    public void setDojoRecord(final boolean reset) {
        this.c.getPlayer().setDojoRecord(reset);
    }
    
    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(this.c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(this.c.getPlayer(), party);
    }
    
    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(this.c.getPlayer());
    }
    
    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(this.c.getPlayer());
    }
    
    public final short getKegs() {
        return AramiaFireWorks.getInstance().getKegsPercentage();
    }
    
    public void giveKegs(final int kegs) {
        AramiaFireWorks.getInstance().giveKegs(this.c.getPlayer(), kegs);
    }
    
    public final short getSunshines() {
        return AramiaFireWorks.getInstance().getSunsPercentage();
    }
    
    public void addSunshines(final int kegs) {
        AramiaFireWorks.getInstance().giveSuns(this.c.getPlayer(), kegs);
    }
    
    public final short getDecorations() {
        return AramiaFireWorks.getInstance().getDecsPercentage();
    }
    
    public void addDecorations(final int kegs) {
        try {
            AramiaFireWorks.getInstance().giveDecs(this.c.getPlayer(), kegs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public final MapleInventory getInventory(final int type) {
        return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
    }
    
    public final MapleCarnivalParty getCarnivalParty() {
        return this.c.getPlayer().getCarnivalParty();
    }
    
    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return this.c.getPlayer().getNextCarnivalRequest();
    }
    
    public int getHour() {
        return Calendar.getInstance().get(11);
    }
    
    public int getMin() {
        return Calendar.getInstance().get(12);
    }
    
    public int getSec() {
        return Calendar.getInstance().get(13);
    }
    
    public final MapleCarnivalChallenge getCarnivalChallenge(final MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }
    
    public void setHP(final short hp) {
        this.c.getPlayer().getStat().setHp(hp);
    }
    
    public void maxStats() {
        final List statup = new ArrayList(2);
        this.c.getPlayer().getStat().setStr((short)32767);
        this.c.getPlayer().getStat().setDex((short)32767);
        this.c.getPlayer().getStat().setInt((short)32767);
        this.c.getPlayer().getStat().setLuk((short)32767);
        this.c.getPlayer().getStat().setMaxHp((short)30000);
        this.c.getPlayer().getStat().setMaxMp((short)30000);
        this.c.getPlayer().getStat().setHp(30000);
        this.c.getPlayer().getStat().setMp(30000);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.DEX, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LUK, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.INT, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, 30000));
        this.c.sendPacket(MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer().getJob()));
    }
    
    public Pair<String, Map<Integer, String>> getSpeedRun(final String typ) {
        final SpeedRunType type = SpeedRunType.valueOf(typ);
        if (SpeedRunner.getInstance().getSpeedRunData(type) != null) {
            return SpeedRunner.getInstance().getSpeedRunData(type);
        }
        return new Pair<String, Map<Integer, String>>("", new HashMap<Integer, String>());
    }
    
    public boolean getSR(final Pair<String, Map<Integer, String>> ma, final int sel) {
        if (ma.getRight().get(sel) == null || ma.getRight().get(sel).length() <= 0) {
            this.dispose();
            return false;
        }
        this.sendOk(ma.getRight().get(sel));
        return true;
    }
    
    public Equip getEquip(final int itemid) {
        return (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }
    
    public void setExpiration(final Object statsSel, final long expire) {
        if (statsSel instanceof Equip) {
            ((Item)statsSel).setExpiration(System.currentTimeMillis() + expire * 24L * 60L * 60L * 1000L);
        }
    }
    
    public void setLock(final Object statsSel) {
        if (statsSel instanceof Equip) {
            final Equip eq = (Equip)statsSel;
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            }
            else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }
    
    public boolean addFromDrop(final Object statsSel) {
        if (statsSel instanceof Item) {
            final Item it = (Item)statsSel;
            return MapleInventoryManipulator.checkSpace(this.getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(this.getClient(), it, false);
        }
        return false;
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int offset, final String type) {
        return this.replaceItem(slot, invType, statsSel, offset, type, false);
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int offset, final String type, final boolean takeSlot) {
        final MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        if (inv == null) {
            return false;
        }
        Item item = this.getPlayer().getInventory(inv).getItem((short)slot);
        if (item == null || statsSel instanceof Item) {
            item = (Item)statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            final Equip eq = (Equip)item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                }
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() - 1));
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() + offset));
            }
            else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte)(eq.getLevel() + offset));
            }
            else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte)(eq.getViciousHammer() + offset));
            }
            else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short)(eq.getStr() + offset));
            }
            else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short)(eq.getDex() + offset));
            }
            else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short)(eq.getInt() + offset));
            }
            else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short)(eq.getLuk() + offset));
            }
            else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short)(eq.getHp() + offset));
            }
            else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short)(eq.getMp() + offset));
            }
            else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short)(eq.getWatk() + offset));
            }
            else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short)(eq.getMatk() + offset));
            }
            else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short)(eq.getWdef() + offset));
            }
            else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short)(eq.getMdef() + offset));
            }
            else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short)(eq.getAcc() + offset));
            }
            else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short)(eq.getAvoid() + offset));
            }
            else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short)(eq.getHands() + offset));
            }
            else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short)(eq.getSpeed() + offset));
            }
            else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short)(eq.getJump() + offset));
            }
            else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            }
            else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration(eq.getExpiration() + offset);
            }
            else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte)(eq.getFlag() + offset));
            }
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            }
            else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(this.getClient(), inv, (short)slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int upgradeSlots) {
        return this.replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }
    
    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }
    
    public void buffGuild(final int buff, final int duration, final String msg) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && this.getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() == this.getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                    }
                }
            }
        }
    }
    
    public boolean createAlliance(final String alliancename) {
        final MapleParty pt = this.c.getPlayer().getParty();
        final MapleCharacter otherChar = this.c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == this.c.getPlayer().getId()) {
            return false;
        }
        try {
            return Alliance.createAlliance(alliancename, this.c.getPlayer().getId(), otherChar.getId(), this.c.getPlayer().getGuildId(), otherChar.getGuildId());
        }
        catch (Exception re) {
            re.printStackTrace();
            return false;
        }
    }
    
    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                this.gainMeso(-10000000);
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }
    
    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && Alliance.disbandAlliance(gs.getAllianceId())) {
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }
    
    public byte getLastMsg() {
        return this.lastMsg;
    }
    
    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }
    
    public final void maxAllSkills() {
        for (final ISkill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId())) {
                this.teachSkill(skil.getId(), skil.getMaxLevel(), skil.getMaxLevel());
            }
        }
    }
    
    public final void resetStats(final int str, final int dex, final int z, final int luk) {
        this.c.getPlayer().resetStats(str, dex, z, luk);
    }
    
    public final boolean dropItem(final int slot, final int invType, final int quantity) {
        final MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        return inv != null && MapleInventoryManipulator.drop(this.c, inv, (short)slot, (short)quantity, true);
    }
    
    public final List<Integer> getAllPotentialInfo() {
        return new ArrayList<Integer>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
    }
    
    public final String getPotentialInfo(final int id) {
        final List<StructPotentialItem> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1;
        int maxLevel = 10;
        for (final StructPotentialItem item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }
    
    public final void sendRPS() {
        this.c.sendPacket(MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
    }
    
    public final void setQuestRecord(final Object ch, final int questid, final String data) {
        ((MapleCharacter)ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }
    
    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter)ch;
        CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    NPCConversationManager.this.warpMap(680000500, 0);
                }
            }
        }, 10000L);
        CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    if (NPCConversationManager.this.getPlayer() != null) {
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "3");
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160002, "0");
                    }
                    else if (chr != null) {
                        NPCConversationManager.this.setQuestRecord(chr, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(chr, 160002, "0");
                    }
                    NPCConversationManager.this.warpMap(680000500, 0);
                }
                else {
                    NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "2");
                    NPCConversationManager.this.setQuestRecord(chr, 160001, "2");
                    NPCConversationManager.this.sendNPCText(NPCConversationManager.this.getPlayer().getName() + " and " + chr.getName() + ", I wish you two all the best on your AsteriaSEA journey together!", 9201002);
                    NPCConversationManager.this.getMap().startExtendedMapEffect("You may now kiss the bride, " + NPCConversationManager.this.getPlayer().getName() + "!", 5120006);
                    if (chr.getGuildId() > 0) {
                        Guild.guildPacket(chr.getGuildId(), MaplePacketCreator.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        Family.familyPacket(chr.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (NPCConversationManager.this.getPlayer().getGuildId() > 0) {
                        Guild.guildPacket(NPCConversationManager.this.getPlayer().getGuildId(), MaplePacketCreator.sendMarriage(false, NPCConversationManager.this.getPlayer().getName()));
                    }
                    if (NPCConversationManager.this.getPlayer().getFamilyId() > 0) {
                        Family.familyPacket(NPCConversationManager.this.getPlayer().getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), NPCConversationManager.this.getPlayer().getId());
                    }
                }
            }
        }, 20000L);
    }
    
    public void openDD(final int type) {
        this.c.sendPacket(MaplePacketCreator.openBeans(this.getPlayer().getBeans(), type));
    }
    
    public void worldMessage(final String text) {
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, text).getBytes());
    }
    
    public int getBeans() {
        return this.getClient().getPlayer().getBeans();
    }
    
    public void gainBeans(final int s) {
        this.getPlayer().gainBeans(s);
        this.c.sendPacket(MaplePacketCreator.updateBeans(this.c.getPlayer().getId(), s));
    }
    
    public int getHyPay(final int type) {
        return this.getPlayer().getHyPay(type);
    }
    
    public void szhs(final String ss) {
        this.c.sendPacket(MaplePacketCreator.游戏屏幕中间黄色字体(ss));
    }
    
    public void szhs(final String ss, final int id) {
        this.c.sendPacket(MaplePacketCreator.游戏屏幕中间黄色字体(ss, id));
    }
    
    public int gainHyPay(final int hypay) {
        return this.getPlayer().gainHyPay(hypay);
    }
    
    public int addHyPay(final int hypay) {
        return this.getPlayer().addHyPay(hypay);
    }
    
    public int delPayReward(final int pay) {
        return this.getPlayer().delPayReward(pay);
    }
    
    public int getItemLevel(final int id) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return ii.getReqLevel(id);
    }
    
    public void alatPQ() {
    }
    
    public void xlkc(final long days) {
        final MapleQuestStatus marr = this.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122700));
        if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
            this.getPlayer().dropMessage(1, "项链扩充失败，您已经进行过项链扩充。");
        }
        else {
            final String customData = String.valueOf(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
            this.getPlayer().getQuestNAdd(MapleQuest.getInstance(122700)).setCustomData(customData);
            this.getPlayer().dropMessage(1, "项链" + days + "扩充扩充成功！");
        }
    }
    
    public void outputWithLogging(final int mobId, final String buff) {
        final String file = "drop_data\\" + mobId + ".sql";
        FileoutputUtil.log(file, buff, true);
    }
    
    public String checkDrop(final int mobId) {
        final List ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0;
            int itemId = 0;
            int ch = 0;
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); ++i) {
                final MonsterDropEntry de = (server.life.MonsterDropEntry)ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (ii.itemExists(itemId)) {
                        if (num == 0) {
                            name.append("当前怪物 #o").append(mobId).append("# 的爆率为:\r\n");
                            name.append("--------------------------------------\r\n");
                        }
                        String namez = "#z" + itemId + "#";
                        if (itemId == 0) {
                            itemId = 4031041;
                            namez = de.Minimum * this.getClient().getChannelServer().getMesoRate() + " - " + de.Maximum * this.getClient().getChannelServer().getMesoRate() + " 的金币";
                        }
                        ch = de.chance * this.getClient().getChannelServer().getDropRate();
                        if (this.getPlayer().isAdmin()) {
                            name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append(" - ").append(((ch >= 999999) ? 1000000 : ch) / 10000.0).append("%的爆率. ").append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("需要接受任务: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                        }
                        else {
                            name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("需要接受任务: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                        }
                        ++num;
                    }
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return "没有找到这个怪物的爆率数据。";
    }
    
    public String checkMapDrop() {
        final List ranks = new ArrayList(MapleMonsterInformationProvider.getInstance().getGlobalDrop());
        final int mapid = this.c.getPlayer().getMap().getId();
        final int cashServerRate = this.getClient().getChannelServer().getCashRate();
        final int globalServerRate = 1;
        if (ranks != null && ranks.size() > 0) {
            int num = 0;
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); ++i) {
                final MonsterGlobalDropEntry de = (server.life.MonsterGlobalDropEntry)ranks.get(i);
                if (de.continent < 0 || (de.continent < 10 && mapid / 100000000 == de.continent) || (de.continent < 100 && mapid / 10000000 == de.continent) || (de.continent < 1000 && mapid / 1000000 == de.continent)) {
                    int itemId = de.itemId;
                    if (num == 0) {
                        name.append("当前地图 #r").append(mapid).append("#k - #m").append(mapid).append("# 的全局爆率为:");
                        name.append("\r\n--------------------------------------\r\n");
                    }
                    String names = "#z" + itemId + "#";
                    if (itemId == 0 && cashServerRate != 0) {
                        itemId = 4031041;
                        names = de.Minimum * cashServerRate + " - " + de.Maximum * cashServerRate + " 的抵用卷";
                    }
                    final int chance = de.chance * globalServerRate;
                    if (this.getPlayer().isAdmin()) {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(names).append(" - ").append(((chance >= 999999) ? 1000000 : chance) / 10000.0).append("%的爆率. ").append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("需要接受任务: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                    }
                    else {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(names).append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("需要接受任务: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                    }
                    ++num;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return "当前地图没有设置全局爆率。";
    }
    
    public int getzb() {
        int money = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                money = rs.getInt("money");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return money;
    }
    
    public void setzb(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            try (final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET money =money+ " + slot + " WHERE id = " + cid + "")) {
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public int getmoneyb() {
        int moneyb = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                moneyb = rs.getInt("moneyb");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return moneyb;
    }
    
    public void setmoneyb(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET moneyb =moneyb+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public MapleMapFactory getMapFactory() {
        return this.getClient().getChannelServer().getMapFactory();
    }
    
    public void warpBack(final int mid, final int retmap, final int time) {
        final MapleMap warpMap = this.c.getChannelServer().getMapFactory().getMap(mid);
        this.c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
        this.c.sendPacket(MaplePacketCreator.getClock(time));
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                final MapleMap warpMap = c.getChannelServer().getMapFactory().getMap(retmap);
                if (c.getPlayer() != null) {
                    c.sendPacket(MaplePacketCreator.stopClock());
                    c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
                    c.getPlayer().dropMessage(6, "到达目的地ヘ!");
                }
            }
        }, 1000 * time);
    }
    
    public void warpMapWithClock(final int mid, final int seconds) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(seconds));
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (c.getPlayer() != null) {
                    for (final MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                        chr.changeMap(mid);
                    }
                }
            }
        }, seconds * 1000);
    }
    
    public void ShowMarrageEffect() {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendMarrageEffect());
    }
    
    public String EquipList(final MapleClient c) {
        final StringBuilder str = new StringBuilder();
        final MapleInventory equip = this.getPlayer().getInventory(MapleInventoryType.EQUIP);
        final List<String> stra = new LinkedList<String>();
        for (final Item item : equip.list()) {
            stra.add("#L" + item.getPosition() + "##v" + item.getItemId() + "##l");
        }
        for (final String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }
    
    public void 刷新地图() {
        final boolean custMap = true;
        final int mapid = this.c.getPlayer().getMapId();
        final MapleMap map = custMap ? this.c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(mapid) : this.c.getPlayer().getMap();
        if (this.c.getPlayer().getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
            final MapleMap newMap = this.c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(mapid);
            final MaplePortal newPor = newMap.getPortal(0);
            final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters());
            for (final MapleCharacter m : mcs) {
                int x = 0;
                while (x < 5) {
                    try {
                        m.changeMap(newMap, newPor);
                    }
                    catch (Throwable t) {
                        ++x;
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    public int gettuiguang() {
        int 推广人 = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM characters WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                推广人 = rs.getInt("推广人");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {}
        return 推广人;
    }
    
    public void settuiguang(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE characters SET 推广人 =推广人+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {}
    }
    
    public int gettuiguang22() {
        int tuiguang2 = 0;
        try {
            final int cid = this.getPlayer().gettuiguang();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM characters WHERE id=" + cid + " WHERE name = ?");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                tuiguang2 = rs.getInt("name");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {}
        return tuiguang2;
    }
    
    public int gettuiguang2() {
        int 推广值 = 0;
        try {
            final int cid = this.getPlayer().gettuiguang();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM characters WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                推广值 = rs.getInt("推广值");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {}
        return 推广值;
    }
    
    public void settuiguang2(final int slot) {
        try {
            final int cid = this.getPlayer().gettuiguang();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET money =money+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {}
    }
    
    public int getcz() {
        int chongzhi = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                chongzhi = rs.getInt("chongzhi");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {}
        return chongzhi;
    }
    
    public void setcz(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET chongzhi =chongzhi+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {}
    }
    
    public void yqm(final int yqm) {
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                if (yqm != mch.getId()) {
                    return;
                }
            }
        }
    }
    
    public void 即时存档() {
        this.c.getPlayer().saveToDB(true, true);
    }
    
    public boolean isBeginner() {
        return this.c.getPlayer().getJob() == MapleJob.新手.getId();
    }
    
    public void 刷新状态() {
        this.c.getPlayer().getClient().getSession().write(MaplePacketCreator.getCharInfo(this.c.getPlayer()));
        this.c.getPlayer().getMap().removePlayer(this.c.getPlayer());
        this.c.getPlayer().getMap().addPlayer(this.c.getPlayer());
        this.c.sendPacket(MaplePacketCreator.enableActions());
    }
}
