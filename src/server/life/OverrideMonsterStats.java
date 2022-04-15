package server.life;

public class OverrideMonsterStats
{
    public long hp;
    public long exp;
    public int mp;
    
    public OverrideMonsterStats() {
        this.hp = 0L;
        this.exp = 0L;
        this.mp = 0;
    }
    
    public OverrideMonsterStats(final long hp, final int mp, final long exp, final boolean change) {
        this.hp = hp;
        this.mp = mp;
        this.exp = exp;
    }
    
    public OverrideMonsterStats(final long hp, final int mp, final long exp) {
        this(hp, mp, exp, true);
    }
    
    public long getExp() {
        return this.exp;
    }
    
    public void setOExp(final long exp) {
        this.exp = exp;
    }
    
    public long getHp() {
        return this.hp;
    }
    
    public void setOHp(final long hp) {
        this.hp = hp;
    }
    
    public int getMp() {
        return this.mp;
    }
    
    public void setOMp(final int mp) {
        this.mp = mp;
    }
}
