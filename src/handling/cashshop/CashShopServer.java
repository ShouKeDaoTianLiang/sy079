package handling.cashshop;

import server.Timer.WorldTimer;
import handling.ServerType;
import server.ServerProperties;
import handling.netty.ServerConnection;
import handling.channel.PlayerStorage;

public class CashShopServer
{
    private static String ip;
    private static final int PORT;
    private static boolean finishedShutdown;
    private static PlayerStorage players;
    private static PlayerStorage playersMTS;
    private static ServerConnection init;
    
    public static void run_startup_configurations() {
        CashShopServer.ip = ServerProperties.getProperty("tms.IP") + ":" + CashShopServer.PORT;
        CashShopServer.players = new PlayerStorage(-10);
        CashShopServer.playersMTS = new PlayerStorage(-20);
        try {
            (CashShopServer.init = new ServerConnection(ServerType.商城服务器, CashShopServer.PORT, 0, -1)).run();
        }
        catch (Exception e) {
            System.err.println("Binding to port " + CashShopServer.PORT + " failed");
            e.printStackTrace();
            throw new RuntimeException("Binding failed.", e);
        }
    }
    
    public static void start() {
        WorldTimer.getInstance().register(() -> {}, 120000L);
    }
    
    public static final String getIP() {
        return CashShopServer.ip;
    }
    
    public static final PlayerStorage getPlayerStorage() {
        return CashShopServer.players;
    }
    
    public static final PlayerStorage getPlayerStorageMTS() {
        return CashShopServer.playersMTS;
    }
    
    public static final void shutdown() {
        if (CashShopServer.finishedShutdown) {
            return;
        }
        System.out.println("正在断开商城内玩家...");
        CashShopServer.players.disconnectAll();
        CashShopServer.playersMTS.disconnectAll();
        System.out.println("正在关闭商城伺服器...");
        CashShopServer.init.close();
        CashShopServer.finishedShutdown = true;
    }
    
    public static boolean isShutdown() {
        return CashShopServer.finishedShutdown;
    }
    
    static {
        PORT = Integer.parseInt(ServerProperties.getProperty("tms.SCPort", "8600"));
        CashShopServer.finishedShutdown = false;
    }
}
