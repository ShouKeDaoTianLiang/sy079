package server;

import java.util.Iterator;
import java.util.Calendar;
import constants.ServerConstants;
import tools.FileoutputUtil1;
import tools.FileoutputUtil;
import java.util.LinkedList;
import client.MapleClient;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class AutobanManager implements Runnable
{
    private final Map<Integer, Integer> points;
    private final Map<Integer, List<String>> reasons;
    private final Set<ExpirationEntry> expirations;
    private static final int AUTOBAN_POINTS = 5000;
    private static final AutobanManager instance;
    private final ReentrantLock lock;
    
    public AutobanManager() {
        this.points = new HashMap<Integer, Integer>();
        this.reasons = new HashMap<Integer, List<String>>();
        this.expirations = new TreeSet<ExpirationEntry>();
        this.lock = new ReentrantLock(true);
    }
    
    public static final AutobanManager getInstance() {
        return AutobanManager.instance;
    }
    
    public final void autoban(final MapleClient c, final String reason) {
        if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
            c.getPlayer().dropMessage(5, "[自动侦测系統] 已触发违规侦测 :" + reason);
            return;
        }
        this.addPoints(c, 5000, 0L, reason);
    }
    
    public final void addPoints(final MapleClient c, final int points, final long expiration, final String reason) {
        this.lock.lock();
        try {
            final int acc = c.getPlayer().getAccountID();
            if (this.points.containsKey(acc)) {
                final int SavedPoints = (int)(int)this.points.get(acc);
                if (SavedPoints >= 5000) {
                    return;
                }
                this.points.put(acc, SavedPoints + points);
                final List<String> reasonList = (java.util.List)(java.util.List)this.reasons.get(acc);
                reasonList.add(reason);
            }
            else {
                this.points.put(acc, points);
                final List<String> reasonList = new LinkedList<String>();
                reasonList.add(reason);
                this.reasons.put(acc, reasonList);
            }
            if (this.points.get(acc) >= 5000) {
                if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
                    c.getPlayer().dropMessage(5, "[自動封號系統] 觸發鎖定 : " + reason);
                    return;
                }
                final StringBuilder sb = new StringBuilder("[自動封號系統] ");
                sb.append("角色 : ");
                sb.append(c.getPlayer().getName());
                sb.append(" IP :");
                sb.append(c.getSession().getRemoteAddress().toString());
                for (final String s : this.reasons.get(acc)) {
                    sb.append(s);
                    sb.append(", ");
                }
                final String note = "时间：" + FileoutputUtil.CurrentReadable_Time() + " || 玩家名字：" + c.getPlayer().getName() + "|| 玩家地图：" + c.getPlayer().getMapId() + "外挂类型 : " + reason + "\r\n";
                FileoutputUtil1.外挂记录("" + c.getPlayer().getName() + "使用外挂.log", note);
                if (ServerConstants.封号系统) {
                    final Calendar cal = Calendar.getInstance();
                    cal.add(5, 5);
                    c.getPlayer().ban(sb.toString(), false, true, false);
                    c.disconnect(true, false);
                }
            }
            else if (expiration > 0L) {
                this.expirations.add(new ExpirationEntry(System.currentTimeMillis() + expiration, acc, points));
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public final void run() {
        final long now = System.currentTimeMillis();
        for (final ExpirationEntry e : this.expirations) {
            if (e.time > now) {
                return;
            }
            this.points.put(e.acc, this.points.get(e.acc) - e.points);
        }
    }
    
    static {
        instance = new AutobanManager();
    }
    
    private static class ExpirationEntry implements Comparable<ExpirationEntry>
    {
        public long time;
        public int acc;
        public int points;
        
        public ExpirationEntry(final long time, final int acc, final int points) {
            this.time = time;
            this.acc = acc;
            this.points = points;
        }
        
        @Override
        public int compareTo(final ExpirationEntry o) {
            return (int)(this.time - o.time);
        }
        
        @Override
        public boolean equals(final Object oth) {
            if (!(oth instanceof ExpirationEntry)) {
                return false;
            }
            final ExpirationEntry ee = (ExpirationEntry)oth;
            return this.time == ee.time && this.points == ee.points && this.acc == ee.acc;
        }
    }
}
