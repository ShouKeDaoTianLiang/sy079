package client.messages.commands;

import client.inventory.MapleInventory;
import java.util.LinkedHashMap;
import client.LoginCrypto;
import handling.login.handler.AutoRegister;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;
import client.inventory.MapleInventoryType;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import tools.CPUSampler;
import server.life.OverrideMonsterStats;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.life.MapleMonsterInformationProvider;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import tools.MapleAESOFB;
import io.netty.channel.Channel;
import handling.netty.MapleSession;
import tools.MockIOSession;
import server.life.PlayerNPC;
import provider.MapleDataProvider;
import tools.Pair;
import provider.MapleDataTool;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import java.io.File;
import server.life.MapleNPC;
import server.life.MapleLifeFactory;
import java.util.HashMap;
import server.maps.MapleMap;
import java.util.Arrays;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactorStats;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import client.anticheat.CheatingOffense;
import server.MaplePortal;
import server.quest.MapleQuest;
import java.util.Map;
import handling.world.CheaterData;
import handling.world.World;
import client.MapleCharacterUtil;
import server.life.MobSkillFactory;
import client.MapleDisease;
import server.events.MapleEventType;
import server.events.MapleEvent;
import scripting.EventManager;
import tools.packet.MobPacket;
import server.life.MapleMonster;
import client.inventory.MapleRing;
import server.MapleItemInformationProvider;
import client.inventory.Equip;
import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;
import server.maps.MapleMapObject;
import java.awt.Point;
import client.inventory.Item;
import java.util.ArrayList;
import tools.StringUtil;
import server.MapleInventoryManipulator;
import tools.FileoutputUtil;
import scripting.NPCScriptManager;
import server.MapleShopFactory;
import constants.ServerConstants;
import client.ISkill;
import client.messages.CommandProcessorUtil;
import client.SkillFactory;
import java.text.DateFormat;
import handling.world.World.Find;
import client.MapleStat;
import java.util.List;
import client.MapleCharacter;
import handling.world.World.Broadcast;
import tools.MaplePacketCreator;
import server.Timer.EventTimer;
import java.util.concurrent.ScheduledFuture;
import server.ShutdownServer;
import java.util.Iterator;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.PreparedStatement;
import database.DatabaseConnection;
import com.mysql.jdbc.Connection;
import server.Start;
import client.MapleClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import constants.ServerConstants.PlayerGMRank;

public class AdminCommand
{
    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }
    
    private static String joinStringFrom(final String[] arr, final int start) {
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; ++i) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
    
    public static String now(final String dateFormat) {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }
    
    public static class 开放地图 extends openmap
    {
    }
    
    public static class 关闭地图 extends closemap
    {
    }
    
    public static class 注册 extends register
    {
    }
    
    public static class 满属性 extends maxstats
    {
    }
    
    public static class 满血 extends maxstats1
    {
    }
    
    public static class 满技能 extends maxSkills
    {
    }
    
    public static class 拉全部 extends WarpAllHere
    {
    }
    
    public static class 给金币 extends mesoEveryone
    {
    }
    
    public static class 给经验 extends ExpEveryone
    {
    }
    
    public static class 给所有人点卷 extends CashEveryone
    {
    }
    
    public static class 给点卷 extends GainCash
    {
    }
    
    public static class 刷新地图 extends ReloadMap
    {
    }
    
    public static class 祝福 extends buff
    {
    }
    
    public static class 倍率设置 extends setRate
    {
    }
    
    public static class 地图代码 extends WhereAmI
    {
    }
    
    public static class 刷 extends 制造商
    {
    }
    
    public static class 丢 extends Drop
    {
    }
    
    public static class 全部复活 extends HealMap
    {
    }
    
    public static class 清怪 extends KillAll
    {
    }
    
    public static class 设置人气 extends Fame
    {
    }
    
    public static class 吸怪 extends MobVac
    {
    }
    
    public static class 清除地板 extends cleardrops
    {
    }
    
    public static class 召唤 extends Spawn
    {
    }
    
    public static class 计时器 extends Clock
    {
    }
    
    public static class 自动注册 extends autoreg
    {
    }
    
    public static class 怪物代码 extends mob
    {
    }
    
    public static class 人数上限 extends setUserLimit
    {
    }
    
    public static class 封号状态 extends BanStatus
    {
    }
    
    public static class 关闭全服摆摊 extends SavePlayerShops
    {
    }
    
    public static class 关闭服务器 extends Shutdown
    {
    }
    
    public static class 关闭服务器时间 extends ShutdownTime
    {
    }
    
    public static class Debug extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setDebugMessage(!c.getPlayer().getDebugMessage());
            return 1;
        }
    }
    
    public static class 设置检测白名单 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                Start.设置白名单(splitted[1]);
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "设置白名单出错." + e.getMessage());
            }
            c.getPlayer().dropMessage(6, "设置白名单成功");
            return 1;
        }
    }
    
    public static class 玩家物品清除 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final Connection dcon = (Connection)DatabaseConnection.getConnection();
            try {
                final int id = 0;
                final int quantity = 0;
                final PreparedStatement ps2 = (PreparedStatement)dcon.prepareStatement("delete from inventoryitems WHERE itemid = ?");
                ps2.setInt(1, Integer.parseInt(splitted[1]));
                ps2.executeUpdate();
                c.getPlayer().dropMessage(6, "所有ID为 " + splitted[1] + " 的道具" + quantity + "已经从所有玩家身上被移除了");
                ps2.close();
                return 1;
            }
            catch (SQLException e) {
                return 0;
            }
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <物品ID> <角色名稱> - 移除玩家身上的道具").toString();
        }
    }
    
    public static class 生怪 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
    }
    
    public static class BanStatus extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            String mac = "";
            String ip = "";
            int acid = 0;
            boolean Systemban = false;
            boolean ACbanned = false;
            boolean IPbanned = false;
            boolean MACbanned = false;
            String reason = null;
            try {
                final java.sql.Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = (PreparedStatement)con.prepareStatement("select accountid from characters where name = ?");
                ps.setString(1, name);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        acid = rs.getInt("accountid");
                    }
                }
                ps = (PreparedStatement)con.prepareStatement("select banned, banreason, macs, Sessionip from accounts where id = ?");
                ps.setInt(1, acid);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Systemban = (rs.getInt("banned") == 2);
                        ACbanned = (rs.getInt("banned") == 1 || rs.getInt("banned") == 2);
                        reason = rs.getString("banreason");
                        mac = rs.getString("macs");
                        ip = rs.getString("Sessionip");
                    }
                }
                ps.close();
            }
            catch (Exception ex) {}
            if (reason == null || reason == "") {
                reason = "?";
            }
            if (c.isBannedIP(ip)) {
                IPbanned = true;
            }
            if (c.isBannedMac(mac)) {
                MACbanned = true;
            }
            c.getPlayer().dropMessage("玩家[" + name + "] 帐号ID[" + acid + "]是否被封锁: " + (ACbanned ? "是" : "否") + (Systemban ? "(系统自动封锁)" : "") + ", 原因: " + reason);
            c.getPlayer().dropMessage("IP: " + ip + " 是否在封锁IP名单: " + (IPbanned ? "是" : "否"));
            c.getPlayer().dropMessage("MAC: " + mac + " 是否在封锁MAC名单: " + (MACbanned ? "是" : "否"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!BanStatus <產嘿> - 琩產琌砆玛の").toString();
        }
    }
    
    public static class setUserLimit extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int UserLimit = LoginServer.getUserLimit();
            try {
                UserLimit = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            LoginServer.setUserLimit(UserLimit);
            c.getPlayer().dropMessage("服务器人数上限已更改为" + UserLimit);
            return 1;
        }
    }
    
    public static class SavePlayerShops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            c.getPlayer().dropMessage(6, "雇佣商人储存完毕.");
            return 1;
        }
    }
    
    public static class Shutdown extends CommandExecute
    {
        private static Thread t;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "关闭服务器...");
            if (Shutdown.t == null || !Shutdown.t.isAlive()) {
                (Shutdown.t = new Thread(ShutdownServer.getInstance())).start();
            }
            else {
                c.getPlayer().dropMessage(6, "已在执行中...");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdown - 关闭服务器").toString();
        }
        
        static {
            Shutdown.t = null;
        }
    }
    
    public static class ShutdownTime extends CommandExecute
    {
        private static ScheduledFuture<?> ts;
        private int minutesLeft;
        private static Thread t;
        
        public ShutdownTime() {
            this.minutesLeft = 0;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            this.minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "服务器将在 " + this.minutesLeft + "分钟后关闭. 请尽速关闭精灵商人 并下线.");
            if (ShutdownTime.ts == null && (ShutdownTime.t == null || !ShutdownTime.t.isAlive())) {
                ShutdownTime.t = new Thread(ShutdownServer.getInstance());
                ShutdownTime.ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        final StringBuilder message = new StringBuilder();
                        message.append("[冒险岛公告] 服务器将在 ");
                        message.append(minutesLeft);
                        message.append("分钟后关闭. 请尽速关闭精灵商人 并下线.");
                        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, message.toString()).getBytes());
                        Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()).getBytes());
                        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
                            cs.setServerMessage("服务器将于 " + minutesLeft + " 分钟后开启");
                        }
                        minutesLeft--;
                    }
                }, 60000L);
            }
            else {
                c.getPlayer().dropMessage(6, "服务器关闭时间修改为 " + this.minutesLeft + "分钟后，请稍等服务器关闭");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <秒数> - 关闭服务器").toString();
        }
        
        static {
            ShutdownTime.ts = null;
            ShutdownTime.t = null;
        }
    }
    
    public static class SaveAll extends CommandExecute
    {
        private int p;
        
        public SaveAll() {
            this.p = 0;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                final List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (final MapleCharacter chr : chrs) {
                    ++this.p;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[保存] " + this.p + "个玩家数据保存到数据中.");
            this.p = 0;
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!saveall - 保存所有角色資料").toString();
        }
    }
    
    public static class LowHP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getStat().setHp(1);
            c.getPlayer().getStat().setMp(1);
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lowhp - 血魔归ㄧ").toString();
        }
    }
    
    public static class Heal extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().dispelDebuffs();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!heal - 补满血魔").toString();
        }
    }
    
    public static class 解锁玩家 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL 错误.");
            }
            else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] 角色不存在.");
            }
            else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            }
            else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP或Mac已解锁其中一個.");
            }
            else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP以及Mac已成功解锁.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!解锁玩家 <玩家名称> - 解锁玩家").toString();
        }
    }
    
    public static class TempBan extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);
            final Calendar cal = Calendar.getInstance();
            cal.add(5, numDay);
            final DateFormat df = DateFormat.getInstance();
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] 找不到目标角色");
            }
            else {
                victim.tempban("由" + c.getPlayer().getName() + "暂时锁定了", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " 已成功被暂时锁定至 " + df.format(cal.getTime()));
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!tempban <玩家名称> - 暂时锁定玩家").toString();
        }
    }
    
    public static class Kill extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return 0;
            }
            for (int i = 1; i < splitted.length; ++i) {
                final String name = splitted[1];
                final int ch = Find.findChannel(name);
                if (ch <= 0) {
                    return 0;
                }
                final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.getPlayer().dropMessage(6, "[kill] 玩家 " + splitted[i] + " 不存在.");
                }
                else if (player.allowedToTarget(victim)) {
                    victim.getStat().setHp(0);
                    victim.getStat().setMp(0);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!kill <玩家名称1> <玩家名称2> ...  - 杀掉玩家").toString();
        }
    }
    
    public static class Skill extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            final byte masterlevel = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!skill <技能ID> [技能等級] [技能最大等級] ...  - 学习技能").toString();
        }
    }
    
    public static class Fame extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage("!fame <角色名称> <名声> ...  - 名声");
                return 0;
            }
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            short fame;
            try {
                fame = Short.parseShort(splitted[2]);
            }
            catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "不合法的数字");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            }
            else {
                c.getPlayer().dropMessage(6, "[fame] 角色不存在");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fame <角色名称> <名声> ...  - 名声").toString();
        }
    }
    
    public static class autoreg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage("目前自动注册已经 " + ServerConstants.ChangeAutoReg());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!autoreg  - 自动注册开关").toString();
        }
    }
    
    public static class HealMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            for (final MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp());
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp());
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                    mch.dispelDebuffs();
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!healmap  - 治愈地图上所有的人").toString();
        }
    }
    
    public static class GodMode extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "无敌已经关闭");
            }
            else {
                player.setInvincible(true);
                player.dropMessage(6, "无敌已经开启.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!godmode  - 无敌开关").toString();
        }
    }
    
    public static class 给予技能 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            final byte masterlevel = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!给予技能 <玩家名称> <技能ID> [技能等級] [技能最大等級] - 给予技能").toString();
        }
    }
    
    public static class SP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.sendPacket(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!sp [数量] - 增加SP").toString();
        }
    }
    
    public static class AP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setRemainingAp((short)CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!ap [数量] - 增加AP").toString();
        }
    }
    
    public static class 给予技能点 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.setRemainingAp((short)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 2));
            victim.updateSingleStat(MapleStat.AVAILABLEAP, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 2));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!ap [数量] - 增加AP").toString();
        }
    }
    
    public static class Shop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId;
            try {
                shopId = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                return 0;
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            else {
                c.getPlayer().dropMessage(5, "此商店ID不存在");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shop - 开启商店").toString();
        }
    }
    
    public static class 关键时刻 extends CommandExecute
    {
        protected static ScheduledFuture<?> ts;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 1) {
                return 0;
            }
            if (关键时刻.ts != null) {
                关键时刻.ts.cancel(false);
                c.getPlayer().dropMessage(0, "原定的关键时刻已取消");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                return 0;
            }
            if (minutesLeft > 0) {
                关键时刻.ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                                if (!c.getPlayer().isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010);
                                }
                            }
                        }
                        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "关键时刻已经开始了!!!").getBytes());
                        Broadcast.broadcastMessage(MaplePacketCreator.serverMessage("关键时刻已经开始了!!!").getBytes());
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60 * 1000);
                c.getPlayer().dropMessage(0, "关键时刻预定已完成");
            }
            else {
                c.getPlayer().dropMessage(0, "设定的时间必须 > 0。");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!关键时刻 <时间:分钟> - 关键时刻").toString();
        }
        
        static {
            关键时刻.ts = null;
        }
    }
    
    public static class GainCash extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            MapleCharacter player = c.getPlayer();
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            }
            catch (Exception ex) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("已经收到点卷" + amount + "点");
            final String msg = "[GM 密语] GM " + c.getPlayer().getName() + " 給了 " + player.getName() + " 点卷 " + amount + "点";
            FileoutputUtil.logToFile("日志/Logs/Data/给予点卷.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " 给了 " + player.getName() + " 点卷 " + amount + "点");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gaingash <數量> <玩家> - 取得Gash点数").toString();
        }
    }
    
    public static class GainMaplePoint extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int amount = Integer.parseInt(splitted[1]);
            final String name = splitted[2];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                return 0;
            }
            player.modifyCSPoints(2, amount, true);
            final String msg = "[GM 密语] GM " + c.getPlayer().getName() + " 给了 " + player.getName() + " 枫叶点数 " + amount + "点";
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <數量> <玩家> - 取得枫叶点数").toString();
        }
    }
    
    public static class GainPoint extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int amount = Integer.parseInt(splitted[1]);
            final String name = splitted[2];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                return 0;
            }
            player.setPoints(player.getPoints() + amount);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainpoint <數量> <玩家> - 取得Point").toString();
        }
    }
    
    public static class GainVP extends GainPoint
    {
    }
    
    public static class LevelUp extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().levelUp();
            }
            else {
                int up = 0;
                try {
                    up = Integer.parseInt(splitted[1]);
                }
                catch (Exception ex) {}
                for (int i = 0; i < up; ++i) {
                    c.getPlayer().levelUp();
                }
            }
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!levelup - 等級上升").toString();
        }
    }
    
    public static class 道具 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "用法: !道具 [物品ID] [数量]");
                return 0;
            }
            final int item = Integer.parseInt(splitted[1]);
            final int quantity = Integer.parseInt(splitted[2]);
            MapleInventoryManipulator.addById(c, item, (short)quantity, (byte)0);
            return 1;
        }
    }
    
    public static class serverMsg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                for (final ChannelServer ch : ChannelServer.getAllInstances()) {
                    ch.setServerMessage(sb.toString());
                }
                Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()).getBytes());
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!servermsg 讯息 - 更改上方黃色公告").toString();
        }
    }
    
    public static class Say extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!say 讯息 - 服务器公告").toString();
        }
    }
    
    public static class Letter extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "指令规则: ");
                return 0;
            }
            int start;
            int nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            }
            else {
                if (!splitted[1].equalsIgnoreCase("red")) {
                    c.getPlayer().dropMessage(6, "未知的顏色!");
                    return 1;
                }
                start = 3991000;
                nstart = 3990009;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            final List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            for (int i = 0; i < splitString.length(); ++i) {
                final char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                }
                else if (chr >= 'A' && chr <= 'Z') {
                    chars.add((int)chr);
                }
                else if (chr >= '0' && chr <= '9') {
                    chars.add(chr + 'È');
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - splitString.length() / 2 * 32;
            for (final Integer j : chars) {
                if (j == -1) {
                    dStart += 32;
                }
                else if (j < 200) {
                    final int val = start + j - 65;
                    final Item item = new Item(val, (byte)0, (short)1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += 32;
                }
                else {
                    if (j < 200 || j > 300) {
                        continue;
                    }
                    final int val = nstart + j - 48 - 200;
                    final Item item = new Item(val, (byte)0, (short)1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += 32;
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - 送信").toString();
        }
    }
    
    public static class Marry extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "错误的戒指ID.");
            }
            else {
                final String name = splitted[1];
                final int ch = Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "玩家必须在线");
                    return 0;
                }
                final MapleCharacter fff = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "玩家必须在线");
                }
                else {
                    final int[] ringID = { MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance() };
                    try {
                        final MapleCharacter[] chrz = { fff, c.getPlayer() };
                        for (int i = 0; i < chrz.length; ++i) {
                            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "错误的戒指ID.");
                                return 1;
                            }
                            eq.setUniqueId(ringID[i]);
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].dropMessage(6, "成功与  " + chrz[0].getName() + " 结婚");
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    }
                    catch (SQLException ex) {}
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!marry <玩家名称> <戒指代码> - 结婚").toString();
        }
    }
    
    public static class ItemCheck extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3 || splitted[1] == null || splitted[1].isEmpty() || splitted[2] == null || splitted[2].isEmpty()) {
                return 0;
            }
            final int item = Integer.parseInt(splitted[2]);
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int itemamount = chr.getItemQuantity(item, true);
            if (itemamount > 0) {
                c.getPlayer().dropMessage(6, chr.getName() + " 有 " + itemamount + " (" + item + ").");
            }
            else {
                c.getPlayer().dropMessage(6, chr.getName() + " 并沒有 (" + item + ")");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - 检查物品").toString();
        }
    }
    
    public static class MobVac extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                final MapleMonster monster = (MapleMonster)mmo;
                c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, -1, 0, 0, 0, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getPosition(), c.getPlayer().getLastRes()));
                monster.setPosition(c.getPlayer().getPosition());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mobvac - 全图吸怪").toString();
        }
    }
    
    public static class Song extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(splitted[1]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!song - 播放音乐").toString();
        }
    }
    
    public static class 开启自动活动 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!开启自动活动 - 开启自动活动").toString();
        }
    }
    
    public static class 活动开始 extends CommandExecute
    {
        private static ScheduledFuture<?> ts;
        private int min;
        
        public 活动开始() {
            this.min = 1;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "已经关闭活动入口，可以使用 !活动开始 來启动。");
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "頻道:" + c.getChannel() + "活动目前已经关闭大门口。").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                活动开始.ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (min == 0) {
                            MapleEvent.onStartEvent(c.getPlayer());
                            ts.cancel(false);
                            return;
                        }
                        min--;
                    }
                }, 60000L);
                return 1;
            }
            c.getPlayer().dropMessage(5, "您必须先使用 ! 设定當前頻道的活动，并在当前頻道活动地图里使用。");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!活动开始 - 活动开始").toString();
        }
        
        static {
            活动开始.ts = null;
        }
    }
    
    public static class 关闭活动入口 extends CommandExecute
    {
        private static boolean tt;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "已经关闭活动入口，可以使用 !活动开始 來启动。");
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "頻道:" + c.getChannel() + "活动目前已经关闭大门口。").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        tt = true;
                    }
                }, 60000L);
                if (关闭活动入口.tt) {
                    MapleEvent.onStartEvent(c.getPlayer());
                }
                return 1;
            }
            c.getPlayer().dropMessage(5, "您必须先使用 !选择活动 设定当前頻道的活动，并在当前頻道活动地图里使用。");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!关闭活动入口 -关闭活动入口").toString();
        }
        
        static {
            关闭活动入口.tt = false;
        }
    }
    
    public static class 选择活动 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("目前开放的活动有: ");
                for (final MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!选择活动 - 选择活动").toString();
        }
    }
    
    public static class CheckGash extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter chrs = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "找不到该角色");
            }
            else {
                c.getPlayer().dropMessage(6, chrs.getName() + " 有 " + chrs.getCSPoints(1) + " 点数.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!checkgash <玩家名称> - 检查点数").toString();
        }
    }
    
    public static class RemoveItem extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final String name = splitted[1];
            final int id = Integer.parseInt(splitted[2]);
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家并不存在");
            }
            else {
                chr.removeAll(id);
                c.getPlayer().dropMessage(6, "所有ID为 " + id + " 的道具已经从 " + name + " 身上被移除了");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <角色名称> <物品ID> - 移除玩家身上的道具").toString();
        }
    }
    
    public static class KillMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp(0);
                    map.getStat().setMp(0);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmap - 杀掉所有玩家").toString();
        }
    }
    
    public static class SpeakMega extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = null;
            if (splitted.length >= 2) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            }
            try {
                Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, (victim == null) ? c.getChannel() : victim.getClient().getChannel(), (victim == null) ? splitted[1] : (victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2)), true).getBytes());
            }
            catch (Exception e) {
                return 0;
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakmega [玩家名称] <讯息> - 对某个玩家的頻道进行广播").toString();
        }
    }
    
    public static class Speak extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + splitted[1]);
                return 0;
            }
            victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speak <玩家名称> <讯息> - 对某个玩家发信息").toString();
        }
    }
    
    public static class SpeakMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakmap <讯息> - 对目前地图进行发送信息").toString();
        }
    }
    
    public static class SpeakChannel extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <讯息> - 对目前频道进行发送信息").toString();
        }
    }
    
    public static class SpeakWorld extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <讯息> - 对目前服务器进行传送信息").toString();
        }
    }
    
    public static class 给予状态 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            int type;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            }
            else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            }
            else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            }
            else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            }
            else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            }
            else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            }
            else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            }
            else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            }
            else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            }
            else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            }
            else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            }
            else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            }
            else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            }
            else {
                if (!splitted[1].equalsIgnoreCase("FREEZE")) {
                    return 0;
                }
                type = 137;
            }
            final MapleDisease dis = MapleDisease.getBySkill(type);
            if (splitted.length == 4) {
                final String name = splitted[2];
                final int ch = Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "玩家必须在线");
                    return 0;
                }
                final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "找不到此玩家");
                }
                else {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
                }
            }
            else {
                for (final MapleCharacter victim2 : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim2.setChair(0);
                    victim2.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim2.getMap().broadcastMessage(victim2, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim2.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!给予状态 <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [角色名称] <状态等级> - 让人得到特殊状态").toString();
        }
    }
    
    public static class SendAllNote extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length >= 1) {
                final String text = StringUtil.joinStringFrom(splitted, 1);
                for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <文字> 传送Note給目前頻道的所有人").toString();
        }
    }
    
    public static class giveMeso extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int gain = Integer.parseInt(splitted[2]);
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + name);
            }
            else {
                victim.gainMeso(gain, true);
                final String msg = "[GM 密语] GM " + c.getPlayer().getName() + " 给了 " + victim.getName() + " 金币 " + gain + "点";
                Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <名字> <数量> - 給玩家金币").toString();
        }
    }
    
    public static class CloneMe extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!cloneme - 产生克隆体").toString();
        }
    }
    
    public static class DisposeClones extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "个克隆体消失了.");
            c.getPlayer().disposeClones();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - 摧毁克隆体").toString();
        }
    }
    
    public static class Monitor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                }
                else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            }
            else {
                c.getPlayer().dropMessage(5, "找不到该玩家");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!monitor <玩家> - 记录玩家资讯").toString();
        }
    }
    
    public static class 设置天气 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "地图天气已被禁用.");
            }
            else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "无效的ID.");
                }
                else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "地图天气已启用.");
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!设置天气 - 设定天气").toString();
        }
    }
    
    public static class CharInfo extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final StringBuilder builder = new StringBuilder();
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter other = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (other == null) {
                builder.append("角色不存在");
                c.getPlayer().dropMessage(6, builder.toString());
            }
            else {
                if (other.getClient().getLastPing() <= 0L) {
                    other.getClient().sendPing();
                }
                builder.append(MapleClient.getLogMessage(other, ""));
                builder.append(" 在 ").append(other.getPosition().x);
                builder.append(" /").append(other.getPosition().y);
                builder.append(" || 血量 : ");
                builder.append(other.getStat().getHp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxHp());
                builder.append(" || 魔量 : ");
                builder.append(other.getStat().getMp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxMp());
                builder.append(" || 物理攻擊力 : ");
                builder.append(other.getStat().getTotalWatk());
                builder.append(" || 魔法攻擊力 : ");
                builder.append(other.getStat().getTotalMagic());
                builder.append(" || 最高攻擊 : ");
                builder.append(other.getStat().getCurrentMaxBaseDamage());
                builder.append(" || 攻擊%數 : ");
                builder.append(other.getStat().dam_r);
                builder.append(" || BOSS攻擊%數 : ");
                builder.append(other.getStat().bossdam_r);
                builder.append(" || 力量 : ");
                builder.append(other.getStat().getStr());
                builder.append(" || 敏捷 : ");
                builder.append(other.getStat().getDex());
                builder.append(" || 智力 : ");
                builder.append(other.getStat().getInt());
                builder.append(" || 幸運 : ");
                builder.append(other.getStat().getLuk());
                builder.append(" || 全部力量 : ");
                builder.append(other.getStat().getTotalStr());
                builder.append(" || 全部敏捷 : ");
                builder.append(other.getStat().getTotalDex());
                builder.append(" || 全部智力 : ");
                builder.append(other.getStat().getTotalInt());
                builder.append(" || 全部幸運 : ");
                builder.append(other.getStat().getTotalLuk());
                builder.append(" || 經驗值 : ");
                builder.append(other.getExp());
                builder.append(" || 組隊狀態 : ");
                builder.append(other.getParty() != null);
                builder.append(" || 交易狀態: ");
                builder.append(other.getTrade() != null);
                builder.append(" || Latency: ");
                builder.append(other.getClient().getLatency());
                builder.append(" || 最後PING: ");
                builder.append(other.getClient().getLastPing());
                builder.append(" || 最後PONG: ");
                builder.append(other.getClient().getLastPong());
                builder.append(" || IP: ");
                builder.append(other.getClient().getSessionIPAddress());
                other.getClient().DebugMessage(builder);
                c.getPlayer().dropMessage(6, builder.toString());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!charinfo <角色名称> - 查看角色状态").toString();
        }
    }
    
    public static class whoishere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            StringBuilder builder = new StringBuilder("在此地图的玩家: ");
            for (final MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) {
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!whoishere - 查看目前地图上的玩家").toString();
        }
    }
    
    public static class Cheaters extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; --x) {
                final CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!cheaters - 查看作弊角色").toString();
        }
    }
    
    public static class Connected extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Map<Integer, Integer> connected = World.getConnected();
            final StringBuilder conStr = new StringBuilder("已连接的客戶端: ");
            boolean first = true;
            for (final int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                }
                else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("所有: ");
                    conStr.append(connected.get(i));
                }
                else {
                    conStr.append("頻道 ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!connected - 查看已连线的客戶端").toString();
        }
    }
    
    public static class ResetQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetquest <任务ID> - 重置任务").toString();
        }
    }
    
    public static class StartQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!startquest <任务ID> - 开始任务").toString();
        }
    }
    
    public static class CompleteQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!completequest <任务ID> - 完成任务").toString();
        }
    }
    
    public static class FStartQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), (splitted.length >= 4) ? splitted[3] : null);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fstartquest <任务ID> - 强制开始任务").toString();
        }
    }
    
    public static class FCompleteQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fcompletequest <任务ID> - 强制完成任务").toString();
        }
    }
    
    public static class FStartOther extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), (splitted.length >= 4) ? splitted[4] : null);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fstartother - 不知道啥").toString();
        }
    }
    
    public static class FCompleteOther extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fcompleteother - 不知道啥").toString();
        }
    }
    
    public static class NearestPortal extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!nearestportal - 不知道啥").toString();
        }
    }
    
    public static class SpawnDebug extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawndebug - debug怪物出生").toString();
        }
    }
    
    public static class Threads extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; ++i) {
                final String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!threads - 查看Threads资讯").toString();
        }
    }
    
    public static class ShowTrace extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            final Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (final StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!showtrace - show trace info").toString();
        }
    }
    
    public static class FakeRelog extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            c.sendPacket(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fakerelog - 假登出再登入").toString();
        }
    }
    
    public static class ToggleOffense extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            try {
                final CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            }
            catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!toggleoffense <Offense> - 开启或关闭CheatOffense").toString();
        }
    }
    
    public static class toggleDrop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!toggledrop - 开启或关闭掉落").toString();
        }
    }
    
    public static class ToggleMegaphone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "广播是否封锁 : " + (c.getChannelServer().getMegaphoneMuteState() ? "是" : "否"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - 开启或者关闭广播").toString();
        }
    }
    
    public static class SpawnReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final MapleReactorStats reactorSt = MapleReactorFactory.getReactor(id);
            final MapleReactor reactor = new MapleReactor(reactorSt, id);
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawnreactor - 设立Reactor").toString();
        }
    }
    
    public static class HReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - 触碰Reactor").toString();
        }
    }
    
    public static class DestroyReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (final MapleMapObject reactorL : reactors) {
                    final MapleReactor reactor2l = (MapleReactor)reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            }
            else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!drstroyreactor - 移除Reactor").toString();
        }
    }
    
    public static class ResetReactors extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetReactors();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetreactors - 重置此地图所有的Reactor").toString();
        }
    }
    
    public static class SetReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - 触碰Reactor").toString();
        }
    }
    
    public static class cleardrops extends RemoveDrops
    {
    }
    
    public static class RemoveDrops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "清除了 " + c.getPlayer().getMap().getNumItems() + " 个掉落物");
            c.getPlayer().getMap().removeDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removedrops - 移除地上的物品").toString();
        }
    }
    
    public static class ExpRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "经验倍率已改变更为 " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!exprate <倍率> - 更改经验倍率").toString();
        }
    }
    
    public static class DropRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "掉宝倍率已改变更为 " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!droprate <倍率> - 更改掉落倍率").toString();
        }
    }
    
    public static class MesoRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "金币爆率已改变更为 " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mesorate <倍率> - 更改金钱倍率").toString();
        }
    }
    
    public static class DCAll extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int range = -1;
            if (splitted.length < 2) {
                return 0;
            }
            String input = null;
            try {
                input = splitted[1];
            }
            catch (Exception ex) {}
            final String s = splitted[1];
            switch (s) {
                case "m": {
                    range = 0;
                    break;
                }
                case "c": {
                    range = 1;
                    break;
                }
                default: {
                    range = 2;
                    break;
                }
            }
            if (range == -1) {
                range = 1;
            }
            switch (range) {
                case 0: {
                    c.getPlayer().getMap().disconnectAll();
                    break;
                }
                case 1: {
                    c.getChannelServer().getPlayerStorage().disconnectAll();
                    break;
                }
                case 2: {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.getPlayerStorage().disconnectAll(true);
                    }
                    break;
                }
            }
            String show = "";
            switch (range) {
                case 0: {
                    show = "地图";
                    break;
                }
                case 1: {
                    show = "頻道";
                    break;
                }
                case 2: {
                    show = "世界";
                    break;
                }
            }
            final String msg = "[GM 密语] GM " + c.getPlayer().getName() + "  DC 了 " + show + "玩家";
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - 所有玩家断线").toString();
        }
    }
    
    public static class GoTo extends CommandExecute
    {
        private static final HashMap<String, Integer> gotomaps;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
            }
            else if (GoTo.gotomaps.containsKey(splitted[1])) {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(GoTo.gotomaps.get(splitted[1]));
                final MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            }
            else if (splitted[1].equals("locations")) {
                c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                final StringBuilder sb = new StringBuilder();
                for (final String s : GoTo.gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            }
            else {
                c.getPlayer().dropMessage(6, "Invalid command 指令規則 - Use !goto <location>. For a list of locations, use !goto locations.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!goto <名称> - 到某个地图").toString();
        }
        
        static {
            (gotomaps = new HashMap<String, Integer>()).put("gmmap", 180000000);
            GoTo.gotomaps.put("southperry", 2000000);
            GoTo.gotomaps.put("amherst", 1010000);
            GoTo.gotomaps.put("henesys", 100000000);
            GoTo.gotomaps.put("ellinia", 101000000);
            GoTo.gotomaps.put("perion", 102000000);
            GoTo.gotomaps.put("kerning", 103000000);
            GoTo.gotomaps.put("lithharbour", 104000000);
            GoTo.gotomaps.put("sleepywood", 105040300);
            GoTo.gotomaps.put("florina", 110000000);
            GoTo.gotomaps.put("orbis", 200000000);
            GoTo.gotomaps.put("happyville", 209000000);
            GoTo.gotomaps.put("elnath", 211000000);
            GoTo.gotomaps.put("ludibrium", 220000000);
            GoTo.gotomaps.put("aquaroad", 230000000);
            GoTo.gotomaps.put("leafre", 240000000);
            GoTo.gotomaps.put("mulung", 250000000);
            GoTo.gotomaps.put("herbtown", 251000000);
            GoTo.gotomaps.put("omegasector", 221000000);
            GoTo.gotomaps.put("koreanfolktown", 222000000);
            GoTo.gotomaps.put("newleafcity", 600000000);
            GoTo.gotomaps.put("sharenian", 990000000);
            GoTo.gotomaps.put("pianus", 230040420);
            GoTo.gotomaps.put("horntail", 240060200);
            GoTo.gotomaps.put("chorntail", 240060201);
            GoTo.gotomaps.put("mushmom", 100000005);
            GoTo.gotomaps.put("griffey", 240020101);
            GoTo.gotomaps.put("manon", 240020401);
            GoTo.gotomaps.put("zakum", 280030000);
            GoTo.gotomaps.put("czakum", 280030001);
            GoTo.gotomaps.put("papulatus", 220080001);
            GoTo.gotomaps.put("showatown", 801000000);
            GoTo.gotomaps.put("zipangu", 800000000);
            GoTo.gotomaps.put("ariant", 260000100);
            GoTo.gotomaps.put("nautilus", 120000000);
            GoTo.gotomaps.put("boatquay", 541000000);
            GoTo.gotomaps.put("malaysia", 550000000);
            GoTo.gotomaps.put("taiwan", 740000000);
            GoTo.gotomaps.put("thailand", 500000000);
            GoTo.gotomaps.put("erev", 130000000);
            GoTo.gotomaps.put("ellinforest", 300000000);
            GoTo.gotomaps.put("kampung", 551000000);
            GoTo.gotomaps.put("singapore", 540000000);
            GoTo.gotomaps.put("amoria", 680000000);
            GoTo.gotomaps.put("timetemple", 270000000);
            GoTo.gotomaps.put("pinkbean", 270050100);
            GoTo.gotomaps.put("peachblossom", 700000000);
            GoTo.gotomaps.put("fm", 910000000);
            GoTo.gotomaps.put("freemarket", 910000000);
            GoTo.gotomaps.put("oxquiz", 109020001);
            GoTo.gotomaps.put("ola", 109030101);
            GoTo.gotomaps.put("fitness", 109040000);
            GoTo.gotomaps.put("snowball", 109060000);
            GoTo.gotomaps.put("cashmap", 741010200);
            GoTo.gotomaps.put("golden", 950100000);
            GoTo.gotomaps.put("phantom", 610010000);
            GoTo.gotomaps.put("cwk", 610030000);
            GoTo.gotomaps.put("rien", 140000000);
        }
    }
    
    public static class KillAll extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            boolean drop = false;
            if (splitted.length > 1) {
                final int irange = 9999;
                if (splitted.length < 2) {
                    range = irange * irange;
                }
                else {
                    try {
                        map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        range = Integer.parseInt(splitted[2]) * Integer.parseInt(splitted[2]);
                    }
                    catch (Exception ex) {}
                }
                if (splitted.length >= 3) {
                    drop = splitted[3].equalsIgnoreCase("true");
                }
            }
            final List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (final MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                final MapleMonster mob = (MapleMonster)monstermo;
                map.killMonster(mob, c.getPlayer(), drop, false, (byte)1);
            }
            c.getPlayer().dropMessage("您总共杀了 " + monsters.size() + " 怪物");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - 杀掉所有怪物").toString();
        }
    }
    
    public static class ResetMobs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetmobs - 重置地图上所有怪物").toString();
        }
    }
    
    public static class KillMonster extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final double range = Double.POSITIVE_INFINITY;
            for (final MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                final MapleMonster mob = (MapleMonster)monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmonster <mobid> - 杀掉地图上某个怪物").toString();
        }
    }
    
    public static class KillMonsterByOID extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final int targetId = Integer.parseInt(splitted[1]);
            final MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte)1);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmonsterbyoid <moboid> - 杀掉地图上某个怪物").toString();
        }
    }
    
    public static class 清怪爆物 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " (<范围:默认0全图> (地图:默认当前地图))");
            }
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.length > 1) {
                final int irange = Integer.parseInt(splitted[1]);
                if (irange != 0) {
                    range = irange * irange;
                }
                if (splitted.length > 2) {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "地图不存在");
                return 0;
            }
            for (final MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                final MapleMonster mob = (MapleMonster)monstermo;
                if (!mob.getStats().isBoss() || mob.getStats().isPartyBonus() || c.getPlayer().isGM()) {
                    map.killMonster(mob, c.getPlayer(), true, false, (byte)1);
                }
            }
            return 1;
        }
    }
    
    public static class HitMonsterByOID extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleMap map = c.getPlayer().getMap();
            final int targetId = Integer.parseInt(splitted[1]);
            final int damage = Integer.parseInt(splitted[2]);
            final MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - 碰撞地图上某個怪物").toString();
        }
    }
    
    public static class NPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int npcId = 0;
            try {
                npcId = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
            }
            else {
                c.getPlayer().dropMessage(6, "找不到此代码为" + npcId + "的Npc");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!npc <npcid> - 呼叫出NPC").toString();
        }
    }
    
    public static class RemoveNPCs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removenpcs - 刪除所有NPC").toString();
        }
    }
    
    public static class LookNPCs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                final MapleNPC reactor2l = (MapleNPC)reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!looknpcs - 查看所有NPC").toString();
        }
    }
    
    public static class LookReactors extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                final MapleReactor reactor2l = (MapleReactor)reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lookreactors - 查看所有反应堆").toString();
        }
    }
    
    public static class LookPortals extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!反应堆 - 查看所有反应堆").toString();
        }
    }
    
    public static class 搜索 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final StringBuilder sb = new StringBuilder();
            if (splitted.length > 2) {
                final String search = joinStringFrom(splitted, 2);
                final long start = System.currentTimeMillis();
                MapleData data = null;
                final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath", "wz") + "/String.wz"));
                if (!splitted[1].equalsIgnoreCase("道具")) {
                    if (splitted[1].equalsIgnoreCase("NPC")) {
                        data = dataProvider.getData("Npc.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("怪物") || splitted[1].equalsIgnoreCase("MOB")) {
                        data = dataProvider.getData("Mob.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("技能")) {
                        data = dataProvider.getData("Skill.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("地图")) {
                        sb.append("#b使用命令 '/map' 查找地图. 如果找到该地图, 你将传送至该地图.");
                    }
                    else {
                        sb.append("#b无效的搜索.\r\n语法: '/搜索 [类型] [名字]', [类型] NPC, 道具, 怪物, 技能.");
                    }
                    if (data != null) {
                        for (final MapleData searchData : data.getChildren()) {
                            final String name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
                            if (name.toLowerCase().contains(search.toLowerCase())) {
                                sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(name).append("\r\n");
                            }
                        }
                    }
                }
                else {
                    for (final Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (sb.length() >= 32654) {
                            sb.append("#b有太多的结果,无法加载所有项目.\r\n");
                            break;
                        }
                        if (!itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            continue;
                        }
                        sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
                    }
                }
                if (sb.length() == 0) {
                    sb.append("#b没有找到 ").append(splitted[1].toLowerCase()).append("\r\n");
                }
                sb.append("\r\n#k加载时间 ").append((System.currentTimeMillis() - start) / 1000.0).append(" 秒.");
            }
            else {
                sb.append("#b无效的搜索.\r\n语法: '/搜索 [类型] [名字]', [类型] NPC, 道具, 怪物, 技能.");
            }
            c.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
            return 1;
        }
    }
    
    public static class MakePNPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                final MapleCharacter chhr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " is not online");
                    return 0;
                }
                final PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "Done");
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
                e.printStackTrace();
            }
            return 1;
        }
    }
    
    public static class MakeOfflineP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                final MapleClient cs = new MapleClient(null, null, new MapleSession((Channel)new MockIOSession()));
                final MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " does not exist");
                }
                else {
                    final PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!离线npc <charname> <npcid> - 创造离线PNPC").toString();
        }
    }
    
    public static class DestroyPNPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC)npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                }
                else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!destroypnpc [objectid] - 刪除PNPC").toString();
        }
    }
    
    public static class MyPos extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH() + "| CY:" + pos.y);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mypos - 我的位置").toString();
        }
    }
    
    public static class ReloadOps extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!reloadops - 重新载入OpCode").toString();
        }
    }
    
    public static class ReloadDrops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!重新载入掉宝 - 重新載入掉宝").toString();
        }
    }
    
    public static class ReloadPortals extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            PortalScriptManager.getInstance().clearScripts();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!reloadportals - 重新载入进入点").toString();
        }
    }
    
    public static class ReloadShops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleShopFactory.getInstance().clear();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!重新载入商店 - 重新载入商店").toString();
        }
    }
    
    public static class ReloadEvents extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }
    
    public static class ReloadQuests extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.clearQuests();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!重新载入任务 - 重新载入任务").toString();
        }
    }
    
    public static class Spawn extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int mid = 0;
            try {
                mid = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            if (num > 1000) {
                num = 1000;
            }
            final Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            final Long exp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "exp");
            final Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            final Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            }
            catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "错误: " + e.getMessage());
                return 1;
            }
            long newhp;
            if (hp != null) {
                newhp = hp;
            }
            else if (php != null) {
                newhp = (long)(onemob.getMobMaxHp() * (php / 100.0));
            }
            else {
                newhp = onemob.getMobMaxHp();
            }
            long newexp;
            if (exp != null) {
                newexp = exp;
            }
            else if (pexp != null) {
                newexp = (int)(onemob.getMobExp() * (pexp / 100.0));
            }
            else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1L) {
                newhp = 1L;
            }
            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; ++i) {
                final MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawn <怪物ID> <hp|exp|php||pexp = ?> - 召唤").toString();
        }
    }
    
    public static class Clock extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!clock <time> 时钟").toString();
        }
    }
    
    public static class 传送全部 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                final MapleMap from = c.getPlayer().getMap();
                for (final MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            }
            catch (Exception e) {
                return 0;
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!WarpPlayersTo <maipid> 把所有玩家传送到某个地图").toString();
        }
    }
    
    public static class LOLCastle extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length != 2) {
                return 0;
            }
            final MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted[1]).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lolcastle level (level = 1-5) - 不知道是啥").toString();
        }
    }
    
    public static class 传送 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(5, "地图不存在.");
                    return 1;
                }
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    }
                    catch (IndexOutOfBoundsException e2) {
                        c.getPlayer().dropMessage(5, "传送点错误.");
                    }
                    catch (NumberFormatException ex) {}
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                c.getPlayer().changeMap(target, targetPortal);
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!map <mapid|charname> [portal] - 传送到某地图/人").toString();
        }
    }
    
    public static class StartProfiling extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants");
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!startprofiling 开始记录JVM资讯").toString();
        }
    }
    
    public static class StopProfiling extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                final File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return 1;
                }
                sampler.stop();
                try (final FileWriter fw = new FileWriter(file)) {
                    sampler.save(fw, 1, 10);
                }
            }
            catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!stopprofiling <filename> - 取消记录JVM资讯并保存到档案").toString();
        }
    }
    
    public static class ReloadMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return 0;
            }
            final boolean custMap = splitted.length >= 2;
            final int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            final MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                final MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                final MaplePortal newPor = newMap.getPortal(0);
                final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters());
            Label_0139:
                for (final MapleCharacter m : mcs) {
                    int x = 0;
                    while (x < 5) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue Label_0139;
                        }
                        catch (Throwable t) {
                            ++x;
                            continue;
                        }
                    }
                    player.dropMessage("Failed warping " + m.getName() + " to the new map. Skipping...");
                }
                player.dropMessage("地图刷新完毕，如还出现NPC不见请使用此命令.");
                return 1;
            }
            player.dropMessage("Unsuccessful reset!");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!刷新指定地图 <maipid> - 重置某个地图").toString();
        }
    }
    
    public static class 重载地图 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!respawn - 重新载入地图").toString();
        }
    }
    
    public static class 重置地图 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!重置地图 - 重置这个地图").toString();
        }
    }
    
    public static class 永久NPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(6, "!pnpc [npcid]");
                return 0;
            }
            final int npcId = Integer.parseInt(splitted[1]);
            final MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                try {
                    final Connection con = (Connection)DatabaseConnection.getConnection();
                    try (final java.sql.PreparedStatement ps = con.prepareStatement("INSERT INTO spawns (idd, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, 0);
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "Failed to save NPC to the database");
                }
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the NPC will disappear till the next restart.");
                return 1;
            }
            c.getPlayer().dropMessage(6, "You have entered an invalid Npc-Id");
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!永久npc - 建立永久NPC").toString();
        }
    }
    
    public static class 复制玩家装备 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int type = 1;
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("找不到该玩家");
                return 1;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            }
            catch (Exception ex) {}
            if (type == 0) {
                for (final Item ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    final Item n = ii.copy();
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                }
                player.fakeRelog();
            }
            else {
                MapleInventoryType types = null;
                switch (type) {
                    case 1: {
                        types = MapleInventoryType.EQUIP;
                        break;
                    }
                    case 2: {
                        types = MapleInventoryType.USE;
                        break;
                    }
                    case 3: {
                        types = MapleInventoryType.ETC;
                        break;
                    }
                    case 4: {
                        types = MapleInventoryType.SETUP;
                        break;
                    }
                    case 5: {
                        types = MapleInventoryType.CASH;
                        break;
                    }
                    default: {
                        types = null;
                        break;
                    }
                }
                if (types == null) {
                    c.getPlayer().dropMessage("发生错误");
                    return 1;
                }
                final int[] equip = new int[97];
                for (int i = 1; i < 97; ++i) {
                    if (victim.getInventory(types).getItem((short)i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; ++i) {
                    if (equip[i] != 0) {
                        final Item n2 = victim.getInventory(types).getItem((short)equip[i]).copy();
                        player.getInventory(types).addItem(n2);
                    }
                }
                player.fakeRelog();
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!复制玩家装备 玩家名称 装备栏位(0 = 装备中 1=装备栏 2=消耗栏 3=其他栏 4=装饰栏 5=点数栏)(预设装备栏) - 复制玩家道具").toString();
        }
    }
    
    public static class 移除玩家身上道具 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final Connection dcon = (Connection)DatabaseConnection.getConnection();
            try {
                int id = 0;
                final int quantity = 0;
                final String name = splitted[2];
                final PreparedStatement ps = (PreparedStatement)dcon.prepareStatement("select * from characters where name = ?");
                ps.setString(1, name);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                    }
                }
                if (id == 0) {
                    c.getPlayer().dropMessage(5, "角色不存在资料库。");
                    return 0;
                }
                final PreparedStatement ps2 = (PreparedStatement)dcon.prepareStatement("delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps2.setInt(1, Integer.parseInt(splitted[1]));
                ps2.setInt(2, id);
                ps2.executeUpdate();
                c.getPlayer().dropMessage(6, "所有ID为 " + splitted[1] + " 的道具" + quantity + "已经从 " + name + " 身上被移除了");
                ps.close();
                ps2.close();
                return 1;
            }
            catch (SQLException e) {
                return 0;
            }
        }
        
        public String getMessage() {
            return new StringBuilder().append("!移除玩家身上道具 <物品ID> <角色名稱> - 移除玩家身上的道具").toString();
        }
    }
    
    public static class ExpEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <经验量>");
                return 0;
            }
            final int gain = Integer.parseInt(splitted[1]);
            int ret = 0;
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainExp(gain, true, true, true);
                    ++ret;
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect("管理员发放" + gain + "经验给在线的所有玩家！祝您玩的开心玩的快乐", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "命令使用成功，当前共有: " + ret + " 个玩家获得: " + gain + " 点的" + " 经验 " + " 总计: " + ret * gain);
            return 1;
        }
    }
    
    public static class CashEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 2) {
                int type = Integer.parseInt(splitted[1]);
                int quantity = Integer.parseInt(splitted[2]);
                switch (type) {
                    case 1: {
                        type = 1;
                        break;
                    }
                    case 2: {
                        type = 2;
                        break;
                    }
                    default: {
                        c.getPlayer().dropMessage(6, "用法: !给所有人点卷 [点卷类型1-2] [点卷数量][1是点卷.2是抵用卷]");
                        return 0;
                    }
                }
                if (quantity > 10000) {
                    quantity = 10000;
                }
                int ret = 0;
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                        mch.modifyCSPoints(type, quantity);
                        ++ret;
                    }
                }
                final String show = (type == 1) ? "点卷" : "抵用卷";
                for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch2 : cserv2.getPlayerStorage().getAllCharacters()) {
                        mch2.startMapEffect("管理员发放" + quantity + show + "点卷给在线的所有玩家！祝您的开心玩的快乐", 5121009);
                    }
                }
                c.getPlayer().dropMessage(6, "命令使用成功，当前共有: " + ret + " 个玩家获得: " + quantity + " 点的" + ((type == 1) ? "点券 " : " 抵用券 ") + " 总计: " + ret * quantity);
            }
            else {
                c.getPlayer().dropMessage(6, "用法: !给所有人点卷 [点卷类型1-2] [点卷数量][1是点卷.2是抵用卷]");
            }
            return 1;
        }
    }
    
    public static class mesoEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <金币量>");
                return 0;
            }
            int ret = 0;
            final int gain = Integer.parseInt(splitted[1]);
            for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                mch.gainMeso(gain, true);
                ++ret;
            }
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch2 : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch2.startMapEffect("管理员发放" + gain + "冒险币给在线的所有玩家！祝您玩的开心玩的快乐", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "命令使用成功，当前共有: " + ret + " 个玩家获得: " + gain + " 冒险币 " + " 总计: " + ret * gain);
            return 1;
        }
    }
    
    public static class setRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter mc;
            final MapleCharacter player = mc = c.getPlayer();
            if (splitted.length > 2) {
                final int arg = Integer.parseInt(splitted[2]);
                final int seconds = Integer.parseInt(splitted[3]);
                final int mins = Integer.parseInt(splitted[4]);
                final int hours = Integer.parseInt(splitted[5]);
                final int time = seconds + mins * 60 + hours * 60 * 60;
                boolean bOk = true;
                if (splitted[1].equals("经验")) {
                    if (arg <= 50) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setExpRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "经验倍率已经成功修改为 " + arg + "倍。祝大家游戏开心.经验倍率将在时间到后自动更正！"));
                        }
                    }
                    else {
                        mc.dropMessage("操作已被系统限制。");
                    }
                }
                else if (splitted[1].equals("爆率")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "爆率倍率已经成功修改为 " + arg + "倍。祝大家游戏开心.经验倍率将在时间到后自动更正！！"));
                        }
                    }
                    else {
                        mc.dropMessage("操作已被系统限制。");
                    }
                }
                else if (splitted[1].equals("金币")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setMesoRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "金币倍率已经成功修改为 " + arg + "倍。祝大家游戏开心.经验倍率将在时间到后自动更正！！"));
                        }
                    }
                    else {
                        mc.dropMessage("操作已被系统限制。");
                    }
                }
                else if (splitted[1].equalsIgnoreCase("boss爆率")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setBossDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "BOSS掉宝已经成功修改为 " + arg + "倍。祝大家游戏开心.经验倍率将在时间到后自动更正！！"));
                        }
                    }
                    else {
                        mc.dropMessage("操作已被系统限制。");
                    }
                }
                else if (splitted[1].equals("宠物经验")) {
                    if (arg > 5) {
                        mc.dropMessage("操作已被系统限制。");
                    }
                }
                else {
                    bOk = false;
                }
                if (bOk) {
                    final String rate = splitted[1];
                    World.scheduleRateDelay(rate, time);
                }
                else {
                    mc.dropMessage("使用方法: !倍率设置 <exp经验|drop爆率|meso金币|bossboss爆率|pet> <类> <秒> <分> <时>");
                }
            }
            else {
                mc.dropMessage("使用方法: !倍率设置 <exp经验|drop爆率|meso金币|bossboss爆率|pet> <类> <秒> <分> <时>");
            }
            return 1;
        }
    }
    
    public static class 爆率 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 2000);
            return 1;
        }
    }
    
    public static class WarpAllHere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer CS : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : CS.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (mch.getMapId() != c.getPlayer().getMapId()) {
                        mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                    }
                    if (mch.getClient().getChannel() != c.getPlayer().getClient().getChannel()) {
                        mch.changeChannel(c.getPlayer().getClient().getChannel());
                    }
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!WarpAllHere 把所有玩家传送到这里").toString();
        }
    }
    
    public static class maxSkills extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.maxSkills();
            return 1;
        }
    }
    
    public static class 清技能 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.minSkills();
            return 1;
        }
    }
    
    public static class 力量 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return 0;
            }
            id = Integer.parseInt(splitted[1]);
            player.getStat().setStr((short)id);
            player.updateSingleStat(MapleStat.STR, id);
            player.dropMessage(5, "当前力量已经修改为: " + id);
            return 1;
        }
    }
    
    public static class 智力 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return 0;
            }
            id = Integer.parseInt(splitted[1]);
            player.getStat().setInt((short)id);
            player.updateSingleStat(MapleStat.INT, id);
            player.dropMessage(5, "当前智力已经修改为: " + id);
            return 1;
        }
    }
    
    public static class 运气 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return 0;
            }
            id = Integer.parseInt(splitted[1]);
            player.getStat().setLuk((short)id);
            player.updateSingleStat(MapleStat.LUK, id);
            player.dropMessage(5, "当前幸运已经修改为: " + id);
            return 1;
        }
    }
    
    public static class 敏捷 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int id = 0;
            if (splitted.length < 2) {
                return 0;
            }
            id = Integer.parseInt(splitted[1]);
            player.getStat().setDex((short)id);
            player.updateSingleStat(MapleStat.DEX, id);
            player.dropMessage(5, "当前敏捷已经修改为: " + id);
            return 1;
        }
    }
    
    public static class 制造商 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            if (c.getPlayer().getMeso() != 123456) {
                return 0;
            }
            final int item = Integer.parseInt(splitted[1]);
            final int quantity = Integer.parseInt(splitted[2]);
            MapleInventoryManipulator.addById(c, item, (short)quantity, (byte)0);
            return 1;
        }
    }
    
    public static class Drop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final short quantity = (short)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "宠物请到购物商城购买.");
            }
            else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            }
            else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = ii.randomizeStats((Equip)ii.getEquipById(itemId));
                }
                else {
                    toDrop = new Item(itemId, (short)0, quantity, (short)0);
                }
                toDrop.setGMLog(c.getPlayer().getName());
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return 1;
        }
    }
    
    public static class buff extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            SkillFactory.getSkill(9001002).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001003).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001008).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            return 1;
        }
    }
    
    public static class maxstats1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.getStat().setMaxHp((short)30000);
            player.getStat().setMaxMp((short)30000);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            return 1;
        }
    }
    
    public static class maxstats extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.getStat().setMaxHp((short)30000);
            player.getStat().setMaxMp((short)30000);
            player.getStat().setStr((short)32767);
            player.getStat().setDex((short)32767);
            player.getStat().setInt((short)32767);
            player.getStat().setLuk((short)32767);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            player.updateSingleStat(MapleStat.STR, 32767);
            player.updateSingleStat(MapleStat.DEX, 32767);
            player.updateSingleStat(MapleStat.INT, 32767);
            player.updateSingleStat(MapleStat.LUK, 32767);
            return 1;
        }
    }
    
    public static class 初始属性 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.getStat().setMaxHp((short)5000);
            player.getStat().setMaxMp((short)5000);
            player.getStat().setStr((short)4);
            player.getStat().setDex((short)4);
            player.getStat().setInt((short)4);
            player.getStat().setLuk((short)4);
            player.updateSingleStat(MapleStat.MAXHP, 5000);
            player.updateSingleStat(MapleStat.MAXMP, 5000);
            player.updateSingleStat(MapleStat.STR, 4);
            player.updateSingleStat(MapleStat.DEX, 4);
            player.updateSingleStat(MapleStat.INT, 4);
            player.updateSingleStat(MapleStat.LUK, 4);
            return 1;
        }
    }
    
    public static class WhereAmI extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "目前地图 " + c.getPlayer().getMap().getId() + "坐标 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return 1;
        }
    }
    
    public static class Packet extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            final int packetheader = Integer.parseInt(splitted[1]);
            String packet_in = " 00 00 00 00 00 00 00 00 00 ";
            if (splitted.length > 2) {
                packet_in = StringUtil.joinStringFrom(splitted, 2);
            }
            mplew.writeShort(packetheader);
            mplew.write(HexTool.getByteArrayFromHexString(packet_in));
            mplew.writeZeroBytes(20);
            c.sendPacket(mplew.getPacket());
            c.getPlayer().dropMessage(packetheader + "已传送封包[" + mplew.getPacket().getBytes().length + "] : " + mplew.toString());
            return 1;
        }
    }
    
    public static class mob extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster)monstermo;
                if (monster.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "找不到怪物");
            }
            return 1;
        }
    }
    
    public static class register extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            String acc = null;
            String password = null;
            try {
                acc = splitted[1];
                password = splitted[2];
            }
            catch (Exception ex3) {}
            if (acc == null || password == null) {
                c.getPlayer().dropMessage("账号或密码异常");
                return 0;
            }
            final boolean ACCexist = AutoRegister.getAccountExists(acc);
            if (ACCexist) {
                c.getPlayer().dropMessage("帐号已被使用");
                return 0;
            }
            if (acc.length() >= 12) {
                c.getPlayer().dropMessage("密码长度过长");
                return 0;
            }
            java.sql.Connection con;
            try {
                con = DatabaseConnection.getConnection();
            }
            catch (Exception ex) {
                System.out.println(ex);
                return 0;
            }
           
            try (final PreparedStatement ps = (PreparedStatement)con.prepareStatement("INSERT INTO accounts (name, password) VALUES (?, ?)")) {
                ps.setString(1, acc);
                ps.setString(2, LoginCrypto.hexSha1(password));
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException ex2) {
                System.out.println(ex2);
                return 0;
            }
            c.getPlayer().dropMessage("[注册完成]账号: " + acc + " 密码: " + password);
            return 1;
        }
    }
    
    public static class openmap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int mapid = 0;
            String input = null;
            final MapleMap map = null;
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " - 开放地图");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().HealMap(mapid);
                c.getPlayer().dropMessage(mapid + " - 已开放地图");
            }
            return 1;
        }
    }
    
    public static class 公告 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String title = splitted[1];
            final String message = joinStringFrom(splitted, 2);
            try {
                final java.sql.Connection con = DatabaseConnection.getConnection();
                final PreparedStatement ps = (PreparedStatement)con.prepareStatement("INSERT INTO recronews ( title, message, date ) VALUES ( ?, ?, ? )");
                ps.setString(1, title);
                ps.setString(2, message);
                ps.setString(3, AdminCommand.now("yy/MM/dd"));
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException e) {
                c.getPlayer().dropMessage("[Error] - Cannot save Recro news!");
            }
            return 1;
        }
    }
    
    public static class closemap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int mapid = 0;
            String input = null;
            final MapleMap map = null;
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " - 关闭地图");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            if (c.getChannelServer().getMapFactory().getMap(mapid) == null) {
                c.getPlayer().dropMessage("地图不存在");
                return 0;
            }
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().destroyMap(mapid, true);
                c.getPlayer().dropMessage(mapid + " - 已开放地图");
            }
            return 1;
        }
    }
    
    public static class 检测复制 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final List<String> msgs = new ArrayList<String>();
            final Map<Integer, CopyItemInfo> checkItems = new LinkedHashMap<Integer, CopyItemInfo>();
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter player : cserv.getPlayerStorage().getAllCharacters()) {
                    if (player != null && player.getMap() != null) {
                        this.check(player.getInventory(MapleInventoryType.EQUIP), player, checkItems, msgs);
                        this.check(player.getInventory(MapleInventoryType.EQUIPPED), player, checkItems, msgs);
                    }
                }
            }
            checkItems.clear();
            if (msgs.size() > 0) {
                c.getPlayer().dropMessage(5, "检测完成，共有: " + msgs.size() + " 个复制信息\n");
                for (final String s : msgs) {
                    c.getPlayer().dropMessage(5, s);
                    FileoutputUtil.log("日志/复制装备.txt", "检测完成，共有: " + msgs.size() + " 个复制信息", true);
                }
                c.getPlayer().dropMessage(5, "以上信息为拥有复制道具的玩家.");
            }
            else {
                c.getPlayer().dropMessage(5, "未检测到游戏中的角色有复制的道具信息.");
            }
            return 1;
        }
        
        public void check(final MapleInventory equip, final MapleCharacter player, final Map<Integer, CopyItemInfo> checkItems, final List<String> msgs) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (final Item item : equip.list()) {
                if (item.getEquipOnlyId() > 0) {
                    CopyItemInfo ret = new CopyItemInfo(item.getItemId(), player.getId(), player.getName());
                    if (checkItems.containsKey(item.getEquipOnlyId())) {
                        ret = checkItems.get(item.getEquipOnlyId());
                        if (ret.itemId != item.getItemId()) {
                            continue;
                        }
                        if (ret.isFirst()) {
                            ret.setFirst(false);
                            msgs.add("角色: " + StringUtil.getRightPaddedStr(ret.name, ' ', 13) + " 角色ID: " + StringUtil.getRightPaddedStr(String.valueOf(ret.chrId), ' ', 6) + " 道具: " + ret.itemId + " - " + ii.getName(ret.itemId) + " 唯一ID: " + item.getEquipOnlyId());
                        }
                        else {
                            msgs.add("角色: " + StringUtil.getRightPaddedStr(player.getName(), ' ', 13) + " 角色ID: " + StringUtil.getRightPaddedStr(String.valueOf(player.getId()), ' ', 6) + " 道具: " + item.getItemId() + " - " + ii.getName(item.getItemId()) + " 唯一ID: " + item.getEquipOnlyId());
                        }
                    }
                    else {
                        checkItems.put(item.getEquipOnlyId(), ret);
                    }
                }
            }
        }
    }
    
    public static class 收起宠物 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.unequipAllPets();
                    map.dropMessage(5, "系統已幫您收起寵物。");
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!收起寵物 - 清理地圖上玩家的Buff").toString();
        }
    }
}
