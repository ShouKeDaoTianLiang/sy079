package handling.channel.handler;

import client.MapleCharacterUtil;
import tools.MaplePacketCreator;
import java.util.Iterator;
import java.util.List;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.World.Find;
import handling.world.family.MapleFamilyCharacter;
import handling.world.World.Family;
import server.maps.FieldLimitType;
import handling.world.family.MapleFamilyBuff;
import client.MapleCharacter;
import tools.packet.FamilyPacket;
import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilyHandler
{
    public static final void RequestFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.sendPacket(FamilyPacket.getFamilyPedigree(chr));
        }
    }
    
    public static final void OpenFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.sendPacket(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }
    
    public static final void UseFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int type = slea.readInt();
        final MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
        if (entry == null) {
            return;
        }
        boolean success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (type) {
            case 0: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "?????????????????????????????????????????????????????????.");
                    success = false;
                    break;
                }
                if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "????????????????????????????????????.");
                    success = false;
                    break;
                }
                if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                    break;
                }
                c.getPlayer().dropMessage(5, "?????????????????????????????????????????????????????????.");
                success = false;
                break;
            }
            case 1: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "?????????????????????????????????????????????????????????.");
                }
                else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "????????????????????????????????????.");
                }
                else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "?????????????????????????????????????????????????????????.");
                }
                else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    victim.getClient().getSession().write(FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                }
                else {
                    c.getPlayer().dropMessage(5, "?????????????????????????????????????????????????????????.");
                }
                return;
            }
            case 4: {
                final MapleFamily fam = Family.getFamily(c.getPlayer().getFamilyId());
                final List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7) {
                    success = false;
                    break;
                }
                for (final MapleFamilyCharacter chrz : chrs) {
                    final int chr = Find.findChannel(chrz.getId());
                    if (chr == -1) {
                        continue;
                    }
                    final MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                    entry.applyTo(chrr);
                }
                break;
            }
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
            case 8: {
                entry.applyTo(c.getPlayer());
                break;
            }
            case 9:
            case 10: {
                entry.applyTo(c.getPlayer());
                if (c.getPlayer().getParty() != null) {
                    for (final MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                        if (mpc.getId() != c.getPlayer().getId()) {
                            final MapleCharacter chr2 = c.getPlayer().getMap().getCharacterById(mpc.getId());
                            if (chr2 == null) {
                                continue;
                            }
                            entry.applyTo(chr2);
                        }
                    }
                    break;
                }
                break;
            }
        }
        if (success) {
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.sendPacket(FamilyPacket.changeRep(-entry.rep));
            c.getPlayer().useFamilyBuff(entry);
        }
        else {
            c.getPlayer().dropMessage(5, "????????????.");
        }
    }
    
    public static final void FamilyOperation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.getPlayer().dropMessage(1, "?????????????????????????????????????????????????????????.");
        }
        else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            c.getPlayer().dropMessage(1, "???????????????????????????.");
        }
        else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.getPlayer().dropMessage(1, "????????????????????????.");
        }
        else if (addChr.getSeniorId() != 0) {
            c.getPlayer().dropMessage(1, "????????????????????????????????????????????????.");
        }
        else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.getPlayer().dropMessage(1, "???????????????????????????????????????.");
        }
        else if (addChr.getLevel() < c.getPlayer().getLevel() - 10) {
            c.getPlayer().dropMessage(1, "????????????????????????????????????10????????????.");
        }
        else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "???????????????10??????????????????.");
        }
        else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0) {
            c.getPlayer().dropMessage(1, "????????????????????????????????????????????????????????????????????????.");
        }
        else {
            addChr.getClient().getSession().write(FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static final void FamilyPrecept(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleFamily fam = Family.getFamily(c.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
        c.getPlayer().dropMessage(1, "??????????????????????????????.");
    }
    
    public static final void FamilySummon(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int TYPE = 1;
        final MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
        final MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && c.getPlayer().isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost) && c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && c.getPlayer().getEventInstance() == null && tt.getEventInstance() == null) {
            final boolean accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().getSession().write(FamilyPacket.changeRep(-cost.rep));
                tt.useFamilyBuff(cost);
            }
            else {
                tt.dropMessage(5, "?????????????????????????????????????????????????????????.");
            }
        }
        else {
            c.getPlayer().dropMessage(5, "?????????????????????????????????????????????????????????.");
        }
        c.getPlayer().setTeleportName("");
    }
    
    public static final void DeleteJunior(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int juniorid = slea.readInt();
        if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid)) {
            return;
        }
        final MapleFamily fam = Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        final MapleFamilyCharacter oth = c.getPlayer().getMFC();
        final boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        }
        else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " ??????????????? ???????????????", 0);
        if (!fam.splitFamily(juniorid, other)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "????????? (" + other.getName() + ").");
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static final void DeleteSenior(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
            return;
        }
        final MapleFamily fam = Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        final MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        final boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        }
        else {
            mgc.setJunior1(0);
        }
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        c.getPlayer().saveFamilyStatus();
        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " ?????????????????? ??????????????????", 0);
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "????????? (" + mgc.getName() + ") ?????????.");
        c.sendPacket(MaplePacketCreator.enableActions());
    }
    
    public static final void AcceptFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (inviter != null && c.getPlayer().getSeniorId() == 0 && (c.getPlayer().isGM() || !inviter.isHidden()) && inviter.getLevel() - 20 <= c.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(slea.readMapleAsciiString()) && inviter.getNoJuniors() < 2 && c.getPlayer().getLevel() >= 10) {
            final boolean accepted = slea.readByte() > 0;
            inviter.getClient().getSession().write(FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                c.sendPacket(FamilyPacket.getSeniorMessage(inviter.getName()));
                final int old = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getFamilyId();
                final int oldj1 = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getJunior1();
                final int oldj2 = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getJunior2();
                if (inviter.getFamilyId() > 0 && Family.getFamily(inviter.getFamilyId()) != null) {
                    final MapleFamily fam = Family.getFamily(inviter.getFamilyId());
                    c.getPlayer().setFamily((old <= 0) ? inviter.getFamilyId() : old, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                    final MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    }
                    else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old > 0 && Family.getFamily(old) != null) {
                        MapleFamily.mergeFamily(fam, Family.getFamily(old));
                    }
                    else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    if (fam != null) {
                        if (inviter.getNoJuniors() == 1 || old > 0) {
                            fam.resetDescendants();
                        }
                        fam.resetPedigree();
                    }
                }
                else {
                    final int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0);
                        c.getPlayer().setFamily(id, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                        final MapleFamily fam2 = Family.getFamily(id);
                        fam2.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if (old > 0 && Family.getFamily(old) != null) {
                            MapleFamily.mergeFamily(fam2, Family.getFamily(old));
                        }
                        else {
                            fam2.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam2.resetDescendants();
                        fam2.resetPedigree();
                    }
                }
                c.sendPacket(FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
    }
}
