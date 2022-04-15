package server;

import java.util.ArrayList;
import client.inventory.Item;
import java.util.List;

public class MerchItemPackage
{
    private long sentTime;
    private int mesos;
    private int packageid;
    private List<Item> items;
    
    public MerchItemPackage() {
        this.mesos = 0;
        this.items = new ArrayList<Item>();
    }
    
    public void setItems(final List<Item> items) {
        this.items = items;
    }
    
    public List<Item> getItems() {
        return this.items;
    }
    
    public void setSentTime(final long sentTime) {
        this.sentTime = sentTime;
    }
    
    public long getSentTime() {
        return this.sentTime;
    }
    
    public int getMesos() {
        return this.mesos;
    }
    
    public void setMesos(final int set) {
        this.mesos = set;
    }
    
    public int getPackageid() {
        return this.packageid;
    }
    
    public void setPackageid(final int packageid) {
        this.packageid = packageid;
    }
}
