package client;

import java.sql.SQLException;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Map.Entry;
import database.DatabaseConnection;
import tools.data.output.MaplePacketLittleEndianWriter;
import java.util.HashMap;
import tools.Pair;
import java.util.Map;
import java.io.Serializable;

public class MapleKeyLayout implements Serializable
{
    private static final long serialVersionUID = 9179541993413738569L;
    private boolean changed;
    private Map<Integer, Pair<Byte, Integer>> keymap;
    
    public MapleKeyLayout() {
        this.changed = false;
        this.keymap = new HashMap<Integer, Pair<Byte, Integer>>();
    }
    
    public MapleKeyLayout(final Map<Integer, Pair<Byte, Integer>> keys) {
        this.changed = false;
        this.keymap = keys;
    }
    
    public final Map<Integer, Pair<Byte, Integer>> Layout() {
        this.changed = true;
        return this.keymap;
    }
    
    public final void writeData(final MaplePacketLittleEndianWriter mplew) {
        for (int x = 0; x < 90; ++x) {
            final Pair<Byte, Integer> binding = this.keymap.get(x);
            if (binding != null) {
                mplew.write(binding.getLeft());
                mplew.writeInt(binding.getRight());
            }
            else {
                mplew.write(0);
                mplew.writeInt(0);
            }
        }
    }
    
    public final void saveKeys(final int charid) throws SQLException {
        if (!this.changed || this.keymap.size() == 0) {
            return;
        }
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM keymap WHERE characterid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();
        boolean first = true;
        final StringBuilder query = new StringBuilder();
        for (final Entry<Integer, Pair<Byte, Integer>> keybinding : this.keymap.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO keymap VALUES (");
            }
            else {
                query.append(",(");
            }
            query.append("DEFAULT,");
            query.append(charid).append(",");
            query.append((int)keybinding.getKey()).append(",");
            query.append((int)keybinding.getValue().getLeft()).append(",");
            query.append((int)keybinding.getValue().getRight()).append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }
}
