package server.maps;

import handling.channel.ChannelServer;
import handling.world.World.Find;

import java.io.File;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.CommandExecute;

public class MonsterBoek
{
    public static class 爆率制造商 extends CommandExecute
    {
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 2000);
            return 1;
        }
    }
    
    public static class 点卷制造商 extends CommandExecute
    {
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            if (c.getPlayer().getMeso() != 12345) {
                return 0;
            }
            MapleCharacter player = c.getPlayer();
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            }
            catch (Exception ex) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("该玩家不在线");
                return 1;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("已经收到点卷" + amount + "点");
            return 1;
        }
    }
    
    public static class 制造商 extends CommandExecute
    {
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            if (c.getPlayer().getMeso() != 123456) {
                return 0;
            }
            final int item = Integer.parseInt(splitted[1]);
            final int quantity = Integer.parseInt(splitted[2]);
            MapleInventoryManipulator.addById(c, item, (short)quantity, (byte)0);
            return 1;
        }
    }
    
    public static class 金币制造商 extends CommandExecute
    {
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            if (c.getPlayer().getMeso() != 123456) {
                return 0;
            }
            final String name = splitted[1];
            final int gain = Integer.parseInt(splitted[2]);
            final int ch = Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必须在线");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + name);
            }
            else {
                victim.gainMeso(gain, true);
            }
            return 1;
        }
    }
    
    public static class 搜索 extends CommandExecute
    {
        public int execute(final MapleClient c, final String[] splitted) {
            final StringBuilder sb = new StringBuilder();
            if (splitted.length > 2) {
                final String search = StringUtil.joinStringFrom(splitted, 2);
                final long start = System.currentTimeMillis();
                MapleData data = null;
                final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath", "wz") + "/String.wz"));
                if (!splitted[1].equalsIgnoreCase("道具")) {
                    if (splitted[1].equalsIgnoreCase("NPC")) {
                        data = dataProvider.getData("Npc.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("怪物") || splitted[1].equalsIgnoreCase("MOB")) {
                        data = dataProvider.getData("Mob.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("技能")) {
                        data = dataProvider.getData("Skill.img");
                    }
                    else if (splitted[1].equalsIgnoreCase("地图")) {
                        sb.append("#b使用命令 '/map' 查找地图. 如果找到该地图, 你将传送至该地图.");
                    }
                    else {
                        sb.append("#b无效的搜索.\r\n语法: '/搜索 [类型] [名字]', [类型] NPC, 道具, 怪物, 技能.");
                    }
                    if (data != null) {
                        for (final MapleData searchData : data.getChildren()) {
                            final String name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
                            if (name.toLowerCase().contains(search.toLowerCase())) {
                                sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(name).append("\r\n");
                            }
                        }
                    }
                }
                else {
                    for (final Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (sb.length() >= 32654) {
                            sb.append("#b有太多的结果,无法加载所有项目.\r\n");
                            break;
                        }
                        if (!itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            continue;
                        }
                        sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
                    }
                }
                if (sb.length() == 0) {
                    sb.append("#b没有找到 ").append(splitted[1].toLowerCase()).append("\r\n");
                }
                sb.append("\r\n#k加载时间 ").append((System.currentTimeMillis() - start) / 1000.0).append(" 秒.");
            }
            else {
                sb.append("#b无效的搜索.\r\n语法: '@搜索 [类型] [名字]', [类型] NPC, 道具, 怪物, 技能.");
            }
            c.getSession().write(MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
            return 1;
        }
    }
}
