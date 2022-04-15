package server;

import java.util.Set;

import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World.Alliance;
import handling.world.World.Broadcast;
import handling.world.World.Family;
import handling.world.World.Guild;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.WorldTimer;
import tools.MaplePacketCreator;

public class ShutdownServer implements Runnable
{
    private static final ShutdownServer instance;
    public static boolean running;
    public int mode;
    
    public ShutdownServer() {
        this.mode = 0;
    }
    
    public static ShutdownServer getInstance() {
        return ShutdownServer.instance;
    }
    
    public void shutdown() {
        this.run();
    }
    
    @Override
    public void run() {
        WorldTimer.getInstance().stop();
        MapTimer.getInstance().stop();
        BuffTimer.getInstance().stop();
        CloneTimer.getInstance().stop();
        EventTimer.getInstance().stop();
        EtcTimer.getInstance().stop();
        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.closeAllMerchant();
        }
        try {
            Guild.save();
            Alliance.save();
            Family.save();
        }
        catch (Exception ex) {}
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, " 游戏服务器将关闭维护，请玩家安全下线..."));
        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
            try {
                cs.setServerMessage("游戏服务器将关闭维护，请玩家安全下线...");
            }
            catch (Exception ex2) {}
        }
        final Set<Integer> channels = ChannelServer.getAllInstance();
        for (final Integer channel : channels) {
            try {
                final ChannelServer cs2 = ChannelServer.getInstance(channel);
                cs2.saveAll();
                cs2.setFinishShutdown();
                cs2.shutdown();
            }
            catch (Exception e2) {
                System.out.println("频道" + String.valueOf(channel) + " 关闭错误.");
            }
        }
        System.out.println("服务端关闭事件 1 已完成.");
        System.out.println("服务端关闭事件 2 开始...");
        try {
            LoginServer.shutdown();
            System.out.println("登录伺服器关闭完成...");
        }
        catch (Exception ex3) {}
        try {
            CashShopServer.shutdown();
            System.out.println("商城伺服器关闭完成...");
        }
        catch (Exception ex4) {}
        try {
            DatabaseConnection.closeAll();
        }
        catch (Exception ex5) {}
        System.out.println("服务端关闭事件 2 已完成.");
        try {
            Thread.sleep(1000L);
        }
        catch (Exception e) {
            System.out.println("关闭服务端错误 - 2" + e);
        }
        System.exit(0);
    }
    
    static {
        instance = new ShutdownServer();
        ShutdownServer.running = false;
    }
}
