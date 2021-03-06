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
    
    public static class ???????????? extends openmap
    {
    }
    
    public static class ???????????? extends closemap
    {
    }
    
    public static class ?????? extends register
    {
    }
    
    public static class ????????? extends maxstats
    {
    }
    
    public static class ?????? extends maxstats1
    {
    }
    
    public static class ????????? extends maxSkills
    {
    }
    
    public static class ????????? extends WarpAllHere
    {
    }
    
    public static class ????????? extends mesoEveryone
    {
    }
    
    public static class ????????? extends ExpEveryone
    {
    }
    
    public static class ?????????????????? extends CashEveryone
    {
    }
    
    public static class ????????? extends GainCash
    {
    }
    
    public static class ???????????? extends ReloadMap
    {
    }
    
    public static class ?????? extends buff
    {
    }
    
    public static class ???????????? extends setRate
    {
    }
    
    public static class ???????????? extends WhereAmI
    {
    }
    
    public static class ??? extends ?????????
    {
    }
    
    public static class ??? extends Drop
    {
    }
    
    public static class ???????????? extends HealMap
    {
    }
    
    public static class ?????? extends KillAll
    {
    }
    
    public static class ???????????? extends Fame
    {
    }
    
    public static class ?????? extends MobVac
    {
    }
    
    public static class ???????????? extends cleardrops
    {
    }
    
    public static class ?????? extends Spawn
    {
    }
    
    public static class ????????? extends Clock
    {
    }
    
    public static class ???????????? extends autoreg
    {
    }
    
    public static class ???????????? extends mob
    {
    }
    
    public static class ???????????? extends setUserLimit
    {
    }
    
    public static class ???????????? extends BanStatus
    {
    }
    
    public static class ?????????????????? extends SavePlayerShops
    {
    }
    
    public static class ??????????????? extends Shutdown
    {
    }
    
    public static class ????????????????????? extends ShutdownTime
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
    
    public static class ????????????????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                Start.???????????????(splitted[1]);
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "?????????????????????." + e.getMessage());
            }
            c.getPlayer().dropMessage(6, "?????????????????????");
            return 1;
        }
    }
    
    public static class ?????????????????? extends CommandExecute
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
                c.getPlayer().dropMessage(6, "??????ID??? " + splitted[1] + " ?????????" + quantity + "???????????????????????????????????????");
                ps2.close();
                return 1;
            }
            catch (SQLException e) {
                return 0;
            }
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <??????ID> <????????????> - ???????????????????????????").toString();
        }
    }
    
    public static class ?????? extends CommandExecute
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
            c.getPlayer().dropMessage("??????[" + name + "] ??????ID[" + acid + "]???????????????: " + (ACbanned ? "???" : "???") + (Systemban ? "(??????????????????)" : "") + ", ??????: " + reason);
            c.getPlayer().dropMessage("IP: " + ip + " ???????????????IP??????: " + (IPbanned ? "???" : "???"));
            c.getPlayer().dropMessage("MAC: " + mac + " ???????????????MAC??????: " + (MACbanned ? "???" : "???"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!BanStatus <????????????> - ????????????????????????????????????").toString();
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
            c.getPlayer().dropMessage("?????????????????????????????????" + UserLimit);
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
            c.getPlayer().dropMessage(6, "????????????????????????.");
            return 1;
        }
    }
    
    public static class Shutdown extends CommandExecute
    {
        private static Thread t;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "???????????????...");
            if (Shutdown.t == null || !Shutdown.t.isAlive()) {
                (Shutdown.t = new Thread(ShutdownServer.getInstance())).start();
            }
            else {
                c.getPlayer().dropMessage(6, "???????????????...");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdown - ???????????????").toString();
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
            c.getPlayer().dropMessage(6, "??????????????? " + this.minutesLeft + "???????????????. ??????????????????????????? ?????????.");
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
                        message.append("[???????????????] ??????????????? ");
                        message.append(minutesLeft);
                        message.append("???????????????. ??????????????????????????? ?????????.");
                        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, message.toString()).getBytes());
                        Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()).getBytes());
                        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
                            cs.setServerMessage("??????????????? " + minutesLeft + " ???????????????");
                        }
                        minutesLeft--;
                    }
                }, 60000L);
            }
            else {
                c.getPlayer().dropMessage(6, "?????????????????????????????? " + this.minutesLeft + "????????????????????????????????????");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <??????> - ???????????????").toString();
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
            c.getPlayer().dropMessage("[??????] " + this.p + "?????????????????????????????????.");
            this.p = 0;
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!saveall - ????????????????????????").toString();
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
            return new StringBuilder().append("!lowhp - ????????????").toString();
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
            return new StringBuilder().append("!heal - ????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL ??????.");
            }
            else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] ???????????????.");
            }
            else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            }
            else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP???Mac?????????????????????.");
            }
            else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP??????Mac???????????????.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? <????????????> - ????????????").toString();
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
                c.getPlayer().dropMessage(6, "[tempban] ?????????????????????");
            }
            else {
                victim.tempban("???" + c.getPlayer().getName() + "???????????????", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " ??????????????????????????? " + df.format(cal.getTime()));
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!tempban <????????????> - ??????????????????").toString();
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
                    c.getPlayer().dropMessage(6, "[kill] ?????? " + splitted[i] + " ?????????.");
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
            return new StringBuilder().append("!kill <????????????1> <????????????2> ...  - ????????????").toString();
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
            return new StringBuilder().append("!skill <??????ID> [????????????] [??????????????????] ...  - ????????????").toString();
        }
    }
    
    public static class Fame extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage("!fame <????????????> <??????> ...  - ??????");
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            }
            else {
                c.getPlayer().dropMessage(6, "[fame] ???????????????");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fame <????????????> <??????> ...  - ??????").toString();
        }
    }
    
    public static class autoreg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage("???????????????????????? " + ServerConstants.ChangeAutoReg());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!autoreg  - ??????????????????").toString();
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
            return new StringBuilder().append("!healmap  - ???????????????????????????").toString();
        }
    }
    
    public static class GodMode extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "??????????????????");
            }
            else {
                player.setInvincible(true);
                player.dropMessage(6, "??????????????????.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!godmode  - ????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
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
            return new StringBuilder().append("!???????????? <????????????> <??????ID> [????????????] [??????????????????] - ????????????").toString();
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
            return new StringBuilder().append("!sp [??????] - ??????SP").toString();
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
            return new StringBuilder().append("!ap [??????] - ??????AP").toString();
        }
    }
    
    public static class ??????????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.setRemainingAp((short)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 2));
            victim.updateSingleStat(MapleStat.AVAILABLEAP, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 2));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!ap [??????] - ??????AP").toString();
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
                c.getPlayer().dropMessage(5, "?????????ID?????????");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shop - ????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        protected static ScheduledFuture<?> ts;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 1) {
                return 0;
            }
            if (????????????.ts != null) {
                ????????????.ts.cancel(false);
                c.getPlayer().dropMessage(0, "??????????????????????????????");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                return 0;
            }
            if (minutesLeft > 0) {
                ????????????.ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                                if (!c.getPlayer().isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010);
                                }
                            }
                        }
                        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "???????????????????????????!!!").getBytes());
                        Broadcast.broadcastMessage(MaplePacketCreator.serverMessage("???????????????????????????!!!").getBytes());
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60 * 1000);
                c.getPlayer().dropMessage(0, "???????????????????????????");
            }
            else {
                c.getPlayer().dropMessage(0, "????????????????????? > 0???");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????:??????> - ????????????").toString();
        }
        
        static {
            ????????????.ts = null;
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
                c.getPlayer().dropMessage("??????????????????");
                return 1;
            }
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("??????????????????");
                return 1;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("??????????????????");
                return 1;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("??????????????????" + amount + "???");
            final String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " ?????? " + amount + "???";
            FileoutputUtil.logToFile("??????/Logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " ?????? " + amount + "???");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gaingash <??????> <??????> - ??????Gash??????").toString();
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
            final String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " ???????????? " + amount + "???";
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <??????> <??????> - ??????????????????").toString();
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
            return new StringBuilder().append("!gainpoint <??????> <??????> - ??????Point").toString();
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
            return new StringBuilder().append("!levelup - ????????????").toString();
        }
    }
    
    public static class ?????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "??????: !?????? [??????ID] [??????]");
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
            return new StringBuilder().append("!servermsg ?????? - ????????????????????????").toString();
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
            return new StringBuilder().append("!say ?????? - ???????????????").toString();
        }
    }
    
    public static class Letter extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "????????????: ");
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
                    c.getPlayer().dropMessage(6, "???????????????!");
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
                    chars.add(chr + '??');
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
            return new StringBuilder().append(" !letter <color (green/red)> <word> - ??????").toString();
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
                c.getPlayer().dropMessage(6, "???????????????ID.");
            }
            else {
                final String name = splitted[1];
                final int ch = Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return 0;
                }
                final MapleCharacter fff = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                }
                else {
                    final int[] ringID = { MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance() };
                    try {
                        final MapleCharacter[] chrz = { fff, c.getPlayer() };
                        for (int i = 0; i < chrz.length; ++i) {
                            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "???????????????ID.");
                                return 1;
                            }
                            eq.setUniqueId(ringID[i]);
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].dropMessage(6, "?????????  " + chrz[0].getName() + " ??????");
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    }
                    catch (SQLException ex) {}
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!marry <????????????> <????????????> - ??????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int itemamount = chr.getItemQuantity(item, true);
            if (itemamount > 0) {
                c.getPlayer().dropMessage(6, chr.getName() + " ??? " + itemamount + " (" + item + ").");
            }
            else {
                c.getPlayer().dropMessage(6, chr.getName() + " ????????? (" + item + ")");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - ????????????").toString();
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
            return new StringBuilder().append("!mobvac - ????????????").toString();
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
            return new StringBuilder().append("!song - ????????????").toString();
        }
    }
    
    public static class ?????????????????? extends CommandExecute
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
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        private static ScheduledFuture<?> ts;
        private int min;
        
        public ????????????() {
            this.min = 1;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "??????????????????????????????????????? !???????????? ????????????");
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "??????:" + c.getChannel() + "????????????????????????????????????").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                ????????????.ts = EventTimer.getInstance().register(new Runnable() {
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
            c.getPlayer().dropMessage(5, "?????????????????? ! ????????????????????????????????????????????????????????????????????????");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????").toString();
        }
        
        static {
            ????????????.ts = null;
        }
    }
    
    public static class ?????????????????? extends CommandExecute
    {
        private static boolean tt;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "??????????????????????????????????????? !???????????? ????????????");
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "??????:" + c.getChannel() + "????????????????????????????????????").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        tt = true;
                    }
                }, 60000L);
                if (??????????????????.tt) {
                    MapleEvent.onStartEvent(c.getPlayer());
                }
                return 1;
            }
            c.getPlayer().dropMessage(5, "?????????????????? !???????????? ????????????????????????????????????????????????????????????????????????");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? -??????????????????").toString();
        }
        
        static {
            ??????????????????.tt = false;
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("????????????????????????: ");
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
            return new StringBuilder().append("!???????????? - ????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter chrs = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "??????????????????");
            }
            else {
                c.getPlayer().dropMessage(6, chrs.getName() + " ??? " + chrs.getCSPoints(1) + " ??????.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!checkgash <????????????> - ????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "?????????????????????");
            }
            else {
                chr.removeAll(id);
                c.getPlayer().dropMessage(6, "??????ID??? " + id + " ?????????????????? " + name + " ??????????????????");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <????????????> <??????ID> - ???????????????????????????").toString();
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
            return new StringBuilder().append("!killmap - ??????????????????").toString();
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
            return new StringBuilder().append("!speakmega [????????????] <??????> - ????????????????????????????????????").toString();
        }
    }
    
    public static class Speak extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String name = splitted[1];
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + splitted[1]);
                return 0;
            }
            victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speak <????????????> <??????> - ????????????????????????").toString();
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
            return new StringBuilder().append("!speakmap <??????> - ?????????????????????????????????").toString();
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
            return new StringBuilder().append("!speakchannel <??????> - ?????????????????????????????????").toString();
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
            return new StringBuilder().append("!speakchannel <??????> - ????????????????????????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
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
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return 0;
                }
                final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "??????????????????");
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
            return new StringBuilder().append("!???????????? <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [????????????] <????????????> - ????????????????????????").toString();
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
            return new StringBuilder().append("!sendallnote <??????> ??????Note???????????????????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + name);
            }
            else {
                victim.gainMeso(gain, true);
                final String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " ?????? " + gain + "???";
                Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <??????> <??????> - ???????????????").toString();
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
            return new StringBuilder().append("!cloneme - ???????????????").toString();
        }
    }
    
    public static class DisposeClones extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "?????????????????????.");
            c.getPlayer().disposeClones();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - ???????????????").toString();
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
                c.getPlayer().dropMessage(5, "??????????????????");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!monitor <??????> - ??????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "????????????????????????.");
            }
            else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "?????????ID.");
                }
                else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "?????????????????????.");
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter other = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (other == null) {
                builder.append("???????????????");
                c.getPlayer().dropMessage(6, builder.toString());
            }
            else {
                if (other.getClient().getLastPing() <= 0L) {
                    other.getClient().sendPing();
                }
                builder.append(MapleClient.getLogMessage(other, ""));
                builder.append(" ??? ").append(other.getPosition().x);
                builder.append(" /").append(other.getPosition().y);
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getHp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxHp());
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getMp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxMp());
                builder.append(" || ??????????????? : ");
                builder.append(other.getStat().getTotalWatk());
                builder.append(" || ??????????????? : ");
                builder.append(other.getStat().getTotalMagic());
                builder.append(" || ???????????? : ");
                builder.append(other.getStat().getCurrentMaxBaseDamage());
                builder.append(" || ??????%??? : ");
                builder.append(other.getStat().dam_r);
                builder.append(" || BOSS??????%??? : ");
                builder.append(other.getStat().bossdam_r);
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getStr());
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getDex());
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getInt());
                builder.append(" || ?????? : ");
                builder.append(other.getStat().getLuk());
                builder.append(" || ???????????? : ");
                builder.append(other.getStat().getTotalStr());
                builder.append(" || ???????????? : ");
                builder.append(other.getStat().getTotalDex());
                builder.append(" || ???????????? : ");
                builder.append(other.getStat().getTotalInt());
                builder.append(" || ???????????? : ");
                builder.append(other.getStat().getTotalLuk());
                builder.append(" || ????????? : ");
                builder.append(other.getExp());
                builder.append(" || ???????????? : ");
                builder.append(other.getParty() != null);
                builder.append(" || ????????????: ");
                builder.append(other.getTrade() != null);
                builder.append(" || Latency: ");
                builder.append(other.getClient().getLatency());
                builder.append(" || ??????PING: ");
                builder.append(other.getClient().getLastPing());
                builder.append(" || ??????PONG: ");
                builder.append(other.getClient().getLastPong());
                builder.append(" || IP: ");
                builder.append(other.getClient().getSessionIPAddress());
                other.getClient().DebugMessage(builder);
                c.getPlayer().dropMessage(6, builder.toString());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!charinfo <????????????> - ??????????????????").toString();
        }
    }
    
    public static class whoishere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            StringBuilder builder = new StringBuilder("?????????????????????: ");
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
            return new StringBuilder().append("!whoishere - ??????????????????????????????").toString();
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
            return new StringBuilder().append("!cheaters - ??????????????????").toString();
        }
    }
    
    public static class Connected extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Map<Integer, Integer> connected = World.getConnected();
            final StringBuilder conStr = new StringBuilder("?????????????????????: ");
            boolean first = true;
            for (final int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                }
                else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("??????: ");
                    conStr.append(connected.get(i));
                }
                else {
                    conStr.append("?????? ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!connected - ???????????????????????????").toString();
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
            return new StringBuilder().append("!resetquest <??????ID> - ????????????").toString();
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
            return new StringBuilder().append("!startquest <??????ID> - ????????????").toString();
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
            return new StringBuilder().append("!completequest <??????ID> - ????????????").toString();
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
            return new StringBuilder().append("!fstartquest <??????ID> - ??????????????????").toString();
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
            return new StringBuilder().append("!fcompletequest <??????ID> - ??????????????????").toString();
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
            return new StringBuilder().append("!fstartother - ????????????").toString();
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
            return new StringBuilder().append("!fcompleteother - ????????????").toString();
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
            return new StringBuilder().append("!nearestportal - ????????????").toString();
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
            return new StringBuilder().append("!spawndebug - debug????????????").toString();
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
            return new StringBuilder().append("!threads - ??????Threads??????").toString();
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
            return new StringBuilder().append("!fakerelog - ??????????????????").toString();
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
            return new StringBuilder().append("!toggleoffense <Offense> - ???????????????CheatOffense").toString();
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
            return new StringBuilder().append("!toggledrop - ?????????????????????").toString();
        }
    }
    
    public static class ToggleMegaphone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "?????????????????? : " + (c.getChannelServer().getMegaphoneMuteState() ? "???" : "???"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - ????????????????????????").toString();
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
            return new StringBuilder().append("!spawnreactor - ??????Reactor").toString();
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
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();
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
            return new StringBuilder().append("!drstroyreactor - ??????Reactor").toString();
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
            return new StringBuilder().append("!resetreactors - ????????????????????????Reactor").toString();
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
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();
        }
    }
    
    public static class cleardrops extends RemoveDrops
    {
    }
    
    public static class RemoveDrops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "????????? " + c.getPlayer().getMap().getNumItems() + " ????????????");
            c.getPlayer().getMap().removeDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removedrops - ?????????????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????????????? " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!exprate <??????> - ??????????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????????????? " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!droprate <??????> - ??????????????????").toString();
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
                c.getPlayer().dropMessage(6, "??????????????????????????? " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mesorate <??????> - ??????????????????").toString();
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
                    show = "??????";
                    break;
                }
                case 1: {
                    show = "??????";
                    break;
                }
                case 2: {
                    show = "??????";
                    break;
                }
            }
            final String msg = "[GM ??????] GM " + c.getPlayer().getName() + "  DC ??? " + show + "??????";
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - ??????????????????").toString();
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
                c.getPlayer().dropMessage(6, "Invalid command ???????????? - Use !goto <location>. For a list of locations, use !goto locations.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!goto <??????> - ???????????????").toString();
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
            c.getPlayer().dropMessage("??????????????? " + monsters.size() + " ??????");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - ??????????????????").toString();
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
            return new StringBuilder().append("!resetmobs - ???????????????????????????").toString();
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
            return new StringBuilder().append("!killmonster <mobid> - ???????????????????????????").toString();
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
            return new StringBuilder().append("!killmonsterbyoid <moboid> - ???????????????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " (<??????:??????0??????> (??????:??????????????????))");
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
                c.getPlayer().dropMessage(6, "???????????????");
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
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - ???????????????????????????").toString();
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
                c.getPlayer().dropMessage(6, "?????????????????????" + npcId + "???Npc");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!npc <npcid> - ?????????NPC").toString();
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
            return new StringBuilder().append("!removenpcs - ????????????NPC").toString();
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
            return new StringBuilder().append("!looknpcs - ????????????NPC").toString();
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
            return new StringBuilder().append("!lookreactors - ?????????????????????").toString();
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
            return new StringBuilder().append("!????????? - ?????????????????????").toString();
        }
    }
    
    public static class ?????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final StringBuilder sb = new StringBuilder();
            if (splitted.length > 2) {
                final String search = joinStringFrom(splitted, 2);
                final long start = System.currentTimeMillis();
                MapleData data = null;
                final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath", "wz") + "/String.wz"));
                if (!splitted[1].equalsIgnoreCase("??????")) {
                    if (splitted[1].equalsIgnoreCase("NPC")) {
                        data = dataProvider.getData("Npc.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("??????") || splitted[1].equalsIgnoreCase("MOB")) {
                        data = dataProvider.getData("Mob.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("??????")) {
                        data = dataProvider.getData("Skill.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("??????")) {
                        sb.append("#b???????????? '/map' ????????????. ?????????????????????, ????????????????????????.");
                    }
                    else {
                        sb.append("#b???????????????.\r\n??????: '/?????? [??????] [??????]', [??????] NPC, ??????, ??????, ??????.");
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
                            sb.append("#b??????????????????,????????????????????????.\r\n");
                            break;
                        }
                        if (!itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            continue;
                        }
                        sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
                    }
                }
                if (sb.length() == 0) {
                    sb.append("#b???????????? ").append(splitted[1].toLowerCase()).append("\r\n");
                }
                sb.append("\r\n#k???????????? ").append((System.currentTimeMillis() - start) / 1000.0).append(" ???.");
            }
            else {
                sb.append("#b???????????????.\r\n??????: '/?????? [??????] [??????]', [??????] NPC, ??????, ??????, ??????.");
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
            return new StringBuilder().append("!??????npc <charname> <npcid> - ????????????PNPC").toString();
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
            return new StringBuilder().append("!destroypnpc [objectid] - ??????PNPC").toString();
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
            return new StringBuilder().append("!mypos - ????????????").toString();
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
            return new StringBuilder().append("!reloadops - ????????????OpCode").toString();
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
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
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
            return new StringBuilder().append("!reloadportals - ?????????????????????").toString();
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
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
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
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
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
                c.getPlayer().dropMessage(5, "??????: " + e.getMessage());
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
            return new StringBuilder().append("!spawn <??????ID> <hp|exp|php||pexp = ?> - ??????").toString();
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
            return new StringBuilder().append("!clock <time> ??????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
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
            return new StringBuilder().append("!WarpPlayersTo <maipid> ????????????????????????????????????").toString();
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
            return new StringBuilder().append("!lolcastle level (level = 1-5) - ???????????????").toString();
        }
    }
    
    public static class ?????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(5, "???????????????.");
                    return 1;
                }
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    }
                    catch (IndexOutOfBoundsException e2) {
                        c.getPlayer().dropMessage(5, "???????????????.");
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
            return new StringBuilder().append("!map <mapid|charname> [portal] - ??????????????????/???").toString();
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
            return new StringBuilder().append("!startprofiling ????????????JVM??????").toString();
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
            return new StringBuilder().append("!stopprofiling <filename> - ????????????JVM????????????????????????").toString();
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
                player.dropMessage("?????????????????????????????????NPC????????????????????????.");
                return 1;
            }
            player.dropMessage("Unsuccessful reset!");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <maipid> - ??????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!respawn - ??????????????????").toString();
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ??????????????????").toString();
        }
    }
    
    public static class ??????NPC extends CommandExecute
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
            return new StringBuilder().append("!??????npc - ????????????NPC").toString();
        }
    }
    
    public static class ?????????????????? extends CommandExecute
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
                c.getPlayer().dropMessage(6, "??????????????????");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("??????????????????");
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
                    c.getPlayer().dropMessage("????????????");
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
            return new StringBuilder().append("!?????????????????? ???????????? ????????????(0 = ????????? 1=????????? 2=????????? 3=????????? 4=????????? 5=?????????)(???????????????) - ??????????????????").toString();
        }
    }
    
    public static class ???????????????????????? extends CommandExecute
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
                    c.getPlayer().dropMessage(5, "???????????????????????????");
                    return 0;
                }
                final PreparedStatement ps2 = (PreparedStatement)dcon.prepareStatement("delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps2.setInt(1, Integer.parseInt(splitted[1]));
                ps2.setInt(2, id);
                ps2.executeUpdate();
                c.getPlayer().dropMessage(6, "??????ID??? " + splitted[1] + " ?????????" + quantity + "????????? " + name + " ??????????????????");
                ps.close();
                ps2.close();
                return 1;
            }
            catch (SQLException e) {
                return 0;
            }
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????????????????? <??????ID> <????????????> - ???????????????????????????").toString();
        }
    }
    
    public static class ExpEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <?????????>");
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
                    mch.startMapEffect("???????????????" + gain + "???????????????????????????????????????????????????????????????", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "?????????????????????????????????: " + ret + " ???????????????: " + gain + " ??????" + " ?????? " + " ??????: " + ret * gain);
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
                        c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????][1?????????.2????????????]");
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
                final String show = (type == 1) ? "??????" : "?????????";
                for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch2 : cserv2.getPlayerStorage().getAllCharacters()) {
                        mch2.startMapEffect("???????????????" + quantity + show + "????????????????????????????????????????????????????????????", 5121009);
                    }
                }
                c.getPlayer().dropMessage(6, "?????????????????????????????????: " + ret + " ???????????????: " + quantity + " ??????" + ((type == 1) ? "?????? " : " ????????? ") + " ??????: " + ret * quantity);
            }
            else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????][1?????????.2????????????]");
            }
            return 1;
        }
    }
    
    public static class mesoEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <?????????>");
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
                    mch2.startMapEffect("???????????????" + gain + "??????????????????????????????????????????????????????????????????", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "?????????????????????????????????: " + ret + " ???????????????: " + gain + " ????????? " + " ??????: " + ret * gain);
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
                if (splitted[1].equals("??????")) {
                    if (arg <= 50) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setExpRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "????????????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                        }
                    }
                    else {
                        mc.dropMessage("???????????????????????????");
                    }
                }
                else if (splitted[1].equals("??????")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "????????????????????????????????? " + arg + "???????????????????????????.????????????????????????????????????????????????"));
                        }
                    }
                    else {
                        mc.dropMessage("???????????????????????????");
                    }
                }
                else if (splitted[1].equals("??????")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setMesoRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "????????????????????????????????? " + arg + "???????????????????????????.????????????????????????????????????????????????"));
                        }
                    }
                    else {
                        mc.dropMessage("???????????????????????????");
                    }
                }
                else if (splitted[1].equalsIgnoreCase("boss??????")) {
                    if (arg <= 5) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setBossDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "BOSS??????????????????????????? " + arg + "???????????????????????????.????????????????????????????????????????????????"));
                        }
                    }
                    else {
                        mc.dropMessage("???????????????????????????");
                    }
                }
                else if (splitted[1].equals("????????????")) {
                    if (arg > 5) {
                        mc.dropMessage("???????????????????????????");
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
                    mc.dropMessage("????????????: !???????????? <exp??????|drop??????|meso??????|bossboss??????|pet> <???> <???> <???> <???>");
                }
            }
            else {
                mc.dropMessage("????????????: !???????????? <exp??????|drop??????|meso??????|bossboss??????|pet> <???> <???> <???> <???>");
            }
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
            return new StringBuilder().append("!WarpAllHere ??????????????????????????????").toString();
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
    
    public static class ????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.minSkills();
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
            player.dropMessage(5, "???????????????????????????: " + id);
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
            player.dropMessage(5, "???????????????????????????: " + id);
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
            player.dropMessage(5, "???????????????????????????: " + id);
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
            player.dropMessage(5, "???????????????????????????: " + id);
            return 1;
        }
    }
    
    public static class ????????? extends CommandExecute
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
                c.getPlayer().dropMessage(5, "??????????????????????????????.");
            }
            else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - ???????????????");
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
    
    public static class ???????????? extends CommandExecute
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
            c.getPlayer().dropMessage(5, "???????????? " + c.getPlayer().getMap().getId() + "?????? (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
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
            c.getPlayer().dropMessage(packetheader + "???????????????[" + mplew.getPacket().getBytes().length + "] : " + mplew.toString());
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
                    c.getPlayer().dropMessage(6, "?????? " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "???????????????");
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
                c.getPlayer().dropMessage("?????????????????????");
                return 0;
            }
            final boolean ACCexist = AutoRegister.getAccountExists(acc);
            if (ACCexist) {
                c.getPlayer().dropMessage("??????????????????");
                return 0;
            }
            if (acc.length() >= 12) {
                c.getPlayer().dropMessage("??????????????????");
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
            c.getPlayer().dropMessage("[????????????]??????: " + acc + " ??????: " + password);
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
                c.getPlayer().dropMessage(splitted[0] + " - ????????????");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().HealMap(mapid);
                c.getPlayer().dropMessage(mapid + " - ???????????????");
            }
            return 1;
        }
    }
    
    public static class ?????? extends CommandExecute
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
                c.getPlayer().dropMessage(splitted[0] + " - ????????????");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            if (c.getChannelServer().getMapFactory().getMap(mapid) == null) {
                c.getPlayer().dropMessage("???????????????");
                return 0;
            }
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().destroyMap(mapid, true);
                c.getPlayer().dropMessage(mapid + " - ???????????????");
            }
            return 1;
        }
    }
    
    public static class ???????????? extends CommandExecute
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
                c.getPlayer().dropMessage(5, "?????????????????????: " + msgs.size() + " ???????????????\n");
                for (final String s : msgs) {
                    c.getPlayer().dropMessage(5, s);
                    FileoutputUtil.log("??????/????????????.txt", "?????????????????????: " + msgs.size() + " ???????????????", true);
                }
                c.getPlayer().dropMessage(5, "??????????????????????????????????????????.");
            }
            else {
                c.getPlayer().dropMessage(5, "??????????????????????????????????????????????????????.");
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
                            msgs.add("??????: " + StringUtil.getRightPaddedStr(ret.name, ' ', 13) + " ??????ID: " + StringUtil.getRightPaddedStr(String.valueOf(ret.chrId), ' ', 6) + " ??????: " + ret.itemId + " - " + ii.getName(ret.itemId) + " ??????ID: " + item.getEquipOnlyId());
                        }
                        else {
                            msgs.add("??????: " + StringUtil.getRightPaddedStr(player.getName(), ' ', 13) + " ??????ID: " + StringUtil.getRightPaddedStr(String.valueOf(player.getId()), ' ', 6) + " ??????: " + item.getItemId() + " - " + ii.getName(item.getItemId()) + " ??????ID: " + item.getEquipOnlyId());
                        }
                    }
                    else {
                        checkItems.put(item.getEquipOnlyId(), ret);
                    }
                }
            }
        }
    }
    
    public static class ???????????? extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.unequipAllPets();
                    map.dropMessage(5, "??????????????????????????????");
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????????????????Buff").toString();
        }
    }
}
