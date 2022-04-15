package handling.channel.handler;

import java.util.List;
import client.MapleCharacter;
import server.Randomizer;
import tools.MaplePacketCreator;
import java.util.ArrayList;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class BeanGame
{
    public static int 进洞次数;
    public static int a;
    public static int b;
    public static int d;
    public static int s;
    public static int as;
    public static int bs;
    public static int ds;
    
    public static final void BeanGame1(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final List<Beans> beansInfo = new ArrayList<Beans>();
        final int type = slea.readByte();
        int 力度 = 0;
        int 豆豆序号 = 0;
        switch (type) {
            case 0: {
                力度 = slea.readShort();
                slea.readInt();
                chr.setBeansRange(力度);
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 1: {
                力度 = slea.readShort();
                slea.readInt();
                chr.setBeansRange(力度);
                c.sendPacket(MaplePacketCreator.enableActions());
                break;
            }
            case 2: {
                slea.readInt();
                break;
            }
            case 3: {
                seta(slea.readInt());
                setb(slea.readInt());
                setd(slea.readInt());
                final boolean aa = getb() - geta() == 3;
                final int sss = Randomizer.nextInt(9);
                if (aa) {
                    setas(sss);
                    setbs(sss);
                }
                else if (geta() == 9) {
                    setas(sss);
                    setbs(sss - 1);
                }
                else {
                    setas(sss);
                    setbs(sss);
                }
                setds(Randomizer.nextInt(9));
                gain进洞次数(1);
                if (get进洞次数() > 7) {
                    set进洞次数(7);
                }
                c.sendPacket(MaplePacketCreator.BeansJDCS(get进洞次数()));
                break;
            }
            case 4: {
                gain进洞次数(-1);
                if (getas() == getbs()) {
                    c.sendPacket(MaplePacketCreator.BeansJDXZ(get进洞次数(), geta(), getb(), getd(), getas(), getbs(), getds()));
                    break;
                }
                c.sendPacket(MaplePacketCreator.BeansJDXZ(get进洞次数(), 0, 0, 0, getas(), getbs(), getds()));
                break;
            }
            case 5: {
                if (getas() == getbs() && getas() == getds()) {
                    chr.gainBeans(gets());
                    chr.gainExp(gets(), true, false, true);
                    final String notea = "恭喜你打豆豆成功中奖！当前中奖获得豆豆：" + gets() + "个！";
                    c.sendPacket(MaplePacketCreator.BeansGameMessage(1, 1, notea));
                    break;
                }
                break;
            }
            case 11: {
                力度 = slea.readShort();
                豆豆序号 = slea.readInt() + 1;
                chr.setBeansRange(力度);
                chr.setBeansNum(豆豆序号);
                if (豆豆序号 == 1) {
                    chr.setCanSetBeansNum(false);
                    break;
                }
                break;
            }
            case 6: {
                slea.skip(1);
                final int 循环次数 = slea.readByte();
                if (循环次数 == 0) {
                    return;
                }
                if (循环次数 != 1) {
                    slea.skip((循环次数 - 1) * 8);
                }
                if (chr.isCanSetBeansNum()) {
                    chr.setBeansNum(chr.getBeansNum() + 循环次数);
                }
                chr.gainBeans(-循环次数);
                chr.setCanSetBeansNum(true);
                break;
            }
            default: {
                System.out.println("未处理的类型【" + type + "】\n包" + slea.toString());
                break;
            }
        }
        if (type == 11 || type == 6) {
            for (int i = 0; i < 5; ++i) {
                beansInfo.add(new Beans(chr.getBeansRange() + rand(-100, 100), getBeanType(), chr.getBeansNum() + i));
            }
            c.sendPacket(MaplePacketCreator.showBeans(beansInfo));
        }
    }
    
    private static int getBeanType() {
        final int random = rand(1, 100);
        int beanType = 0;
        switch (random) {
            case 2: {
                beanType = 1;
                break;
            }
            case 49: {
                beanType = 2;
                break;
            }
            case 99: {
                beanType = 3;
                break;
            }
        }
        return beanType;
    }
    
    public static final int get进洞次数() {
        return BeanGame.进洞次数;
    }
    
    public static final void gain进洞次数(final int a) {
        BeanGame.进洞次数 += a;
    }
    
    public static final void set进洞次数(final int a) {
        BeanGame.进洞次数 = a;
    }
    
    public static final int geta() {
        return BeanGame.a;
    }
    
    public static final void seta(final int s) {
        BeanGame.a = s;
    }
    
    public static final int getb() {
        return BeanGame.b;
    }
    
    public static final void setb(final int a) {
        BeanGame.b = a;
    }
    
    public static final int getd() {
        return BeanGame.d;
    }
    
    public static final void setd(final int a) {
        BeanGame.d = a;
    }
    
    public static final int gets() {
        return BeanGame.s;
    }
    
    public static final void sets(final int a) {
        BeanGame.s = a;
    }
    
    public static final int getas() {
        return BeanGame.as;
    }
    
    public static final void setas(final int s) {
        BeanGame.as = s;
    }
    
    public static final int getbs() {
        return BeanGame.bs;
    }
    
    public static final void setbs(final int a) {
        BeanGame.bs = a;
    }
    
    public static final int getds() {
        return BeanGame.ds;
    }
    
    public static final void setds(final int a) {
        BeanGame.ds = a;
    }
    
    private static int rand(final int lbound, final int ubound) {
        return (int)(Math.random() * (ubound - lbound + 1) + lbound);
    }
    
    public static final void BeanGame2(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.sendPacket(MaplePacketCreator.updateBeans(c.getPlayer().getId(), c.getPlayer().getBeans()));
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    static {
        BeanGame.进洞次数 = 0;
        BeanGame.a = 0;
        BeanGame.b = 0;
        BeanGame.d = 0;
        BeanGame.s = 0;
        BeanGame.as = 0;
        BeanGame.bs = 0;
        BeanGame.ds = 0;
    }
}
