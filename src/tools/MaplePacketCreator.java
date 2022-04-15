package tools;

import client.inventory.ModifyInventory;
import java.sql.SQLException;
import java.sql.ResultSet;
import handling.channel.handler.Beans;
import server.maps.MapleNodes.MaplePlatform;
import server.maps.MapleNodes.MapleNodeInfo;
import server.life.MapleMonster;
import server.shops.MaplePlayerShopItem;
import server.shops.HiredMerchant;
import client.MapleStat.Temp;
import server.maps.MapleDragon;
import server.events.MapleSnowball.MapleSnowballs;
import server.MapleDueyActions;
import client.SkillMacro;
import handling.channel.MapleGuildRanking.GuildRankingInfo;
import handling.world.guild.MapleBBSThread.MapleBBSReply;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuildCharacter;
import server.maps.MapleReactor;
import client.BuddyEntry;
import server.maps.MapleMist;
import handling.world.PartyOperation;
import handling.world.MaplePartyCharacter;
import handling.world.MapleParty;
import client.MapleKeyLayout;
import server.MapleTrade;
import server.MapleStatEffect;
import client.MapleDisease;
import client.inventory.MapleInventory;
import client.inventory.MapleMount;
import handling.world.guild.MapleGuildAlliance;
import java.util.ArrayList;
import client.inventory.MaplePet;
import handling.world.World.Alliance;
import client.MapleQuestStatus;
import client.inventory.Equip.ScrollResult;
import server.MapleItemInformationProvider;
import server.MapleShopItem;
import client.MapleClient;
import server.life.SummonAttackEntry;
import tools.data.output.LittleEndianWriter;
import server.movement.LifeMovementFragment;
import handling.world.guild.MapleGuild;
import client.inventory.MapleRing;
import java.util.Collection;
import constants.GameConstants;
import client.inventory.MapleInventoryType;
import server.Randomizer;
import client.MapleBuffStat;
import handling.world.World.Guild;
import server.maps.MapleMapItem;
import handling.ByteArrayMaplePacket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import server.life.PlayerNPC;
import server.life.MapleNPC;
import client.inventory.Item;
import server.maps.MapleSummon;
import java.awt.Point;
import server.maps.MapleMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import tools.packet.PacketHelper;
import client.MapleCharacter;
import java.net.UnknownHostException;
import java.net.InetAddress;
import server.ServerProperties;
import handling.SendPacketOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import handling.MaplePacket;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.File;
import server.Timer.WorldTimer;
import constants.ServerConstants;
import client.MapleStat;
import java.util.List;

public class MaplePacketCreator
{
    public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE;
    private static final byte[] CHAR_INFO_MAGIC;
    private static String[] serialNumberStr;
    private static final boolean showPacket = false;
    ServerConstants ERROR;
    
    public MaplePacketCreator() {
        this.ERROR = new ServerConstants();
    }
    
    public static void start() {
        WorldTimer.getInstance().register(() -> {}, 120000L);
    }
    
    public static boolean 检测机器码() {
        boolean isok = false;
        final String num = returnSerialNumber();
        for (int i = 0; i < MaplePacketCreator.serialNumberStr.length; ++i) {
            final String tempnum = getSerialNumberStrStr(i);
            if (num.equals(tempnum)) {
                isok = true;
            }
        }
        if (isok) {
            return true;
        }
        FileoutputUtil.log("机器码.txt", num);
        System.err.println("机器码:" + num);
        return false;
    }
    
    public static String returnSerialNumber() {
        final String cpu = getCPUSerial();
        final String disk = getHardDiskSerialNumber("C");
        final int newdisk = Integer.parseInt(disk);
        final String s = cpu + newdisk;
        final String newStr = s.substring(8, s.length());
        return newStr;
    }
    
    public static String getCPUSerial() {
        String result = "";
        try {
            final File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            final FileWriter fw = new FileWriter(file);
            final String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_Processor\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.ProcessorId \n    exit for  ' do the first cpu only! \nNext \n";
            fw.write(vbs);
            fw.close();
            final Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        }
        catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }
    
    public static String getHardDiskSerialNumber(final String drive) {
        String result = "";
        try {
            final File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            final FileWriter fw = new FileWriter(file);
            final String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\nSet colDrives = objFSO.Drives\nSet objDrive = colDrives.item(\"" + drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber";
            fw.write(vbs);
            fw.close();
            final Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        }
        catch (Exception ex) {}
        return result.trim();
    }
    
    public static boolean isshowPacket() {
        return false;
    }
    
    public static String getSerialNumberStrStr(final int index) {
        final String tempStr = MaplePacketCreator.serialNumberStr[index];
        return tempStr;
    }
    
    public static MaplePacket loveEffect() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(72);
        mplew.writeZeroBytes(20);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getServerIP(final int port, final int clientId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getServerIP--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.writeShort(0);
        try {
            mplew.write(InetAddress.getByName(ServerProperties.getProperty("tms.IP")).getAddress());
        }
        catch (UnknownHostException e) {
            System.err.println("登录服务器IP：" + e);
        }
        mplew.writeShort(port);
        mplew.writeInt(clientId);
        mplew.write(new byte[] { 1, 0, 0, 0, 0 });
        return mplew.getPacket();
    }
    
    public static MaplePacket getChannelChange(final InetAddress inetAddr, final int port) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getChannelChange--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        try {
            mplew.write(InetAddress.getByName(ServerProperties.getProperty("tms.IP")).getAddress());
        }
        catch (UnknownHostException ex) {}
        mplew.writeShort(port);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getCharInfo(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getCharInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.write(1);
        mplew.write(1);
        mplew.writeShort(0);
        chr.CRand().connectData(mplew);
        PacketHelper.addCharacterInfo(mplew, chr);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getCharInfo-175：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket enableActions() {
        if (ServerConstants.调试输出封包) {
            System.err.println("enableActions--------------------");
        }
        return updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, 0);
    }
    
    public static final MaplePacket updatePlayerStats(final List<Pair<MapleStat, Integer>> stats, final int evan) {
        if (ServerConstants.调试输出封包) {
            System.err.println("updatePlayerStatsA--------------------");
        }
        return updatePlayerStats(stats, false, evan);
    }
    
    public static final MaplePacket updatePlayerStats(final List<Pair<MapleStat, Integer>> stats, final boolean itemReaction, final int evan) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updatePlayerStats--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(itemReaction ? 1 : 0);
        int updateMask = 0;
        for (final Pair<MapleStat, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        final List<Pair<MapleStat, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<MapleStat, Integer>>() {
                @Override
                public int compare(final Pair<MapleStat, Integer> o1, final Pair<MapleStat, Integer> o2) {
                    final int val1 = o1.getLeft().getValue();
                    final int val2 = o2.getLeft().getValue();
                    return (val1 == val2) ? 0 : ((val1 < val2) ? -1 : 1);
                }
            });
        }
        mplew.writeInt(updateMask);
        for (final Pair<MapleStat, Integer> statupdate2 : mystats) {
            if (statupdate2.getLeft().getValue() >= 1) {
                if (statupdate2.getLeft().getValue() == 1) {
                    mplew.writeShort(statupdate2.getRight().shortValue());
                }
                else if (statupdate2.getLeft().getValue() <= 4) {
                    mplew.writeInt(statupdate2.getRight());
                }
                else if (statupdate2.getLeft().getValue() < 128) {
                    mplew.write(statupdate2.getRight().shortValue());
                }
                else if (statupdate2.getLeft().getValue() < 262144) {
                    mplew.writeShort(statupdate2.getRight().shortValue());
                }
                else {
                    mplew.writeInt(statupdate2.getRight());
                }
            }
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket blockedPortal() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("blockedPortal--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(1);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("blockedPortal-253：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket weirdStatUpdate() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("weirdStatUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(0);
        mplew.write(56);
        mplew.writeShort(0);
        mplew.writeLong(0L);
        mplew.writeLong(0L);
        mplew.writeLong(0L);
        mplew.write(0);
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("weirdStatUpdate-276：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket updateSp(final MapleCharacter chr, final boolean itemReaction) {
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSpA--------------------");
        }
        return updateSp(chr, itemReaction, false);
    }
    
    public static final MaplePacket updateSp(final MapleCharacter chr, final boolean itemReaction, final boolean overrideJob) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSp--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(itemReaction ? 1 : 0);
        mplew.writeInt(131072);
        mplew.writeShort(chr.getRemainingSp());
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateSp-310：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getWarpToMap(final MapleMap to, final int spawnPoint, final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getWarpToMap--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.write(3);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeShort(chr.getStat().getHp());
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getWarpToMap-336：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket instantMapWarp(final byte portal) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("instantMapWarp--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
        mplew.write(0);
        mplew.write(portal);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("instantMapWarp-353：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket spawnPortal(final int townId, final int targetId, final int skillId, final Point pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnPortal--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if (pos != null) {
            mplew.writePos(pos);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnPortal-374：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket spawnDoor(final int oid, final Point pos, final boolean town) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnDoor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());
        mplew.write(town ? 1 : 0);
        mplew.writeInt(oid);
        mplew.writePos(pos);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnDoor-392：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeDoor(final int oid, final boolean town) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeDoor--------------------");
        }
        if (town) {
            mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
        }
        else {
            mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
            mplew.write(0);
            mplew.writeInt(oid);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeDoor-415：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnSummon(final MapleSummon summon, final boolean animated) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnSummon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(summon.getSkill());
        mplew.write(summon.getOwnerLevel());
        mplew.write(summon.getSkillLevel());
        mplew.writeShort(summon.getPosition().x + 200);
        mplew.writeInt(summon.getPosition().y);
        mplew.write(0);
        mplew.write(summon.getMovementType().getValue());
        mplew.write(summon.getSummonType());
        mplew.write(animated ? 0 : 1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnSummon-452：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeSummon(final MapleSummon summon, final boolean animated) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeSummon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.write(animated ? 4 : 1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeSummon-470：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getRelogResponse() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        if (ServerConstants.调试输出封包) {
            System.err.println("getRelogResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.RELOG_RESPONSE.getValue());
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getRelogResponse-486：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket serverBlocked(final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("serverBlocked--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVER_BLOCKED.getValue());
        mplew.write(type);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("serverBlocked-516：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket serverMessage(final String message) {
        if (ServerConstants.调试输出封包) {}
        return serverMessage(4, 0, message, false);
    }
    
    public static MaplePacket serverNotice(final int type, final String message) {
        if (ServerConstants.调试输出封包) {}
        return serverMessage(type, 0, message, false);
    }
    
    public static MaplePacket serverNotice(final int type, final int channel, final String message) {
        if (ServerConstants.调试输出封包) {}
        return serverMessage(type, channel, message, false);
    }
    
    public static MaplePacket serverNotice(final int type, final int channel, final String message, final boolean smegaEar) {
        if (ServerConstants.调试输出封包) {}
        return serverMessage(type, channel, message, smegaEar);
    }
    
    public static MaplePacket spouseMessage(final int op, final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.writeShort(op);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }
    
    private static MaplePacket serverMessage(final int type, final int channel, final String message, final boolean megaEar) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {}
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        }
        mplew.writeMapleAsciiString(message);
        switch (type) {
            case 3:
            case 9:
            case 10:
            case 11:
            case 12: {
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            }
            case 6:
            case 18: {
                mplew.writeInt((channel >= 1000000 && channel < 6000000) ? channel : 0);
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("serverMessage-596：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getGachaponMega(final String name, final String message, final Item item, final byte rareness, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getGachaponMega--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(14);
        mplew.writeMapleAsciiString(name + message);
        mplew.writeInt(channel - 1);
        PacketHelper.addItemInfo(mplew, item, true, true);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getGachaponMega-616：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket tripleSmega(final List<String> message, final boolean ear, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("tripleSmega--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(10);
        if (message.get(0) != null) {
            mplew.writeMapleAsciiString(message.get(0));
        }
        mplew.write(message.size());
        for (int i = 1; i < message.size(); ++i) {
            if (message.get(i) != null) {
                mplew.writeMapleAsciiString(message.get(i));
            }
        }
        mplew.write(channel - 1);
        mplew.write(ear ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("tripleSmega-644：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getAvatarMega(final MapleCharacter chr, final int channel, final int itemId, final String message, final boolean ear) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getAvatarMega--------------------");
        }
        mplew.writeShort(SendPacketOpcode.AVATAR_MEGA.getValue());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(channel - 1);
        mplew.write(ear ? 1 : 0);
        PacketHelper.addCharLook(mplew, chr, true);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getAvatarMega-665：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket itemMegaphone(final String msg, final boolean whisper, final int channel, final Item item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("itemMegaphone--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(8);
        mplew.writeMapleAsciiString(msg);
        mplew.write(channel - 1);
        mplew.write(whisper ? 1 : 0);
        if (item == null) {
            mplew.write(0);
        }
        else {
            PacketHelper.addItemInfo(mplew, item, false, false, true);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("itemMegaphone-689：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnNPC(final MapleNPC life, final boolean show) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write((life.getF() != 1) ? 1 : 0);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(show ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnNPC-713：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeNPC(final int objectid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeNPC--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
        mplew.writeInt(objectid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeNPC-729：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnNPCRequestController(final MapleNPC life, final boolean MiniMap) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {}
        mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write((life.getF() != 1) ? 1 : 0);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(MiniMap ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnNPCRequestController-754：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnPlayerNPC1(final PlayerNPC npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnPlayerNPC--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
        mplew.write((npc.getF() != 1) ? 1 : 0);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        final Map<Byte, Integer> equip = npc.getEquips();
        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (final Entry<Byte, Integer> position : equip.entrySet()) {
            byte pos = (byte)(position.getKey() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, position.getValue());
            }
            else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte)((pos == -128) ? 28 : (pos - 100));
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, position.getValue());
            }
            else {
                if (myEquip.get(pos) == null) {
                    continue;
                }
                maskedEquip.put(pos, position.getValue());
            }
        }
        for (final Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        for (final Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        final Integer cWeapon = equip.get(-111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon);
        }
        else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; ++i) {
            mplew.writeInt(npc.getPet(i));
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnPlayerNPC-812：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnPlayerNPC(final PlayerNPC npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnPlayerNPC--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
        mplew.write((npc.getF() != 1) ? 1 : 0);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        final Map<Byte, Integer> equip = npc.getEquips();
        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (final Entry<Byte, Integer> position : equip.entrySet()) {
            byte pos = (byte)(position.getKey() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, position.getValue());
            }
            else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte)((pos == -128) ? 28 : (pos - 100));
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, position.getValue());
            }
            else {
                if (myEquip.get(pos) == null) {
                    continue;
                }
                maskedEquip.put(pos, position.getValue());
            }
        }
        for (final Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        for (final Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        final Integer cWeapon = equip.get(-111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon);
        }
        else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; ++i) {
            mplew.writeInt(npc.getPet(i));
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnPlayerNPC-812：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getChatText(final int cidfrom, final String text, final boolean whiteBG, final int show) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getChatText--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getChatText-831：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket GameMaster_Func(final int value) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("GameMaster_Func--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GM_EFFECT.getValue());
        mplew.write(value);
        mplew.writeZeroBytes(17);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("GameMaster_Func-848：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket testCombo(final int value) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("testCombo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ARAN_COMBO.getValue());
        mplew.writeInt(value);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("testCombo-864：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPacketFromHexString(final String hex) {
        if (ServerConstants.调试输出封包) {
            System.err.println("getPacketFromHexString--------------------");
        }
        return new ByteArrayMaplePacket(HexTool.getByteArrayFromHexString(hex));
    }
    
    public static final MaplePacket GainEXP_Monster(final int gain, final boolean white, final int partyinc, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, final int 网吧经验, final int 结婚经验) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("GainEXP_Monster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(结婚经验);
        mplew.write(0);
        mplew.writeInt(partyinc);
        mplew.writeInt(Equipment_Bonus_EXP);
        mplew.writeInt(网吧经验);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("GainEXP_Monster-903：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("GainEXP_Others--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(0);
        mplew.writeInt(inChat ? 1 : 0);
        mplew.writeShort(0);
        mplew.writeZeroBytes(4);
        if (inChat) {
            mplew.writeZeroBytes(13);
        }
        else {
            mplew.writeZeroBytes(13);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("GainEXP_Others-935：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getShowFameGain(final int gain) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowFameGain--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(4);
        mplew.writeInt(gain);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getShowFameGain-952：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket showMesoGain(final int gain, final boolean inChat) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showMesoGain--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        if (!inChat) {
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);
        }
        else {
            mplew.write(5);
        }
        mplew.writeInt(gain);
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showMesoGain-976：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getShowItemGain(final int itemId, final short quantity) {
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowItemGainA--------------------");
        }
        return getShowItemGain(itemId, quantity, false);
    }
    
    public static MaplePacket getShowItemGain(final int itemId, final short quantity, final boolean inChat) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowItemGain--------------------");
        }
        if (inChat) {
            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(3);
            mplew.write(1);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        }
        else {
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getShowItemGain-1014：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showRewardItemAnimation(final int itemId, final String effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showRewardItemAnimationA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(11);
        mplew.writeInt(itemId);
        mplew.write((effect != null && effect.length() > 0) ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showRewardItemAnimationA-1035：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showRewardItemAnimation(final int itemId, final String effect, final int from_playerid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showRewardItemAnimationB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(from_playerid);
        mplew.write(11);
        mplew.writeInt(itemId);
        mplew.write((effect != null && effect.length() > 0) ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showRewardItemAnimationB-1057：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket dropItemFromMapObject(final MapleMapItem drop, final Point dropfrom, final Point dropto, final byte mod) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("dropItemFromMapObject--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
        mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write((drop.getMeso() > 0) ? 1 : 0);
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType());
        mplew.writePos(dropto);
        mplew.writeInt(0);
        if (mod != 2) {
            mplew.writePos(dropfrom);
        }
        mplew.write(0);
        if (mod != 2) {
            mplew.write(0);
            mplew.write(1);
        }
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("dropItemFromMapObject-1095：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnPlayerMapobject(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnPlayerMapobject--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());
        if (chr.isAriantPQMap()) {
            mplew.writeMapleAsciiString("1st");
            mplew.write(new byte[6]);
        }
        else if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("");
            mplew.write(new byte[6]);
        }
        else {
            final MapleGuild gs = Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            }
            else {
                mplew.writeMapleAsciiString("");
                mplew.write(new byte[6]);
            }
        }
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(224);
        mplew.write(31);
        mplew.write(0);
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            mplew.writeInt(2);
        }
        else {
            mplew.writeInt(0);
        }
        long buffmask = 0L;
        Integer buffvalue = null;
        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue = chr.getBuffedValue(MapleBuffStat.COMBO);
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            buffmask |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffvalue = chr.getBuffedValue(MapleBuffStat.MORPH);
        }
        if (chr.getBuffedValue(MapleBuffStat.能量) != null) {
            buffmask |= MapleBuffStat.能量.getValue();
            buffvalue = chr.getBuffedValue(MapleBuffStat.能量);
        }
        mplew.writeInt((int)(buffmask >> 32 & -1L));
        if (buffvalue != null) {
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                mplew.writeShort(buffvalue);
            }
            else {
                mplew.write(buffvalue.byteValue());
            }
        }
        final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
        mplew.writeInt((int)(buffmask & -1L));
        mplew.write(new byte[6]);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        final Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18));
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null && mount != null) {
            mplew.writeInt(mount.getItemId());
            mplew.writeInt(1004);
            mplew.writeInt(19275520);
            mplew.write(0);
        }
        else {
            mplew.writeInt(CHAR_MAGIC_SPAWN);
            mplew.writeLong(0L);
            mplew.write(0);
        }
        mplew.writeLong(0L);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.write(0);
        mplew.write(1);
        mplew.write(65);
        mplew.write(154);
        mplew.write(112);
        mplew.write(7);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0L);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.write(0);
        mplew.writeShort(chr.getJob());
        PacketHelper.addCharLook(mplew, chr, false);
        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000)));
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeInt((GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP) ? chr.getChair() : 0);
        mplew.writePos(chr.getPosition());
        mplew.write(chr.getStance());
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        PacketHelper.addAnnounceBox(mplew, chr);
        mplew.write((chr.getChalkboard() != null && chr.getChalkboard().length() > 0) ? 1 : 0);
        if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }
        final Pair<List<MapleRing>, List<MapleRing>>  rings = chr.getRings(false);
        final List allrings = rings.getLeft();
        allrings.addAll(rings.getRight());
        addRingInfo(mplew, allrings);
        addRingInfo(mplew, allrings);
        addMarriageRingLook(mplew, chr);
        mplew.writeShort(0);
        if (chr.getCarnivalParty() != null) {
            mplew.write(chr.getCarnivalParty().getTeam());
        }
        else if (chr.getMapId() == 109080000 || chr.getMapId() == 109080010) {
            mplew.write(chr.getCoconutTeam());
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removePlayerFromMap(final int cid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("removePlayerFromMap--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        mplew.writeInt(cid);
        return mplew.getPacket();
    }
    
    public static MaplePacket removePlayerFromMap(final int cid, final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removePlayerFromMap--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        mplew.writeInt(cid);
        final ServerConstants ERROR = new ServerConstants();
        if ((ServerConstants.get38记录() && ERROR.getChannel() != 1) || ERROR.getRemovePlayerFromMap() != 1) {
            final String note = "时间：" + FileoutputUtil.CurrentReadable_Time() + " || 玩家名字：" + chr.getName() + "|| 玩家地图：" + chr.getMapId() + "\r\n38错误：" + ERROR.getPACKET_ERROR() + "\r\n\r\n";
            FileoutputUtil.packetLog("日志/Logs\\38掉线\\" + chr.getName() + ".log", note);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket facialExpression(final MapleCharacter from, final int expression) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("facialExpression--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("facialExpression-2092：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket movePlayer(final int cid, final List<LifeMovementFragment> moves, final Point startPos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {}
        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);
        if (ServerConstants.get38记录()) {
            final ServerConstants serverConstants = new ServerConstants();
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveSummon(final int cid, final int oid, final Point startPos, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveSummon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writePos(startPos);
        PacketHelper.serializeMovementList(mplew, moves);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveSummon-2131：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket summonAttack(final int cid, final int summonSkillId, final byte animation, final List<SummonAttackEntry> allDamage) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("summonAttack--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("summonAttack-2158：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket closeRangeAttack(final int cid, final int tbyte, final int skill, final int level, final byte display, final byte animation, final byte speed, final List<AttackPair> damage, final boolean energy, final int lvl, final byte mastery, final byte unk, final int charge) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("closeRangeAttack--------------------");
        }
        mplew.writeShort(energy ? SendPacketOpcode.ENERGY_ATTACK.getValue() : SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        }
        else {
            mplew.write(0);
        }
        mplew.write(unk);
        mplew.write(display);
        mplew.write(animation);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(0);
        if (skill == 4211006) {
            for (final AttackPair oned : damage) {
                if (oned.attack != null) {
                    mplew.writeInt(oned.objectid);
                    mplew.write(7);
                    mplew.write(oned.attack.size());
                    for (final Pair<Integer, Boolean> eachd : oned.attack) {
                        mplew.writeInt(eachd.left);
                    }
                }
            }
        }
        else {
            for (final AttackPair oned : damage) {
                if (oned.attack != null) {
                    mplew.writeInt(oned.objectid);
                    mplew.write(7);
                    for (final Pair<Integer, Boolean> eachd : oned.attack) {
                        if (eachd.right) {
                            mplew.writeInt(eachd.left + Integer.MIN_VALUE);
                        }
                        else {
                            mplew.writeInt(eachd.left);
                        }
                    }
                }
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("closeRangeAttack-2219：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket rangedAttack(final int cid, final byte tbyte, final int skill, final int level, final byte display, final byte animation, final byte speed, final int itemid, final List<AttackPair> damage, final Point pos, final int lvl, final byte mastery, final byte unk) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("rangedAttack--------------------");
        }
        mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        }
        else {
            mplew.write(0);
        }
        mplew.write(unk);
        mplew.write(display);
        mplew.write(animation);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(itemid);
        for (final AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(7);
                for (final Pair<Integer, Boolean> eachd : oned.attack) {
                    if (eachd.right) {
                        mplew.writeInt(eachd.left + Integer.MIN_VALUE);
                    }
                    else {
                        mplew.writeInt(eachd.left);
                    }
                }
            }
        }
        mplew.writePos(pos);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("rangedAttack-2265：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket magicAttack(final int cid, final int tbyte, final int skill, final int level, final byte display, final byte animation, final byte speed, final List<AttackPair> damage, final int charge, final int lvl, final byte unk) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("magicAttack--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MAGIC_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        mplew.write(level);
        mplew.writeInt(skill);
        mplew.write(unk);
        mplew.write(display);
        mplew.write(animation);
        mplew.write(speed);
        mplew.write(0);
        mplew.writeInt(0);
        for (final AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(-1);
                for (final Pair<Integer, Boolean> eachd : oned.attack) {
                    if (eachd.right) {
                        mplew.writeInt(eachd.left + Integer.MIN_VALUE);
                    }
                    else {
                        mplew.writeInt(eachd.left);
                    }
                }
            }
        }
        if (charge > 0) {
            mplew.writeInt(charge);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("magicAttack-2309：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getNPCShop(final MapleClient c, final int sid, final List<MapleShopItem> items) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ServerConstants.调试输出封包) {
            System.err.println("getNPCShop--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
        mplew.writeInt(sid);
        mplew.writeShort(items.size());
        for (final MapleShopItem item : items) {
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getPrice());
            if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                mplew.writeShort(1);
                mplew.writeShort(item.getBuyable());
            }
            else {
                mplew.writeZeroBytes(6);
                mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                mplew.writeShort(ii.getSlotMax(c, item.getItemId()));
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNPCShop-2341：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket confirmShopTransaction(final byte code) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("confirmShopTransaction--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
        mplew.write(code);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("confirmShopTransaction-2357：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket addInventorySlot(final MapleInventoryType type, final Item item) {
        if (ServerConstants.调试输出封包) {
            System.err.println("addInventorySlotA--------------------");
        }
        return addInventorySlot(type, item, false);
    }
    
    public static MaplePacket addInventorySlot(final MapleInventoryType type, final Item item, final boolean fromDrop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("addInventorySlot--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.writeShort(1);
        mplew.write(type.getType());
        mplew.write(item.getPosition());
        PacketHelper.addItemInfo(mplew, item, true, false);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("addInventorySlot-2384：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateInventorySlot(final MapleInventoryType type, final Item item, final boolean fromDrop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateInventorySlot--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateInventorySlot-2411：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveInventoryItem(final MapleInventoryType type, final short src, final short dst) {
        if (ServerConstants.调试输出封包) {
            System.err.println("moveInventoryItemA--------------------");
        }
        return moveInventoryItem(type, src, dst, (short)(-1));
    }
    
    public static MaplePacket moveInventoryItem(final MapleInventoryType type, final short src, final short dst, final short equipIndicator) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveInventoryItemB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 02"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveInventoryItemB-2439：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveAndMergeInventoryItem(final MapleInventoryType type, final short src, final short dst, final short total) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveAndMergeInventoryItem--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1);
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveAndMergeInventoryItem-2461：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveAndMergeWithRestInventoryItem(final MapleInventoryType type, final short src, final short dst, final short srcQ, final short dstQ) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveAndMergeWithRestInventoryItem--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveAndMergeWithRestInventoryItem-2484：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket clearInventoryItem(final MapleInventoryType type, final short slot, final boolean fromDrop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("clearInventoryItem--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));
        mplew.write(type.getType());
        mplew.writeShort(slot);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("clearInventoryItem-2503：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateSpecialItemUse(final Item item, final byte invType) {
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSpecialItemUseA--------------------");
        }
        return updateSpecialItemUse(item, invType, item.getPosition());
    }
    
    public static MaplePacket updateSpecialItemUse(final Item item, final byte invType, final short pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSpecialItemUseB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(invType);
        mplew.writeShort(pos);
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        }
        else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(2);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateSpecialItemUseB-2541：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateSpecialItemUse_(final Item item, final byte invType) {
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSpecialItemUse_A--------------------");
        }
        return updateSpecialItemUse_(item, invType, item.getPosition());
    }
    
    public static MaplePacket updateSpecialItemUse_(final Item item, final byte invType, final short pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSpecialItemUse_B--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(1);
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        }
        else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(1);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateSpecialItemUse_B-2576：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket scrolledItem(final Item scroll, final Item item, final boolean destroyed, final boolean potential) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("scrolledItem--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.write(destroyed ? 2 : 3);
        mplew.write((scroll.getQuantity() > 0) ? 1 : 3);
        mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
        mplew.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            mplew.writeShort(scroll.getQuantity());
        }
        mplew.write(3);
        if (!destroyed) {
            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
        }
        mplew.write(MapleInventoryType.EQUIP.getType());
        mplew.writeShort(item.getPosition());
        if (!destroyed) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        if (!potential) {
            mplew.write(1);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("scrolledItem-2614：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getScrollEffect(final int chr, final ScrollResult scrollSuccess, final boolean legendarySpirit) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getScrollEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chr);
        switch (scrollSuccess) {
            case 成功: {
                mplew.writeShort(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            }
            case 失败: {
                mplew.writeShort(0);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            }
            case 消失: {
                mplew.write(0);
                mplew.write(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            }
        }
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getScrollEffect-2646：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPotentialEffect(final int chr, final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPotentialEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.writeInt(itemid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPotentialEffect-2663：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPotentialReset(final int chr, final short pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPotentialReset--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue());
        mplew.writeInt(chr);
        mplew.writeShort(pos);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPotentialReset-2680：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket ItemMaker_Success() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("ItemMaker_Success--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(17);
        mplew.writeZeroBytes(4);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("ItemMaker_Success-2697：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket ItemMaker_Success_3rdParty(final int from_playerid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("ItemMaker_Success_3rdParty--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(from_playerid);
        mplew.write(17);
        mplew.writeZeroBytes(4);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("ItemMaker_Success_3rdParty-2715：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket explodeDrop(final int oid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("explodeDrop--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("explodeDrop-2733：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeItemFromMap(final int oid, final int animation, final int cid) {
        if (ServerConstants.调试输出封包) {
            System.err.println("removeItemFromMapA--------------------");
        }
        return removeItemFromMap(oid, animation, cid, 0);
    }
    
    public static MaplePacket removeItemFromMap(final int oid, final int animation, final int cid, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeItemFromMapB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) {
                mplew.write(slot);
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeItemFromMapB-2762：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateCharLook(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateCharLook--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false);
        final Pair<List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        final List allrings = rings.getLeft();
        allrings.addAll(rings.getRight());
        addRingInfo(mplew, allrings);
        addRingInfo(mplew, allrings);
        addMarriageRingLook(mplew, chr);
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    private static void addMarriageRingLook(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.write((byte)((chr.getMarriageRing(false) != null) ? 1 : 0));
        if (chr.getMarriageRing(false) != null) {
            mplew.writeInt(chr.getId());
            mplew.writeInt(chr.getMarriageRing(false).getPartnerChrId());
            mplew.writeInt(chr.getMarriageRing(false).getRingId());
        }
    }
    
    public static void addRingInfo(final MaplePacketLittleEndianWriter mplew, final List<MapleRing> rings) {
        if (ServerConstants.调试输出封包) {
            System.err.println("addRingInfo--------------------");
        }
        mplew.write((rings.size() > 0) ? 1 : 0);
        mplew.writeInt(rings.size());
        for (final MapleRing ring : rings) {
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }
    
    public static MaplePacket dropInventoryItem(final MapleInventoryType type, final short src) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("dropInventoryItem--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("dropInventoryItem-2818：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket dropInventoryItemUpdate(final MapleInventoryType type, final Item item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("dropInventoryItemUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("dropInventoryItemUpdate-2838：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket damagePlayer(final int skill, final int monsteridfrom, final int cid, final int damage) {
        return damagePlayer(skill, monsteridfrom, cid, damage, 0, (byte)0, 0, false, 0, 0, 0);
    }
    
    public static MaplePacket damagePlayer(final int skill, final int monsteridfrom, final int cid, final int damage, final int fake, final byte direction, final int reflect, final boolean is_pg, final int oid, final int pos_x, final int pos_y) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("damagePlayer--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(direction);
        if (reflect > 0) {
            mplew.write(reflect);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);
        }
        else {
            mplew.writeShort(0);
        }
        mplew.writeInt(damage);
        if (fake > 0) {
            mplew.writeInt(fake);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("damagePlayer-2873：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket updateQuest(final MapleQuestStatus quest) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateQuest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest.getQuest().getId());
        mplew.write(quest.getStatus());
        switch (quest.getStatus()) {
            case 0: {
                mplew.writeZeroBytes(10);
                break;
            }
            case 1: {
                mplew.writeMapleAsciiString((quest.getCustomData() != null) ? quest.getCustomData() : "");
                break;
            }
            case 2: {
                mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateQuest-2902：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket updateInfoQuest(final int quest, final String data) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateInfoQuest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(10);
        mplew.writeShort(quest);
        mplew.writeMapleAsciiString(data);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateInfoQuest-2920：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateQuestInfo(final MapleCharacter c, final int quest, final int npc, final byte progress) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateQuestInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateQuestInfo-2939：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateQuestFinish(final int quest, final int npc, final int nextquest) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateQuestFinish--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(8);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(nextquest);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateQuestFinish-2956：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket charInfo(final MapleCharacter chr, final boolean isSelf) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("charInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAR_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        mplew.writeShort(chr.getFame());
        mplew.write((chr.getMarriageId() > 0) ? 1 : 0);
        String guildName = "-";
        String allianceName = "-";
        final MapleGuild gs = Guild.getGuild(chr.getGuildId());
        if (chr.getGuildId() > 0 && gs != null) {
            guildName = gs.getName();
            if (gs.getAllianceId() > 0) {
                final MapleGuildAlliance allianceNameA = Alliance.getAlliance(gs.getAllianceId());
                if (allianceNameA != null) {
                    allianceName = allianceNameA.getName();
                }
            }
        }
        mplew.writeMapleAsciiString(guildName);
        mplew.writeMapleAsciiString(allianceName);
        final Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-114));
        final int peteqid = (inv != null) ? inv.getItemId() : 0;
        final Item inv2 = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-122));
        final int peteqid2 = (inv2 != null) ? inv2.getItemId() : 0;
        final Item inv3 = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-124));
        final int peteqid3 = (inv3 != null) ? inv3.getItemId() : 0;
        for (final MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.write(pet.getUniqueId());
                mplew.writeInt(pet.getPetItemId());
                mplew.writeMapleAsciiString(pet.getName());
                mplew.write(pet.getLevel());
                mplew.writeShort(pet.getCloseness());
                mplew.write(pet.getFullness());
                mplew.writeShort(pet.getFlags());
                mplew.writeInt(peteqid);
            }
        }
        mplew.write(0);
        if (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18)) != null) {
            final int itemid = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18)).getItemId();
            final MapleMount mount = chr.getMount();
            final boolean canwear = MapleItemInformationProvider.getInstance().getReqLevel(itemid) <= chr.getLevel();
            mplew.write(canwear ? 1 : 0);
            mplew.writeInt(mount.getLevel());
            mplew.writeInt(mount.getExp());
            mplew.writeInt(mount.getFatigue());
        }
        else {
            mplew.write(0);
        }
        mplew.write(0);
        chr.getMonsterBook().addCharInfoPacket(chr.getMonsterBookCover(), mplew);
        final Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-26));
        mplew.writeInt((medal == null) ? 0 : medal.getItemId());
        final List<Integer> medalQuests = new ArrayList<Integer>();
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        for (final MapleQuestStatus q : completed) {
            if (q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                medalQuests.add(q.getQuest().getId());
            }
        }
        mplew.writeShort(medalQuests.size());
        for (final int x : medalQuests) {
            mplew.writeShort(x);
        }
        final MapleInventory iv = chr.getInventory(MapleInventoryType.SETUP);
        final List<Item> chairItems = new ArrayList<Item>();
        for (final Item item : iv.list()) {
            if (item.getItemId() >= 3010000 && item.getItemId() <= 3020001) {
                chairItems.add(item);
            }
        }
        mplew.writeInt(chairItems.size());
        for (final Item item : chairItems) {
            mplew.writeInt(item.getItemId());
        }
        final MapleInventory 勋章列表 = chr.getInventory(MapleInventoryType.EQUIP);
        final List<Item> 勋章列表Items = new ArrayList<Item>();
        for (final Item item2 : 勋章列表.list()) {
            if (item2.getItemId() >= 1142000 && item2.getItemId() <= 1142999) {
                勋章列表Items.add(item2);
            }
        }
        mplew.writeInt(勋章列表Items.size());
        for (final Item item2 : 勋章列表Items) {
            mplew.writeInt(item2.getItemId());
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("charInfo-3080：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    private static void writeLongMask(final MaplePacketLittleEndianWriter mplew, final List<Pair<MapleBuffStat, Integer>> statups) {
        if (ServerConstants.调试输出封包) {
            System.err.println("writeLongMask--------------------");
        }
        long firstmask = 0L;
        long secondmask = 0L;
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            }
            else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        mplew.writeLong(firstmask);
        mplew.writeLong(secondmask);
    }
    
    private static void writeLongDiseaseMask(final MaplePacketLittleEndianWriter mplew, final List<Pair<MapleDisease, Integer>> statups) {
        if (ServerConstants.调试输出封包) {
            System.err.println("writeLongDiseaseMask--------------------");
        }
        long firstmask = 0L;
        long secondmask = 0L;
        for (final Pair<MapleDisease, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            }
            else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        mplew.writeLong(firstmask);
        mplew.writeLong(secondmask);
    }
    
    private static void writeLongMaskFromListM(final MaplePacketLittleEndianWriter mplew, final List<MapleBuffStat> statups) {
        if (ServerConstants.调试输出封包) {
            System.err.println("writeLongMaskFromList--------------------");
        }
        long firstmask = 0L;
        long secondmask = 0L;
        mplew.write(0);
        for (final MapleBuffStat statup : statups) {
            if (statup.isFirst()) {
                firstmask |= statup.getValue();
            }
            else {
                secondmask |= statup.getValue();
            }
        }
        mplew.writeLong(firstmask);
        mplew.writeInt(0);
        mplew.writeZeroBytes(3);
    }
    
    private static void writeLongMaskFromList(final MaplePacketLittleEndianWriter mplew, final List<MapleBuffStat> statups) {
        if (ServerConstants.调试输出封包) {
            System.err.println("writeLongMaskFromList--------------------");
        }
        long firstmask = 0L;
        long secondmask = 0L;
        for (final MapleBuffStat statup : statups) {
            if (statup.isFirst()) {
                firstmask |= statup.getValue();
            }
            else {
                secondmask |= statup.getValue();
            }
        }
        mplew.writeLong(firstmask);
        mplew.writeLong(secondmask);
    }
    
    public static MaplePacket giveMount(final MapleCharacter c, final int buffid, final int skillid, final List<Pair<MapleBuffStat, Integer>> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveMount--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.write(0);
        writeLongMask(mplew, statups);
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getRight().shortValue() >= 1000 && statup.getRight().shortValue() != 1002) {
                mplew.writeShort(statup.getRight().shortValue() + c.getGender() * 100);
            }
            else {
                mplew.write(0);
            }
            mplew.writeInt(buffid);
            mplew.writeInt(skillid);
        }
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(2);
        final int a = giveBuff(c, buffid);
        if (a > 0) {
            mplew.write(a);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveMount-3191：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static int giveBuff(final MapleCharacter c, final int buffid) {
        int a = 0;
        switch (buffid) {
            case 1002:
            case 8000:
            case 1121000:
            case 1221000:
            case 1321000:
            case 2121000:
            case 2221000:
            case 2321000:
            case 3121000:
            case 3221000:
            case 4101004:
            case 4121000:
            case 4201003:
            case 4221000:
            case 4341000:
            case 5101007:
            case 5121000:
            case 5221000:
            case 5321005:
            case 9001001:
            case 10001002:
            case 10008000:
            case 14101003:
            case 20001002:
            case 20008000:
            case 20018000:
            case 21121000:
            case 22171000:
            case 23121005:
            case 30008000:
            case 31121004:
            case 32121007:
            case 33121007:
            case 35121007: {
                a = 5;
                break;
            }
            case 32101003: {
                a = 29;
                break;
            }
            case -2022458:
            case 33121006: {
                a = 6;
                break;
            }
            case 5111005:
            case 5121003:
            case 13111005:
            case 15111002: {
                a = 7;
                break;
            }
            case 5301003: {
                a = 3;
                break;
            }
        }
        return a;
    }
    
    public static MaplePacket givePirate(final List<Pair<MapleBuffStat, Integer>> statups, final int duration, final int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("givePirate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeLong(0L);
        mplew.writeLong(MapleBuffStat.MORPH.getValue());
        mplew.writeShort(0);
        mplew.writeInt(skillid);
        mplew.writeZeroBytes(1);
        mplew.writeInt(duration);
        mplew.writeZeroBytes(6);
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket giveForeignPirate(final List<Pair<MapleBuffStat, Integer>> statups, final int duration, final int cid, final int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveForeignPirate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        writeLongMask(mplew, statups);
        mplew.writeShort(0);
        for (final Pair<MapleBuffStat, Integer> stat : statups) {
            if (infusion) {
                mplew.writeInt(0);
            }
            else {
                mplew.writeInt(stat.getRight());
            }
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 7 : 1);
            mplew.writeShort(duration);
        }
        mplew.writeShort(infusion ? 600 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveForeignPirate-3310：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveHoming(final int skillid, final int mobid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveHoming--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeInt(1);
        mplew.writeLong(skillid);
        mplew.write(0);
        mplew.writeInt(mobid);
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveHoming-3333：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveForeignEnergyCharge(final int cid, final int barammount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0L);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeShort(0);
        mplew.writeShort(barammount);
        mplew.writeShort(0);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket giveEnergyChargeTest(final int bar, final int bufflength) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveEnergyChargeTestA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeInt(Math.min(bar, 10000));
        mplew.writeLong(0L);
        mplew.write(0);
        mplew.writeInt((bar >= 10000) ? bufflength : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveEnergyChargeTestA-3357：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket givePirateBuff(final int buffid, final int bufflength, final List<Pair<MapleBuffStat, Integer>> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("givePirateBuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0L);
        mplew.writeShort(0);
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(0);
            mplew.writeInt(buffid);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(bufflength);
        }
        mplew.writeShort(0);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("givePirateBuff-3385：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket 能量条(final List<Pair<MapleBuffStat, Integer>> statups, final int duration) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("能量条--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.write(0);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0L);
        mplew.write(0);
        for (final Pair<MapleBuffStat, Integer> stat : statups) {
            mplew.writeInt(stat.getRight().shortValue());
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(duration);
        }
        mplew.writeShort(0);
        mplew.write(2);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("能量条-3413：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveEnergyChargeTest(final int cid, final int bar, final int bufflength) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveEnergyChargeTestB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0L);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeShort(0);
        mplew.writeInt(Math.min(bar, 10000));
        mplew.writeLong(0L);
        mplew.writeInt((bar >= 10000) ? bufflength : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveEnergyChargeTestB-3439：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveBuff(final int buffid, final int bufflength, final List<Pair<MapleBuffStat, Integer>> statups, final MapleStatEffect effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveBuff--1------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        writeLongMask(mplew, statups);
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeInt(buffid);
            mplew.writeInt(bufflength);
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        if (effect == null || (!effect.isCombo() && !effect.isFinalAttack())) {
            mplew.write(0);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveBuff-3469：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveDebuff(final List<Pair<MapleDisease, Integer>> statups, final int skillid, final int level, final int duration) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveDebuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        writeLongDiseaseMask(mplew, statups);
        for (final Pair<MapleDisease, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
        }
        mplew.writeShort(0);
        mplew.writeShort(900);
        mplew.write(2);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveDebuff-3496：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveForeignDebuff(final int cid, final List<Pair<MapleDisease, Integer>> statups, final int skillid, final int level) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveForeignDebuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        writeLongDiseaseMask(mplew, statups);
        mplew.writeShort(skillid);
        mplew.writeShort(level);
        mplew.writeShort(0);
        mplew.writeShort(900);
        mplew.write(3);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveForeignDebuff-3523：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelForeignDebuff(final int cid, final long mask, final boolean first) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelForeignDebuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(first ? mask : 0L);
        mplew.writeLong(first ? 0L : mask);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelForeignDebuff-3541：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showMonsterRiding(final int cid, final List<Pair<MapleBuffStat, Integer>> statups, final int itemId, final int skillId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showMonsterRiding--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        writeLongMask(mplew, statups);
        mplew.write(0);
        mplew.writeInt(itemId);
        mplew.writeInt(skillId);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showMonsterRiding-3567：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveForeignBuff(final MapleCharacter c, final int cid, final List<Pair<MapleBuffStat, Integer>> statups, final MapleStatEffect effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveForeignBuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        writeLongMask(mplew, statups);
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            if (effect.isMorph() && statup.getRight().intValue() <= 255) {
                mplew.write(statup.getRight().byteValue());
            }
            else if (effect.isPirateMorph()) {
                mplew.writeShort(statup.getRight().shortValue() + c.getGender() * 100);
            }
            else {
                mplew.writeShort(statup.getRight().shortValue());
            }
        }
        mplew.writeShort(0);
        if (effect.isMorph() && !effect.isPirateMorph()) {
            mplew.writeShort(0);
        }
        mplew.write(0);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveForeignBuff-3605：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelForeignBuff(final int cid, final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelForeignBuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        writeLongMaskFromList(mplew, statups);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelForeignBuff-3623：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelForeignBuffMONSTER(final int cid, final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelForeignBuffA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        writeLongMaskFromListM(mplew, statups);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelForeignBuffA-3641：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelForeignBuffMONSTERS(final int cid, final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelForeignBuffA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 01 00"));
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00"));
        mplew.write(3);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelForeignBuffA-3662：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelBuffMONSTER(final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelBuffMONSTER--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        writeLongMaskFromListM(mplew, statups);
        mplew.write(3);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelBuffMONSTER-3686：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelBuffMONSTERS(final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelBuffMONSTERS--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 01 00"));
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00"));
        mplew.write(3);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelBuffMONSTERS-3711：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelBuff(final List<MapleBuffStat> statups) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelBuffA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        writeLongMaskFromList(mplew, statups);
        mplew.write(3);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelBuffA-3735：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelHoming() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelHoming--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0L);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelHoming-3753：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelDebuff(final long mask, final boolean first) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelDebuff--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
        mplew.writeLong(first ? mask : 0L);
        mplew.writeLong(first ? 0L : mask);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelDebuff-3771：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateMount(final MapleCharacter chr, final boolean levelup) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateMount--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateMount-3791：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket mountInfo(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("mountInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("mountInfo-3811：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPlayerShopNewVisitor(final MapleCharacter c, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPlayerShopNewVisitor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("04 0" + slot));
        PacketHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPlayerShopNewVisitor-3830：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPlayerShopRemoveVisitor(final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPlayerShopRemoveVisitor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("0A 0" + slot));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPlayerShopRemoveVisitor-3846：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradePartnerAdd(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradePartnerAdd--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(4);
        mplew.write(1);
        PacketHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradePartnerAdd-3866：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeInvite(final MapleCharacter c, final boolean 现金交易) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeInvite--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(2);
        mplew.write(现金交易 ? 6 : 3);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeInvite-3885：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeMesoSet(final byte number, final int meso) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeMesoSet--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(15);
        mplew.write(number);
        mplew.writeInt(meso);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeMesoSet-3903：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeItemAdd(final byte number, final Item item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeItemAdd--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(14);
        mplew.write(number);
        PacketHelper.addItemInfo(mplew, item, false, false, true);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeItemAdd-3921：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeStart(final MapleClient c, final MapleTrade trade, final byte number, final boolean 现金交易) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeStart--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(5);
        mplew.write(现金交易 ? 6 : 3);
        mplew.write(2);
        mplew.write(number);
        if (number == 1) {
            mplew.write(0);
            PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
        }
        mplew.write(number);
        PacketHelper.addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.write(255);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeStart-3952：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeConfirmation() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeConfirmation--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(16);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeConfirmation-3968：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket TradeMessage(final byte UserSlot, final byte message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("TradeMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10);
        mplew.write(UserSlot);
        mplew.write(message);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("TradeMessage-3991：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getTradeCancel(final byte UserSlot, final int unsuccessful) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getTradeCancel--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10);
        mplew.write(UserSlot);
        mplew.write((unsuccessful == 0) ? 2 : ((unsuccessful == 1) ? 9 : 10));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getTradeCancel-4009：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getNPCTalk(final int npc, final byte msgType, final String talk, final String endBytes, final byte type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getNPCTalk--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(msgType);
        mplew.write(type);
        mplew.writeMapleAsciiString(talk);
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNPCTalk-4030：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getMapSelection(final int npcid, final String sel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getMapSelection--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npcid);
        mplew.writeShort(13);
        mplew.writeInt(0);
        mplew.writeInt(5);
        mplew.writeMapleAsciiString(sel);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getMapSelection-4051：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getNPCTalkStyle(final int npc, final String talk, final int card, final int[] args) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getNPCTalkStyle--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(7);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);
        for (int i = 0; i < args.length; ++i) {
            mplew.writeInt(args[i]);
        }
        mplew.writeInt(card);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNPCTalkStyle-4075：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getNPCTalkNum(final int npc, final String talk, final int def, final int min, final int max) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getNPCTalkNum--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(3);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(def);
        mplew.writeInt(min);
        mplew.writeInt(max);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNPCTalkNum-4098：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getNPCTalkText(final int npc, final String talk) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getNPCTalkText--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(2);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNPCTalkText-4119：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showForeignEffect(final int cid, final int effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showForeignEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(effect);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showForeignEffect-4136：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showBuffeffect(final int cid, final int skillid, final int effectid) {
        if (ServerConstants.调试输出封包) {
            System.err.println("showBuffeffect--------------------");
        }
        return showBuffeffect(cid, skillid, effectid, (byte)3);
    }
    
    public static MaplePacket showBuffeffect(final int cid, final int skillid, final int effectid, final byte direction) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBuffeffectA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(2);
        mplew.write(1);
        if (direction != 3) {
            mplew.write(direction);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showBuffeffectA-4165：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showOwnBuffEffect(final int skillid, final int effectid) {
        if (ServerConstants.调试输出封包) {
            System.err.println("showOwnBuffEffectA--------------------");
        }
        return showOwnBuffEffect(skillid, effectid, (byte)3);
    }
    
    public static MaplePacket showOwnBuffEffect(final int skillid, final int effectid, final byte direction) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showOwnBuffEffectB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(169);
        mplew.write(1);
        if (direction != 3) {
            mplew.write(direction);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showOwnBuffEffectB-4194：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showItemLevelupEffect() {
        if (ServerConstants.调试输出封包) {
            System.err.println("showItemLevelupEffect--------------------");
        }
        return showSpecialEffect(17);
    }
    
    public static MaplePacket showMonsterBookPickup() {
        return showSpecialEffect(14);
    }
    
    public static MaplePacket showEquipmentLevelUp() {
        return showSpecialEffect(17);
    }
    
    public static MaplePacket showItemLevelup() {
        return showSpecialEffect(17);
    }
    
    public static MaplePacket showForeignItemLevelupEffect(final int cid) {
        if (ServerConstants.调试输出封包) {
            System.err.println("showForeignItemLevelupEffect--------------------");
        }
        return showSpecialEffect(cid, 17);
    }
    
    public static MaplePacket showSpecialEffect(final int effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showSpecialEffectA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effect);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showSpecialEffectA-4236：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showSpecialEffect(final int cid, final int effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showSpecialEffectB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(effect);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showSpecialEffectB-4253：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateSkill(final int skillid, final int level, final int masterlevel, final long expiration) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateSkill--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
        mplew.write(1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateSkill-4275：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket updateQuestMobKills(final MapleQuestStatus status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateQuestMobKills--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(status.getQuest().getId());
        mplew.write(1);
        final StringBuilder sb = new StringBuilder();
        for (final int kills : status.getMobKills().values()) {
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
        }
        mplew.writeMapleAsciiString(sb.toString());
        mplew.writeZeroBytes(8);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateQuestMobKills-4300：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket 游戏屏幕中间黄色字体(final String status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("游戏屏幕中间黄色字体--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(4761);
        mplew.write(1);
        mplew.writeMapleAsciiString(status);
        mplew.writeZeroBytes(8);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("游戏屏幕中间黄色字体-4325：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket 游戏屏幕中间黄色字体(final String status, final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("游戏屏幕中间黄色字体--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(id);
        mplew.write(1);
        mplew.writeMapleAsciiString(status);
        mplew.writeZeroBytes(8);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("游戏屏幕中间黄色字体-4350：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getShowQuestCompletion(final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowQuestCompletion--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_QUEST_COMPLETION.getValue());
        mplew.writeShort(id);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getShowQuestCompletion-4366：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getKeymap(final MapleKeyLayout layout) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getKeymap--------------------");
        }
        mplew.writeShort(SendPacketOpcode.KEYMAP.getValue());
        mplew.write(0);
        layout.writeData(mplew);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getKeymap4384：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getWhisper(final String sender, final int channel, final String text) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getWhisper--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getWhisper-4403：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getWhisperReply(final String target, final byte reply) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getWhisperReply--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(10);
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getWhisperReply-4421：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getFindReplyWithMap(final String target, final int mapid, final boolean buddy) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFindReplyWithMap--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.writeZeroBytes(8);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFindReplyWithMap-4441：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getFindReply(final String target, final int channel, final boolean buddy) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFindReply--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFindReply-4460：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getInventoryFull() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getInventoryFull--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getInventoryFull-4477：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getShowInventoryFull() {
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowInventoryFull--------------------");
        }
        return getShowInventoryStatus(255);
    }
    
    public static MaplePacket showItemUnavailable() {
        if (ServerConstants.调试输出封包) {
            System.err.println("showItemUnavailable--------------------");
        }
        return getShowInventoryStatus(254);
    }
    
    public static MaplePacket getShowInventoryStatus(final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getShowInventoryStatus--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0);
        mplew.write(mode);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getShowInventoryStatus-4510：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getStorage(final int npcId, final byte slots, final Collection<Item> items, final int meso) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getStorage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(22);
        mplew.writeInt(npcId);
        mplew.write(slots);
        mplew.writeShort(126);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        mplew.write((byte)items.size());
        for (final Item item : items) {
            if (GameConstants.is豆豆装备(item.getItemId())) {
                PacketHelper.addDDItemInfo(mplew, item, true, true, false);
            }
            else {
                PacketHelper.addItemInfo(mplew, item, true, true);
            }
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getStorage-4544：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getStorageFull() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getStorageFull--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(17);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getStorageFull-4561：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket mesoStorage(final byte slots, final int meso) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("mesoStorage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(19);
        mplew.write(slots);
        mplew.writeShort(2);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("mesoStorage-4582：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket storeStorage(final byte slots, final MapleInventoryType type, final Collection<Item> items) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("storeStorage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(13);
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (final Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("storeStorage-4605：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket takeOutStorage(final byte slots, final MapleInventoryType type, final Collection<Item> items) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("takeOutStorage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(9);
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (final Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("takeOutStorage-4628：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket fairyPendantMessage(final int type, final int percent) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("fairyPendantMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FAIRY_PEND_MSG.getValue());
        mplew.writeShort(21);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(percent);
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("fairyPendantMessage-4648：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveFameResponse(final int mode, final String charname, final int newfame) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveFameResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeShort(newfame);
        mplew.writeShort(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveFameResponse-4668：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveFameErrorResponse(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("giveFameErrorResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(status);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("giveFameErrorResponse-4691：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket receiveFame(final int mode, final String charnameFrom) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("receiveFame--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("receiveFame-4709：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket partyCreated(final int partyid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("partyCreated--开启组队");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(8);
        mplew.writeInt(partyid);
        mplew.write(MaplePacketCreator.CHAR_INFO_MAGIC);
        mplew.write(MaplePacketCreator.CHAR_INFO_MAGIC);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("partyCreated-4729：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket partyInvite(final MapleCharacter from) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("partyInvite--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(4);
        mplew.writeInt(from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("partyInvite-4750：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket partyStatusMessage(final int message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("partyStatusMessageA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(message);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("partyStatusMessageA-4772：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket partyStatusMessage(final int message, final String charname) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("partyStatusMessageB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(message);
        mplew.writeMapleAsciiString(charname);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("partyStatusMessageB-4789：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    private static void addPartyStatus(final int forchannel, final MapleParty party, final LittleEndianWriter lew, final boolean leaving) {
        if (ServerConstants.调试输出封包) {
            System.err.println("addPartyStatus--------------------");
        }
        final List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(StringUtil.getRightPaddedStr(partychar.getName(), '\0', 13));
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            }
            else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (final MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            }
            else {
                lew.writeInt(0);
            }
        }
        for (final MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            }
            else {
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
            }
        }
    }
    
    public static MaplePacket updateParty(final int forChannel, final MapleParty party, final PartyOperation op, final MaplePartyCharacter target) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateParty--退出组队");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE: {
                mplew.write(12);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());
                mplew.write((op != PartyOperation.DISBAND) ? 1 : 0);
                if (op == PartyOperation.DISBAND) {
                    mplew.writeInt(target.getId());
                    break;
                }
                mplew.write((op == PartyOperation.EXPEL) ? 1 : 0);
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            }
            case JOIN: {
                mplew.write(15);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            }
            case SILENT_UPDATE:
            case LOG_ONOFF: {
                mplew.write(7);
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            }
            case CHANGE_LEADER: {
                mplew.write(26);
                mplew.writeInt(target.getId());
                mplew.write(0);
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateParty-4897：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket partyPortal(final int townId, final int targetId, final int skillId, final Point position) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("partyPortal--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.writeShort(35);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writePos(position);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("partyPortal-4917：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updatePartyMemberHP(final int cid, final int curhp, final int maxhp) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updatePartyMemberHP--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updatePartyMemberHP-4935：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket multiChat(final String name, final String chattext, final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("multiChat--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("multiChat-4953：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getClock(final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getClock--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2);
        mplew.writeInt(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getClock-4970：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getClockTime(final int hour, final int min, final int sec) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getClockTime--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getClockTime-4989：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnMist(final MapleMist mist) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnMist--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
        mplew.writeInt(mist.getObjectId());
        mplew.writeInt(mist.isMobMist() ? 0 : ((mist.isPoisonMist() != 0) ? 1 : 2));
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        }
        else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeInt(mist.getBox().x);
        mplew.writeInt(mist.getBox().y);
        mplew.writeInt(mist.getBox().x + mist.getBox().width);
        mplew.writeInt(mist.getBox().y + mist.getBox().height);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnMist-5019：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeMist(final int oid, final boolean eruption) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeMist--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
        mplew.writeInt(oid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeMist-5036：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket damageSummon(final int cid, final int summonSkillId, final int damage, final int unkByte, final int monsterIdFrom) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("damageSummon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(unkByte);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("damageSummon-5057：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket buddylistMessage(final byte message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("buddylistMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(message);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("buddylistMessage-5073：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAriantScore(final List<MapleCharacter> players) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(players.isEmpty() ? 0 : 1);
        if (!players.isEmpty()) {
            for (final MapleCharacter i : players) {
                mplew.writeMapleAsciiString(i.getName());
                mplew.writeInt(i.getAriantScore());
            }
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAriantScore(final String name, final int score, final boolean empty) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(score);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateBuddylist(final Collection<BuddyEntry> buddylist) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateBuddylist--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(7);
        mplew.write(buddylist.size());
        for (final BuddyEntry buddy : buddylist) {
            if (buddy.isVisible()) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\0', 13));
                mplew.write(0);
                mplew.writeInt((buddy.getChannel() == -1) ? -1 : (buddy.getChannel() - 1));
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getGroup(), '\0', 17));
            }
        }
        for (int x = 0; x < buddylist.size(); ++x) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket requestBuddylistAdd(final int cidFrom, final String nameFrom, final int levelFrom, final int jobFrom) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("requestBuddylistAdd--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\0', 13));
        mplew.write(1);
        mplew.write(5);
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr("群未定", '\0', 17));
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("requestBuddylistAdd-5130：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateBuddyChannel(final int characterid, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateBuddyChannel--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(20);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateBuddyChannel-5149：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket itemEffect(final int characterid, final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("itemEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("itemEffect-5166：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket itemEffects(final int characterid, final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("itemEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("itemEffect-5183：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateBuddyCapacity(final int capacity) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateBuddyCapacity--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(21);
        mplew.write(capacity);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateBuddyCapacity-5200：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showChair(final int characterid, final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showChair--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showChair-5217：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelChair(final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("cancelChair--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
        if (id == -1) {
            mplew.write(0);
        }
        else {
            mplew.write(1);
            mplew.writeShort(id);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("cancelChair-5237：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnReactor(final MapleReactor reactor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnReactor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());
        mplew.write(reactor.getFacingDirection());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnReactor-5258：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket triggerReactor(final MapleReactor reactor, final int stance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("triggerReactor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());
        mplew.writeInt(stance);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("triggerReactor-5279：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket destroyReactor(final MapleReactor reactor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("destroyReactor--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("destroyReactor-5297：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket musicChange(final String song) {
        if (ServerConstants.调试输出封包) {
            System.err.println("musicChange--------------------");
        }
        return environmentChange(song, 6);
    }
    
    public static MaplePacket showEffect(final String effect) {
        if (ServerConstants.调试输出封包) {
            System.err.println("showEffect--------------------");
        }
        return environmentChange(effect, 3);
    }
    
    public static MaplePacket playSound(final String sound) {
        if (ServerConstants.调试输出封包) {
            System.err.println("playSound--------------------");
        }
        return environmentChange(sound, 4);
    }
    
    public static MaplePacket environmentChange(final String env, final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {}
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("environmentChange-5335：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket environmentMove(final String env, final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("environmentMove--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_ENV.getValue());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("environmentMove-5352：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket startMapEffect(final String msg, final int itemid, final boolean active) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("startMapEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("startMapEffect-5372：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeMapEffect() {
        if (ServerConstants.调试输出封包) {
            System.err.println("removeMapEffect--------------------");
        }
        return startMapEffect(null, 0, false);
    }
    
    public static MaplePacket fuckGuildInfo(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("fuckGuildInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(26);
        String Prefix = "";
        if (c.getPrefix() == 1) {
            Prefix = "[技術團隊成員]";
        }
        if (c.getPrefix() == 2) {
            Prefix = "[遊戲管理成員]";
        }
        if (c.getPrefix() == 3) {
            Prefix = "[活動辦理成員]";
        }
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(Prefix);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("fuckGuildInfo-5427：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showGuildInfo(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showGuildInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(26);
        if (c == null || c.getMGC() == null) {
            mplew.write(0);
            if (ServerConstants.get38记录()) {
                final ServerConstants ERROR = new ServerConstants();
                ERROR.setPACKET_ERROR("showGuildInfo-5445：\r\n" + mplew.getPacket() + "\r\n\r\n");
            }
            return mplew.getPacket();
        }
        final MapleGuild g = Guild.getGuild(c.getGuildId());
        if (g == null) {
            mplew.write(0);
            if (ServerConstants.get38记录()) {
                final ServerConstants ERROR2 = new ServerConstants();
                ERROR2.setPACKET_ERROR("showGuildInfo-5454：\r\n" + mplew.getPacket() + "\r\n\r\n");
            }
            return mplew.getPacket();
        }
        final MapleGuildCharacter mgc = g.getMGC(c.getId());
        c.setGuildRank(mgc.getGuildRank());
        mplew.write(1);
        getGuildInfo(mplew, g);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR2 = new ServerConstants();
            ERROR2.setPACKET_ERROR("showGuildInfo-5465：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    private static void getGuildInfo(final MaplePacketLittleEndianWriter mplew, final MapleGuild guild) {
        if (ServerConstants.调试输出封包) {
            System.err.println("getGuildInfo--------------------");
        }
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; ++i) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt((guild.getAllianceId() > 0) ? guild.getAllianceId() : 0);
    }
    
    private static void getGuildInfo2(final MaplePacketLittleEndianWriter mplew, final MapleGuild guild, final MapleCharacter chr) {
        if (ServerConstants.调试输出封包) {
            System.err.println("getGuildInfo2--------------------");
        }
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; ++i) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt((guild.getAllianceId() > 0) ? guild.getAllianceId() : 0);
    }
    
    public static MaplePacket guildMemberOnline(final int gid, final int cid, final boolean bOnline) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildMemberOnline--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(61);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildMemberOnline-5528：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildInvite(final int gid, final String charName, final int levelFrom, final int jobFrom) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildInvite--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(5);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildInvite-5548：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket denyGuildInvitation(final String charname) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("denyGuildInvitation--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(55);
        mplew.writeMapleAsciiString(charname);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("denyGuildInvitation-5565：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket genericGuildMessage(final byte code) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("genericGuildMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(code);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("genericGuildMessage-5581：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket newGuildMember(final MapleGuildCharacter mgc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("newGuildMember--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(39);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank());
        mplew.writeInt(mgc.isOnline() ? 1 : 0);
        mplew.writeInt(1);
        mplew.writeInt(mgc.getAllianceRank());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("newGuildMember-5607：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket memberLeft(final MapleGuildCharacter mgc, final boolean bExpelled) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("memberLeft--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(bExpelled ? 47 : 44);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("memberLeft-5628：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeRank(final MapleGuildCharacter mgc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("changeRank--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(64);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeRank-5647：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildNotice(final int gid, final String notice) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildNotice--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(68);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildNotice-5665：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildMemberLevelJobUpdate(final MapleGuildCharacter mgc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildMemberLevelJobUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(60);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildMemberLevelJobUpdate-5685：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket rankTitleChange(final int gid, final String[] ranks) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("rankTitleChange--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(62);
        mplew.writeInt(gid);
        for (final String r : ranks) {
            mplew.writeMapleAsciiString(r);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("rankTitleChange-5705：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildDisband(final int gid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildDisband--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(50);
        mplew.writeInt(gid);
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildDisband-5723：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildEmblemChange(final int gid, final short bg, final byte bgcolor, final short logo, final byte logocolor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildEmblemChange--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(66);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildEmblemChange-5744：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket guildCapacityChange(final int gid, final int capacity) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("guildCapacityChange--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(58);
        mplew.writeInt(gid);
        mplew.write(capacity);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("guildCapacityChange-5762：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeGuildFromAlliance(final MapleGuildAlliance alliance, final MapleGuild expelledGuild, final boolean expelled) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeGuildFromAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(16);
        addAllianceInfo(mplew, alliance);
        getGuildInfo(mplew, expelledGuild);
        mplew.write(expelled ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeGuildFromAlliance-5780：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeAlliance(final MapleGuildAlliance alliance, final boolean in) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("changeAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(1);
        mplew.write(in ? 1 : 0);
        mplew.writeInt(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        final MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; ++i) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        mplew.write(noGuilds);
        for (int i = 0; i < noGuilds; ++i) {
            mplew.writeInt(g[i].getId());
            final Collection<MapleGuildCharacter> members = g[i].getMembers();
            mplew.writeInt(members.size());
            for (final MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write((byte)(in ? mgc.getAllianceRank() : 0));
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeAlliance-5816：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeAllianceLeader(final int allianceid, final int newLeader, final int oldLeader) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("changeAllianceLeaderA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(2);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeAllianceLeaderA-5834：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAllianceLeader(final int allianceid, final int newLeader, final int oldLeader) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateAllianceLeaderB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(25);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateAllianceLeaderB-5852：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendAllianceInvite(final String allianceName, final MapleCharacter inviter) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendAllianceInvite--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(3);
        mplew.writeInt(inviter.getGuildId());
        mplew.writeMapleAsciiString(inviter.getName());
        mplew.writeMapleAsciiString(allianceName);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendAllianceInvite-5871：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeGuildInAlliance(final MapleGuildAlliance alliance, final MapleGuild guild, final boolean add) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("changeGuildInAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(4);
        mplew.writeInt(add ? alliance.getId() : 0);
        mplew.writeInt(guild.getId());
        final Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.writeInt(members.size());
        for (final MapleGuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write((byte)(add ? mgc.getAllianceRank() : 0));
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeGuildInAlliance-5894：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeAllianceRank(final int allianceid, final MapleGuildCharacter player) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("changeAllianceRank--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(5);
        mplew.writeInt(allianceid);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getAllianceRank());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeAllianceRank-5912：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket createGuildAlliance(final MapleGuildAlliance alliance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("createGuildAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(15);
        addAllianceInfo(mplew, alliance);
        final int noGuilds = alliance.getNoGuilds();
        final MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); ++i) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        for (final MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("createGuildAlliance-5939：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getAllianceInfo(final MapleGuildAlliance alliance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getAllianceInfo--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(12);
        mplew.write((alliance != null) ? 1 : 0);
        if (alliance != null) {
            addAllianceInfo(mplew, alliance);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getAllianceInfo-5958：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getAllianceUpdate(final MapleGuildAlliance alliance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getAllianceUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(23);
        addAllianceInfo(mplew, alliance);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getAllianceUpdate-5973：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getGuildAlliance(final MapleGuildAlliance alliance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getGuildAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(13);
        if (alliance == null) {
            mplew.writeInt(0);
            if (ServerConstants.get38记录()) {
                final ServerConstants ERROR = new ServerConstants();
                ERROR.setPACKET_ERROR("getGuildAlliance-5991：\r\n" + mplew.getPacket() + "\r\n\r\n");
            }
            return mplew.getPacket();
        }
        final int noGuilds = alliance.getNoGuilds();
        final MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); ++i) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        mplew.writeInt(noGuilds);
        for (final MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR2 = new ServerConstants();
            ERROR2.setPACKET_ERROR("getGuildAlliance-6009：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket addGuildToAlliance(final MapleGuildAlliance alliance, final MapleGuild newGuild) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("addGuildToAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(18);
        addAllianceInfo(mplew, alliance);
        mplew.writeInt(newGuild.getId());
        getGuildInfo(mplew, newGuild);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("addGuildToAlliance-6028：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    private static void addAllianceInfo(final MaplePacketLittleEndianWriter mplew, final MapleGuildAlliance alliance) {
        if (ServerConstants.调试输出封包) {
            System.err.println("addAllianceInfo--------------------");
        }
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; ++i) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
        }
        mplew.write(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); ++i) {
            mplew.writeInt(alliance.getGuildId(i));
        }
        mplew.writeInt(alliance.getCapacity());
        mplew.writeMapleAsciiString(alliance.getNotice());
    }
    
    public static MaplePacket allianceMemberOnline(final int alliance, final int gid, final int id, final boolean online) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("allianceMemberOnline--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(14);
        mplew.writeInt(alliance);
        mplew.writeInt(gid);
        mplew.writeInt(id);
        mplew.write(online ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("allianceMemberOnline-6065：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAlliance(final MapleGuildCharacter mgc, final int allianceid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(24);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateAlliance-6086：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAllianceRank(final int allianceid, final MapleGuildCharacter mgc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateAllianceRank--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(27);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getAllianceRank());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateAllianceRank-6105：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket disbandAlliance(final int alliance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("disbandAlliance--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(29);
        mplew.writeInt(alliance);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("disbandAlliance-6122：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("BBSThreadList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(6);
        if (bbs == null) {
            mplew.write(0);
            mplew.writeLong(0L);
            if (ServerConstants.get38记录()) {
                final ServerConstants ERROR = new ServerConstants();
                ERROR.setPACKET_ERROR("BBSThreadList-6141：\r\n" + mplew.getPacket() + "\r\n\r\n");
            }
            return mplew.getPacket();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (final MapleBBSThread b : bbs) {
            if (b.isNotice()) {
                notice = b;
                break;
            }
        }
        final int ret = (notice != null) ? 1 : 0;
        mplew.write(ret);
        if (notice != null) {
            addThread(mplew, notice);
            --threadCount;
        }
        if (threadCount < start) {
            start = 0;
        }
        mplew.writeInt(threadCount);
        final int pages = Math.min(10, threadCount - start);
        mplew.writeInt(pages);
        for (int i = 0; i < pages; ++i) {
            addThread(mplew, bbs.get(start + i + ret));
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR2 = new ServerConstants();
            ERROR2.setPACKET_ERROR("BBSThreadList-6173：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    private static void addThread(final MaplePacketLittleEndianWriter mplew, final MapleBBSThread rs) {
        if (ServerConstants.调试输出封包) {
            System.err.println("addThread--------------------");
        }
        mplew.writeInt(rs.localthreadID);
        mplew.writeInt(rs.ownerID);
        mplew.writeMapleAsciiString(rs.name);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
        mplew.writeInt(rs.icon);
        mplew.writeInt(rs.getReplyCount());
    }
    
    public static MaplePacket showThread(final MapleBBSThread thread) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showThread--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(7);
        mplew.writeInt(thread.localthreadID);
        mplew.writeInt(thread.ownerID);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
        mplew.writeMapleAsciiString(thread.name);
        mplew.writeMapleAsciiString(thread.text);
        mplew.writeInt(thread.icon);
        mplew.writeInt(thread.getReplyCount());
        for (final MapleBBSReply reply : thread.replies.values()) {
            mplew.writeInt(reply.replyid);
            mplew.writeInt(reply.ownerID);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
            mplew.writeMapleAsciiString(reply.content);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showThread-6214：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showGuildRanks(final int npcid, final List<GuildRankingInfo> all) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showGuildRanks--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        mplew.writeInt(all.size());
        for (final GuildRankingInfo info : all) {
            mplew.writeMapleAsciiString(info.getName());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getLogo());
            mplew.writeInt(info.getLogoColor());
            mplew.writeInt(info.getLogoBg());
            mplew.writeInt(info.getLogoBgColor());
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showGuildRanks-6241：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateGP(final int gid, final int GP) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateGP--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(72);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateGP-6259：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket skillEffect(final MapleCharacter from, final int skillId, final byte level, final byte flags, final byte speed, final byte unk) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("skillEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.write(flags);
        mplew.write(speed);
        mplew.write(unk);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("skillEffect-6280：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket skillCancel(final MapleCharacter from, final int skillId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("skillCancel--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("skillCancel-6297：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showMagnet(final int mobid, final byte success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showMagnet--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
        mplew.writeInt(mobid);
        mplew.write(success);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showMagnet-6314：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendHint(final String hint, int width, int height) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendHint--------------------");
        }
        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendHint-6342：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket messengerInvite(final String from, final int messengerid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("messengerInvite--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(5);
        mplew.writeInt(messengerid);
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("messengerInvite-6362：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket addMessengerPlayer(final String from, final MapleCharacter chr, final int position, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("addMessengerPlayer--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("addMessengerPlayer-6382：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeMessengerPlayer(final int position) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeMessengerPlayer--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(2);
        mplew.write(position);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeMessengerPlayer-6399：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateMessengerPlayer(final String from, final MapleCharacter chr, final int position, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateMessengerPlayer--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(7);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateMessengerPlayer-6419：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket joinMessenger(final int position) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("joinMessenger--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(1);
        mplew.write(position);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("joinMessenger-6436：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket messengerChat(final String text) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("messengerChat--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(6);
        mplew.writeMapleAsciiString(text);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("messengerChat-6453：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket messengerNote(final String text, final int mode, final int mode2) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("messengerNote--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("messengerNote-6471：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getFindReplyWithCS(final String target, final boolean buddy) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFindReplyWithCS--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFindReplyWithCS-6490：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getFindReplyWithMTS(final String target, final boolean buddy) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFindReplyWithMTS--------------------");
        }
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(0);
        mplew.writeInt(-1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFindReplyWithMTS-6509：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showEquipEffect() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showEquipEffectA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showEquipEffectA-6524：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showEquipEffect(final int team) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showEquipEffectB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        mplew.writeShort(team);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showEquipEffectB-6539：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket summonSkill(final int cid, final int summonSkillId, final int newStance) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("summonSkill--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("summonSkill-6557：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket skillCooldown(final int sid, final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("skillCooldown--------------------");
        }
        mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
        mplew.writeInt(sid);
        mplew.writeShort(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("skillCooldown-6574：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket useSkillBook(final MapleCharacter chr, final int skillid, final int maxlevel, final boolean canuse, final boolean success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("useSkillBook--------------------");
        }
        mplew.writeShort(SendPacketOpcode.USE_SKILL_BOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("useSkillBook-6596：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getMacros(final SkillMacro[] macros) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getMacros--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
        int count = 0;
        for (int i = 0; i < 5; ++i) {
            if (macros[i] != null) {
                ++count;
            }
        }
        mplew.write(count);
        for (int i = 0; i < 5; ++i) {
            final SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getMacros-6627：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateAriantPQRanking(final String name, final int score, final boolean empty) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateAriantPQRanking--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ARIANT_PQ_START.getValue());
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(score);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateAriantPQRanking-6646：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket catchMonster(final int mobid, final int itemid, final byte success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("catchMonster--------------------");
        }
        if (itemid == 2270002) {
            mplew.writeShort(SendPacketOpcode.CATCH_ARIANT.getValue());
        }
        else {
            mplew.writeShort(SendPacketOpcode.CATCH_MONSTER.getValue());
        }
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("catchMonster6668：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showAriantScoreBoard() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showAriantScoreBoard--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ARIANT_SCOREBOARD.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showAriantScoreBoard-6687：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket boatPacket(final boolean type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("boatPacket--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOAT_EFFECT.getValue());
        mplew.writeShort(type ? 1 : 2);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("boatPacket-6702：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket boatPacket(final int effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("boatPacket--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOAT_EFFECT.getValue());
        mplew.writeShort(effect);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("boatPacket-6720：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket boatEffect(final int effect) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("boatEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOAT_EFF.getValue());
        mplew.writeShort(effect);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("boatEffect-6738：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeItemFromDuey(final boolean remove, final int Package) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeItemFromDuey--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(23);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeItemFromDuey-6756：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendDuey(final byte operation, final List<MapleDueyActions> packages) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendDuey--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(operation);
        switch (operation) {
            case 8: {
                mplew.write(1);
                break;
            }
            case 9: {
                mplew.write(0);
                mplew.write(packages.size());
                for (final MapleDueyActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 15);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(KoreanDateUtil.getFileTimestamp(dp.getSentTime(), false));
                    mplew.writeZeroBytes(205);
                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem(), true, true);
                    }
                    else {
                        mplew.write(0);
                    }
                }
                mplew.write(0);
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendDuey-6081：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket Mulung_DojoUp2() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("Mulung_DojoUp2--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(7);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("Mulung_DojoUp2-6817：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket dojoWarpUp() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("dojoWarpUp--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DOJO_WARP_UP.getValue());
        mplew.write(0);
        mplew.write(6);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("dojoWarpUp-6832：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showQuestMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showQuestMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(msg);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showQuestMsg-6847：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket HSText(final String m) {
        if (ServerConstants.调试输出封包) {
            System.err.println("Mulung_Pts--------------------");
        }
        return showQuestMsg(m);
    }
    
    public static MaplePacket Mulung_Pts(final int recv, final int total) {
        if (ServerConstants.调试输出封包) {
            System.err.println("Mulung_Pts--------------------");
        }
        return showQuestMsg("你獲得 " + recv + " 修煉點數, 目前累積了 " + total + " 點修煉點數");
    }
    
    public static MaplePacket showOXQuiz(final int questionSet, final int questionId, final boolean askQuestion) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showOXQuiz--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showOXQuiz-6877：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket leftKnockBack() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("leftKnockBack--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LEFT_KNOCK_BACK.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("leftKnockBack-6890：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket rollSnowball(final int type, final MapleSnowballs ball1, final MapleSnowballs ball2) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("rollSnowball--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ROLL_SNOWBALL.getValue());
        mplew.write(type);
        mplew.writeInt((ball1 == null) ? 0 : (ball1.getSnowmanHP() / 75));
        mplew.writeInt((ball2 == null) ? 0 : (ball2.getSnowmanHP() / 75));
        mplew.writeShort((ball1 == null) ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort((ball2 == null) ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("rollSnowball-6910：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket enterSnowBall() {
        if (ServerConstants.调试输出封包) {
            System.err.println("enterSnowBall--------------------");
        }
        return rollSnowball(0, null, null);
    }
    
    public static MaplePacket hitSnowBall(final int team, final int damage, final int distance, final int delay) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("hitSnowBall--------------------");
        }
        mplew.writeShort(SendPacketOpcode.HIT_SNOWBALL.getValue());
        mplew.write(team);
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("hitSnowBall-6934：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket snowballMessage(final int team, final int message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("snowballMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SNOWBALL_MESSAGE.getValue());
        mplew.write(team);
        mplew.writeInt(message);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("snowballMessage6949：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket finishedSort(final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("finishedSort--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FINISH_SORT.getValue());
        mplew.write(1);
        mplew.write(type);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("finishedSort-6964：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket coconutScore(final int[] coconutscore) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("coconutScore--------------------");
        }
        mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("coconutScore-6980：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket hitCoconut(final boolean spawn, final int id, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("hitCoconut--------------------");
        }
        mplew.writeShort(SendPacketOpcode.HIT_COCONUT.getValue());
        if (spawn) {
            mplew.write(0);
            mplew.writeInt(128);
        }
        else {
            mplew.writeInt(id);
            mplew.write(type);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("hitCoconut-7001：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket finishedGather(final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("finishedGather--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FINISH_GATHER.getValue());
        mplew.write(1);
        mplew.write(type);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("finishedGather-7016：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket yellowChat(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("yellowChat--------------------");
        }
        mplew.writeShort(SendPacketOpcode.YELLOW_CHAT.getValue());
        mplew.write(-1);
        mplew.writeMapleAsciiString(msg);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("yellowChat-7031：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPeanutResult(final int itemId, final short quantity, final int itemId2, final short quantity2) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPeanutResult--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PIGMI_REWARD.getValue());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(5060003);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPeanutResult-7051：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendLevelup(final boolean family, final int level, final String name) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendLevelup--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LEVEL_UPDATE.getValue());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendLevelup-7069：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendMarriage(final boolean family, final String name) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendMarriage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MARRIAGE_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendMarriage-7086：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendJobup(final boolean family, final int jobid, final String name) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendJobup--------------------");
        }
        mplew.writeShort(SendPacketOpcode.JOB_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid);
        mplew.writeMapleAsciiString(name);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendJobup-7104：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showZakumShrine(final boolean spawned, final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showZakumShrine--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showZakumShrine-7119：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showHorntailShrine(final boolean spawned, final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showHorntailShrine--------------------");
        }
        mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showHorntailShrine-7134：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showChaosZakumShrine(final boolean spawned, final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showChaosZakumShrine--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showChaosZakumShrine-7149：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showChaosHorntailShrine(final boolean spawned, final int time) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showChaosHorntailShrine--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAOS_HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showChaosHorntailShrine-7164：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket stopClock() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("stopClock--------------------");
        }
        mplew.writeShort(SendPacketOpcode.STOP_CLOCK.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("stopClock-7177：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnDragon(final MapleDragon d) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnDragon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DRAGON_SPAWN.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnDragon-7196：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeDragon(final int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeDragon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DRAGON_REMOVE.getValue());
        mplew.writeInt(chrid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeDragon-7210：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveDragon(final MapleDragon d, final Point startPos, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveDragon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveDragon-7230：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket addTutorialStats() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(0);
        if (ServerConstants.调试输出封包) {
            System.err.println("addTutorialStats--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TEMP_STATS.getValue());
        mplew.writeInt(3871);
        mplew.writeShort(999);
        mplew.writeShort(999);
        mplew.writeShort(999);
        mplew.writeShort(999);
        mplew.writeShort(255);
        mplew.writeShort(999);
        mplew.writeShort(999);
        mplew.write(120);
        mplew.write(140);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("addTutorialStats-7253：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket temporaryStats_Aran() {
        if (ServerConstants.调试输出封包) {
            System.err.println("temporaryStats_Aran--------------------");
        }
        final List<Pair<Temp, Integer>> stats = new ArrayList<Pair<Temp, Integer>>();
        stats.add(new Pair<Temp, Integer>(Temp.STR, 999));
        stats.add(new Pair<Temp, Integer>(Temp.DEX, 999));
        stats.add(new Pair<Temp, Integer>(Temp.INT, 999));
        stats.add(new Pair<Temp, Integer>(Temp.LUK, 999));
        stats.add(new Pair<Temp, Integer>(Temp.WATK, 255));
        stats.add(new Pair<Temp, Integer>(Temp.ACC, 999));
        stats.add(new Pair<Temp, Integer>(Temp.AVOID, 999));
        stats.add(new Pair<Temp, Integer>(Temp.SPEED, 140));
        stats.add(new Pair<Temp, Integer>(Temp.JUMP, 120));
        return temporaryStats(stats);
    }
    
    public static final MaplePacket temporaryStats_Balrog(final MapleCharacter chr) {
        if (ServerConstants.调试输出封包) {
            System.err.println("temporaryStats_Balrog--------------------");
        }
        final List<Pair<Temp, Integer>> stats = new ArrayList<Pair<Temp, Integer>>();
        final int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.add(new Pair<Temp, Integer>(Temp.STR, chr.getStat().getTotalStr() / offset));
        stats.add(new Pair<Temp, Integer>(Temp.DEX, chr.getStat().getTotalDex() / offset));
        stats.add(new Pair<Temp, Integer>(Temp.INT, chr.getStat().getTotalInt() / offset));
        stats.add(new Pair<Temp, Integer>(Temp.LUK, chr.getStat().getTotalLuk() / offset));
        stats.add(new Pair<Temp, Integer>(Temp.WATK, chr.getStat().getTotalWatk() / offset));
        stats.add(new Pair<Temp, Integer>(Temp.MATK, chr.getStat().getTotalMagic() / offset));
        return temporaryStats(stats);
    }
    
    public static final MaplePacket temporaryStats(final List<Pair<Temp, Integer>> stats) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("temporaryStats--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TEMP_STATS.getValue());
        int updateMask = 0;
        for (final Pair<Temp, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        final List<Pair<Temp, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<Temp, Integer>>() {
                @Override
                public int compare(final Pair<Temp, Integer> o1, final Pair<Temp, Integer> o2) {
                    final int val1 = o1.getLeft().getValue();
                    final int val2 = o2.getLeft().getValue();
                    return (val1 < val2) ? -1 : ((val1 == val2) ? 0 : 1);
                }
            });
        }
        mplew.writeInt(updateMask);
        for (final Pair<Temp, Integer> statupdate2 : mystats) {
            final Integer value = statupdate2.getLeft().getValue();
            if (value >= 1) {
                if (value <= 512) {
                    mplew.writeShort(statupdate2.getRight().shortValue());
                }
                else {
                    mplew.write(statupdate2.getRight().byteValue());
                }
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("temporaryStats-7335：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket temporaryStats_Reset() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("temporaryStats_Reset--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TEMP_STATS_RESET.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("temporaryStats_Reset-7348：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket showHpHealed(final int cid, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showHpHealed--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(6);
        mplew.writeInt(amount);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showHpHealed-7365：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket showOwnHpHealed(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showOwnHpHealed--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(6);
        mplew.writeInt(amount);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showOwnHpHealed-7380：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendRepairWindow(final int npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendRepairWindow--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REPAIR_WINDOW.getValue());
        mplew.writeInt(34);
        mplew.writeInt(npc);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendRepairWindow-7395：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendPyramidUpdate(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendPyramidUpdate--------------------");
        }
        final int v2 = 0;
        final int v3 = 0;
        mplew.writeShort(SendPacketOpcode.PYRAMID_UPDATE.getValue());
        mplew.writeInt(amount);
        if (amount > 0) {
            mplew.writeInt(v2);
            mplew.write(v3);
            if (v3 != v2) {
                mplew.writeMapleAsciiString("");
                mplew.writeInt(v2);
                mplew.writeInt(v2);
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendPyramidUpdate-7409：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendPyramidResult(final byte rank, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendPyramidResult--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PYRAMID_RESULT.getValue());
        mplew.write(rank);
        mplew.writeInt(amount);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendPyramidResult-7424：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendMarrageEffect() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendMarrageEffect--------------------");
        }
        mplew.writeShort(71);
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendPyramidEnergy(final String type, final String amount) {
        if (ServerConstants.调试输出封包) {
            System.err.println("sendPyramidEnergy--------------------");
        }
        return sendString(1, type, amount);
    }
    
    public static final MaplePacket sendString(final int type, final String object, final String amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendString--------------------");
        }
        switch (type) {
            case 1: {
                mplew.writeShort(SendPacketOpcode.ENERGY.getValue());
                break;
            }
            case 2: {
                mplew.writeShort(SendPacketOpcode.GHOST_POINT.getValue());
                break;
            }
            case 3: {
                mplew.writeShort(SendPacketOpcode.GHOST_STATUS.getValue());
                break;
            }
        }
        mplew.writeMapleAsciiString(object);
        mplew.writeMapleAsciiString(amount);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendString-7461：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket sendGhostPoint(final String type, final String amount) {
        if (ServerConstants.调试输出封包) {
            System.err.println("sendGhostPoint--------------------");
        }
        return sendString(2, type, amount);
    }
    
    public static final MaplePacket sendGhostStatus(final String type, final String amount) {
        if (ServerConstants.调试输出封包) {
            System.err.println("sendGhostStatus--------------------");
        }
        return sendString(3, type, amount);
    }
    
    public static MaplePacket MulungEnergy(final int energy) {
        if (ServerConstants.调试输出封包) {
            System.err.println("MulungEnergy--------------------");
        }
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }
    
    public static MaplePacket getPollQuestion() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPollQuestion--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GAME_POLL_QUESTION.getValue());
        mplew.writeInt(1);
        mplew.writeInt(14);
        mplew.writeMapleAsciiString("Are you mudkiz?");
        mplew.writeInt(ServerConstants.Poll_Answers.length);
        for (byte i = 0; i < ServerConstants.Poll_Answers.length; ++i) {
            mplew.writeMapleAsciiString(ServerConstants.Poll_Answers[i]);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPollQuestion-7504：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getPollReply(final String message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getPollReply--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GAME_POLL_REPLY.getValue());
        mplew.writeMapleAsciiString(message);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getPollReply-7520：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getEvanTutorial(final String data) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getEvanTutorial--------------------");
        }
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.writeInt(8);
        mplew.write(0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeMapleAsciiString(data);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getEvanTutorial-7542：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showEventInstructions() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showEventInstructions--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showEventInstructions-7556：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getOwlOpen() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getOwlOpen--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(7);
        mplew.write(GameConstants.owlItems.length);
        for (final int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getOwlOpen-7574：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getOwlSearched(final int itemSearch, final List<HiredMerchant> hms) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getOwlSearched--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(6);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;
        for (final HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }
        mplew.writeInt(size);
        for (final HiredMerchant hm : hms) {
            final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
            for (final MaplePlayerShopItem item : items) {
                mplew.writeMapleAsciiString(hm.getOwnerName());
                mplew.writeInt(hm.getMap().getId());
                mplew.writeMapleAsciiString(hm.getDescription());
                mplew.writeInt(item.item.getQuantity());
                mplew.writeInt(item.bundles);
                mplew.writeInt(item.price);
                switch (2) {
                    case 0: {
                        mplew.writeInt(hm.getOwnerId());
                        break;
                    }
                    case 1: {
                        mplew.writeInt(hm.getStoreId());
                        break;
                    }
                    default: {
                        mplew.writeInt(hm.getObjectId());
                        break;
                    }
                }
                mplew.write((hm.getFreeSlot() == -1) ? 1 : 0);
                mplew.write(GameConstants.getInventoryType(itemSearch).getType());
                if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, item.item, true, true);
                }
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getOwlSearched-7623：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getRPSMode(final byte mode, final int mesos, final int selection, final int answer) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getRPSMode--------------------");
        }
        mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
        mplew.write(mode);
        switch (mode) {
            case 6: {
                if (mesos != -1) {
                    mplew.writeInt(mesos);
                    break;
                }
                break;
            }
            case 8: {
                mplew.writeInt(9000019);
                break;
            }
            case 11: {
                mplew.write(selection);
                mplew.write(answer);
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getRPSMode-7655：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getSlotUpdate(final byte invType, final byte newSlots) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getSlotUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_INVENTORY_SLOT.getValue());
        mplew.write(invType);
        mplew.write(newSlots);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getSlotUpdate-7671：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket followRequest(final int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("followRequest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FOLLOW_REQUEST.getValue());
        mplew.writeInt(chrid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("followRequest-7685：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket followEffect(final int initiator, final int replier, final Point toMap) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("followEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FOLLOW_EFFECT.getValue());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        if (replier == 0) {
            mplew.write((toMap != null) ? 1 : 0);
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("followEffect-7707：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getFollowMsg(final int opcode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFollowMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FOLLOW_MSG.getValue());
        mplew.writeLong(opcode);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFollowMsg-7721：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket moveFollow(final Point otherStart, final Point myStart, final Point otherEnd, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("moveFollow--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FOLLOW_MOVE.getValue());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(17);
        for (int i = 0; i < 8; ++i) {
            mplew.write(136);
        }
        mplew.write(8);
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("moveFollow-7746：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getFollowMessage(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getFollowMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FOLLOW_MESSAGE.getValue());
        mplew.writeShort(11);
        mplew.writeMapleAsciiString(msg);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getFollowMessage-7762：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getNodeProperties(final MapleMonster objectid, final MapleMap map) {
        if (ServerConstants.调试输出封包) {
            System.err.println("getNodeProperties--------------------");
        }
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MONSTER_PROPERTIES.getValue());
        mplew.writeInt(objectid.getObjectId());
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        for (final MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) {
                mplew.writeInt(500);
            }
        }
        mplew.writeZeroBytes(6);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getNodeProperties-7793：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }
    
    public static final MaplePacket getMovingPlatforms(final MapleMap map) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getMovingPlatforms--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_PLATFORM.getValue());
        mplew.writeInt(map.getPlatforms().size());
        for (final MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); ++x) {
                mplew.writeInt(mp.SN.get(x));
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getMovingPlatforms-7826：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static final MaplePacket getUpdateEnvironment(final MapleMap map) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("getUpdateEnvironment--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_ENV.getValue());
        mplew.writeInt(map.getEnvironment().size());
        for (final Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString(mp.getKey());
            mplew.writeInt(mp.getValue());
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("getUpdateEnvironment-7845：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendEngagementRequest(final String name, final int cid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendEngagementRequest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ENGAGE_REQUEST.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(name);
        mplew.writeInt(cid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendEngagementRequest-7861：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket trembleEffect(final int type, final int delay) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("trembleEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("trembleEffect-7883：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendEngagement(final byte msg, final int item, final MapleCharacter male, final MapleCharacter female) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendEngagement--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ENGAGE_RESULT.getValue());
        mplew.write(msg);
        switch (msg) {
            case 11: {
                mplew.writeInt(0);
                mplew.writeInt(male.getId());
                mplew.writeInt(female.getId());
                mplew.writeShort(1);
                mplew.writeInt(item);
                mplew.writeInt(item);
                mplew.writeAsciiString(male.getName(), 15);
                mplew.writeAsciiString(female.getName(), 15);
                break;
            }
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendEngagement-7930：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket englishQuizMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("englishQuizMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ENGLISH_QUIZ.getValue());
        mplew.writeInt(20);
        mplew.writeMapleAsciiString(msg);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("englishQuizMsg-7947：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket openBeans(final int beansCount, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("openBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME1.getValue());
        mplew.writeInt(beansCount);
        mplew.write(type);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("openBeans-7962：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket updateBeans(final int cid, final int beansCount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.UPDATE_BEANS.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(beansCount);
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("updateBeans-7978：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansZJgeidd(final int b, final int a) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(b);
        mplew.writeInt(a);
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansHJG() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(7);
        mplew.writeInt(1);
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansJDCS(final int a) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(4);
        mplew.writeInt(a);
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansJDXZ(final int a, final int a1, final int a2, final int a3, final int a4, final int a5, final int a6) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(4);
        mplew.write(a);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(a1);
        mplew.writeInt(a2);
        mplew.writeInt(a3);
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansQR() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(7);
        mplew.writeInt(1);
        return mplew.getPacket();
    }
    
    public static MaplePacket showBeans(final List<Beans> beansInfo) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showBeans--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME2.getValue());
        mplew.write(0);
        mplew.write(beansInfo.size());
        for (final Beans bean : beansInfo) {
            mplew.writeShort(bean.getPos());
            mplew.write(bean.getType());
            mplew.writeInt(bean.getNumber());
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showBeans-8004：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showCharCash(final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showCharCash--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAR_CASH.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getCSPoints(2));
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showCharCash-8021：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnLove(final int oid, final int itemid, final String name, final String msg, final Point pos, final int ft) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnLove--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_LOVE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(msg);
        mplew.writeMapleAsciiString(name);
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y + ft);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnLove-8040：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeLove(final int oid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeLove--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_LOVE.getValue());
        mplew.writeInt(oid);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("removeLove-8054：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket licenseRequest() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("licenseRequest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(22);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("licenseRequest-8069：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket licenseResult() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("licenseResult--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LICENSE_RESULT.getValue());
        mplew.write(1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("licenseResult-8084：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showForcedEquip() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("showForcedEquip--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FORCED_MAP_EQUIP.getValue());
        mplew.writeInt(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showForcedEquip-8098：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket removeTutorialStats() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("removeTutorialStats--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TEMP_STATS_RESET.getValue());
        if (ServerConstants.get38记录()) {
            final ServerConstants serverConstants = new ServerConstants();
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket spawnTutorialSummon(final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("spawnTutorialSummon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TUTORIAL_SUMMON.getValue());
        mplew.write(type);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("spawnTutorialSummon-8125：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket requestBuddylistAdd(final int cidFrom, final String nameFrom) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("requestBuddylistAdd--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\0', 13));
        mplew.write(1);
        mplew.write(5);
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr("群未定", '\0', 17));
        mplew.write(0);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("requestBuddylistAdd-8151：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendAutoHpPot(final int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendAutoHpPot--------------------");
        }
        mplew.writeShort(SendPacketOpcode.AUTO_HP_POT.getValue());
        mplew.writeInt(itemId);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendAutoHpPot-8167：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendAutoMpPot(final int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendAutoMpPot--------------------");
        }
        mplew.writeShort(SendPacketOpcode.AUTO_MP_POT.getValue());
        mplew.writeInt(itemId);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendAutoMpPot-8183：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket testPacket(final byte[] testmsg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("testPacket--------------------");
        }
        mplew.write(testmsg);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("testPacket-8196：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket BeansGameMessage(final int cid, final int x, final String laba) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("testPacket--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BEANS_GAME_MESSAGE.getValue());
        mplew.writeInt(cid);
        mplew.write(x);
        mplew.writeMapleAsciiString(laba);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("testPacket-8196：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendEventWindow(final int npc, final int lx) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("sendEventWindow--------------------");
        }
        mplew.writeShort(SendPacketOpcode.EVENT_WINDOW.getValue());
        if (lx <= 0) {
            mplew.writeInt(55);
        }
        else {
            mplew.writeInt(lx);
        }
        if (npc > 0) {
            mplew.writeInt(npc);
        }
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("sendEventWindow-8218：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket openWeb(final String web) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("openWeb--------------------");
        }
        mplew.writeShort(SendPacketOpcode.OPEN_WEB.getValue());
        mplew.writeMapleAsciiString(web);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("openWeb-8232：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket giveEnergyCharge(final int barammount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeShort(barammount);
        mplew.writeShort(0);
        mplew.writeLong(0L);
        mplew.write(0);
        mplew.writeInt(50);
        return mplew.getPacket();
    }
    
    public static MaplePacket shenlong(final int i) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(i);
        mplew.write(HexTool.getByteArrayFromHexString("DC 05 00 00 90 5F 01 00 DC 05 00 00 9B 00 00 00"));
        return mplew.getPacket();
    }
    
    public static MaplePacket shenlong2(final int i) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(i);
        mplew.write(HexTool.getByteArrayFromHexString("02 CB 06 00 00 FB 44 00 00"));
        return mplew.getPacket();
    }
    
    public static MaplePacket DragonBall1(final int i, final boolean Zhaohuan) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeInt(0);
        mplew.write(1);
        if (!Zhaohuan) {
            mplew.writeShort(0);
            mplew.writeShort(i);
            mplew.writeShort(0);
        }
        else {
            mplew.writeLong(512L);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket getCY1(final int npc, final String talk, final byte type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(13);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(type);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(talk);
        return mplew.getPacket();
    }
    
    public static MaplePacket getCY2(final int npc, final String talk, final byte type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(16);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(type);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(talk);
        return mplew.getPacket();
    }
    
    public static MaplePacket showRQRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("fame"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket show初级(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("mrsgrw"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket show中级(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("mrsgrwa"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket show高级(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("mrsgrwas"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showJXRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeLong(rs.getInt("jx"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showXRRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("pvpkills"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showBSRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("pvpdeaths"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showVipRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("vip"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showLevelRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("vip"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showMesoRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(rs.getInt("vip"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showRenwu2Ranks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("renwu2"));
            mplew.writeInt(rs.getInt("vip"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showGuildRanks(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("GP"));
            mplew.writeInt(rs.getInt("logo"));
            mplew.writeInt(rs.getInt("logoColor"));
            mplew.writeInt(rs.getInt("logoBG"));
            mplew.writeInt(rs.getInt("logoBGColor"));
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket 转生(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("ddj"));
            mplew.writeInt(rs.getInt("level"));
            mplew.writeInt(rs.getInt("meso"));
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sub_93F0BE(final int v1) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(235);
        mplew.writeInt(v1);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket rechargeCombo(final String n, final int value) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(232);
        mplew.writeMapleAsciiString(n);
        mplew.writeInt(value);
        if (ServerConstants.get38记录()) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket modifyInventory(final boolean updateTick, final ModifyInventory mod) {
        return modifyInventory(updateTick, Collections.singletonList(mod));
    }
    
    public static MaplePacket modifyInventory(final boolean updateTick, final List<ModifyInventory> mods) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(updateTick ? 1 : 0);
        mplew.write(mods.size());
        int addMovement = -1;
        for (final ModifyInventory mod : mods) {
            mplew.write(mod.getMode());
            mplew.write(mod.getInventoryType());
            mplew.writeShort((mod.getMode() == 2) ? mod.getOldPosition() : mod.getPosition());
            switch (mod.getMode()) {
                case 0: {
                    PacketHelper.addItemInfo(mplew, mod.getItem(), true, false);
                    break;
                }
                case 1: {
                    mplew.writeShort(mod.getQuantity());
                    break;
                }
                case 2: {
                    mplew.writeShort(mod.getPosition());
                    if (mod.getPosition() < 0 || mod.getOldPosition() < 0) {
                        addMovement = ((mod.getOldPosition() < 0) ? 1 : 2);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (mod.getPosition() < 0) {
                        addMovement = 2;
                        break;
                    }
                    break;
                }
            }
            mod.clear();
        }
        if (addMovement > -1) {
            mplew.write(addMovement);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket petAutoHP(final int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.AUTO_HP_POT.getValue());
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }
    
    public static MaplePacket petAutoMP(final int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.AUTO_MP_POT.getValue());
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }
    
    public static MaplePacket catchMob(final int mobid, final int itemid, final byte success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(80);
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(mobid);
        return mplew.getPacket();
    }
    
    public static MaplePacket updateEquipSlot(final Item item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("updateEquipSlot--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(HexTool.getByteArrayFromHexString("02 03 01"));
        mplew.writeShort(item.getPosition());
        mplew.write(0);
        mplew.write(item.getType());
        mplew.writeShort(item.getPosition());
        PacketHelper.addItemInfo(mplew, item, true, true);
        mplew.writeMapleAsciiString("wat");
        return mplew.getPacket();
    }
    
    public static MaplePacket MonsterBoat(final boolean isEnter) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.BOAT_EFFECT.getValue());
        if (isEnter) {
            mplew.write(11);
            mplew.write(5);
        }
        else {
            mplew.write(11);
            mplew.write(6);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket movePlayer(final int cid, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.err.println("movePlayer--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }
    
    public static MaplePacket 伤害排行榜(final int npcid, final ResultSet rs) throws SQLException {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(npcid);
        if (!rs.last()) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("ddj"));
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket sendPolice(final String text) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MAPLE_ADMIN.getValue());
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }
    
    public static MaplePacket PVPdamagePlayer(final int chrId, final int type, final int monsteridfrom, final int damage) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(chrId);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(damage);
        return mplew.getPacket();
    }
    
    static {
        EMPTY_STATUPDATE = Collections.emptyList();
        CHAR_INFO_MAGIC = new byte[] { -1, -55, -102, 59 };
        MaplePacketCreator.serialNumberStr = new String[] { "000206C21850713218", "000306C31008231660", "000206C2-466031447", "000506E32129939347", "000306C3-730520493", "000206C21755774662", "000206D7912588756", "000206C2694547", "000206D7-2098222661", "000206C2232414", "000206C2199851", "000206C2715841891", "000306F21042804612", "000206C2222325", "000506E3917974035", "000206C2185553", "000206C2222325", "000206C2-625893834", "000206C2827442" };
    }
}
