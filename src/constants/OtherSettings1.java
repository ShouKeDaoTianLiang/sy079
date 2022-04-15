package constants;

import org.slf4j.LoggerFactory;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileReader;
import org.slf4j.Logger;
import java.util.Properties;

public class OtherSettings1
{
    private static OtherSettings1 instance;
    private static boolean CANLOG;
    private Properties itempb_cfg;
    private String[] A;
    private String[] B;
    private String[] C;
    private String[] D;
    private String[] E;
    private String[] AA;
    private String[] BB;
    private String[] CC;
    private String[] DD;
    private String[] EE;
    private String[] PK;
    private String[] PK1;
    private String[] 等级;
    private String[] 等级1;
    private String[] 等级2;
    private String[] 等级3;
    private String[] 等级4;
    private String[] 等级5;
    private String[] 会员经验;
    private String[] 会员经验1;
    private String[] 会员经验2;
    private String[] 会员祝福;
    private String[] 会员祝福1;
    private String[] 会员祝福2;
    private String[] 会员复活;
    private String[] 会员复活1;
    private String[] 会员复活2;
    private static final Logger log;
    
    public OtherSettings1() {
        this.itempb_cfg = new Properties();
        try {
            final InputStreamReader is = new FileReader("游戏功能.ini");
            this.itempb_cfg.load(is);
            is.close();
            this.A = this.itempb_cfg.getProperty("A").split(",");
            this.B = this.itempb_cfg.getProperty("B").split(",");
            this.C = this.itempb_cfg.getProperty("C").split(",");
            this.D = this.itempb_cfg.getProperty("D").split(",");
            this.E = this.itempb_cfg.getProperty("E").split(",");
            this.等级1 = this.itempb_cfg.getProperty("world.dt").split(",");
            this.等级2 = this.itempb_cfg.getProperty("world.jy").split(",");
            this.等级3 = this.itempb_cfg.getProperty("world.dd").split(",");
            this.等级4 = this.itempb_cfg.getProperty("world.dy").split(",");
            this.会员祝福 = this.itempb_cfg.getProperty("world.BUFF").split(",");
            this.会员祝福1 = this.itempb_cfg.getProperty("world.BUFF1").split(",");
            this.会员祝福2 = this.itempb_cfg.getProperty("world.BUFF2").split(",");
            this.会员复活 = this.itempb_cfg.getProperty("world.fuhuo").split(",");
            this.会员复活1 = this.itempb_cfg.getProperty("world.fuhuo1").split(",");
            this.会员复活2 = this.itempb_cfg.getProperty("world.fuhuo2").split(",");
            this.会员经验 = this.itempb_cfg.getProperty("world.VIP").split(",");
            this.会员经验1 = this.itempb_cfg.getProperty("world.VIP1").split(",");
            this.会员经验2 = this.itempb_cfg.getProperty("world.VIP2").split(",");
        }
        catch (Exception e) {
            OtherSettings1.log.error("Could not configuration", (Throwable)e);
        }
    }
    
    public String[] getItempb_A() {
        return this.A;
    }
    
    public String[] getItempb_B() {
        return this.B;
    }
    
    public String[] getItempb_C() {
        return this.C;
    }
    
    public String[] getItempb_D() {
        return this.D;
    }
    
    public String[] getItempb_E() {
        return this.E;
    }
    
    public String[] getItempb_PK() {
        return this.PK;
    }
    
    public String[] getItempb_PK1() {
        return this.PK1;
    }
    
    public String[] getItempb_等级() {
        return this.等级;
    }
    
    public String[] getItempb_等级1() {
        return this.等级1;
    }
    
    public String[] getItempb_等级2() {
        return this.等级2;
    }
    
    public String[] getItempb_等级3() {
        return this.等级3;
    }
    
    public String[] getItempb_等级4() {
        return this.等级4;
    }
    
    public String[] getItempb_等级5() {
        return this.等级5;
    }
    
    public String[] getItempb_AA() {
        return this.AA;
    }
    
    public String[] getItempb_BB() {
        return this.BB;
    }
    
    public String[] getItempb_CC() {
        return this.CC;
    }
    
    public String[] getItempb_DD() {
        return this.DD;
    }
    
    public String[] getItempb_EE() {
        return this.EE;
    }
    
    public String[] is会员经验() {
        return this.会员经验;
    }
    
    public String[] is会员经验1() {
        return this.会员经验1;
    }
    
    public String[] is会员经验2() {
        return this.会员经验2;
    }
    
    public String[] is会员复活() {
        return this.会员复活;
    }
    
    public String[] is会员复活1() {
        return this.会员复活1;
    }
    
    public String[] is会员复活2() {
        return this.会员复活2;
    }
    
    public String[] is会员祝福() {
        return this.会员祝福;
    }
    
    public String[] is会员祝福1() {
        return this.会员祝福1;
    }
    
    public String[] is会员祝福2() {
        return this.会员祝福2;
    }
    
    public boolean isCANLOG() {
        return OtherSettings1.CANLOG;
    }
    
    public void setCANLOG(final boolean CANLOG) {
        OtherSettings1.CANLOG = CANLOG;
    }
    
    public static OtherSettings1 getInstance() {
        if (OtherSettings1.instance == null) {
            OtherSettings1.instance = new OtherSettings1();
        }
        return OtherSettings1.instance;
    }
    
    static {
        OtherSettings1.instance = null;
        log = LoggerFactory.getLogger(OtherSettings1.class);
    }
}
