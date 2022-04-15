package client.inventory;

import java.util.HashMap;
import provider.MapleDataProviderFactory;
import java.io.File;
import provider.MapleData;
import provider.MapleDataTool;
import tools.Pair;
import java.util.Map;
import provider.MapleDataProvider;

public class PetDataFactory
{
    private static final MapleDataProvider dataRoot;
    private static final Map<Pair<Integer, Integer>, PetCommand> petCommands;
    private static final Map<Integer, Integer> petHunger;
    
    public static final PetCommand getPetCommand(final int petId, final int skillId) {
        PetCommand ret = (client.inventory.PetCommand)(client.inventory.PetCommand)PetDataFactory.petCommands.get(new Pair(petId, skillId));
        if (ret != null) {
            return ret;
        }
        final MapleData skillData = PetDataFactory.dataRoot.getData("Pet/" + petId + ".img");
        int prob = 0;
        int inc = 0;
        if (skillData != null) {
            prob = MapleDataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
            inc = MapleDataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
        }
        ret = new PetCommand(petId, skillId, prob, inc);
        PetDataFactory.petCommands.put(new Pair<Integer, Integer>(petId, skillId), ret);
        return ret;
    }
    
    public static final int getHunger(final int petId) {
        Integer ret = (Integer)(Integer)PetDataFactory.petHunger.get(petId);
        if (ret != null) {
            return ret;
        }
        final MapleData hungerData = PetDataFactory.dataRoot.getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        ret = MapleDataTool.getInt(hungerData, 1);
        PetDataFactory.petHunger.put(petId, ret);
        return ret;
    }
    
    static {
        dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
        petCommands = new HashMap<Pair<Integer, Integer>, PetCommand>();
        petHunger = new HashMap<Integer, Integer>();
    }
}
