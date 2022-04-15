package client.messages;

import java.util.List;
import java.util.Collections;
import client.messages.commands.CommandExecute;
import java.lang.reflect.Modifier;
import client.messages.commands.AdminCommand;
import client.messages.commands.InternCommand;
import client.messages.commands.GMCommand;
import client.messages.commands.PlayerCommand;
import java.util.Iterator;
import tools.MaplePacketCreator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import tools.FileoutputUtil;
import database.DatabaseConnection;
import client.MapleCharacter;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import constants.ServerConstants.CommandType;
import client.MapleClient;
import java.util.ArrayList;
import client.messages.commands.CommandObject;
import java.util.HashMap;

public class CommandProcessor
{
    private static final HashMap<String, CommandObject> commands;
    private static final HashMap<Integer, ArrayList<String>> commandList;
    
    private static void sendDisplayMessage(final MapleClient c, final String msg, final CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL: {
                c.getPlayer().dropMessage(6, msg);
                break;
            }
            case TRADE: {
                c.getPlayer().dropMessage(-2, "错误 : " + msg);
                break;
            }
        }
    }
    
    public static boolean processCommand(final MapleClient c, final String line, final CommandType type) {
        if (line.charAt(0) != PlayerGMRank.NORMAL.getCommandPrefix()) {
            if (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel() && (line.charAt(0) == PlayerGMRank.GM.getCommandPrefix() || line.charAt(0) == PlayerGMRank.ADMIN.getCommandPrefix() || line.charAt(0) == PlayerGMRank.INTERN.getCommandPrefix())) {
                final String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();
                if (line.charAt(0) == '!') {
                    final CommandObject co = (client.messages.commands.CommandObject)(client.messages.commands.CommandObject)CommandProcessor.commands.get(splitted[0]);
                    if (splitted[0].equals("!命令")) {
                        dropHelp(c, 0);
                        return true;
                    }
                    if (co == null || co.getType() != type) {
                        sendDisplayMessage(c, "输入的命令不存在.", type);
                        return true;
                    }
                    if (ServerConstants.是否允许使用管理员命令 && c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                        final int ret = co.execute(c, splitted);
                        if (ret > 0 && c.getPlayer() != null) {
                            logGMCommandToDB(c.getPlayer(), line);
                            System.out.println("[ " + c.getPlayer().getName() + " ] 使用了指令: " + line);
                        }
                    }
                    else {
                        sendDisplayMessage(c, "您的权限等级不足以使用次命令.", type);
                    }
                    return true;
                }
            }
            return false;
        }
        final String[] splitted = line.split(" ");
        splitted[0] = splitted[0].toLowerCase();
        final CommandObject co = (client.messages.commands.CommandObject)(client.messages.commands.CommandObject)CommandProcessor.commands.get(splitted[0]);
        if (co == null || co.getType() != type) {
            sendDisplayMessage(c, "输入的玩家命令不存在,可以使用 @帮助/@help 来查看指令.", type);
            return true;
        }
        try {
            co.execute(c, splitted);
        }
        catch (Exception e) {
            sendDisplayMessage(c, "有错误.", type);
            if (c.getPlayer().isGM()) {
                sendDisplayMessage(c, "错误: " + e, type);
            }
        }
        return true;
    }
    
    private static void logGMCommandToDB(final MapleCharacter player, final String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO gmlog (cid, name, command, mapid, ip) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, command);
            ps.setInt(4, player.getMap().getId());
            ps.setString(5, player.getClient().getSessionIPAddress());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", ex);
            ex.printStackTrace();
        }
        finally {
            try {
                ps.close();
            }
            catch (SQLException ex2) {}
        }
    }
    
    public static void dropHelp(final MapleClient c, final int type) {
        final StringBuilder sb = new StringBuilder("指令列表:\r\n ");
        int check = 0;
        if (type == 0) {
            check = c.getPlayer().getGMLevel();
        }
        for (int i = 0; i <= check; ++i) {
            if (CommandProcessor.commandList.containsKey(i)) {
                sb.append((type == 1) ? "VIP" : "").append("权限等級： ").append(i).append("\r\n");
                for (final String s : CommandProcessor.commandList.get(i)) {
                    sb.append(s);
                    sb.append(" \r\n");
                }
            }
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
    }
    
    static {
        commands = new HashMap<String, CommandObject>();
        commandList = new HashMap<Integer, ArrayList<String>>();
        final Class[] array;
        final Class[] CommandFiles = array = new Class[] { PlayerCommand.class, GMCommand.class, InternCommand.class, AdminCommand.class };
        for (final Class clasz : array) {
            try {
                final PlayerGMRank rankNeeded = (PlayerGMRank)clasz.getMethod("getPlayerLevelRequired", new Class[0]).invoke(null, (Object) null);
                final Class[] a = clasz.getDeclaredClasses();
                final ArrayList<String> cL = new ArrayList<String>();
                for (final Class c : a) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            final Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            }
                            catch (NoSuchFieldException ex3) {
                                enabled = true;
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                CommandProcessor.commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), (CommandExecute)o, rankNeeded.getLevel()));
                            }
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError("日志/Logs/Log_Script_脚本异常.rtf", ex);
                    }
                }
                Collections.sort(cL);
                CommandProcessor.commandList.put(rankNeeded.getLevel(), cL);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
                FileoutputUtil.outputFileError("日志/Logs/Log_Script_脚本异常.rtf", ex2);
            }
        }
    }
}
