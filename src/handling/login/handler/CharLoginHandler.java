package handling.login.handler;

import server.ServerProperties;
import client.inventory.MapleInventory;
import client.inventory.Item;
import server.quest.MapleQuest;
import server.MapleItemInformationProvider;
import client.inventory.MapleInventoryType;
import handling.login.LoginInformationProvider;
import client.MapleCharacterUtil;
import client.MapleCharacter;
import java.util.List;
import tools.data.input.LittleEndianAccessor;
import handling.login.LoginServer;
import java.util.Calendar;
import handling.login.LoginWorker;
import tools.KoreanDateUtil;
import tools.packet.LoginPacket;
import handling.channel.ChannelServer;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import constants.ServerConstants;
import tools.StringUtil;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleClient;

public class CharLoginHandler
{
    public static boolean 冒险家;
    public static boolean 骑士团;
    public static boolean 战神;
    public static boolean 限制登陆;
    
    private static boolean loginFailCount(final MapleClient c) {
        ++c.loginAttempt;
        return c.loginAttempt > 5;
    }
    
    public static final void Welcome(final MapleClient c) {
    }
    
    public static final void login(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String login = slea.readMapleAsciiString();
        final String pwd = slea.readMapleAsciiString();
        final String ip = c.getSessionIPAddress();
        c.setAccountName(login);
        final int[] bytes = new int[6];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = slea.readByteAsInt();
        }
        final StringBuilder sps = new StringBuilder();
        for (int j = 0; j < bytes.length; ++j) {
            sps.append(StringUtil.getLeftPaddedStr(Integer.toHexString(bytes[j]).toUpperCase(), '0', 2));
            sps.append("-");
        }
        String macData = sps.toString();
        macData = macData.substring(0, macData.length() - 1);
        c.setMac(macData);
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.isBannedMac(macData);
        final boolean banned = ipBan || macBan;
        int loginok = 0;
        final int channel = c.getChannel();
        ChannelServer.getInstance(channel);
        if (ServerConstants.getAutoReg() && AutoRegister.autoRegister && !AutoRegister.getAccountExists(login) && !banned) {
            AutoRegister.createAccount(login, pwd, c.getSession().getRemoteAddress().toString(), macData);
            AutoRegister.success = true;
            AutoRegister.mac = true;
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        loginok = c.login(login, pwd, ipBan || macBan);
        final Calendar tempbannedTill = c.getTempBanCalendar();
        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (!macBan) {}
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getLoginFailed(loginok));
            }
        }
        else if (tempbannedTill.getTimeInMillis() != 0L) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            }
        }
        else {
            FileoutputUtil.logToFile("日志/Logs/ACPW.txt", "ACC: " + login + " PW: " + pwd + " MAC : " + macData + " IP: " + c.getSession().getRemoteAddress().toString() + "\r\n");
            c.updateMacs();
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }
    
    public static final void SetGenderRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte gender = slea.readByte();
        final String username = slea.readMapleAsciiString();
        if (c.getAccountName().equals(username)) {
            c.setGender(gender);
            c.updateSecondPassword();
            c.updateGender();
            c.sendPacket(LoginPacket.getGenderChanged(c));
            c.sendPacket(MaplePacketCreator.licenseRequest());
            c.updateLoginState(0, c.getSessionIPAddress());
        }
        else {
            c.getSession().close();
        }
    }
    
    public static final void ServerListRequest(final MapleClient c) {
        c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
        c.sendPacket(LoginPacket.getEndOfServerList());
    }
    
    public static final void ServerStatusRequest(final MapleClient c) {
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        }
        else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        }
        else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }
    
    public static final void LicenseRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (slea.readByte() == 1) {
            c.sendPacket(MaplePacketCreator.licenseResult());
            c.updateLoginState(0);
        }
        else {
            c.getSession().close();
        }
    }
    
    public static void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        slea.readInt();
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        }
        else {
            c.getSession().close();
        }
    }
    
    public static final void CheckCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name, !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }
    
    public static final void CreateChar(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        final int JobType = slea.readInt();
        if (!CharLoginHandler.骑士团 && JobType == 0) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "无法创建骑士团职业！"));
            return;
        }
        if (!CharLoginHandler.冒险家 && JobType == 1) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "无法创建冒险家职业！"));
            return;
        }
        if (!CharLoginHandler.战神 && JobType == 2) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "无法创建战神职业！"));
            return;
        }
        final short db = 0;
        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int hairColor = 0;
        final byte skinColor = 0;
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();
        final byte gender = c.getGender();
        switch (gender) {
            case 0: {
                if (face != 20100 && face != 20401 && face != 20402) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，脸型数据异常(男)！"));
                    return;
                }
                if (hair != 30030 && hair != 30027 && hair != 30000) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，发型数据异常(男)！"));
                    return;
                }
                if (top != 1040002 && top != 1040006 && top != 1040010 && top != 1042167) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，上衣数据异常(男)！"));
                    return;
                }
                if (bottom != 1060002 && bottom != 1060006 && bottom != 1062115) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，裤裙数据异常(男)！"));
                    return;
                }
                break;
            }
            case 1: {
                if (face != 21002 && face != 21700 && face != 21201) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，脸型数据异常(女)！"));
                    return;
                }
                if (hair != 31002 && hair != 31047 && hair != 31057) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，发型数据异常(女)！"));
                    return;
                }
                if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011 && top != 1042167) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，上衣数据异常(女)！"));
                    return;
                }
                if (bottom != 1061002 && bottom != 1061008 && bottom != 1062115) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，裤裙数据异常(女)！"));
                    return;
                }
                break;
            }
            default: {
                return;
            }
        }
        if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038 && shoes != 1072383) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，鞋子数据异常！"));
            return;
        }
        if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004 && weapon != 1442079) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "创建失败，武器数据异常！"));
            return;
        }
        final MapleCharacter newchar = MapleCharacter.getDefault(c, JobType);
        newchar.setWorld((byte)c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + 0);
        newchar.setSkinColor((byte)0);
        newchar.setGender(gender);
        newchar.setName(name);
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        Item item = li.getEquipById(top);
        item.setPosition((short)(-5));
        equip.addFromDB(item);
        item = li.getEquipById(bottom);
        item.setPosition((short)(-6));
        equip.addFromDB(item);
        item = li.getEquipById(shoes);
        item.setPosition((short)(-7));
        equip.addFromDB(item);
        item = li.getEquipById(weapon);
        item.setPosition((short)(-11));
        equip.addFromDB(item);
        switch (JobType) {
            case 0: {
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte)1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte)1, null);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5530000, (short)0, (short)1, (short)0));
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (short)0, (short)1, (short)0));
                break;
            }
            case 1: {
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5530000, (short)0, (short)1, (short)0));
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (short)0, (short)1, (short)0));
                break;
            }
            case 2: {
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5530000, (short)0, (short)1, (short)0));
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (short)0, (short)1, (short)0));
                break;
            }
        }
        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1);
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        }
        else {
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, false));
        }
    }
    
    public static final void DeleteChar(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();
        String Secondpw_Client = null;
        Secondpw_Client = slea.readMapleAsciiString();
        final int Character_ID = slea.readInt();
        if (!c.login_Auth(Character_ID)) {
            c.sendPacket(LoginPacket.secondPwError((byte)20));
            return;
        }
        byte state = 0;
        if (c.getSecondPassword() != null) {
            if (Secondpw_Client == null) {
                c.getSession().close();
                return;
            }
            if (!c.CheckSecondPassword(Secondpw_Client)) {
                state = 16;
            }
        }
        if (state == 0) {
            state = (byte)c.deleteCharacter(Character_ID);
        }
        c.sendPacket(LoginPacket.deleteCharResponse(Character_ID, state));
    }
    
    public static void Character_WithoutSecondPassword(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int charId = slea.readInt();
        if (!c.isLoggedIn() || loginFailCount(c) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null) {
            c.getSession().close();
            return;
        }
        if (ChannelServer.getInstance(c.getChannel()) == null || c.getWorld() != 0) {
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf(47) + 1, s.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(1, s);
        c.sendPacket(MaplePacketCreator.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }
    
    public static final void Character_WithSecondPassword(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (!c.isLoggedIn() || loginFailCount(c) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null) {
            c.getSession().close();
            return;
        }
        if (c.CheckSecondPassword(password)) {
            c.updateMacs(slea.readMapleAsciiString());
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf(47) + 1, s.length()), c.getTempIP(), c.getChannel());
            c.updateLoginState(1, s);
            c.sendPacket(MaplePacketCreator.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        }
        else {
            c.sendPacket(LoginPacket.secondPwError((byte)20));
        }
    }
    
    static {
        CharLoginHandler.冒险家 = Boolean.parseBoolean(ServerProperties.getProperty("world.冒险家", "false"));
        CharLoginHandler.骑士团 = Boolean.parseBoolean(ServerProperties.getProperty("world.骑士团", "false"));
        CharLoginHandler.战神 = Boolean.parseBoolean(ServerProperties.getProperty("world.战神", "false"));
        CharLoginHandler.限制登陆 = Boolean.parseBoolean(ServerProperties.getProperty("world.限制登陆", "false"));
    }
}
