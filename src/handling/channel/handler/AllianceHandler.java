package handling.channel.handler;

import client.MapleCharacter;
import java.util.Iterator;
import handling.world.guild.MapleGuild;
import handling.MaplePacket;
import handling.world.World.Alliance;
import handling.world.World.Guild;
import tools.MaplePacketCreator;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class AllianceHandler
{
    public static final void HandleAlliance(final SeekableLittleEndianAccessor slea, final MapleClient c, boolean denied) {
        if (c.getPlayer().getGuildId() <= 0) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final MapleGuild gs = Guild.getGuild(c.getPlayer().getGuildId());
        if (gs == null) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        final byte op = slea.readByte();
        if (c.getPlayer().getGuildRank() != 1 && op != 1) {
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = Alliance.getAllianceLeader(gs.getAllianceId());
        }
        if (op != 4 && !denied) {
            if (gs.getAllianceId() <= 0 || leaderid <= 0) {
                return;
            }
        }
        else if (leaderid > 0 || gs.getAllianceId() > 0) {
            return;
        }
        if (denied) {
            DenyInvite(c, gs);
            return;
        }
        switch (op) {
            case 1: {
                for (final MaplePacket pack : Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack != null) {
                        c.sendPacket(pack);
                    }
                }
                break;
            }
            case 3: {
                final int newGuild = Guild.getGuildLeader(slea.readMapleAsciiString());
                if (newGuild <= 0 || c.getPlayer().getAllianceRank() != 1 || leaderid != c.getPlayer().getId()) {
                    break;
                }
                final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newGuild);
                if (chr != null && chr.getGuildId() > 0 && Alliance.canInvite(gs.getAllianceId())) {
                    chr.getClient().getSession().write(MaplePacketCreator.sendAllianceInvite(Alliance.getAlliance(gs.getAllianceId()).getName(), c.getPlayer()));
                    Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                    break;
                }
                break;
            }
            case 4: {
                final int inviteid = Guild.getInvitedId(c.getPlayer().getGuildId());
                if (inviteid > 0) {
                    if (!Alliance.addGuildToAlliance(inviteid, c.getPlayer().getGuildId())) {
                        c.getPlayer().dropMessage(5, "加入家族时出现错误.");
                    }
                    Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
                    break;
                }
                break;
            }
            case 2:
            case 6: {
                int gid;
                if (op == 6 && slea.available() >= 4L) {
                    gid = slea.readInt();
                    if (slea.available() >= 4L && gs.getAllianceId() != slea.readInt()) {
                        break;
                    }
                }
                else {
                    gid = c.getPlayer().getGuildId();
                }
                if (c.getPlayer().getAllianceRank() <= 2 && (c.getPlayer().getAllianceRank() == 1 || c.getPlayer().getGuildId() == gid) && !Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, c.getPlayer().getGuildId() !=  gid)) {
                    c.getPlayer().dropMessage(5, "删除家族时出现错误.");
                    break;
                }
                break;
            }
            case 7: {
                if (c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId() && !Alliance.changeAllianceLeader(gs.getAllianceId(), slea.readInt())) {
                    c.getPlayer().dropMessage(5, "更换族长时发生错误.");
                    break;
                }
                break;
            }
            case 8: {
                if (c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId()) {
                    final String[] ranks = new String[5];
                    for (int i = 0; i < 5; ++i) {
                        ranks[i] = slea.readMapleAsciiString();
                    }
                    Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                    break;
                }
                break;
            }
            case 9: {
                if (c.getPlayer().getAllianceRank() <= 2 && !Alliance.changeAllianceRank(gs.getAllianceId(), slea.readInt(), slea.readByte())) {
                    c.getPlayer().dropMessage(5, "更改等级时发生错误.");
                    break;
                }
                break;
            }
            case 10: {
                if (c.getPlayer().getAllianceRank() > 2) {
                    break;
                }
                final String notice = slea.readMapleAsciiString();
                if (notice.length() > 100) {
                    break;
                }
                Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                break;
            }
        }
    }
    
    public static final void DenyInvite(final MapleClient c, final MapleGuild gs) {
        final int inviteid = Guild.getInvitedId(c.getPlayer().getGuildId());
        if (inviteid > 0) {
            final int newAlliance = Alliance.getAllianceLeader(inviteid);
            if (newAlliance > 0) {
                final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newAlliance);
                if (chr != null) {
                    chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
                }
                Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
            }
        }
    }
}
