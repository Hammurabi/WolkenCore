package org.wolkenproject.core.events;

import org.wolkenproject.core.Account;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.utils.Utils;

public class NewAccountEvent extends Event {
    private byte    address[];

    public NewAccountEvent(byte[] address) {
        super();
        this.address    = address;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().newAccount(address);
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Account Registration".getBytes(), address);
    }
}
