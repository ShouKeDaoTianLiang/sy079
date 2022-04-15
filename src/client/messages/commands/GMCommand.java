package client.messages.commands;

import handling.MaplePacket;
import handling.world.World.Broadcast;
import java.util.Map;
import java.util.Map.Entry;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import tools.Pair;
import tools.ArrayMap;
import java.util.Collection;
import client.MapleCharacterUtil;
import handling.world.World.Find;
import client.inventory.MaplePet;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import server.ServerProperties;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import tools.CPUSampler;
import java.util.Iterator;
import client.MapleCharacter;
import server.Timer.EventTimer;
import tools.MaplePacketCreator;
import client.MapleStat;
import client.MapleClient;
import constants.ServerConstants.PlayerGMRank;

public class GMCommand
{
    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }
    
    public static class 拉 extends WarpHere
    {
    }
    
    public static class 等级 extends Level
    {
    }
    
    public static class 转职 extends Job
    {
    }
    
    public static class 清空 extends ClearInv
    {
    }
    
    public static class 踢人 extends DC
    {
    }
    
    public static class 读取玩家 extends spy
    {
    }
    
    public static class 玩家信息 extends spy1
    {
    }
    
    public static class 在线人数 extends Online
    {
    }
    
    public static class 解除封号 extends UnBan
    {
    }
    
    public static class 刷钱 extends GainMeso
    {
    }
    
    public static class 设置VIP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setVip(Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage("VIP设置成功当前; " + c.getPlayer().getVip() + "");
            return 1;
        }
    }
    
    public static class 设置管理员 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setVip(Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage("VIP设置成功当前; " + c.getPlayer().getVip() + "");
            return 1;
        }
    }
    
    public static class 设置血量 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final int stat = Integer.parseInt(splitted[1]);
            c.getPlayer().getStat().setMaxHp((short)stat);
            c.getPlayer().updateSingleStat(MapleStat.MAXHP, (short)stat);
            return 1;
        }
    }
    
    public static class 血量 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final int stat = Integer.parseInt(splitted[1]);
            c.getPlayer().getStat().setHp((short)stat);
            c.getPlayer().updateSingleStat(MapleStat.HP, (short)stat);
            return 1;
        }
    }
    
    public static class 设置蓝量 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final int stat = Integer.parseInt(splitted[1]);
            c.getPlayer().getStat().setMaxMp((short)stat);
            c.getPlayer().updateSingleStat(MapleStat.MAXMP, (short)stat);
            return 1;
        }
    }
    
    public static class 定时更变地图 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 4) {
                c.getPlayer().dropMessage(6, splitted[0] + " <初始地图ID> <更变后的地图ID> <时间:秒>");
                return 0;
            }
            final int map = Integer.parseInt(splitted[1]);
            final int nextmap = Integer.parseInt(splitted[2]);
            final int time = Integer.parseInt(splitted[3]);
            c.getChannelServer().getMapFactory().getMap(map).broadcastMessage(MaplePacketCreator.getClock(time));
            c.getChannelServer().getMapFactory().getMap(map).startMapEffect("计时结束后你将被传送离开此地图。", 5120041);
            EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (final MapleCharacter mch : c.getChannelServer().getMapFactory().getMap(map).getCharacters()) {
                        if (mch == null) {
                            return;
                        }
                    }
                }
            }, time * 1000);
            return 1;
        }
    }
    
    public static class 关闭服务器性能 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                final File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "输入的文件名字已经存在，请重新输入1个新的文件名。");
                    return 0;
                }
                sampler.stop();
                final FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            }
            catch (IOException e) {
                System.err.println("保存文件出错." + e);
            }
            sampler.reset();
            c.getPlayer().dropMessage(6, "已经停止服务端性能监测.");
            return 1;
        }
    }
    
    public static class 服务器性能 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants");
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            c.getPlayer().dropMessage(6, "已经开启服务端性能监测.");
            return 1;
        }
    }
    
    public static class 给所有人发送留言 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length >= 2) {
                final String text = StringUtil.joinStringFrom(splitted, 1);
                for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
                return 1;
            }
            c.getPlayer().dropMessage(6, splitted[0] + " <内容>");
            return 0;
        }
    }
    
    public static class 开关喇叭 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "喇叭状态 : " + (c.getChannelServer().getMegaphoneMuteState() ? "不可用" : "可用"));
            return 1;
        }
    }
    
    public static class 给所有人经验 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            final int quantity = Integer.parseInt(splitted[1]);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainExp(quantity, true, false, true);
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "经验给在线的所以玩家！快感谢管理员吧！", 5121020);
                }
            }
            return 1;
        }
    }
    
    public static class 弹窗 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (splitted.length <= 1) {
                    c.getPlayer().dropMessage(6, splitted[0] + " <内容>");
                    return 0;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                mch.dropMessage(1, sb.toString());
            }
            return 1;
        }
    }
    
    public static class 地图禁言 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                chr.canTalk(false);
                c.getPlayer().dropMessage(6, "地图禁言成功");
            }
            return 1;
        }
    }
    
    public static class 取消地图禁言 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                chr.canTalk(true);
                c.getPlayer().dropMessage(6, "地图解除禁言成功");
            }
            return 1;
        }
    }
    
    public static class 禁言 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " <玩家名字>");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.canTalk(false);
            c.getPlayer().dropMessage(6, splitted[1] + "已被禁言");
            return 1;
        }
    }
    
    public static class 取消禁言 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " <玩家名字>");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.canTalk(true);
            c.getPlayer().dropMessage(6, splitted[1] + "已解除禁言");
            return 1;
        }
    }
    
    public static class 监狱 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, splitted[0] + " <玩家名字> <时间(分钟,0为永久)>");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int minutes = Math.max(0, Integer.parseInt(splitted[2]));
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                final MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
                victim.getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData(String.valueOf(minutes * 60));
                victim.changeMap(target, target.getPortal(0));
                return 1;
            }
            c.getPlayer().dropMessage(6, "请到玩家所在的频道");
            return 0;
        }
    }
    
    public static class 给当前地图点卷 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给当前地图点卷 [点卷类型1：点卷 - 2：抵用] [点卷数量]");
                return 0;
            }
            int type = Integer.parseInt(splitted[1]);
            int quantity = Integer.parseInt(splitted[2]);
            if (type <= 0 || type > 2) {
                type = 2;
            }
            if (quantity > 9000) {
                quantity = 9000;
            }
            int ret = 0;
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        mch.modifyCSPoints(type, quantity, false);
                        mch.dropMessage(-11, "[系统提示] 恭喜您获得管理员赠送给您的" + ((type == 1) ? "点券 " : " 抵用券 ") + quantity + " 点.");
                        ++ret;
                    }
                }
            }
            if (type == 1) {
                for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                        if (c.getPlayer().getMapId() == mch.getMapId()) {
                            mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "点卷给当前地图在线的所以玩家！快感谢管理员吧！", 5120004);
                        }
                    }
                }
            }
            else if (type == 2) {
                for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                        if (c.getPlayer().getMapId() == mch.getMapId()) {
                            mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "抵用卷给当前地图在线的所以玩家！快感谢管理员吧！", 5120004);
                        }
                    }
                }
            }
            c.getPlayer().dropMessage(6, "命令使用成功，当前共有: " + ret + " 个玩家获得: " + quantity + " 点的" + ((type == 1) ? "点券 " : " 抵用券 ") + " 总计: " + ret * quantity);
            return 1;
        }
    }
    
    public static class 给当前地图经验 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            final int quantity = Integer.parseInt(splitted[1]);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        mch.gainExp(quantity, true, false, true);
                    }
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "经验给当前地图在线的所以玩家！快感谢管理员吧！", 5121020);
                    }
                }
            }
            return 1;
        }
    }
    
    public static class 给当前地图冒险币 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            final int quantity = Integer.parseInt(splitted[1]);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        mch.gainMeso(quantity, true);
                    }
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "冒险币给当前地图在线的所以玩家！快感谢管理员吧！", 5121020);
                    }
                }
            }
            return 1;
        }
    }
    
    public static class 给当前地图物品 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给当前地图物品 [物品ID] [数量]");
                return 0;
            }
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final int item = Integer.parseInt(splitted[1]);
            final int quantity = Integer.parseInt(splitted[2]);
            final String mz = ii.getName(item);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.getPlayer().getMapId() == mch.getMapId()) {
                        MapleInventoryManipulator.输出道具(mch.getClient(), item, (short)quantity, "", null, 0L, "管理员发放奖励 " + mz);
                    }
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    if (quantity <= 1) {
                        if (c.getPlayer().getMapId() != mch.getMapId()) {
                            continue;
                        }
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放【" + mz + "】物品给当前地图在线的所以玩家！快感谢管理员吧！", 5120000);
                    }
                    else {
                        if (c.getPlayer().getMapId() != mch.getMapId()) {
                            continue;
                        }
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放【" + mz + "】物品【" + quantity + "】个给当前地图在线的所以玩家！快感谢管理员吧！", 5120000);
                    }
                }
            }
            return 1;
        }
    }
    
    public static class 给玩家物品 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (c.getPlayer().getGMLevel() == 1) {
                c.getPlayer().dropMessage(1, "肥水不流外人田。所以你的东西不能丢弃。");
                c.sendPacket(MaplePacketCreator.enableActions());
                return 0;
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "用法: !给玩家物品 [角色名字][物品ID] [数量]");
                return 0;
            }
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final String name = splitted[1];
            final int item = Integer.parseInt(splitted[2]);
            final int quantity = Integer.parseInt(splitted[3]);
            final String mz = ii.getName(item);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (mch.getName().equals(name)) {
                        MapleInventoryManipulator.addById(mch.getClient(), item, (short)quantity, (byte)0);
                        c.getPlayer().dropMessage(6, "给予成功！");
                    }
                }
            }
            return 1;
        }
    }
    
    public static class 给所有人冒险币 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            final int quantity = Integer.parseInt(splitted[1]);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(quantity, true);
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放" + quantity + "冒险币给在线的所以玩家！快感谢管理员吧！", 5121020);
                }
            }
            return 1;
        }
    }
    
    public static class 给所有人物品 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (!c.getPlayer().isAdmin()) {
                return 0;
            }
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给所有人物品 [物品ID] [数量]");
                return 0;
            }
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final int item = Integer.parseInt(splitted[1]);
            final int quantity = Integer.parseInt(splitted[2]);
            final String mz = ii.getName(item);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    MapleInventoryManipulator.输出道具(mch.getClient(), item, (short)quantity, "", null, 0L, "管理员发放奖励 " + mz);
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    if (quantity <= 1) {
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放【" + mz + "】物品给在线的所以玩家！快感谢管理员吧！", 5120000);
                    }
                    else {
                        mch.startMapEffect(ServerProperties.getProperty("tms.ServerName") + "管理员发放【" + mz + "】物品【" + quantity + "】个给在线的所以玩家！快感谢管理员吧！", 5120000);
                    }
                }
            }
            return 1;
        }
    }
    
    public static class WarpHere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            }
            else {
                final int ch = Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "角色不在线");
                    return 1;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                c.getPlayer().dropMessage(5, "正在传送玩家到身边");
                if (victim.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    victim.changeMap(mapp, mapp.getPortal(0));
                }
                victim.changeChannel(c.getChannel());
            }
            return 1;
        }
    }
    
    public static class UnBan extends CommandExecute
    {
        protected boolean hellban;
        
        public UnBan() {
            this.hellban = false;
        }
        
        private String getCommand() {
            return "UnBan";
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[Syntax] !" + this.getCommand() + " <原因>");
                return 0;
            }
            byte ret;
            if (this.hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            }
            else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] SQL error.");
                return 0;
            }
            if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] The character does not exist.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] Successfully unbanned!");
            final byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            }
            else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            }
            else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            }
            else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            }
            else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return (ret_ > 0) ? 1 : 0;
        }
    }
    
    public static class DC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            ChannelServer.forceRemovePlayerByCharName(splitted[1]);
            c.getPlayer().dropMessage("解除卡号卡角成功");
            return 1;
        }
    }
    
    public static class Job extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().changeJob(Integer.parseInt(splitted[1]));
            return 1;
        }
    }
    
    public static class 给予转职 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给予转职 <玩家名字> <职业>");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.changeJob(Short.parseShort(splitted[2]));
            return 1;
        }
    }
    
    public static class GainMeso extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return 1;
        }
    }
    
    public static class Level extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }
    
    public static class 给予等级 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给予等级 <玩家名字> <等级>");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.setLevel(Short.parseShort(splitted[2]));
            victim.levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }
    
    public static class spy extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用规则: !spy <玩家名字>");
            }
            else {
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (victim.getGMLevel() > c.getPlayer().getGMLevel() && c.getPlayer().getId() != victim.getId()) {
                    c.getPlayer().dropMessage(5, "你不能查看比你高权限的人!");
                    return 0;
                }
                if (victim != null) {
                    c.getPlayer().dropMessage(5, "此玩家(" + victim.getId() + ")状态:");
                    c.getPlayer().dropMessage(5, "玩家HP:" + victim.getStat().getHp() + "/" + victim.getStat().getCurrentMaxHp() + "");
                    c.getPlayer().dropMessage(5, "玩家MP:" + victim.getStat().getMp() + "/" + victim.getStat().getCurrentMaxMp() + "");
                    c.getPlayer().dropMessage(5, "等級: " + victim.getLevel() + "职业: " + victim.getJob() + "名声: " + victim.getFame());
                    c.getPlayer().dropMessage(5, "地图: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                    c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  运气: " + victim.getStat().getLuk());
                    c.getPlayer().dropMessage(5, "拥有 " + victim.getMeso() + " 金币.");
                }
                else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return 1;
        }
    }
    
    public static class spy1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用规则: !spy <ID>");
            }
            else {
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
                if (victim.getGMLevel() > c.getPlayer().getGMLevel() && c.getPlayer().getId() != victim.getId()) {
                    c.getPlayer().dropMessage(5, "你不能查看比你高权限的人!");
                    return 0;
                }
                if (victim != null) {
                    c.getPlayer().dropMessage(5, "此玩家(" + victim.getId() + ")状态:");
                    c.getPlayer().dropMessage(5, "玩家HP:" + victim.getStat().getHp() + "/" + victim.getStat().getCurrentMaxHp() + "");
                    c.getPlayer().dropMessage(5, "玩家MP:" + victim.getStat().getMp() + "/" + victim.getStat().getCurrentMaxMp() + "");
                    c.getPlayer().dropMessage(5, "等級: " + victim.getLevel() + "职业: " + victim.getJob() + "名声: " + victim.getFame());
                    c.getPlayer().dropMessage(5, "IP: " + victim.getClient().getSessionIPAddress() + " .");
                }
                else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return 1;
        }
    }
    
    public static class Online extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage("在线人物: ");
            for (final ChannelServer cs : ChannelServer.getAllInstances()) {
                c.getPlayer().dropMessage("[频道 " + cs.getChannel() + "]");
                StringBuilder sb = new StringBuilder();
                final Collection<MapleCharacter> cmc = cs.getPlayerStorage().getAllCharacters();
                for (final MapleCharacter chr : cmc) {
                    if (sb.length() > 150) {
                        sb.setLength(sb.length() - 2);
                        c.getPlayer().dropMessage(sb.toString());
                        sb = new StringBuilder();
                    }
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable("【ID】:" + chr.getId() + "," + chr.getName()));
                        if (chr.getMap() != null) {
                            sb.append("(").append(chr.getMap().getMapName()).append(") ");
                        }
                        sb.append(" 丨 ");
                    }
                }
                if (sb.length() >= 2) {
                    sb.setLength(sb.length() - 2);
                    c.getPlayer().dropMessage(sb.toString());
                }
            }
            return 1;
        }
    }
    
    public static class ClearInv extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<Pair<Short, Short>, MapleInventoryType>();
            final String s = splitted[1];
            switch (s) {
                case "全部": {
                    for (final MapleInventoryType type : MapleInventoryType.values()) {
                        for (final Item item : c.getPlayer().getInventory(type)) {
                            eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), type);
                        }
                    }
                    break;
                }
                case "已装备道具": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.EQUIPPED);
                    }
                    break;
                }
                case "武器": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.EQUIP);
                    }
                    break;
                }
                case "消耗": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.USE);
                    }
                    break;
                }
                case "装饰": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.SETUP);
                    }
                    break;
                }
                case "其他": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.ETC);
                    }
                    break;
                }
                case "特殊": {
                    for (final Item item2 : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                        eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.CASH);
                    }
                    break;
                }
                default: {
                    c.getPlayer().dropMessage(6, "[全部/已装备道具/武器/消耗/装饰/其他/特殊]");
                    break;
                }
            }
            for (final Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return 1;
        }
    }
    
    public static class 黄字事项 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " (对象:默认w) <内容>");
                c.getPlayer().dropMessage(6, splitted[0] + "对象:地图所有人 - m/频道所有人 - c/服务器所有人 - w");
                return 0;
            }
            int range = -1;
            final String s = splitted[1];
            switch (s) {
                case "m": {
                    range = 0;
                    break;
                }
                case "c": {
                    range = 1;
                    break;
                }
                case "w": {
                    range = 2;
                    break;
                }
            }
            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            final MaplePacket packet = MaplePacketCreator.yellowChat((splitted[0].equals("!带名黄字事项") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, tfrom));
            switch (range) {
                case 0: {
                    c.getPlayer().getMap().broadcastMessage(packet);
                    break;
                }
                case 1: {
                    ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
                    break;
                }
                case 2: {
                    Broadcast.broadcastMessage(packet);
                    break;
                }
            }
            return 1;
        }
    }
    
    public static class 带名黄字事项 extends 黄字事项
    {
    }
    
    public static class 命令 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "!移除玩家身上道具 <物品ID> <角色名稱> - 移除玩家身上的道具");
            c.getPlayer().dropMessage(5, "!定时更变地图  <初始地图ID> <更变后的地图ID> <时间:秒>");
            c.getPlayer().dropMessage(5, "!开放地图");
            c.getPlayer().dropMessage(5, "!关闭地图");
            c.getPlayer().dropMessage(5, "!玩家npc <playername> <npcid> - 创造玩家NPC");
            c.getPlayer().dropMessage(5, "!给所有人发送留言  - 留言内容");
            c.getPlayer().dropMessage(5, "!开启自动活动 - 开启自动活动");
            c.getPlayer().dropMessage(5, "!活动开始 - 活动开始");
            c.getPlayer().dropMessage(5, "!关闭活动入口 -关闭活动入口");
            c.getPlayer().dropMessage(5, "!选择活动 - 选择活动");
            c.getPlayer().dropMessage(5, "!在线人数 - ");
            c.getPlayer().dropMessage(5, "!读取玩家 - <玩家名字>");
            c.getPlayer().dropMessage(5, "!复制玩家装备 玩家名称 装备栏位(0 = 装备中 1=装备栏 2=消耗栏 3=其他栏 4=装饰栏 5=点数栏)(预设装备栏) - 复制玩家道具");
            c.getPlayer().dropMessage(5, "!永久npc - 建立永久NPC");
            c.getPlayer().dropMessage(5, "!解锁玩家 <玩家名称> - 解锁玩家");
            c.getPlayer().dropMessage(5, "!开关喇叭 - 开启或关闭喇叭功能");
            c.getPlayer().dropMessage(5, "!弹窗 - <内容>");
            c.getPlayer().dropMessage(5, "!监狱 - <玩家名字> <时间(分钟,0为永久)>");
            c.getPlayer().dropMessage(5, "!给所有人发送留言  - <内容>");
            c.getPlayer().dropMessage(5, "!设置天气  - <内容> ");
            c.getPlayer().dropMessage(5, "!脱掉所有人  - <内容>");
            c.getPlayer().dropMessage(5, "!禁言  - <玩家名字>");
            c.getPlayer().dropMessage(5, "!取消禁言  - <玩家名字>");
            c.getPlayer().dropMessage(5, "!开关喇叭");
            return 1;
        }
    }
}
