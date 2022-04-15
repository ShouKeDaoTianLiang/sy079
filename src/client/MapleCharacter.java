package client;

import client.inventory.ModifyInventory;
import tools.StringUtil;
import java.awt.Rectangle;
import server.life.MapleLifeFactory;
import client.inventory.Equip;
import server.Timer.EventTimer;
import server.maps.MapleMapEffect;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import scripting.NPCScriptManager;
import constants.ServerConstants.PlayerGMRank;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetAddress;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.PlayerBuffStorage;
import handling.world.MapleMessengerCharacter;
import tools.MapleAESOFB;
import io.netty.channel.Channel;
import handling.netty.MapleSession;
import tools.MockIOSession;
import client.inventory.MapleInventoryIdentifier;
import server.maps.MapleFoothold;
import java.util.Comparator;
import client.inventory.MapleRing.RingComparator;
import client.inventory.MapleRing;
import tools.packet.MonsterCarnivalPacket;
import tools.packet.PlayerShopPacket;
import server.life.MobSkill;
import provider.MapleDataProvider;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import java.io.File;
import handling.world.World.Guild;
import handling.world.guild.MapleGuild;
import handling.world.World.Broadcast;
import tools.packet.MobPacket;
import client.inventory.ItemFlag;
import java.util.Properties;
import handling.world.World.Family;
import server.ServerProperties;
import server.maps.FieldLimitType;
import tools.packet.MTSCSPacket;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.MaplePacket;
import java.util.Collections;
import handling.world.PlayerBuffValueHolder;
import java.util.Arrays;
import tools.packet.PetPacket;
import server.maps.MapleMapObjectType;
import java.util.EnumMap;
import server.MapleItemInformationProvider;
import tools.packet.UIPacket;
import server.Randomizer;
import constants.FishingConstants;
import server.MapleInventoryManipulator;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.Timer.BuffTimer;
import server.MapleStatEffect;
import java.util.Collection;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;
import server.life.PlayerNPC;
import database.DatabaseException;
import constants.ServerConstants;
import tools.FileoutputUtil;
import java.util.Calendar;
import client.inventory.Item;
import tools.Pair;
import client.inventory.ItemLoader;
import java.util.Iterator;
import server.MaplePortal;
import server.maps.MapleMapFactory;
import constants.GameConstants;
import java.util.Map.Entry;
import handling.world.World.Party;
import handling.world.World.Messenger;
import handling.channel.ChannelServer;
import handling.world.CharacterTransfer;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import database.DatabaseConnection;
import server.maps.SavedLocationType;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import client.inventory.MapleInventoryType;
import java.util.HashMap;
import java.util.ArrayList;
import tools.ConcurrentEnumMap;
import java.util.LinkedHashMap;
import server.Timer.WorldTimer;
import client.anticheat.ReportType;
import server.maps.Event_PyramidSubway;
import java.util.concurrent.ScheduledFuture;
import client.inventory.MapleInventory;
import scripting.EventInstanceManager;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuildCharacter;
import handling.world.MapleParty;
import server.shops.IMaplePlayerShop;
import handling.world.MapleMessenger;
import client.inventory.MapleMount;
import server.MapleTrade;
import server.MapleStorage;
import server.maps.MapleDragon;
import server.MapleShop;
import server.maps.MapleMap;
import client.anticheat.CheatTracker;
import server.MapleCarnivalParty;
import server.MapleCarnivalChallenge;
import java.util.Deque;
import server.CashShop;
import server.maps.MapleSummon;
import server.quest.MapleQuest;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.maps.MapleMapObject;
import server.life.MapleMonster;
import java.util.Set;
import java.lang.ref.WeakReference;
import client.inventory.MaplePet;
import server.maps.MapleDoor;
import server.movement.LifeMovementFragment;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Point;
import java.sql.Timestamp;
import java.io.Serializable;
import server.maps.AbstractAnimatedMapleMapObject;

public class MapleCharacter extends AbstractAnimatedMapleMapObject implements Serializable
{
    private static final long serialVersionUID = 845748950829L;
    private String name;
    private String chalktext;
    private String BlessOfFairy_Origin;
    private String charmessage;
    private long lastCombo;
    private long lastfametime;
    private long keydown_skill;
    private byte dojoRecord;
    private byte gmLevel;
    private byte gender;
    private byte initialSpawnPoint;
    private byte skinColor;
    private byte guildrank;
    private byte allianceRank;
    private byte world;
    private byte fairyExp;
    private byte numClones;
    private byte subcategory;
    private short level;
    private short mulung_energy;
    private short combo;
    private short availableCP;
    private short totalCP;
    private short fame;
    private short hpApUsed;
    private short job;
    private short remainingAp;
    private int accountid;
    private int id;
    private int meso;
    private int exp;
    private int hair;
    private int face;
    private int mapid;
    private int bookCover;
    private int dojo;
    private int guildid;
    private int fallcounter;
    private int chair;
    private int itemEffect;
    private int points;
    private int vpoints;
    private int rank;
    private int rankMove;
    private int jobRank;
    private int jobRankMove;
    private int marriageId;
    private int marriageItemId;
    private int currentrep;
    private int totalrep;
    private int linkMid;
    private int coconutteam;
    private int followid;
    private int battleshipHP;
    private int expression;
    private int constellation;
    private int blood;
    private int month;
    private int day;
    private int beans;
    private int beansNum;
    private int beansRange;
    private int prefix;
    private int vip;
    private int vipczz;
    private Timestamp viptime;
    private final int gachexp = 0;
    private boolean canSetBeansNum;
    private Point old;
    private boolean smega;
    private boolean hidden;
    private boolean hasSummon;
    private int[] wishlist;
    private int[] rocks;
    private int[] savedLocations;
    private int[] regrocks;
    private int[] remainingSp;
    private transient AtomicInteger inst;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids;
    private List<MapleDoor> doors;
    private List<MaplePet> pets;
    private transient WeakReference<MapleCharacter>[] clones;
    private transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private final Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private final Map<ISkill, SkillEntry> skills;
    private final transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects;
    private transient Map<Integer, MapleSummon> summons;
    private final transient Map<Integer, MapleCoolDownValueHolder> coolDowns;
    private final transient Map<MapleDisease, MapleDiseaseValueHolder> diseases;
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private PlayerStats stats;
    private transient PlayerRandomStream CRand;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient RockPaperScissors rps;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private final List<Integer> finishedAchievements;
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private MapleParty party;
    private boolean invincible;
    private boolean canTalk;
    private boolean clone;
    private boolean followinitiator;
    private boolean followon;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros;
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> beholderHealingSchedule;
    private transient ScheduledFuture<?> beholderBuffSchedule;
    private transient ScheduledFuture<?> BerserkSchedule;
    private transient ScheduledFuture<?> dragonBloodSchedule;
    private transient ScheduledFuture<?> fairySchedule;
    private transient ScheduledFuture<?> mapTimeLimitTask;
    private transient ScheduledFuture<?> fishing;
    private long nextConsume;
    private long pqStartTime;
    private transient Event_PyramidSubway pyramidSubway;
    private transient List<Integer> pendingExpiration;
    private transient List<Integer> pendingSkills;
    private final transient Map<Integer, Integer> movedMobs;
    private String teleportname;
    private int APQScore;
    private long lasttime;
    private long currenttime;
    private long deadtime;
    private short scrolledPosition;
    private static final String[] ariantroomleader;
    private static final int[] ariantroomslot;
    public int apprentice;
    public int master;
    public boolean DebugMessage;
    public int ariantScore;
    public long lastGainHM;
    private long lastHPTime;
    private long lastMPTime;
    private long lastCheckPeriodTime;
    private long lastMoveItemTime;
    private long lastQuestTime;
    public long lastRecoveryTime;
    private int ddj;
    private int MobVac;
    private int MobVac2;
    private int mount_id;
    private final List<String> blockedPortals;
    private short chattype;
    private int 推广人;
    private int 推广值;
    private final int cz = 0;
    private long logintime;
    private long onlineTime;
    private MapleUnlimitSlots unlimitSlots;
    private long skillzq;
    private transient MaplePvpStats pvpStats;
    private int pvpDeaths;
    private int pvpKills;
    private int pvpVictory;
    private int qiandao;
    private long 防止复制时间;
    private int touzhuNum;
    private int touzhuType;
    private int touzhuNX;
    private Map<ReportType, Integer> reports;
    private boolean hiddenChatCanSee;
    private boolean testingdps;
    private long dps;
    private boolean switchHiredMerchant;
    private boolean 玩家私聊1;
    private boolean 玩家私聊2;
    private boolean 玩家私聊3;
    private boolean GMinfo;
    private boolean 聊天稱號;
    private boolean GM聊天;
    private String chattitle;
    
    public static void start() {
        WorldTimer.getInstance().register(() -> {}, 120000L);
    }
    
    public long get喇叭() {
        return this.skillzq;
    }
    
    public void gain喇叭() {
        ++this.skillzq;
    }
    
    public void set喇叭(final long id) {
        this.skillzq = id;
    }
    
    public MapleUnlimitSlots getUnlimitSlots() {
        if (this.unlimitSlots == null) {
            this.unlimitSlots = new MapleUnlimitSlots(this.id);
        }
        return this.unlimitSlots;
    }
    
    public int getDiseaseSize() {
        return this.diseases.size();
    }
    
    private MapleCharacter(final boolean ChannelServer) {
        this.guildrank = 5;
        this.allianceRank = 5;
        this.fairyExp = 30;
        this.guildid = 0;
        this.fallcounter = 0;
        this.rank = 1;
        this.rankMove = 0;
        this.jobRank = 1;
        this.jobRankMove = 0;
        this.marriageItemId = 0;
        this.linkMid = 0;
        this.coconutteam = 0;
        this.followid = 0;
        this.battleshipHP = 0;
        this.old = new Point(0, 0);
        this.hasSummon = false;
        this.remainingSp = new int[10];
        this.skills = new LinkedHashMap<ISkill, SkillEntry>();
        this.effects = new ConcurrentEnumMap<MapleBuffStat, MapleBuffStatValueHolder>(MapleBuffStat.class);
        this.coolDowns = new LinkedHashMap<Integer, MapleCoolDownValueHolder>();
        this.diseases = new ConcurrentEnumMap<MapleDisease, MapleDiseaseValueHolder>(MapleDisease.class);
        this.finishedAchievements = new ArrayList<Integer>();
        this.invincible = false;
        this.canTalk = true;
        this.clone = false;
        this.followinitiator = false;
        this.followon = false;
        this.skillMacros = new SkillMacro[5];
        this.nextConsume = 0L;
        this.pqStartTime = 0L;
        this.pyramidSubway = null;
        this.pendingExpiration = null;
        this.pendingSkills = null;
        this.movedMobs = new HashMap<Integer, Integer>();
        this.teleportname = "";
        this.lasttime = 0L;
        this.currenttime = 0L;
        this.deadtime = 1000L;
        this.apprentice = 0;
        this.master = 0;
        this.DebugMessage = false;
        this.ariantScore = 0;
        this.lastRecoveryTime = 0L;
        this.ddj = 0;
        this.MobVac = 0;
        this.MobVac2 = 0;
        this.mount_id = 0;
        this.blockedPortals = new ArrayList<String>();
        this.chattype = 0;
        this.qiandao = 0;
        this.防止复制时间 = 1000L;
        this.hiddenChatCanSee = false;
        this.testingdps = false;
        this.switchHiredMerchant = false;
        this.玩家私聊1 = false;
        this.玩家私聊2 = false;
        this.玩家私聊3 = false;
        this.GMinfo = false;
        this.聊天稱號 = false;
        this.GM聊天 = false;
        this.setStance(0);
        this.setPosition(new Point(0, 0));
        this.inventory = new MapleInventory[MapleInventoryType.values().length];
        for (final MapleInventoryType type : MapleInventoryType.values()) {
            this.inventory[type.ordinal()] = new MapleInventory(type, (byte)100);
        }
        this.quests = new LinkedHashMap<MapleQuest, MapleQuestStatus>();
        this.stats = new PlayerStats(this);
        for (int i = 0; i < this.remainingSp.length; ++i) {
            this.remainingSp[i] = 0;
        }
        if (ChannelServer) {
            this.lastMoveItemTime = 0L;
            this.lastCheckPeriodTime = 0L;
            this.lastQuestTime = 0L;
            this.lastHPTime = 0L;
            this.lastMPTime = 0L;
            this.lastCombo = 0L;
            this.mulung_energy = 0;
            this.combo = 0;
            this.scrolledPosition = 0;
            this.keydown_skill = 0L;
            this.smega = true;
            this.petStore = new byte[3];
            for (int i = 0; i < this.petStore.length; ++i) {
                this.petStore[i] = -1;
            }
            this.wishlist = new int[10];
            this.rocks = new int[10];
            this.regrocks = new int[5];
            this.clones = (WeakReference<MapleCharacter>[])new WeakReference[25];
            for (int i = 0; i < this.clones.length; ++i) {
                this.clones[i] = new WeakReference<MapleCharacter>(null);
            }
            (this.inst = new AtomicInteger()).set(0);
            this.keylayout = new MapleKeyLayout();
            this.doors = new ArrayList<MapleDoor>();
            this.controlled = new LinkedHashSet<MapleMonster>();
            this.summons = new LinkedHashMap<Integer, MapleSummon>();
            this.visibleMapObjects = new LinkedHashSet<MapleMapObject>();
            this.visibleMapObjectsLock = new ReentrantReadWriteLock();
            this.pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();
            this.savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; ++i) {
                this.savedLocations[i] = -1;
            }
            this.questinfo = new LinkedHashMap<Integer, String>();
            this.anticheat = new CheatTracker(this);
            this.pets = new ArrayList<MaplePet>();
        }
    }
    
    public static MapleCharacter getDefault(final MapleClient client, final int type) {
        final MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        ret.gmLevel = 0;
        ret.job = (short)((type == 4) ? 3000 : ((type == 3) ? 2001 : ((type == 0) ? 1000 : ((type == 1) ? 0 : 2000))));
        ret.beans = 0;
        ret.meso = 0;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte)20);
        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 50;
        ret.stats.mp = 50;
        ret.prefix = 0;
        ret.推广人 = 0;
        ret.推广值 = 0;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret.client.setAccountName(rs.getString("name"));
                ret.points = rs.getInt("points");
                ret.vpoints = rs.getInt("vpoints");
                ret.lastGainHM = rs.getLong("lastGainHM");
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error getting character default" + e);
        }
        return ret;
    }
    
    public short getScrolledPosition() {
        return this.scrolledPosition;
    }
    
    public void setScrolledPosition(final short s) {
        this.scrolledPosition = s;
    }
    
    public static final MapleCharacter ReconstructChr(final CharacterTransfer ct, final MapleClient client, final boolean isChannel) {
        final MapleCharacter ret = new MapleCharacter(true);
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.mount_id = ct.mount_id;
        ret.DebugMessage = ct.DebugMessage;
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;
        ret.CRand = new PlayerRandomStream();
        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;
        ret.chalktext = ct.chalkboard;
        ret.exp = ct.exp;
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingAp = ct.remainingAp;
        ret.beans = ct.beans;
        ret.meso = ct.meso;
        ret.gmLevel = ct.gmLevel;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.accountid = ct.accountid;
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.bookCover = ct.mBookCover;
        ret.dojo = ct.dojo;
        ret.dojoRecord = ct.dojoRecord;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.allianceRank = ct.alliancerank;
        ret.points = ct.points;
        ret.vpoints = ct.vpoints;
        ret.fairyExp = ct.fairyExp;
        ret.marriageId = ct.marriageId;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.charmessage = ct.charmessage;
        ret.expression = ct.expression;
        ret.constellation = ct.constellation;
        ret.blood = ct.blood;
        ret.month = ct.month;
        ret.day = ct.day;
        ret.vip = ct.vip;
        ret.vipczz = ct.vipczz;
        ret.viptime = ct.viptime;
        ret.ddj = ct.ddj;
        ret.skillzq = ct.skillzq;
        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;
        ret.prefix = ct.prefix;
        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) {
                ret.map = mapFactory.getMap(100000000);
            }
            else if (ret.map.getForcedReturnId() != 999999999) {
                ret.map = ret.map.getForcedReturnMap();
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0);
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());
            final int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = Messenger.getMessenger(messengerid);
            }
        }
        else {
            ret.messenger = null;
        }
        final int partyid = ct.partyid;
        if (partyid >= 0) {
            final MapleParty party = Party.getParty(partyid);
            if (party != null && party.getMemberById(ret.id) != null) {
                ret.party = party;
            }
        }
        for (final Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            final MapleQuest quest = MapleQuest.getInstance(qs.getKey());
            final MapleQuestStatus queststatus_from = (MapleQuestStatus) qs.getValue();
            final MapleQuestStatus queststatus = new MapleQuestStatus(quest, queststatus_from.getStatus());
            queststatus.setForfeited(queststatus_from.getForfeited());
            queststatus.setCustomData(queststatus_from.getCustomData());
            queststatus.setCompletionTime(queststatus_from.getCompletionTime());
            if (queststatus_from.getMobKills() != null) {
                for (final Entry<Integer, Integer> mobkills : queststatus_from.getMobKills().entrySet()) {
                    queststatus.setMobKills(mobkills.getKey(), mobkills.getValue());
                }
            }
            ret.quests.put(quest, queststatus);
        }
        for (final Entry<Integer, SkillEntry> qs2 : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs2.getKey()), qs2.getValue());
        }
        for (final Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        ret.monsterbook = new MonsterBook(ct.mbook);
        ret.inventory = (MapleInventory[])ct.inventorys;
        ret.BlessOfFairy_Origin = ct.BlessOfFairy;
        ret.skillMacros = (SkillMacro[])ct.skillmacro;
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.buddylist.loadFromTransfer(ct.buddies);
        ret.keydown_skill = 0L;
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.storage = (MapleStorage)ct.storage;
        ret.cs = (CashShop)ct.cs;
        client.setAccountName(ct.accountname);
        ret.lastGainHM = ct.lastGainHM;
        ret.numClones = ct.clonez;
        ret.mount = new MapleMount(ret, ct.mount_itemid, GameConstants.isEvan(ret.job) ? 20011004 : (GameConstants.isAran(ret.job) ? 20001004 : (GameConstants.isKOC(ret.job) ? 10001004 : 1004)), ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        ret.stats.recalcLocalStats(true);
        return ret;
    }
    
    public static MapleCharacter loadCharFromDB(final int charid, final MapleClient client, final boolean channelserver) {
        final MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;
        ret.unlimitSlots = new MapleUnlimitSlots(charid);
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }
            ret.推广人 = rs.getInt("推广人");
            ret.推广值 = rs.getInt("推广值");
            ret.mount_id = rs.getInt("mountid");
            ret.vip = rs.getInt("vip");
            ret.vipczz = rs.getInt("vipczz");
            final Timestamp expiration = rs.getTimestamp("viptime");
            ret.viptime = ((expiration == null) ? null : expiration);
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            ret.fame = rs.getShort("fame");
            ret.stats.str = rs.getShort("str");
            ret.stats.dex = rs.getShort("dex");
            ret.stats.int_ = rs.getShort("int");
            ret.stats.luk = rs.getShort("luk");
            ret.stats.maxhp = rs.getShort("maxhp");
            ret.stats.maxmp = rs.getShort("maxmp");
            ret.stats.hp = rs.getShort("hp");
            ret.stats.mp = rs.getShort("mp");
            ret.exp = rs.getInt("exp");
            ret.hpApUsed = rs.getShort("hpApUsed");
            final String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < ret.remainingSp.length; ++i) {
                ret.remainingSp[i] = Integer.parseInt(sp[i]);
            }
            ret.remainingAp = rs.getShort("ap");
            ret.beans = rs.getInt("beans");
            ret.meso = rs.getInt("meso");
            ret.gmLevel = rs.getByte("gm");
            ret.skinColor = rs.getByte("skincolor");
            ret.gender = rs.getByte("gender");
            ret.job = rs.getShort("job");
            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.accountid = rs.getInt("accountid");
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getByte("spawnpoint");
            ret.world = rs.getByte("world");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.currentrep = rs.getInt("currentrep");
            ret.totalrep = rs.getInt("totalrep");
            ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
            ret.subcategory = rs.getByte("subcategory");
            ret.mount = new MapleMount(ret, 0, (ret.job >= 2000) ? 20001004 : ((ret.job >= 3000) ? 30001004 : ((ret.job == 2001 || (ret.job >= 2200 && ret.job <= 2218)) ? 20011004 : ((ret.job > 1000 && ret.job < 2000) ? 10001004 : 1004))), (byte)0, (byte)1, 0);
            ret.rank = rs.getInt("rank");
            ret.rankMove = rs.getInt("rankMove");
            ret.jobRank = rs.getInt("jobRank");
            ret.jobRankMove = rs.getInt("jobRankMove");
            ret.marriageId = rs.getInt("marriageId");
            ret.charmessage = rs.getString("charmessage");
            ret.expression = rs.getInt("expression");
            ret.constellation = rs.getInt("constellation");
            ret.blood = rs.getInt("blood");
            ret.month = rs.getInt("month");
            ret.day = rs.getInt("day");
            ret.prefix = rs.getInt("prefix");
            ret.ddj = rs.getInt("ddj");
            ret.skillzq = rs.getLong("skillzq");
            if (channelserver) {
                final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
                ret.map = mapFactory.getMap(ret.mapid);
                if (ret.map == null) {
                    ret.map = mapFactory.getMap(100000000);
                }
                MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                if (portal == null) {
                    portal = ret.map.getPortal(0);
                    ret.initialSpawnPoint = 0;
                }
                ret.setPosition(portal.getPosition());
                final int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    final MapleParty party = Party.getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }
                ret.bookCover = rs.getInt("monsterbookcover");
                ret.dojo = rs.getInt("dojo_pts");
                ret.dojoRecord = rs.getByte("dojoRecord");
                final String[] pets = rs.getString("pets").split(",");
                for (int j = 0; j < ret.petStore.length; ++j) {
                    ret.petStore[j] = Byte.parseByte(pets[j]);
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.finishedAchievements.add(rs.getInt("achievementid"));
                }
            }
            rs.close();
            ps.close();
            boolean compensate_previousEvans = false;
            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");
            while (rs.next()) {
                final int id = rs.getInt("quest");
                if (id == 170000) {
                    compensate_previousEvans = true;
                }
                final MapleQuest q = MapleQuest.getInstance(id);
                final MapleQuestStatus status = new MapleQuestStatus(q, rs.getByte("status"));
                final long cTime = rs.getLong("time");
                if (cTime > -1L) {
                    status.setCompletionTime(cTime * 1000L);
                }
                status.setForfeited(rs.getInt("forfeited"));
                status.setCustomData(rs.getString("customData"));
                ret.quests.put(q, status);
                pse.setInt(1, rs.getInt("queststatusid"));
                final ResultSet rsMobs = pse.executeQuery();
                while (rsMobs.next()) {
                    status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                }
                rsMobs.close();
            }
            rs.close();
            ps.close();
            pse.close();
            if (channelserver) {
                ret.CRand = new PlayerRandomStream();
                ret.monsterbook = MonsterBook.loadCards(charid);
                ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]*********************");
                }
                ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                ps.close();
                rs.close();
                for (final Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(false, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                    if (mit.getLeft().getPet() != null) {
                        ret.pets.add(mit.getLeft().getPet());
                    }
                }
                ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.getClient().setAccountName(rs.getString("name"));
                    ret.lastGainHM = rs.getLong("lastGainHM");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");
                    if (rs.getTimestamp("lastlogon") != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?");
                    ps.setInt(1, ret.accountid);
                    ps.executeUpdate();
                }
                else {
                    rs.close();
                }
                ps.close();
                ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration FROM skills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    final ISkill skil = SkillFactory.getSkill(rs.getInt("skillid"));
                    if (skil != null && GameConstants.isApplicableSkill(rs.getInt("skillid"))) {
                        ret.skills.put(skil, new SkillEntry(rs.getByte("skilllevel"), rs.getByte("masterlevel"), rs.getLong("expiration")));
                    }
                    else {
                        if (skil != null) {
                            continue;
                        }
                        final int[] remainingSp = ret.remainingSp;
                        final int skillBookForSkill = GameConstants.getSkillBookForSkill(rs.getInt("skillid"));
                        remainingSp[skillBookForSkill] += rs.getByte("skilllevel");
                    }
                }
                rs.close();
                ps.close();
                ret.expirationTask(false);
                ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY level DESC");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                byte maxlevel_ = 0;
                while (rs.next()) {
                    if (rs.getInt("id") != charid) {
                        byte maxlevel = (byte)(rs.getShort("level") / 10);
                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if (maxlevel <= maxlevel_) {
                            continue;
                        }
                        maxlevel_ = maxlevel;
                        ret.BlessOfFairy_Origin = rs.getString("name");
                    }
                    else {
                        if (charid >= 17000 || compensate_previousEvans || ret.job < 2200 || ret.job > 2218) {
                            continue;
                        }
                        for (int k = 0; k <= GameConstants.getSkillBook(ret.job); ++k) {
                            final int[] remainingSp2 = ret.remainingSp;
                            final int n = k;
                            remainingSp2[n] += 2;
                        }
                        ret.setQuestAdd(MapleQuest.getInstance(170000), (byte)0, null);
                    }
                }
                ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte)0, -1L));
                ps.close();
                rs.close();
                ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    final int position = rs.getInt("position");
                    final SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                    ret.skillMacros[position] = macro;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                final Map keyb = ret.keylayout.Layout();
                while (rs.next()) {
                    keyb.put(rs.getInt("key"), new Pair<Byte, Integer>(rs.getByte("type"), rs.getInt("action")));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ret.lastfametime = 0L;
                ret.lastmonthfameids = new ArrayList<Integer>(31);
                while (rs.next()) {
                    ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                    ret.lastmonthfameids.add(rs.getInt("characterid_to"));
                }
                rs.close();
                ps.close();
                ret.buddylist.loadFromDb(charid);
                ret.storage = MapleStorage.loadStorage(ret.accountid);
                ret.cs = new CashShop(ret.accountid, charid, ret.getJob());
                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int l = 0;
                while (rs.next()) {
                    ret.wishlist[l] = rs.getInt("sn");
                    ++l;
                }
                while (l < 10) {
                    ret.wishlist[l] = 0;
                    ++l;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                while (rs.next()) {
                    ret.rocks[r] = rs.getInt("mapid");
                    ++r;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    ++r;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT mapid FROM regrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.regrocks[r] = rs.getInt("mapid");
                    ++r;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999999999;
                    ++r;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("No mount data found on SQL column");
                }
                final Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18));
                ret.mount = new MapleMount(ret, (mount != null) ? mount.getItemId() : 0, (ret.job >= 2000) ? 20001004 : ((ret.job >= 3000) ? 30001004 : ((ret.job == 2001 || ret.job >= 2200) ? 20011004 : ((ret.job > 1000 && ret.job < 2000) ? 10001004 : 1004))), rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                ps.close();
                rs.close();
                ret.stats.recalcLocalStats(true);
            }
            else {
                for (final Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(true, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                }
            }
        }
        catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("加载角色数据信息出错...");
            FileoutputUtil.outputFileError("log\\Packet_Except.log", ess);
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException ex) {}
        }
        return ret;
    }
    
    public static void saveNewCharToDB(final MapleCharacter chr, final int type, final boolean db) {
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(1);
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters (level, fame, str, dex, luk, `int`, exp, hp, mp, maxhp, maxmp, sp, ap, gm, skincolor, gender, job, hair, face, map, meso, hpApUsed, spawnpoint, party, buddyCapacity, monsterbookcover, dojo_pts, dojoRecord, pets, subcategory, marriageId, currentrep, totalrep, prefix, accountid, name, world, mountid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            int index = 0;
            ps.setInt(++index, 1);
            ps.setShort(++index, (short)0);
            final PlayerStats stat = chr.stats;
            ps.setShort(++index, stat.getStr());
            ps.setShort(++index, stat.getDex());
            ps.setShort(++index, stat.getInt());
            ps.setShort(++index, stat.getLuk());
            ps.setInt(++index, 0);
            ps.setShort(++index, stat.getHp());
            ps.setShort(++index, stat.getMp());
            ps.setShort(++index, stat.getMaxHp());
            ps.setShort(++index, stat.getMaxMp());
            ps.setString(++index, "0,0,0,0,0,0,0,0,0,0");
            ps.setShort(++index, (short)0);
            ps.setInt(++index, chr.getClient().gm ? 5 : 0);
            ps.setByte(++index, chr.skinColor);
            ps.setByte(++index, chr.gender);
            ps.setShort(++index, chr.job);
            ps.setInt(++index, chr.hair);
            ps.setInt(++index, chr.face);
            ps.setInt(++index, ServerConstants.出生地图);
            ps.setInt(++index, chr.meso);
            ps.setShort(++index, (short)0);
            ps.setByte(++index, (byte)0);
            ps.setInt(++index, -1);
            ps.setByte(++index, chr.buddylist.getCapacity());
            ps.setInt(++index, 0);
            ps.setInt(++index, 0);
            ps.setInt(++index, 0);
            ps.setString(++index, "-1,-1,-1");
            ps.setInt(++index, 0);
            ps.setInt(++index, 0);
            ps.setInt(++index, 0);
            ps.setInt(++index, 0);
            ps.setInt(++index, chr.prefix);
            ps.setInt(++index, chr.getAccountID());
            ps.setString(++index, chr.name);
            ps.setByte(++index, chr.world);
            ps.setInt(++index, chr.mount_id);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new DatabaseException("Inserting char failed.");
            }
            chr.id = rs.getInt(1);
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int)(q.getCompletionTime() / 1000L));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                if (q.hasMobKills()) {
                    for (final int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.executeUpdate();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte)32);
            ps.setByte(3, (byte)32);
            ps.setByte(4, (byte)32);
            ps.setByte(5, (byte)32);
            ps.setByte(6, (byte)60);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte)1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte)0);
            ps.execute();
            ps.close();
            final List<Pair<Item, MapleInventoryType>> listing = new ArrayList<Pair<Item, MapleInventoryType>>();
            for (final MapleInventory iv : chr.inventory) {
                for (final Item item : iv.list()) {
                    listing.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
                }
            }
            ItemLoader.INVENTORY.saveItems(listing, con, chr.id);
            final int[] array1 = { 2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 23, 25, 26, 27, 29, 31, 34, 35, 37, 38, 40, 41, 43, 44, 45, 46, 48, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65 };
            final int[] array2 = { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6 };
            final int[] array3 = { 10, 12, 13, 18, 24, 21, 8, 5, 0, 4, 1, 19, 14, 15, 52, 2, 17, 11, 3, 20, 16, 23, 9, 50, 51, 6, 22, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106 };
            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; ++i) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();
            con.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            }
            catch (SQLException ex) {
                e.printStackTrace();
                FileoutputUtil.outputFileError("log\\Packet_Except.log", ex);
                System.err.println("[charsave] Error Rolling Back");
            }
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            }
            catch (SQLException e2) {
                e2.printStackTrace();
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e2);
                System.err.println("[charsave] Error going back to autocommit mode");
            }
        }
        finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            }
            catch (SQLException e3) {
                e3.printStackTrace();
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e3);
                System.err.println("[charsave] Error going back to autocommit mode");
            }
        }
    }
    
    public void saveToDB(final boolean dc, final boolean fromcs) {
        if (this.isClone()) {
            return;
        }
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(1);
            con.setAutoCommit(false);
            ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, monsterbookcover = ?, dojo_pts = ?, dojoRecord = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, charmessage = ?, expression = ?, constellation = ?, blood = ?, month = ?, day = ?, beans = ?, prefix = ?, name = ?, mountid = ?, vip = ?, vipczz=?, viptime = ?, ddj = ?, skillzq = ?, 推广人 = ?, 推广值 = ?  WHERE id = ?");
            int index = 0;
            ps.setInt(++index, this.level);
            ps.setShort(++index, this.fame);
            ps.setShort(++index, this.stats.getStr());
            ps.setShort(++index, this.stats.getDex());
            ps.setShort(++index, this.stats.getLuk());
            ps.setShort(++index, this.stats.getInt());
            ps.setInt(++index, this.exp);
            ps.setShort(++index, (short)((this.stats.getHp() < 1) ? 50 : this.stats.getHp()));
            ps.setShort(++index, this.stats.getMp());
            ps.setShort(++index, this.stats.getMaxHp());
            ps.setShort(++index, this.stats.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < this.remainingSp.length; ++i) {
                sps.append(this.remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(++index, sp.substring(0, sp.length() - 1));
            ps.setShort(++index, this.remainingAp);
            ps.setByte(++index, this.gmLevel);
            ps.setByte(++index, this.skinColor);
            ps.setByte(++index, this.gender);
            ps.setShort(++index, this.job);
            ps.setInt(++index, this.hair);
            ps.setInt(++index, this.face);
            if (!fromcs && this.map != null) {
                if (this.map.getForcedReturnId() != 999999999) {
                    ps.setInt(++index, this.map.getForcedReturnId());
                }
                else {
                    ps.setInt(++index, (this.stats.getHp() < 1) ? this.map.getReturnMapId() : this.map.getId());
                }
            }
            else {
                ps.setInt(++index, this.mapid);
            }
            ps.setInt(++index, this.meso);
            ps.setShort(++index, this.hpApUsed);
            if (this.map == null) {
                ps.setByte(++index, (byte)0);
            }
            else {
                final MaplePortal closest = this.map.findClosestSpawnpoint(this.getPosition());
                ps.setByte(++index, (byte)((closest != null) ? closest.getId() : 0));
            }
            ps.setInt(++index, (this.party != null) ? this.party.getId() : -1);
            ps.setShort(++index, this.buddylist.getCapacity());
            ps.setInt(++index, this.bookCover);
            ps.setInt(++index, this.dojo);
            ps.setInt(++index, this.dojoRecord);
            final StringBuilder petz = new StringBuilder();
            int petLength = 0;
            for (final MaplePet pet : this.pets) {
                pet.saveToDb();
                if (pet.getSummoned()) {
                    petz.append(pet.getInventoryPosition());
                    petz.append(",");
                    ++petLength;
                }
            }
            while (petLength < 3) {
                petz.append("-1,");
                ++petLength;
            }
            final String petstring = petz.toString();
            ps.setString(++index, petstring.substring(0, petstring.length() - 1));
            ps.setByte(++index, this.subcategory);
            ps.setInt(++index, this.marriageId);
            ps.setInt(++index, this.currentrep);
            ps.setInt(++index, this.totalrep);
            ps.setString(++index, this.charmessage);
            ps.setInt(++index, this.expression);
            ps.setInt(++index, this.constellation);
            ps.setInt(++index, this.blood);
            ps.setInt(++index, this.month);
            ps.setInt(++index, this.day);
            ps.setInt(++index, this.beans);
            ps.setInt(++index, this.prefix);
            ps.setString(++index, this.name);
            ps.setInt(++index, this.mount_id);
            ps.setInt(++index, this.vip);
            ps.setInt(++index, this.vipczz);
            ps.setTimestamp(++index, (this.getViptime() == null) ? null : this.getViptime());
            ps.setInt(++index, this.ddj);
            ps.setLong(++index, this.skillzq);
            ps.setInt(++index, this.推广人);
            ps.setInt(++index, this.推广值);
            ps.setInt(++index, this.id);
            if (ps.executeUpdate() < 1) {
                ps.close();
                throw new DatabaseException("Character not in database (" + this.id + ")");
            }
            ps.close();
            this.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
            for (int j = 0; j < 5; ++j) {
                final SkillMacro macro = this.skillMacros[j];
                if (macro != null) {
                    ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, this.id);
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getShout());
                    ps.setInt(7, j);
                    ps.execute();
                    ps.close();
                }
            }
            this.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, this.id);
            ps.setInt(2, this.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setInt(3, this.getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setInt(4, this.getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setInt(5, this.getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setInt(6, this.getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.execute();
            ps.close();
            this.saveInventory(con);
            this.deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)");
            ps.setInt(1, this.id);
            for (final Entry<Integer, String> q : this.questinfo.entrySet()) {
                ps.setInt(2, q.getKey());
                ps.setString(3, q.getValue());
                ps.execute();
            }
            ps.close();
            this.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, this.id);
            for (final MapleQuestStatus q2 : this.quests.values()) {
                ps.setInt(2, q2.getQuest().getId());
                ps.setInt(3, q2.getStatus());
                ps.setInt(4, (int)(q2.getCompletionTime() / 1000L));
                ps.setInt(5, q2.getForfeited());
                ps.setString(6, q2.getCustomData());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                if (q2.hasMobKills()) {
                    for (final int mob : q2.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q2.getMobKills(mob));
                        pse.executeUpdate();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();
            this.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, this.id);
            for (final Entry<ISkill, SkillEntry> skill : this.skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) {
                    ps.setInt(2, skill.getKey().getId());
                    ps.setByte(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.execute();
                }
            }
            ps.close();
            final List<MapleCoolDownValueHolder> cd = this.getCooldowns();
            if (dc && cd.size() > 0) {
                ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                ps.setInt(1, this.getId());
                for (final MapleCoolDownValueHolder cooling : cd) {
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.execute();
                }
                ps.close();
            }
            this.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
            ps.setInt(1, this.id);
            for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (this.savedLocations[savedLocationType.getValue()] != -1) {
                    ps.setInt(2, savedLocationType.getValue());
                    ps.setInt(3, this.savedLocations[savedLocationType.getValue()]);
                    ps.execute();
                }
            }
            ps.close();
            ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?");
            ps.setInt(1, this.accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)");
            for (final Integer achid : this.finishedAchievements) {
                ps.setInt(1, this.id);
                ps.setInt(2, achid);
                ps.setInt(3, this.accountid);
                ps.executeUpdate();
            }
            ps.close();
            this.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, ?)");
            ps.setInt(1, this.id);
            for (final BuddyEntry entry : this.buddylist.getBuddies()) {
                if (entry != null) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setInt(3, entry.isVisible() ? 0 : 1);
                    ps.execute();
                }
            }
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET `points` = ?, `vpoints` = ? WHERE id = ?");
            ps.setInt(1, this.points);
            ps.setInt(2, this.vpoints);
            ps.setInt(3, this.client.getAccID());
            ps.execute();
            ps.close();
            if (this.storage != null) {
                this.storage.saveToDB();
            }
            ps = con.prepareStatement("UPDATE accounts SET `lastGainHM` = ? WHERE id = ?");
            ps.setLong(1, this.lastGainHM);
            ps.setInt(2, this.client.getAccID());
            ps.execute();
            ps.close();
            if (this.cs != null) {
                this.cs.save();
            }
            PlayerNPC.updateByCharId(this);
            this.keylayout.saveKeys(this.id);
            this.mount.saveMount(this.id);
            this.monsterbook.saveCards(this.id);
            this.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
            for (int k = 0; k < this.getWishlistSize(); ++k) {
                ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                ps.setInt(1, this.getId());
                ps.setInt(2, this.wishlist[k]);
                ps.execute();
                ps.close();
            }
            this.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
            for (int k = 0; k < this.rocks.length; ++k) {
                if (this.rocks[k] != 999999999) {
                    ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid) VALUES(?, ?) ");
                    ps.setInt(1, this.getId());
                    ps.setInt(2, this.rocks[k]);
                    ps.execute();
                    ps.close();
                }
            }
            this.deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?");
            for (int k = 0; k < this.regrocks.length; ++k) {
                if (this.regrocks[k] != 999999999) {
                    ps = con.prepareStatement("INSERT INTO regrocklocations(characterid, mapid) VALUES(?, ?) ");
                    ps.setInt(1, this.getId());
                    ps.setInt(2, this.regrocks[k]);
                    ps.execute();
                    ps.close();
                }
            }
            con.commit();
        }
        catch (SQLException ex2) {}
        catch (DatabaseException ex3) {}
        catch (UnsupportedOperationException e) {
            FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", e);
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex) {
                FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", ex);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            }
            catch (SQLException e2) {
                FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", e2);
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            }
            catch (SQLException e3) {
                FileoutputUtil.outputFileError("日志/Logs/Log_Packet_封包异常.rtf", e3);
            }
        }
    }
    
    private void deleteWhereCharacterId(final Connection con, final String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, this.id);
    }
    
    public static void deleteWhereCharacterId(final Connection con, final String sql, final int id) throws SQLException {
        final PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
    
    public void saveInventory(final Connection con) throws SQLException {
        final List<Pair<Item, MapleInventoryType>> listing = new ArrayList<Pair<Item, MapleInventoryType>>();
        for (final MapleInventory iv : this.inventory) {
            for (final Item item : iv.list()) {
                listing.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
            }
        }
        if (con != null) {
            ItemLoader.INVENTORY.saveItems(listing, con, this.id);
        }
        else {
            ItemLoader.INVENTORY.saveItems(listing, this.id);
        }
    }
    
    public final PlayerStats getStat() {
        return this.stats;
    }
    
    public final PlayerRandomStream CRand() {
        return this.CRand;
    }
    
    public final void QuestInfoPacket(final MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.questinfo.size());
        for (final Entry<Integer, String> q : this.questinfo.entrySet()) {
            mplew.writeShort(q.getKey());
            mplew.writeMapleAsciiString((q.getValue() == null) ? "" : q.getValue());
        }
    }
    
    public final void updateInfoQuest(final int questid, final String data) {
        this.questinfo.put(questid, data);
        this.client.getSession().write(MaplePacketCreator.updateInfoQuest(questid, data));
    }
    
    public final String getInfoQuest(final int questid) {
        if (this.questinfo.containsKey(questid)) {
            return this.questinfo.get(questid);
        }
        return "";
    }
    
    public final int getNumQuest() {
        int i = 0;
        for (final MapleQuestStatus q : this.quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom()) {
                ++i;
            }
        }
        return i;
    }
    
    public final byte getQuestStatus(final int quest) {
        return this.getQuest(MapleQuest.getInstance(quest)).getStatus();
    }
    
    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!this.quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte)0);
        }
        return this.quests.get(quest);
    }
    
    public void setQuestAdd(final int quest) {
        this.setQuestAddZ(MapleQuest.getInstance(quest), (byte)2, null);
    }
    
    public final void setQuestAddZ(final MapleQuest quest, final byte status, final String customData) {
        final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
        stat.setCustomData(customData);
        this.quests.put(quest, stat);
    }
    
    public final void setQuestAdd(final MapleQuest quest, final byte status, final String customData) {
        if (!this.quests.containsKey(quest)) {
            final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            this.quests.put(quest, stat);
        }
    }
    
    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!this.quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, (byte)0);
            this.quests.put(quest, status);
            return status;
        }
        return this.quests.get(quest);
    }
    
    public MapleQuestStatus getQuestRemove(final MapleQuest quest) {
        return this.quests.remove(quest);
    }
    
    public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
        return this.quests.get(quest);
    }
    
    public final void updateQuest(final MapleQuestStatus quest) {
        this.updateQuest(quest, false);
    }
    
    public final void updateQuest(final MapleQuestStatus quest, final boolean update) {
        this.quests.put(quest.getQuest(), quest);
        if (!quest.isCustom()) {
            this.client.getSession().write(MaplePacketCreator.updateQuest(quest));
            if (quest.getStatus() == 1 && !update) {
                this.client.getSession().write(MaplePacketCreator.updateQuestInfo(this, quest.getQuest().getId(), quest.getNpc(), (byte)8));
            }
        }
    }
    
    public final Map<Integer, String> getInfoQuest_Map() {
        return this.questinfo;
    }
    
    public final Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return this.quests;
    }
    
    public boolean isActiveBuffedValue(final int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                return true;
            }
        }
        return false;
    }
    
    public Integer getBuffedValue(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        return (mbsvh == null) ? null : Integer.valueOf(mbsvh.value);
    }
    
    public final Integer getBuffedSkill_X(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getX();
    }
    
    public final Integer getBuffedSkill_Y(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getY();
    }
    
    public boolean isBuffFrom(final MapleBuffStat stat, final ISkill skill) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(stat);
        return mbsvh != null && mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }
    
    public int getBuffSource(final MapleBuffStat stat) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(stat);
        return (mbsvh == null) ? -1 : mbsvh.effect.getSourceId();
    }
    
    public int getItemQuantity(final int itemid, final boolean checkEquipped) {
        int possesed = this.inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }
    
    public void setBuffedValue(final MapleBuffStat effect, final int value) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }
    
    public Long getBuffedStarttime(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        return (mbsvh == null) ? null : Long.valueOf(mbsvh.startTime);
    }
    
    public MapleStatEffect getStatForBuff(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = (client.MapleBuffStatValueHolder)(client.MapleBuffStatValueHolder)this.effects.get(effect);
        return (mbsvh == null) ? null : mbsvh.effect;
    }
    
    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (this.dragonBloodSchedule != null) {
            this.dragonBloodSchedule.cancel(false);
        }
        this.dragonBloodSchedule = BuffTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (MapleCharacter.this.getHp() > 100) {
                    MapleCharacter.this.addHP(-bloodEffect.getX());
                }
                MapleCharacter.this.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 5));
                MapleCharacter.this.getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(MapleCharacter.this.getId(), bloodEffect.getSourceId(), 5, (byte)3), false);
            }
        }, 4000L, 4000L);
    }
    
    public void startMapTimeLimitTask(int time, final MapleMap to) {
        this.client.getSession().write(MaplePacketCreator.getClock(time));
        time *= 1000;
        this.mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                MapleCharacter.this.changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }
    
    public void startFishingTask(final boolean VIP) {
        final int time = GameConstants.getFishingTime(VIP, this.isGM());
        this.cancelFishingTask();
        this.fishing = EtcTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                final boolean expMulti = MapleCharacter.this.haveItem(2300001, 1, false, true);
                if ((!expMulti && !MapleCharacter.this.haveItem(2300000, 1, false, true)) || !GameConstants.isFishingMap(MapleCharacter.this.getMapId())) {
                    MapleCharacter.this.cancelFishingTask();
                    return;
                }
                MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, expMulti ? 2300001 : 2300000, 1, false, false);
                boolean passed = false;
                while (!passed) {
                    final FishingConstants Fishing = new FishingConstants();
                    final String[] FishingItem = Fishing.getFishingItem();
                    final String[] FishingItemS = Fishing.getFishingItemS();
                    final int FishingItemSL = Fishing.getFishingItemSL();
                    final int FishingItemSLS = Fishing.getFishingItemSLS();
                    final int FishingMeso = Fishing.getFishingMeso();
                    final int FishingMesoS = Fishing.getFishingMesoS();
                    final int FishingExp = Fishing.getFishingExp();
                    final int FishingExpS = Fishing.getFishingExpS();
                    final int FishingItemSJ = Fishing.getFishingItemSJ();
                    int randval = 0;
                    final int x = 0;
                    final int 随机 = Randomizer.nextInt(FishingItemSL);
                    final int 随机S = Randomizer.nextInt(FishingItemSLS);
                    final int A = Randomizer.nextInt(FishingItemSJ);
                    final int B = Randomizer.nextInt(FishingItemSJ);
                    if (A == B && expMulti) {
                        for (int i = 0; i < FishingItemS.length; ++i) {
                            if (随机S == i) {
                                randval = Integer.parseInt(FishingItemS[i]);
                                break;
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < FishingItem.length; ++i) {
                            if (随机 == i) {
                                randval = Integer.parseInt(FishingItem[i]);
                                break;
                            }
                        }
                    }
                    switch (randval) {
                        case 0: {
                            final int money = Randomizer.nextInt(expMulti ? FishingMesoS : FishingMeso) + 1;
                            MapleCharacter.this.gainMeso(money, true);
                            client.sendPacket(UIPacket.fishingUpdate((byte)1, money));
                            passed = true;
                            continue;
                        }
                        case 1: {
                            int experi = Randomizer.nextInt(expMulti ? FishingExpS : FishingExp) + 1;
                            if (experi < 1) {
                                experi = 1;
                            }
                            MapleCharacter.this.gainExp(expMulti ? (experi * 3 / 2) : experi, true, false, true);
                            client.sendPacket(UIPacket.fishingUpdate((byte)2, experi));
                            passed = true;
                            continue;
                        }
                        default: {
                            if (Randomizer.nextInt(100) > 95) {
                                MapleInventoryManipulator.addById(client, 4001200, (short)1, (byte)0);
                                client.sendPacket(UIPacket.fishingUpdate((byte)0, randval));
                                continue;
                            }
                            MapleInventoryManipulator.addById(client, randval, (short)1, (byte)0);
                            client.sendPacket(UIPacket.fishingUpdate((byte)0, randval));
                            MapleCharacter.this.dropTopMsg("您从钓鱼中获得物品," + MapleItemInformationProvider.getInstance().getName(randval) + "");
                            passed = true;
                            continue;
                        }
                    }
                }
                map.broadcastMessage(UIPacket.fishingCaught(id));
            }
        }, time, time);
    }
    
    public void cancelMapTimeLimitTask() {
        if (this.mapTimeLimitTask != null) {
            this.mapTimeLimitTask.cancel(false);
        }
    }
    
    public void cancelFishingTask() {
        if (this.fishing != null) {
            this.fishing.cancel(false);
        }
    }
    
    public void registerEffect(final MapleStatEffect effect, final long starttime, final ScheduledFuture<?> schedule) {
        this.registerEffect(effect, starttime, schedule, effect.getStatups());
    }
    
    public void registerEffect(final MapleStatEffect effect, final long starttime, final ScheduledFuture<?> schedule, final List<Pair<MapleBuffStat, Integer>> statups) {
        if (effect.isHide()) {
            this.hidden = true;
            this.map.broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(this.getId(), this), false);
        }
        else if (effect.isDragonBlood()) {
            this.prepareDragonBlood(effect);
        }
        else if (effect.isBerserk()) {
            this.checkBerserk();
        }
        else if (effect.isMonsterRiding_()) {
            this.getMount().startSchedule();
        }
        else if (effect.isBeholder()) {
            if (this.beholderHealingSchedule != null) {
                this.beholderHealingSchedule.cancel(false);
            }
            if (this.beholderBuffSchedule != null) {
                this.beholderBuffSchedule.cancel(false);
            }
            final ISkill bHealing = SkillFactory.getSkill(1320008);
            final int bHealingLvl = this.getSkillLevel(bHealing);
            final ISkill bBuff = SkillFactory.getSkill(1320009);
            if (this.getSkillLevel(bBuff) > 0) {
                final MapleStatEffect buffEffect = bBuff.getEffect(this.getSkillLevel(bBuff));
                int buffInterval = buffEffect.getX() * 1000;
                if (bHealingLvl > 0) {
                    final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
                    buffInterval = healEffect.getX() * 1000;
                    this.beholderBuffSchedule = BuffTimer.getInstance().register(new Runnable() {
                        @Override
                        public void run() {
                            MapleCharacter.this.addHP(healEffect.getHp());
                            MapleCharacter.this.checkBerserk();
                            buffEffect.applyTo(MapleCharacter.this);
                            client.sendPacket(MaplePacketCreator.showOwnBuffEffect(1321007, 2));
                            map.broadcastMessage(MaplePacketCreator.summonSkill(MapleCharacter.this.getId(), 1321007, Randomizer.nextInt(3) + 6));
                            map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(MapleCharacter.this.getId(), 1321007, 2), false);
                        }
                    }, buffInterval, buffInterval);
                }
            }
        }
        else if (effect.getSourceId() == 1001 || effect.getSourceId() == 10001001 || effect.getSourceId() == 1001) {
            this.prepareRecovery();
        }
        int clonez = 0;
        for (final Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft() == MapleBuffStat.ILLUSION) {
                clonez = statup.getRight();
            }
            final int value = statup.getRight();
            if (statup.getLeft() == MapleBuffStat.MONSTER_RIDING && effect.getSourceId() == 5221006 && this.battleshipHP <= 0) {
                this.battleshipHP = value;
            }
            this.effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, value));
        }
        if (clonez > 0) {
            final int cloneSize = Math.max(this.getNumClones(), this.getCloneSize());
            if (clonez > cloneSize) {
                for (int i = 0; i < clonez - cloneSize; ++i) {
                    this.cloneLook();
                }
            }
        }
        this.stats.recalcLocalStats();
        if (this.isGM()) {
            this.dropMessage(6, "[BUFF信息] 注册BUFF效果 - 当前BUFF总数: " + this.effects.size() + " 技能ID: " + effect.getSourceId());
        }
    }
    
    public List<MapleBuffStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<MapleBuffStat> bstats = new ArrayList<MapleBuffStat>();
        final Map<MapleBuffStat, MapleBuffStatValueHolder> allBuffs = new EnumMap<MapleBuffStat, MapleBuffStatValueHolder>(this.effects);
        for (final Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            final MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1L || startTime == mbsvh.startTime)) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }
    
    private boolean deregisterBuffStats(final List<MapleBuffStat> stats) {
        boolean clonez = false;
        final List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<MapleBuffStatValueHolder>(stats.size());
        for (final MapleBuffStat stat : stats) {
            final MapleBuffStatValueHolder mbsvh = this.effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (final MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET || stat == MapleBuffStat.REAPER) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final MapleSummon summon = (server.maps.MapleSummon)(server.maps.MapleSummon)this.summons.get(summonId);
                    if (summon == null) {
                        continue;
                    }
                    this.map.broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                    this.map.removeMapObject(summon);
                    this.removeVisibleMapObject(summon);
                    this.summons.remove(summonId);
                    if (summon.getSkill() != 1321007) {
                        continue;
                    }
                    if (this.beholderHealingSchedule != null) {
                        this.beholderHealingSchedule.cancel(false);
                        this.beholderHealingSchedule = null;
                    }
                    if (this.beholderBuffSchedule == null) {
                        continue;
                    }
                    this.beholderBuffSchedule.cancel(false);
                    this.beholderBuffSchedule = null;
                }
                else if (stat == MapleBuffStat.龙之力) {
                    if (this.dragonBloodSchedule == null) {
                        continue;
                    }
                    this.dragonBloodSchedule.cancel(false);
                    this.dragonBloodSchedule = null;
                }
                else if (stat == MapleBuffStat.神圣祈祷) {
                    this.cancelBuffStats(MapleBuffStat.神圣祈祷);
                }
                else if (stat == MapleBuffStat.灵魂助力) {
                    this.cancelBuffStats(MapleBuffStat.灵魂助力);
                }
                else {
                    if (stat != MapleBuffStat.ILLUSION) {
                        continue;
                    }
                    this.disposeClones();
                    clonez = true;
                }
            }
        }
        for (final MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (this.getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty() && cancelEffectCancelTasks.schedule != null) {
                cancelEffectCancelTasks.schedule.cancel(false);
            }
        }
        return clonez;
    }
    
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime) {
        if (effect == null) {
            return;
        }
        this.cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }
    
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime, final List<Pair<MapleBuffStat, Integer>> statups) {
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = this.getBuffStats(effect, startTime);
        }
        else {
            buffstats = new ArrayList<MapleBuffStat>(statups.size());
            for (final Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        if (buffstats.size() <= 0) {
            return;
        }
        final boolean clonez = this.deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            if (!this.getDoors().isEmpty()) {
                final MapleDoor door = this.getDoors().iterator().next();
                for (final MapleCharacter chr : door.getTarget().getCharacters()) {
                    door.sendDestroyData(chr.client);
                }
                for (final MapleCharacter chr : door.getTown().getCharacters()) {
                    door.sendDestroyData(chr.client);
                }
                for (final MapleDoor destroyDoor : this.getDoors()) {
                    door.getTarget().removeMapObject(destroyDoor);
                    door.getTown().removeMapObject(destroyDoor);
                }
                this.removeDoor();
                this.silentPartyUpdate();
            }
        }
        else if (effect.isMonsterRiding_()) {
            this.getMount().cancelSchedule();
        }
        else if (effect.isMonsterRiding()) {
            this.cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        }
        else if (effect.isAranCombo()) {
            this.combo = 0;
        }
        if (!overwrite) {
            this.cancelPlayerBuffs(buffstats);
            if (effect.isHide() && this.map.getMapObject(this.getObjectId(), MapleMapObjectType.PLAYER) != null) {
                this.hidden = false;
                this.map.broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);
                for (final MaplePet pet : this.pets) {
                    if (pet.getSummoned()) {
                        this.map.broadcastMessage(this, PetPacket.showPet(this, pet, false, false), false);
                    }
                }
                for (final WeakReference<MapleCharacter> chr2 : this.clones) {
                    if (chr2.get() != null) {
                        this.map.broadcastMessage(chr2.get(), MaplePacketCreator.spawnPlayerMapobject(chr2.get()), false);
                    }
                }
            }
        }
        if (!clonez) {
            for (final WeakReference<MapleCharacter> chr2 : this.clones) {
                if (chr2.get() != null) {
                    chr2.get().cancelEffect(effect, overwrite, startTime);
                }
            }
        }
    }
    
    public void cancelBuffStats(final MapleBuffStat... stat) {
        final List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        this.deregisterBuffStats(buffStatList);
        this.cancelPlayerBuffs(buffStatList);
    }
    
    public void cancelEffectFromBuffStat(final MapleBuffStat stat) {
        if (this.effects.get(stat) != null) {
            this.cancelEffect((this.effects.get(stat)).effect, false, -1L);
        }
    }
    
    private void cancelPlayerBuffs(final List<MapleBuffStat> buffstats) {
        final boolean write = this.client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null;
        if (buffstats.contains(MapleBuffStat.HOMING_BEACON)) {
            if (write) {
                this.client.getSession().write(MaplePacketCreator.cancelHoming());
            }
        }
        else if (buffstats.contains(MapleBuffStat.MONSTER_RIDING)) {
            this.client.getSession().write(MaplePacketCreator.cancelBuffMONSTER(buffstats));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuffMONSTER(this.getId(), buffstats), false);
        }
        else {
            this.client.getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(this.getId(), buffstats), false);
        }
        if (this.isGM()) {
            this.dropMessage(6, "取消技能BUFF: - buffstats.size() " + buffstats);
        }
    }
    
    private void cancelPlayerBuffs(final List<MapleBuffStat> buffstats, final MapleStatEffect effect) {
        if (effect.isMonsterS()) {
            this.client.getSession().write(MaplePacketCreator.cancelBuffMONSTERS(buffstats));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuffMONSTERS(this.getId(), buffstats), false);
        }
    }
    
    public void dispel() {
        if (!this.isHidden()) {
            final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
            for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
                if (mbsvh.effect.isSkill() && mbsvh.schedule != null && !mbsvh.effect.isMorph()) {
                    this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }
    
    public void dispelSkill(final int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (skillid == 0) {
                if (mbsvh.effect.isSkill() && (mbsvh.effect.getSourceId() == 4331003 || mbsvh.effect.getSourceId() == 4331002 || mbsvh.effect.getSourceId() == 4341002 || mbsvh.effect.getSourceId() == 22131001 || mbsvh.effect.getSourceId() == 1321007 || mbsvh.effect.getSourceId() == 2121005 || mbsvh.effect.getSourceId() == 2221005 || mbsvh.effect.getSourceId() == 2311006 || mbsvh.effect.getSourceId() == 2321003 || mbsvh.effect.getSourceId() == 3111002 || mbsvh.effect.getSourceId() == 3111005 || mbsvh.effect.getSourceId() == 3211002 || mbsvh.effect.getSourceId() == 3211005 || mbsvh.effect.getSourceId() == 4111002)) {
                    this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
                continue;
            }
            else {
                if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                    this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
                continue;
            }
        }
    }
    
    public void dispelBuff(final int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSourceId() == skillid) {
                this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }
    
    public void cancelAllBuffs_() {
        this.effects.clear();
    }
    
    public void cancelAllBuffs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }
    
    public void cancelMorphs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5111005:
                case 5121003:
                case 13111005:
                case 15111002: {}
                default: {
                    if (!mbsvh.effect.isMorph()) {
                        continue;
                    }
                    this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    continue;
                }
            }
        }
    }
    
    public int getMorphState() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }
    
    public void silentGiveBuffs(final List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        for (final PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime);
        }
    }
    
    public List<PlayerBuffValueHolder> getAllBuffs() {
        final List ret = new ArrayList();
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
        }
        return (List<PlayerBuffValueHolder>)ret;
    }
    
    public void cancelMagicDoor() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (final MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                this.cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }
    
    public int getSkillLevel(final int skillid) {
        return this.getSkillLevel(SkillFactory.getSkill(skillid));
    }
    
    public void handleEnergyCharge(final int skillid, final int targets) {
        final ISkill echskill = SkillFactory.getSkill(skillid);
        final byte skilllevel = this.getSkillLevel(echskill);
        if (skilllevel > 0) {
            final MapleStatEffect echeff = echskill.getEffect(skilllevel);
            if (targets > 0) {
                if (this.getBuffedValue(MapleBuffStat.能量获得) == null) {
                    echeff.applyEnergyBuff(this, true);
                }
                else {
                    Integer energyLevel = this.getBuffedValue(MapleBuffStat.能量获得);
                    if (energyLevel <= 10000) {
                        energyLevel += echeff.getX() * targets;
                        this.client.getSession().write(MaplePacketCreator.showOwnBuffEffect(skillid, 2));
                        this.map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(this.id, skillid, 2), false);
                        if (energyLevel >= 10000) {
                            energyLevel = 10000;
                            BuffTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    final Integer energyLevel = 0;
                                    MapleCharacter.this.setBuffedValue(MapleBuffStat.能量获得, energyLevel);
                                    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.能量获得, energyLevel));
                                    client.getSession().write(MaplePacketCreator.能量条(stat, 0));
                                }
                            }, 50000L);
                        }
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.能量获得, energyLevel));
                        this.client.getSession().write(MaplePacketCreator.能量条(stat, energyLevel / 1000));
                        this.setBuffedValue(MapleBuffStat.能量获得, energyLevel);
                    }
                    else if (energyLevel == 10000) {
                        echeff.applyEnergyBuff(this, false);
                        this.setBuffedValue(MapleBuffStat.能量获得, 10001);
                    }
                }
            }
        }
    }
    
    public void handleBattleshipHP(final int damage) {
        if (this.isActiveBuffedValue(5221006)) {
            this.battleshipHP -= damage;
            if (this.battleshipHP <= 0) {
                this.battleshipHP = 0;
                final MapleStatEffect effect = this.getStatForBuff(MapleBuffStat.骑兽技能);
                this.client.getSession().write(MaplePacketCreator.skillCooldown(5221006, effect.getCooldown()));
                this.addCooldown(5221006, System.currentTimeMillis(), effect.getCooldown() * 1000);
                this.dispelSkill(5221006);
            }
        }
    }
    
    public final void handleOrbgain() {
        if (this.getBuffedValue(MapleBuffStat.COMBO) == null) {
            return;
        }
        final int orbcount = this.getBuffedValue(MapleBuffStat.COMBO);
        ISkill combo = null;
        ISkill advcombo = null;
        switch (this.getJob()) {
            case 1110:
            case 1111:
            case 1112: {
                combo = SkillFactory.getSkill(11111001);
                advcombo = SkillFactory.getSkill(11110005);
                break;
            }
            default: {
                combo = SkillFactory.getSkill(1111002);
                advcombo = SkillFactory.getSkill(1120003);
                break;
            }
        }
        MapleStatEffect ceffect = null;
        final int advComboSkillLevel = this.getSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        }
        else {
            if (this.getSkillLevel(combo) <= 0) {
                return;
            }
            ceffect = combo.getEffect(this.getSkillLevel(combo));
        }
        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult() && neworbcount < ceffect.getX() + 1) {
                ++neworbcount;
            }
            final List stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, neworbcount));
            this.setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int)(this.getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis());
            this.client.getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat, ceffect));
            this.map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(this, this.getId(), stat, ceffect), false);
        }
    }
    
    public void handleOrbconsume() {
        ISkill combo = null;
        switch (this.getJob()) {
            case 1110:
            case 1111: {
                combo = SkillFactory.getSkill(11111001);
                break;
            }
            default: {
                combo = SkillFactory.getSkill(1111002);
                break;
            }
        }
        if (this.getSkillLevel(combo) <= 0) {
            return;
        }
        final MapleStatEffect ceffect = this.getStatForBuff(MapleBuffStat.COMBO);
        if (ceffect == null) {
            return;
        }
        final List stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
        this.setBuffedValue(MapleBuffStat.COMBO, 1);
        int duration = ceffect.getDuration();
        duration += (int)(this.getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis());
        this.client.getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat, ceffect));
        this.map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(this, this.getId(), stat, ceffect), false);
    }
    
    public void silentEnforceMaxHpMp() {
        this.stats.setMp(this.stats.getMp());
        this.stats.setHp(this.stats.getHp(), true);
    }
    
    public void enforceMaxHpMp() {
        final List<Pair<MapleStat, Integer>> statups = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (this.stats.getMp() > this.stats.getCurrentMaxMp()) {
            this.stats.setMp(this.stats.getMp());
            statups.add(new Pair<MapleStat, Integer>(MapleStat.MP, (int)this.stats.getMp()));
        }
        if (this.stats.getHp() > this.stats.getCurrentMaxHp()) {
            this.stats.setHp(this.stats.getHp());
            statups.add(new Pair<MapleStat, Integer>(MapleStat.HP, (int)this.stats.getHp()));
        }
        if (statups.size() > 0) {
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, this.getJob()));
        }
    }
    
    public MapleMap getMap() {
        return this.map;
    }
    
    public MonsterBook getMonsterBook() {
        return this.monsterbook;
    }
    
    public void setMap(final MapleMap newmap) {
        this.map = newmap;
    }
    
    public void setMap(final int PmapId) {
        this.mapid = PmapId;
    }
    
    public int getMapId() {
        if (this.map != null) {
            return this.map.getId();
        }
        return this.mapid;
    }
    
    public byte getInitialSpawnpoint() {
        return this.initialSpawnPoint;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public final String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }
    
    public final short getLevel() {
        return this.level;
    }
    
    public final short getFame() {
        return this.fame;
    }
    
    public final int getDojo() {
        return this.dojo;
    }
    
    public final int getDojoRecord() {
        return this.dojoRecord;
    }
    
    public final int getFallCounter() {
        return this.fallcounter;
    }
    
    public final MapleClient getClient() {
        return this.client;
    }
    
    public final void setClient(final MapleClient client) {
        this.client = client;
    }
    
    public int getExp() {
        return this.exp;
    }
    
    public short getRemainingAp() {
        return this.remainingAp;
    }
    
    public int getRemainingSp() {
        return this.remainingSp[GameConstants.getSkillBook(this.job)];
    }
    
    public int getRemainingSp(final int skillbook) {
        return this.remainingSp[skillbook];
    }
    
    public int[] getRemainingSps() {
        return this.remainingSp;
    }
    
    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < this.remainingSp.length; ++i) {
            if (this.remainingSp[i] > 0) {
                ++ret;
            }
        }
        return ret;
    }
    
    public short getHpApUsed() {
        return this.hpApUsed;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public void setHpApUsed(final short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }
    
    public byte getSkinColor() {
        return this.skinColor;
    }
    
    public void setSkinColor(final byte skinColor) {
        this.skinColor = skinColor;
    }
    
    public short getJob() {
        return this.job;
    }
    
    public byte getGender() {
        return this.gender;
    }
    
    public int getHair() {
        return this.hair;
    }
    
    public int getFace() {
        return this.face;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setExp(final int exp) {
        this.exp = exp;
    }
    
    public void setHair(final int hair) {
        this.hair = hair;
    }
    
    public void setFace(final int face) {
        this.face = face;
    }
    
    public void setFame(final short fame) {
        this.fame = fame;
    }
    
    public void setDojo(final int dojo) {
        this.dojo = dojo;
    }
    
    public void setDojoRecord(final boolean reset) {
        if (reset) {
            this.dojo = 0;
            this.dojoRecord = 0;
        }
        else {
            ++this.dojoRecord;
        }
    }
    
    public void setFallCounter(final int fallcounter) {
        this.fallcounter = fallcounter;
    }
    
    public Point getOldPosition() {
        return this.old;
    }
    
    public void setOldPosition(final Point x) {
        this.old = x;
    }
    
    public void setRemainingAp(final short remainingAp) {
        this.remainingAp = remainingAp;
    }
    
    public void setRemainingSp(final int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(this.job)] = remainingSp;
    }
    
    public void setRemainingSp(final int remainingSp, final int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }
    
    public void setGender(final byte gender) {
        this.gender = gender;
    }
    
    public void setInvincible(final boolean invinc) {
        this.invincible = invinc;
    }
    
    public boolean isInvincible() {
        return this.invincible;
    }
    
    public CheatTracker getCheatTracker() {
        return this.anticheat;
    }
    
    public BuddyList getBuddylist() {
        return this.buddylist;
    }
    
    public void addFame(final int famechange) {
        this.fame += (short)famechange;
    }
    
    public void changeMapBanish(final int mapid, final String portal, final String msg) {
        this.dropMessage(5, msg);
        final MapleMap map = this.client.getChannelServer().getMapFactory().getMap(mapid);
        this.changeMap(map, map.getPortal(portal));
    }
    
    public void changeMap(final MapleMap to, final Point pos) {
        this.changeMapInternal(to, pos, MaplePacketCreator.getWarpToMap(to, 128, this), null);
    }
    
    public void changeMap(final int to) {
        final MapleMap map = ChannelServer.getInstance(this.getClient().getChannel()).getMapFactory().getMap(to);
        this.changeMapInternal(map, map.getPortal(0).getPosition(), MaplePacketCreator.getWarpToMap(map, 0, this), map.getPortal(0));
    }
    
    public void changeMap(final MapleMap to, final MaplePortal pto) {
        this.changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), null);
    }
    
    public void changeMapPortal(final MapleMap to, final MaplePortal pto) {
        this.changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), pto);
    }
    
    private void changeMapInternal(final MapleMap to, final Point pos, final MaplePacket warpPacket, final MaplePortal pto) {
        if (ChannelServer.banmaps.contains(to.getId())) {
            if (!this.isGM()) {
                this.dropMessage(1, "该地图已禁止进入.");
                this.getClient().getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            this.dropMessage(1, "由于你是GM可以正常进入已屏蔽的地图.");
        }
        if (to == null) {
            return;
        }
        final int nowmapid = this.map.getId();
        if (this.eventInstance != null) {
            this.eventInstance.changedMap(this, to.getId());
        }
        final boolean pyramid = this.pyramidSubway != null;
        if (this.map.getId() == nowmapid) {
            this.client.getSession().write(warpPacket);
            this.map.removePlayer(this);
            if (!this.isClone() && this.client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null) {
                this.map = to;
                this.setPosition(pos);
                to.addPlayer(this);
                this.stats.relocHeal();
            }
        }
        if (this.party != null) {
            this.silentPartyUpdate();
            this.getClient().getSession().write(MaplePacketCreator.updateParty(this.getClient().getChannel(), this.party, PartyOperation.SILENT_UPDATE, null));
            this.updatePartyMemberHP();
        }
        if (pyramid && this.pyramidSubway != null) {
            this.pyramidSubway.onChangeMap(this, to.getId());
        }
    }
    
    public void leaveMap() {
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            this.visibleMapObjects.clear();
        }
        finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
        if (this.chair != 0) {
            this.cancelFishingTask();
            this.chair = 0;
        }
        this.cancelMapTimeLimitTask();
    }
    
    public void changeJob(final int newJob) {
        try {
            final boolean isEv = GameConstants.isEvan(this.job) || GameConstants.isResist(this.job);
            this.job = (short)newJob;
            if (newJob != 0 && newJob != 1000 && newJob != 2000 && newJob != 2001 && newJob != 3000) {
                if (isEv) {
                    final int[] remainingSp = this.remainingSp;
                    final int skillBook = GameConstants.getSkillBook(newJob);
                    remainingSp[skillBook] += 5;
                    this.client.getSession().write(UIPacket.getSPMsg((byte)5, (short)newJob));
                }
                else {
                    final int[] remainingSp2 = this.remainingSp;
                    final int skillBook2 = GameConstants.getSkillBook(newJob);
                    ++remainingSp2[skillBook2];
                    if (newJob % 10 >= 2) {
                        final int[] remainingSp3 = this.remainingSp;
                        final int skillBook3 = GameConstants.getSkillBook(newJob);
                        remainingSp3[skillBook3] += 2;
                    }
                }
            }
            if (newJob > 0 && !this.isGM()) {
                this.resetStatsByJob(true);
                if (!GameConstants.isEvan(newJob)) {
                    if (this.getLevel() > ((newJob == 200) ? 8 : 10) && newJob % 100 == 0 && newJob % 1000 / 100 > 0) {
                        final int[] remainingSp4 = this.remainingSp;
                        final int skillBook4 = GameConstants.getSkillBook(newJob);
                        remainingSp4[skillBook4] += 3 * (this.getLevel() - ((newJob == 200) ? 8 : 10));
                    }
                }
                else if (newJob == 2200) {
                    MapleQuest.getInstance(22100).forceStart(this, 0, null);
                    MapleQuest.getInstance(22100).forceComplete(this, 0);
                    this.expandInventory((byte)1, 4);
                    this.expandInventory((byte)2, 4);
                    this.expandInventory((byte)3, 4);
                    this.expandInventory((byte)4, 4);
                    this.client.getSession().write(MaplePacketCreator.getEvanTutorial("UI/tutorial/evan/14/0"));
                    this.dropMessage(5, "The baby Dragon hatched and appears to have something to tell you. Click the baby Dragon to start a conversation.");
                }
            }
            this.client.getSession().write(MaplePacketCreator.updateSp(this, false, isEv));
            this.updateSingleStat(MapleStat.JOB, newJob);
            int maxhp = this.stats.getMaxHp();
            int maxmp = this.stats.getMaxMp();
            switch (this.job) {
                case 100:
                case 1100:
                case 2100:
                case 3200: {
                    maxhp += Randomizer.rand(200, 250);
                    break;
                }
                case 200:
                case 2200:
                case 2210: {
                    maxmp += Randomizer.rand(100, 150);
                    break;
                }
                case 300:
                case 400:
                case 500:
                case 3300:
                case 3500: {
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                }
                case 110: {
                    maxhp += Randomizer.rand(300, 350);
                    break;
                }
                case 120:
                case 130:
                case 510:
                case 512:
                case 1110:
                case 2110:
                case 3210: {
                    maxhp += Randomizer.rand(300, 350);
                    break;
                }
                case 210:
                case 220:
                case 230: {
                    maxmp += Randomizer.rand(400, 450);
                    break;
                }
                case 310:
                case 312:
                case 320:
                case 322:
                case 410:
                case 412:
                case 420:
                case 422:
                case 430:
                case 520:
                case 522:
                case 1310:
                case 1410:
                case 3310:
                case 3510: {
                    maxhp += Randomizer.rand(300, 350);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                }
                case 800:
                case 900: {
                    maxhp += 30000;
                    maxhp += 30000;
                    break;
                }
            }
            if (maxhp >= 30000) {
                maxhp = 30000;
            }
            if (maxmp >= 30000) {
                maxmp = 30000;
            }
            this.stats.setMaxHp((short)maxhp);
            this.stats.setMaxMp((short)maxmp);
            this.stats.setHp((short)maxhp);
            this.stats.setMp((short)maxmp);
            final List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(4);
            statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, maxhp));
            statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, maxmp));
            statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, maxhp));
            statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, maxmp));
            this.stats.recalcLocalStats();
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this.getJob()));
            this.map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(this.getId(), 8), false);
            this.silentPartyUpdate();
            this.guildUpdate();
            this.familyUpdate();
            if (this.dragon != null) {
                this.map.broadcastMessage(MaplePacketCreator.removeDragon(this.id));
                this.map.removeMapObject(this.dragon);
                this.dragon = null;
            }
            this.baseSkills();
            if (newJob >= 2200 && newJob <= 2218) {
                if (this.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
                    this.cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
                }
                this.makeDragon();
                this.map.spawnDragon(this.dragon);
                this.map.updateMapObjectVisibility(this, this.dragon);
            }
        }
        catch (Exception e) {
            FileoutputUtil.outputFileError("日志/Logs/Log_Script_脚本异常.txt", e);
        }
    }
    
    public void baseSkills() {
        if (GameConstants.getJobNumber(this.job) >= 3) {
            final List<Integer> skills = SkillFactory.getSkillsByJob(this.job);
            if (skills != null) {
                for (final int i : skills) {
                    final ISkill skil = SkillFactory.getSkill(i);
                    if (skil != null && !skil.isInvisible() && skil.isFourthJob() && this.getSkillLevel(skil) <= 0 && this.getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        this.changeSkillLevel(skil, (byte)0, (byte)skil.getMasterLevel());
                    }
                }
            }
        }
    }
    
    public void makeDragon() {
        this.dragon = new MapleDragon(this);
    }
    
    public MapleDragon getDragon() {
        return this.dragon;
    }
    
    public void gainAp(final short ap) {
        this.remainingAp += ap;
        this.updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }
    
    public void gainSP(final int sp) {
        final int[] remainingSp = this.remainingSp;
        final int skillBook = GameConstants.getSkillBook(this.job);
        remainingSp[skillBook] += sp;
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        this.client.getSession().write(UIPacket.getSPMsg((byte)sp, this.job));
    }
    
    public void gainSP(final int sp, final int skillbook) {
        final int[] remainingSp = this.remainingSp;
        remainingSp[skillbook] += sp;
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        this.client.getSession().write(UIPacket.getSPMsg((byte)sp, this.job));
    }
    
    public void resetSP(final int sp) {
        for (int i = 0; i < this.remainingSp.length; ++i) {
            this.remainingSp[i] = sp;
        }
        this.updateSingleStat(MapleStat.AVAILABLESP, this.getRemainingSp());
    }
    
    public void resetAPSP() {
        for (int i = 0; i < this.remainingSp.length; ++i) {
            this.remainingSp[i] = 0;
        }
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        this.gainAp((short)(-this.remainingAp));
    }
    
    public int getAllSkillLevels() {
        int rett = 0;
        for (final Entry<ISkill, SkillEntry> ret : this.skills.entrySet()) {
            if (!ret.getKey().isBeginnerSkill() && ret.getValue().skillevel > 0) {
                rett += ret.getValue().skillevel;
            }
        }
        return rett;
    }
    
    public void changeSkillLevel(final ISkill skill, final byte newLevel, final byte newMasterlevel) {
        if (skill == null) {
            return;
        }
        this.changeSkillLevel(skill, newLevel, newMasterlevel, skill.isTimeLimited() ? (System.currentTimeMillis() + 2592000000L) : -1L);
    }
    
    public void changeSkillLevel(final ISkill skill, final byte newLevel, final byte newMasterlevel, final long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            return;
        }
        this.client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, expiration));
        if (newLevel == 0 && newMasterlevel == 0) {
            if (this.skills.containsKey(skill)) {
                this.skills.remove(skill);
            }
        }
        else {
            this.skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
        }
        if (GameConstants.isRecoveryIncSkill(skill.getId())) {
            this.stats.relocHeal();
        }
        else if (GameConstants.isElementAmp_Skill(skill.getId())) {
            this.stats.recalcLocalStats();
        }
    }
    
    public void changeSkillLevel_Skip(final ISkill skill, final byte newLevel, final byte newMasterlevel) {
        if (skill == null) {
            return;
        }
        this.client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, -1L));
        if (newLevel == 0 && newMasterlevel == 0) {
            if (this.skills.containsKey(skill)) {
                this.skills.remove(skill);
            }
        }
        else {
            this.skills.put(skill, new SkillEntry(newLevel, newMasterlevel, -1L));
        }
    }
    
    public void playerDead() {
        final MapleStatEffect statss = this.getStatForBuff(MapleBuffStat.SOUL_STONE);
        if (statss != null) {
            this.dropMessage(5, "You have been revived by Soul Stone.");
            this.getStat().setHp(this.getStat().getMaxHp() / 100 * statss.getX());
            this.setStance(0);
            this.changeMap(this.getMap(), this.getMap().getPortal(0));
            return;
        }
        if (this.getEventInstance() != null) {
            this.getEventInstance().playerKilled(this);
        }
        final MaplePet pet = this.getPet(0);
        if (pet != null) {
            this.unequipPet(pet, true);
        }
        this.dispelSkill(0);
        this.cancelEffectFromBuffStat(MapleBuffStat.MORPH);
        this.cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
        this.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
        this.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        this.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        this.checkFollow();
        if (this.job != 0 && this.job != 1000 && this.job != 2000 && this.job != 2001 && this.job != 3000) {
            int charms = this.getItemQuantity(5130000, false);
            if (charms > 0) {
                MapleInventoryManipulator.removeById(this.client, MapleInventoryType.CASH, 5130000, 1, true, false);
                if (--charms > 255) {
                    charms = 255;
                }
                this.client.sendPacket(MTSCSPacket.useCharm((byte)charms, (byte)0));
            }
            else {
                float diepercentage = 0.0f;
                final int expforlevel = GameConstants.getExpNeededForLevel(this.level);
                if (this.map.isTown() || FieldLimitType.RegularExpLoss.check(this.map.getFieldLimit())) {
                    diepercentage = 0.01f;
                }
                else {
                    float v8 = 0.0f;
                    if (this.job / 100 == 3) {
                        v8 = 0.08f;
                    }
                    else {
                        v8 = 0.2f;
                    }
                    diepercentage = (float)(v8 / this.stats.getLuk() + 0.05);
                }
                int v9 = (int)(this.exp - (long)(expforlevel * (double)diepercentage));
                if (v9 < 0) {
                    v9 = 0;
                }
                this.exp = v9;
            }
        }
        this.updateSingleStat(MapleStat.EXP, this.exp);
        if (!this.stats.checkEquipDurabilitys(this, -100)) {
            this.dropMessage(5, "該裝備耐久度已經使用完畢，必須修理才可以繼續使用.");
        }
        if (this.pyramidSubway != null) {
            this.stats.setHp(50);
            this.pyramidSubway.fail(this);
        }
    }
    
    public void updatePartyMemberHP() {
        if (this.party != null) {
            final int channel = this.client.getChannel();
            for (final MaplePartyCharacter partychar : this.party.getMembers()) {
                if (partychar.getMapid() == this.getMapId() && partychar.getChannel() == channel) {
                    final MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other == null) {
                        continue;
                    }
                    other.getClient().getSession().write(MaplePacketCreator.updatePartyMemberHP(this.getId(), this.stats.getHp(), this.stats.getCurrentMaxHp()));
                }
            }
        }
    }
    
    public void receivePartyMemberHP() {
        if (this.party == null) {
            return;
        }
        final int channel = this.client.getChannel();
        for (final MaplePartyCharacter partychar : this.party.getMembers()) {
            if (partychar.getMapid() == this.getMapId() && partychar.getChannel() == channel) {
                final MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                if (other == null) {
                    continue;
                }
                this.client.getSession().write(MaplePacketCreator.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
            }
        }
    }
    
    public void healHP(final int delta) {
        this.addHP(delta);
    }
    
    public void healMP(final int delta) {
        this.addMP(delta);
    }
    
    public void addHP(final int delta) {
        if (this.stats.setHp(this.stats.getHp() + delta)) {
            this.updateSingleStat(MapleStat.HP, this.stats.getHp());
        }
    }
    
    public void addMP(final int delta) {
        if (this.stats.setMp(this.stats.getMp() + delta)) {
            this.updateSingleStat(MapleStat.MP, this.stats.getMp());
        }
    }
    
    public void addMPHP(final int hpDiff, final int mpDiff) {
        final List statups = new ArrayList();
        if (this.stats.setHp(this.stats.getHp() + hpDiff)) {
            statups.add(new Pair<MapleStat, Integer>(MapleStat.HP, (int)this.stats.getHp()));
        }
        if (this.stats.setMp(this.stats.getMp() + mpDiff)) {
            statups.add(new Pair<MapleStat, Integer>(MapleStat.MP, (int)this.stats.getMp()));
        }
        if (statups.size() > 0) {
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, this.getJob()));
        }
    }
    
    public final boolean canQuestAction() {
        if (this.lastQuestTime + 250L > System.currentTimeMillis()) {
            return false;
        }
        this.lastQuestTime = System.currentTimeMillis();
        return true;
    }
    
    private void prepareRecovery() {
        this.lastRecoveryTime = System.currentTimeMillis();
    }
    
    public boolean canRecovery() {
        return this.lastRecoveryTime > 0L && this.lastRecoveryTime + 5000L < System.currentTimeMillis() + 5000L;
    }
    
    public void doRecovery() {
        final MapleStatEffect eff = this.getStatForBuff(MapleBuffStat.RECOVERY);
        if (eff != null) {
            this.prepareRecovery();
            if (this.stats.getHp() > this.stats.getCurrentMaxHp()) {
                this.cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
            }
            else {
                this.healHP(eff.getX());
            }
        }
    }
    
    public final boolean canHP() {
        if (this.lastHPTime + 5000L > System.currentTimeMillis()) {
            return false;
        }
        this.lastHPTime = System.currentTimeMillis();
        return true;
    }
    
    public final boolean canMP() {
        if (this.lastMPTime + 5000L > System.currentTimeMillis()) {
            return false;
        }
        this.lastMPTime = System.currentTimeMillis();
        return true;
    }
    
    public final boolean canCheckPeriod() {
        if (this.lastCheckPeriodTime + 30000L > System.currentTimeMillis()) {
            return false;
        }
        this.lastCheckPeriodTime = System.currentTimeMillis();
        return true;
    }
    
    public final boolean canMoveItem() {
        if (this.lastMoveItemTime + 250L > System.currentTimeMillis()) {
            return false;
        }
        this.lastMoveItemTime = System.currentTimeMillis();
        return true;
    }
    
    public void updateSingleStat(final MapleStat stat, final int newval) {
        this.updateSingleStat(stat, newval, false);
    }
    
    public void updateSingleStat(final MapleStat stat, final int newval, final boolean itemReaction) {
        final Pair<MapleStat, Integer> statpair = new Pair<MapleStat, Integer>(stat, newval);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(Collections.singletonList(statpair), itemReaction, this.getJob()));
    }
    
    public void gainExp(final int total, final boolean show, final boolean inChat, final boolean white) {
        try {
            final int prevexp = this.getExp();
            int needed = GameConstants.getExpNeededForLevel(this.level);
            if (this.level >= Integer.parseInt(ServerProperties.getProperty("tms.MLevel")) || (GameConstants.isKOC(this.job) && this.level >= Integer.parseInt(ServerProperties.getProperty("tms.QLevel")))) {
                if (this.exp + total > needed) {
                    this.setExp(needed);
                }
                else {
                    this.exp += total;
                }
            }
            else {
                boolean leveled = false;
                if (this.exp + total >= needed) {
                    this.exp += total;
                    this.levelUp();
                    leveled = true;
                    needed = GameConstants.getExpNeededForLevel(this.level);
                    if (this.exp > needed) {
                        this.setExp(needed);
                    }
                }
                else {
                    this.exp += total;
                }
                if (total > 0) {
                    this.familyRep(prevexp, needed, leveled);
                }
            }
            if (total != 0) {
                if (this.exp < 0) {
                    if (total > 0) {
                        this.setExp(needed);
                    }
                    else if (total < 0) {
                        this.setExp(0);
                    }
                }
                if (show) {
                    this.client.getSession().write(MaplePacketCreator.GainEXP_Others(total, inChat, white));
                }
                this.updateSingleStat(MapleStat.EXP, this.getExp());
            }
        }
        catch (Exception e) {
            FileoutputUtil.outputFileError("日志/Logs/Log_Script_脚本异常.rtf", e);
        }
    }
    
    public void familyRep(final int prevexp, final int needed, final boolean leveled) {
        if (this.mfc != null) {
            final int onepercent = needed / 100;
            int percentrep = prevexp / onepercent + this.getExp() / onepercent;
            if (leveled) {
                percentrep = 100 - percentrep + this.level / 2;
            }
            if (percentrep > 0) {
                final int sensen = Family.setRep(this.mfc.getFamilyId(), this.mfc.getSeniorId(), percentrep, this.level);
                if (sensen > 0) {
                    Family.setRep(this.mfc.getFamilyId(), sensen, percentrep / 2, this.level);
                }
            }
        }
    }
    
    public void gainExpMonster(final int gain, final boolean show, final boolean white, final byte pty, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, int 网吧经验, final boolean partyBonusMob, final int partyBonusRate) {
        final Properties props = ServerConstants.getDefaultProperties("serverConstants.properties");
        final int 结婚经验 = 0;
        网吧经验 = 0;
        int total = gain + Class_Bonus_EXP + Equipment_Bonus_EXP + 结婚经验;
        final int partyinc = 0;
        if (this.canCheckPeriod()) {
            this.expirationTask2(true);
        }
        if (this.level <= Integer.parseInt(ServerProperties.getProperty("tms.获得经验等级"))) {
            网吧经验 = (int)(gain / 100.0 * Integer.parseInt(ServerProperties.getProperty("tms.获得经验百分比")));
            total += 网吧经验;
        }
        int 组队经验 = 0;
        if (pty > 1) {
            final double rate = (this.map == null || !partyBonusMob || this.map.getPartyBonusRate() <= 0) ? 0.05 : ((partyBonusRate > 0) ? (partyBonusRate / 100.0) : (this.map.getPartyBonusRate() / 100.0));
            组队经验 = (int)((float)(gain * rate) * (pty + ((rate > 0.05) ? -1 : 1)));
            total += 组队经验;
        }
        if (gain > 0 && total < gain) {
            total = Integer.MAX_VALUE;
        }
        int needed = GameConstants.getExpNeededForLevel(this.level);
        if (this.level >= Integer.parseInt(ServerProperties.getProperty("tms.MLevel", "200")) || (GameConstants.isKOC(this.job) && this.level >= Integer.parseInt(ServerProperties.getProperty("tms.QLevel", "200")))) {
            if (this.exp + total > needed) {
                this.setExp(needed);
            }
            else {
                this.exp += total;
            }
        }
        else {
            boolean leveled = false;
            if (this.exp + total >= needed) {
                this.exp += total;
                this.levelUp();
                leveled = true;
                needed = GameConstants.getExpNeededForLevel(this.level);
                if (this.exp > needed) {
                    this.setExp(needed);
                }
            }
            else {
                this.exp += total;
            }
            if (total > 0) {}
        }
        if (gain != 0) {
            if (this.exp < 0) {
                if (gain > 0) {
                    this.setExp(GameConstants.getExpNeededForLevel(this.level));
                }
                else if (gain < 0) {
                    this.setExp(0);
                }
            }
            this.updateSingleStat(MapleStat.EXP, this.getExp());
            if (show) {
                this.client.getSession().write(MaplePacketCreator.GainEXP_Monster(gain, white, partyinc, Class_Bonus_EXP, Equipment_Bonus_EXP, 网吧经验, 结婚经验));
            }
        }
    }
    
    public void forceReAddItem_NoUpdate(final Item item, final MapleInventoryType type) {
        this.getInventory(type).removeSlot(item.getPosition());
        this.getInventory(type).addFromDB(item);
    }
    
    public void forceReAddItem(final Item item, final MapleInventoryType type) {
        this.forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            this.client.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, (byte)((type == MapleInventoryType.EQUIPPED) ? 1 : type.getType())));
        }
    }
    
    public void forceReAddItem_Flag(final Item item, final MapleInventoryType type) {
        this.forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            this.client.getSession().write(MaplePacketCreator.updateSpecialItemUse_(item, (byte)((type == MapleInventoryType.EQUIPPED) ? 1 : type.getType())));
        }
    }
    
    public void silentPartyUpdate() {
        if (this.party != null) {
            Party.updateParty(this.party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }
    
    public boolean isGM() {
        return this.gmLevel > 0;
    }
    
    public boolean isAdmin() {
        return this.gmLevel >= 100;
    }
    
    public int getGMLevel() {
        return this.gmLevel;
    }
    
    public boolean isPlayer() {
        return this.gmLevel == 0;
    }
    
    public boolean hasGmLevel(final int level) {
        return this.gmLevel >= level;
    }
    
    public final MapleInventory getInventory(final MapleInventoryType type) {
        return this.inventory[type.ordinal()];
    }
    
    public final MapleInventory[] getInventorys() {
        return this.inventory;
    }
    
    public final void expirationTask2(final boolean packet) {
        final List<Integer> ret = new ArrayList<Integer>();
        final long currenttime = System.currentTimeMillis();
        final List<Pair<MapleInventoryType, Item>> toberemove = new ArrayList<Pair<MapleInventoryType, Item>>();
        final List<Item> tobeunlock = new ArrayList<Item>();
        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final Item item : this.getInventory(inv)) {
                final long expiration = item.getExpiration();
                if (expiration != -1L && !GameConstants.isPet(item.getItemId()) && currenttime > expiration) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    }
                    else {
                        if (currenttime <= expiration) {
                            continue;
                        }
                        toberemove.add(new Pair<MapleInventoryType, Item>(inv, item));
                    }
                }
                else {
                    if (item.getItemId() != 5000054 || item.getPet() == null || item.getPet().getSecondsLeft() > 0) {
                        continue;
                    }
                    toberemove.add(new Pair<MapleInventoryType, Item>(inv, item));
                }
            }
        }
        for (final Pair<MapleInventoryType, Item> itemz : toberemove) {
            final Item item2 = itemz.getRight();
            ret.add(item2.getItemId());
            if (packet) {
                this.getInventory(itemz.getLeft()).removeItem(item2.getPosition(), item2.getQuantity(), false, this);
            }
            else {
                this.getInventory(itemz.getLeft()).removeItem(item2.getPosition(), item2.getQuantity(), false);
            }
        }
        for (final Item itemz2 : tobeunlock) {
            itemz2.setExpiration(-1L);
            itemz2.setFlag((byte)(itemz2.getFlag() - ItemFlag.LOCK.getValue()));
        }
        this.pendingExpiration = ret;
        final List<Integer> skilz = new ArrayList<Integer>();
        final List<ISkill> toberem = new ArrayList<ISkill>();
        for (final Entry<ISkill, SkillEntry> skil : this.skills.entrySet()) {
            if (skil.getValue().expiration != -1L && currenttime > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (final ISkill skil2 : toberem) {
            skilz.add(skil2.getId());
            this.skills.remove(skil2);
        }
        this.pendingSkills = skilz;
    }
    
    public final void expirationTask() {
        this.expirationTask(true);
    }
    
    public final void expirationTask(final boolean pending) {
        if (pending) {
            if (this.pendingExpiration != null) {
                for (final Integer z : this.pendingExpiration) {
                    this.client.getSession().write(MTSCSPacket.itemExpired(z));
                }
            }
            this.pendingExpiration = null;
            if (this.pendingSkills != null) {
                for (final Integer z : this.pendingSkills) {
                    this.client.getSession().write(MaplePacketCreator.updateSkill(z, 0, 0, -1L));
                    this.client.getSession().write(MaplePacketCreator.serverNotice(5, "[" + SkillFactory.getSkillName(z) + "] skill has expired and will not be available for use."));
                }
            }
            this.pendingSkills = null;
            return;
        }
        final List<Integer> ret = new ArrayList<Integer>();
        final long currenttimes = System.currentTimeMillis();
        final List<Pair<MapleInventoryType, Item>> toberemove = new ArrayList<Pair<MapleInventoryType, Item>>();
        final List<Item> tobeunlock = new ArrayList<Item>();
        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final Item item : this.getInventory(inv)) {
                final long expiration = item.getExpiration();
                if (expiration != -1L && !GameConstants.isPet(item.getItemId()) && currenttimes > expiration) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    }
                    else {
                        if (currenttimes <= expiration) {
                            continue;
                        }
                        toberemove.add(new Pair<MapleInventoryType, Item>(inv, item));
                    }
                }
                else if (item.getItemId() == 5000054 && item.getPet() != null && item.getPet().getSecondsLeft() <= 0) {
                    toberemove.add(new Pair<MapleInventoryType, Item>(inv, item));
                }
                else {
                    if (expiration == -1L || expiration >= currenttimes) {
                        continue;
                    }
                    this.client.getSession().write(MTSCSPacket.itemExpired(item.getItemId()));
                    if (GameConstants.is经验卡(item.getItemId())) {}
                    toberemove.add(new Pair<MapleInventoryType, Item>(inv, item));
                }
            }
        }
        for (final Pair<MapleInventoryType, Item> itemz : toberemove) {
            final Item item2 = itemz.getRight();
            ret.add(item2.getItemId());
            this.getInventory(itemz.getLeft()).removeItem(item2.getPosition(), item2.getQuantity(), false);
        }
        for (final Item itemz2 : tobeunlock) {
            itemz2.setExpiration(-1L);
            itemz2.setFlag((byte)(itemz2.getFlag() - ItemFlag.LOCK.getValue()));
        }
        this.pendingExpiration = ret;
        final List<Integer> skilz = new ArrayList<Integer>();
        final List<ISkill> toberem = new ArrayList<ISkill>();
        for (final Entry<ISkill, SkillEntry> skil : this.skills.entrySet()) {
            if (skil.getValue().expiration != -1L && currenttimes > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (final ISkill skil2 : toberem) {
            skilz.add(skil2.getId());
            this.skills.remove(skil2);
        }
        this.pendingSkills = skilz;
    }
    
    public MapleShop getShop() {
        return this.shop;
    }
    
    public void setShop(final MapleShop shop) {
        this.shop = shop;
    }
    
    public int getMeso() {
        return this.meso;
    }
    
    public final int[] getSavedLocations() {
        return this.savedLocations;
    }
    
    public int getSavedLocation(final SavedLocationType type) {
        return this.savedLocations[type.getValue()];
    }
    
    public void saveLocation(final SavedLocationType type) {
        this.savedLocations[type.getValue()] = this.getMapId();
    }
    
    public void saveLocation(final SavedLocationType type, final int mapz) {
        this.savedLocations[type.getValue()] = mapz;
    }
    
    public void clearSavedLocation(final SavedLocationType type) {
        this.savedLocations[type.getValue()] = -1;
    }
    
    public void setmoneyb(final int slot) {
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET moneyb =moneyb+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public int getzb() {
        int money = 0;
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                money = rs.getInt("money");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return money;
    }
    
    public void setzb(final int slot) {
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET money =money+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public void gainMeso(final int gain, final boolean show) {
        this.gainMeso(gain, show, false, false);
    }
    
    public void gainMeso(final int gain, final boolean show, final boolean enableActions) {
        this.gainMeso(gain, show, enableActions, false);
    }
    
    public void gainMeso(final int gain, final boolean show, final boolean enableActions, final boolean inChat) {
        if (this.meso + gain < 0) {
            this.client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        this.meso += gain;
        this.updateSingleStat(MapleStat.MESO, this.meso, enableActions);
        if (show) {
            this.client.getSession().write(MaplePacketCreator.showMesoGain(gain, inChat));
        }
    }
    
    public void controlMonster(final MapleMonster monster, final boolean aggro) {
        if (this.clone) {
            return;
        }
        monster.setController(this);
        this.controlled.add(monster);
        this.client.getSession().write(MobPacket.controlMonster(monster, false, aggro));
    }
    
    public void stopControllingMonster(final MapleMonster monster) {
        if (this.clone) {
            return;
        }
        if (monster != null && this.controlled.contains(monster)) {
            this.controlled.remove(monster);
        }
    }
    
    public void checkMonsterAggro(final MapleMonster monster) {
        if (this.clone || monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        }
        else {
            monster.switchController(this, true);
        }
    }
    
    public Set<MapleMonster> getControlled() {
        return this.controlled;
    }
    
    public int getControlledSize() {
        return this.controlled.size();
    }
    
    public int getAccountID() {
        return this.accountid;
    }
    
    public void mobKilled(final int id, final int skillID) {
        for (final MapleQuestStatus q : this.quests.values()) {
            if (q.getStatus() == 1 && q.hasMobKills() && q.mobKilled(id, skillID)) {
                this.client.getSession().write(MaplePacketCreator.updateQuestMobKills(q));
                if (!q.getQuest().canComplete(this, null)) {
                    continue;
                }
                this.client.getSession().write(MaplePacketCreator.getShowQuestCompletion(q.getQuest().getId()));
            }
        }
    }
    
    public final List<MapleQuestStatus> getStartedQuests() {
        final List ret = new LinkedList();
        for (final MapleQuestStatus q : this.quests.values()) {
            if (q.getStatus() == 1 && !q.isCustom()) {
                ret.add(q);
            }
        }
        return (List<MapleQuestStatus>)ret;
    }
    
    public final List<MapleQuestStatus> getCompletedQuests() {
        final List ret = new LinkedList();
        for (final MapleQuestStatus q : this.quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom()) {
                ret.add(q);
            }
        }
        return (List<MapleQuestStatus>)ret;
    }
    
    public Map<ISkill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap((Map<? extends ISkill, ? extends SkillEntry>)this.skills);
    }
    
    public byte getSkillLevel(final ISkill skill) {
        final SkillEntry ret = (client.SkillEntry)(client.SkillEntry)this.skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return (byte)Math.min(skill.getMaxLevel(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : this.stats.incAllskill));
    }
    
    public byte getMasterLevel(final int skill) {
        return this.getMasterLevel(SkillFactory.getSkill(skill));
    }
    
    public byte getMasterLevel(final ISkill skill) {
        final SkillEntry ret = (client.SkillEntry)(client.SkillEntry)this.skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }
    
    public void levelUp() {
        final int vipAp = this.getVip();
        if (Boolean.parseBoolean(ServerProperties.getProperty("world.VIP能力值", "false"))) {
            this.remainingAp = (short)(this.remainingAp + 5 + vipAp);
        }
        else {
            this.remainingAp += 5;
        }
        int maxhp = this.stats.getMaxHp();
        int maxmp = this.stats.getMaxMp();
        if (this.job == 0 || this.job == 1000 || this.job == 2000 || this.job == 2001 || this.job == 3000) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        }
        else if (this.job >= 100 && this.job <= 132) {
            final ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
            final int slevel = this.getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        }
        else if (this.job >= 200 && this.job <= 232) {
            final ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
            final int slevel = this.getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        }
        else if (this.job >= 3200 && this.job <= 3212) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        }
        else if ((this.job >= 300 && this.job <= 322) || (this.job >= 400 && this.job <= 434) || (this.job >= 1300 && this.job <= 1311) || (this.job >= 1400 && this.job <= 1411) || (this.job >= 3300 && this.job <= 3312)) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        }
        else if ((this.job >= 500 && this.job <= 522) || (this.job >= 3500 && this.job <= 3512)) {
            final ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
            final int slevel = this.getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        }
        else if (this.job >= 1100 && this.job <= 1111) {
            final ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
            final int slevel = this.getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        }
        else if (this.job >= 1200 && this.job <= 1211) {
            final ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
            final int slevel = this.getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        }
        else if (this.job >= 1500 && this.job <= 1512) {
            final ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
            final int slevel = this.getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        }
        else if (this.job >= 2100 && this.job <= 2112) {
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        }
        else if (this.job >= 2200 && this.job <= 2218) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        }
        else {
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += this.stats.getTotalInt() / 10;
        this.exp -= GameConstants.getExpNeededForLevel(this.level);
        ++this.level;
        this.setExp(0);
        final int level = this.getLevel();
        if (!this.isGM() && (level == 10 || level == 30 || level == 70 || level == 120 || level == 200)) {
            final StringBuilder sb = new StringBuilder("[恭喜] ");
            final Item medal = this.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-26));
            if (medal != null) {
                sb.append("<");
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                sb.append("> ");
            }
            sb.append(this.getName());
            sb.append(" 达到了 " + level + " 级,让我们一起恭喜他/她吧!");
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
        }
        maxhp = Math.min(30000, maxhp);
        maxmp = Math.min(30000, maxmp);
        final List statup = new ArrayList(8);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, (int)this.remainingAp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, maxhp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, maxmp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, maxhp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, maxmp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.EXP, this.exp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LEVEL, level));
        if (this.isGM() || (this.job != 0 && this.job != 1000 && this.job != 2000 && this.job != 2001 && this.job != 3000)) {
            final int[] remainingSp = this.remainingSp;
            final int skillBook = GameConstants.getSkillBook(this.job);
            remainingSp[skillBook] += 3;
            this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        }
        else if (level <= 10) {
            this.stats.setStr((short)(this.stats.getStr() + this.remainingAp));
            this.remainingAp = 0;
            statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, (int)this.stats.getStr()));
        }
        statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, (int)this.remainingAp));
        this.stats.setMaxHp((short)maxhp);
        this.stats.setMaxMp((short)maxmp);
        this.stats.setHp((short)maxhp);
        this.stats.setMp((short)maxmp);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this.getJob()));
        this.map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(this.getId(), 0), false);
        this.stats.recalcLocalStats();
        this.silentPartyUpdate();
        this.guildUpdate();
        this.familyUpdate();
        this.saveToDB(false, false);
    }
    
    public void changeKeybinding(final int key, final byte type, final int action) {
        if (type != 0) {
            this.keylayout.Layout().put(key, new Pair<Byte, Integer>(type, action));
        }
        else {
            this.keylayout.Layout().remove(key);
        }
    }
    
    public void sendMacros() {
        for (int i = 0; i < 5; ++i) {
            if (this.skillMacros[i] != null) {
                this.client.getSession().write(MaplePacketCreator.getMacros(this.skillMacros));
                break;
            }
        }
    }
    
    public void updateMacros(final int position, final SkillMacro updateMacro) {
        this.skillMacros[position] = updateMacro;
    }
    
    public final SkillMacro[] getMacros() {
        return this.skillMacros;
    }
    
    public void tempban(final String reason, final Calendar duration, final int greason, final boolean IPMac) {
        if (IPMac) {
            this.client.banMacs();
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            ps.setString(1, this.client.getSession().getRemoteAddress().toString().split(":")[0]);
            ps.execute();
            ps.close();
            this.client.getSession().close();
            ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            final Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, this.accountid);
            ps.execute();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error while tempbanning" + ex);
        }
    }
    
    public final boolean ban(final String reason, final boolean IPMac, final boolean autoban, boolean hellban) {
        hellban = false;
        if (this.lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, autoban ? 2 : 1);
            ps.setString(2, reason);
            ps.setInt(3, this.accountid);
            ps.execute();
            ps.close();
            this.client.banMacs();
            if (hellban) {
                final PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                psa.setInt(1, this.accountid);
                final ResultSet rsa = psa.executeQuery();
                if (rsa.next()) {
                    final PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE email = ? ");
                    pss.setInt(1, autoban ? 2 : 1);
                    pss.setString(2, reason);
                    pss.setString(3, rsa.getString("email"));
                    pss.execute();
                    pss.close();
                }
                rsa.close();
                psa.close();
            }
        }
        catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
            return false;
        }
        this.client.getSession().close();
        return true;
    }
    
    public static boolean ban(final String id, final String reason, final boolean accountId, final int gmlevel, final boolean hellban) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            if (id.matches("/[0-9]{1,3}\\..*")) {
                if (id != "/127.0.0.1") {}
                return true;
            }
            PreparedStatement ps;
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            }
            else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final int z = rs.getInt(1);
                final PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ? AND gm < ?");
                psb.setString(1, reason);
                psb.setInt(2, z);
                psb.setInt(3, gmlevel);
                psb.execute();
                psb.close();
                if (gmlevel > 100) {
                    final PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                    psa.setInt(1, z);
                    final ResultSet rsa = psa.executeQuery();
                    if (rsa.next()) {
                        final String sessionIP = rsa.getString("sessionIP");
                        if (sessionIP != null && sessionIP.matches("/[0-9]{1,3}\\..*")) {
                            final PreparedStatement psz = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                            psz.setString(1, sessionIP);
                            psz.execute();
                            psz.close();
                        }
                        if (rsa.getString("macs") != null) {
                            final String[] macData = rsa.getString("macs").split(", ");
                            if (macData.length > 0) {
                                MapleClient.banMacs(macData);
                            }
                        }
                        if (hellban) {
                            final PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE email = ?" + ((sessionIP == null) ? "" : " OR SessionIP = ?"));
                            pss.setString(1, reason);
                            pss.setString(2, rsa.getString("email"));
                            if (sessionIP != null) {
                                pss.setString(3, sessionIP);
                            }
                            pss.execute();
                            pss.close();
                        }
                    }
                    rsa.close();
                    psa.close();
                }
                ret = true;
            }
            rs.close();
            ps.close();
            return ret;
        }
        catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
            return false;
        }
    }
    
    @Override
    public int getObjectId() {
        return this.getId();
    }
    
    @Override
    public void setObjectId(final int id) {
        throw new UnsupportedOperationException();
    }
    
    public MapleStorage getStorage() {
        return this.storage;
    }
    
    public void addVisibleMapObject(final MapleMapObject mo) {
        if (this.clone) {
            return;
        }
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            this.visibleMapObjects.add(mo);
        }
        finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }
    
    public void removeVisibleMapObject(final MapleMapObject mo) {
        if (this.clone) {
            return;
        }
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            this.visibleMapObjects.remove(mo);
        }
        finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }
    
    public boolean isMapObjectVisible(final MapleMapObject mo) {
        this.visibleMapObjectsLock.readLock().lock();
        try {
            return !this.clone && this.visibleMapObjects.contains(mo);
        }
        finally {
            this.visibleMapObjectsLock.readLock().unlock();
        }
    }
    
    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        this.visibleMapObjectsLock.writeLock().lock();
        return this.visibleMapObjects;
    }
    
    public void unlockWriteVisibleMapObjects() {
        this.visibleMapObjectsLock.writeLock().unlock();
    }
    
    public boolean isAlive() {
        return this.stats.getHp() > 0;
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(this.getObjectId(), this));
        for (final WeakReference chr : this.clones) {
            if (chr.get() != null) {
                ((MapleMapObject)chr.get()).sendDestroyData(client);
            }
        }
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            client.sendPacket(MaplePacketCreator.spawnPlayerMapobject(this));
            if (this.getParty() != null && !this.isClone()) {
                this.updatePartyMemberHP();
                this.receivePartyMemberHP();
            }
            for (final MaplePet pet : this.pets) {
                if (pet.getSummoned()) {
                    client.sendPacket(PetPacket.showPet(this, pet, false, false));
                }
            }
            for (final WeakReference<MapleCharacter> chr : this.clones) {
                if (chr.get() != null) {
                    chr.get().sendSpawnData(client);
                }
            }
            if (this.summons != null) {
                for (final MapleSummon summon : this.summons.values()) {
                    client.sendPacket(MaplePacketCreator.spawnSummon(summon, false));
                }
            }
            if (this.followid > 0) {}
            if (this.party != null) {
                this.updatePartyMemberHP();
            }
        }
    }
    
    public final void equipChanged() {
        this.map.broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        this.map.broadcastMessage(MaplePacketCreator.loveEffect());
        this.stats.recalcLocalStats();
        if (this.getMessenger() != null) {
            Messenger.updateMessenger(this.getMessenger().getId(), this.getName(), this.client.getChannel());
        }
        this.saveToDB(false, false);
    }
    
    public final MaplePet getPet(final int index) {
        byte count = 0;
        for (final MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                ++count;
            }
        }
        return null;
    }
    
    public void addPet(final MaplePet pet) {
        if (this.pets.contains(pet)) {
            this.pets.remove(pet);
        }
        this.pets.add(pet);
    }
    
    public void removePet(final MaplePet pet, final boolean shiftLeft) {
        pet.setSummoned(0);
    }
    
    public final List<MaplePet> getSummonedPets() {
        final List ret = new ArrayList();
        for (int i = 0; i < 3; ++i) {
            ret.add(null);
        }
        for (final MaplePet pet : this.pets) {
            if (pet != null && pet.getSummoned()) {
                final int index = pet.getSummonedValue() - 1;
                ret.remove(index);
                ret.add(index, pet);
            }
        }
        final List nullArr = new ArrayList();
        nullArr.add(null);
        ret.removeAll(nullArr);
        return (List<MaplePet>)ret;
    }
    
    public final MaplePet getSummonedPet(final int index) {
        for (final MaplePet pet : this.getSummonedPets()) {
            if (pet.getSummonedValue() - 1 == index) {
                return pet;
            }
        }
        return null;
    }
    
    public final void shiftPetsRight() {
        final List<MaplePet> petsz = this.getSummonedPets();
        if (petsz.size() >= 3 || petsz.size() < 1) {
            return;
        }
        final boolean[] indexBool = { false, false, false };
        for (int i = 0; i < 3; ++i) {
            for (final MaplePet p : petsz) {
                if (p.getSummonedValue() == i + 1) {
                    indexBool[i] = true;
                }
            }
        }
        if (petsz.size() > 1) {
            if (!indexBool[2]) {
                petsz.get(0).setSummoned(2);
                petsz.get(1).setSummoned(3);
            }
            else if (!indexBool[1]) {
                petsz.get(0).setSummoned(2);
            }
        }
        else if (indexBool[0]) {
            petsz.get(0).setSummoned(2);
        }
    }
    
    public final int getPetSlotNext() {
        final List<MaplePet> petsz = this.getSummonedPets();
        int index = 0;
        if (petsz.size() >= 3) {
            this.unequipPet(this.getSummonedPet(0), false);
        }
        else {
            final boolean[] indexBool = { false, false, false };
            for (int i = 0; i < 3; ++i) {
                for (final MaplePet p : petsz) {
                    if (p.getSummonedValue() == i + 1) {
                        indexBool[i] = true;
                    }
                }
            }
            for (final boolean b : indexBool) {
                if (!b) {
                    break;
                }
                ++index;
            }
            index = Math.min(index, 2);
            for (final MaplePet p2 : petsz) {
                if (p2.getSummonedValue() == index + 1) {
                    this.unequipPet(p2, false);
                }
            }
        }
        return index;
    }
    
    public final byte getPetIndex(final MaplePet petz) {
        return (byte)Math.max(-1, petz.getSummonedValue() - 1);
    }
    
    public final byte getPetIndex(final int petId) {
        for (final MaplePet pet : this.getSummonedPets()) {
            if (pet.getUniqueId() == petId) {
                return (byte)Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }
    
    public final byte getPetIndexById(final int petId) {
        for (final MaplePet pet : this.getSummonedPets()) {
            if (pet.getPetItemId() == petId) {
                return (byte)Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }
    
    public final List<MaplePet> getPets() {
        return this.pets;
    }
    
    public final void unequipAllPets() {
        for (final MaplePet pet : this.getSummonedPets()) {
            this.unequipPet(pet, false);
        }
    }
    
    public void unequipPet(final MaplePet pet, final boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveToDb();
            final List<MaplePet> summonedPets = this.getSummonedPets();
            if (summonedPets.contains(pet)) {
                summonedPets.remove(pet);
                int i = 1;
                for (final MaplePet p : summonedPets) {
                    if (p == null) {
                        continue;
                    }
                    p.setSummoned(i);
                    ++i;
                }
            }
            if (this.map != null) {
                this.map.broadcastMessage(this, PetPacket.showPet(this, pet, true, hunger), true);
            }
            pet.setSummoned(0);
            this.client.sendPacket(PetPacket.petStatUpdate(this));
            this.client.sendPacket(MaplePacketCreator.enableActions());
        }
    }
    
    public final long getLastFameTime() {
        return this.lastfametime;
    }
    
    public final List<Integer> getFamedCharacters() {
        return this.lastmonthfameids;
    }
    
    public FameStatus canGiveFame(final MapleCharacter from) {
        if (this.lastfametime >= System.currentTimeMillis() - 86400000L) {
            return FameStatus.NOT_TODAY;
        }
        if (from == null || this.lastmonthfameids == null || this.lastmonthfameids.contains(from.getId())) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }
    
    public void hasGivenFame(final MapleCharacter to) {
        this.lastfametime = System.currentTimeMillis();
        this.lastmonthfameids.add(to.getId());
        final Connection con = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, this.getId());
            ps.setInt(2, to.getId());
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + this.getName() + " to " + to.getName() + e);
        }
    }
    
    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }
    
    public MapleParty getParty() {
        return this.party;
    }
    
    public int getPartyId() {
        return (this.party != null) ? this.party.getId() : -1;
    }
    
    public byte getWorld() {
        return this.world;
    }
    
    public void setWorld(final byte world) {
        this.world = world;
    }
    
    public void setParty(final MapleParty party) {
        this.party = party;
    }
    
    public MapleTrade getTrade() {
        return this.trade;
    }
    
    public void setTrade(final MapleTrade trade) {
        this.trade = trade;
    }
    
    public EventInstanceManager getEventInstance() {
        return this.eventInstance;
    }
    
    public void setEventInstance(final EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }
    
    public void addDoor(final MapleDoor door) {
        this.doors.add(door);
    }
    
    public void clearDoors() {
        this.doors.clear();
    }
    
    public List<MapleDoor> getDoors() {
        return new ArrayList<MapleDoor>(this.doors);
    }
    
    public void setSmega() {
        if (this.smega) {
            this.smega = false;
            this.dropMessage(5, "You have set megaphone to disabled mode");
        }
        else {
            this.smega = true;
            this.dropMessage(5, "You have set megaphone to enabled mode");
        }
    }
    
    public boolean getSmega() {
        return this.smega;
    }
    
    public Map<Integer, MapleSummon> getSummons() {
        return this.summons;
    }
    
    public int getSummonsSize() {
        return this.summons.size();
    }
    
    public int getChair() {
        return this.chair;
    }
    
    public int getItemEffect() {
        return this.itemEffect;
    }
    
    public void setChair(final int chair) {
        this.chair = chair;
        this.stats.relocHeal();
    }
    
    public void setItemEffect(final int itemEffect) {
        this.itemEffect = itemEffect;
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }
    
    public int getFamilyId() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getFamilyId();
    }
    
    public int getSeniorId() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getSeniorId();
    }
    
    public int getJunior1() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getJunior1();
    }
    
    public int getJunior2() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getJunior2();
    }
    
    public int getCurrentRep() {
        return this.currentrep;
    }
    
    public int getTotalRep() {
        return this.totalrep;
    }
    
    public void setCurrentRep(final int _rank) {
        this.currentrep = _rank;
        if (this.mfc != null) {
            this.mfc.setCurrentRep(_rank);
        }
    }
    
    public void setTotalRep(final int _rank) {
        this.totalrep = _rank;
        if (this.mfc != null) {
            this.mfc.setTotalRep(_rank);
        }
    }
    
    public int getGuildId() {
        return this.guildid;
    }
    
    public byte getGuildRank() {
        return this.guildrank;
    }
    
    public void setGuildId(final int _id) {
        this.guildid = _id;
        if (this.guildid > 0) {
            if (this.mgc == null) {
                this.mgc = new MapleGuildCharacter(this);
            }
            else {
                this.mgc.setGuildId(this.guildid);
            }
        }
        else {
            this.mgc = null;
        }
    }
    
    public void setGuildRank(final byte _rank) {
        this.guildrank = _rank;
        if (this.mgc != null) {
            this.mgc.setGuildRank(_rank);
        }
    }
    
    public MapleGuildCharacter getMGC() {
        return this.mgc;
    }
    
    public void setAllianceRank(final byte rank) {
        this.allianceRank = rank;
        if (this.mgc != null) {
            this.mgc.setAllianceRank(rank);
        }
    }
    
    public byte getAllianceRank() {
        return this.allianceRank;
    }
    
    public MapleGuild getGuild() {
        if (this.getGuildId() <= 0) {
            return null;
        }
        return Guild.getGuild(this.getGuildId());
    }
    
    public void guildUpdate() {
        if (this.guildid <= 0) {
            return;
        }
        this.mgc.setLevel(this.level);
        this.mgc.setJobId(this.job);
        Guild.memberLevelJobUpdate(this.mgc);
    }
    
    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(this.guildid, this.guildrank, this.allianceRank, this.id);
    }
    
    public void familyUpdate() {
        if (this.mfc == null) {
            return;
        }
        Family.memberFamilyUpdate(this.mfc, this);
    }
    
    public void saveFamilyStatus() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?");
            if (this.mfc == null) {
                ps.setInt(1, 0);
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
            }
            else {
                ps.setInt(1, this.mfc.getFamilyId());
                ps.setInt(2, this.mfc.getSeniorId());
                ps.setInt(3, this.mfc.getJunior1());
                ps.setInt(4, this.mfc.getJunior2());
            }
            ps.setInt(5, this.id);
            ps.execute();
            ps.close();
        }
        catch (SQLException se) {
            System.out.println("SQLException: " + se.getLocalizedMessage());
            se.printStackTrace();
        }
    }
    
    public void modifyCSPoints(final int type, final int quantity) {
        this.modifyCSPoints(type, quantity, false);
    }
    
    public void dropMessage(final String message) {
        this.dropMessage(6, message);
    }
    
    public void modifyCSPoints(final int slot) {
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET acash =acash+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public void 抵用卷(final int slot) {
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET mPoints =mPoints+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public int modifyCSPoints() {
        int money = 0;
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                money = rs.getInt("acash");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return money;
    }
    
    public int 抵用卷() {
        int money = 0;
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                money = rs.getInt("mPoints");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return money;
    }
    
    public void modifyCSPoints(final int type, final int quantity, final boolean show) {
        switch (type) {
            case 1: {
                if (this.modifyCSPoints() + quantity < 0) {
                    if (show) {
                        this.dropMessage(5, "你的点卷已经满了");
                    }
                    return;
                }
                this.modifyCSPoints(quantity);
                break;
            }
            case 2: {
                if (this.抵用卷() + quantity < 0) {
                    if (show) {
                        this.dropMessage(5, "你的抵用卷已经满了.");
                    }
                    return;
                }
                this.抵用卷(quantity);
                break;
            }
        }
        if (show && quantity != 0) {
            this.dropMessage(5, "你已经 " + ((quantity > 0) ? "获得 " : "使用 ") + quantity + ((type == 1) ? " 点卷." : " 抵用卷."));
        }
    }
    
    public int getNX() {
        return this.getCSPoints(1);
    }
    
    public int getCSPoints(final int type) {
        switch (type) {
            case 1: {
                return this.modifyCSPoints();
            }
            case 2: {
                return this.抵用卷();
            }
            default: {
                return 0;
            }
        }
    }
    
    public final boolean hasEquipped(final int itemid) {
        return this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }
    
    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = this.inventory[type.ordinal()].countById(itemid);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        }
        return possesed == quantity;
    }
    
    public final boolean haveItem(final int itemid, final int quantity) {
        return this.haveItem(itemid, quantity, true, true);
    }
    
    public final boolean haveItem(final int itemid) {
        return this.haveItem(itemid, 1, true, true);
    }
    
    public void maxAllSkills() {
        final MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wz") + "/" + "String.wz"));
        final MapleData skilldData = dataProvider.getData("Skill.img");
        for (final MapleData skill_ : skilldData.getChildren()) {
            try {
                final Skill skill = (Skill)SkillFactory.getSkill1(Integer.parseInt(skill_.getName()));
                if (this.level < 0) {
                    continue;
                }
                this.changeSkillLevel(skill, skill.getMaxLevel(), skill.getMaxLevel());
            }
            catch (NumberFormatException nfe) {
                break;
            }
            catch (NullPointerException ex) {}
        }
    }
    
    public void setAPQScore(final int score) {
        this.APQScore = score;
    }
    
    public int getAPQScore() {
        return this.APQScore;
    }
    
    public long getLasttime() {
        return this.lasttime;
    }
    
    public void setLasttime(final long lasttime) {
        this.lasttime = lasttime;
    }
    
    public long getCurrenttime() {
        return this.currenttime;
    }
    
    public void setCurrenttime(final long currenttime) {
        this.currenttime = currenttime;
    }
    
    public byte getBuddyCapacity() {
        return this.buddylist.getCapacity();
    }
    
    public void setBuddyCapacity(final byte capacity) {
        this.buddylist.setCapacity(capacity);
        this.client.getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
    }
    
    public MapleMessenger getMessenger() {
        return this.messenger;
    }
    
    public void setMessenger(final MapleMessenger messenger) {
        this.messenger = messenger;
    }
    
    public void addCooldown(final int skillId, final long startTime, final long length) {
        this.coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length));
    }
    
    public void removeCooldown(final int skillId) {
        if (this.coolDowns.containsKey(skillId)) {
            this.coolDowns.remove(skillId);
        }
    }
    
    public boolean skillisCooling(final int skillId) {
        return this.coolDowns.containsKey(skillId);
    }
    
    public void giveCoolDowns(final int skillid, final long starttime, final long length) {
        this.addCooldown(skillid, starttime, length);
    }
    
    public void giveCoolDowns(final List<MapleCoolDownValueHolder> cooldowns) {
        if (cooldowns != null) {
            for (final MapleCoolDownValueHolder cooldown : cooldowns) {
                this.coolDowns.put(cooldown.skillId, cooldown);
            }
        }
        else {
            try {
                final Connection con = DatabaseConnection.getConnection();
                final PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?");
                ps.setInt(1, this.getId());
                final ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() > 0L) {
                        this.giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                    }
                }
                ps.close();
                rs.close();
                this.deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");
            }
            catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            }
        }
    }
    
    public List<MapleCoolDownValueHolder> getCooldowns() {
        return new ArrayList<MapleCoolDownValueHolder>(this.coolDowns.values());
    }
    
    public final List<MapleDiseaseValueHolder> getAllDiseases() {
        return new ArrayList<MapleDiseaseValueHolder>(this.diseases.values());
    }
    
    public final boolean hasDisease(final MapleDisease dis) {
        return this.diseases.keySet().contains(dis);
    }
    
    public void giveDebuff(final MapleDisease disease, final MobSkill skill) {
        this.giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel());
    }
    
    public void giveDebuff(final MapleDisease disease, final int x, final long duration, final int skillid, final int level) {
        final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<MapleDisease, Integer>(disease, x));
        if (!this.hasDisease(disease) && this.diseases.size() < 2) {
            if (disease != MapleDisease.SEDUCE && disease != MapleDisease.STUN && this.isActiveBuffedValue(2321005)) {
                return;
            }
            this.diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), duration));
            this.client.getSession().write(MaplePacketCreator.giveDebuff(debuff, skillid, level, (int)duration));
            this.map.broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(this.id, debuff, skillid, level), false);
        }
    }
    
    public final void giveSilentDebuff(final List<MapleDiseaseValueHolder> ld) {
        if (ld != null) {
            for (final MapleDiseaseValueHolder disease : ld) {
                this.diseases.put(disease.disease, disease);
            }
        }
    }
    
    public void dispelDebuff(final MapleDisease debuff) {
        if (this.hasDisease(debuff)) {
            final long mask = debuff.getValue();
            final boolean first = debuff.isFirst();
            this.client.getSession().write(MaplePacketCreator.cancelDebuff(mask, first));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(this.id, mask, first), false);
            this.diseases.remove(debuff);
        }
    }
    
    public void dispelDebuffs() {
        this.dispelDebuff(MapleDisease.CURSE);
        this.dispelDebuff(MapleDisease.DARKNESS);
        this.dispelDebuff(MapleDisease.POISON);
        this.dispelDebuff(MapleDisease.SEAL);
        this.dispelDebuff(MapleDisease.WEAKEN);
    }
    
    public void cancelAllDebuffs() {
        this.diseases.clear();
    }
    
    public void setLevel(final short level) {
        this.level = (short)(level - 1);
    }
    
    public void sendNote(final String to, final String msg) {
        this.sendNote(to, msg, 0);
    }
    
    public void sendNote(final String to, final String msg, final int fame) {
        MapleCharacterUtil.sendNote(to, this.getName(), msg, fame);
    }
    
    public void showNote() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", 1005, 1008);
            ps.setString(1, this.getName());
            final ResultSet rs = ps.executeQuery();
            rs.last();
            final int count = rs.getRow();
            rs.first();
            this.client.getSession().write(MTSCSPacket.showNotes(rs, count));
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        }
    }
    
    public void deleteNote(final int id, final int fame) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            final ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("gift") == fame && fame > 0) {
                this.addFame(fame);
                this.updateSingleStat(MapleStat.FAME, this.getFame());
                this.client.getSession().write(MaplePacketCreator.getShowFameGain(fame));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Unable to delete note" + e);
        }
    }
    
    public void mulung_EnergyModify(final boolean inc) {
        if (inc) {
            if (this.mulung_energy + 100 > 10000) {
                this.mulung_energy = 10000;
            }
            else {
                this.mulung_energy += 100;
            }
        }
        else {
            this.mulung_energy = 0;
        }
    }
    
    public void writeMulungEnergy() {
    }
    
    public void writeEnergy(final String type, final String inc) {
    }
    
    public void writeStatus(final String type, final String inc) {
    }
    
    public void writePoint(final String type, final String inc) {
    }
    
    public final short getCombo() {
        return this.combo;
    }
    
    public void setCombo(final short combo) {
        this.combo = combo;
    }
    
    public final long getLastCombo() {
        return this.lastCombo;
    }
    
    public void setLastCombo(final long combo) {
        this.lastCombo = combo;
    }
    
    public final long getKeyDownSkill_Time() {
        return this.keydown_skill;
    }
    
    public void setKeyDownSkill_Time(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }
    
    public void checkBerserk() {
        if (this.BerserkSchedule != null) {
            this.BerserkSchedule.cancel(false);
        }
        final ISkill BerserkX = SkillFactory.getSkill(1320006);
        final int skilllevel = this.getSkillLevel(BerserkX);
        if (skilllevel >= 1) {
            final MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            this.stats.Berserk = (this.stats.getHp() * 100 / this.stats.getMaxHp() < BerserkX.getEffect(skilllevel).getX());
            this.client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1320006, 1, (byte)(this.stats.Berserk ? 1 : 0)));
            this.map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(this.getId(), 1320006, 1, (byte)(this.stats.Berserk ? 1 : 0)), false);
            this.BerserkSchedule = BuffTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    MapleCharacter.this.checkBerserk();
                }
            }, 10000L);
        }
    }
    
    private void prepareBeholderEffect() {
        if (this.beholderHealingSchedule != null) {
            this.beholderHealingSchedule.cancel(false);
        }
        if (this.beholderBuffSchedule != null) {
            this.beholderBuffSchedule.cancel(false);
        }
        final ISkill bHealing = SkillFactory.getSkill(1320008);
        final int bHealingLvl = this.getSkillLevel(bHealing);
        final int berserkLvl = this.getSkillLevel(SkillFactory.getSkill(1320006));
        if (bHealingLvl > 0) {
            final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
            final int healInterval = healEffect.getX() * 15000;
            this.beholderHealingSchedule = BuffTimer.getInstance().register(new Runnable() {
                @Override
                public void run() {
                    final int remhppercentage = (int)Math.ceil(MapleCharacter.this.getStat().getHp() * 100.0 / MapleCharacter.this.getStat().getMaxHp());
                    if (berserkLvl == 0 || remhppercentage >= berserkLvl + 10) {
                        MapleCharacter.this.addHP(healEffect.getHp());
                    }
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1321007, 2));
                    map.broadcastMessage(MaplePacketCreator.summonSkill(MapleCharacter.this.getId(), 1321007, 5));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(MapleCharacter.this.getId(), 1321007, 2), false);
                }
            }, healInterval, healInterval);
        }
        final ISkill bBuff = SkillFactory.getSkill(1320009);
        final int bBuffLvl = this.getSkillLevel(bBuff);
        if (bBuffLvl > 0) {
            final MapleStatEffect buffEffect = bBuff.getEffect(bBuffLvl);
            final int buffInterval = buffEffect.getX() * 10000;
            this.beholderBuffSchedule = BuffTimer.getInstance().register(new Runnable() {
                @Override
                public void run() {
                    buffEffect.applyTo(MapleCharacter.this);
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1321007, 2));
                    map.broadcastMessage(MaplePacketCreator.summonSkill(MapleCharacter.this.getId(), 1321007, Randomizer.nextInt(3) + 6));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(MapleCharacter.this.getId(), 1321007, 2), false);
                }
            }, buffInterval, buffInterval);
        }
    }
    
    public void setChalkboard(final String text) {
        this.chalktext = text;
        this.map.broadcastMessage(MTSCSPacket.useChalkboard(this.getId(), text));
    }
    
    public String getChalkboard() {
        return this.chalktext;
    }
    
    public MapleMount getMount() {
        return this.mount;
    }
    
    public int[] getWishlist() {
        return this.wishlist;
    }
    
    public void clearWishlist() {
        for (int i = 0; i < 10; ++i) {
            this.wishlist[i] = 0;
        }
    }
    
    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; ++i) {
            if (this.wishlist[i] > 0) {
                ++ret;
            }
        }
        return ret;
    }
    
    public void setWishlist(final int[] wl) {
        this.wishlist = wl;
    }
    
    public int[] getRocks() {
        return this.rocks;
    }
    
    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; ++i) {
            if (this.rocks[i] != 999999999) {
                ++ret;
            }
        }
        return ret;
    }
    
    public void deleteFromRocks(final int map) {
        for (int i = 0; i < 10; ++i) {
            if (this.rocks[i] == map) {
                this.rocks[i] = 999999999;
                break;
            }
        }
    }
    
    public void addRockMap() {
        if (this.getRockSize() >= 10) {
            return;
        }
        this.rocks[this.getRockSize()] = this.getMapId();
    }
    
    public boolean isRockMap(final int id) {
        for (int i = 0; i < 10; ++i) {
            if (this.rocks[i] == id) {
                return true;
            }
        }
        return false;
    }
    
    public int[] getRegRocks() {
        return this.regrocks;
    }
    
    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; ++i) {
            if (this.regrocks[i] != 999999999) {
                ++ret;
            }
        }
        return ret;
    }
    
    public void deleteFromRegRocks(final int map) {
        for (int i = 0; i < 5; ++i) {
            if (this.regrocks[i] == map) {
                this.regrocks[i] = 999999999;
                break;
            }
        }
    }
    
    public void addRegRockMap() {
        if (this.getRegRockSize() >= 5) {
            return;
        }
        this.regrocks[this.getRegRockSize()] = this.getMapId();
    }
    
    public boolean isRegRockMap(final int id) {
        for (int i = 0; i < 5; ++i) {
            if (this.regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }
    
    public List<LifeMovementFragment> getLastRes() {
        return this.lastres;
    }
    
    public void setLastRes(final List<LifeMovementFragment> lastres) {
        this.lastres = lastres;
    }
    
    public void setMonsterBookCover(final int bookCover) {
        this.bookCover = bookCover;
    }
    
    public int getMonsterBookCover() {
        return this.bookCover;
    }
    
    public int getOneTimeLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            final PreparedStatement ps = con1.prepareStatement("select count(*) from onetimelog where characterid = ? and log = ?");
            ps.setInt(1, this.id);
            ps.setString(2, bossid);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            }
            else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public void setOneTimeLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con1.prepareStatement("insert into onetimelog (characterid, log) values (?,?)");
            ps.setInt(1, this.id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception ex) {}
    }
    
    public int getBossLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            final PreparedStatement ps = con1.prepareStatement("select count(*) from bosslog where characterid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, this.id);
            ps.setString(2, bossid);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            }
            else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public void setBossLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con1.prepareStatement("insert into bosslog (characterid, bossid) values (?,?)");
            ps.setInt(1, this.id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception ex) {}
    }
    
    public void setPrizeLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con1.prepareStatement("insert into Prizelog (accid, bossid) values (?,?)");
            ps.setInt(1, this.getClient().getAccID());
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception ex) {}
    }
    
    public int getPrizeLog(final String bossid) {
        final Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            final PreparedStatement ps = con1.prepareStatement("select count(*) from Prizelog where accid = ? and bossid = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setString(2, bossid);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            }
            else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public void 黄色公告(final String message) {
        this.client.getSession().write(UIPacket.getTopMsg(message));
    }
    
    public void dropTopMsg(final String message) {
        this.client.getSession().write(UIPacket.getTopMsg(message));
    }
    
    public void dropMessage(final int type, final String message) {
        if (type == -2) {
            this.client.getSession().write(PlayerShopPacket.shopChat(message, 0));
        }
        else {
            this.client.getSession().write(MaplePacketCreator.serverNotice(type, message));
        }
    }
    
    public IMaplePlayerShop getPlayerShop() {
        return this.playerShop;
    }
    
    public void setPlayerShop(final IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }
    
    public int getConversation() {
        return this.inst.get();
    }
    
    public void setConversation(final int inst) {
        this.inst.set(inst);
    }
    
    public MapleCarnivalParty getCarnivalParty() {
        return this.carnivalParty;
    }
    
    public void setCarnivalParty(final MapleCarnivalParty party) {
        this.carnivalParty = party;
    }
    
    public void addCP(final int ammount) {
        this.totalCP += (short)ammount;
        this.availableCP += (short)ammount;
    }
    
    public void useCP(final int ammount) {
        this.availableCP -= (short)ammount;
    }
    
    public int getAvailableCP() {
        return this.availableCP;
    }
    
    public int getTotalCP() {
        return this.totalCP;
    }
    
    public void resetCP() {
        this.totalCP = 0;
        this.availableCP = 0;
    }
    
    public void addCarnivalRequest(final MapleCarnivalChallenge request) {
        this.pendingCarnivalRequests.add(request);
    }
    
    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return this.pendingCarnivalRequests.pollLast();
    }
    
    public void clearCarnivalRequests() {
        this.pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();
    }
    
    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        this.client.getSession().write(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }
    
    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        this.client.getSession().write(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }
    
    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        this.client.getSession().write(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }
    
    public boolean getCanTalk() {
        return this.canTalk;
    }
    
    public void canTalk(final boolean talk) {
        this.canTalk = talk;
    }
    
    public int getHp() {
        return this.stats.hp;
    }
    
    public void setHp(final int hp) {
        this.stats.setHp(hp);
    }
    
    public int getMp() {
        return this.stats.mp;
    }
    
    public void setMp(final int mp) {
        this.stats.setMp(mp);
    }
    
    public int getStr() {
        return this.stats.str;
    }
    
    public int getDex() {
        return this.stats.dex;
    }
    
    public int getLuk() {
        return this.stats.luk;
    }
    
    public int getInt() {
        return this.stats.int_;
    }
    
    public int getEXPMod() {
        return this.stats.expMod;
    }
    
    public int getDropMod() {
        return this.stats.dropMod;
    }
    
    public int getCashMod() {
        return this.stats.cashMod;
    }
    
    public void setPoints(final int p) {
        this.points = p;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public void setVPoints(final int p) {
        this.vpoints = p;
    }
    
    public int getVPoints() {
        return this.vpoints;
    }
    
    public CashShop getCashInventory() {
        return this.cs;
    }
    
    public void removeAll(final int id) {
        this.removeAll(id, true, false);
    }
    
    public void removeAll(final int id, final boolean show, final boolean checkEquipped) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = this.getInventory(type).countById(id);
        if (possessed > 0) {
            MapleInventoryManipulator.removeById(this.getClient(), type, id, possessed, true, false);
            if (show) {
                this.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short)(-possessed), true));
            }
        }
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            type = MapleInventoryType.EQUIPPED;
            possessed = this.getInventory(type).countById(id);
            if (possessed > 0) {
                MapleInventoryManipulator.removeById(this.getClient(), type, id, possessed, true, false);
                if (show) {
                    this.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short)(-possessed), true));
                }
                this.equipChanged();
            }
        }
    }
    
    public Pair<List<MapleRing>, List<MapleRing>> getRings(final boolean equip) {
        MapleInventory iv = this.getInventory(MapleInventoryType.EQUIPPED);
        final Collection<Item> equippedC = iv.list();
        final List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (final Item item : equippedC) {
            equipped.add(item);
        }
        Collections.sort(equipped);
        final List<MapleRing> crings = new ArrayList<MapleRing>();
        final List<MapleRing> frings = new ArrayList<MapleRing>();
        for (final Item item2 : equipped) {
            if (item2.getRing() != null) {
                final MapleRing ring = item2.getRing();
                ring.setEquipped(true);
                if (!GameConstants.isFriendshipRing(item2.getItemId()) && !GameConstants.isCrushRing(item2.getItemId())) {
                    continue;
                }
                if (equip) {
                    if (GameConstants.isCrushRing(item2.getItemId())) {
                        crings.add(ring);
                    }
                    else {
                        if (!GameConstants.isFriendshipRing(item2.getItemId())) {
                            continue;
                        }
                        frings.add(ring);
                    }
                }
                else if (crings.isEmpty() && GameConstants.isCrushRing(item2.getItemId())) {
                    crings.add(ring);
                }
                else {
                    if (!frings.isEmpty() || !GameConstants.isFriendshipRing(item2.getItemId())) {
                        continue;
                    }
                    frings.add(ring);
                }
            }
        }
        if (equip) {
            iv = this.getInventory(MapleInventoryType.EQUIP);
            for (final Item item2 : iv.list()) {
                if (item2.getRing() != null && GameConstants.isEffectRing(item2.getItemId())) {
                    final MapleRing ring = item2.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item2.getItemId())) {
                        frings.add(ring);
                    }
                    else {
                        if (!GameConstants.isCrushRing(item2.getItemId())) {
                            continue;
                        }
                        crings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new RingComparator());
        Collections.sort(crings, new RingComparator());
        return new Pair<List<MapleRing>, List<MapleRing>>(crings, frings);
    }
    
    public int getFH() {
        final MapleFoothold fh = this.getMap().getFootholds().findBelow(this.getPosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }
    
    public void startFairySchedule(final boolean exp) {
        this.startFairySchedule(exp, false);
    }
    
    public void startFairySchedule(final boolean exp, final boolean equipped) {
        this.cancelFairySchedule(exp);
        if (this.fairyExp < 30 && this.stats.equippedFairy) {
            if (equipped) {
                this.dropMessage(5, "佩戴道具经验将会在1小时候之后增加 " + this.fairyExp + "% ");
            }
            this.fairySchedule = EtcTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (fairyExp < 30 && stats.equippedFairy) {
                        fairyExp = 30;
                        MapleCharacter.this.dropMessage(5, "佩戴道具经验增加 " + fairyExp + "%.");
                        MapleCharacter.this.startFairySchedule(false, true);
                    }
                    else {
                        MapleCharacter.this.cancelFairySchedule(!stats.equippedFairy);
                    }
                }
            }, 3600000L);
        }
        else if (this.fairyExp < 50 && this.stats.equippedFairy) {
            this.fairySchedule = EtcTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (fairyExp < 50 && stats.equippedFairy) {
                        fairyExp = 50;
                        MapleCharacter.this.dropMessage(5, "佩戴道具经验增加 " + fairyExp + "%.");
                        MapleCharacter.this.startFairySchedule(false, true);
                    }
                    else {
                        MapleCharacter.this.cancelFairySchedule(!stats.equippedFairy);
                    }
                }
            }, 7200000L);
        }
        else if (this.fairyExp < 100 && this.stats.equippedFairy) {
            if (equipped) {
                this.dropMessage(5, "佩戴道具经验会在3小时之后增加 " + this.fairyExp + "% ");
            }
            this.fairySchedule = EtcTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (fairyExp < 100 && stats.equippedFairy) {
                        fairyExp = 100;
                        MapleCharacter.this.dropMessage(5, "佩戴道具经验增加 " + fairyExp + "%.");
                        MapleCharacter.this.startFairySchedule(false, true);
                    }
                    else {
                        MapleCharacter.this.cancelFairySchedule(!stats.equippedFairy);
                    }
                }
            }, 10800000L);
        }
        else {
            this.cancelFairySchedule(!this.stats.equippedFairy);
        }
    }
    
    public void cancelFairySchedule(final boolean exp) {
        if (this.fairySchedule != null) {
            this.fairySchedule.cancel(false);
            this.fairySchedule = null;
        }
        if (exp) {
            this.fairyExp = 30;
        }
    }
    
    public byte getFairyExp() {
        return this.fairyExp;
    }
    
    public int getCoconutTeam() {
        return this.coconutteam;
    }
    
    public void setCoconutTeam(final int team) {
        this.coconutteam = team;
    }
    
    public void spawnPet(final byte slot) {
        this.spawnPet(slot, false, true);
    }
    
    public void spawnPet(final byte slot, final boolean lead) {
        this.spawnPet(slot, lead, true);
    }
    
    public void spawnPet(final byte slot, final boolean lead, final boolean broadcast) {
        final Item item = this.getInventory(MapleInventoryType.CASH).getItem(slot);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (item == null || item.getItemId() > 5010000 || item.getItemId() < 5000000) {
            return;
        }
        switch (item.getItemId()) {
            case 5000028:
            case 5000047: {
                final MaplePet pet = MaplePet.createPet(item.getItemId() + 1, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(this.client, item.getItemId() + 1, (short)1, item.getOwner(), pet, 45L, (byte)0);
                    MapleInventoryManipulator.removeFromSlot(this.client, MapleInventoryType.CASH, slot, (short)1, false);
                    break;
                }
                break;
            }
            default: {
                final MaplePet pet = item.getPet();
                if (pet == null || (item.getItemId() == 5000054 && pet.getSecondsLeft() <= 0) || (item.getExpiration() != -1L && item.getExpiration() <= System.currentTimeMillis())) {
                    break;
                }
                if (pet.getSummoned()) {
                    this.unequipPet(pet, false);
                    break;
                }
                int leadid = 8;
                if (GameConstants.isKOC(this.getJob())) {
                    leadid = 10000018;
                }
                else if (GameConstants.isAran(this.getJob())) {
                    leadid = 20000024;
                }
                if (this.getSkillLevel(SkillFactory.getSkill(leadid)) == 0 && this.getPet(0) != null) {
                    this.unequipPet(this.getPet(0), false);
                }
                else if (lead) {
                    this.shiftPetsRight();
                }
                final Point pos = this.getPosition();
                pet.setPos(pos);
                try {
                    pet.setFh(this.getMap().getFootholds().findBelow(pos).getId());
                }
                catch (NullPointerException e) {
                    pet.setFh(0);
                }
                pet.setStance(0);
                pet.setSummoned(this.getPetSlotNext() + 1);
                this.addPet(pet);
                if (broadcast) {
                    this.getMap().broadcastMessage(this, PetPacket.showPet(this, pet, false, false), true);
                    this.client.sendPacket(PetPacket.petStatUpdate(this));
                }
                break;
            }
        }
        this.client.sendPacket(PetPacket.emptyStatUpdate());
    }
    
    public void addMoveMob(final int mobid) {
        if (this.movedMobs.containsKey(mobid)) {
            this.movedMobs.put(mobid, this.movedMobs.get(mobid) + 1);
            if (this.movedMobs.get(mobid) > 30) {
                for (final MapleCharacter chr : this.getMap().getCharactersThreadsafe()) {
                    if (chr.getMoveMobs().containsKey(mobid)) {
                        chr.getClient().getSession().write(MobPacket.killMonster(mobid, 1));
                        chr.getMoveMobs().remove(mobid);
                    }
                }
            }
        }
        else {
            this.movedMobs.put(mobid, 1);
        }
    }
    
    public Map<Integer, Integer> getMoveMobs() {
        return this.movedMobs;
    }
    
    public int getLinkMid() {
        return this.linkMid;
    }
    
    public void setLinkMid(final int lm) {
        this.linkMid = lm;
    }
    
    public boolean isClone() {
        return this.clone;
    }
    
    public void setClone(final boolean c) {
        this.clone = c;
    }
    
    public WeakReference<MapleCharacter>[] getClones() {
        return this.clones;
    }
    
    public MapleCharacter cloneLooks() {
        final MapleClient cs = new MapleClient(null, null, new MapleSession((Channel)new MockIOSession()));
        final int minus = this.getId() + Randomizer.nextInt(this.getId());
        final MapleCharacter ret = new MapleCharacter(true);
        ret.id = minus;
        ret.client = cs;
        ret.exp = 0;
        ret.meso = 0;
        ret.beans = this.beans;
        ret.blood = this.blood;
        ret.month = this.month;
        ret.day = this.day;
        ret.charmessage = this.charmessage;
        ret.expression = this.expression;
        ret.constellation = this.constellation;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = this.client.getAccID();
        ret.name = this.name;
        ret.level = this.level;
        ret.fame = this.fame;
        ret.job = this.job;
        ret.hair = this.hair;
        ret.face = this.face;
        ret.skinColor = this.skinColor;
        ret.bookCover = this.bookCover;
        ret.monsterbook = this.monsterbook;
        ret.mount = this.mount;
        ret.CRand = new PlayerRandomStream();
        ret.gmLevel = this.gmLevel;
        ret.gender = this.gender;
        ret.mapid = this.map.getId();
        ret.map = this.map;
        ret.setStance(this.getStance());
        ret.chair = this.chair;
        ret.itemEffect = this.itemEffect;
        ret.guildid = this.guildid;
        ret.currentrep = this.currentrep;
        ret.totalrep = this.totalrep;
        ret.stats = this.stats;
        ret.effects.putAll(this.effects);
        if (ret.effects.get(MapleBuffStat.ILLUSION) != null) {
            ret.effects.remove(MapleBuffStat.ILLUSION);
        }
        if (ret.effects.get(MapleBuffStat.SUMMON) != null) {
            ret.effects.remove(MapleBuffStat.SUMMON);
        }
        if (ret.effects.get(MapleBuffStat.REAPER) != null) {
            ret.effects.remove(MapleBuffStat.REAPER);
        }
        if (ret.effects.get(MapleBuffStat.PUPPET) != null) {
            ret.effects.remove(MapleBuffStat.PUPPET);
        }
        ret.guildrank = this.guildrank;
        ret.allianceRank = this.allianceRank;
        ret.hidden = this.hidden;
        ret.setPosition(new Point(this.getPosition()));
        for (final Item equip : this.getInventory(MapleInventoryType.EQUIPPED)) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
        ret.skillMacros = this.skillMacros;
        ret.keylayout = this.keylayout;
        ret.questinfo = this.questinfo;
        ret.savedLocations = this.savedLocations;
        ret.wishlist = this.wishlist;
        ret.rocks = this.rocks;
        ret.regrocks = this.regrocks;
        ret.buddylist = this.buddylist;
        ret.keydown_skill = 0L;
        ret.lastmonthfameids = this.lastmonthfameids;
        ret.lastfametime = this.lastfametime;
        ret.storage = this.storage;
        ret.cs = this.cs;
        ret.client.setAccountName(this.client.getAccountName());
        ret.lastGainHM = this.lastGainHM;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        System.out.println("cloneLooks输出：" + this.client.getChannel());
        while (this.map.getCharacterById(ret.id) != null || this.client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null) {
            final MapleCharacter mapleCharacter = ret;
            ++mapleCharacter.id;
        }
        ret.client.setPlayer(ret);
        return ret;
    }
    
    public final void cloneLook() {
        if (this.clone) {
            return;
        }
        for (int i = 0; i < this.clones.length; ++i) {
            if (this.clones[i].get() == null) {
                final MapleCharacter newp = this.cloneLooks();
                this.map.addPlayer(newp);
                this.map.broadcastMessage(MaplePacketCreator.updateCharLook(newp));
                this.map.movePlayer(newp, this.getPosition());
                this.clones[i] = new WeakReference<MapleCharacter>(newp);
                return;
            }
        }
    }
    
    public final void disposeClones() {
        this.numClones = 0;
        for (int i = 0; i < this.clones.length; ++i) {
            if (this.clones[i].get() != null) {
                this.map.removePlayer(this.clones[i].get());
                this.clones[i].get().getClient().disconnect(false, false);
                this.clones[i] = new WeakReference<MapleCharacter>(null);
                ++this.numClones;
            }
        }
    }
    
    public final int getCloneSize() {
        int z = 0;
        for (int i = 0; i < this.clones.length; ++i) {
            if (this.clones[i].get() != null) {
                ++z;
            }
        }
        return z;
    }
    
    public void spawnClones() {
        if (this.numClones == 0 && this.stats.hasClone) {
            this.cloneLook();
        }
        for (int i = 0; i < this.numClones; ++i) {
            this.cloneLook();
        }
        this.numClones = 0;
    }
    
    public byte getNumClones() {
        return this.numClones;
    }
    
    public void setDragon(final MapleDragon d) {
        this.dragon = d;
    }
    
    public final void spawnSavedPets() {
        for (int i = 0; i < this.petStore.length; ++i) {
            if (this.petStore[i] > -1) {
                this.spawnPet(this.petStore[i], false, false);
            }
        }
        this.client.sendPacket(PetPacket.petStatUpdate(this));
        this.petStore = new byte[] { -1, -1, -1 };
    }
    
    public final byte[] getPetStores() {
        return this.petStore;
    }
    
    public void resetStats(final int str, final int dex, final int int_, final int luk) {
        final List stat = new ArrayList(2);
        int total = this.stats.getStr() + this.stats.getDex() + this.stats.getLuk() + this.stats.getInt() + this.getRemainingAp();
        total -= str;
        this.stats.setStr((short)str);
        total -= dex;
        this.stats.setDex((short)dex);
        total -= int_;
        this.stats.setInt((short)int_);
        total -= luk;
        this.stats.setLuk((short)luk);
        this.setRemainingAp((short)total);
        stat.add(new Pair<MapleStat, Integer>(MapleStat.STR, str));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.DEX, dex));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.INT, int_));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.LUK, luk));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, total));
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this.getJob()));
    }
    
    public Event_PyramidSubway getPyramidSubway() {
        return this.pyramidSubway;
    }
    
    public void setPyramidSubway(final Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }
    
    public byte getSubcategory() {
        if (this.job >= 430 && this.job <= 434) {
            return 1;
        }
        return this.subcategory;
    }
    
    public int itemQuantity(final int itemid) {
        return this.getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }
    
    public void setRPS(final RockPaperScissors rps) {
        this.rps = rps;
    }
    
    public RockPaperScissors getRPS() {
        return this.rps;
    }
    
    public long getNextConsume() {
        return this.nextConsume;
    }
    
    public void setNextConsume(final long nc) {
        this.nextConsume = nc;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getRankMove() {
        return this.rankMove;
    }
    
    public int getJobRank() {
        return this.jobRank;
    }
    
    public int getJobRankMove() {
        return this.jobRankMove;
    }
    
    public void changeChannel(final int channel) {
        final Integer energyLevel = this.getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
        if (energyLevel != null && energyLevel > 0) {
            this.setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
            final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, energyLevel));
            this.client.getSession().write(MaplePacketCreator.能量条(stat, 0));
        }
        final String[] socket = this.client.getChannelServer().getIP().split(":");
        final ChannelServer toch = ChannelServer.getInstance(channel);
        if (channel == this.client.getChannel() || toch == null || toch.isShutdown()) {
            this.client.getSession().write(MaplePacketCreator.serverBlocked(1));
            return;
        }
        this.changeRemoval();
        final ChannelServer ch = ChannelServer.getInstance(this.client.getChannel());
        if (this.getMessenger() != null) {
            Messenger.silentLeaveMessenger(this.getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(this.getId(), this.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(this.getId(), this.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(this.getId(), this.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(this), this.getId(), channel);
        ch.removePlayer(this);
        this.client.updateLoginState(6, this.client.getSessionIPAddress());
        final String s = this.client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf(47) + 1, s.length()));
        try {
            this.client.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(toch.getIP().split(":")[1])));
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(MapleCharacter.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.saveToDB(false, false);
        this.getMap().removePlayer(this);
        this.client.setPlayer(null);
        this.client.setReceiving(false);
        this.expirationTask(true);
    }
    
    public void expandInventory(final byte type, final int amount) {
        final MapleInventory inv = this.getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte)amount);
    }
    
    public boolean allowedToTarget(final MapleCharacter other) {
        return other != null && (!other.isHidden() || this.getGMLevel() >= other.getGMLevel());
    }
    
    public int getFollowId() {
        return this.followid;
    }
    
    public void setFollowId(final int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }
    
    public void setFollowInitiator(final boolean fi) {
        this.followinitiator = fi;
    }
    
    public void setFollowOn(final boolean fi) {
        this.followon = fi;
    }
    
    public boolean isFollowOn() {
        return this.followon;
    }
    
    public boolean isFollowInitiator() {
        return this.followinitiator;
    }
    
    public void checkFollow() {
        if (this.followon) {
            final MapleCharacter tt = this.map.getCharacterById(this.followid);
            if (tt != null) {
                tt.setFollowId(0);
            }
            this.setFollowId(0);
        }
    }
    
    public int getMarriageId() {
        return this.marriageId;
    }
    
    public void setMarriageId(final int mi) {
        this.marriageId = mi;
    }
    
    public int getMarriageItemId() {
        return this.marriageItemId;
    }
    
    public void setMarriageItemId(final int mi) {
        this.marriageItemId = mi;
    }
    
    public boolean isStaff() {
        return this.gmLevel > PlayerGMRank.NORMAL.getLevel();
    }
    
    public boolean startPartyQuest(final int questid) {
        boolean ret = false;
        if (!this.quests.containsKey(MapleQuest.getInstance(questid)) || !this.questinfo.containsKey(questid)) {
            final MapleQuestStatus status = this.getQuestNAdd(MapleQuest.getInstance(questid));
            status.setStatus((byte)1);
            this.updateQuest(status);
            switch (questid) {
                case 1300:
                case 1301:
                case 1302: {
                    this.updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                }
                case 1204: {
                    this.updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                }
                case 1206: {
                    this.updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                }
                default: {
                    this.updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                }
            }
            ret = true;
        }
        return ret;
    }
    
    public String getOneInfo(final int questid, final String key) {
        if (!this.questinfo.containsKey(questid) || key == null) {
            return null;
        }
        final String[] split3;
        final String[] split = split3 = (this.questinfo.get(questid)).split(";");
        for (final String x : split3) {
            final String[] split2 = x.split("=");
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }
    
    public void updateOneInfo(final int questid, final String key, final String value) {
        if (!this.questinfo.containsKey(questid) || key == null || value == null) {
            return;
        }
        final String[] split = (this.questinfo.get(questid)).split(";");
        boolean changed = false;
        final StringBuilder newQuest = new StringBuilder();
        for (final String x : split) {
            final String[] split2 = x.split("=");
            if (split2.length == 2) {
                if (split2[0].equals(key)) {
                    newQuest.append(key).append("=").append(value);
                }
                else {
                    newQuest.append(x);
                }
                newQuest.append(";");
                changed = true;
            }
        }
        this.updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }
    
    public void recalcPartyQuestRank(final int questid) {
        if (!this.startPartyQuest(questid)) {
            final String oldRank = this.getOneInfo(questid, "rank");
            if (oldRank == null || oldRank.equals("S")) {
                return;
            }
            final String[] split = (this.questinfo.get(questid)).split(";");
            String newRank = null;
            final String s = oldRank;
            switch (s) {
                case "A": {
                    newRank = "S";
                    break;
                }
                case "B": {
                    newRank = "A";
                    break;
                }
                case "C": {
                    newRank = "B";
                    break;
                }
                case "D": {
                    newRank = "C";
                    break;
                }
                case "F": {
                    newRank = "D";
                    break;
                }
                default: {
                    return;
                }
            }
            final List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            for (final Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = this.getOneInfo(questid, q.right.left);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                }
                catch (NumberFormatException e) {
                    return;
                }
                final String s2 = q.left;
                switch (s2) {
                    case "less": {
                        found = (vall < q.right.right);
                        break;
                    }
                    case "more": {
                        found = (vall > q.right.right);
                        break;
                    }
                    case "equal": {
                        found = (vall == q.right.right);
                        break;
                    }
                }
                if (!found) {
                    return;
                }
            }
            this.updateOneInfo(questid, "rank", newRank);
        }
    }
    
    public void tryPartyQuest(final int questid) {
        try {
            this.startPartyQuest(questid);
            this.pqStartTime = System.currentTimeMillis();
            this.updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(this.getOneInfo(questid, "try")) + 1));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("tryPartyQuest error");
        }
    }
    
    public void endPartyQuest(final int questid) {
        try {
            this.startPartyQuest(questid);
            if (this.pqStartTime > 0L) {
                final long changeTime = System.currentTimeMillis() - this.pqStartTime;
                final int mins = (int)(changeTime / 1000L / 60L);
                final int secs = (int)(changeTime / 1000L % 60L);
                final int mins2 = Integer.parseInt(this.getOneInfo(questid, "min"));
                final int secs2 = Integer.parseInt(this.getOneInfo(questid, "sec"));
                if (mins2 <= 0 || mins < mins2) {
                    this.updateOneInfo(questid, "min", String.valueOf(mins));
                    this.updateOneInfo(questid, "sec", String.valueOf(secs));
                    this.updateOneInfo(questid, "date", FileoutputUtil.CurrentReadable_Date());
                }
                final int newCmp = Integer.parseInt(this.getOneInfo(questid, "cmp")) + 1;
                this.updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                this.updateOneInfo(questid, "CR", String.valueOf((int)Math.ceil(newCmp * 100.0 / Integer.parseInt(this.getOneInfo(questid, "try")))));
                this.recalcPartyQuestRank(questid);
                this.pqStartTime = 0L;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("endPartyQuest error");
        }
    }
    
    public void havePartyQuest(final int itemId) {
        int questid = 0;
        int index = -1;
        switch (itemId) {
            case 1002798: {
                questid = 1200;
                break;
            }
            case 1072369: {
                questid = 1201;
                break;
            }
            case 1022073: {
                questid = 1202;
                break;
            }
            case 1082232: {
                questid = 1203;
                break;
            }
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574: {
                questid = 1204;
                index = itemId - 1002571;
                break;
            }
            case 1122010: {
                questid = 1205;
                break;
            }
            case 1032060:
            case 1032061: {
                questid = 1206;
                index = itemId - 1032060;
                break;
            }
            case 3010018: {
                questid = 1300;
                break;
            }
            case 1122007: {
                questid = 1301;
                break;
            }
            case 1122058: {
                questid = 1302;
                break;
            }
            default: {
                return;
            }
        }
        this.startPartyQuest(questid);
        this.updateOneInfo(questid, "have" + ((index == -1) ? "" : Integer.valueOf(index)), "1");
    }
    
    public void resetStatsByJob(final boolean beginnerJob) {
        final int baseJob = beginnerJob ? (this.job % 1000) : (this.job % 1000 / 100 * 100);
        if (baseJob == 100) {
            this.resetStats(25, 4, 4, 4);
        }
        else if (baseJob == 200) {
            this.resetStats(4, 4, 20, 4);
        }
        else if (baseJob == 300 || baseJob == 400) {
            this.resetStats(4, 25, 4, 4);
        }
        else if (baseJob == 500) {
            this.resetStats(4, 20, 4, 4);
        }
    }
    
    public boolean hasSummon() {
        return this.hasSummon;
    }
    
    public void setHasSummon(final boolean summ) {
        this.hasSummon = summ;
    }
    
    public void removeDoor() {
        final MapleDoor door = this.getDoors().iterator().next();
        for (final MapleCharacter chr : door.getTarget().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleCharacter chr : door.getTown().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleDoor destroyDoor : this.getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        this.clearDoors();
    }
    
    public void changeRemoval() {
        this.changeRemoval(false);
    }
    
    public void changeRemoval(final boolean dc) {
        if (this.getTrade() != null) {
            MapleTrade.cancelTrade(this.getTrade(), this.client);
        }
        if (this.getCheatTracker() != null) {
            this.getCheatTracker().dispose();
        }
        if (!dc) {
            this.cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            this.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            this.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
            this.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        }
        if (this.getPyramidSubway() != null) {
            this.getPyramidSubway().dispose(this);
        }
        if (this.playerShop != null && !dc) {
            this.playerShop.removeVisitor(this);
            if (this.playerShop.isOwner(this)) {
                this.playerShop.setOpen(true);
            }
        }
        if (!this.getDoors().isEmpty()) {
            this.removeDoor();
        }
        this.disposeClones();
        NPCScriptManager.getInstance().dispose(this.client);
    }
    
    public void updateTick(final int newTick) {
        this.anticheat.updateTick(newTick);
    }
    
    public boolean canUseFamilyBuff(final MapleFamilyBuffEntry buff) {
        final MapleQuestStatus stat = this.getQuestNAdd(MapleQuest.getInstance(buff.questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + 86400000L < System.currentTimeMillis();
    }
    
    public void useFamilyBuff(final MapleFamilyBuffEntry buff) {
        final MapleQuestStatus stat = this.getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }
    
    public List<Pair<Integer, Integer>> usedBuffs() {
        final List used = new ArrayList();
        for (final MapleFamilyBuffEntry buff : MapleFamilyBuff.getBuffEntry()) {
            if (!this.canUseFamilyBuff(buff)) {
                used.add(new Pair<Integer, Integer>(buff.index, buff.count));
            }
        }
        return (List<Pair<Integer, Integer>>)used;
    }
    
    public String getTeleportName() {
        return this.teleportname;
    }
    
    public void setTeleportName(final String tname) {
        this.teleportname = tname;
    }
    
    public int getNoJuniors() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getNoJuniors();
    }
    
    public MapleFamilyCharacter getMFC() {
        return this.mfc;
    }
    
    public void makeMFC(final int familyid, final int seniorid, final int junior1, final int junior2) {
        if (familyid > 0) {
            final MapleFamily f = Family.getFamily(familyid);
            if (f == null) {
                this.mfc = null;
            }
            else {
                this.mfc = f.getMFC(this.id);
                if (this.mfc == null) {
                    this.mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (this.mfc.getSeniorId() != seniorid) {
                    this.mfc.setSeniorId(seniorid);
                }
                if (this.mfc.getJunior1() != junior1) {
                    this.mfc.setJunior1(junior1);
                }
                if (this.mfc.getJunior2() != junior2) {
                    this.mfc.setJunior2(junior2);
                }
            }
        }
        else {
            this.mfc = null;
        }
    }
    
    public void setFamily(final int newf, final int news, final int newj1, final int newj2) {
        if (this.mfc == null || newf != this.mfc.getFamilyId() || news != this.mfc.getSeniorId() || newj1 != this.mfc.getJunior1() || newj2 != this.mfc.getJunior2()) {
            this.makeMFC(newf, news, newj1, newj2);
        }
    }
    
    public int maxBattleshipHP(final int skillid) {
        return this.getSkillLevel(skillid) * 5000 + (this.getLevel() - 120) * 3000;
    }
    
    public int currentBattleshipHP() {
        return this.battleshipHP;
    }
    
    public void sendEnglishQuiz(final String msg) {
        this.client.getSession().write(MaplePacketCreator.englishQuizMsg(msg));
    }
    
    public void fakeRelog() {
        this.client.getSession().write(MaplePacketCreator.getCharInfo(this));
        final MapleMap mapp = this.getMap();
        mapp.removePlayer(this);
        mapp.addPlayer(this);
    }
    
    public String getcharmessage() {
        return this.charmessage;
    }
    
    public void setcharmessage(final String s) {
        this.charmessage = s;
    }
    
    public int getexpression() {
        return this.expression;
    }
    
    public void setexpression(final int s) {
        this.expression = s;
    }
    
    public int getconstellation() {
        return this.constellation;
    }
    
    public void setconstellation(final int s) {
        this.constellation = s;
    }
    
    public int getblood() {
        return this.blood;
    }
    
    public void setblood(final int s) {
        this.blood = s;
    }
    
    public int getmonth() {
        return this.month;
    }
    
    public void setmonth(final int s) {
        this.month = s;
    }
    
    public int getday() {
        return this.day;
    }
    
    public void setday(final int s) {
        this.day = s;
    }
    
    public int getTeam() {
        return this.coconutteam;
    }
    
    public int getBeans() {
        return this.beans;
    }
    
    public void gainBeans(final int s) {
        this.beans += s;
    }
    
    public void setBeans(final int s) {
        this.beans = s;
    }
    
    public int getBeansNum() {
        return this.beansNum;
    }
    
    public void setBeansNum(final int beansNum) {
        this.beansNum = beansNum;
    }
    
    public int getBeansRange() {
        return this.beansRange;
    }
    
    public void setBeansRange(int beansRange) {
        beansRange = beansRange;
    }
    
    public boolean isCanSetBeansNum() {
        return this.canSetBeansNum;
    }
    
    public void setCanSetBeansNum(final boolean canSetBeansNum) {
        this.canSetBeansNum = canSetBeansNum;
    }
    
    public boolean haveGM() {
        return this.gmLevel >= 2 && this.gmLevel <= 3;
    }
    
    public void setprefix(final int prefix) {
        this.prefix = prefix;
    }
    
    public int getPrefix() {
        return this.prefix;
    }
    
    public void startMapEffect(final String msg, final int itemId) {
        this.startMapEffect(msg, itemId, 3000);
    }
    
    public void startMapEffect1(final String msg, final int itemId) {
        this.startMapEffect(msg, itemId, 20000);
    }
    
    public void startMapEffect(final String msg, final int itemId, final int duration) {
        final MapleMapEffect mapEffect = new MapleMapEffect(msg, itemId);
        this.getClient().getSession().write(mapEffect.makeStartData());
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleCharacter.this.getClient().getSession().write(mapEffect.makeDestroyData());
            }
        }, duration);
    }
    
    public int getHyPay(final int type) {
        int pay = 0;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("select * from hypay where accname = ?");
            ps.setString(1, this.getClient().getAccountName());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                switch (type) {
                    case 1: {
                        pay = rs.getInt("pay");
                        break;
                    }
                    case 2: {
                        pay = rs.getInt("payUsed");
                        break;
                    }
                    case 3: {
                        pay = rs.getInt("pay") + rs.getInt("payUsed");
                        break;
                    }
                    case 4: {
                        pay = rs.getInt("payReward");
                        break;
                    }
                    default: {
                        pay = 0;
                        break;
                    }
                }
            }
            else {
                final PreparedStatement psu = con.prepareStatement("insert into hypay (accname, pay, payUsed, payReward) VALUES (?, ?, ?, ?)");
                psu.setString(1, this.getClient().getAccountName());
                psu.setInt(2, 0);
                psu.setInt(3, 0);
                psu.setInt(4, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        }
        catch (SQLException ex) {
            System.err.println("获取充值信息发生错误: " + ex);
        }
        return pay;
    }
    
    public int gainHyPay(final int hypay) {
        final int pay = this.getHyPay(1);
        final int payUsed = this.getHyPay(2);
        final int payReward = this.getHyPay(4);
        if (hypay <= 0) {
            return 0;
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? ,payUsed = ? ,payReward = ? where accname = ?");
            ps.setInt(1, pay + hypay);
            ps.setInt(2, payUsed);
            ps.setInt(3, payReward);
            ps.setString(4, this.getClient().getAccountName());
            ps.executeUpdate();
            ps.close();
            return 1;
        }
        catch (SQLException ex) {
            System.err.println("加减充值信息发生错误: " + ex);
            return 0;
        }
    }
    
    public int addHyPay(final int hypay) {
        final int pay = this.getHyPay(1);
        final int payUsed = this.getHyPay(2);
        final int payReward = this.getHyPay(4);
        if (hypay > pay) {
            return -1;
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? ,payUsed = ? ,payReward = ? where accname = ?");
            ps.setInt(1, pay - hypay);
            ps.setInt(2, payUsed + hypay);
            ps.setInt(3, payReward + hypay);
            ps.setString(4, this.getClient().getAccountName());
            ps.executeUpdate();
            ps.close();
            return 1;
        }
        catch (SQLException ex) {
            System.err.println("加减充值信息发生错误: " + ex);
            return -1;
        }
    }
    
    public int delPayReward(final int pay) {
        final int payReward = this.getHyPay(4);
        if (pay <= 0) {
            return -1;
        }
        if (pay > payReward) {
            return -1;
        }
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE hypay SET payReward = ? where accname = ?");
            ps.setInt(1, payReward - pay);
            ps.setString(2, this.getClient().getAccountName());
            ps.executeUpdate();
            ps.close();
            return 1;
        }
        catch (SQLException ex) {
            System.err.println("加减消费奖励信息发生错误: " + ex);
            return -1;
        }
    }
    
    public int getGamePoints() {
        try {
            int gamePoints = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gamePoints = rs.getInt("gamePoints");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    gamePoints = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePoints = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePoints) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return gamePoints;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误01 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public int getGamePointsPD() {
        try {
            int gamePointsPD = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gamePointsPD = rs.getInt("gamePointspd");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    gamePointsPD = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointspd = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointspd) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return gamePointsPD;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误00 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void gainGamePoints(final int amount) {
        final int gamePoints = this.getGamePoints() + amount;
        this.updateGamePoints(gamePoints);
    }
    
    public void gainGamePointsPD(final int amount) {
        final int gamePointsPD = this.getGamePointsPD() + amount;
        this.updateGamePointsPD(gamePointsPD);
    }
    
    public void resetGamePointsPD() {
        this.updateGamePointsPD(0);
    }
    
    public void updateGamePointsPD(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointspd = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误0 - 数据库更新失败." + Ex);
        }
    }
    
    public void gainGamePointsPS(final int amount) {
        final int gamePointsPS = this.getGamePointsPS() + amount;
        this.updateGamePointsPS(gamePointsPS);
    }
    
    public void resetGamePointsPS() {
        this.updateGamePointsPS(0);
    }
    
    public void updateGamePointsPS(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointsps = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误5 - 数据库更新失败." + Ex);
        }
    }
    
    public void resetGamePoints() {
        this.updateGamePoints(0);
    }
    
    public void updateGamePoints(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePoints = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误1 - 数据库更新失败." + Ex);
        }
    }
    
    public int getGamePointsRQ() {
        try {
            int gamePointsRQ = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gamePointsRQ = rs.getInt("gamePointsrq");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    gamePointsRQ = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointsrq = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointsrq) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return gamePointsRQ;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误2 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void gainGamePointsRQ(final int amount) {
        final int gamePointsRQ = this.getGamePointsRQ() + amount;
        this.updateGamePointsRQ(gamePointsRQ);
    }
    
    public void resetGamePointsRQ() {
        this.updateGamePointsRQ(0);
    }
    
    public void updateGamePointsRQ(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointsrq = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误3 - 数据库更新失败." + Ex);
        }
    }
    
    public int getGamePointsPS() {
        try {
            int gamePointsRQ = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gamePointsRQ = rs.getInt("gamePointsps");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    gamePointsRQ = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointsps = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointsps) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return gamePointsRQ;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误4 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void petName(final String name) {
        final MaplePet pet = this.getPet(0);
        if (pet == null) {
            this.getClient().getSession().write(MaplePacketCreator.serverNotice(1, "请召唤一只宠物出来！"));
            this.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        pet.setName(name);
        this.getClient().getSession().write(PetPacket.updatePet(pet, this.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
        this.getClient().getSession().write(MaplePacketCreator.enableActions());
        this.getClient().getPlayer().getMap().broadcastMessage(this.getClient().getPlayer(), MTSCSPacket.changePetName(this.getClient().getPlayer(), name, 1), true);
    }
    
    public void setName(final String name, final boolean changeName) {
        if (!changeName) {
            this.name = name;
        }
        else {
            final Connection con = DatabaseConnection.getConnection();
            try {
                con.setTransactionIsolation(1);
                con.setAutoCommit(false);
                final PreparedStatement sn = con.prepareStatement("UPDATE characters SET name = ? WHERE id = ?");
                sn.setString(1, name);
                sn.setInt(2, this.id);
                sn.execute();
                con.commit();
                sn.close();
                this.name = name;
            }
            catch (SQLException se) {
                System.err.println("SQL error: " + se.getLocalizedMessage() + "-----错误输出：" + se);
            }
        }
    }
    
    public long getDeadtime() {
        return this.deadtime;
    }
    
    public void setDeadtime(final long deadtime) {
        this.deadtime = deadtime;
    }
    
    public void increaseEquipExp(final int mobexp) {
        final MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        try {
            for (final Item item : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
                final Equip nEquip = (Equip)item;
                final String itemName = mii.getName(nEquip.getItemId());
                if (itemName != null && ((itemName.contains("重生") && nEquip.getEquipLevel() < 4) || (itemName.contains("永恒") && nEquip.getEquipLevel() < 6))) {
                    nEquip.gainItemExp(this.client, mobexp, itemName.contains("永恒"));
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public void reloadC() {
        this.client.getSession().write(MaplePacketCreator.getCharInfo(this.client.getPlayer()));
        this.client.getPlayer().getMap().removePlayer(this.client.getPlayer());
        this.client.getPlayer().getMap().addPlayer(this.client.getPlayer());
    }
    
    public void maxSkills() {
        for (final ISkill sk : SkillFactory.getAllSkills()) {
            this.changeSkillLevel(sk, sk.getMaxLevel(), sk.getMaxLevel());
        }
    }
    
    public void minSkills() {
        for (final ISkill sk : SkillFactory.getAllSkills()) {
            this.changeSkillLevel(sk, (byte)0, (byte)0);
        }
    }
    
    public void UpdateCash() {
        this.getClient().getSession().write(MaplePacketCreator.showCharCash(this));
    }
    
    public static String getAriantRoomLeaderName(final int room) {
        return MapleCharacter.ariantroomleader[room];
    }
    
    public static int getAriantSlotsRoom(final int room) {
        return MapleCharacter.ariantroomslot[room];
    }
    
    public static void removeAriantRoom(final int room) {
        MapleCharacter.ariantroomleader[room] = "";
        MapleCharacter.ariantroomslot[room] = 0;
    }
    
    public static void setAriantRoomLeader(final int room, final String charname) {
        MapleCharacter.ariantroomleader[room] = charname;
    }
    
    public static void setAriantSlotRoom(final int room, final int slot) {
        MapleCharacter.ariantroomslot[room] = slot;
    }
    
    public void addAriantScore() {
        ++this.ariantScore;
    }
    
    public void resetAriantScore() {
        this.ariantScore = 0;
    }
    
    public int getAriantScore() {
        return this.ariantScore;
    }
    
    public void updateAriantScore() {
        this.getMap().broadcastMessage(MaplePacketCreator.updateAriantScore(this.getName(), this.getAriantScore(), false));
    }
    
    public int getAveragePartyLevel() {
        int averageLevel = 0;
        int size = 0;
        for (final MaplePartyCharacter pl : this.getParty().getMembers()) {
            averageLevel += pl.getLevel();
            ++size;
        }
        if (size <= 0) {
            return this.level;
        }
        averageLevel /= size;
        return averageLevel;
    }
    
    public int getAverageMapLevel() {
        int averageLevel = 0;
        int size = 0;
        for (final MapleCharacter pl : this.getMap().getCharacters()) {
            averageLevel += pl.getLevel();
            ++size;
        }
        if (size <= 0) {
            return this.level;
        }
        averageLevel /= size;
        return averageLevel;
    }
    
    public void setApprentice(final int app) {
        this.apprentice = app;
    }
    
    public boolean hasApprentice() {
        return this.apprentice > 0;
    }
    
    public int getMaster() {
        return this.master;
    }
    
    public int getApprentice() {
        return this.apprentice;
    }
    
    public MapleCharacter getApp() {
        return this.client.getChannelServer().getPlayerStorage().getCharacterById(this.apprentice);
    }
    
    public MapleCharacter getMster() {
        return this.client.getChannelServer().getPlayerStorage().getCharacterById(this.master);
    }
    
    public void setMaster(final int mstr) {
        this.master = mstr;
    }
    
    public MapleRing getMarriageRing(final boolean incluedEquip) {
        MapleInventory iv = this.getInventory(MapleInventoryType.EQUIPPED);
        final Collection<Item> equippedC = iv.list();
        final List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (final Item item : equippedC) {
            equipped.add(item);
        }
        for (final Item item : equipped) {
            if (item.getRing() != null) {
                final MapleRing ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isMarriageRing(item.getItemId())) {
                    return ring;
                }
                continue;
            }
        }
        if (incluedEquip) {
            iv = this.getInventory(MapleInventoryType.EQUIP);
            for (final Item item : iv.list()) {
                if (item.getRing() != null && GameConstants.isMarriageRing(item.getItemId())) {
                    final MapleRing ring = item.getRing();
                    ring.setEquipped(false);
                    return ring;
                }
            }
        }
        return null;
    }
    
    public void setDebugMessage(final boolean control) {
        this.DebugMessage = control;
    }
    
    public boolean getDebugMessage() {
        return this.DebugMessage;
    }
    
    public final boolean canHold(final int itemid) {
        return this.getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }
    
    public int getIntRecord(final int questID) {
        final MapleQuestStatus stat = this.getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }
    
    public int getIntNoRecord(final int questID) {
        final MapleQuestStatus stat = this.getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }
    
    public void updatePetAuto() {
        this.client.announce(MaplePacketCreator.petAutoHP(this.getIntRecord(122221)));
        this.client.announce(MaplePacketCreator.petAutoMP(this.getIntRecord(122222)));
    }
    
    public void updatePetEquip() {
        if (this.getIntNoRecord(122221) > 0) {
            this.client.getSession().write(MaplePacketCreator.sendAutoHpPot(this.getIntRecord(122221)));
        }
        if (this.getIntNoRecord(122222) > 0) {
            this.client.getSession().write(MaplePacketCreator.petAutoMP(this.getIntRecord(122222)));
        }
    }
    
    public void spawnBomb() {
        final MapleMonster bomb = MapleLifeFactory.getMonster(9300166);
        bomb.changeLevel(250, true);
        this.getMap().spawnMonster_sSack(bomb, this.getPosition(), -2);
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                map.killMonster(bomb, client.getPlayer(), false, false, (byte)1);
            }
        }, 10000L);
    }
    
    public boolean isAriantPQMap() {
        switch (this.getMapId()) {
            case 980010101:
            case 980010201:
            case 980010301: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void addMobVac(final int type) {
        if (type == 1) {
            ++this.MobVac;
        }
        else if (type == 2) {
            ++this.MobVac2;
        }
    }
    
    public int getMobVac(final int type) {
        if (type == 1) {
            return this.MobVac;
        }
        if (type == 2) {
            return this.MobVac2;
        }
        return 0;
    }
    
    public int getMountId() {
        return this.mount_id;
    }
    
    public void setMountId(final int id) {
        this.mount_id = id;
    }
    
    public long getLastHM() {
        return this.lastGainHM;
    }
    
    public void setLastHM(final long newTime) {
        this.lastGainHM = newTime;
    }
    
    public void 刷新状态() {
        this.getClient().getSession().write(MaplePacketCreator.getCharInfo(this));
        this.getMap().removePlayer(this);
        this.getMap().addPlayer(this);
        this.getClient().getSession().write(MaplePacketCreator.enableActions());
    }
    
    public void 刷新地图() {
        final boolean custMap = true;
        final int mapid = this.getMapId();
        final MapleMap map = custMap ? this.getClient().getChannelServer().getMapFactory().getMap(mapid) : this.getMap();
        if (this.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
            final MapleMap newMap = this.getClient().getChannelServer().getMapFactory().getMap(mapid);
            final MaplePortal newPor = newMap.getPortal(0);
            final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters());
            for (final MapleCharacter m : mcs) {
                int x = 0;
                while (x < 5) {
                    try {
                        m.changeMap(newMap, newPor);
                    }
                    catch (Throwable t) {
                        ++x;
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    public MaplePvpStats getPvpStats() {
        return this.pvpStats;
    }
    
    public int getPvpKills() {
        return this.pvpKills;
    }
    
    public void gainPvpKill() {
        ++this.pvpKills;
        ++this.pvpVictory;
        if (this.pvpVictory == 2) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 获得了 双杀。"));
        }
        else if (this.pvpVictory == 3) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 获得了 三杀。"));
        }
        else if (this.pvpVictory == 4) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 获得了 四杀。"));
        }
        else if (this.pvpVictory == 5) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 获得了 五杀。"));
        }
        else if (this.pvpVictory == 7) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 获得了 God Like。"));
        }
        else if (this.pvpVictory >= 8 && this.pvpVictory < 20) {
            this.getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[PvP] 玩家 " + this.getName() + " 连续击杀 " + this.pvpVictory + " 次 获得了 传奇 。"));
        }
        else if (this.pvpVictory >= 20) {
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Pvp] 玩家 " + this.getName() + " 连续击杀 " + this.pvpVictory + " 次 获得了 legendary 。他(她)在頻道 " + this.client.getChannel() + " 地图 " + this.map.getMapName() + " 中喊到谁能赐我一死。"));
        }
        else {
            this.dropMessage(6, "当前: " + this.pvpVictory + " 连杀.");
        }
    }
    
    public int getqiandao() {
        return this.qiandao;
    }
    
    public void gainqiandao(final int gain) {
        this.qiandao += gain;
    }
    
    public void setqiandao(final int set) {
        this.qiandao = set;
    }
    
    public int get怪物数量() {
        try {
            int sgrw = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sgrw = rs.getInt("sgrw");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    sgrw = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET sgrw = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, sgrw) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return sgrw;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的每日任务出现错误 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void gain怪物数量(final int amount) {
        final int sgrw = this.get怪物数量() + amount;
        this.updateSGRW(sgrw);
    }
    
    public void 取消怪物数量() {
        this.updateSGRW(0);
    }
    
    public void updateSGRW(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET sgrw = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的每日任务出现错误 - 数据库更新失败." + Ex);
        }
    }
    
    public int get怪物ID() {
        try {
            int sgrwa = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sgrwa = rs.getInt("sgrwa");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    sgrwa = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET sgrwa = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, sgrwa) VALUES (?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return sgrwa;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的每日任务出现出现错误 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void gain怪物ID(final int amount) {
        final int sgrw = this.get怪物ID() + amount;
        this.updateSGRWA(sgrw);
    }
    
    public void 取消怪物ID() {
        this.updateSGRWA(0);
    }
    
    public void updateSGRWA(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET sgrwa = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新角色帐号的每日任务出现错误 - 数据库更新失败." + Ex);
        }
    }
    
    public int 判断会员() {
        try {
            int vip = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");
            ps.setInt(1, this.getClient().getAccID());
            ps.setInt(2, this.getWorld());
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vip = rs.getInt("vip");
                final Timestamp updateTime = rs.getTimestamp("updateTime");
                final Calendar sqlcal = Calendar.getInstance();
                if (updateTime != null) {
                    sqlcal.setTimeInMillis(updateTime.getTime());
                }
                if (sqlcal.get(5) + 1 <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                    vip = 0;
                    final PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET vip = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
                    psu.setInt(1, this.getClient().getAccID());
                    psu.setInt(2, this.getWorld());
                    psu.executeUpdate();
                    psu.close();
                }
            }
            else {
                final PreparedStatement psu2 = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePoints, vip, name) VALUES (?, ?, ?, ?, ?)");
                psu2.setInt(1, this.getClient().getAccID());
                psu2.setInt(2, this.getWorld());
                psu2.setInt(3, 0);
                psu2.setInt(4, 0);
                psu2.setString(5, this.name);
                psu2.executeUpdate();
                psu2.close();
            }
            rs.close();
            ps.close();
            return vip;
        }
        catch (SQLException Ex) {
            System.err.println("获取角色帐号的会员出现错误 - 数据库查询失败" + Ex);
            return -1;
        }
    }
    
    public void 给会员(final int amount) {
        final int vip = this.判断会员() + amount;
        this.会员2(vip);
    }
    
    public void 会员1() {
        this.会员2(0);
    }
    
    public void 会员2(final int amount) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET vip = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?");
            ps.setInt(1, amount);
            ps.setInt(2, this.getClient().getAccID());
            ps.setInt(3, this.getWorld());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException Ex) {
            System.err.println("更新会员出现错误 - 数据库更新失败." + Ex);
        }
    }
    
    public int getPvpDeaths() {
        return this.pvpDeaths;
    }
    
    public void gainPvpDeath() {
        ++this.pvpDeaths;
        this.pvpVictory = 0;
    }
    
    public int Lianjie() {
        final Connection con = DatabaseConnection.getConnection();
        int count = 0;
        try {
            final PreparedStatement ps = con.prepareStatement("SELECT count(*) as cc FROM accounts WHERE loggedin = 2", 1);
            final ResultSet re = ps.executeQuery();
            while (re.next()) {
                count = re.getInt("cc");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(EventInstanceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    public void forceUpdateItem(final MapleInventoryType type, final Item item) {
        this.client.getSession().write(MaplePacketCreator.clearInventoryItem(type, item.getPosition(), false));
        this.client.getSession().write(MaplePacketCreator.addInventorySlot(type, item, false));
    }
    
    public long get防止复制时间() {
        return this.防止复制时间;
    }
    
    public void set防止复制时间(final long 防止复制时间) {
        this.防止复制时间 = 防止复制时间;
    }
    
    public int getPvpVictory() {
        return this.pvpVictory;
    }
    
    public int getTouzhuNX() {
        return this.touzhuNX;
    }
    
    public void addMobVac() {
        ++this.MobVac;
    }
    
    public int getMobVac() {
        return this.MobVac;
    }
    
    public void setTouzhuNX(final int touzhuNX) {
        this.touzhuNX = touzhuNX;
    }
    
    public int getTouzhuNum() {
        return this.touzhuNum;
    }
    
    public void setTouzhuNum(final int touzhuNum) {
        this.touzhuNum = touzhuNum;
    }
    
    public int getTouzhuType() {
        return this.touzhuType;
    }
    
    public void setTouzhuType(final int touzhuType) {
        this.touzhuType = touzhuType;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(this.getTruePosition().x - 25, this.getTruePosition().y - 75, 50, 75);
    }
    
    public boolean hasBlockedInventory() {
        return !this.isAlive() || this.getTrade() != null || this.getConversation() > 0 || this.getPlayerShop() != null || this.map == null;
    }
    
    public final int getReportPoints() {
        int ret = 0;
        for (final Integer entry : this.reports.values()) {
            ret += entry;
        }
        return ret;
    }
    
    public String getReportSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<Pair<ReportType, Integer>> offenseList = new ArrayList<Pair<ReportType, Integer>>();
        for (final Entry<ReportType, Integer> entry : this.reports.entrySet()) {
            offenseList.add(new Pair<ReportType, Integer>(entry.getKey(), entry.getValue()));
        }
     
        offenseList.sort((o1, o2) -> {
        	   final int thisVal;
               final int anotherVal;
            thisVal = o1.getRight();
            anotherVal = o2.getRight();
            return (thisVal < anotherVal) ? 1 : ((thisVal == anotherVal) ? 0 : -1);
        });
        for (final Pair<ReportType, Integer> anOffenseList : offenseList) {
            ret.append(StringUtil.makeEnumHumanReadable(anOffenseList.left.name()));
            ret.append(": ");
            ret.append(anOffenseList.right);
            ret.append(" ");
        }
        return ret.toString();
    }
    
    public void getCherryBanMessage() {
        final String msg = "您因不当行为，而遭游戏管理员禁止攻击、禁止获得经验和金币、禁止交易、禁止丢弃道具、禁止开启个人商店与精灵商人、禁止组队、禁止使用拍卖系统，因此无法使用该功能。";
        this.dropMessage(5, msg);
    }
    
    public void blockPortal(final String scriptName) {
        if (!this.blockedPortals.contains(scriptName) && scriptName != null) {
            this.blockedPortals.add(scriptName);
        }
        this.getClient().getSession().write(MaplePacketCreator.blockedPortal());
    }
    
    public void unblockPortal(final String scriptName) {
        if (this.blockedPortals.contains(scriptName) && scriptName != null) {
            this.blockedPortals.remove(scriptName);
        }
    }
    
    public List getBlockedPortals() {
        return this.blockedPortals;
    }
    
    public boolean isIntern() {
        return this.gmLevel >= PlayerGMRank.INTERN.getLevel();
    }
    
    public boolean isHiddenChatCanSee() {
        return this.hiddenChatCanSee;
    }
    
    public void setHiddenChatCanSee(final boolean can) {
        this.hiddenChatCanSee = can;
    }
    
    public boolean isSuperGM() {
        return this.gmLevel >= PlayerGMRank.GM.getLevel();
    }
    
    public boolean isDonator() {
        return this.getInventory(MapleInventoryType.EQUIP).findById(1142000) != null || this.getInventory(MapleInventoryType.EQUIPPED).findById(1142000) != null;
    }
    
    public void setChatType(final short chattype) {
        this.chattype = chattype;
    }
    
    public short getChatType() {
        return this.chattype;
    }
    
    public void setVip(final int vip) {
        if (vip >= 5) {
            this.vip = 5;
        }
        else if (vip < 0) {
            this.vip = 0;
        }
        else {
            this.vip = vip;
        }
    }
    
    public int getVip() {
        return this.vip;
    }
    
    public boolean isVip() {
        return this.vip > 0;
    }
    
    public int getVipczz() {
        return this.vipczz;
    }
    
    public void setVipczz(final int vipczz) {
        this.vipczz = vipczz;
    }
    
    public Timestamp getViptime() {
        if (this.getVip() == 0) {
            return null;
        }
        return this.viptime;
    }
    
    public void setViptime(final Timestamp expire) {
        this.viptime = expire;
    }
    
    public void setViptime(final long period) {
        if (period != 0L) {
            final Timestamp expiration = new Timestamp(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            this.setViptime(expiration);
        }
        else {
            this.setViptime(null);
        }
    }
    
    public int getReborns() {
        return this.ddj;
    }
    
    public void gainReborns(final int type) {
        ++this.ddj;
    }
    
    public void setJob(final int jobId) {
        this.job = (short)jobId;
    }
    
    public String getRebornsName() {
        if (this.getReborns() > 0 && this.getReborns() <= 1) {
            return "炼气[前期]";
        }
        if (this.getReborns() > 1 && this.getReborns() <= 3) {
            return "炼气[中期]";
        }
        if (this.getReborns() > 3 && this.getReborns() <= 5) {
            return "炼气[后期]";
        }
        if (this.getReborns() > 5 && this.getReborns() <= 10) {
            return "筑基[前期]";
        }
        if (this.getReborns() > 10 && this.getReborns() <= 15) {
            return "筑基[中期]";
        }
        if (this.getReborns() > 15 && this.getReborns() <= 20) {
            return "筑基[后期]";
        }
        if (this.getReborns() > 20 && this.getReborns() <= 30) {
            return "开光[前期]";
        }
        if (this.getReborns() > 30 && this.getReborns() <= 40) {
            return "开光[中期]";
        }
        if (this.getReborns() > 40 && this.getReborns() <= 50) {
            return "开光[后期]";
        }
        if (this.getReborns() > 50 && this.getReborns() <= 65) {
            return "胎息[前期]";
        }
        if (this.getReborns() > 65 && this.getReborns() <= 80) {
            return "胎息[中期]";
        }
        if (this.getReborns() > 80 && this.getReborns() <= 100) {
            return "胎息[后期]";
        }
        if (this.getReborns() > 100 && this.getReborns() <= 120) {
            return "辟谷[前期]";
        }
        if (this.getReborns() > 120 && this.getReborns() <= 140) {
            return "辟谷[中期]";
        }
        if (this.getReborns() > 140 && this.getReborns() <= 160) {
            return "辟谷[后期]";
        }
        if (this.getReborns() > 160 && this.getReborns() <= 190) {
            return "金丹[前期]";
        }
        if (this.getReborns() > 190 && this.getReborns() <= 220) {
            return "金丹[中期]";
        }
        if (this.getReborns() > 220 && this.getReborns() <= 250) {
            return "金丹[后期]";
        }
        if (this.getReborns() > 250 && this.getReborns() <= 300) {
            return "元婴[前期]";
        }
        if (this.getReborns() > 300 && this.getReborns() <= 350) {
            return "元婴[中期]";
        }
        if (this.getReborns() > 350 && this.getReborns() <= 400) {
            return "元婴[后期]";
        }
        if (this.getReborns() > 400 && this.getReborns() <= 500) {
            return "出窍[前期]";
        }
        if (this.getReborns() > 500 && this.getReborns() <= 600) {
            return "出窍[中期]";
        }
        if (this.getReborns() > 600 && this.getReborns() <= 700) {
            return "出窍[后期]";
        }
        if (this.getReborns() > 700 && this.getReborns() <= 900) {
            return "分神[前期]";
        }
        if (this.getReborns() > 900 && this.getReborns() <= 1100) {
            return "分神[中期]";
        }
        if (this.getReborns() > 1100 && this.getReborns() <= 1300) {
            return "分神[后期]";
        }
        if (this.getReborns() > 1300 && this.getReborns() <= 1800) {
            return "合体[前期]";
        }
        if (this.getReborns() > 1800 && this.getReborns() <= 2300) {
            return "合体[中期]";
        }
        if (this.getReborns() > 2300 && this.getReborns() <= 2800) {
            return "合体[后期]";
        }
        if (this.getReborns() > 2800 && this.getReborns() <= 3300) {
            return "大乘[前期]";
        }
        if (this.getReborns() > 3300 && this.getReborns() <= 3800) {
            return "大乘[中期]";
        }
        if (this.getReborns() > 3800 && this.getReborns() <= 4300) {
            return "大乘[后期]";
        }
        if (this.getReborns() > 4300 && this.getReborns() <= 4800) {
            return "渡劫[前期]";
        }
        if (this.getReborns() > 4800 && this.getReborns() <= 5300) {
            return "渡劫[中期]";
        }
        if (this.getReborns() > 5300 && this.getReborns() <= 5800) {
            return "渡劫[后期]";
        }
        if (this.getReborns() > 5800 && this.getReborns() < 6666) {
            return "渡劫[巅峰]";
        }
        if (this.getReborns() >= 6666) {
            return "神";
        }
        return "凡人";
    }
    
    public boolean 转生能力值判断() {
        return this.getRemainingAp() == 0;
    }
    
    public boolean 转生物品判断() {
        return this.haveItem(2340000, 1);
    }
    
    public boolean 转生金币判断() {
        return this.getMeso() >= Integer.parseInt(ServerProperties.getProperty("channel.转生需要金币"));
    }
    
    public void gainAPS(final int aps) {
        this.remainingAp += (short)aps;
    }
    
    public boolean XXRebornLevel() {
        return (this.getJob() >= 1000 && this.getJob() <= 1512 && this.getLevel() >= 180) || this.getLevel() >= 200;
    }
    
    public void XXReborn(final String namejob) {
        final List stat = new ArrayList(2);
        this.gainReborns(1);
        this.setLevel((short)1);
        this.setExp(0);
        this.setRemainingAp((short)0);
        this.client.getPlayer().setJob(0);
        final int oriStats = this.stats.getStr() + this.stats.getDex() + this.stats.getLuk() + this.stats.getInt();
        final int str = 12;
        final int dex = 5;
        final int int_ = 4;
        final int luk = 4;
        final int afterStats = str + dex + int_ + luk;
        final int 扣除能力值 = 800;
        final int MAS = oriStats - afterStats + this.getRemainingAp() - 扣除能力值;
        this.client.getPlayer().gainAPS(MAS);
        if (null != namejob) {
            switch (namejob) {
                case "骑士团": {
                    this.client.getPlayer().setJob(1000);
                    break;
                }
                case "战神": {
                    this.client.getPlayer().setJob(2000);
                    break;
                }
                case "冒险家": {
                    this.client.getPlayer().setJob(0);
                    break;
                }
                default: {
                    this.client.getPlayer().setJob(0);
                    break;
                }
            }
        }
        this.stats.recalcLocalStats();
        this.stats.str = (short)str;
        this.stats.dex = (short)dex;
        this.stats.int_ = (short)int_;
        this.stats.luk = (short)luk;
        stat.add(new Pair<MapleStat, Integer>(MapleStat.STR, str));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.DEX, dex));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.INT, int_));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.LUK, luk));
        stat.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, 0));
        this.updateSingleStat(MapleStat.LEVEL, 1);
        this.updateSingleStat(MapleStat.JOB, 0);
        this.updateSingleStat(MapleStat.EXP, 0);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, this.getJob()));
        String name = "";
        if (this.getReborns() > 0 && this.getReborns() <= 1) {
            name = "炼气[前期]";
        }
        else if (this.getReborns() > 1 && this.getReborns() <= 3) {
            name = "炼气[中期]";
        }
        else if (this.getReborns() > 3 && this.getReborns() <= 5) {
            name = "炼气[后期]";
        }
        else if (this.getReborns() > 5 && this.getReborns() <= 10) {
            name = "筑基[前期]";
        }
        else if (this.getReborns() > 10 && this.getReborns() <= 15) {
            name = "筑基[中期]";
        }
        else if (this.getReborns() > 15 && this.getReborns() <= 20) {
            name = "筑基[后期]";
        }
        else if (this.getReborns() > 20 && this.getReborns() <= 30) {
            name = "开光[前期]";
        }
        else if (this.getReborns() > 30 && this.getReborns() <= 40) {
            name = "开光[中期]";
        }
        else if (this.getReborns() > 40 && this.getReborns() <= 50) {
            name = "开光[后期]";
        }
        else if (this.getReborns() > 50 && this.getReborns() <= 65) {
            name = "胎息[前期]";
        }
        else if (this.getReborns() > 65 && this.getReborns() <= 80) {
            name = "胎息[中期]";
        }
        else if (this.getReborns() > 80 && this.getReborns() <= 100) {
            name = "胎息[后期]";
        }
        else if (this.getReborns() > 100 && this.getReborns() <= 120) {
            name = "辟谷[前期]";
        }
        else if (this.getReborns() > 120 && this.getReborns() <= 140) {
            name = "辟谷[中期]";
        }
        else if (this.getReborns() > 140 && this.getReborns() <= 160) {
            name = "辟谷[后期]";
        }
        else if (this.getReborns() > 160 && this.getReborns() <= 190) {
            name = "金丹[前期]";
        }
        else if (this.getReborns() > 190 && this.getReborns() <= 220) {
            name = "金丹[中期]";
        }
        else if (this.getReborns() > 220 && this.getReborns() <= 250) {
            name = "金丹[后期]";
        }
        else if (this.getReborns() > 250 && this.getReborns() <= 300) {
            name = "元婴[前期]";
        }
        else if (this.getReborns() > 300 && this.getReborns() <= 350) {
            name = "元婴[中期]";
        }
        else if (this.getReborns() > 350 && this.getReborns() <= 400) {
            name = "元婴[后期]";
        }
        else if (this.getReborns() > 400 && this.getReborns() <= 500) {
            name = "出窍[前期]";
        }
        else if (this.getReborns() > 500 && this.getReborns() <= 600) {
            name = "出窍[中期]";
        }
        else if (this.getReborns() > 600 && this.getReborns() <= 700) {
            name = "出窍[后期]";
        }
        else if (this.getReborns() > 700 && this.getReborns() <= 900) {
            name = "分神[前期]";
        }
        else if (this.getReborns() > 900 && this.getReborns() <= 1100) {
            name = "分神[中期]";
        }
        else if (this.getReborns() > 1100 && this.getReborns() <= 1300) {
            name = "分神[后期]";
        }
        else if (this.getReborns() > 1300 && this.getReborns() <= 1800) {
            name = "合体[前期]";
        }
        else if (this.getReborns() > 1800 && this.getReborns() <= 2300) {
            name = "合体[中期]";
        }
        else if (this.getReborns() > 2300 && this.getReborns() <= 2800) {
            name = "合体[后期]";
        }
        else if (this.getReborns() > 2800 && this.getReborns() <= 3300) {
            name = "大乘[前期]";
        }
        else if (this.getReborns() > 3300 && this.getReborns() <= 3800) {
            name = "大乘[中期]";
        }
        else if (this.getReborns() > 3800 && this.getReborns() <= 4300) {
            name = "大乘[后期]";
        }
        else if (this.getReborns() > 4300 && this.getReborns() <= 4800) {
            name = "渡劫[前期]";
        }
        else if (this.getReborns() > 4800 && this.getReborns() <= 5300) {
            name = "渡劫[中期]";
        }
        else if (this.getReborns() > 5300 && this.getReborns() <= 5800) {
            name = "渡劫[后期]";
        }
        else if (this.getReborns() > 5800 && this.getReborns() < 6666) {
            name = "渡劫[巅峰]";
        }
        else if (this.getReborns() >= 6666) {
            name = "神";
        }
        else {
            name = "凡人";
        }
        if (this.getGender() == 0) {
            this.client.getSession().write(MaplePacketCreator.yellowChat("[飞升] 玩家 " + this.getName() + " 第" + this.ddj + "次成功修成 " + name + " ！恭喜他吧"));
        }
        else {
            this.client.getSession().write(MaplePacketCreator.yellowChat("[飞升] 玩家 " + this.getName() + " 第" + this.ddj + "次成功修成 " + name + " ！恭喜他吧"));
        }
    }
    
    public void gainIten(final int id, final int amount) {
        MapleInventoryManipulator.addById(this.getClient(), id, (short)amount, (byte)0);
    }
    
    public void gainItem(final int code, final int amount, final String log) {
        MapleInventoryManipulator.输出道具1(this.client, code, (short)amount, log);
    }
    
    public int gettuiguang() {
        return this.推广人;
    }
    
    public void settuiguang(final int set) {
        this.推广人 = set;
    }
    
    public void gaintuiguang(final int gain) {
        this.推广人 += gain;
    }
    
    public int gettuiguang2() {
        return this.推广值;
    }
    
    public void settuiguang2(final int set) {
        this.推广值 = set;
    }
    
    public void yqm(final int yqm) {
        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                if (yqm != mch.getId()) {
                    this.dropMessage("推广码为空.请填写正确的推广码！");
                    this.getClient().getSession().write(MaplePacketCreator.getCharInfo(this));
                    this.getMap().removePlayer(this);
                    this.getMap().addPlayer(this);
                }
            }
        }
    }
    
    public int getcz() {
        int chongzhi = 0;
        try {
            final int cid = this.getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                chongzhi = rs.getInt("chongzhi");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {}
        return chongzhi;
    }
    
    public void forceUpdateItem(final Item item) {
        this.forceUpdateItem(item, false);
    }
    
    public void forceUpdateItem(final Item item, final boolean updateTick) {
        final List<ModifyInventory> mods = new LinkedList<ModifyInventory>();
        mods.add(new ModifyInventory(3, item));
        mods.add(new ModifyInventory(0, item));
        this.client.announce(MaplePacketCreator.modifyInventory(updateTick, mods));
    }
    
    public void checkCopyItems() {
        final List<Integer> equipOnlyIds = new ArrayList<Integer>();
        final Map<Integer, Integer> checkItems = new HashMap<Integer, Integer>();
        for (final Item item : this.getInventory(MapleInventoryType.EQUIP).list()) {
            final int equipOnlyId = item.getEquipOnlyId();
            if (equipOnlyId > 0) {
                if (checkItems.containsKey(equipOnlyId)) {
                    if (checkItems.get(equipOnlyId) != item.getItemId()) {
                        continue;
                    }
                    equipOnlyIds.add(equipOnlyId);
                }
                else {
                    checkItems.put(equipOnlyId, item.getItemId());
                }
            }
        }
        for (final Item item : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
            final int equipOnlyId = item.getEquipOnlyId();
            if (equipOnlyId > 0) {
                if (checkItems.containsKey(equipOnlyId)) {
                    if (checkItems.get(equipOnlyId) != item.getItemId()) {
                        continue;
                    }
                    equipOnlyIds.add(equipOnlyId);
                }
                else {
                    checkItems.put(equipOnlyId, item.getItemId());
                }
            }
        }
        boolean autoban = false;
        for (final Integer equipOnlyId2 : equipOnlyIds) {
            MapleInventoryManipulator.removeAllByEquipOnlyId(this.client, equipOnlyId2);
            autoban = true;
        }
        if (autoban) {}
        checkItems.clear();
        equipOnlyIds.clear();
    }
    
    public void Dci() {
        this.client.disconnect(true, false);
    }
    
    public Calendar getDaybyDay(final int n2) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(5, calendar.get(5) + n2);
        calendar.set(11, 0);
        calendar.set(9, 0);
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }
    
    public void setOnlineTime() {
        if (this.getLogintime() > 0L) {
            final long l2 = this.getDaybyDay(1).getTimeInMillis() - this.getLogintime();
            if (this.onlineTime < 0L) {}
            this.setLogintime(System.currentTimeMillis());
        }
    }
    
    public long getLogintime() {
        return this.logintime;
    }
    
    public void setLogintime(final long logintime) {
        this.logintime = logintime;
    }
    
    public long getQuestDiffTime() {
        return (System.currentTimeMillis() - this.logintime) / 1000L / 60L;
    }
    
    public int getBossLog2(final String boss) {
        return this.getBossLog2(boss, 0);
    }
    
    public int getBossLog2(final String boss, final int type) {
        try {
            int count = 0;
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, this.id);
            ps.setString(2, boss);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                final Timestamp bossTime = rs.getTimestamp("time");
                rs.close();
                ps.close();
                if (type == 0) {
                    if (bossTime != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(bossTime.getTime());
                        if (cal.get(5) + 1 <= Calendar.getInstance().get(5) || cal.get(2) + 1 <= Calendar.getInstance().get(2)) {
                            count = 0;
                            ps = con.prepareStatement("UPDATE bosslog SET count = 0  WHERE characterid = ? AND bossid = ?");
                            ps.setInt(1, this.id);
                            ps.setString(2, boss);
                            ps.executeUpdate();
                        }
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE bosslog SET time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
                    ps.setInt(1, this.id);
                    ps.setString(2, boss);
                    ps.executeUpdate();
                }
            }
            else {
                final PreparedStatement psu = con.prepareStatement("INSERT INTO bosslog (characterid, bossid, count, type) VALUES (?, ?, ?, ?)");
                psu.setInt(1, this.id);
                psu.setString(2, boss);
                psu.setInt(3, 0);
                psu.setInt(4, type);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
            return count;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public void setBossLog2(final String boss) {
        this.setBossLog2(boss, 0);
    }
    
    public void setBossLog2(final String boss, final int type) {
        this.setBossLog2(boss, type, 1);
    }
    
    public void setBossLog2(final String boss, final int type, final int count) {
        final int bossCount = this.getBossLog2(boss, type);
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, bossCount + count);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception ex) {}
    }
    
    public void resetBossLog2(final String boss) {
        this.resetBossLog2(boss, 0);
    }
    
    public void resetBossLog2(final String boss, final int type) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, 0);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception ex) {}
    }
    
    public void dropNPC(final String message) {
        this.client.sendPacket(MaplePacketCreator.getNPCTalk(9010000, (byte)0, message, "00 00", (byte)0));
    }
    
    public void toggleTestingDPS() {
        this.testingdps = !this.testingdps;
    }
    
    public boolean isTestingDPS() {
        return this.testingdps;
    }
    
    public void setDPS(final long newdps) {
        if (this.dps >= 2147483647L) {
            this.dps = 0L;
        }
        this.dps = newdps;
    }
    
    public long getDPS() {
        return this.dps;
    }
    
    public void savePlayer() {
        this.saveToDB(false, false);
    }
    
    public void get玩家私聊1(final boolean xx) {
        this.玩家私聊1 = xx;
    }
    
    public boolean get玩家私聊1() {
        return this.玩家私聊1;
    }
    
    public void get玩家私聊2(final boolean xx) {
        this.玩家私聊2 = xx;
    }
    
    public boolean get玩家私聊2() {
        return this.玩家私聊2;
    }
    
    public void get玩家私聊3(final boolean xx) {
        this.玩家私聊3 = xx;
    }
    
    public boolean get玩家私聊3() {
        return this.玩家私聊3;
    }
    
    public void getGMChat(final boolean xx) {
        this.GM聊天 = xx;
    }
    
    public boolean getGMChat() {
        return this.GM聊天;
    }
    
    public void getCTitle(final boolean xx) {
        this.聊天稱號 = xx;
    }
    
    public boolean getCTitle() {
        return this.聊天稱號;
    }
    
    public void setChatTitle(final String text) {
        this.chattitle = text;
    }
    
    public String getChatTitle() {
        return this.chattitle;
    }
    
    public int get输出Log(final String boss) {
        return this.get输出Log(boss, 0);
    }
    
    public int get输出Log(final String boss, final int type) {
        try {
            int count = 0;
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM 任务记录 WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, this.id);
            ps.setString(2, boss);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                final Timestamp bossTime = rs.getTimestamp("time");
                rs.close();
                ps.close();
                if (type == 0) {
                    if (bossTime != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(bossTime.getTime());
                        if (cal.get(5) + 1 <= Calendar.getInstance().get(5) || cal.get(2) + 1 <= Calendar.getInstance().get(2)) {
                            count = 0;
                            ps = con.prepareStatement("UPDATE 任务记录 SET count = 0  WHERE characterid = ? AND bossid = ?");
                            ps.setInt(1, this.id);
                            ps.setString(2, boss);
                            ps.executeUpdate();
                        }
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE 任务记录 SET time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
                    ps.setInt(1, this.id);
                    ps.setString(2, boss);
                    ps.executeUpdate();
                }
            }
            else {
                final PreparedStatement psu = con.prepareStatement("INSERT INTO 任务记录 (characterid, bossid, count, type) VALUES (?, ?, ?, ?)");
                psu.setInt(1, this.id);
                psu.setString(2, boss);
                psu.setInt(3, 0);
                psu.setInt(4, type);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
            return count;
        }
        catch (Exception Ex) {
            System.out.println("Error while read 任务记录." + Ex);
            return -1;
        }
    }
    
    public void set输出Log(final String boss) {
        this.set输出Log(boss, 0);
    }
    
    public void set输出Log(final String boss, final int type) {
        this.set输出Log(boss, type, 1);
    }
    
    public void set输出Log(final String boss, final int type, final int count) {
        final int bossCount = this.get输出Log(boss, type);
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE 任务记录 SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, bossCount + count);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception Ex) {
            System.out.println("Error while set 任务记录." + Ex);
        }
    }
    
    public void reset输出Log(final String boss) {
        this.reset输出Log(boss, 0);
    }
    
    public void reset输出Log(final String boss, final int type) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE 任务记录 SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, 0);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception Ex) {
            System.out.println("Error while reset 任务记录." + Ex);
        }
    }
    
    static {
        ariantroomleader = new String[3];
        ariantroomslot = new int[3];
    }
    
    public enum FameStatus
    {
        OK, 
        NOT_TODAY, 
        NOT_THIS_MONTH;
    }
}
