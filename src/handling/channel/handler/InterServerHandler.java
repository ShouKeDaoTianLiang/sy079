package handling.channel.handler;

import client.ISkill;
import tools.data.input.SeekableLittleEndianAccessor;
import java.util.Iterator;
import java.util.List;
import handling.world.guild.MapleGuild;
import handling.world.MapleMessenger;
import handling.world.CharacterIdChannelPair;
import java.util.Collection;
import tools.Triple;
import handling.world.World.Broadcast;
import server.ServerProperties;
import client.BuddyEntry;
import client.MapleQuestStatus;
import tools.packet.FamilyPacket;
import handling.world.World.Family;
import handling.MaplePacket;
import handling.world.World.Alliance;
import handling.world.World.Guild;
import handling.world.World.Find;
import handling.world.World.Party;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World.Buddy;
import java.util.Optional;
import client.SkillFactory;
import handling.world.World.Client;
import tools.FileoutputUtil;
import handling.login.LoginServer;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import handling.cashshop.CashShopServer;
import java.net.InetAddress;
import handling.world.World;
import handling.world.CharacterTransfer;
import handling.world.PlayerBuffStorage;
import handling.world.World.Messenger;
import handling.world.MapleMessengerCharacter;
import handling.channel.ChannelServer;
import client.MapleBuffStat;
import tools.MaplePacketCreator;
import scripting.NPCScriptManager;
import constants.ServerConstants;
import client.MapleCharacter;
import client.MapleClient;

public class InterServerHandler
{
    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        try {
            if (!mts && ServerConstants.getCSNpcID() != 0) {
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, ServerConstants.getCSNpcID());
                c.announce(MaplePacketCreator.enableActions());
                return;
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SUMMON) != null) {
                c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            }
            c.getPlayer().saveToDB(false, false);
            final String[] socket = c.getChannelServer().getIP().split(":");
            final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
            chr.changeRemoval();
            if (chr.getMessenger() != null) {
                final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }
            PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
            PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
            ch.removePlayer(chr);
            c.updateLoginState(6, c.getSessionIPAddress());
            c.sendPacket(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(CashShopServer.getIP().split(":")[1])));
            chr.saveToDB(false, false);
            chr.getMap().removePlayer(chr);
            c.getPlayer().expirationTask(true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(InterServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static final void EnterMTS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        final String[] socket = c.getChannelServer().getIP().split(":");
        if (c.getPlayer().getTrade() != null) {
            c.getPlayer().dropMessage(1, "交易中无法进行其他操作！");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!chr.isGM() || chr.isGM()) {
            NPCScriptManager.getInstance().start(c, 9900004);
            c.getSession().write(MaplePacketCreator.enableActions());
        }
        else {
            try {
                final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
                chr.changeRemoval();
                if (chr.getMessenger() != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                    Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
                }
                PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
                PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
                PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
                World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
                ch.removePlayer(chr);
                c.updateLoginState(6, c.getSessionIPAddress());
                c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(CashShopServer.getIP().split(":")[1])));
                chr.saveToDB(false, false);
                chr.getMap().removePlayer(chr);
                c.setPlayer(null);
                c.setReceiving(false);
            }
            catch (UnknownHostException ex) {
                Logger.getLogger(InterServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void Loggedin(final int playerid, final MapleClient c) {
        final ChannelServer channelServer = c.getChannelServer();
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);
        MapleCharacter player;
        if (transfer == null) {
            final Triple<String, String, Integer> ip = LoginServer.getLoginAuth(playerid);
            final String s = c.getSessionIPAddress();
            if (ip == null) {
                if ("/127.0.0.1".equals(ip) || ip != null) {
                    LoginServer.putLoginAuth(playerid, ip.left, ip.mid, ip.right);
                }
                FileoutputUtil.logToFile("日志/Logs/Log_DC.txt", "\r\n伺服器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
                c.getSession().close();
                return;
            }
            c.setTempIP(ip.mid);
            c.setChannel(ip.right);
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        }
        else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }
        final MapleClient oldClient = Client.getClient(player.getAccountID());
        if (oldClient != null) {
            oldClient.disconnect(true, false);
        }
        Client.addClient(player.getAccountID(), c);
        c.setPlayer(player);
        c.setAccID(player.getAccountID());
        ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == 1 || state == 6 || state == 0) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        if (!allowLogin) {
            System.err.println("允許登入 = false");
            c.getSession().close();
            System.err.println("斷線處理!");
            c.setPlayer(null);
            System.err.println("清除用戶端的角色記錄");
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        channelServer.addPlayer(player);
        c.getSession().write(MaplePacketCreator.getCharInfo(player));
        if (player.isIntern()) {
            final int[] array;
            final int[] ints = array = new int[] { 9001004 };
            for (final int skillid : array) {
                Optional.ofNullable(SkillFactory.getSkill(skillid)).ifPresent(skill -> skill.getEffect(1).applyTo(player));
            }
        }
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("[欢迎] 尊敬的管理员 " + c.getPlayer().getName() + " ,当前在线人数为: " + c.getPlayer().Lianjie());
        }
        c.getSession().write(MaplePacketCreator.temporaryStats_Reset());
        player.getMap().addPlayer(player);
        try {
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
            final Collection<Integer> buddyIds = player.getBuddylist().getBuddiesIds();
            Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                Party.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            final CharacterIdChannelPair[] multiBuddyFind;
            final CharacterIdChannelPair[] onlineBuddies = multiBuddyFind = Find.multiBuddyFind(player.getId(), buddyIds);
            for (final CharacterIdChannelPair onlineBuddy : multiBuddyFind) {
                final BuddyEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }
            if (player.getGuildId() > 0) {
                Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.sendPacket(MaplePacketCreator.showGuildInfo(player));
                final MapleGuild gs = Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<MaplePacket> packetList = Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (final MaplePacket pack : packetList) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                    }
                }
                else {
                    player.setGuildId(0);
                    player.setGuildRank((byte)5);
                    player.setAllianceRank((byte)5);
                    player.saveGuildStatus();
                }
            }
            if (player.getFamilyId() > 0) {
                Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.sendPacket(FamilyPacket.getFamilyInfo(player));
        }
        catch (Exception e) {
            FileoutputUtil.outputFileError("日志/Logs/Log_登录错误.txt", e);
        }
        c.getSession().write(FamilyPacket.getFamilyData());
        player.checkCopyItems();
        player.sendMacros();
        player.showNote();
        player.receivePartyMemberHP();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.updatePetEquip();
        player.expirationTask();
        player.baseSkills();
        c.sendPacket(MaplePacketCreator.getKeymap(player.getKeyLayout()));
        for (final MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.sendPacket(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        final BuddyEntry pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddyEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getCharacterId(), "ETC", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
            c.sendPacket(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        if (player.getJob() == 132) {
            player.checkBerserk();
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("world.破功", "false"))) {
            final int VIP = c.getPlayer().getVipczz();
            final int 突破石头数值 = Integer.parseInt(ServerProperties.getProperty("world.突破石增加伤害"));
            final long maxdamage = 199999 + VIP * 突破石头数值;
            final String mds = "当前您的攻击上限为： " + maxdamage + " (注:该上限与您使用突破石有关)";
            c.getPlayer().dropMessage(5, mds);
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("tms.玩家登录公告", "false")) && !c.messageOn()) {
            if (player.getGMLevel() == 0) {
                Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(11, c.getChannel(), "『登录公告』 : 玩家 " + c.getPlayer().getName() + "," + new StringBuilder().append(ServerProperties.getProperty("tms.登录公告")).toString()).getBytes());
            }
            c.setMessageToggle(1);
        }
        player.spawnClones();
        player.spawnSavedPets();
        c.sendPacket(MaplePacketCreator.showCharCash(c.getPlayer()));
        c.sendPacket(MaplePacketCreator.weirdStatUpdate());
        System.out.println("[岁月工作室][名字:" + c.getPlayer().getName() + "]通过游戏[" + channelServer.getChannel() + "]频道[等级:" + c.getPlayer().getLevel() + "][IP:" + player.getClient().getSession().getRemoteAddress().toString().split(":")[0] + "]");
    }
    
    public static final void ChangeChannel(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int chc = slea.readByte() + 1;
        chr.changeChannel(chc);
    }
}
