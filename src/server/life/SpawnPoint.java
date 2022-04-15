package server.life;

import server.MapleCarnivalFactory.MCSkill;
import java.util.Iterator;
import tools.MaplePacketCreator;
import client.MapleCharacter;
import server.MapleCarnivalFactory;
import server.maps.MapleReactor;
import server.maps.MapleMap;
import constants.ServerConstants;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Point;

public class SpawnPoint extends Spawns
{
    private final MapleMonster monster;
    private final Point pos;
    private long nextPossibleSpawn;
    private int mobTime;
    private int carnival;
    private int level;
    private final AtomicInteger spawnedMonsters;
    private final boolean immobile;
    private final String msg;
    private final byte carnivalTeam;
    
    public SpawnPoint(final MapleMonster monster, final Point pos, final int mobTime, final byte carnivalTeam, final String msg) {
        this.carnival = -1;
        this.level = -1;
        this.spawnedMonsters = new AtomicInteger(0);
        this.monster = monster;
        this.pos = pos;
        this.mobTime = ((mobTime < 0) ? -1 : (mobTime * 1000));
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;
        this.immobile = !monster.getStats().getMobile();
        this.nextPossibleSpawn = System.currentTimeMillis();
    }
    
    public final void setLevel(final int c) {
        this.level = c;
    }
    
    public final void setCarnival(final int c) {
        this.carnival = c;
    }
    
    @Override
    public final Point getPosition() {
        return this.pos;
    }
    
    @Override
    public final MapleMonster getMonster() {
        return this.monster;
    }
    
    @Override
    public final byte getCarnivalTeam() {
        return this.carnivalTeam;
    }
    
    @Override
    public final int getCarnivalId() {
        return this.carnival;
    }
    
    @Override
    public final boolean shouldSpawn() {
        return this.mobTime >= 0 && ((this.mobTime == 0 && !this.immobile) || this.spawnedMonsters.get() <= 0) && this.spawnedMonsters.get() <= ServerConstants.地图出现怪物倍率 && this.spawnedMonsters.get() <= ServerConstants.monsterSpawn && this.nextPossibleSpawn <= System.currentTimeMillis();
    }
    
    @Override
    public final MapleMonster spawnMonster(final MapleMap map) {
        final MapleMonster mob = new MapleMonster(this.monster);
        mob.setPosition(this.pos);
        mob.setCarnivalTeam(this.carnivalTeam);
        if (this.level > -1) {
            mob.changeLevel(this.level, true);
        }
        this.spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {
            @Override
            public void monsterKilled() {
                nextPossibleSpawn = System.currentTimeMillis();
                if (mobTime > 0) {
                    nextPossibleSpawn += mobTime;
                }
                spawnedMonsters.decrementAndGet();
            }
        });
        map.spawnMonster(mob, -2);
        if (this.carnivalTeam > -1) {
            for (final MapleReactor r : map.getAllReactorsThreadsafe()) {
                if (r.getName().startsWith(String.valueOf(this.carnivalTeam)) && r.getReactorId() == 9980000 + this.carnivalTeam && r.getState() < 5) {
                    final int num = Integer.parseInt(r.getName().substring(1, 2));
                    final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                    if (skil == null) {
                        continue;
                    }
                    skil.getSkill().applyEffect(null, mob, false);
                }
            }
        }
        if (this.msg != null) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.msg));
        }
        return mob;
    }
    
    @Override
    public final int getMobTime() {
        return this.mobTime;
    }
}
