package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class Block extends BlockHeader {
    public static int UniqueIdentifierLength = 32;
    private Set<TransactionI>   transactions;

    public Block() {}

    public Block(Block parent) {
    }

    public final int countLength() {
        return asByteArray().length;
    }

    public final BlockHeader getBlockHeader() {
        return new BlockHeader(getVersion(), getTimestamp(), getParentHash(), getMerkleRoot(), getBits(), getNonce());
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        header.write(stream);
        Utils.writeInt(transactions.size(), stream);
        for (TransactionI transaction : transactions)
        {
            // use serialize here to write transaction serial id
            transaction.serialize(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        header.read(stream);
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            transactions.add(Context.getInstance().getSerialFactory().fromStream(stream));
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Block();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Block.class);
    }
}
