package org.bitcorej.chain.vsys;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;

public class HashUtil {
    public static final ThreadLocal<Digest> BLAKE2B256 = new ThreadLocal<Digest>();
    public static final ThreadLocal<Digest> KECCAK256 = new ThreadLocal<Digest>();
    public static final ThreadLocal<Digest> SHA256 = new ThreadLocal<Digest>();
    public static final ThreadLocal<Digest> BLAKE2B160 = new ThreadLocal<Digest>();

    private static Digest digest(ThreadLocal<Digest> cache) {
        Digest d = cache.get();
        if (d == null) {
            if (cache == BLAKE2B256) {
                d = new Blake2bDigest(256);
            } else if (cache == KECCAK256) {
                d = new KeccakDigest(256);
            } else if (cache == SHA256) {
                d = new SHA256Digest();
            } else if (cache == BLAKE2B160) {
                d = new Blake2bDigest(160);
            }
            cache.set(d);
        }
        return d;
    }

    public static byte[] hash(byte[] message, int ofs, int len, ThreadLocal<Digest> alg) {
        Digest d = digest(alg);
        byte[] res = new byte[d.getDigestSize()];
        d.update(message, ofs, len);
        d.doFinal(res, 0);
        return res;
    }

    public static byte[] secureHash(byte[] message, int ofs, int len) {
        byte[] blake2b = hash(message, ofs, len, HashUtil.BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, HashUtil.KECCAK256);
    }

    public static byte[] hashB(byte[] message) {
        return hash(message, 0, message.length, HashUtil.BLAKE2B256);
    }

    public static byte[] hashB160(byte[] message) {
        return hash(message, 0, message.length, HashUtil.BLAKE2B160);
    }

    public static byte[] hashB(byte[] message, int size) {
        Digest d = new Blake2bDigest(size);
        byte[] res = new byte[d.getDigestSize()];
        d.update(message, 0, message.length);
        d.doFinal(res, 0);
        return res;
    }

    public static byte[] hashB(byte[] message, int ofs, int len) {
        return hash(message, ofs, len, HashUtil.BLAKE2B256);
    }

    public static byte[] doubleHashB(byte[] message) {
        byte[] blake2b = hash(message, 0, message.length, HashUtil.BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, HashUtil.BLAKE2B256);
    }

    public static byte[] doubleHashB(byte[] message, int ofs, int len) {
        byte[] blake2b = hash(message, ofs, len, HashUtil.BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, HashUtil.BLAKE2B256);
    }
}
