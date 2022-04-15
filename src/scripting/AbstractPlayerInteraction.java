package scripting;

import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World.Broadcast;
import handling.world.World.Guild;
import handling.world.guild.MapleGuild;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scripting.MapleMonsterInformationProvider.DropEntry;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Randomizer;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Event_DojoAgent;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.PetPacket;
import tools.packet.UIPacket;
import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import constants.MapleItemType;

public abstract class AbstractPlayerInteraction
{
    private final MapleClient c;
    
    public AbstractPlayerInteraction(final MapleClient c) {
        this.c = c;
    }
    
    public void lockUI() {
        this.c.announce(UIPacket.IntroLock(true));
        this.c.announce(UIPacket.IntroDisableUI(true));
    }
    
    public boolean isQuestStarted(final int questId) {
        final byte status = this.c.getPlayer().getQuestStatus(questId);
        return status == 1;
    }
    
    public void spouseMessage(final int op, final String msg) {
        this.getClient().announce(MaplePacketCreator.spouseMessage(op, msg));
    }
    
    public final MapleClient getClient() {
        return this.c;
    }
    
    public final MapleClient getC() {
        return this.c;
    }
    
    public final boolean Leader() {
        return this.getPlayer().getParty() != null && this.getParty().getLeader().getId() == this.c.getPlayer().getId();
    }
    
    public MapleCharacter getChar() {
        this.c.getPlayer().getInventory(MapleInventoryType.USE).listById(1).iterator();
        return this.c.getPlayer();
    }
    
    public final ChannelServer getChannelServer() {
        return this.c.getChannelServer();
    }
    
    public final MapleCharacter getPlayer() {
        return this.c.getPlayer();
    }
    
    public final MapleMap getMap() {
        return this.c.getPlayer().getMap();
    }
    
    public final EventManager getEventManager(final String event) {
        return this.c.getChannelServer().getEventSM().getEventManager(event);
    }
    
    public final EventInstanceManager getEventInstance() {
        return this.c.getPlayer().getEventInstance();
    }
    
    public final void forceRemovePlayerByCharName(final String name) {
        ChannelServer.forceRemovePlayerByCharName(name);
    }
    
    public final void warp(final int map) {
        final MapleMap mapz = this.getWarpMap(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        }
        catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }
    
    public final void warp_Instanced(final int map) {
        final MapleMap mapz = this.getMap_Instanced(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        }
        catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }
    
    public final void warp(final int map, final int portal) {
        final MapleMap mapz = this.getWarpMap(map);
        if (portal != 0 && map == this.c.getPlayer().getMapId()) {
            final Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
        else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }
    
    public final void warpS(final int map, final int portal) {
        final MapleMap mapz = this.getWarpMap(map);
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }
    
    public final void warp(final int map, String portal) {
        final MapleMap mapz = this.getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == this.c.getPlayer().getMapId()) {
            final Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
        else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }
    
    public final void warpS(final int map, String portal) {
        final MapleMap mapz = this.getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }
    
    public final void warpMap(final int mapid, final int portal) {
        final MapleMap map = this.getMap(mapid);
        for (final MapleCharacter chr : this.c.getPlayer().getMap().getCharactersThreadsafe()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }
    
    public final void playPortalSE() {
        this.c.sendPacket(MaplePacketCreator.showOwnBuffEffect(0, 5));
    }
    
    private final MapleMap getWarpMap(final int map) {
        return ChannelServer.getInstance(this.c.getChannel()).getMapFactory().getMap(map);
    }
    
    public final MapleMap getMap(final int map) {
        return this.getWarpMap(map);
    }
    
    public final MapleMap getMap_Instanced(final int map) {
        return (this.c.getPlayer().getEventInstance() == null) ? this.getMap(map) : this.c.getPlayer().getEventInstance().getMapInstance(map);
    }
    
    public final void spawnMap(final int MapID, final int MapID2) {
        for (final ChannelServer chan : ChannelServer.getAllInstances()) {
            for (final MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr != null && this.getC().getChannel() == chr.getClient().getChannel() && chr.getMapId() == MapID) {
                    this.warp(MapID2);
                }
            }
        }
    }
    
    public final void spawnMap(final int MapID) {
        for (final ChannelServer chan : ChannelServer.getAllInstances()) {
            for (final MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr != null && this.getC().getChannel() == chr.getClient().getChannel() && chr.getMapId() == this.getMapId()) {
                    this.warp(MapID);
                }
            }
        }
    }
    
    public void spawnMonster(final int id, final int qty) {
        this.spawnMob(id, qty, new Point(this.c.getPlayer().getPosition()));
    }
    
    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map) {
        for (int i = 0; i < qty; ++i) {
            this.getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }
    
    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map, final int hp) {
        for (int i = 0; i < qty; ++i) {
            this.getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y), hp);
        }
    }
    
    public final void spawnMob(final int id, final int qty, final int x, final int y) {
        this.spawnMob(id, qty, new Point(x, y));
    }
    
    public final void spawnMob_map(final int id, final int mapid, final int x, final int y) {
        this.spawnMob_map(id, mapid, new Point(x, y));
    }
    
    public final void spawnMob_map(final int id, final int mapid, final Point pos) {
        this.c.getChannelServer().getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
    }
    
    public final void spawnMob(final int id, final int x, final int y) {
        this.spawnMob(id, 1, new Point(x, y));
    }
    
    public final void spawnMob(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; ++i) {
            this.c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }
    
    public final void killMob(final int ids) {
        this.c.getPlayer().getMap().killMonster(ids);
    }
    
    public final void killAllMob() {
        this.c.getPlayer().getMap().killAllMonsters(true);
    }
    
    public final void addHP(final int delta) {
        this.c.getPlayer().addHP(delta);
    }
    
    public final void setPlayerStat(final String type, final int x) {
        switch (type) {
            case "LVL": {
                this.c.getPlayer().setLevel((short)x);
                break;
            }
            case "STR": {
                this.c.getPlayer().getStat().setStr((short)x);
                break;
            }
            case "DEX": {
                this.c.getPlayer().getStat().setDex((short)x);
                break;
            }
            case "INT": {
                this.c.getPlayer().getStat().setInt((short)x);
                break;
            }
            case "LUK": {
                this.c.getPlayer().getStat().setLuk((short)x);
                break;
            }
            case "HP": {
                this.c.getPlayer().getStat().setHp(x);
                break;
            }
            case "MP": {
                this.c.getPlayer().getStat().setMp(x);
                break;
            }
            case "MAXHP": {
                this.c.getPlayer().getStat().setMaxHp((short)x);
                break;
            }
            case "MAXMP": {
                this.c.getPlayer().getStat().setMaxMp((short)x);
                break;
            }
            case "RAP": {
                this.c.getPlayer().setRemainingAp((short)x);
                break;
            }
            case "RSP": {
                this.c.getPlayer().setRemainingSp((short)x);
                break;
            }
            case "GID": {
                this.c.getPlayer().setGuildId(x);
                break;
            }
            case "GRANK": {
                this.c.getPlayer().setGuildRank((byte)x);
                break;
            }
            case "ARANK": {
                this.c.getPlayer().setAllianceRank((byte)x);
                break;
            }
            case "GENDER": {
                this.c.getPlayer().setGender((byte)x);
                break;
            }
            case "FACE": {
                this.c.getPlayer().setFace(x);
                break;
            }
            case "HAIR": {
                this.c.getPlayer().setHair(x);
                break;
            }
        }
    }
    
    public int getPlayerStat(final String type) {
        if (type.equals("LVL")) {
            return this.c.getPlayer().getLevel();
        }
        if (type.equals("STR")) {
            return this.c.getPlayer().getStat().getStr();
        }
        if (type.equals("DEX")) {
            return this.c.getPlayer().getStat().getDex();
        }
        if (type.equals("INT")) {
            return this.c.getPlayer().getStat().getInt();
        }
        if (type.equals("LUK")) {
            return this.c.getPlayer().getStat().getLuk();
        }
        if (type.equals("HP")) {
            return this.c.getPlayer().getStat().getHp();
        }
        if (type.equals("MP")) {
            return this.c.getPlayer().getStat().getMp();
        }
        if (type.equals("MAXHP")) {
            return this.c.getPlayer().getStat().getMaxHp();
        }
        if (type.equals("MAXMP")) {
            return this.c.getPlayer().getStat().getMaxMp();
        }
        if (type.equals("RAP")) {
            return this.c.getPlayer().getRemainingAp();
        }
        if (type.equals("RSP")) {
            return this.c.getPlayer().getRemainingSp();
        }
        if (type.equals("GID")) {
            return this.c.getPlayer().getGuildId();
        }
        if (type.equals("GRANK")) {
            return this.c.getPlayer().getGuildRank();
        }
        if (type.equals("ARANK")) {
            return this.c.getPlayer().getAllianceRank();
        }
        if (type.equals("GM")) {
            return this.c.getPlayer().isGM() ? 1 : 0;
        }
        if (type.equals("ADMIN")) {
            return this.c.getPlayer().isAdmin() ? 1 : 0;
        }
        if (type.equals("GENDER")) {
            return this.c.getPlayer().getGender();
        }
        if (type.equals("FACE")) {
            return this.c.getPlayer().getFace();
        }
        if (type.equals("HAIR")) {
            return this.c.getPlayer().getHair();
        }
        return -1;
    }
    
    public final String getName() {
        return this.c.getPlayer().getName();
    }
    
    public final boolean haveItem(final int itemid) {
        return this.haveItem(itemid, 1);
    }
    
    public final boolean haveItem(final int itemid, final int quantity) {
        return this.haveItem(itemid, quantity, false, true);
    }
    
    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        return this.c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }
    
    public final boolean canHold() {
        for (int i = 1; i <= 5; ++i) {
            if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean canHold(final int itemid) {
        return this.c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }
    
    public final boolean canHold(final int itemid, final int quantity) {
        return MapleInventoryManipulator.checkSpace(this.c, itemid, quantity, "");
    }
    
    public final MapleQuestStatus getQuestRecord(final int id) {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }
    
    public final byte getQuestStatus(final int id) {
        return this.c.getPlayer().getQuestStatus(id);
    }
    
    public void completeQuest(final int id) {
        this.c.getPlayer().setQuestAdd(id);
    }
    
    public final boolean isQuestActive(final int id) {
        return this.getQuestStatus(id) == 1;
    }
    
    public final boolean isQuestFinished(final int id) {
        return this.getQuestStatus(id) == 2;
    }
    
    public final void showQuestMsg(final String msg) {
        this.c.sendPacket(MaplePacketCreator.showQuestMsg(msg));
    }
    
    public final void forceStartQuest(final int id, final String data) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, data);
    }
    
    public final void forceStartQuest(final int id, final int data, final boolean filler) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }
    
    public void clearAranPolearm() {
        this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((short)(-11));
    }
    
    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, null);
    }
    
    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(this.getPlayer(), 0);
    }
    
    public void spawnNpc(final int npcId) {
        this.c.getPlayer().getMap().spawnNpc(npcId, this.c.getPlayer().getPosition());
    }
    
    public final void spawnNpc(final int npcId, final int x, final int y) {
        this.c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }
    
    public final void spawnNpc(final int npcId, final Point pos) {
        this.c.getPlayer().getMap().spawnNpc(npcId, pos);
    }
    
    public final void removeNpc(final int mapid, final int npcId) {
        this.c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }
    
    public final void forceStartReactor(final int mapid, final int id) {
        final MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            final MapleReactor react = (MapleReactor)remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(this.c);
                break;
            }
        }
    }
    
    public final void destroyReactor(final int mapid, final int id) {
        final MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            final MapleReactor react = (MapleReactor)remo;
            if (react.getReactorId() == id) {
                react.hitReactor(this.c);
                break;
            }
        }
    }
    
    public final void hitReactor(final int mapid, final int id) {
        final MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            final MapleReactor react = (MapleReactor)remo;
            if (react.getReactorId() == id) {
                react.hitReactor(this.c);
                break;
            }
        }
    }
    
    public final int getJob() {
        return this.c.getPlayer().getJob();
    }
    
    public final int getNX(final int 类型) {
        return this.c.getPlayer().getCSPoints(类型);
    }
    
    public final void gainD(final int amount) {
        this.c.getPlayer().modifyCSPoints(2, amount, true);
    }
    
    public final void gainNX(final int amount) {
        this.c.getPlayer().modifyCSPoints(1, amount, true);
    }
    
    public final void gainItemPeriod(final int id, final short quantity, final int period) {
        this.gainItem(id, quantity, false, period, -1, "", (byte)0);
    }
    
    public final void gainItemPeriod(final int id, final short quantity, final long period, final String owner) {
        this.gainItem(id, quantity, false, period, -1, owner, (byte)0);
    }
    
    public void gainItem(final int id, final short quantity) {
        this.gainItem(id, quantity, false, 0L, -1, "", (byte)0);
    }
    
    public void gainItem(final int id, final short quantity, final long period, final byte Flag) {
        this.gainItem(id, quantity, false, period, -1, "", Flag);
    }
    
    public void gainItem(final int id, final short quantity, final boolean randomStats) {
        this.gainItem(id, quantity, randomStats, 0L, -1, "", (byte)0);
    }
    
    public void gainItem(final int id, final short quantity, final boolean randomStats, final int slots) {
        this.gainItem(id, quantity, randomStats, 0L, slots, "", (byte)0);
    }
    
    public void gainItem(final int id, final short quantity, final long period) {
        this.gainItem(id, quantity, false, period, -1, "", (byte)0);
    }
    
    public void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots) {
        this.gainItem(id, quantity, randomStats, period, slots, "", (byte)0);
    }
    
    public void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final byte Flag) {
        this.gainItem(id, quantity, randomStats, period, slots, owner, this.c, Flag);
    }
    
    public void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final MapleClient cg, final byte Flag) {
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);
            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.is飞镖道具(id) && !GameConstants.is子弹道具(id)) {
                final Equip item = (Equip)(randomStats ? ii.randomizeStats((Equip)ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0L) {
                    if (period < 1000L) {
                        item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                    }
                    else {
                        item.setExpiration(System.currentTimeMillis() + period);
                    }
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte)(item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                final String name = ii.getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) {
                    final String msg = "你已获得称号 <" + name + ">";
                    cg.getPlayer().dropMessage(5, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            }
            else {
                MapleInventoryManipulator.addById(cg, id, quantity, (owner == null) ? "" : owner, null, period, Flag);
            }
        }
        else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }
    
    public void gainItem(final int id, final int str, final int dex, final int luk, final int Int, final int watk, final int matk) {
        this.gainItem(id, str, dex, luk, Int, 0, 0, watk, matk, 0, 0, 0, 0, 0, 0);
    }
    
    public void gainItem(final int id, final int str, final int dex, final int luk, final int Int, final int hp, final int mp, final int watk, final int matk, final int wdef, final int mdef, final int hb, final int mz, final int ty, final int yd) {
        this.gainItemS(id, str, dex, luk, Int, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd, this.c, 0);
    }
    
    public void gainItem(final int id, final int str, final int dex, final int luk, final int Int, final int hp, final int mp, final int watk, final int matk, final int wdef, final int mdef, final int hb, final int mz, final int ty, final int yd, final int time) {
        this.gainItemS(id, str, dex, luk, Int, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd, this.c, time);
    }
    
    public void gainItemS(final int id, final int str, final int dex, final int luk, final int Int, final int hp, final int mp, final int watk, final int matk, final int wdef, final int mdef, final int hb, final int mz, final int ty, final int yd, final int time) {
        this.gainItemS(id, str, dex, luk, Int, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd, this.c, time);
    }
    
    public void gainItemS(final int id, final int str, final int dex, final int luk, final int Int, final int hp, final int mp, final int watk, final int matk, final int wdef, final int mdef, final int hb, final int mz, final int ty, final int yd, final MapleClient cg, final int time) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleInventoryType type = GameConstants.getInventoryType(id);
        if (!MapleInventoryManipulator.checkSpace(cg, id, 1, "")) {
            return;
        }
        if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
            final Equip item = (Equip)ii.getEquipById(id);
            final String name = ii.getName(id);
            if (id / 10000 == 114 && name != null && name.length() > 0) {
                final String msg = "你已获得称号 <" + name + ">";
                cg.getPlayer().dropMessage(5, msg);
                cg.getPlayer().dropMessage(5, msg);
            }
            if (time > 0) {
                item.setExpiration(System.currentTimeMillis() + time * 60 * 60 * 1000);
            }
            if (str > 0) {
                item.setStr((short)str);
            }
            if (dex > 0) {
                item.setDex((short)dex);
            }
            if (luk > 0) {
                item.setLuk((short)luk);
            }
            if (Int > 0) {
                item.setInt((short)Int);
            }
            if (hp > 0) {
                item.setHp((short)hp);
            }
            if (mp > 0) {
                item.setMp((short)mp);
            }
            if (watk > 0) {
                item.setWatk((short)watk);
            }
            if (matk > 0) {
                item.setMatk((short)matk);
            }
            if (wdef > 0) {
                item.setWdef((short)wdef);
            }
            if (mdef > 0) {
                item.setMdef((short)mdef);
            }
            if (hb > 0) {
                item.setAvoid((short)hb);
            }
            if (mz > 0) {
                item.setAcc((short)mz);
            }
            if (ty > 0) {
                item.setJump((short)ty);
            }
            if (yd > 0) {
                item.setSpeed((short)yd);
            }
            MapleInventoryManipulator.addbyItem(cg, item.copy());
        }
        else {
            MapleInventoryManipulator.addById(cg, id, (short)1, "", (byte)0);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, (short)1, true));
    }
    
    public final void changeMusic(final String songName) {
        this.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(songName));
    }
    
    public final void cs(final String songName) {
        this.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(songName));
    }
    
    public final void worldMessage(final int type, final int channel, final String message, final boolean smegaEar) {
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, channel, message, smegaEar).getBytes());
    }
    
    public final void worldMessage(final int type, final String message) {
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, message).getBytes());
    }
    
    public void givePartyExp_PQ(final int maxLevel, final double mod) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            final int amount = (int)Math.round(GameConstants.getExpNeededForLevel((this.getPlayer().getLevel() > maxLevel) ? (maxLevel + this.getPlayer().getLevel() / 10) : this.getPlayer().getLevel()) / (Math.min(this.getPlayer().getLevel(), maxLevel) / 10.0) / mod);
            this.gainExp(amount);
            return;
        }
        final int cMap = this.getPlayer().getMapId();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
                final int amount2 = (int)Math.round(GameConstants.getExpNeededForLevel((curChar.getLevel() > maxLevel) ? (maxLevel + curChar.getLevel() / 10) : curChar.getLevel()) / (Math.min(curChar.getLevel(), maxLevel) / 10.0) / mod);
                curChar.gainExp(amount2, true, true, true);
            }
        }
    }
    
    public final void playerMessage(final String message) {
        this.playerMessage(5, message);
    }
    
    public final void mapMessage(final String message) {
        this.mapMessage(5, message);
    }
    
    public final void guildMessage(final String message) {
        this.guildMessage(5, message);
    }
    
    public final void playerMessage(final int type, final String message) {
        this.c.sendPacket(MaplePacketCreator.serverNotice(type, message));
    }
    
    public final void mapMessage(final int type, final String message) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }
    
    public final void guildMessage(final int type, final String message) {
        if (this.getPlayer().getGuildId() > 0) {
            Guild.guildPacket(this.getPlayer().getGuildId(), MaplePacketCreator.serverNotice(type, message));
        }
    }
    
    public final MapleGuild getGuild() {
        return this.getGuild(this.getPlayer().getGuildId());
    }
    
    public final MapleGuild getGuild(final int guildid) {
        return Guild.getGuild(guildid);
    }
    
    public final MapleParty getParty() {
        return this.c.getPlayer().getParty();
    }
    
    public final int getCurrentPartyId(final int mapid) {
        return this.getMap(mapid).getCurrentPartyId();
    }
    
    public void czdt(final int MapID) {
        final MapleCharacter player = this.c.getPlayer();
        final int mapid = MapID;
        final MapleMap map = player.getMap();
        if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
            final MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
            final MaplePortal newPor = newMap.getPortal(0);
            final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet(map.getCharacters());
            for (final MapleCharacter m : mcs) {
                final int x = 0;
                if (x >= 5) {
                    return;
                }
            }
        }
    }
    
    public final boolean isLeader() {
        return this.getParty() != null && this.getParty().getLeader().getId() == this.c.getPlayer().getId();
    }
    
    public final boolean isAllPartyMembersAllowedJob(final int job) {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean allMembersHere() {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            final MapleCharacter chr = this.c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }
    
    public final void warpParty(final int mapId) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.warp(mapId, 0);
            return;
        }
        final MapleMap target = this.getMap(mapId);
        final int cMap = this.getPlayer().getMapId();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }
    
    public final void warpParty(final int mapId, final String portal) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.warp(mapId, portal);
            return;
        }
        final MapleMap target = this.getMap(mapId);
        final int cMap = this.getPlayer().getMapId();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(portal));
            }
        }
    }
    
    public final void warpParty(final int mapId, final int portal) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                this.warp(mapId);
            }
            else {
                this.warp(mapId, portal);
            }
            return;
        }
        final boolean rand = portal < 0;
        final MapleMap target = this.getMap(mapId);
        final int cMap = this.getPlayer().getMapId();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    }
                    catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                }
                else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }
    
    public final void warpParty_Instanced(final int mapId) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.warp_Instanced(mapId);
            return;
        }
        final MapleMap target = this.getMap_Instanced(mapId);
        final int cMap = this.getPlayer().getMapId();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == this.getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }
    
    public void gainDY(final int gain) {
        this.c.getPlayer().modifyCSPoints(2, gain, true);
    }
    
    public void gainMeso(final int gain) {
        this.c.getPlayer().gainMeso(gain, true, false, true);
    }
    
    public void gainExp(final int gain) {
        this.c.getPlayer().gainExp(gain, true, true, true);
    }
    
    public void gainExpR(final int gain) {
        this.c.getPlayer().gainExp(gain * this.c.getChannelServer().getExpRate(), true, true, true);
    }
    
    public final void givePartyItems(final int id, final short quantity, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity, (byte)0);
            }
            else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
        }
    }
    
    public final void givePartyItems(final int id, final short quantity) {
        this.givePartyItems(id, quantity, false);
    }
    
    public final void givePartyItems(final int id, final short quantity, final boolean removeAll) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainItem(id, (short)(removeAll ? (-this.getPlayer().itemQuantity(id)) : quantity));
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                this.gainItem(id, (short)(removeAll ? (-curChar.itemQuantity(id)) : quantity), false, 0L, 0, "", curChar.getClient(), (byte)0);
            }
        }
    }
    
    public final void givePartyExp(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.gainExp(amount, true, true, true);
        }
    }
    
    public final void givePartyExp(final int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainExp(amount);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.gainExp(amount, true, true, true);
            }
        }
    }
    
    public final void givePartyNX(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }
    
    public final void givePartyDY(final int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainDY(amount);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.modifyCSPoints(2, amount, true);
            }
        }
    }
    
    public final void givePartyMeso(final int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainMeso(amount);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.gainMeso(amount, true);
            }
        }
    }
    
    public final void givePartyNX(final int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainNX(amount);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.modifyCSPoints(1, amount, true);
            }
        }
    }
    
    public final void endPartyQuest(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }
    
    public final void endPartyQuest(final int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.getPlayer().endPartyQuest(amount);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.endPartyQuest(amount);
            }
        }
    }
    
    public final void removeFromParty(final int id, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            final int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(this.c, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short)(-possesed), true));
            }
        }
    }
    
    public final void removeFromParty(final int id) {
        this.givePartyItems(id, (short)0, true);
    }
    
    public final void useSkill(final int skill, final int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(this.c.getPlayer());
    }
    
    public final void useItem(final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(this.c.getPlayer());
        this.c.sendPacket(UIPacket.getStatusMsg(id));
    }
    
    public final void cancelItem(final int id) {
        this.c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1L);
    }
    
    public final int getMorphState() {
        return this.c.getPlayer().getMorphState();
    }
    
    public final void removeAll(final int id) {
        this.c.getPlayer().removeAll(id);
    }
    
    public final void gainCloseness(final int closeness, final int index) {
        final MaplePet pet = this.getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            this.getClient().getSession().write(PetPacket.updatePet(pet, this.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition()), true));
        }
    }
    
    public final void gainClosenessAll(final int closeness) {
        for (final MaplePet pet : this.getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                this.getClient().getSession().write(PetPacket.updatePet(pet, this.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition()), true));
            }
        }
    }
    
    public final void resetMap(final int mapid) {
        this.getMap(mapid).resetFully();
    }
    
    public final void openNpc(final int id) {
        NPCScriptManager.getInstance().start(this.getClient(), id);
    }
    
    public void openNpc(final int id, final int wh) {
        NPCScriptManager.getInstance().dispose(this.c);
        NPCScriptManager.getInstance().start(this.getClient(), id, wh);
    }
    
    public void serverNotice(final String Text) {
        this.getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, Text));
    }
    
    public final void openNpc(final MapleClient cg, final int id) {
        NPCScriptManager.getInstance().start(cg, id);
    }
    
    public final int getMapId() {
        return this.c.getPlayer().getMap().getId();
    }
    
    public final boolean haveMonster(final int mobid) {
        for (final MapleMapObject obj : this.c.getPlayer().getMap().getAllMonstersThreadsafe()) {
            final MapleMonster mob = (MapleMonster)obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }
    
    public final int getChannelNumber() {
        return this.c.getChannel();
    }
    
    public final int getMonsterCount(final int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }
    
    public final void teachSkill(final int id, final byte level, final byte masterlevel) {
        this.getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }
    
    public final void teachSkill(final int id, byte level) {
        final ISkill skil = SkillFactory.getSkill(id);
        if (this.getPlayer().getSkillLevel(skil) > level) {
            level = this.getPlayer().getSkillLevel(skil);
        }
        this.getPlayer().changeSkillLevel(skil, level, skil.getMaxLevel());
    }
    
    public final int getPlayerCount(final int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }
    
    public final void dojo_getUp() {
        this.c.sendPacket(MaplePacketCreator.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1"));
        this.c.sendPacket(MaplePacketCreator.dojoWarpUp());
    }
    
    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting);
        }
        return Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
    }
    
    public final int dojo_getPts() {
        return this.c.getPlayer().getDojo();
    }
    
    public final byte getShopType() {
        return this.c.getPlayer().getPlayerShop().getShopType();
    }
    
    public final MapleEvent getEvent(final String loc) {
        return this.c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }
    
    public final int getSavedLocation(final String loc) {
        final Integer ret = this.c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100000000;
        }
        return ret;
    }
    
    public final void saveLocation(final String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }
    
    public final void saveReturnLocation(final String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc), this.c.getPlayer().getMap().getReturnMap().getId());
    }
    
    public final void clearSavedLocation(final String loc) {
        this.c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }
    
    public final void summonMsg(final String msg) {
        if (!this.c.getPlayer().hasSummon()) {
            this.playerSummonHint(true);
        }
        this.c.sendPacket(UIPacket.summonMessage(msg));
    }
    
    public final void summonMsg(final int type) {
        if (!this.c.getPlayer().hasSummon()) {
            this.playerSummonHint(true);
        }
        this.c.sendPacket(UIPacket.summonMessage(type));
    }
    
    public final void HSText(final String msg) {
        this.c.sendPacket(MaplePacketCreator.HSText(msg));
    }
    
    public final void showInstruction(final String msg, final int width, final int height) {
        this.c.sendPacket(MaplePacketCreator.sendHint(msg, width, height));
    }
    
    public final void playerSummonHint(final boolean summon) {
        this.c.getPlayer().setHasSummon(summon);
        this.c.sendPacket(UIPacket.summonHelper(summon));
    }
    
    public final String getInfoQuest(final int id) {
        return this.c.getPlayer().getInfoQuest(id);
    }
    
    public final void updateInfoQuest(final int id, final String data) {
        this.c.getPlayer().updateInfoQuest(id, data);
    }
    
    public final boolean getEvanIntroState(final String data) {
        return this.getInfoQuest(22013).equals(data);
    }
    
    public final void updateEvanIntroState(final String data) {
        this.updateInfoQuest(22013, data);
    }
    
    public final void Aran_Start() {
        this.c.sendPacket(UIPacket.Aran_Start());
    }
    
    public final void evanTutorial(final String data, final int v1) {
        this.c.sendPacket(MaplePacketCreator.getEvanTutorial(data));
    }
    
    public final void AranTutInstructionalBubble(final String data) {
        this.c.sendPacket(UIPacket.AranTutInstructionalBalloon(data));
    }
    
    public final void ShowWZEffect(final String data) {
        this.c.sendPacket(UIPacket.AranTutInstructionalBalloon(data));
    }
    
    public final void showWZEffect(final String data, final int info) {
        this.c.sendPacket(UIPacket.ShowWZEffect(data, info));
    }
    
    public final void EarnTitleMsg(final String data) {
        this.c.sendPacket(UIPacket.EarnTitleMsg(data));
    }
    
    public final void MovieClipIntroUI(final boolean enabled) {
        this.c.sendPacket(UIPacket.IntroDisableUI(enabled));
        this.c.sendPacket(UIPacket.IntroLock(enabled));
    }
    
    public MapleInventoryType getInvType(final int i) {
        return MapleInventoryType.getByType((byte)i);
    }
    
    public String getItemName(final int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }
    
    public void gainPet(int id, String name, int level, int closeness, int fullness, final long period) {
        if (id > 5010000 || id < 5000000) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        name = this.getItemName(id);
        try {
            MapleInventoryManipulator.addById(this.c, id, (short)1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), (id == 5000054) ? ((int)period) : 0), period, (byte)0);
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removeSlot(final int invType, final byte slot, final short quantity) {
        MapleInventoryManipulator.removeFromSlot(this.c, this.getInvType(invType), slot, quantity, true);
    }
    
    public void gainGP(final int gp) {
        if (this.getPlayer().getGuildId() <= 0) {
            return;
        }
        Guild.gainGP(this.getPlayer().getGuildId(), gp);
    }
    
    public int getGP() {
        if (this.getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return Guild.getGP(this.getPlayer().getGuildId());
    }
    
    public void showMapEffect(final String path) {
        this.getClient().getSession().write(UIPacket.MapEff(path));
    }
    
    public int itemQuantity(final int itemid) {
        return this.getPlayer().itemQuantity(itemid);
    }
    
    public EventInstanceManager getDisconnected(final String event) {
        final EventManager em = this.getEventManager(event);
        if (em == null) {
            return null;
        }
        for (final EventInstanceManager eim : em.getInstances()) {
            if (eim.isDisconnected(this.c.getPlayer()) && eim.getPlayerCount() > 0) {
                return eim;
            }
        }
        return null;
    }
    
    public boolean isAllReactorState(final int reactorId, final int state) {
        boolean ret = false;
        for (final MapleReactor r : this.getMap().getAllReactorsThreadsafe()) {
            if (r.getReactorId() == reactorId) {
                ret = (r.getState() == state);
            }
        }
        return ret;
    }
    
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
    
    public void spawnMonster(final int id) {
        this.spawnMonster(id, 1, new Point(this.getPlayer().getPosition()));
    }
    
    public void spawnMonster(final int id, final int x, final int y) {
        this.spawnMonster(id, 1, new Point(x, y));
    }
    
    public void spawnMonster(final int id, final int qty, final int x, final int y) {
        this.spawnMonster(id, qty, new Point(x, y));
    }
    
    public void spawnMonster(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; ++i) {
            this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }
    
    public void sendNPCText(final String text, final int npc) {
        this.getMap().broadcastMessage(MaplePacketCreator.getNPCTalk(npc, (byte)0, text, "00 00", (byte)0));
    }
    
    public boolean getTempFlag(final int flag) {
        return (this.c.getChannelServer().getTempFlag() & flag) == flag;
    }
    
    public int getGamePoints() {
        return this.c.getPlayer().getGamePoints();
    }
    
    public void gainGamePoints(final int amount) {
        this.c.getPlayer().gainGamePoints(amount);
    }
    
    public void resetGamePoints() {
        this.c.getPlayer().resetGamePoints();
    }
    
    public int getGamePointsPD() {
        return this.c.getPlayer().getGamePointsPD();
    }
    
    public void gainGamePointsPD(final int amount) {
        this.c.getPlayer().gainGamePointsPD(amount);
    }
    
    public void resetGamePointsPD() {
        this.c.getPlayer().resetGamePointsPD();
    }
    
    public int getPS() {
        return this.c.getPlayer().getGamePointsPS();
    }
    
    public void gainPS(final int amount) {
        this.c.getPlayer().gainGamePointsPS(amount);
    }
    
    public void resetPS() {
        this.c.getPlayer().resetGamePointsPS();
    }
    
    public boolean beibao(final int A) {
        return (this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && A == 1) || (this.c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && A == 2) || (this.c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && A == 3) || (this.c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1 && A == 4) || (this.c.getPlayer().getInventory(MapleInventoryType.CASH).getNextFreeSlot() > -1 && A == 5);
    }
    
    public boolean beibao(final int A, final int kw) {
        return (this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > kw && A == 1) || (this.c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > kw && A == 2) || (this.c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > kw && A == 3) || (this.c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > kw && A == 4) || (this.c.getPlayer().getInventory(MapleInventoryType.CASH).getNextFreeSlot() > kw && A == 5);
    }
    
    public final void startAriantPQ(final int mapid) {
        for (final MapleCharacter chr : this.c.getPlayer().getMap().getCharacters()) {
            chr.updateAriantScore();
            chr.changeMap(mapid);
            this.c.getPlayer().getMap().resetAriantPQ(this.c.getPlayer().getAverageMapLevel());
            chr.getClient().getSession().write(MaplePacketCreator.getClock(480));
            chr.dropMessage(5, "建议把你的小地图忘下移动，来查看排名.");
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    chr.updateAriantScore();
                }
            }, 800L);
            EtcTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    chr.getClient().getSession().write(MaplePacketCreator.showAriantScoreBoard());
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            chr.changeMap(980010010);
                            chr.resetAriantScore();
                        }
                    }, 9000L);
                }
            }, 480000L);
        }
    }
    
    public int getBossLog(final String bossid) {
        return this.getPlayer().getBossLog(bossid);
    }
    
    public void setBossLog(final String bossid) {
        this.getPlayer().setBossLog(bossid);
    }
    
    public final void givePartyBossLog(final String bossid) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.setBossLog(bossid);
            return;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.setBossLog(bossid);
            }
        }
    }
    
    public final boolean getPartyBossLog(final String bossid, final int num) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            final int bossnum = this.getBossLog(bossid);
            return bossnum <= num;
        }
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                final int bossnum2 = curChar.getBossLog(bossid);
                if (bossnum2 > num) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public int getBosslog(final String bossid) {
        return this.getPlayer().getBossLog(bossid);
    }
    
    public void setBosslog(final String bossid) {
        this.getPlayer().setBossLog(bossid);
    }
    
    public int getOneTimeLog(final String bossid) {
        return this.getPlayer().getOneTimeLog(bossid);
    }
    
    public void setOneTimeLog(final String bossid) {
        this.getPlayer().setOneTimeLog(bossid);
    }
    
    public void resetAp() {
        final boolean beginner = this.getPlayer().getJob() == 0 || this.getPlayer().getJob() == 1000 || this.getPlayer().getJob() == 2001;
        this.getPlayer().resetStatsByJob(beginner);
    }
    
    public void openWeb(final String web) {
        this.c.sendPacket(MaplePacketCreator.openWeb(web));
    }
    
    public int getJobId() {
        return this.c.getPlayer().getJob();
    }
    
    public int getLevel() {
        return this.c.getPlayer().getLevel();
    }
    
    public String getJobName(final int jobId) {
        return MapleCarnivalChallenge.getJobNameById(jobId);
    }
    
    public boolean isBeginnerJob() {
        return GameConstants.is新手职业(this.getJobId()) && this.getLevel() < 11;
    }
    
    public int get输出log(final String bossid) {
        return this.c.getPlayer().get输出Log(bossid);
    }
    
    public int get输出log(final String bossid, final int days) {
        return this.c.getPlayer().get输出Log(bossid, days);
    }
    
    public void setbosslog(final String bossid) {
        this.c.getPlayer().setBossLog(bossid);
    }
    
    public void delbosslog(final String boss) {
        this.c.getPlayer().reset输出Log(boss);
    }
    
    public int get输出Log(final String bossid) {
        return this.c.getPlayer().get输出Log(bossid);
    }
    
    public int get输出Log(final String bossid, final int type) {
        return this.c.getPlayer().get输出Log(bossid, type);
    }
    
    public void set输出Log(final String bossid) {
        this.c.getPlayer().set输出Log(bossid);
    }
    
    public void set输出Log(final String bossid, final int type) {
        this.c.getPlayer().set输出Log(bossid, type);
    }
    
    public void set输出Log(final String bossid, final int type, final int count) {
        this.c.getPlayer().set输出Log(bossid, type, count);
    }
    
    public void reset输出Log(final String bossid) {
        this.c.getPlayer().reset输出Log(bossid);
    }
    
    public void reset输出Log(final String bossid, final int type) {
        this.c.getPlayer().reset输出Log(bossid, type);
    }
    
    public List<MapleItemType> getDropsTypeName(final int type) {
        final List<MapleItemType> ret = new ArrayList<MapleItemType>();
        final Map<MapleItemType, Map<Integer, Set<Integer>>> dropsFromMonstersMap = ServerManager.getInstance().getDropsFromMonsters();
        if (dropsFromMonstersMap == null || dropsFromMonstersMap.isEmpty()) {
            return null;
        }
        final Set<MapleItemType> typeList = dropsFromMonstersMap.keySet();
        switch (type) {
            case 1: {
                for (final MapleItemType mapleItemType : typeList) {
                    if (mapleItemType != null && mapleItemType.is装备()) {
                        ret.add(mapleItemType);
                    }
                }
                break;
            }
            case 2: {
                for (final MapleItemType mapleItemType : typeList) {
                    if (mapleItemType != null && mapleItemType.is消耗()) {
                        ret.add(mapleItemType);
                    }
                }
                break;
            }
            case 3: {
                for (final MapleItemType mapleItemType : typeList) {
                    if (mapleItemType != null && mapleItemType.is道具()) {
                        ret.add(mapleItemType);
                    }
                }
                break;
            }
        }
        if (ret.size() > 0) {
            Collections.sort(ret);
        }
        return ret;
    }
    
    public List<Integer> getMapMobsIds() {
        final List<Integer> mobs = new ArrayList<Integer>();
        final Set<Integer> tmobs = new HashSet<Integer>();
        final MapleMap map = this.c.getPlayer().getMap();
        final double range = Double.POSITIVE_INFINITY;
        final List<MapleMapObject> monsters = map.getMapObjectsInRange(this.c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
        for (final MapleMapObject monstermo : map.getMapObjectsInRange(this.c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            final MapleMonster monster = (MapleMonster)monstermo;
            tmobs.add(monster.getId());
        }
        for (final Integer id : tmobs) {
            mobs.add(id);
        }
        Collections.sort(mobs);
        return mobs;
    }
    
    public List<Integer> getMapMobDropsIds(final int mobId) {
        final List<Integer> items = new ArrayList<Integer>();
        final Set<Integer> titems = new HashSet<Integer>();
        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<DropEntry> dropEntrys = mi.retrieveDropChances(mobId);
        for (final DropEntry dropEntry : dropEntrys) {
            titems.add(dropEntry.itemid);
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (final Integer id : titems) {
            if (ii.getName(id) != null) {
                items.add(id);
            }
        }
        Collections.sort(items);
        return items;
    }
    
    public List<Integer> getDropItemsIdByItemType(final MapleItemType type) {
        if (type == null) {
            return null;
        }
        final List<Integer> ret = new ArrayList<Integer>();
        final Map<MapleItemType, Map<Integer, Set<Integer>>> dropsFromMonstersMap = ServerManager.getInstance().getDropsFromMonsters();
        if (dropsFromMonstersMap == null || dropsFromMonstersMap.size() < 1) {
            return null;
        }
        if (dropsFromMonstersMap.get(type) != null && dropsFromMonstersMap.get(type).keySet() != null) {
            final Set<Integer> items = dropsFromMonstersMap.get(type).keySet();
            for (final Integer itemId : items) {
                ret.add(itemId);
            }
        }
        if (!ret.isEmpty()) {
            Collections.sort(ret);
        }
        return ret;
    }
    
    public List<Integer> getDropsFromMonsters(final MapleItemType type, final int itemId) {
        final List<Integer> ret = new ArrayList<Integer>();
        final Map<MapleItemType, Map<Integer, Set<Integer>>> dropsFromMonstersMap = ServerManager.getInstance().getDropsFromMonsters();
        if (dropsFromMonstersMap == null || dropsFromMonstersMap.size() < 1) {
            return null;
        }
        if (dropsFromMonstersMap.get(type) != null && dropsFromMonstersMap.get(type).get(itemId) != null) {
            for (final Integer mobId : dropsFromMonstersMap.get(type).get(itemId)) {
                ret.add(mobId);
            }
        }
        if (ret.size() > 0) {
            Collections.sort(ret);
        }
        return ret;
    }
}
