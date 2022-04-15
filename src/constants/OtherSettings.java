package constants;

import org.slf4j.LoggerFactory;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileReader;
import org.slf4j.Logger;
import java.util.Properties;

public class OtherSettings
{
    private static OtherSettings instance;
    private static boolean CANLOG;
    private Properties itempb_cfg;
    private String[] itempb_id;
    private String[] itemjy_id;
    private String[] itemgy_id;
    private String[] mappb_id;
    private String[] itemdl_id;
    private String[] wupin_id;
    private String[] jiance_id;
    private static final Logger log;
    
    public OtherSettings() {
        this.itempb_cfg = new Properties();
        try {
            final InputStreamReader is = new FileReader("商城配置.ini");
            this.itempb_cfg.load(is);
            is.close();
            this.itempb_id = this.itempb_cfg.getProperty("cashban").split(",");
            this.itemjy_id = this.itempb_cfg.getProperty("cashjy", "0").split(",");
            this.itemgy_id = this.itempb_cfg.getProperty("gysj", "0").split(",");
            this.wupin_id = this.itempb_cfg.getProperty("wupin").split(",");
            this.jiance_id = this.itempb_cfg.getProperty("jiance").split(",");
        }
        catch (Exception e) {
            OtherSettings.log.error("Could not configuration", e);
        }
    }
    
    public String[] is检测_id() {
        return this.jiance_id;
    }
    
    public String[] get禁止丢弃_id() {
        return this.wupin_id;
    }
    
    public String[] getItempb_id() {
        return this.itempb_id;
    }
    
    public String[] getItemdl_id() {
        return this.itemdl_id;
    }
    
    public String[] getItemgy_id() {
        return this.itemgy_id;
    }
    
    public String[] getItemjy_id() {
        return this.itemjy_id;
    }
    
    public String[] getMappb_id() {
        return this.mappb_id;
    }
    
    public boolean isCANLOG() {
        return OtherSettings.CANLOG;
    }
    
    public void setCANLOG(final boolean CANLOG) {
        OtherSettings.CANLOG = CANLOG;
    }
    
    public static OtherSettings getInstance() {
        if (OtherSettings.instance == null) {
            OtherSettings.instance = new OtherSettings();
        }
        return OtherSettings.instance;
    }
    
    static {
        OtherSettings.instance = null;
        log = LoggerFactory.getLogger(OtherSettings.class);
    }
}
