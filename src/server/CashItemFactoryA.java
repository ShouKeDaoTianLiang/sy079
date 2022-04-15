package server;

import tools.StringUtil;
import java.util.WeakHashMap;
import java.util.Iterator;
import provider.MapleDataProviderFactory;
import java.io.File;
import java.util.ArrayList;
import provider.MapleDataTool;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import java.util.Map;

public class CashItemFactoryA
{
    private static final Map<Integer, Integer> snLookup;
    private static final Map<Integer, Integer> idLookup;
    private static final Map<Integer, CashItemInfoA> itemStats;
    private static final MapleDataProvider data;
    private static final MapleData commodities;
    private static final Map<Integer, List<CashItemInfoA>> cashPackages;
    
    public static CashItemInfoA getItem(final int sn) {
        CashItemInfoA stats = (server.CashItemInfoA)(server.CashItemInfoA)CashItemFactoryA.itemStats.get(sn);
        if (stats == null) {
            final int cid = getCommodityFromSN(sn);
            final int itemId = MapleDataTool.getIntConvert(cid + "/ItemId", CashItemFactoryA.commodities);
            final int count = MapleDataTool.getIntConvert(cid + "/Count", CashItemFactoryA.commodities, 1);
            final int price = MapleDataTool.getIntConvert(cid + "/Price", CashItemFactoryA.commodities, 0);
            final int period = MapleDataTool.getIntConvert(cid + "/Period", CashItemFactoryA.commodities, 0);
            final int gender = MapleDataTool.getIntConvert(cid + "/Gender", CashItemFactoryA.commodities, 2);
            final boolean onSale = MapleDataTool.getIntConvert(cid + "/OnSale", CashItemFactoryA.commodities, 0) == 1;
            stats = new CashItemInfoA(sn, itemId, count, price, period, gender, onSale);
            CashItemFactoryA.itemStats.put(sn, stats);
        }
        return stats;
    }
    
    private static int getCommodityFromSN(final int sn) {
        int cid;
        if (CashItemFactoryA.snLookup.get(sn) == null) {
            int curr = CashItemFactoryA.snLookup.size() - 1;
            int currSN = 0;
            if (curr == -1) {
                curr = 0;
                currSN = MapleDataTool.getIntConvert("0/SN", CashItemFactoryA.commodities);
                CashItemFactoryA.snLookup.put(currSN, curr);
            }
            int i = CashItemFactoryA.snLookup.size() - 1;
            while (currSN != sn) {
                curr = i;
                currSN = MapleDataTool.getIntConvert(curr + "/SN", CashItemFactoryA.commodities);
                CashItemFactoryA.snLookup.put(currSN, curr);
                ++i;
            }
            cid = curr;
        }
        else {
            cid = (Integer)(Integer)CashItemFactoryA.snLookup.get(sn);
        }
        return cid;
    }
    
    public static List<CashItemInfoA> getPackageItems(final int itemId) {
        if (CashItemFactoryA.cashPackages.containsKey(itemId)) {
            return CashItemFactoryA.cashPackages.get(itemId);
        }
        final List<CashItemInfoA> packageItems = new ArrayList<CashItemInfoA>();
        final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.cherry.wzpath") + "/Etc.wz"));
        final MapleData a = dataProvider.getData("CashPackage.img");
        for (final MapleData b : a.getChildren()) {
            if (itemId == Integer.parseInt(b.getName())) {
                for (final MapleData c : b.getChildren()) {
                    for (final MapleData d : c.getChildren()) {
                        final int SN = MapleDataTool.getIntConvert("" + Integer.parseInt(d.getName()), c);
                        packageItems.add(getItem(SN));
                    }
                }
                break;
            }
        }
        CashItemFactoryA.cashPackages.put(itemId, packageItems);
        return packageItems;
    }
    
    public static int getSnFromId(final int id) {
        int cid;
        if (CashItemFactoryA.idLookup.get(id) == null) {
            int curr = CashItemFactoryA.idLookup.size() - 1;
            int currSN = 0;
            if (curr == -1) {
                curr = 0;
                currSN = MapleDataTool.getIntConvert("0/ItemId", CashItemFactoryA.commodities);
                CashItemFactoryA.idLookup.put(currSN, curr);
            }
            int i = CashItemFactoryA.idLookup.size() - 1;
            while (currSN != id) {
                curr = i;
                currSN = MapleDataTool.getIntConvert(curr + "/ItemId", CashItemFactoryA.commodities);
                CashItemFactoryA.idLookup.put(currSN, curr);
                ++i;
            }
            cid = curr;
        }
        else {
            cid = (Integer)(Integer)CashItemFactoryA.idLookup.get(id);
        }
        return MapleDataTool.getIntConvert(cid + "/SN", CashItemFactoryA.commodities);
    }
    
    static {
        snLookup = new WeakHashMap<Integer, Integer>();
        idLookup = new WeakHashMap<Integer, Integer>();
        itemStats = new WeakHashMap<Integer, CashItemInfoA>();
        data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
        commodities = CashItemFactoryA.data.getData(StringUtil.getLeftPaddedStr("Commodity.img", '0', 11));
        cashPackages = new WeakHashMap<Integer, List<CashItemInfoA>>();
    }
}
