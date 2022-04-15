package client;

import java.util.HashMap;
import java.util.Collection;
import tools.StringUtil;
import java.util.Iterator;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataTool;
import java.util.ArrayList;
import provider.MapleDataFileEntry;
import provider.MapleDataProviderFactory;
import java.io.File;
import provider.MapleDataProvider;
import provider.MapleData;
import java.util.List;
import java.util.Map;

public class SkillFactory
{
    private static final Map<Integer, ISkill> skills;
    private static final Map<Integer, List<Integer>> skillsByJob;
    private static final Map<Integer, SummonSkillEntry> SummonSkillInformation;
    private static final MapleData stringData;
    private static final MapleDataProvider datasource;
    
    public static final ISkill getSkill(final int id) {
        if (!SkillFactory.skills.isEmpty()) {
            return SkillFactory.skills.get(id);
        }
        final MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
        final MapleDataDirectoryEntry root = datasource.getRoot();
        for (final MapleDataFileEntry topDir : root.getFiles()) {
            if (topDir.getName().length() <= 8) {
                for (final MapleData data : datasource.getData(topDir.getName())) {
                    if (data.getName().equals("skill")) {
                        for (final MapleData data2 : data) {
                            if (data2 != null) {
                                final int skillid = Integer.parseInt(data2.getName());
                                final Skill skil = Skill.loadFromData(skillid, data2);
                                List job = (java.util.List)(java.util.List)SkillFactory.skillsByJob.get(skillid / 10000);
                                if (job == null) {
                                    job = new ArrayList();
                                    SkillFactory.skillsByJob.put(skillid / 10000, job);
                                }
                                job.add(skillid);
                                skil.setName(getName(skillid));
                                SkillFactory.skills.put(skillid, skil);
                                final MapleData summon_data = data2.getChildByPath("summon/attack1/info");
                                if (summon_data == null) {
                                    continue;
                                }
                                final SummonSkillEntry sse = new SummonSkillEntry();
                                sse.attackAfter = (short)MapleDataTool.getInt("attackAfter", summon_data, 999999);
                                sse.type = (byte)MapleDataTool.getInt("type", summon_data, 0);
                                sse.mobCount = (byte)MapleDataTool.getInt("mobCount", summon_data, 1);
                                SkillFactory.SummonSkillInformation.put(skillid, sse);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static ISkill getSkill1(final int id) {
        ISkill ret = (client.ISkill)(client.ISkill)SkillFactory.skills.get(id);
        if (ret != null) {
            return ret;
        }
        synchronized (SkillFactory.skills) {
            ret = (client.ISkill)(client.ISkill)SkillFactory.skills.get(id);
            if (ret == null) {
                final int job = id / 10000;
                final MapleData skillroot = SkillFactory.datasource.getData(StringUtil.getLeftPaddedStr(String.valueOf(job), '0', 3) + ".img");
                final MapleData skillData = skillroot.getChildByPath("skill/" + StringUtil.getLeftPaddedStr(String.valueOf(id), '0', 7));
                if (skillData != null) {
                    ret = Skill.loadFromData(id, skillData);
                }
                SkillFactory.skills.put(id, ret);
            }
            return ret;
        }
    }
    
    public static final List<Integer> getSkillsByJob(final int jobId) {
        return SkillFactory.skillsByJob.get(jobId);
    }
    
    public static final String getSkillName(final int id) {
        final ISkill skil = getSkill(id);
        if (skil != null) {
            return skil.getName();
        }
        return null;
    }
    
    public static final String getName(final int id) {
        String strId = Integer.toString(id);
        strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
        final MapleData skillroot = SkillFactory.stringData.getChildByPath(strId);
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }
    
    public static final SummonSkillEntry getSummonData(final int skillid) {
        return SkillFactory.SummonSkillInformation.get(skillid);
    }
    
    public static final Collection<ISkill> getAllSkills() {
        return SkillFactory.skills.values();
    }
    
    static {
        skills = new HashMap<Integer, ISkill>();
        skillsByJob = new HashMap<Integer, List<Integer>>();
        SummonSkillInformation = new HashMap<Integer, SummonSkillEntry>();
        stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")).getData("Skill.img");
        datasource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
    }
}
