package handling.channel.handler;

import server.MapleStatEffect;
import client.ISkill;
import server.life.MapleMonster;
import client.SummonSkillEntry;
import server.maps.MapleMapObject;
import server.life.MobSkill;
import client.status.MonsterStatusEffect;
import client.status.MonsterStatus;
import java.util.Map.Entry;
import server.life.SummonAttackEntry;
import client.SkillFactory;
import server.maps.MapleMapObjectType;
import client.MapleClient;
import client.MapleBuffStat;
import java.util.Iterator;
import server.maps.SummonMovementType;
import server.maps.MapleSummon;
import java.lang.ref.WeakReference;
import java.awt.Point;
import java.util.List;
import server.maps.MapleMap;
import server.Timer.CloneTimer;
import java.util.Collection;
import server.movement.LifeMovementFragment;
import java.util.ArrayList;
import tools.MaplePacketCreator;
import server.maps.AnimatedMapleMapObject;
import client.MapleCharacter;
import tools.data.input.SeekableLittleEndianAccessor;

public class SummonHandler
{
    public static final void MoveDragon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        slea.skip(8);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 5, chr);
        if (chr != null && chr.getDragon() != null) {
            final Point pos = chr.getDragon().getPosition();
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveDragon(chr.getDragon(), pos, res), chr.getPosition());
            }
            final WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() != null) {
                    final MapleMap map = chr.getMap();
                    final MapleCharacter clone = clones[i].get();
                    final List<LifeMovementFragment> res2 = new ArrayList<LifeMovementFragment>(res);
                    CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map && clone.getDragon() != null) {
                                    final Point startPos = clone.getDragon().getPosition();
                                    MovementParse.updatePosition(res2, clone.getDragon(), 0);
                                    if (!clone.isHidden()) {
                                        map.broadcastMessage(clone, MaplePacketCreator.moveDragon(clone.getDragon(), startPos, res2), clone.getPosition());
                                    }
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }
    
    public static final void MoveSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int oid = slea.readInt();
        final Point startPos = new Point(slea.readShort(), slea.readShort());
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 5, chr);
        if (chr == null) {
            return;
        }
        for (final MapleSummon sum : chr.getSummons().values()) {
            if (sum.getObjectId() == oid && sum.getMovementType() != SummonMovementType.STATIONARY) {
                final Point pos = sum.getPosition();
                MovementParse.updatePosition(res, sum, 0);
                if (!sum.isChangedMap()) {
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveSummon(chr.getId(), oid, startPos, res), sum.getPosition());
                    break;
                }
                break;
            }
        }
    }
    
    public static final void DamageSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int unkByte = slea.readByte();
        final int damage = slea.readInt();
        final int monsterIdFrom = slea.readInt();
        for (final MapleSummon summon : chr.getSummons().values()) {
            if (summon.isPuppet() && summon.getOwnerId() == chr.getId()) {
                summon.addHP((short)(-damage));
                if (summon.getHP() <= 0) {
                    chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
                }
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getPosition());
                break;
            }
        }
    }
    
    public static void SummonAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null) {
            return;
        }
        final MapleMap map = chr.getMap();
        final MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        final MapleSummon summon = (MapleSummon)obj;
        if (summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0) {
            return;
        }
        final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if (sse == null) {
            return;
        }
        slea.skip(8);
        final int tick = slea.readInt();
        chr.updateTick(tick);
        summon.CheckSummonAttackFrequency(chr, tick);
        slea.skip(8);
        final byte animation = slea.readByte();
        slea.skip(8);
        final int numAttacked = slea.readByte();
        final List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
        chr.getCheatTracker().checkSummonAttack();
        for (int i = 0; i < numAttacked; ++i) {
            final MapleMonster mob = map.getMonsterByOid(slea.readInt());
            if (mob != null) {
                slea.skip(14);
                final int damage = slea.readInt();
                allDamage.add(new SummonAttackEntry(mob, damage));
            }
        }
        if (!summon.isChangedMap()) {}
        final ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
        final MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        if (summonEffect == null) {
            chr.dropMessage(5, "召唤兽攻击出现错误 => 攻击效果为空.");
            return;
        }
        for (final SummonAttackEntry attackEntry : allDamage) {
            final int toDamage = attackEntry.getDamage();
            final MapleMonster mob2 = attackEntry.getMonster();
            if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0 && summonEffect.makeChanceResult()) {
                for (final Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                    mob2.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000L, false);
                }
            }
        }
        if (summon.isGaviota()) {
            chr.getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        }
    }
}
