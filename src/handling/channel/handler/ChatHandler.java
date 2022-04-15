package handling.channel.handler;

import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import handling.world.World.Alliance;
import handling.world.World.Buddy;
import handling.world.World.Find;
import handling.world.World.Guild;
import handling.world.World.Messenger;
import handling.world.World.Party;
import server.maps.MapleMap;
import tools.FileoutputUtil;
import tools.FileoutputUtil1;
import tools.MaplePacketCreator;
import client.messages.CommandProcessor;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants.CommandType;

public class ChatHandler
{
    public static final void GeneralChat(final String text, final byte unk, final MapleClient c, final MapleCharacter chr) {
        if (chr != null && !CommandProcessor.processCommand(c, text, CommandType.NORMAL)) {
            if (!chr.isGM() && text.length() >= 80) {
                return;
            }
            if (chr.getCanTalk() || chr.isStaff()) {
                final MapleMap map = c.getPlayer().getMap();
                String gg = "称号";
                if (c.getPlayer().isGM()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                    return;
                }
                if (chr.getLevel() >= 300) {
                    if (c.getPlayer().getCTitle()) {
                        gg = c.getPlayer().getChatTitle();
                    }
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.multiChat("『" + gg + "』" + c.getPlayer().getName(), text, 2));
                    map.broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
                }
                else if (!c.getPlayer().isGM()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getPosition());
                    final StringBuilder sb = new StringBuilder("[普通聊天偷听] 玩家：" + chr.getName() + " 地图：" + chr.getMapId() + "：  " + text);
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr_.get玩家私聊1() && chr_.isGM()) {
                                chr_.dropMessage(sb.toString());
                            }
                        }
                    }
                }
                else {
                    map.broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                }
            }
            else {
                c.sendPacket(MaplePacketCreator.serverNotice(6, "在這個地方不能說話。"));
            }
        }
    }
    
    public static final void Others(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        final int[] recipients = new int[numRecipients];
        for (byte i = 0; i < numRecipients; ++i) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.sendPacket(MaplePacketCreator.serverNotice(6, "你已经被禁言，因此无法说话."));
            return;
        }
        if (CommandProcessor.processCommand(c, chattext, CommandType.NORMAL)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0: {
                Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                final StringBuilder sb = new StringBuilder("[好友聊天偷听] 玩家：" + chr.getName() + " 地图：" + chr.getMapId() + " ：  " + chattext);
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                        if (chr_.get玩家私聊2() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            }
            case 1: {
                if (chr.getParty() == null) {
                    break;
                }
                Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                final StringBuilder sb = new StringBuilder("[組隊聊天偷听] 玩家：" + chr.getName() + " 地图：" + chr.getMapId() + " ：  " + chattext);
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊2() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            }
            case 2: {
                if (chr.getGuildId() <= 0) {
                    break;
                }
                Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                final StringBuilder sb = new StringBuilder("[公會聊天偷听] 公會：" + chr.getGuild().getName() + "玩家：" + chr.getName() + " 地图：" + chr.getMapId() + " ：  " + chattext);
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊3() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            }
            case 3: {
                if (chr.getGuildId() <= 0) {
                    break;
                }
                Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                final StringBuilder sb = new StringBuilder("[聯盟聊天偷听] 公會：" + chr.getGuild().getName() + " 玩家：" + chr.getName() + " 地图：" + chr.getMapId() + " ：  " + chattext);
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr_.get玩家私聊3() && chr_.isGM()) {
                            chr_.dropMessage(sb.toString());
                        }
                    }
                }
                break;
            }
        }
    }
    
    public static final void Messenger(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        MapleMessenger messenger = c.getPlayer().getMessenger();
        switch (slea.readByte()) {
            case 0: {
                if (messenger == null) {
                    final int messengerid = slea.readInt();
                    if (messengerid == 0) {
                        c.getPlayer().setMessenger(Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    }
                    else {
                        messenger = Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                    break;
                }
                break;
            }
            case 3: {
                if (messenger == null) {
                    break;
                }
                final int position2 = messenger.getLowestPosition();
                if (position2 <= -1 || position2 >= 4) {
                    return;
                }
                final String input = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
                if (target != null) {
                    if (target.getMessenger() == null) {
                        if (!target.isGM() || c.getPlayer().isGM()) {
                            c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 1));
                            target.getClient().getSession().write(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                        }
                        else {
                            c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                        }
                    }
                    else {
                        c.sendPacket(MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + "已经是使用枫叶信使."));
                    }
                }
                else if (World.isConnected(input)) {
                    Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGM());
                }
                else {
                    c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                }
                break;
            }
            case 5: {
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) {
                    if (target.getMessenger() != null) {
                        target.getClient().getSession().write(MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                        break;
                    }
                    break;
                }
                else {
                    if (!c.getPlayer().isGM()) {
                        Messenger.declineChat(targeted, c.getPlayer().getName());
                        break;
                    }
                    break;
                }
            }
            case 6: {
                if (messenger != null) {
                    Messenger.messengerChat(messenger.getId(), slea.readMapleAsciiString(), c.getPlayer().getName());
                    break;
                }
                break;
            }
        }
    }
    
    public static final void Whisper_Find(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        switch (mode) {
            case 5:
            case 68: {
                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player == null) {
                    final int ch = Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }
                        if (player != null) {
                            if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                                c.sendPacket(MaplePacketCreator.getFindReply(recipient, (byte)ch, mode == 68));
                            }
                            else {
                                c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                            }
                            return;
                        }
                    }
                    switch (ch) {
                        case -10: {
                            c.sendPacket(MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                            break;
                        }
                        case -20: {
                            c.sendPacket(MaplePacketCreator.getFindReplyWithMTS(recipient, mode == 68));
                            break;
                        }
                        default: {
                            c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                            break;
                        }
                    }
                    break;
                }
                if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                    c.sendPacket(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    break;
                }
                c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                break;
            }
            case 6: {
                if (!c.getPlayer().getCanTalk()) {
                    c.sendPacket(MaplePacketCreator.serverNotice(6, "你已经被禁言，因此无法说话."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = Find.findChannel(recipient);
                if (ch > 0) {
                    final MapleCharacter player2 = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player2 == null) {
                        break;
                    }
                    player2.getClient().getSession().write(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    final StringBuilder sb = new StringBuilder("[密語聊天偷听] 玩家：" + c.getPlayer().getName() + " -> " + player2.getName() + " ：" + text);
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        for (final MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr_.get玩家私聊2() && chr_.isGM()) {
                                chr_.dropMessage(sb.toString());
                            }
                        }
                    }
                    if (!c.getPlayer().isGM() && player2.isGM()) {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                    }
                    else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)1));
                    }
                }
                else {
                    c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                }
                FileoutputUtil1.玩家谈话("" + c.getPlayer().getName() + "私聊对话.txt", "玩家: " + c.getPlayer().getName() + " 对玩家: " + recipient + " 使用悄悄话说: " + text + "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "");
                break;
            }
        }
    }
}
