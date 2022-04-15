package server.cherryms;

import tools.MaplePacketCreator;
import java.util.Iterator;
import server.maps.MapleMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import client.MapleCharacter;
import java.util.Collection;
import server.maps.MapleMapFactory;
import handling.channel.ChannelServer;

public class CherryMSLotteryImpl implements CherryMSLottery
{
    private static CherryMSLotteryImpl instance;
    private ChannelServer cserv;
    private MapleMapFactory mapFactory;
    private int zjNum;
    Collection<MapleCharacter> characters;
    private long alltouzhu;
    private long allpeichu;
    
    private CherryMSLotteryImpl() {
        this.characters = new ArrayList<MapleCharacter>();
    }
    
    public static CherryMSLotteryImpl getInstance() {
        if (CherryMSLotteryImpl.instance == null) {
            CherryMSLotteryImpl.instance = new CherryMSLotteryImpl();
        }
        return CherryMSLotteryImpl.instance;
    }
    
    private CherryMSLotteryImpl(final ChannelServer cserv, final MapleMapFactory mapFactory) {
        this.characters = new ArrayList<MapleCharacter>();
        this.cserv = cserv;
        this.mapFactory = mapFactory;
    }
    
    public static CherryMSLotteryImpl getInstance(final ChannelServer cserv, final MapleMapFactory mapFactory) {
        if (CherryMSLotteryImpl.instance == null) {
            CherryMSLotteryImpl.instance = new CherryMSLotteryImpl(cserv, mapFactory);
        }
        return CherryMSLotteryImpl.instance;
    }
    
    @Override
    public ChannelServer getChannelServer() {
        return this.cserv;
    }
    
    @Override
    public MapleMapFactory getMapleMapFactory() {
        return this.mapFactory;
    }
    
    public static int getDatetimemm() {
        final Date date = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("mm");
        final String datetime = sdf.format(date);
        return Integer.parseInt(datetime);
    }
    
    @Override
    public void warp(final int map, final MapleCharacter c) {
        final MapleMap target = this.getWarpMap(map, c);
        c.changeMap(target, target.getPortal(0));
    }
    
    private MapleMap getWarpMap(final int map, final MapleCharacter c) {
        MapleMap target;
        if (c.getEventInstance() == null) {
            target = ChannelServer.getInstance(c.getClient().getChannel()).getMapFactory().getMap(map);
        }
        else {
            target = c.getEventInstance().getMapInstance(map);
        }
        return target;
    }
    
    @Override
    public long getAllpeichu() {
        return this.allpeichu;
    }
    
    @Override
    public void setAllpeichu(final long allpeichu) {
        this.allpeichu = allpeichu;
    }
    
    @Override
    public long getAlltouzhu() {
        return this.alltouzhu;
    }
    
    @Override
    public void setAlltouzhu(final long alltouzhu) {
        this.alltouzhu = alltouzhu;
    }
    
    @Override
    public Collection<MapleCharacter> getCharacters() {
        return this.characters;
    }
    
    @Override
    public void setCharacters(final Collection<MapleCharacter> characters) {
        this.characters = characters;
    }
    
    @Override
    public void addChar(final MapleCharacter chr) {
        this.characters.add(chr);
    }
    
    @Override
    public int getZjNum() {
        return this.zjNum;
    }
    
    @Override
    public void setZjNum(final int zjNum) {
        this.zjNum = zjNum;
    }
    
    @Override
    public void doLottery() {
        this.drawalottery();
    }
    
    @Override
    public int getTouNumbyType(final int type) {
        int count = 0;
        for (final MapleCharacter chr : this.characters) {
            if (chr.getTouzhuType() == type) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public void drawalottery() {
        this.zjNum = (int)(Math.random() * 6.0 + 1.0);
        final String zjNames2 = " ";
        final String zjNames3 = " ";
        String zjNames4 = " ";
        int toucount2 = 0;
        int toucount3 = 0;
        int toucount4 = 0;
        int zhongcount2 = 0;
        int zhongcount3 = 0;
        int zhongcount4 = 0;
        long sumNX = 0L;
        long peiNX = 0L;
        int zjpeople = 0;
        final Collection<MapleCharacter> drawchars = this.characters;
        if (drawchars != null) {
            for (final MapleCharacter chr : drawchars) {
                final int charType = chr.getTouzhuType();
                final int charNum = chr.getTouzhuNum();
                int charZhuNX = chr.getTouzhuNX();
                chr.setTouzhuType(0);
                chr.setTouzhuNum(0);
                chr.setTouzhuNX(0);
                sumNX += charZhuNX;
                if (charType == 2) {
                    ++toucount2;
                    if ((this.zjNum > 3 && charNum > 3) || (this.zjNum < 4 && charNum < 4)) {
                        charZhuNX *= 2;
                        charZhuNX -= charZhuNX * 20 / 100;
                        chr.modifyCSPoints(1, charZhuNX, true);
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】 \r\n恭喜中奖。\r\n万恶的管理员偷偷拿走20%的佣金!! \r\n获得点卷:" + charZhuNX);
                        peiNX += charZhuNX;
                        zjNames4 = zjNames4 + chr.getName() + ":赢得" + peiNX + "点卷 ";
                        ++zhongcount2;
                        ++zjpeople;
                    }
                    else {
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】\r\n对不起您没有中奖，请继续努力");
                    }
                }
                if (charType == 3) {
                    ++toucount3;
                    if ((this.zjNum > 4 && charNum > 4) || (this.zjNum > 2 && this.zjNum < 5 && charNum > 2 && charNum < 5) || (this.zjNum < 3 && charNum < 3)) {
                        charZhuNX *= 3;
                        charZhuNX -= charZhuNX * 20 / 100;
                        chr.modifyCSPoints(0, charZhuNX);
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】 \r\n恭喜中奖。\r\n万恶的管理员偷偷拿走20%的佣金!! \r\n获得点卷:" + charZhuNX);
                        peiNX += charZhuNX;
                        zjNames4 = zjNames4 + chr.getName() + ":赢得" + peiNX + "点卷 ";
                        ++zhongcount3;
                        ++zjpeople;
                    }
                    else {
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】\r\n对不起您没有中奖，请继续努力");
                    }
                }
                if (charType == 6) {
                    ++toucount4;
                    if (this.zjNum == charNum) {
                        charZhuNX *= 6;
                        charZhuNX -= charZhuNX * 20 / 100;
                        chr.modifyCSPoints(0, charZhuNX);
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】 \r\n恭喜中奖。\r\n万恶的管理员偷偷拿走20%的佣金!! \r\n获得点卷:" + charZhuNX);
                        peiNX += charZhuNX;
                        zjNames4 = zjNames4 + chr.getName() + ":赢得" + peiNX + "点卷 ";
                        ++zhongcount4;
                        ++zjpeople;
                    }
                    else {
                        chr.dropMessage(1, "本期号码：【" + this.zjNum + "】\r\n对不起您没有中奖，请继续努力");
                    }
                }
            }
            this.alltouzhu += sumNX;
            this.allpeichu += peiNX;
            this.characters.removeAll(drawchars);
        }
        int peoplecount = 0;
        if (drawchars != null) {
            peoplecount = drawchars.size();
        }
        if (this.getChannelServer().getChannel() == 1) {
            this.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(5, "[竞猜系统]中奖名单:" + zjNames4 + zjNames3 + zjNames2 + " "));
        }
    }
    
    static {
        CherryMSLotteryImpl.instance = null;
    }
}
