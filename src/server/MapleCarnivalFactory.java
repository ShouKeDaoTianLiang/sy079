package server;

import client.MapleDisease;
import server.life.MobSkillFactory;
import server.life.MobSkill;
import java.util.Iterator;
import provider.MapleDataTool;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import java.io.File;
import java.util.HashMap;
import provider.MapleDataProvider;
import java.util.Map;

public class MapleCarnivalFactory
{
    private static final MapleCarnivalFactory instance;
    private final Map<Integer, MCSkill> skills;
    private final Map<Integer, MCSkill> guardians;
    private final MapleDataProvider dataRoot;
    
    public MapleCarnivalFactory() {
        this.skills = new HashMap<Integer, MCSkill>();
        this.guardians = new HashMap<Integer, MCSkill>();
        this.dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
        this.initialize();
    }
    
    public static final MapleCarnivalFactory getInstance() {
        return MapleCarnivalFactory.instance;
    }
    
    private void initialize() {
        if (!this.skills.isEmpty()) {
            return;
        }
        for (final MapleData z : this.dataRoot.getData("MCSkill.img")) {
            this.skills.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), MapleDataTool.getInt("target", z, 1) > 1));
        }
        for (final MapleData z : this.dataRoot.getData("MCGuardian.img")) {
            this.guardians.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), true));
        }
    }
    
    public MCSkill getSkill(final int id) {
        return this.skills.get(id);
    }
    
    public MCSkill getGuardian(final int id) {
        return this.guardians.get(id);
    }
    
    static {
        instance = new MapleCarnivalFactory();
    }
    
    public static class MCSkill
    {
        public int cpLoss;
        public int skillid;
        public int level;
        public boolean targetsAll;
        
        public MCSkill(final int _cpLoss, final int _skillid, final int _level, final boolean _targetsAll) {
            this.cpLoss = _cpLoss;
            this.skillid = _skillid;
            this.level = _level;
            this.targetsAll = _targetsAll;
        }
        
        public MobSkill getSkill() {
            return MobSkillFactory.getMobSkill(this.skillid, 1);
        }
        
        public MapleDisease getDisease() {
            if (this.skillid <= 0) {
                return MapleDisease.getRandom();
            }
            return MapleDisease.getBySkill(this.skillid);
        }
    }
}
