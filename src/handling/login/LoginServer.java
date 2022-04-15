package handling.login;

import handling.ServerType;
import server.ServerProperties;
import server.Timer.WorldTimer;
import handling.netty.ServerConnection;
import java.util.HashSet;
import tools.Triple;
import java.util.HashMap;
import java.util.Map;

public class LoginServer
{
    public static int PORT;
    private static Map<Integer, Integer> load;
    private static String serverName;
    private static String eventMessage;
    private static byte flag;
    private static int maxCharacters;
    private static int userLimit;
    private static int 个人PK地图;
    private static int 组队PK地图;
    private static int 家族PK地图;
    private static int usersOn;
    private static boolean finishedShutdown;
    private static boolean adminOnly;
    private static final HashMap<Integer, Triple<String, String, Integer>> loginAuth;
    private static final HashSet<String> loginIPAuth;
    private static LoginServer instance;
    private static ServerConnection acceptor;
    private final Map<String, Integer> connectedIps;
    
    public LoginServer() {
        this.connectedIps = new HashMap<String, Integer>();
    }
    
    public static LoginServer getInstance() {
        return LoginServer.instance;
    }
    
    public static void start() {
        WorldTimer.getInstance().register(() -> {}, 120000L);
    }
    
    public static void putLoginAuth(final int chrid, final String ip, final String tempIP, final int channel) {
        final Triple<String, String, Integer> put = LoginServer.loginAuth.put(chrid, new Triple<String, String, Integer>(ip, tempIP, channel));
        LoginServer.loginIPAuth.add(ip);
    }
    
    public static int 个人PK地图() {
        return LoginServer.个人PK地图;
    }
    
    public static int 组队PK地图() {
        return LoginServer.组队PK地图;
    }
    
    public static int 家族PK地图() {
        return LoginServer.家族PK地图;
    }
    
    public static Triple<String, String, Integer> getLoginAuth(final int chrid) {
        return LoginServer.loginAuth.remove(chrid);
    }
    
    public static boolean containsIPAuth(final String ip) {
        return LoginServer.loginIPAuth.contains(ip);
    }
    
    public static void removeIPAuth(final String ip) {
        LoginServer.loginIPAuth.remove(ip);
    }
    
    public static void addIPAuth(final String ip) {
        LoginServer.loginIPAuth.add(ip);
    }
    
    public static final void addChannel(final int channel) {
        LoginServer.load.put(channel, 0);
    }
    
    public static final void removeChannel(final int channel) {
        LoginServer.load.remove(channel);
    }
    
    public static final void run_startup_configurations() {
        LoginServer.userLimit = Integer.parseInt(ServerProperties.getProperty("tms.UserLimit", "100"));
        LoginServer.serverName = ServerProperties.getProperty("tms.ServerName");
        LoginServer.eventMessage = ServerProperties.getProperty("tms.EventMessage");
        LoginServer.flag = Byte.parseByte(ServerProperties.getProperty("tms.Flag"));
        LoginServer.PORT = Short.parseShort(ServerProperties.getProperty("tms.LPort"));
        LoginServer.adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"));
        LoginServer.maxCharacters = Integer.parseInt(ServerProperties.getProperty("tms.MaxCharacters"));
        LoginServer.个人PK地图 = Integer.parseInt(ServerProperties.getProperty("channel.个人PVP"));
        LoginServer.组队PK地图 = Integer.parseInt(ServerProperties.getProperty("channel.组队PVP"));
        LoginServer.家族PK地图 = Integer.parseInt(ServerProperties.getProperty("channel.家族PVP"));
        try {
            (LoginServer.acceptor = new ServerConnection(ServerType.登录服务器, LoginServer.PORT, 0, -1)).run();
        }
        catch (Exception e) {
            System.err.println("Binding to port " + LoginServer.PORT + " failed" + e);
        }
    }
    
    public void addConnectedIP(final String ip) {
        if (this.connectedIps.containsKey(ip)) {
            final int connections = (int)(int)this.connectedIps.get(ip);
            this.connectedIps.remove(ip);
            this.connectedIps.put(ip, connections + 1);
        }
        else {
            this.connectedIps.put(ip, 1);
        }
    }
    
    public void removeConnectedIp(final String ip) {
        if (this.connectedIps.containsKey(ip)) {
            final int connections = (int)(int)this.connectedIps.get(ip);
            this.connectedIps.remove(ip);
            if (connections - 1 != 0) {
                this.connectedIps.put(ip, connections - 1);
            }
        }
    }
    
    public boolean ipCanConnect(final String ip) {
        return !this.connectedIps.containsKey(ip) || this.connectedIps.get(ip) < 5;
    }
    
    public static final void shutdown() {
        if (LoginServer.finishedShutdown) {
            return;
        }
        System.out.println("正在关闭登录伺服器...");
        LoginServer.finishedShutdown = true;
    }
    
    public static final String getServerName() {
        return LoginServer.serverName;
    }
    
    public static final String getEventMessage() {
        return LoginServer.eventMessage;
    }
    
    public static final byte getFlag() {
        return LoginServer.flag;
    }
    
    public static final int getMaxCharacters() {
        return LoginServer.maxCharacters;
    }
    
    public static final Map<Integer, Integer> getLoad() {
        return LoginServer.load;
    }
    
    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        LoginServer.load = load_;
        LoginServer.usersOn = usersOn_;
    }
    
    public static final void setEventMessage(final String newMessage) {
        LoginServer.eventMessage = newMessage;
    }
    
    public static final void setFlag(final byte newflag) {
        LoginServer.flag = newflag;
    }
    
    public static final int getUserLimit() {
        return LoginServer.userLimit;
    }
    
    public static final int getUsersOn() {
        return LoginServer.usersOn;
    }
    
    public static final void setUserLimit(final int newLimit) {
        LoginServer.userLimit = newLimit;
    }
    
    public static final boolean isAdminOnly() {
        return LoginServer.adminOnly;
    }
    
    public static final boolean isShutdown() {
        return LoginServer.finishedShutdown;
    }
    
    public static final void setOn() {
        LoginServer.finishedShutdown = false;
    }
    
    static {
        LoginServer.PORT = 8484;
        LoginServer.load = new HashMap<Integer, Integer>();
        LoginServer.usersOn = 0;
        LoginServer.finishedShutdown = true;
        LoginServer.adminOnly = false;
        loginAuth = new HashMap<Integer, Triple<String, String, Integer>>();
        loginIPAuth = new HashSet<String>();
        LoginServer.instance = new LoginServer();
    }
}
