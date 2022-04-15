package handling.world;

import client.MapleCharacter;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import handling.channel.ChannelServer;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WorldFindService
{
    private final ReentrantReadWriteLock lock;
    private final HashMap<Integer, Integer> idToChannel;
    private final HashMap<String, Integer> nameToChannel;
    
    private WorldFindService() {
        this.lock = new ReentrantReadWriteLock();
        this.idToChannel = new HashMap<Integer, Integer>();
        this.nameToChannel = new HashMap<String, Integer>();
    }
    
    public static WorldFindService getInstance() {
        return SingletonHolder.instance;
    }
    
    public void forceDeregister(final int chrId) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家离开 - 角色ID: " + chrId);
    }
    
    public void forceDeregister(final String chrName) {
        this.lock.writeLock().lock();
        try {
            this.nameToChannel.remove(chrName.toLowerCase());
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家离开 - 角色名字: " + chrName);
    }
    
    public void forceDeregister(final int chrId, final String chrName) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
            this.nameToChannel.remove(chrName.toLowerCase());
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家离开 - 角色ID: " + chrId + " 名字: " + chrName);
    }
    
    public void forceDeregisterEx(final int chrId, final String chrName) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
            this.nameToChannel.remove(chrName.toLowerCase());
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("清理卡号玩家 - 角色ID: " + chrId + " 名字: " + chrName);
    }
    
    public int findChannel(final int chrId) {
        this.lock.readLock().lock();
        Integer ret;
        try {
            ret = (Integer)(Integer)this.idToChannel.get(chrId);
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (ret == null) {
            return -1;
        }
        if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
            this.forceDeregister(chrId);
            return -1;
        }
        return ret;
    }
    
    public int findChannel(final String chrName) {
        this.lock.readLock().lock();
        Integer ret;
        try {
            ret = (Integer)(Integer)this.nameToChannel.get(chrName.toLowerCase());
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (ret == null) {
            return -1;
        }
        if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
            this.forceDeregister(chrName);
            return -1;
        }
        return ret;
    }
    
    public CharacterIdChannelPair[] multiBuddyFind(final int charIdFrom, final int[] characterIds) {
        final List<CharacterIdChannelPair> foundsChars = new ArrayList<CharacterIdChannelPair>(characterIds.length);
        for (final int i : characterIds) {
            final int channel = this.findChannel(i);
            if (channel > 0) {
                foundsChars.add(new CharacterIdChannelPair(i, channel));
            }
        }
        Collections.sort(foundsChars);
        return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
    }
    
    public MapleCharacter findCharacterByName(final String name) {
        final int ch = this.findChannel(name);
        if (ch > 0) {
            return ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
        }
        return null;
    }
    
    public MapleCharacter findCharacterById(final int id) {
        final int ch = this.findChannel(id);
        if (ch > 0) {
            return ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
        }
        return null;
    }
    
    private static class SingletonHolder
    {
        protected static final WorldFindService instance;
        
        static {
            instance = new WorldFindService();
        }
    }
}
