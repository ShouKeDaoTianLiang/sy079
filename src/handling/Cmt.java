package handling;

import org.slf4j.LoggerFactory;
import java.util.Properties;
import server.Timer.WorldTimer;
import constants.ServerConstants;
import org.slf4j.Logger;

public class Cmt implements Runnable
{
    private static Logger log;
    
    public static Cmt getInstance() {
        return InstanceHolder.instance;
    }
    
    @Override
    public void run() {
        final Properties props = ServerConstants.getDefaultProperties("serverConstants.properties");
        final WorldTimer tMan = WorldTimer.getInstance();
        tMan.start();
        Cmt.log.info("自动保存线程开启...10分钟一次");
    }
    
    static {
        Cmt.log = LoggerFactory.getLogger(Cmt.class);
    }
    
    private static class InstanceHolder
    {
        public static final Cmt instance;
        
        static {
            instance = new Cmt();
        }
    }
}
