package server;

import java.util.Collections;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

public class RandomRewards
{
    private static final RandomRewards instance;
    private List<Integer> compiledGold;
    private List<Integer> compiledSilver;
    private final Properties itempb_cfg;
    private List<Integer> compiledFishing;
    private List<Integer> compiledEvent;
    private List<Integer> compiledEventC;
    private List<Integer> compiledEventB;
    private List<Integer> compiledEventA;
    private List<Integer> 黑龙箱子;
    private List<Integer> 水晶天平;
    
    public static RandomRewards getInstance() {
        return RandomRewards.instance;
    }
    
    protected RandomRewards() {
        this.compiledGold = null;
        this.compiledSilver = null;
        this.itempb_cfg = new Properties();
        this.compiledFishing = null;
        this.compiledEvent = null;
        this.compiledEventC = null;
        this.compiledEventB = null;
        this.compiledEventA = null;
        this.黑龙箱子 = null;
        this.水晶天平 = null;
        List returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.goldrewards);
        this.compiledGold = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.黑龙箱子);
        this.黑龙箱子 = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.silverrewards);
        this.compiledSilver = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.fishingReward);
        this.compiledFishing = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventCommonReward);
        this.compiledEventC = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventUncommonReward);
        this.compiledEventB = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventRareReward);
        this.compiledEventA = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventSuperReward);
        this.compiledEvent = (List<Integer>)returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.水晶天平s);
        this.水晶天平 = (List<Integer>)returnArray;
        returnArray = new ArrayList();
    }
    
    private final void processRewards(final List<Integer> returnArray, final int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; ++i) {
            if (i % 2 == 0) {
                lastitem = list[i];
            }
            else {
                for (int j = 0; j < list[i]; ++j) {
                    returnArray.add(lastitem);
                }
            }
        }
        Collections.shuffle(returnArray);
    }
    
    public final int getGoldBoxReward() {
        return this.compiledGold.get(Randomizer.nextInt(this.compiledGold.size()));
    }
    
    public final int 黑龙箱子() {
        return this.黑龙箱子.get(Randomizer.nextInt(this.黑龙箱子.size()));
    }
    
    public final int getSilverBoxReward() {
        return this.compiledSilver.get(Randomizer.nextInt(this.compiledSilver.size()));
    }
    
    public final int get极品道具() {
        return this.水晶天平.get(Randomizer.nextInt(this.水晶天平.size()));
    }
    
    public final int getFishingReward() {
        return this.compiledFishing.get(Randomizer.nextInt(this.compiledFishing.size()));
    }
    
    public final int getEventReward() {
        final int chance = Randomizer.nextInt(100);
        if (chance < 50) {
            return this.compiledEventC.get(Randomizer.nextInt(this.compiledEventC.size()));
        }
        if (chance < 80) {
            return this.compiledEventB.get(Randomizer.nextInt(this.compiledEventB.size()));
        }
        if (chance < 95) {
            return this.compiledEventA.get(Randomizer.nextInt(this.compiledEventA.size()));
        }
        return this.compiledEvent.get(Randomizer.nextInt(this.compiledEvent.size()));
    }
    
    static {
        instance = new RandomRewards();
    }
}
