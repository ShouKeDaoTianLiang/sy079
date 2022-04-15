package handling.login;

import java.util.Iterator;
import java.util.Map;
import server.Timer.PingTimer;
import java.util.Map.Entry;
import handling.channel.ChannelServer;
import constants.ServerConstants;
import tools.packet.LoginPacket;
import tools.MaplePacketCreator;
import client.MapleClient;

public class LoginWorker
{
    private static long lastUpdate;
    
    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "服务器正在维护中"));
            c.sendPacket(LoginPacket.getLoginFailed(7));
            return;
        }
        if (ServerConstants.限制登陆1 && !c.isGm()) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "当前服务器正在维护中.\r\n请稍后再试."));
            c.sendPacket(LoginPacket.getLoginFailed(7));
            return;
        }
        if (System.currentTimeMillis() - LoginWorker.lastUpdate > 600000L) {
            LoginWorker.lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) {
                LoginWorker.lastUpdate = 0L;
                c.sendPacket(LoginPacket.getLoginFailed(7));
                return;
            }
            final double loads = load.size();
            final double userlimit = LoginServer.getUserLimit();
            final double loadFactor = 1200.0 / (LoginServer.getUserLimit() / (double)load.size());
            for (final Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int)(entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            LoginWorker.lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            if (c.getGender() == 10) {
                c.sendPacket(LoginPacket.getGenderNeeded(c));
            }
            else {
                c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
                c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
                c.sendPacket(LoginPacket.getEndOfServerList());
            }
            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    c.getSession().close();
                }
            }, 6000000L));
        }
        else if (c.getGender() == 10) {
            c.sendPacket(LoginPacket.getGenderNeeded(c));
        }
        else {
            c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
            c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
            c.sendPacket(LoginPacket.getEndOfServerList());
        }
    }
    
    static {
        LoginWorker.lastUpdate = 0L;
    }
}
