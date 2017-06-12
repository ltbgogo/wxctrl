package com.abc.wxctrl.utility;

import java.util.UUID;
import org.apache.commons.codec.digest.Crypt;

/**
 * 产生一个24位的UUID
 */
public class ShortUUID {

    public static void main(String[] args) {
        System.out.println(Crypt.crypt("sddddddddddddddd", "fsfs"));
        System.out.println(randomUUID12());
        System.out.println(randomUUID());
    }

    public static String fromString(String name) {
        return toShortString(UUID.fromString(name));
    }

    public static String nameUUIDFromBytes(byte[] bytes) {
        return toShortString(UUID.nameUUIDFromBytes(bytes));
    }

    public static String randomUUID() {
        return toShortString(UUID.randomUUID());
    }
    
    public static String randomUUID12() {
        return toShortString12(UUID.randomUUID());
    }

    private static String toShortString(UUID u) {
        return UUIDtoString(u);
    }

    private static String toShortString12(UUID u) {
        return UUIDtoString12(u);
    }

    private static String UUIDtoString(UUID u) {
        long mostSigBits = u.getMostSignificantBits();
        long leastSigBits = u.getLeastSignificantBits();
        return (digits(mostSigBits >> 32, 8) + digits(mostSigBits >> 16, 4)
                + digits(mostSigBits, 4) + digits(leastSigBits >> 48, 4) + digits(
                leastSigBits, 12));
    }

    private static String UUIDtoString12(UUID u) {
        String uuid24 = UUIDtoString(u);
        return uuid24.substring(6, 18);
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toString(hi | (val & (hi - 1)), 36).substring(1);
    }
}
