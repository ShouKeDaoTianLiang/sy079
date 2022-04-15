package handling;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import client.MapleCharacter;
import java.util.LinkedHashSet;
import java.util.Collections;
import handling.channel.ChannelServer;
import org.slf4j.Logger;

public class Ast implements Runnable
{
    private static final Logger log;
    
    public static Ast gi() {
        return i.i;
    }
    
    @Override
    public void run() {
        Ast.log.info("保存所有角色信息...开始");
        final Collection<ChannelServer> ccs = ChannelServer.getAllInstances();
        for (final ChannelServer chan : ccs) {
            Ast.log.info("保存角色信息 频道" + chan.getChannel());
            if (chan != null) {
                final Collection<MapleCharacter> chars = new LinkedHashSet<MapleCharacter>(Collections.synchronizedCollection(chan.getPlayerStorage().getAllCharacters()));
                synchronized (chars) {
                    for (final MapleCharacter chr : chars) {
                        try {
                            if (chr == null) {
                                continue;
                            }
                            chr.saveToDB(true, true);
                        }
                        catch (Exception ex) {}
                    }
                }
            }
        }
        Ast.log.info("保存所有角色信息...结束");
    }
    
    static {
        log = LoggerFactory.getLogger(Ast.class);
    }
    
    private static class i
    {
        public static final Ast i;
        
        static {
            i = new Ast();
        }
    }
}
