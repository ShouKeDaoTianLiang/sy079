package handling;

import java.util.Collection;
import java.util.Arrays;
import server.ServerProperties;
import handling.channel.handler.BeanGame;
import handling.channel.handler.FamilyHandler;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.DueyHandler;
import handling.channel.handler.MonsterCarnivalHandler;
import handling.channel.handler.PetHandler;
import handling.channel.handler.SummonHandler;
import handling.cashshop.handler.MTSOperation;
import server.MTSStorage;
import handling.channel.handler.UserInterfaceHandler;
import handling.channel.handler.BuddyListHandler;
import handling.channel.handler.PartyHandler;
import handling.channel.handler.BBSHandler;
import handling.channel.handler.AllianceHandler;
import handling.channel.handler.GuildHandler;
import handling.channel.handler.PlayerInteractionHandler;
import handling.channel.handler.StatsHandling;
import handling.channel.handler.ChatHandler;
import handling.channel.handler.NPCHandler;
import handling.channel.handler.MobHandler;
import handling.channel.handler.ItemMakerHandler;
import handling.channel.handler.PlayersHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.PlayerHandler;
import handling.cashshop.handler.CashShopOperation;
import handling.channel.handler.InterServerHandler;
import tools.data.input.LittleEndianAccessor;
import handling.login.handler.PacketErrorHandler;
import handling.login.handler.CharLoginHandler;
import java.util.concurrent.RejectedExecutionException;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.data.input.SeekableInputStreamBytestream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.ByteArrayByteStream;
import handling.world.World.Client;
import tools.packet.LoginPacket;
import io.netty.util.AttributeKey;
import handling.netty.MaplePacketDecoder;
import handling.netty.MaplePacketDecoder.DecoderState;
import handling.netty.MapleSession;
import tools.MapleAESOFB;
import constants.ServerConstants;
import server.Randomizer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;
import io.netty.channel.ChannelHandlerContext;
import java.util.Iterator;
import java.util.Scanner;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.io.FileWriter;
import java.util.HashMap;
import java.io.File;
import tools.Pair;
import java.util.Map;
import java.util.List;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MapleServerHandler extends ChannelInboundHandlerAdapter implements MapleServerHandlerMBean
{
    public static final boolean Log_Packets = true;
    private int channel;
    private boolean cs;
    private final List<String> BlockedIP;
    private final Map<String, Pair<Long, Byte>> tracker;
    private static final String nl;
    private static final File loggedIPs;
    private static final HashMap<String, FileWriter> logIPMap;
    private static boolean debugMode;
    private static final EnumSet<RecvPacketOpcode> blocked;
    private ServerType type;
    private static final int Log_Size = 10000;
    private static final ArrayList<LoggedPacket> Packet_Log;
    private static final ReentrantReadWriteLock Packet_Log_Lock;
    private static final File Packet_Log_Output;
    public static final int FARM_SERVER = -20;
    public static final int CASH_SHOP_SERVER = -10;
    public static final int LOGIN_SERVER = -1;
    
    public static void reloadLoggedIPs() {
        for (final FileWriter fw : MapleServerHandler.logIPMap.values()) {
            if (fw != null) {
                try {
                    fw.write("=== Closing Log ===");
                    fw.write(MapleServerHandler.nl);
                    fw.flush();
                    fw.close();
                }
                catch (IOException ex) {
                    System.out.println("Error closing Packet Log." + ex);
                }
            }
        }
        MapleServerHandler.logIPMap.clear();
        try {
            final Scanner sc = new Scanner(MapleServerHandler.loggedIPs);
            while (sc.hasNextLine()) {
                final String line = sc.nextLine().trim();
                if (line.length() > 0) {
                    final FileWriter fw2 = new FileWriter(new File("PacketLog_" + line + ".txt"), true);
                    fw2.write("=== Creating Log ===");
                    fw2.write(MapleServerHandler.nl);
                    fw2.flush();
                    MapleServerHandler.logIPMap.put(line, fw2);
                }
            }
        }
        catch (Exception ex2) {
            System.out.println("Could not reload packet logged IPs." + ex2);
        }
    }
    
    private static FileWriter isLoggedIP(final ChannelHandlerContext ctx) {
        final String a = ctx.channel().remoteAddress().toString();
        final String realIP = a.substring(a.indexOf(47) + 1, a.indexOf(58));
        return MapleServerHandler.logIPMap.get(realIP);
    }
    
    public static void log(final SeekableLittleEndianAccessor packet, final RecvPacketOpcode op, final MapleClient c, final ChannelHandlerContext ctx) {
        if (MapleServerHandler.blocked.contains(op)) {
            return;
        }
        try {
            MapleServerHandler.Packet_Log_Lock.writeLock().lock();
            LoggedPacket logged = null;
            if (MapleServerHandler.Packet_Log.size() == 10000) {
                logged = MapleServerHandler.Packet_Log.remove(0);
            }
            if (logged == null) {
                logged = new LoggedPacket(packet, op, ctx.channel().remoteAddress().toString(), (c == null) ? -1 : c.getAccID(), (c == null || c.getAccountName() == null) ? "[Null]" : c.getAccountName(), (c == null || c.getPlayer() == null || c.getPlayer().getName() == null) ? "[Null]" : c.getPlayer().getName());
            }
            else {
                logged.setInfo(packet, op, ctx.channel().remoteAddress().toString(), (c == null) ? -1 : c.getAccID(), (c == null || c.getAccountName() == null) ? "[Null]" : c.getAccountName(), (c == null || c.getPlayer() == null || c.getPlayer().getName() == null) ? "[Null]" : c.getPlayer().getName());
            }
            MapleServerHandler.Packet_Log.add(logged);
        }
        finally {
            MapleServerHandler.Packet_Log_Lock.writeLock().unlock();
        }
    }
    
    public static void registerMBean() {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            final MapleServerHandler mbean = new MapleServerHandler();
            mBeanServer.registerMBean(mbean, new ObjectName("handling:type=MapleServerHandler"));
        }
        catch (Exception e) {
            System.out.println("Error registering PacketLog MBean");
            e.printStackTrace();
        }
    }
    
    public void writeLog() {
        try {
            final FileWriter fw = new FileWriter(MapleServerHandler.Packet_Log_Output, true);
            try {
                MapleServerHandler.Packet_Log_Lock.readLock().lock();
                final String nl = System.getProperty("line.separator");
                for (final LoggedPacket loggedPacket : MapleServerHandler.Packet_Log) {
                    fw.write(loggedPacket.toString());
                    fw.write(nl);
                }
                fw.flush();
                fw.close();
            }
            finally {
                MapleServerHandler.Packet_Log_Lock.readLock().unlock();
            }
        }
        catch (IOException ex) {
            System.out.println("Error writing log to file.");
        }
    }
    
    public MapleServerHandler() {
        this.channel = -1;
        this.BlockedIP = new ArrayList<String>();
        this.tracker = new ConcurrentHashMap<String, Pair<Long, Byte>>();
        this.type = null;
    }
    
    public MapleServerHandler(final int channel, final ServerType type) {
        this.channel = -1;
        this.BlockedIP = new ArrayList<String>();
        this.tracker = new ConcurrentHashMap<String, Pair<Long, Byte>>();
        this.type = null;
        this.channel = channel;
        this.type = type;
    }
    
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    }
    
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final String address = ctx.channel().remoteAddress().toString().split(":")[0];
        if (this.BlockedIP.contains(address)) {
            ctx.channel().close();
            return;
        }
        final Pair<Long, Byte> track = this.tracker.get(address);
        byte count;
        if (track == null) {
            count = 1;
        }
        else {
            count = track.right;
            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000L) {
                ++count;
            }
            else if (difference > 20000L) {
                count = 1;
            }
            if (count >= 10) {
                this.BlockedIP.add(address);
                this.tracker.remove(address);
                ctx.channel().close();
                return;
            }
        }
        this.tracker.put(address, new Pair<Long, Byte>(System.currentTimeMillis(), count));
        final String IP = address.substring(address.indexOf(47) + 1, address.length());
        if (this.channel == -10) {
            if (CashShopServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        }
        else if (this.channel == -1) {
            if (LoginServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        }
        else {
            if (this.channel <= -1) {
                System.out.println("[連結錯誤] 未知類型: " + this.channel);
                ctx.channel().close();
                return;
            }
            if (ChannelServer.getInstance(this.channel).isShutdown()) {
                ctx.channel().close();
                return;
            }
            if (!LoginServer.containsIPAuth(IP)) {}
        }
        LoginServer.removeIPAuth(IP);
        final byte[] serverRecv = { 70, 114, 122, (byte)Randomizer.nextInt(255) };
        final byte[] serverSend = { 82, 48, 120, (byte)Randomizer.nextInt(255) };
        final byte[] ivRecv = ServerConstants.Use_Fixed_IV ? new byte[] { 9, 0, 5, 95 } : serverRecv;
        final byte[] ivSend = ServerConstants.Use_Fixed_IV ? new byte[] { 1, 95, 4, 63 } : serverSend;
        final MapleClient client = new MapleClient(new MapleAESOFB(ivSend, (short)(-80)), new MapleAESOFB(ivRecv, (short)79), new MapleSession(ctx.channel()));
        client.setChannel(this.channel);
        final DecoderState decoderState = new DecoderState();
        ctx.channel().attr((AttributeKey)MaplePacketDecoder.DECODER_STATE_KEY).set(decoderState);
        ctx.channel().writeAndFlush(LoginPacket.getHello((short)79, ServerConstants.Use_Fixed_IV ? serverSend : ivSend, ServerConstants.Use_Fixed_IV ? serverRecv : ivRecv));
        ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).set(client);
        Client.addClient(client);
        final StringBuilder sb = new StringBuilder();
        if (this.channel > -1) {
            sb.append("[频道服务器] 频道 ").append(this.channel).append(" : ");
        }
        else if (this.type == ServerType.商城服务器) {
            sb.append("[商城服务器]");
        }
        else {
            sb.append("[登录服务器]");
        }
        sb.append("IoSession opened ").append(address);
    }
    
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final MapleClient client = (MapleClient)ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).get();
        if (client != null) {
            try {
                final FileWriter fw = isLoggedIP(ctx);
                if (fw != null) {
                    fw.write("=== Session Closed ===");
                    fw.write(MapleServerHandler.nl);
                    fw.flush();
                }
                client.disconnect(true, this.type == ServerType.商城服务器);
            }
            finally {
                Client.removeClient(client);
                ctx.channel().close();
                ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).remove();
            }
        }
        super.channelInactive(ctx);
    }
    
    public void channelRead(final ChannelHandlerContext ctx, final Object message) {
        try {
            final SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream((byte[])message));
            if (slea.available() < 2L) {
                return;
            }
            final short header_num = slea.readShort();
            final RecvPacketOpcode[] values = RecvPacketOpcode.values();
            final int length = values.length;
            int i = 0;
            while (i < length) {
                final RecvPacketOpcode recv = values[i];
                if (recv.getValue() == header_num) {
                    if (MapleServerHandler.debugMode && !RecvPacketOpcode.isSpamHeader(recv)) {
                        final StringBuilder sb = new StringBuilder("Received data 已處理 :" + String.valueOf(recv) + "\n");
                        sb.append(HexTool.toString((byte[])message)).append("\n").append(HexTool.toStringFromAscii((byte[])message));
                        System.out.println("messageReceived" + sb.toString());
                    }
                    final MapleClient c = (MapleClient)ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).get();
                    if (!c.isReceiving()) {
                        return;
                    }
                    if (recv.NeedsChecking() && !c.isLoggedIn()) {
                        return;
                    }
                    if (c.getPlayer() != null && c.isMonitored() && !MapleServerHandler.blocked.contains(recv)) {
                        FileoutputUtil.log("日志/Logs/Monitored/" + c.getPlayer().getName() + ".txt", String.valueOf(recv) + " (" + Integer.toHexString(header_num) + ") Handled: \r\n" + slea.toString() + "\r\n");
                    }
                    log(slea, recv, c, ctx);
                    handlePacket(recv, slea, c, this.type == ServerType.商城服务器);
                    final FileWriter fw = isLoggedIP(ctx);
                    if (fw != null && !MapleServerHandler.blocked.contains(recv)) {
                        if (recv == RecvPacketOpcode.PLAYER_LOGGEDIN && c != null) {
                            fw.write(">> [AccountName: " + ((c.getAccountName() == null) ? "null" : c.getAccountName()) + "] | [IGN: " + ((c.getPlayer() == null || c.getPlayer().getName() == null) ? "null" : c.getPlayer().getName()) + "] | [Time: " + FileoutputUtil.CurrentReadable_Time() + "]");
                            fw.write(MapleServerHandler.nl);
                        }
                        fw.write("[" + recv.toString() + "]" + slea.toString(true));
                        fw.write(MapleServerHandler.nl);
                        fw.flush();
                    }
                    return;
                }
                else {
                    ++i;
                }
            }
            if (MapleServerHandler.debugMode) {
                final StringBuilder sb2 = new StringBuilder("Received data 未處理 : ");
                sb2.append(HexTool.toString((byte[])message)).append("\n").append(HexTool.toStringFromAscii((byte[])message));
                System.out.println("Received未處理" + sb2.toString());
            }
        }
        catch (RejectedExecutionException ex) {}
        catch (Exception e) {
            FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", e);
            e.printStackTrace();
        }
    }
    
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        final MapleClient client = (MapleClient)ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).get();
        if (client != null && this.type == ServerType.频道服务器) {
            client.sendPing();
            super.userEventTriggered(ctx, evt);
            return;
        }
        ctx.channel().close();
    }
    
    public static void handlePacket(final RecvPacketOpcode header, final SeekableLittleEndianAccessor slea, final MapleClient c, final boolean cs) throws Exception {
        switch (header) {
            case PONG: {
                c.pongReceived();
                break;
            }
            case STRANGE_DATA: {
                break;
            }
            case EFFECT_ON_OFF:
            case NEW_SX: {
                break;
            }
            case HELLO_LOGIN: {
                CharLoginHandler.Welcome(c);
                break;
            }
            case HELLO_CHANNEL: {
                CharLoginHandler.Welcome(c);
                break;
            }
            case PACKET_ERROR: {
                PacketErrorHandler.handlePacket(slea, c);
                break;
            }
            case LOGIN_PASSWORD: {
                CharLoginHandler.login(slea, c);
                break;
            }
            case SERVERLIST_REQUEST: {
                CharLoginHandler.ServerListRequest(c);
                break;
            }
            case LICENSE_REQUEST: {
                CharLoginHandler.ServerListRequest(c);
                break;
            }
            case CHARLIST_REQUEST: {
                CharLoginHandler.CharlistRequest(slea, c);
                break;
            }
            case SERVERSTATUS_REQUEST: {
                CharLoginHandler.ServerStatusRequest(c);
                break;
            }
            case CHECK_CHAR_NAME: {
                CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
                break;
            }
            case CREATE_CHAR: {
                CharLoginHandler.CreateChar(slea, c);
                break;
            }
            case DELETE_CHAR: {
                CharLoginHandler.DeleteChar(slea, c);
                break;
            }
            case CHAR_SELECT: {
                CharLoginHandler.Character_WithoutSecondPassword(slea, c);
                break;
            }
            case AUTH_SECOND_PASSWORD: {
                CharLoginHandler.Character_WithSecondPassword(slea, c);
                break;
            }
            case SET_GENDER: {
                CharLoginHandler.SetGenderRequest(slea, c);
                break;
            }
            case RSA_KEY: {
                c.sendPacket(LoginPacket.StrangeDATA());
                break;
            }
            case CHANGE_CHANNEL: {
                InterServerHandler.ChangeChannel(slea, c, c.getPlayer());
                break;
            }
            case PLAYER_LOGGEDIN: {
                final int playerid = slea.readInt();
                if (cs) {
                    CashShopOperation.进入商城(playerid, c);
                    break;
                }
                InterServerHandler.Loggedin(playerid, c);
                break;
            }
            case ENTER_CASH_SHOP: {
                slea.readInt();
                InterServerHandler.EnterCS(c, c.getPlayer(), false);
                break;
            }
            case ENTER_MTS: {
                InterServerHandler.EnterMTS(c, c.getPlayer(), true);
                break;
            }
            case PLAYER_UPDATE: {
                PlayerHandler.UpdateHandler(slea, c, c.getPlayer());
                break;
            }
            case MOVE_PLAYER: {
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            }
            case CHAR_INFO_REQUEST: {
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                break;
            }
            case CLOSE_RANGE_ATTACK: {
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), false);
                break;
            }
            case RANGED_ATTACK: {
                PlayerHandler.rangedAttack(slea, c, c.getPlayer());
                break;
            }
            case MAGIC_ATTACK: {
                PlayerHandler.MagicDamage(slea, c, c.getPlayer());
                break;
            }
            case SPECIAL_MOVE: {
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            }
            case PASSIVE_ENERGY: {
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), true);
                break;
            }
            case FACE_EXPRESSION: {
                PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            }
            case TAKE_DAMAGE: {
                PlayerHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            }
            case HEAL_OVER_TIME: {
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            }
            case CANCEL_BUFF: {
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            }
            case CANCEL_ITEM_EFFECT: {
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            }
            case USE_CHAIR: {
                PlayerHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            }
            case CANCEL_CHAIR: {
                PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            }
            case USE_ITEMEFFECT:
            case WHEEL_OF_FORTUNE: {
                PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            }
            case SKILL_EFFECT: {
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            }
            case MESO_DROP: {
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            }
            case MONSTER_BOOK_COVER: {
                PlayerHandler.ChangeMonsterBookCover(slea.readInt(), c, c.getPlayer());
                break;
            }
            case CHANGE_KEYMAP: {
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            }
            case CHANGE_MAP: {
                if (c.getPlayer().getMap() == null) {
                    CashShopOperation.LeaveCS(slea, c, c.getPlayer());
                    break;
                }
                PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                break;
            }
            case CHANGE_MAP_SPECIAL: {
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea, slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            }
            case USE_INNER_PORTAL: {
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            }
            case TROCK_ADD_MAP: {
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            }
            case ARAN_COMBO: {
                PlayerHandler.AranCombo(c, c.getPlayer());
                break;
            }
            case SKILL_MACRO: {
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;
            }
            case ITEM_BAOWU: {
                InventoryHandler.UsePenguinBox(slea, c);
                break;
            }
            case ITEM_SUNZI: {
                InventoryHandler.SunziBF(slea, c);
                break;
            }
            case GIVE_FAME: {
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            }
            case TRANSFORM_PLAYER: {
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            }
            case NOTE_ACTION: {
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            }
            case USE_DOOR: {
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            }
            case DAMAGE_REACTOR: {
                PlayersHandler.HitReactor(slea, c);
                break;
            }
            case TOUCH_REACTOR: {
                PlayersHandler.TouchReactor(slea, c);
                break;
            }
            case CLOSE_CHALKBOARD: {
                c.getPlayer().setChalkboard(null);
                break;
            }
            case ITEM_MAKER: {
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            }
            case ITEM_SORT: {
                InventoryHandler.ItemSort(slea, c);
                break;
            }
            case ITEM_GATHER: {
                InventoryHandler.ItemGather(slea, c);
                break;
            }
            case ITEM_MOVE: {
                InventoryHandler.ItemMove(slea, c);
                break;
            }
            case ITEM_PICKUP: {
                InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
                break;
            }
            case USE_CASH_ITEM: {
                InventoryHandler.UseCashItem(slea, c);
                break;
            }
            case quest_KJ: {
                InventoryHandler.QuestKJ(slea, c, c.getPlayer());
                break;
            }
            case USE_ITEM: {
                InventoryHandler.UseItem(slea, c, c.getPlayer());
                break;
            }
            case USE_MAGNIFY_GLASS: {
                InventoryHandler.UseMagnify(slea, c);
                break;
            }
            case USE_SCRIPTED_NPC_ITEM: {
                InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            }
            case USE_RETURN_SCROLL: {
                InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            }
            case USE_UPGRADE_SCROLL: {
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte)slea.readShort(), (byte)slea.readShort(), (byte)slea.readShort(), c, c.getPlayer());
                break;
            }
            case USE_POTENTIAL_SCROLL: {
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte)slea.readShort(), (byte)slea.readShort(), (byte)0, c, c.getPlayer());
                break;
            }
            case USE_EQUIP_SCROLL: {
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte)slea.readShort(), (byte)slea.readShort(), (byte)0, c, c.getPlayer());
                break;
            }
            case USE_SUMMON_BAG: {
                InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            }
            case USE_TREASUER_CHEST: {
                InventoryHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            }
            case USE_SKILL_BOOK: {
                InventoryHandler.UseSkillBook(slea, c, c.getPlayer());
                break;
            }
            case USE_CATCH_ITEM: {
                InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            }
            case USE_MOUNT_FOOD: {
                InventoryHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            }
            case REWARD_ITEM: {
                InventoryHandler.UseRewardItem((byte)slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            }
            case HYPNOTIZE_DMG: {
                MobHandler.HypnotizeDmg(slea, c.getPlayer());
                break;
            }
            case MOB_NODE: {
                MobHandler.MobNode(slea, c.getPlayer());
                break;
            }
            case DISPLAY_NODE: {
                MobHandler.DisplayNode(slea, c.getPlayer());
                break;
            }
            case MOVE_LIFE: {
                MobHandler.MoveMonster(slea, c, c.getPlayer());
                break;
            }
            case AUTO_AGGRO: {
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            }
            case FRIENDLY_DAMAGE: {
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            }
            case MONSTER_BOMB: {
                MobHandler.MonsterBomb(slea.readInt(), c.getPlayer());
                break;
            }
            case NPC_SHOP: {
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            }
            case NPC_TALK: {
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            }
            case NPC_TALK_MORE: {
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            }
            case MARRAGE_RECV: {
                NPCHandler.MarrageNpc(c);
                break;
            }
            case NPC_ACTION: {
                NPCHandler.NPCAnimation(slea, c);
                break;
            }
            case QUEST_ACTION: {
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            }
            case STORAGE: {
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            }
            case GENERAL_CHAT: {
                ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
                break;
            }
            case PARTYCHAT: {
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            }
            case WHISPER: {
                ChatHandler.Whisper_Find(slea, c);
                break;
            }
            case MESSENGER: {
                ChatHandler.Messenger(slea, c);
                break;
            }
            case AUTO_ASSIGN_AP: {
                StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
                break;
            }
            case DISTRIBUTE_AP: {
                StatsHandling.DistributeAP(slea, c, c.getPlayer());
                break;
            }
            case DISTRIBUTE_SP: {
                c.getPlayer().updateTick(slea.readInt());
                StatsHandling.DistributeSP(slea.readInt(), c, c.getPlayer());
                break;
            }
            case PLAYER_INTERACTION: {
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            }
            case GUILD_OPERATION: {
                GuildHandler.Guild(slea, c);
                break;
            }
            case UPDATE_CHAR_INFO: {
                PlayersHandler.UpdateCharInfo(slea, c, c.getPlayer());
                break;
            }
            case DENY_GUILD_REQUEST: {
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            }
            case ALLIANCE_OPERATION: {
                AllianceHandler.HandleAlliance(slea, c, false);
                break;
            }
            case DENY_ALLIANCE_REQUEST: {
                AllianceHandler.HandleAlliance(slea, c, true);
                break;
            }
            case BBS_OPERATION: {
                BBSHandler.BBSOperatopn(slea, c);
                break;
            }
            case PARTY_OPERATION: {
                PartyHandler.PartyOperatopn(slea, c);
                break;
            }
            case DENY_PARTY_REQUEST: {
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            }
            case BUDDYLIST_MODIFY: {
                BuddyListHandler.BuddyOperation(slea, c);
                break;
            }
            case CYGNUS_SUMMON: {
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            }
            case SHIP_OBJECT: {
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            }
            case BUY_CS_ITEM: {
                CashShopOperation.BuyCashItem(slea, c, c.getPlayer());
                break;
            }
            case TOUCHING_CS: {
                CashShopOperation.TouchingCashShop(c);
                break;
            }
            case COUPON_CODE: {
                FileoutputUtil.log("日志/Logs/Log_Packet_封包异常.rtf", "Coupon : \n" + slea.toString(true));
                System.out.println(slea.toString());
                slea.skip(2);
                CashShopOperation.CouponCode(slea.readMapleAsciiString(), c);
                break;
            }
            case CS_UPDATE: {
                CashShopOperation.CSUpdate(c);
                break;
            }
            case TOUCHING_MTS: {
                MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
                break;
            }
            case MTS_TAB: {
                MTSOperation.MTSOperation(slea, c);
                break;
            }
            case DAMAGE_SUMMON: {
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            }
            case MOVE_SUMMON: {
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            }
            case SUMMON_ATTACK: {
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            }
            case MOVE_DRAGON: {
                SummonHandler.MoveDragon(slea, c.getPlayer());
                break;
            }
            case PET_EXCEPTIONLIST: {
                PetHandler.PickExceptionList(slea, c, c.getPlayer());
                break;
            }
            case SPAWN_PET: {
                PetHandler.SpawnPet(slea, c, c.getPlayer());
                break;
            }
            case MOVE_PET: {
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            }
            case PET_CHAT: {
                if (slea.available() < 12L) {
                    break;
                }
                PetHandler.PetChat((int)slea.readLong(), slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
                break;
            }
            case PET_COMMAND: {
                PetHandler.PetCommand(slea, c, c.getPlayer());
                break;
            }
            case PET_FOOD: {
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            }
            case PET_LOOT: {
                InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
                break;
            }
            case PET_AUTO_POT: {
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            }
            case MONSTER_CARNIVAL: {
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            }
            case DUEY_ACTION: {
                DueyHandler.DueyOperation(slea, c);
                break;
            }
            case USE_HIRED_MERCHANT: {
                HiredMerchantHandler.UseHiredMerchant(slea, c);
                break;
            }
            case MERCH_ITEM_STORE: {
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;
            }
            case CANCEL_DEBUFF: {
                break;
            }
            case LEFT_KNOCK_BACK: {
                PlayerHandler.leftKnockBack(slea, c);
                break;
            }
            case SNOWBALL: {
                PlayerHandler.snowBall(slea, c);
                break;
            }
            case ChatRoom_SYSTEM: {
                PlayersHandler.ChatRoomHandler(slea, c);
                break;
            }
            case COCONUT: {
                PlayersHandler.hitCoconut(slea, c);
                break;
            }
            case REPAIR: {
                NPCHandler.repair(slea, c);
                break;
            }
            case REPAIR_ALL: {
                NPCHandler.repairAll(c);
                break;
            }
            case GAME_POLL: {
                UserInterfaceHandler.InGame_Poll(slea, c);
                break;
            }
            case OWL: {
                InventoryHandler.Owl(slea, c);
                break;
            }
            case OWL_WARP: {
                InventoryHandler.OwlWarp(slea, c);
                break;
            }
            case USE_OWL_MINERVA: {
                InventoryHandler.OwlMinerva(slea, c);
                break;
            }
            case RPS_GAME: {
                NPCHandler.RPSGame(slea, c);
                break;
            }
            case UPDATE_QUEST: {
                NPCHandler.UpdateQuest(slea, c);
                break;
            }
            case USE_ITEM_QUEST: {
                NPCHandler.UseItemQuest(slea, c);
                break;
            }
            case FOLLOW_REQUEST: {
                PlayersHandler.FollowRequest(slea, c);
                break;
            }
            case FOLLOW_REPLY: {
                PlayersHandler.FollowReply(slea, c);
                break;
            }
            case RING_ACTION: {
                PlayersHandler.RingAction(slea, c);
                break;
            }
            case REQUEST_FAMILY: {
                FamilyHandler.RequestFamily(slea, c);
                break;
            }
            case OPEN_FAMILY: {
                FamilyHandler.OpenFamily(slea, c);
                break;
            }
            case FAMILY_OPERATION: {
                FamilyHandler.FamilyOperation(slea, c);
                break;
            }
            case DELETE_JUNIOR: {
                FamilyHandler.DeleteJunior(slea, c);
                break;
            }
            case DELETE_SENIOR: {
                FamilyHandler.DeleteSenior(slea, c);
                break;
            }
            case USE_FAMILY: {
                FamilyHandler.UseFamily(slea, c);
                break;
            }
            case FAMILY_PRECEPT: {
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            }
            case FAMILY_SUMMON: {
                FamilyHandler.FamilySummon(slea, c);
                break;
            }
            case ACCEPT_FAMILY: {
                FamilyHandler.AcceptFamily(slea, c);
                break;
            }
            case BEANS_GAME1: {
                BeanGame.BeanGame1(slea, c);
                break;
            }
            case BEANS_GAME2: {
                BeanGame.BeanGame2(slea, c);
                break;
            }
            case MOONRABBIT_HP: {
                PlayerHandler.Rabbit(slea, c);
                break;
            }
            default: {
                System.out.println("[未经处理的] 客户端包 [" + header.toString() + "] 发现了");
                break;
            }
        }
    }
    
    static {
        nl = System.getProperty("line.separator");
        loggedIPs = new File("日志/Logs/LogIPs.txt");
        logIPMap = new HashMap<String, FileWriter>();
        MapleServerHandler.debugMode = Boolean.parseBoolean(ServerProperties.getProperty("tms.Debug", "false"));
        blocked = EnumSet.noneOf(RecvPacketOpcode.class);
        reloadLoggedIPs();
        final RecvPacketOpcode[] block = { RecvPacketOpcode.NPC_ACTION, RecvPacketOpcode.MOVE_PLAYER, RecvPacketOpcode.MOVE_PET, RecvPacketOpcode.MOVE_SUMMON, RecvPacketOpcode.MOVE_DRAGON, RecvPacketOpcode.MOVE_LIFE, RecvPacketOpcode.HEAL_OVER_TIME, RecvPacketOpcode.STRANGE_DATA };
        MapleServerHandler.blocked.addAll(Arrays.asList(block));
        Packet_Log = new ArrayList<LoggedPacket>(10000);
        Packet_Log_Lock = new ReentrantReadWriteLock();
        Packet_Log_Output = new File("PacketLog.txt");
    }
    
    private static class LoggedPacket
    {
        private static final String nl;
        private String ip;
        private String accName;
        private String accId;
        private String chrName;
        private SeekableLittleEndianAccessor packet;
        private long timestamp;
        private RecvPacketOpcode op;
        
        public LoggedPacket(final SeekableLittleEndianAccessor p, final RecvPacketOpcode op, final String ip, final int id, final String accName, final String chrName) {
            this.setInfo(p, op, ip, id, accName, chrName);
        }
        
        public final void setInfo(final SeekableLittleEndianAccessor p, final RecvPacketOpcode op, final String ip, final int id, final String accName, final String chrName) {
            this.ip = ip;
            this.op = op;
            this.packet = p;
            this.accName = accName;
            this.chrName = chrName;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[IP: ").append(this.ip).append("] [").append(this.accId).append('|').append(this.accName).append('|').append(this.chrName).append("] [Time: ").append(this.timestamp).append(']');
            sb.append(LoggedPacket.nl);
            sb.append("[Op: ").append(this.op.toString()).append(']');
            sb.append(" [Data: ").append(this.packet.toString()).append(']');
            return sb.toString();
        }
        
        static {
            nl = System.getProperty("line.separator");
        }
    }
}
