package client;

import tools.packet.MonsterBookPacket;
import server.MapleItemInformationProvider;
import tools.data.output.MaplePacketLittleEndianWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import database.DatabaseConnection;
import java.util.Iterator;
import constants.GameConstants;
import java.util.Map.Entry;
import java.util.Map;
import java.io.Serializable;

public class MonsterBook implements Serializable
{
    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed;
    private int SpecialCard;
    private int NormalCard;
    private int BookLevel;
    private final Map<Integer, Integer> cards;
    
    public MonsterBook(final Map<Integer, Integer> cards) {
        this.changed = false;
        this.SpecialCard = 0;
        this.NormalCard = 0;
        this.BookLevel = 1;
        this.cards = cards;
        for (final Entry<Integer, Integer> card : cards.entrySet()) {
            if (GameConstants.isSpecialCard(card.getKey())) {
                this.SpecialCard += card.getValue();
            }
            else {
                this.NormalCard += card.getValue();
            }
        }
        this.calculateLevel();
    }
    
    public Map<Integer, Integer> getCards() {
        return this.cards;
    }
    
    public final int getTotalCards() {
        return this.SpecialCard + this.NormalCard;
    }
    
    public final int getLevelByCard(final int cardid) {
        return (this.cards.get(cardid) == null) ? 0 : this.cards.get(cardid);
    }
    
    public static final MonsterBook loadCards(final int charid) throws SQLException {
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
        ps.setInt(1, charid);
        final ResultSet rs = ps.executeQuery();
        final Map cards = new LinkedHashMap();
        while (rs.next()) {
            cards.put(rs.getInt("cardid"), rs.getInt("level"));
        }
        rs.close();
        ps.close();
        return new MonsterBook(cards);
    }
    
    public final void saveCards(final int charid) throws SQLException {
        if (!this.changed || this.cards.isEmpty()) {
            return;
        }
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();
        boolean first = true;
        final StringBuilder query = new StringBuilder();
        for (final Entry all : this.cards.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            }
            else {
                query.append(",(DEFAULT,");
            }
            query.append(charid);
            query.append(",");
            query.append(all.getKey());
            query.append(",");
            query.append(all.getValue());
            query.append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }
    
    private final void calculateLevel() {
        final int Size = this.NormalCard + this.SpecialCard;
        this.BookLevel = 8;
        for (int i = 0; i < 8; ++i) {
            if (Size <= GameConstants.getBookLevel(i)) {
                this.BookLevel = i + 1;
                break;
            }
        }
    }
    
    public final void addCardPacket(final MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.cards.size());
        for (final Entry<Integer, Integer> all : this.cards.entrySet()) {
            mplew.writeShort(GameConstants.getCardShortId(all.getKey()));
            mplew.write(all.getValue());
        }
    }
    
    public final void addCharInfoPacket(final int bookcover, final MaplePacketLittleEndianWriter mplew) {
        mplew.writeInt(this.BookLevel);
        mplew.writeInt(this.NormalCard);
        mplew.writeInt(this.SpecialCard);
        mplew.writeInt(this.NormalCard + this.SpecialCard);
        mplew.writeInt(MapleItemInformationProvider.getInstance().getCardMobId(bookcover));
    }
    
    public final void updateCard(final MapleClient c, final int cardid) {
        c.sendPacket(MonsterBookPacket.changeCover(cardid));
    }
    
    public final void addCard(final MapleClient c, final int cardid) {
        this.changed = true;
        if (this.cards.containsKey(cardid)) {
            final int levels = (int)(int)this.cards.get(cardid);
            if (levels >= 5) {
                c.sendPacket(MonsterBookPacket.addCard(true, cardid, levels));
            }
            else {
                if (GameConstants.isSpecialCard(cardid)) {
                    ++this.SpecialCard;
                }
                else {
                    ++this.NormalCard;
                }
                c.sendPacket(MonsterBookPacket.addCard(false, cardid, levels));
                c.sendPacket(MonsterBookPacket.showGainCard(cardid));
                this.cards.put(cardid, levels + 1);
                this.calculateLevel();
            }
            return;
        }
        if (GameConstants.isSpecialCard(cardid)) {
            ++this.SpecialCard;
        }
        else {
            ++this.NormalCard;
        }
        this.cards.put(cardid, 1);
        c.sendPacket(MonsterBookPacket.addCard(false, cardid, 1));
        c.sendPacket(MonsterBookPacket.showGainCard(cardid));
        this.calculateLevel();
    }
}
