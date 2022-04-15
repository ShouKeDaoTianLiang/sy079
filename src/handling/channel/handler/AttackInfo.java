package handling.channel.handler;

import server.AutobanManager;
import client.SkillFactory;
import constants.GameConstants;
import server.MapleStatEffect;
import client.ISkill;
import client.MapleCharacter;
import java.awt.Point;
import tools.AttackPair;
import java.util.List;

public class AttackInfo
{
    public int skill;
    public int charge;
    public int lastAttackTickCount;
    public int direction;
    public List<AttackPair> allDamage;
    public Point position;
    public byte hits;
    public byte targets;
    public byte tbyte;
    public byte display;
    public byte animation;
    public byte speed;
    public byte csstar;
    public byte AOE;
    public byte slot;
    public byte unk;
    public boolean real;
    public boolean isCloseRangeAttack;
    
    public AttackInfo() {
        this.real = true;
        this.isCloseRangeAttack = false;
    }
    
    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final ISkill skill_) {
        if (GameConstants.isMulungSkill(this.skill) || GameConstants.isPyramidSkill(this.skill)) {
            skillLevel = 1;
        }
        else if (skillLevel <= 0) {
            return null;
        }
        if (GameConstants.isLinkedAranSkill(this.skill)) {
            final ISkill skillLink = SkillFactory.getSkill(this.skill);
            if (this.display > 80 && !skillLink.getAction()) {
                AutobanManager.getInstance().autoban(chr.getClient(), "攻击无延迟，技能ID： : " + this.skill);
                return null;
            }
            return skillLink.getEffect(skillLevel);
        }
        else {
            if (this.display > 80 && !skill_.getAction()) {
                AutobanManager.getInstance().autoban(chr.getClient(), "攻击无延迟，技能ID： " + this.skill);
                return null;
            }
            return skill_.getEffect(skillLevel);
        }
    }
}
