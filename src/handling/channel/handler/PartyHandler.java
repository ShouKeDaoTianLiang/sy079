package handling.channel.handler;

import tools.StringUtil;
import server.maps.Event_DojoAgent;
import constants.GameConstants;
import client.MapleCharacter;
import handling.world.MapleParty;
import tools.MaplePacketCreator;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World.Party;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class PartyHandler
{
    public static final void DenyPartyRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int action = slea.readByte();
        final int partyid = slea.readInt();
        if (c.getPlayer().getParty() == null) {
            final MapleParty party = Party.getParty(partyid);
            if (party != null) {
                if (action == 27) {
                    if (party.getMembers().size() < 6) {
                        Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    }
                    else {
                        c.sendPacket(MaplePacketCreator.partyStatusMessage(17));
                    }
                }
                else if (action != 22) {
                    final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                    if (cfrom != null) {
                        cfrom.getClient().getSession().write(MaplePacketCreator.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
            }
            else {
                c.getPlayer().dropMessage(5, "要参加的队伍不存在。");
            }
        }
        else {
            c.getPlayer().dropMessage(5, "您已经有一个组队，无法加入其他组队!");
        }
    }
    
    public static final void PartyOperatopn(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int operation = slea.readByte();
        MapleParty party = c.getPlayer().getParty();
        final MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
        switch (operation) {
            case 1: {
                if (party == null) {
                    party = Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.sendPacket(MaplePacketCreator.partyCreated(party.getId()));
                    break;
                }
                if (partyplayer.equals(party.getLeader()) && party.getMembers().size() == 1) {
                    c.sendPacket(MaplePacketCreator.partyCreated(party.getId()));
                    break;
                }
                c.getPlayer().dropMessage(5, "你已经有一个队伍了，无法再次创建！");
                break;
            }
            case 2: {
                if (party != null) {
                    if (partyplayer.equals(party.getLeader())) {
                        if (GameConstants.is武陵道场(c.getPlayer().getMapId())) {
                            Event_DojoAgent.道场任务失败(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    }
                    else {
                        if (GameConstants.is武陵道场(c.getPlayer().getMapId())) {
                            Event_DojoAgent.道场任务失败(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                        }
                        Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                    break;
                }
                break;
            }
            case 3: {
                final int 队伍编号 = slea.readInt();
                if (party != null) {
                    c.getPlayer().dropMessage(5, "您已经有一个组队，无法加入其他组队!");
                    break;
                }
                party = Party.getParty(队伍编号);
                if (party == null) {
                    c.getPlayer().dropMessage(5, "要加入的队伍不存在");
                    break;
                }
                if (party.getMembers().size() < 6) {
                    c.getPlayer().setParty(party);
                    Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                    c.getPlayer().receivePartyMemberHP();
                    c.getPlayer().updatePartyMemberHP();
                    break;
                }
                c.getPlayer().dropMessage(5, "队伍成员已满");
                break;
            }
            case 4: {
                final String 玩家名字 = slea.readMapleAsciiString();
                final MapleCharacter 邀请 = c.getChannelServer().getPlayerStorage().getCharacterByName(玩家名字);
                if (邀请 == null) {
                    c.getPlayer().dropMessage(5, "找不到'" + 玩家名字 + "");
                    break;
                }
                if (邀请.getParty() != null) {
                    c.getPlayer().dropMessage(5, "'" + 玩家名字 + "'已经加入其他组。");
                    break;
                }
                if (party.getMembers().size() < 6) {
                    c.getPlayer().dropMessage(5, "向" + 邀请.getName() + "发送了组队邀请。");
                    邀请.getClient().getSession().write(MaplePacketCreator.partyInvite(c.getPlayer()));
                    break;
                }
                c.getPlayer().dropMessage(5, "组队成员已满");
                break;
            }
            case 5: {
                final int 被驱逐的玩家ID = slea.readInt();
                if (partyplayer.equals(party.getLeader())) {
                    final MaplePartyCharacter 驱逐队员 = party.getMemberById(被驱逐的玩家ID);
                    if (驱逐队员 != null) {
                        Party.updateParty(party.getId(), PartyOperation.EXPEL, 驱逐队员);
                        if (c.getPlayer().getEventInstance() != null && 驱逐队员.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        if (c.getPlayer().getPyramidSubway() != null && 驱逐队员.isOnline()) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                    }
                    break;
                }
                break;
            }
            case 6: {
                final int 编号ID = slea.readInt();
                final MaplePartyCharacter 新的队长 = party.getMemberById(编号ID);
                if (partyplayer.equals(party.getLeader()) && 新的队长 != null) {
                    party.setLeader(新的队长);
                    Party.updateParty(party.getId(), PartyOperation.SILENT_UPDATE, 新的队长);
                    break;
                }
                break;
            }
            default: {
                if (c.getPlayer().isAdmin()) {
                    System.out.println("未知的队伍操作 : 0x" + StringUtil.getLeftPaddedStr(Integer.toHexString(operation).toUpperCase(), '0', 2) + " " + slea);
                    break;
                }
                break;
            }
        }
    }
}
