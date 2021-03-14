package org.wolkenproject.utils;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

// this class represents an UNSIGNED variable integer
// that has a range of 1 - 8 bytes
public class VarInt extends SerializableI {
    private BigInteger value;

    public VarInt() {
        this(0);
    }

    public VarInt(String value) {
        this(new BigInteger(value));
    }

    public VarInt(long value) {
        this.value = new BigInteger(Long.toString(value));
    }

    public VarInt(BigInteger bigInteger) {
        this.value = bigInteger;
    }

    public static void writeCompactUInt32(int version, OutputStream stream) {
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        int bits = value.bitLength();

        if (bits <= 7) {
            stream.write(value & 0x7F);
        } else if (bits <= 15) {
            byte v[] = Utils.takeApartShort(value);
            stream.write((v[0] & 0x3F));
            stream.write((v[1] & 0x7F));
        } else if (value < 2097151) {
            byte v[] = Utils.takeApartShort(value);
            stream.write((v[0] & 0x3F));
            stream.write((v[1] & 0x7F));
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new VarInt();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(VarInt.class);
    }
}
