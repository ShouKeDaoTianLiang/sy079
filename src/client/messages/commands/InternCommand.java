package client.messages.commands;

import server.MaplePortal;
import java.util.HashMap;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import java.text.DateFormat;
import java.util.Calendar;
import handling.world.World.Broadcast;
import tools.FileoutputUtil;
import handling.world.World.Find;
import handling.channel.ChannelServer;
import client.MapleCharacter;
import server.CashItemFactory;
import java.util.Iterator;
import java.util.List;
import provider.MapleDataProvider;
import tools.MaplePacketCreator;
import handling.RecvPacketOpcode;
import tools.HexTool;
import handling.SendPacketOpcode;
import client.ISkill;
import client.SkillFactory;
import server.MapleItemInformationProvider;
import provider.MapleDataTool;
import provider.MapleData;
import tools.Pair;
import java.util.LinkedList;
import java.util.ArrayList;
import provider.MapleDataProviderFactory;
import java.io.File;
import tools.StringUtil;
import client.MapleClient;
import constants.ServerConstants.PlayerGMRank;

public class InternCommand
{
    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.INTERN;
    }
    
    public static class 跟踪 extends Warp
    {
    }
    
    public static class 跟踪1 extends Warp1
    {
    }
    
    public static class 封号 extends Ban
    {
    }
    
    public static class 封号1 extends Ban1
    {
    }
    
    public static class 隐身 extends Hide
    {
    }
    
    public static class 解除隐身 extends UnHide
    {
    }
    
    public static class 在线 extends online
    {
    }
    
    public static class 查找 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length == 1) {
                c.getPlayer().dropMessage(6, splitted[0] + ": <类型> <搜索信息>");
                c.getPlayer().dropMessage(6, "类型:NPC/怪物/物品/地图/技能/任务/包头");
            }
            else if (splitted.length == 2) {
                c.getPlayer().dropMessage(6, "请提供搜索信息");
            }
            else {
                final String type = splitted[1];
                final String search = StringUtil.joinStringFrom(splitted, 2);
                final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath", "wz") + "/String.wz"));
                final StringBuilder sb = new StringBuilder();
                sb.append("<<类型: ").append(type).append(" | 搜索信息: ").append(search).append(">>");
                if (type.equalsIgnoreCase("NPC")) {
                    final List<String> retNpcs = new ArrayList<String>();
                    final MapleData data = dataProvider.getData("Npc.img");
                    final List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                    for (final MapleData npcIdData : data.getChildren()) {
                        npcPairList.add(new Pair<Integer, String>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "无名字")));
                    }
                    for (final Pair<Integer, String> npcPair : npcPairList) {
                        if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retNpcs.add("\r\n" + npcPair.getLeft() + " - " + npcPair.getRight());
                        }
                    }
                    if (retNpcs.size() > 0) {
                        for (final String singleRetNpc : retNpcs) {
                            if (sb.length() > 10000) {
                                sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                                break;
                            }
                            sb.append(singleRetNpc);
                        }
                    }
                    else {
                        c.getPlayer().dropMessage(6, "该NPC信息搜索不到");
                    }
                }
                else if (type.equalsIgnoreCase("地图")) {
                    final List<String> retMaps = new ArrayList<String>();
                    final MapleData data = dataProvider.getData("Map.img");
                    final List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                    for (final MapleData mapAreaData : data.getChildren()) {
                        for (final MapleData mapIdData : mapAreaData.getChildren()) {
                            mapPairList.add(new Pair<Integer, String>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                        }
                    }
                    for (final Pair<Integer, String> mapPair : mapPairList) {
                        if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMaps.add("\r\n" + mapPair.getLeft() + " - " + mapPair.getRight());
                        }
                    }
                    if (retMaps.size() > 0) {
                        for (final String singleRetMap : retMaps) {
                            if (sb.length() > 10000) {
                                sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                                break;
                            }
                            sb.append(singleRetMap);
                        }
                    }
                    else {
                        c.getPlayer().dropMessage(6, "该地图信息搜索不到");
                    }
                }
                else if (type.equalsIgnoreCase("怪物")) {
                    final List<String> retMobs = new ArrayList<String>();
                    final MapleData data = dataProvider.getData("Mob.img");
                    final List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                    for (final MapleData mobIdData : data.getChildren()) {
                        mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (final Pair<Integer, String> mobPair : mobPairList) {
                        if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMobs.add("\r\n" + mobPair.getLeft() + " - " + mobPair.getRight());
                        }
                    }
                    if (retMobs.size() > 0) {
                        for (final String singleRetMob : retMobs) {
                            if (sb.length() > 10000) {
                                sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                                break;
                            }
                            sb.append(singleRetMob);
                        }
                    }
                    else {
                        c.getPlayer().dropMessage(6, "该怪物信息搜索不到");
                    }
                }
                else if (type.equalsIgnoreCase("物品")) {
                    final List<String> retItems = new ArrayList<String>();
                    for (final Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair != null && itemPair.name != null && itemPair.name.toLowerCase().contains(search.toLowerCase())) {
                            retItems.add("\r\n" + itemPair.itemId + " - #i" + itemPair.itemId + ":# #z" + itemPair.itemId + "#");
                        }
                    }
                    if (retItems.size() > 0) {
                        for (final String singleRetItem : retItems) {
                            if (sb.length() > 10000) {
                                sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                                break;
                            }
                            sb.append(singleRetItem);
                        }
                    }
                    else {
                        c.getPlayer().dropMessage(6, "该物品信息搜索不到");
                    }
                }
                else if (type.equalsIgnoreCase("技能")) {
                    final List<String> retSkills = new ArrayList<String>();
                    for (final ISkill skill : SkillFactory.getAllSkills()) {
                        if (skill.getName() != null && skill.getName().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add("\r\n#s" + skill.getId() + "#" + skill.getId() + " - " + skill.getName());
                        }
                    }
                    if (retSkills.size() > 0) {
                        for (final String singleRetSkill : retSkills) {
                            if (sb.length() > 10000) {
                                sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                                break;
                            }
                            sb.append(singleRetSkill);
                        }
                    }
                    else {
                        c.getPlayer().dropMessage(6, "该技能信息搜索不到");
                    }
                }
                else if (type.equalsIgnoreCase("包头")) {
                    final List<String> headers = new ArrayList<String>();
                    headers.add("\r\n服务端包头:");
                    for (final SendPacketOpcode send : SendPacketOpcode.values()) {
                        if (send.name() != null && send.name().toLowerCase().contains(search.toLowerCase())) {
                            headers.add("\r\n" + send.name() + " 值: " + send.getValue() + " 16进制: " + HexTool.getOpcodeToString(send.getValue()));
                        }
                    }
                    headers.add("\r\n客户端包头:");
                    for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
                        if (recv.name() != null && recv.name().toLowerCase().contains(search.toLowerCase())) {
                            headers.add("\r\n" + recv.name() + " 值: " + recv.getValue() + " 16进制: " + HexTool.getOpcodeToString(recv.getValue()));
                        }
                    }
                    for (final String header : headers) {
                        if (sb.length() > 10000) {
                            sb.append("\r\n后面还有很多搜索结果, 但已经无法显示更多");
                            break;
                        }
                        sb.append(header);
                    }
                }
                else {
                    c.getPlayer().dropMessage(6, "对不起, 不支持这个检索命令");
                }
                c.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
            }
            return 0;
        }
    }
    
    public static class 重载商城数据 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            CashItemFactory.getInstance().clearCashShop();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!重载商城数据 - 重载商城数据").toString();
        }
    }
    
    public static class online extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int total = 0;
            final int curConnected = c.getChannelServer().getConnectedClients();
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, "頻道: " + c.getChannelServer().getChannel() + " 线上人数: " + curConnected);
            total += curConnected;
            for (final MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (chr != null && c.getPlayer().getGMLevel() >= chr.getGMLevel()) {
                    final StringBuilder ret = new StringBuilder();
                    ret.append(" 角色名称 ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 15));
                    ret.append(" ID: ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 4));
                    ret.append(" 等级: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 4));
                    ret.append(" 职业: ");
                    ret.append(chr.getJob());
                    if (chr.getMap() == null) {
                        continue;
                    }
                    ret.append(" 地图: ");
                    ret.append(chr.getMapId());
                    ret.append("(").append(chr.getMap().getMapName()).append(")");
                    c.getPlayer().dropMessage(6, ret.toString());
                }
            }
            c.getPlayer().dropMessage(6, "当前频道总计在线人数: " + total);
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            final int channelOnline = c.getChannelServer().getConnectedClients();
            int totalOnline = 0;
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }
            c.getPlayer().dropMessage(6, "当前服务器总计在线人数: " + totalOnline + "个");
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            return 1;
        }
    }
    
    public static class Ban extends CommandExecute
    {
        protected boolean hellban;
        
        public Ban() {
            this.hellban = false;
        }
        
        private String getCommand() {
            return "Ban";
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "[Syntax] !" + this.getCommand() + " <玩家> <原因>");
                return 0;
            }
            final int ch = Find.findChannel(splitted[1]);
            final StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            final MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (target == null || ch < 1) {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功离线封锁 " + splitted[1] + ".");
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败 " + splitted[1]);
                return 0;
            }
            else {
                if (c.getPlayer().getGMLevel() <= target.getGMLevel()) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 不能封锁GM...");
                    return 1;
                }
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, this.hellban)) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功封锁 " + splitted[1] + ".");
                    FileoutputUtil.logToFile_chr(c.getPlayer(), "日志/Logs/Log_封号.rtf", sb.toString());
                    Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[封号系统]" + target.getName() + " 因为使用非法软件而被永久封号。").getBytes());
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败.");
                return 0;
            }
        }
    }
    
    public static class Ban1 extends CommandExecute
    {
        protected boolean hellban;
        
        public Ban1() {
            this.hellban = false;
        }
        
        private String getCommand() {
            return "Ban";
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "[Syntax] !" + this.getCommand() + " <玩家> <原因>");
                return 0;
            }
            final int ch = Find.findChannel(splitted[1]);
            final StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            final MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (target == null || ch < 1) {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功离线封锁 " + splitted[1] + ".");
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败 " + splitted[1]);
                return 0;
            }
            else {
                if (c.getPlayer().getGMLevel() <= target.getGMLevel()) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 不能封锁GM...");
                    return 1;
                }
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, this.hellban)) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功封锁 " + splitted[1] + ".");
                    FileoutputUtil.logToFile_chr(c.getPlayer(), "日志/Logs/Log_封号.rtf", sb.toString());
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败.");
                return 0;
            }
        }
    }
    
    public static class 临时封号 extends CommandExecute
    {
        protected boolean ipBan;
        private final String[] types;
        
        public 临时封号() {
            this.ipBan = false;
            this.types = new String[] { "任性" };
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 4) {
                c.getPlayer().dropMessage(6, "<玩家名字> <理由> <时长(小时)>");
                final StringBuilder s = new StringBuilder("临时封号理由:");
                for (int i = 0; i < this.types.length; ++i) {
                    s.append(i + 1).append(" - ").append(this.types[i]).append(", ");
                }
                c.getPlayer().dropMessage(6, s.toString());
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int reason = Integer.parseInt(splitted[2]);
            final int numHour = Integer.parseInt(splitted[3]);
            final Calendar cal = Calendar.getInstance();
            cal.add(10, numHour);
            final DateFormat df = DateFormat.getInstance();
            if (victim == null || reason < 0 || reason >= this.types.length) {
                c.getPlayer().dropMessage(6, "无法找到玩家或者理由是无效的, 输入" + splitted[0] + "查看可用理由");
                return 0;
            }
            victim.tempban("已经被 " + c.getPlayer().getName() + " 因为" + this.types[reason] + "临时封号", cal, reason, this.ipBan);
            c.getPlayer().dropMessage(6, "玩家 " + splitted[1] + " 被临时封号到 " + df.format(cal.getTime()));
            return 1;
        }
    }
    
    public static class online1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "上线的角色 頻道-" + c.getChannel() + ":");
            c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }
    
    public static class CnGM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(5, "<GM聊天视窗>頻道" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return 1;
        }
    }
    
    public static class Hide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理员隐藏 = 开启 \r\n 解除请输入!unhide");
            return 0;
        }
    }
    
    public static class 监禁 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "用法: !监禁 [玩家名字] [多少分钟, 0 = forever]");
                return 0;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int minutes = Math.max(0, Integer.parseInt(splitted[2]));
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                final MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(180000001);
                victim.getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData(String.valueOf(minutes * 60));
                victim.changeMap(target, target.getPortal(0));
                return 1;
            }
            c.getPlayer().dropMessage(6, "请确保要监禁的玩家处于在线状态.");
            return 0;
        }
    }
    
    public static class UnHide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理员隐藏 = 关闭 \r\n 开启请输入!hide");
            return 1;
        }
    }
    
    public static class 无敌 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "无敌模式已关闭。");
            }
            else {
                player.setInvincible(true);
                player.dropMessage(6, "无敌模式已开启。");
            }
            return 1;
        }
    }
    
    public static class 去往 extends CommandExecute
    {
        private static final HashMap<String, Integer> gotomaps;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, splitted[0] + " <地图名>");
            }
            else if (去往.gotomaps.containsKey(splitted[1])) {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(去往.gotomaps.get(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "地图不存在");
                    return 0;
                }
                final MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            }
            else if (splitted[1].equals("列表")) {
                c.getPlayer().dropMessage(6, "地图列表: ");
                final StringBuilder sb = new StringBuilder();
                for (final String s : 去往.gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            }
            else {
                c.getPlayer().dropMessage(6, "命令错误: " + splitted[0] + " <地图名> 你可以使用 " + splitted[0] + " 列表 来获取可用地图列表");
            }
            return 1;
        }
        
        static {
            (gotomaps = new HashMap<String, Integer>()).put("阿里安特", 260000100);
            去往.gotomaps.put("彩虹岛", 1010000);
            去往.gotomaps.put("婚礼村", 680000000);
            去往.gotomaps.put("蔚蓝道路", 860000000);
            去往.gotomaps.put("水下世界", 230000000);
            去往.gotomaps.put("驳船码头", 541000000);
            去往.gotomaps.put("艾琳森林", 300000000);
            去往.gotomaps.put("魔法密林", 101000000);
            去往.gotomaps.put("冰峰雪域", 211000000);
            去往.gotomaps.put("圣地", 130000000);
            去往.gotomaps.put("自由市场", 910000000);
            去往.gotomaps.put("未来之门", 271000000);
            去往.gotomaps.put("工作场所", 180000000);
            去往.gotomaps.put("幸福村", 209000000);
            去往.gotomaps.put("明珠港", 104000000);
            去往.gotomaps.put("射手村", 100000000);
            去往.gotomaps.put("百草堂", 251000000);
            去往.gotomaps.put("甘榜村", 551000000);
            去往.gotomaps.put("废弃都市", 103000000);
            去往.gotomaps.put("神木村", 240000000);
            去往.gotomaps.put("玩具城", 220000000);
            去往.gotomaps.put("马来西亚", 550000000);
            去往.gotomaps.put("武陵", 250000000);
            去往.gotomaps.put("诺特勒斯", 120000000);
            去往.gotomaps.put("新野城", 600000000);
            去往.gotomaps.put("天空之城", 200000000);
            去往.gotomaps.put("万神殿", 400000000);
            去往.gotomaps.put("品克缤", 270050100);
            去往.gotomaps.put("神的黄昏", 270050100);
            去往.gotomaps.put("勇士部落", 102000000);
            去往.gotomaps.put("里恩", 140000000);
            去往.gotomaps.put("昭和村", 801000000);
            去往.gotomaps.put("新加坡", 540000000);
            去往.gotomaps.put("六岔路口", 104020000);
            去往.gotomaps.put("林中之城", 105000000);
            去往.gotomaps.put("南港", 2000000);
            去往.gotomaps.put("大树口村", 866000000);
            去往.gotomaps.put("时间神殿", 270000000);
            去往.gotomaps.put("三个门", 270000000);
            去往.gotomaps.put("黄昏勇士部落", 273000000);
            去往.gotomaps.put("克林逊森林城堡", 301000000);
            去往.gotomaps.put("城堡顶端", 301000000);
            去往.gotomaps.put("皮亚奴斯", 230040420);
            去往.gotomaps.put("皮亚奴斯洞穴", 230040420);
            去往.gotomaps.put("黑龙", 240060200);
            去往.gotomaps.put("暗黑龙王洞穴", 240060200);
            去往.gotomaps.put("进阶黑龙", 240060201);
            去往.gotomaps.put("进阶暗黑龙王洞穴", 240060201);
            去往.gotomaps.put("天鹰", 240020101);
            去往.gotomaps.put("格瑞芬多森林", 240020101);
            去往.gotomaps.put("火焰龙", 240020401);
            去往.gotomaps.put("喷火龙栖息地", 240020401);
            去往.gotomaps.put("扎昆", 280030100);
            去往.gotomaps.put("扎昆的祭台", 280030100);
            去往.gotomaps.put("闹钟", 220080001);
            去往.gotomaps.put("帕普拉图斯", 220080001);
            去往.gotomaps.put("时间塔的本源", 220080001);
            去往.gotomaps.put("OX问答", 109020001);
            去往.gotomaps.put("上楼", 109030101);
            去往.gotomaps.put("向高地", 109040000);
            去往.gotomaps.put("雪球赛", 109060000);
            去往.gotomaps.put("江户村", 800000000);
        }
    }
    
    public static class Warp extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                }
                else {
                    final MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            }
            else {
                try {
                    victim = c.getPlayer();
                    final int ch = Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        final MapleMap target2 = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        c.getPlayer().changeMap(target2, target2.getPortal(0));
                    }
                    else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "正在换频道,请等待.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                }
                catch (Exception e) {
                    c.getPlayer().dropMessage(6, "该玩家不在线 " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }
    
    public static class Warp1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                }
                else {
                    final MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            }
            else {
                try {
                    victim = c.getPlayer();
                    final int ch = Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        final MapleMap target2 = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        c.getPlayer().changeMap(target2, target2.getPortal(0));
                    }
                    else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
                        c.getPlayer().dropMessage(6, "正在换频道,请等待.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                }
                catch (Exception e) {
                    c.getPlayer().dropMessage(6, "该玩家不在线 " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }
    
    public static class 玩家私聊2 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            boolean hack2 = c.getPlayer().get玩家私聊2();
            if (hack2) {
                c.getPlayer().get玩家私聊2(false);
            }
            else {
                c.getPlayer().get玩家私聊2(true);
            }
            hack2 = c.getPlayer().get玩家私聊2();
            c.getPlayer().dropMessage(6, "[玩家私聊2] " + (hack2 ? "開啟" : "關閉"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊2  - 玩家好友.組隊.密語聊天偷聽開關").toString();
        }
    }
    
    public static class 玩家私聊3 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            boolean hack2 = c.getPlayer().get玩家私聊3();
            if (hack2) {
                c.getPlayer().get玩家私聊3(false);
            }
            else {
                c.getPlayer().get玩家私聊3(true);
            }
            hack2 = c.getPlayer().get玩家私聊3();
            c.getPlayer().dropMessage(6, "[玩家私聊3] " + (hack2 ? "開啟" : "關閉"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊3  - 玩家公會.家族聊天偷聽開關").toString();
        }
    }
    
    public static class 聊天称号开关 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            boolean ChatTitle = c.getPlayer().getCTitle();
            if (ChatTitle) {
                c.getPlayer().getCTitle(false);
            }
            else {
                c.getPlayer().getCTitle(true);
            }
            ChatTitle = c.getPlayer().getCTitle();
            c.getPlayer().dropMessage(6, "[聊天稱號開關] " + (ChatTitle ? "開啟" : "關閉"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!聊天稱號開關  - 聊天稱號開關").toString();
        }
    }
    
    public static class 聊天称号设定 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 1) {
                return 0;
            }
            String ChatTtitle = "";
            ChatTtitle = splitted[1];
            c.getPlayer().setChatTitle(ChatTtitle);
            c.getPlayer().dropMessage(6, "[聊天称号] 設定成功");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!聊天称号  - 聊天称号").toString();
        }
    }
    
    public static class GM聊天 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            boolean GMChat = c.getPlayer().getGMChat();
            if (GMChat) {
                c.getPlayer().getGMChat(false);
            }
            else {
                c.getPlayer().getGMChat(true);
            }
            GMChat = c.getPlayer().getGMChat();
            c.getPlayer().dropMessage(6, "[GM聊天開關] " + (GMChat ? "開啟" : "關閉"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!GM聊天 - GM聊天").toString();
        }
    }
    
    public static class 玩家私聊1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            boolean hack1 = c.getPlayer().get玩家私聊1();
            if (hack1) {
                c.getPlayer().get玩家私聊1(false);
            }
            else {
                c.getPlayer().get玩家私聊1(true);
            }
            hack1 = c.getPlayer().get玩家私聊1();
            c.getPlayer().dropMessage(6, "[玩家私聊1] " + (hack1 ? "開啟" : "關閉"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!玩家私聊1  - 玩家普通.交易聊天偷聽開關").toString();
        }
    }
}
