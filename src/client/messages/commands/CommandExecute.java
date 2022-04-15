package client.messages.commands;

import constants.ServerConstants.CommandType;
import client.MapleClient;

public abstract class CommandExecute
{
    public abstract int execute(final MapleClient p0, final String[] p1);
    
    public CommandType getType() {
        return CommandType.NORMAL;
    }
    
    enum ReturnValue
    {
        DONT_LOG, 
        LOG;
    }
    
    public abstract static class TradeExecute extends CommandExecute
    {
        @Override
        public CommandType getType() {
            return CommandType.TRADE;
        }
    }
}
