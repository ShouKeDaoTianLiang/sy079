package client.anticheat;

import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import tools.StringUtil;
import server.AutobanManager;
import tools.FileoutputUtil;
import handling.world.World.Broadcast;
import tools.MaplePacketCreator;
import client.MapleCharacterUtil;
import constants.GameConstants;
import server.Timer.CheatTimer;
import java.util.EnumMap;
import java.util.concurrent.ScheduledFuture;
import java.awt.Point;
import client.MapleCharacter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CheatTracker
{
    private final ReentrantReadWriteLock lock;
    private final Lock rL;
    private final Lock wL;
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses;
    private final WeakReference<MapleCharacter> chr;
    private int lastAttackTickCount;
    private byte Attack_tickResetCount;
    private long Server_ClientAtkTickDiff;
    private long lastDamage;
    private long takingDamageSince;
    private int numSequentialDamage;
    private long lastDamageTakenTime;
    private byte numZeroDamageTaken;
    private int numSequentialSummonAttack;
    private long summonSummonTime;
    private int numSameDamage;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit;
    private byte dropsPerSecond;
    private long lastDropTime;
    private byte msgsPerSecond;
    private long lastMsgTime;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message;
    private int lastTickCount;
    private int tickSame;
    private long lastASmegaTime;
    private boolean saveToDB;
    private long lastSaveTime;
    private long[] lastTime;
    
    public CheatTracker(final MapleCharacter chr) {
        this.lock = new ReentrantReadWriteLock();
        this.rL = this.lock.readLock();
        this.wL = this.lock.writeLock();
        this.offenses = new EnumMap<CheatingOffense, CheatingOffenseEntry>(CheatingOffense.class);
        this.lastAttackTickCount = 0;
        this.Attack_tickResetCount = 0;
        this.Server_ClientAtkTickDiff = 0L;
        this.lastDamage = 0L;
        this.numSequentialDamage = 0;
        this.lastDamageTakenTime = 0L;
        this.numZeroDamageTaken = 0;
        this.numSequentialSummonAttack = 0;
        this.summonSummonTime = 0L;
        this.numSameDamage = 0;
        this.attacksWithoutHit = 0;
        this.dropsPerSecond = 0;
        this.lastDropTime = 0L;
        this.msgsPerSecond = 0;
        this.lastMsgTime = 0L;
        this.gm_message = 50;
        this.lastTickCount = 0;
        this.tickSame = 0;
        this.lastASmegaTime = 0L;
        this.saveToDB = false;
        this.lastSaveTime = 0L;
        this.lastTime = new long[6];
        this.chr = new WeakReference<MapleCharacter>(chr);
        this.invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000L);
        this.takingDamageSince = System.currentTimeMillis();
    }
    
    public final void checkAttack(final int skillId, final int tickcount) {
        final short AtkDelay = GameConstants.getAttackDelay(skillId);
        if (tickcount - this.lastAttackTickCount < AtkDelay) {
            this.registerOffense(CheatingOffense.FASTATTACK);
        }
        final long STime_TC = System.currentTimeMillis() - tickcount;
        if (this.Server_ClientAtkTickDiff - STime_TC > 250L) {
            this.registerOffense(CheatingOffense.FASTATTACK2);
        }
        ++this.Attack_tickResetCount;
        if (this.Attack_tickResetCount >= ((AtkDelay <= 200) ? 2 : 4)) {
            this.Attack_tickResetCount = 0;
            this.Server_ClientAtkTickDiff = STime_TC;
        }
        this.chr.get().updateTick(tickcount);
        this.lastAttackTickCount = tickcount;
    }
    
    public final void checkTakeDamage(final int damage) {
        ++this.numSequentialDamage;
        this.lastDamageTakenTime = System.currentTimeMillis();
        if (this.lastDamageTakenTime - this.takingDamageSince / 500L < this.numSequentialDamage) {
            this.registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
        }
        if (this.lastDamageTakenTime - this.takingDamageSince > 4500L) {
            this.takingDamageSince = this.lastDamageTakenTime;
            this.numSequentialDamage = 0;
        }
        if (damage == 0) {
            ++this.numZeroDamageTaken;
            if (this.numZeroDamageTaken >= 35) {
                this.numZeroDamageTaken = 0;
                this.registerOffense(CheatingOffense.HIGH_AVOID);
            }
        }
        else if (damage != -1) {
            this.numZeroDamageTaken = 0;
        }
    }
    
    public boolean canSaveDB() {
        if (!this.saveToDB) {
            this.saveToDB = true;
            return false;
        }
        if (this.lastSaveTime + 60000L > System.currentTimeMillis() && this.chr.get() != null) {
            return false;
        }
        this.lastSaveTime = System.currentTimeMillis();
        return true;
    }
    
    public final void checkSameDamage(final int dmg) {
        if (dmg > 2000 && this.lastDamage == dmg) {
            ++this.numSameDamage;
            if (this.numSameDamage > 5) {
                this.numSameDamage = 0;
                this.registerOffense(CheatingOffense.SAME_DAMAGE, this.numSameDamage + " times: " + dmg);
            }
        }
        else {
            this.lastDamage = dmg;
            this.numSameDamage = 0;
        }
    }
    
    public final void checkMoveMonster(final Point pos, final MapleCharacter chr) {
        if (pos.equals(this.lastMonsterMove)) {
            ++this.monsterMoveCount;
            if (this.monsterMoveCount > 50) {
                this.monsterMoveCount = 0;
                Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[???????????????] ????????????[" + MapleCharacterUtil.makeMapleReadable(chr.getName()) + "] ??????ID[" + chr.getMapId() + "] ??????????????????! ").getBytes());
                final String note = "?????????" + FileoutputUtil.CurrentReadable_Time() + " || ???????????????" + chr.getName() + "|| ???????????????" + chr.getMapId() + "\r\n";
                FileoutputUtil.packetLog("log\\????????????\\" + chr.getName() + ".log", note);
            }
        }
        else {
            this.lastMonsterMove = pos;
            this.monsterMoveCount = 1;
        }
    }
    
    public void checkSameDamage(final int dmg, final double expected) {
        if (dmg > 2000 && this.lastDamage == dmg && this.chr.get() != null && (this.chr.get().getLevel() < 180 || dmg > expected * 2.0)) {
            ++this.numSameDamage;
            if (this.numSameDamage > 5) {
                this.registerOffense(CheatingOffense.SAME_DAMAGE, this.numSameDamage + " times, ???????????? " + dmg + ", ???????????? " + expected + " [??????: " + this.chr.get().getLevel() + ", ??????: " + this.chr.get().getJob() + "]");
                this.numSameDamage = 0;
            }
        }
        else {
            this.lastDamage = dmg;
            this.numSameDamage = 0;
        }
    }
    
    public final void resetSummonAttack() {
        this.summonSummonTime = System.currentTimeMillis();
        this.numSequentialSummonAttack = 0;
    }
    
    public final boolean checkSummonAttack() {
        ++this.numSequentialSummonAttack;
        if ((System.currentTimeMillis() - this.summonSummonTime) / 2001L < this.numSequentialSummonAttack) {
            this.registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
            return false;
        }
        return true;
    }
    
    public final void checkDrop() {
        this.checkDrop(false);
    }
    
    public final void checkDrop(final boolean dc) {
        if (System.currentTimeMillis() - this.lastDropTime < 1000L) {
            ++this.dropsPerSecond;
            if (this.dropsPerSecond >= (dc ? 32 : 16) && this.chr.get() != null) {
                this.chr.get().getClient().setMonitored(true);
            }
        }
        else {
            this.dropsPerSecond = 0;
        }
        this.lastDropTime = System.currentTimeMillis();
    }
    
    public boolean canAvatarSmega2() {
        if (this.lastASmegaTime + 10000L > System.currentTimeMillis() && this.chr.get() != null && !this.chr.get().isGM()) {
            return false;
        }
        this.lastASmegaTime = System.currentTimeMillis();
        return true;
    }
    
    public synchronized boolean GMSpam(final int limit, int type) {
        if (type < 0 || this.lastTime.length < type) {
            type = 1;
        }
        if (System.currentTimeMillis() < limit + this.lastTime[type]) {
            return true;
        }
        this.lastTime[type] = System.currentTimeMillis();
        return false;
    }
    
    public final void checkMsg() {
        if (System.currentTimeMillis() - this.lastMsgTime < 1000L) {
            ++this.msgsPerSecond;
        }
        else {
            this.msgsPerSecond = 0;
        }
        this.lastMsgTime = System.currentTimeMillis();
    }
    
    public final int getAttacksWithoutHit() {
        return this.attacksWithoutHit;
    }
    
    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            ++this.attacksWithoutHit;
        }
        else {
            this.attacksWithoutHit = 0;
        }
    }
    
    public final void registerOffense(final CheatingOffense offense) {
        this.registerOffense(offense, null);
    }
    
    public final void registerOffense(final CheatingOffense offense, final String param) {
        final MapleCharacter chrhardref = this.chr.get();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone() || chrhardref.isGM()) {
            return;
        }
        CheatingOffenseEntry entry = null;
        this.rL.lock();
        try {
            entry = (client.anticheat.CheatingOffenseEntry)(client.anticheat.CheatingOffenseEntry)this.offenses.get(offense);
        }
        finally {
            this.rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            this.expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            final byte type = offense.getBanType();
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            }
            else if (type == 2) {}
            this.gm_message = 50;
            return;
        }
        this.wL.lock();
        try {
            this.offenses.put(offense, entry);
        }
        finally {
            this.wL.unlock();
        }
        switch (offense) {
            case SAME_DAMAGE: {
                --this.gm_message;
                if (this.gm_message == 0) {
                    System.out.println(MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + "??????????????????");
                    Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[???????????????] ????????????[" + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + "] ??????ID[" + chrhardref.getMapId() + "] suspected of hacking! " + StringUtil.makeEnumHumanReadable(offense.name()) + ((param == null) ? "" : (" - " + param))).getBytes());
                    final String note = "?????????" + FileoutputUtil.CurrentReadable_Time() + " || ???????????????" + chrhardref.getName() + "|| ???????????????" + chrhardref.getMapId() + "|| ???????????????" + offense.name() + "\r\n";
                    FileoutputUtil.packetLog("log\\????????????\\" + chrhardref.getName() + ".log", note);
                    this.gm_message = 50;
                    break;
                }
                break;
            }
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }
    
    public void updateTick(final int newTick) {
        if (newTick == this.lastTickCount) {
            ++this.tickSame;
        }
        else {
            this.tickSame = 0;
        }
        this.lastTickCount = newTick;
    }
    
    public final void expireEntry(final CheatingOffenseEntry coe) {
        this.wL.lock();
        try {
            this.offenses.remove(coe.getOffense());
        }
        finally {
            this.wL.unlock();
        }
    }
    
    public final int getPoints() {
        int ret = 0;
        this.rL.lock();
        CheatingOffenseEntry[] offenses_copy;
        try {
            offenses_copy = this.offenses.values().toArray(new CheatingOffenseEntry[this.offenses.size()]);
        }
        finally {
            this.rL.unlock();
        }
        for (final CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                this.expireEntry(entry);
            }
            else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }
    
    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap((Map<? extends CheatingOffense, ? extends CheatingOffenseEntry>)this.offenses);
    }
    
    public final String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<CheatingOffenseEntry>();
        this.rL.lock();
        try {
            for (final CheatingOffenseEntry entry : this.offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        }
        finally {
            this.rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {
            @Override
            public final int compare(final CheatingOffenseEntry o1, final CheatingOffenseEntry o2) {
                final int thisVal = o1.getPoints();
                final int anotherVal = o2.getPoints();
                return (thisVal < anotherVal) ? 1 : ((thisVal == anotherVal) ? 0 : -1);
            }
        });
        for (int to = Math.min(offenseList.size(), 4), x = 0; x < to; ++x) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }
    
    public final void dispose() {
        if (this.invalidationTask != null) {
            this.invalidationTask.cancel(false);
        }
        this.invalidationTask = null;
    }
    
    private final class InvalidationTask implements Runnable
    {
        @Override
        public final void run() {
            rL.lock();
            CheatingOffenseEntry[] offenses_copy;
            try {
                offenses_copy = (CheatingOffenseEntry[])offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            }
            finally {
                rL.unlock();
            }
            for (final CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    CheatTracker.this.expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                CheatTracker.this.dispose();
            }
        }
    }
}
