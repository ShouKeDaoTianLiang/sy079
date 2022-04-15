package scripting;

import java.util.Iterator;
import tools.FileoutputUtil;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import client.MapleClient;
import java.util.LinkedHashMap;
import handling.channel.ChannelServer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class EventScriptManager extends AbstractScriptManager
{
    private final Map<String, EventEntry> events;
    private final AtomicInteger runningInstanceMapId;
    
    public final int getNewInstanceMapId() {
        return this.runningInstanceMapId.addAndGet(1);
    }
    
    public EventScriptManager(final ChannelServer cserv, final String[] scripts) {
        this.events = new LinkedHashMap<String, EventEntry>();
        this.runningInstanceMapId = new AtomicInteger(0);
        for (final String script : scripts) {
            if (!script.isEmpty()) {
                final Invocable iv = this.getInvocable("event/" + script + ".js", null);
                if (iv != null) {
                    this.events.put(script, new EventEntry(script, iv, new EventManager(cserv, iv, script)));
                }
            }
        }
    }
    
    public final EventManager getEventManager(final String event) {
        final EventEntry entry = this.events.get(event);
        if (entry == null) {
            return null;
        }
        return entry.em;
    }
    
    public final void init() {
        for (final EventEntry entry : this.events.values()) {
            try {
                ((ScriptEngine)entry.iv).put("em", entry.em);
                entry.iv.invokeFunction("init", (Object) null);
            }
            catch (Exception ex) {
                System.out.println("Error initiating event: " + entry.script + ":" + ex);
                FileoutputUtil.log("日志/Logs/Log_Script_脚本异常.rtf", "Error initiating event: " + entry.script + ":" + ex);
            }
        }
    }
    
    public final void cancel() {
        for (final EventEntry entry : this.events.values()) {
            entry.em.cancel();
        }
    }
    
    private static class EventEntry
    {
        public String script;
        public Invocable iv;
        public EventManager em;
        
        public EventEntry(final String script, final Invocable iv, final EventManager em) {
            this.script = script;
            this.iv = iv;
            this.em = em;
        }
    }
}
