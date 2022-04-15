package handling.channel;

import java.util.Observable;
import java.util.Map.Entry;
import handling.MaplePacket;
import tools.FileoutputUtil;
import java.util.Iterator;
import client.MapleCharacterUtil;
import java.util.ArrayList;
import handling.world.CheaterData;
import java.util.List;
import handling.world.World.Find;
import java.util.Collections;
import java.util.Collection;
import server.Timer.PingTimer;
import java.util.HashMap;
import handling.world.CharacterTransfer;
import client.MapleCharacter;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerStorage
{
    private final ReentrantReadWriteLock mutex;
    private final Lock rL;
    private final Lock wL;
    private final ReentrantReadWriteLock mutex2;
    private final Lock rL2;
    private final Lock wL2;
    private final Map<String, MapleCharacter> nameToChar;
    private final Map<Integer, MapleCharacter> idToChar;
    private final Map<Integer, CharacterTransfer> PendingCharacter;
    private final int channel;
    private final PlayerObservable playerObservable;
    
    public PlayerStorage(final int channel) {
        this.mutex = new ReentrantReadWriteLock();
        this.rL = this.mutex.readLock();
        this.wL = this.mutex.writeLock();
        this.mutex2 = new ReentrantReadWriteLock();
        this.rL2 = this.mutex2.readLock();
        this.wL2 = this.mutex2.writeLock();
        this.nameToChar = new HashMap<String, MapleCharacter>();
        this.idToChar = new HashMap<Integer, MapleCharacter>();
        this.PendingCharacter = new HashMap<Integer, CharacterTransfer>();
        this.playerObservable = new PlayerObservable();
        this.channel = channel;
        PingTimer.getInstance().schedule(new PersistingTask(), 60000L);
    }
    
    public final Collection<MapleCharacter> getAllCharacters() {
        this.rL.lock();
        try {
            return Collections.unmodifiableCollection(this.idToChar.values());
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final void registerPlayer(final MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.put(chr.getName().toLowerCase(), chr);
            this.idToChar.put(chr.getId(), chr);
        }
        finally {
            this.wL.unlock();
        }
        Find.register(chr.getId(), chr.getName(), this.channel);
    }
    
    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.put(playerid, chr);
        }
        finally {
            this.wL2.unlock();
        }
    }
    
    public final void deregisterPlayer(final MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.remove(chr.getName().toLowerCase());
            this.idToChar.remove(chr.getId());
        }
        finally {
            this.wL.unlock();
        }
        Find.forceDeregister(chr.getId(), chr.getName());
    }
    
    public final void deregisterPlayer(final int idz, final String namez) {
        this.wL.lock();
        try {
            this.nameToChar.remove(namez.toLowerCase());
            this.idToChar.remove(idz);
        }
        finally {
            this.wL.unlock();
        }
        Find.forceDeregister(idz, namez);
    }
    
    public final void deregisterPendingPlayer(final int charid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.remove(charid);
        }
        finally {
            this.wL2.unlock();
        }
    }
    
    public final CharacterTransfer getPendingCharacter(final int charid) {
        this.wL2.lock();
        try {
            return this.PendingCharacter.remove(charid);
        }
        finally {
            this.wL2.unlock();
        }
    }
    
    public final MapleCharacter getCharacterByName(final String name) {
        this.rL.lock();
        try {
            return this.nameToChar.get(name.toLowerCase());
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final MapleCharacter getCharacterById(final int id) {
        this.rL.lock();
        try {
            return this.idToChar.get(id);
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final int getConnectedClients() {
        return this.idToChar.size();
    }
    
    public final List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = new ArrayList<CheaterData>();
        this.rL.lock();
        try {
            for (final MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getReportPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getReportPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getReportPoints() + ") " + chr.getReportSummary()));
                }
            }
        }
        finally {
            this.rL.unlock();
        }
        return cheaters;
    }
    
    public final void disconnectAll() {
        this.disconnectAll(false);
    }
    
    public final void disconnectAll(final boolean checkGM) {
        this.wL.lock();
        try {
            final Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
            while (itr.hasNext()) {
                final MapleCharacter chr = itr.next();
                if (!chr.isGM() || !checkGM) {
                    chr.getClient().disconnect(false, false, true);
                    chr.getClient().getSession().close();
                    FileoutputUtil.logToFile("日志/Logs/Log_DC.txt", "\r\n伺服器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
                    Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        }
        finally {
            this.wL.unlock();
        }
    }
    
    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();
        if (byGM) {
            this.rL.lock();
            try {
                final Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            }
            finally {
                this.rL.unlock();
            }
        }
        else {
            this.rL.lock();
            try {
                for (final MapleCharacter chr : this.nameToChar.values()) {
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            }
            finally {
                this.rL.unlock();
            }
        }
        return sb.toString();
    }
    
    public final void broadcastPacket(final MaplePacket data) {
        this.rL.lock();
        try {
            final Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
            while (itr.hasNext()) {
                itr.next().getClient().getSession().write(data);
            }
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final void broadcastSmegaPacket(final MaplePacket data) {
        this.rL.lock();
        try {
            for (final MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getClient().isLoggedIn() && chr.getSmega()) {
                    chr.getClient().getSession().write(data);
                }
            }
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final void broadcastGMPacket(final MaplePacket data) {
        this.rL.lock();
        try {
            for (final MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getClient().isLoggedIn() && chr.isGM()) {
                    chr.getClient().getSession().write(data);
                }
            }
        }
        finally {
            this.rL.unlock();
        }
    }
    
    public final List<MapleCharacter> getAllCharactersThreadSafe() {
        final List<MapleCharacter> ret = new ArrayList<MapleCharacter>();
        ret.addAll(this.getAllCharacters());
        return ret;
    }
    
    public PlayerObservable getPlayerObservable() {
        return this.playerObservable;
    }
    
    public class PersistingTask implements Runnable
    {
        @Override
        public void run() {
            wL2.lock();
            try {
                final long currenttime = System.currentTimeMillis();
                final Iterator<Entry<Integer, CharacterTransfer>> itr = PendingCharacter.entrySet().iterator();
                while (itr.hasNext()) {
                    if (currenttime - itr.next().getValue().TranferTime > 40000L) {
                        itr.remove();
                    }
                }
            }
            finally {
                wL2.unlock();
            }
        }
    }
    
    public class PlayerObservable extends Observable
    {
        private int count;
        
        public int getCount() {
            return this.count;
        }
        
        public void changed() {
            this.count = nameToChar.size();
            this.setChanged();
            this.notifyObservers();
        }
    }
}
