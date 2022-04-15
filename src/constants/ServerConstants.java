package constants;

import server.ServerProperties;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import server.Timer.WorldTimer;
import java.util.Properties;
import org.slf4j.Logger;

public class ServerConstants
{
    private static final Logger log;
    public static final boolean PollEnabled = false;
    public static final String Poll_Question = "Are you mudkiz?";
    public static final String[] Poll_Answers;
    public static final short MAPLE_VERSION = 79;
    public static MapleType MAPLE_TYPE;
    public static final String MAPLE_PATCH = "1";
    public static boolean Use_Fixed_IV;
    public static boolean USE_FIXED_IV;
    public static final int MIN_MTS = 110;
    public static final int MTS_BASE = 100;
    public static final int MTS_TAX = 10;
    public static final int MTS_MESO = 5000;
    public static final int CHANNEL_COUNT = 200;
    public static String PACKET_ERROR;
    public static int Channel;
    public static int removePlayerFromMap;
    public static int getHello;
    public static boolean PACKET_ERROR_OFF;
    Properties p;
    public static boolean 封包显示;
    public static boolean 调试输出封包;
    public static boolean 破功;
    public static boolean 天气功能;
    public static boolean 战斗力;
    public static boolean 自动注册;
    public static boolean 怪物掉落抵用卷;
    public static boolean 怪物掉落点卷;
    public static boolean 加速怪物刷新模式;
    public static boolean 复古刷怪;
    public static boolean 登录公告;
    public static final int 泡点地图;
    public static boolean Super_password;
    public static boolean PK系统;
    public static int monsterSpawn;
    public static boolean 分级系统;
    public static String superpw;
    public static final boolean 万能限制;
    public static final int 出生地图;
    public static int 怪物刷新时间;
    public static boolean 法师破攻;
    public static boolean 是否允许顶号;
    public static boolean 是否允许使用管理员命令;
    public static final int LOGIN_DEFAULTUSERLIMIT;
    public static boolean 给怪物BUFF;
    public static int 地图最大怪物数量;
    public static int 地图出现怪物倍率;
    public static int 每日免费复活次数;
    public static int 时间;
    public static boolean 限制登陆;
    public static boolean 限制登陆1;
    public static final double 目前服务端修改编号 = 79.3;
    public static boolean 吸怪检测;
    public static boolean 封号系统;
    public static final String CashShop_Key = "a;!%dfb_=*-a123d9{P~";
    public static final String Login_Key = "pWv]xq:SPTCtk^LGnU9F";
    public static final String[] Channel_Key;
    public static String CHANNEL_ENTERNPC_CASHSHOP;
    
    public ServerConstants() {
        this.p = new Properties();
    }
    
    public static boolean get商城开关() {
        return ServerConstants.给怪物BUFF;
    }
    
    public static void is商城开关(boolean 给怪物BUFF) {
        给怪物BUFF = 给怪物BUFF;
    }
    
    public static boolean get限制登陆1() {
        return ServerConstants.限制登陆1;
    }
    
    public void sendPolice(final String text) {
        WorldTimer.getInstance().schedule(() -> {}, 6000L);
    }
    
    public static String is限制登陆1() {
        ServerConstants.限制登陆1 = !get限制登陆1();
        return ServerConstants.限制登陆1 ? "开启" : "关闭";
    }
    
    public static boolean get限制登陆() {
        return ServerConstants.限制登陆;
    }
    
    public static String is限制登陆() {
        ServerConstants.限制登陆 = !get限制登陆();
        return ServerConstants.限制登陆 ? "开启" : "关闭";
    }
    
    public static boolean is是否允许顶号() {
        return ServerConstants.是否允许顶号;
    }
    
    public static String is允许顶号() {
        ServerConstants.是否允许顶号 = !is是否允许顶号();
        return ServerConstants.是否允许顶号 ? "开启" : "关闭";
    }
    
    public static boolean is是否允许使用管理员命令() {
        return ServerConstants.是否允许使用管理员命令;
    }
    
    public static String is允许使用管理员命令() {
        ServerConstants.是否允许使用管理员命令 = !is是否允许使用管理员命令();
        return ServerConstants.是否允许使用管理员命令 ? "开启" : "关闭";
    }
    
    public static boolean get分级系统() {
        return ServerConstants.分级系统;
    }
    
    public static String is分级系统() {
        ServerConstants.分级系统 = !get分级系统();
        return ServerConstants.分级系统 ? "开启" : "关闭";
    }
    
    public void setPACKET_ERROR(final String ERROR) {
        ServerConstants.PACKET_ERROR = ERROR;
    }
    
    public String getPACKET_ERROR() {
        return ServerConstants.PACKET_ERROR;
    }
    
    public void setChannel(final int ERROR) {
        ServerConstants.Channel = ERROR;
    }
    
    public int getChannel() {
        return ServerConstants.Channel;
    }
    
    public void setRemovePlayerFromMap(final int ERROR) {
        ServerConstants.removePlayerFromMap = ERROR;
    }
    
    public int getRemovePlayerFromMap() {
        return ServerConstants.removePlayerFromMap;
    }
    
    public void setHello(final int ERROR) {
        ServerConstants.getHello = ERROR;
    }
    
    public int getHello() {
        return ServerConstants.getHello;
    }
    
    public static boolean PK系统() {
        return ServerConstants.PK系统;
    }
    
    public static void isPK系统(final boolean PK系统1) {
        ServerConstants.PK系统 = PK系统1;
    }
    
    public static boolean get法师破攻() {
        return ServerConstants.法师破攻;
    }
    
    public static String is法师破攻() {
        ServerConstants.法师破攻 = !get法师破攻();
        return ServerConstants.法师破攻 ? "开启" : "关闭";
    }
    
    public static boolean getAutoReg() {
        return ServerConstants.自动注册;
    }
    
    public static String ChangeAutoReg() {
        ServerConstants.自动注册 = !getAutoReg();
        return ServerConstants.自动注册 ? "开启" : "关闭";
    }
    
    public static boolean get破功() {
        return ServerConstants.破功;
    }
    
    public static void Change破功(final boolean 破功1) {
        ServerConstants.破功 = 破功1;
    }
    
    public static boolean get登录公告() {
        return ServerConstants.登录公告;
    }
    
    public static void Change登录公告(final boolean 破功1) {
        ServerConstants.登录公告 = 破功1;
    }
    
    public static boolean get38记录() {
        return ServerConstants.PACKET_ERROR_OFF;
    }
    
    public static String Change38记录() {
        ServerConstants.PACKET_ERROR_OFF = !get38记录();
        return ServerConstants.PACKET_ERROR_OFF ? "开启" : "关闭";
    }
    
    public static boolean 战斗力() {
        return ServerConstants.战斗力;
    }
    
    public static void is战斗力(final boolean 武力值) {
        ServerConstants.战斗力 = 武力值;
    }
    
    public static boolean get天气() {
        return ServerConstants.天气功能;
    }
    
    public static String Change天气() {
        ServerConstants.天气功能 = !get天气();
        return ServerConstants.天气功能 ? "开启" : "关闭";
    }
    
    public static boolean get调试封包() {
        return ServerConstants.调试输出封包;
    }
    
    public static void is调试封包(final boolean 调试输出封包1) {
        ServerConstants.调试输出封包 = 调试输出封包1;
    }
    
    public static boolean get怪物掉落抵用卷() {
        return ServerConstants.怪物掉落抵用卷;
    }
    
    public static void is怪物掉落抵用卷(final boolean 怪物掉落抵用卷1) {
        ServerConstants.怪物掉落抵用卷 = 怪物掉落抵用卷1;
    }
    
    public static boolean get怪物掉落点卷() {
        return ServerConstants.怪物掉落点卷;
    }
    
    public static void is怪物掉落点卷(final boolean 怪物掉落抵用卷1) {
        ServerConstants.怪物掉落点卷 = 怪物掉落抵用卷1;
    }
    
    public static final byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 3000:
            case 3200:
            case 3210:
            case 3211:
            case 3212:
            case 3300:
            case 3310:
            case 3311:
            case 3312:
            case 3500:
            case 3510:
            case 3511:
            case 3512: {
                return 10;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getCSNpcID() {
        if (ServerConstants.CHANNEL_ENTERNPC_CASHSHOP.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(ServerConstants.CHANNEL_ENTERNPC_CASHSHOP.split("_")[0]);
    }
    
    public static String getCSNpcID_Mode() {
        return ServerConstants.CHANNEL_ENTERNPC_CASHSHOP.contains("_") ? ServerConstants.CHANNEL_ENTERNPC_CASHSHOP.split("_")[1] : null;
    }
    
    public static Properties getDefaultProperties(final String ops) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ops);
            props.load(fis);
        }
        catch (FileNotFoundException ex) {
            ServerConstants.log.error(ops, (Throwable)ex);
        }
        catch (IOException ex2) {
            ServerConstants.log.error(ops, (Throwable)ex2);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (IOException ex3) {
                ServerConstants.log.error(ops, (Throwable)ex3);
            }
        }
        return props;
    }
    
    static {
        log = LoggerFactory.getLogger(ServerConstants.class);
        Poll_Answers = new String[] { "test1", "test2", "test3" };
        ServerConstants.MAPLE_TYPE = MapleType.中国;
        ServerConstants.Use_Fixed_IV = false;
        ServerConstants.USE_FIXED_IV = true;
        ServerConstants.PACKET_ERROR = "";
        ServerConstants.Channel = 0;
        ServerConstants.removePlayerFromMap = 0;
        ServerConstants.getHello = 0;
        ServerConstants.PACKET_ERROR_OFF = Boolean.parseBoolean(ServerProperties.getProperty("tms.记录38错误", "false"));
        ServerConstants.封包显示 = Boolean.parseBoolean(ServerProperties.getProperty("tms.封包显示", "false"));
        ServerConstants.调试输出封包 = Boolean.parseBoolean(ServerProperties.getProperty("tms.调试输出封包", "false"));
        ServerConstants.破功 = Boolean.parseBoolean(ServerProperties.getProperty("world.破功", "false"));
        ServerConstants.天气功能 = Boolean.parseBoolean(ServerProperties.getProperty("tms.天气效果", "false"));
        ServerConstants.战斗力 = Boolean.parseBoolean(ServerProperties.getProperty("tms.战斗力记录", "false"));
        ServerConstants.自动注册 = Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister", "false"));
        ServerConstants.怪物掉落抵用卷 = Boolean.parseBoolean(ServerProperties.getProperty("tms.怪物掉落抵用卷", "false"));
        ServerConstants.怪物掉落点卷 = Boolean.parseBoolean(ServerProperties.getProperty("tms.怪物掉落点卷", "false"));
        ServerConstants.加速怪物刷新模式 = Boolean.parseBoolean(ServerProperties.getProperty("world.加速刷怪模式", "false"));
        ServerConstants.复古刷怪 = Boolean.parseBoolean(ServerProperties.getProperty("world.复古刷怪模式", "false"));
        ServerConstants.登录公告 = Boolean.parseBoolean(ServerProperties.getProperty("tms.玩家登录公告", "false"));
        泡点地图 = Integer.parseInt(ServerProperties.getProperty("world.泡点地图", "910000000"));
        ServerConstants.Super_password = false;
        ServerConstants.PK系统 = Boolean.parseBoolean(ServerProperties.getProperty("channel.PK系统", "false"));
        ServerConstants.monsterSpawn = 100;
        ServerConstants.分级系统 = Boolean.parseBoolean(ServerProperties.getProperty("world.等级分布系统", "false"));
        ServerConstants.superpw = "";
        万能限制 = Boolean.parseBoolean(ServerProperties.getProperty("channel.开启万能检测", "false"));
        出生地图 = Integer.parseInt(ServerProperties.getProperty("channel.beginnermap"));
        ServerConstants.怪物刷新时间 = Integer.parseInt(ServerProperties.getProperty("channel.怪物刷新时间"));
        ServerConstants.法师破攻 = Boolean.parseBoolean(ServerProperties.getProperty("world.法师破攻", "false"));
        ServerConstants.是否允许顶号 = Boolean.parseBoolean(ServerProperties.getProperty("channel.是否允许顶号", "false"));
        ServerConstants.是否允许使用管理员命令 = Boolean.parseBoolean(ServerProperties.getProperty("channel.是否允许使用管理员命令", "false"));
        LOGIN_DEFAULTUSERLIMIT = Integer.parseInt(ServerProperties.getProperty("world.虚假在线人数", "100"));
        ServerConstants.给怪物BUFF = Boolean.parseBoolean(ServerProperties.getProperty("channel.cashshop", "false"));
        ServerConstants.地图最大怪物数量 = 400;
        ServerConstants.地图出现怪物倍率 = Integer.parseInt(ServerProperties.getProperty("channel.怪物数量倍率", "1"));
        ServerConstants.每日免费复活次数 = Integer.parseInt(ServerProperties.getProperty("channel.每日免费复活次数", "1"));
        ServerConstants.时间 = Integer.parseInt(ServerProperties.getProperty("channel.店铺时间"));
        ServerConstants.限制登陆 = Boolean.parseBoolean(ServerProperties.getProperty("channel.开启万能检测", "false"));
        ServerConstants.限制登陆1 = Boolean.parseBoolean(ServerProperties.getProperty("channel.开启万能检测1", "false"));
        ServerConstants.吸怪检测 = Boolean.parseBoolean(ServerProperties.getProperty("world.吸怪检测", "false"));
        ServerConstants.封号系统 = Boolean.parseBoolean(ServerProperties.getProperty("world.封号检测", "false"));
        Channel_Key = new String[] { "a56=-_dcSAgb", "y5(9=8@nV$;G", "yS5j943GzdUm", "G]R8Frg;kx6Y", "Z)?7fh*([N6S", "p4H8=*sknaEK", "A!Z7:mS.2?Kq", "M5:!rfv[?mdF", "Ee@3-7u5s6xy", "p]6L3eS(R;8A", "gZ,^k9.npy#F", "cG3M,*7%@zgt", "t+#@TV^3)hL9", "mw4:?sAU7[!6", "b6L]HF(2S,aE", "H@rAq]#^Y3+J", "o2A%wKCuqc7Txk5?#rNZ", "d4.Np*B89C6+]y2M^z-7", "oTL2jy9^zkH.84u(%b[d", "WCSJZj3tGX,[4hu;9s?g" };
        ServerConstants.CHANNEL_ENTERNPC_CASHSHOP = ServerProperties.getProperty("channel.server.enternpc_cashshop", "0");
    }
    
    public enum MapleType
    {
        中国(4, "GB18030");
        
        final byte type;
        final String ascii;
        
        private MapleType(final int type, final String ascii) {
            this.type = (byte)type;
            this.ascii = ascii;
        }
        
        public String getAscii() {
            return this.ascii;
        }
        
        public byte getType() {
            return this.type;
        }
        
        public static MapleType getByType(final byte type) {
            for (final MapleType l : values()) {
                if (l.getType() == type) {
                    return l;
                }
            }
            return MapleType.中国;
        }
    }
    
    public enum PlayerGMRank
    {
        NORMAL('@', 0), 
        INTERN('!', 1), 
        GM('!', 2), 
        SUPERGM('!', 4), 
        ADMIN('!', 3);
        
        private final char commandPrefix;
        private final int level;
        
        private PlayerGMRank(final char ch, final int level) {
            this.commandPrefix = ch;
            this.level = level;
        }
        
        public static PlayerGMRank getByLevel(final int level) {
            for (final PlayerGMRank i : values()) {
                if (i.getLevel() == level) {
                    return i;
                }
            }
            return PlayerGMRank.NORMAL;
        }
        
        public char getCommandPrefix() {
            return this.commandPrefix;
        }
        
        public int getLevel() {
            return this.level;
        }
    }
    
    public enum CommandType
    {
        NORMAL(0), 
        TRADE(1);
        
        private final int level;
        
        private CommandType(final int level) {
            this.level = level;
        }
        
        public int getType() {
            return this.level;
        }
    }
}
