package server.events;

import handling.world.World.Broadcast;
import server.Timer.EventTimer;
import tools.MaplePacketCreator;
import server.maps.SavedLocationType;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import server.maps.MapleMap;

public abstract class MapleEvent
{
    protected int[] mapid;
    protected int channel;
    protected boolean isRunning;
    
    public MapleEvent(final int channel, final int[] mapid) {
        this.isRunning = false;
        this.channel = channel;
        this.mapid = mapid;
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public MapleMap getMap(final int i) {
        return this.getChannelServer().getMapFactory().getMap(this.mapid[i]);
    }
    
    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }
    
    public void broadcast(final MaplePacket packet) {
        for (int i = 0; i < this.mapid.length; ++i) {
            this.getMap(i).broadcastMessage(packet);
        }
    }
    
    public void givePrize(final MapleCharacter chr) {
        final int reward = RandomRewards.getInstance().getEventReward();
        switch (reward) {
            case 0: {
                chr.gainMeso(10000, true, false, false);
                chr.dropMessage(5, "你获得 10000 冒险币");
                break;
            }
            case 1: {
                chr.gainMeso(29999, true, false, false);
                chr.dropMessage(5, "你获得 29999 冒险币");
                break;
            }
            case 2: {
                chr.modifyCSPoints(1, 100, false);
                chr.dropMessage(5, "你获得 100 抵用卷卷");
                break;
            }
            case 3: {
                chr.addFame(1);
                chr.dropMessage(5, "你获得 1 人气");
                break;
            }
        }
        if (MapleInventoryManipulator.checkSpace(chr.getClient(), 4001126, 1, "")) {
            MapleInventoryManipulator.addById(chr.getClient(), 4001126, (short)1, (byte)0);
            chr.dropMessage(5, "你获得 1 个枫叶");
        }
        else {
            chr.gainMeso(10000, true, false, false);
            chr.dropMessage(5, "由于你背包满了。所以只能给予你冒险币！");
        }
    }
    
    public void finished(final MapleCharacter chr) {
    }
    
    public void onMapLoad(final MapleCharacter chr) {
    }
    
    public void startEvent() {
    }
    
    public void warpBack(final MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104000000;
        }
        final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }
    
    public void reset() {
        this.isRunning = true;
    }
    
    public void unreset() {
        this.isRunning = false;
    }
    
    public static final void setEvent(final ChannelServer cserv, final boolean auto) {
        if (auto) {
            for (final MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (e.isRunning) {
                    for (final int i : e.mapid) {
                        if (cserv.getEvent() == i) {
                            e.broadcast(MaplePacketCreator.serverNotice(0, "距离活动开始只剩下一分钟!"));
                            e.broadcast(MaplePacketCreator.getClock(60));
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    e.startEvent();
                                }
                            }, 60000L);
                            break;
                        }
                    }
                }
            }
        }
        cserv.setEvent(-1);
    }
    
    public static final void mapLoad(final MapleCharacter chr, final int channel) {
        if (chr == null) {
            return;
        }
        for (final MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
                if (chr.getMapId() == 109050000) {
                    e.finished(chr);
                }
                for (final int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.onMapLoad(chr);
                    }
                }
            }
        }
    }
    
    public static final void onStartEvent(final MapleCharacter chr) {
        for (final MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (e.isRunning) {
                for (final int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.startEvent();
                        chr.dropMessage(5, String.valueOf(t) + " 活动开始");
                    }
                }
            }
        }
    }
    
    public static final String scheduleEvent(final MapleEventType event, final ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "改活动已经被禁止安排了.";
        }
        for (final int i : cserv.getEvent(event).mapid) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
                return "该活动已经在执行中.";
            }
        }
        cserv.setEvent(cserv.getEvent(event).mapid[0]);
        cserv.getEvent(event).reset();
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场活动npc并进入！").getBytes());
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场活动npc并进入！").getBytes());
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场活动npc并进入！").getBytes());
        return "";
    }
}
