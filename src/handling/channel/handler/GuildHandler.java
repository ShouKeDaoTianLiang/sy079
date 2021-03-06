package handling.channel.handler;

import java.util.LinkedList;
import handling.world.guild.MapleGuildResponse;
import java.util.Iterator;
import handling.MaplePacket;
import handling.world.World.Alliance;
import handling.world.guild.MapleGuild;
import handling.world.World.Guild;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleCharacter;
import tools.MaplePacketCreator;
import client.MapleClient;
import java.util.List;

public class GuildHandler
{
    private static final List<Invited> invited;
    private static long nextPruneTime;
    
    public static final void DenyGuildRequest(final String from, final MapleClient c) {
        final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null) {
            cfrom.getClient().getSession().write(MaplePacketCreator.denyGuildInvitation(c.getPlayer().getName()));
        }
    }
    
    private static final boolean isGuildNameAcceptable(final String name) {
        return name.length() <= 15 && name.length() >= 3;
    }
    
    private static final void respawnPlayer(final MapleCharacter mc) {
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(mc.getId(), mc), false);
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
    }
    
    public static final void Guild(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (System.currentTimeMillis() >= GuildHandler.nextPruneTime) {
            final Iterator<Invited> itr = GuildHandler.invited.iterator();
            while (itr.hasNext()) {
                final Invited inv = itr.next();
                if (System.currentTimeMillis() >= inv.expiration) {
                    itr.remove();
                }
            }
            GuildHandler.nextPruneTime = System.currentTimeMillis() + 1200000L;
        }
        switch (slea.readByte()) {
            case 2: {
                if (c.getPlayer().getGuildId() > 0 || c.getPlayer().getMapId() != 200000301) {
                    c.getPlayer().dropMessage(1, "????????????????????????????????????.");
                    return;
                }
                if (c.getPlayer().getMeso() < 15000000) {
                    c.getPlayer().dropMessage(1, "???????????????????????????????????????");
                    return;
                }
                final String guildName = slea.readMapleAsciiString();
                if (!isGuildNameAcceptable(guildName)) {
                    c.getPlayer().dropMessage(1, "????????????????????????????????????.");
                    return;
                }
                final int guildId = Guild.createGuild(c.getPlayer().getId(), guildName);
                if (guildId == 0) {
                    c.sendPacket(MaplePacketCreator.genericGuildMessage((byte)28));
                    return;
                }
                c.getPlayer().gainMeso(-15000000, true, false, true);
                c.getPlayer().setGuildId(guildId);
                c.getPlayer().setGuildRank((byte)1);
                c.getPlayer().saveGuildStatus();
                c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                c.getPlayer().dropMessage(1, "?????????????????????????????????.");
                respawnPlayer(c.getPlayer());
                break;
            }
            case 5: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                final String name = slea.readMapleAsciiString();
                final MapleGuildResponse mgr = MapleGuild.sendInvite(c, name);
                if (mgr != null) {
                    c.sendPacket(mgr.getPacket());
                    break;
                }
                final Invited inv2 = new Invited(name, c.getPlayer().getGuildId());
                if (!GuildHandler.invited.contains(inv2)) {
                    GuildHandler.invited.add(inv2);
                }
                break;
            }
            case 6: {
                if (c.getPlayer().getGuildId() > 0) {
                    return;
                }
                final int guildId = slea.readInt();
                final int cid = slea.readInt();
                if (cid != c.getPlayer().getId()) {
                    return;
                }
                final String name = c.getPlayer().getName().toLowerCase();
                final Iterator<Invited> itr2 = GuildHandler.invited.iterator();
                while (itr2.hasNext()) {
                    final Invited inv3 = itr2.next();
                    if (guildId == inv3.gid && name.equals(inv3.name)) {
                        c.getPlayer().setGuildId(guildId);
                        c.getPlayer().setGuildRank((byte)5);
                        itr2.remove();
                        final int s = Guild.addGuildMember(c.getPlayer().getMGC());
                        if (s == 0) {
                            c.getPlayer().dropMessage(1, "???????????????????????????????????????.");
                            c.getPlayer().setGuildId(0);
                            return;
                        }
                        c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                        final MapleGuild gs = Guild.getGuild(guildId);
                        for (final MaplePacket pack : Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                        c.getPlayer().saveGuildStatus();
                        respawnPlayer(c.getPlayer());
                        break;
                    }
                }
                break;
            }
            case 7: {
                final int cid = slea.readInt();
                final String name = slea.readMapleAsciiString();
                if (cid != c.getPlayer().getId() || !name.equals(c.getPlayer().getName()) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                Guild.leaveGuild(c.getPlayer().getMGC());
                c.sendPacket(MaplePacketCreator.showGuildInfo(null));
                c.sendPacket(MaplePacketCreator.fuckGuildInfo(c.getPlayer()));
                break;
            }
            case 8: {
                final int cid = slea.readInt();
                final String name = slea.readMapleAsciiString();
                if (c.getPlayer().getGuildRank() > 2 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                Guild.expelMember(c.getPlayer().getMGC(), name, cid);
                break;
            }
            case 13: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }
                final String[] ranks = new String[5];
                for (int i = 0; i < 5; ++i) {
                    ranks[i] = slea.readMapleAsciiString();
                }
                Guild.changeRankTitle(c.getPlayer().getGuildId(), ranks);
                break;
            }
            case 14: {
                final int cid = slea.readInt();
                final byte newRank = slea.readByte();
                if (newRank <= 1 || newRank > 5 || c.getPlayer().getGuildRank() > 2 || (newRank <= 2 && c.getPlayer().getGuildRank() != 1) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                Guild.changeRank(c.getPlayer().getGuildId(), cid, newRank);
                break;
            }
            case 15: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1 || c.getPlayer().getMapId() != 200000301) {
                    return;
                }
                if (c.getPlayer().getMeso() < 5000000) {
                    c.getPlayer().dropMessage(1, "?????????????????????????????????????????????");
                    return;
                }
                final short bg = slea.readShort();
                final byte bgcolor = slea.readByte();
                final short logo = slea.readShort();
                final byte logocolor = slea.readByte();
                Guild.setGuildEmblem(c.getPlayer().getGuildId(), bg, bgcolor, logo, logocolor);
                c.getPlayer().gainMeso(-5000000, true, false, true);
                respawnPlayer(c.getPlayer());
                break;
            }
            case 16: {
                final String notice = slea.readMapleAsciiString();
                if (notice.length() > 100 || c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                Guild.setGuildNotice(c.getPlayer().getGuildId(), notice);
                break;
            }
        }
    }
    
    static {
        invited = new LinkedList<Invited>();
        GuildHandler.nextPruneTime = System.currentTimeMillis() + 1200000L;
    }
    
    private static final class Invited
    {
        public String name;
        public int gid;
        public long expiration;
        
        public Invited(final String n, final int id) {
            this.name = n.toLowerCase();
            this.gid = id;
            this.expiration = System.currentTimeMillis() + 3600000L;
        }
        
        @Override
        public final boolean equals(final Object other) {
            if (!(other instanceof Invited)) {
                return false;
            }
            final Invited oth = (Invited)other;
            return this.gid == oth.gid && this.name.equals(oth.name);
        }
    }
}
