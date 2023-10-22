//
// Decompiled by Procyon v0.6.0
//

package sheepy.util.text;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Base85
{
    private static final long Power4 = 52200625L;
    private static final long Power3 = 614125L;
    private static final long Power2 = 7225L;
    private static Encoder RFC1924ENCODER;
    private static Encoder Z85ENCODER;
    private static Encoder ASCII85ENCODER;
    private static Decoder RFC1924DECODER;
    private static Decoder Z85DECODER;
    private static Decoder ASCII85DECODER;

    private static void buildDecodeMap(final byte[] encodeMap, final byte[] decodeMap) {
        Arrays.fill(decodeMap, (byte)(-1));
        for (byte i = 0, len = (byte)encodeMap.length; i < len; ++i) {
            final byte b = encodeMap[i];
            decodeMap[b] = i;
        }
    }

    public static Encoder getRfc1924Encoder() {
        if (Base85.RFC1924ENCODER == null) {
            Base85.RFC1924ENCODER = new Rfc1924Encoder();
        }
        return Base85.RFC1924ENCODER;
    }

    public static Decoder getRfc1924Decoder() {
        if (Base85.RFC1924DECODER == null) {
            Base85.RFC1924DECODER = new Rfc1924Decoder();
        }
        return Base85.RFC1924DECODER;
    }

    public static Encoder getZ85Encoder() {
        if (Base85.Z85ENCODER == null) {
            Base85.Z85ENCODER = new Z85Encoder();
        }
        return Base85.Z85ENCODER;
    }

    public static Decoder getZ85Decoder() {
        if (Base85.Z85DECODER == null) {
            Base85.Z85DECODER = new Z85Decoder();
        }
        return Base85.Z85DECODER;
    }

    public static Encoder getAscii85Encoder() {
        if (Base85.ASCII85ENCODER == null) {
            Base85.ASCII85ENCODER = new Ascii85Encoder();
        }
        return Base85.ASCII85ENCODER;
    }

    public static Decoder getAscii85Decoder() {
        if (Base85.ASCII85DECODER == null) {
            Base85.ASCII85DECODER = new Ascii85Decoder();
        }
        return Base85.ASCII85DECODER;
    }

    public static void main(final String[] args) {
    }

    public abstract static class Encoder
    {
        public int calcEncodedLength(final String data) {
            return this.calcEncodedLength(data.getBytes(StandardCharsets.UTF_8));
        }

        public int calcEncodedLength(final byte[] data) {
            return this.calcEncodedLength(data, 0, data.length);
        }

        public int calcEncodedLength(final byte[] data, final int offset, final int length) {
            if (offset < 0 || length < 0) {
                throw new IllegalArgumentException("Offset and length must not be negative");
            }
            return (int)Math.ceil(length * 1.25);
        }

        public final String encode(final String data) {
            return this.encodeToString(data.getBytes(StandardCharsets.UTF_8));
        }

        public final String encodeToString(final byte[] data) {
            return new String(this.encode(data), StandardCharsets.US_ASCII);
        }

        public final String encodeToString(final byte[] data, final int offset, final int length) {
            return new String(this.encode(data, offset, length), StandardCharsets.US_ASCII);
        }

        public final byte[] encode(final byte[] data) {
            return this.encode(data, 0, data.length);
        }

        public final byte[] encode(final byte[] data, final int offset, final int length) {
            final byte[] out = new byte[this.calcEncodedLength(data, offset, length)];
            final int len = this._encode(data, offset, length, out, 0);
            if (out.length == len) {
                return out;
            }
            return Arrays.copyOf(out, len);
        }

        public final int encode(final byte[] data, final int offset, final int length, final byte[] out, final int out_offset) {
            final int size = this.calcEncodedLength(data, offset, length);
            return this._encode(data, offset, length, out, out_offset);
        }

        public byte[] encodeBlockReverse(final byte[] data) {
            final int size = Math.max(0, (int)Math.ceil(data.length * 1.25));
            final byte[] result = new byte[size];
            this.encodeBlockReverse(data, 0, data.length, result, 0);
            return result;
        }

        public int encodeBlockReverse(byte[] data, final int offset, final int length, final byte[] out, final int out_offset) {
            final int size = (int)Math.ceil(length * 1.25);
            if (offset != 0 || length != data.length) {
                data = Arrays.copyOfRange(data, offset, offset + length);
            }
            BigInteger sum = new BigInteger(1, data);
            final BigInteger b85 = BigInteger.valueOf(85L);
            final byte[] map = this.getEncodeMap();
            for (int i = size + out_offset - 1; i >= out_offset; --i) {
                final BigInteger[] mod = sum.divideAndRemainder(b85);
                out[i] = map[mod[1].intValue()];
                sum = mod[0];
            }
            return size;
        }

        protected int _encodeDangling(final byte[] encodeMap, final byte[] out, final int wi, final ByteBuffer buffer, final int leftover) {
            long sum = (long)buffer.getInt(0) & 0xFFFFFFFFL;
            out[wi] = encodeMap[(int)(sum / 52200625L)];
            sum %= 52200625L;
            out[wi + 1] = encodeMap[(int)(sum / 614125L)];
            sum %= 614125L;
            if (leftover >= 2) {
                out[wi + 2] = encodeMap[(int)(sum / 7225L)];
                sum %= 7225L;
                if (leftover >= 3) {
                    out[wi + 3] = encodeMap[(int)(sum / 85L)];
                }
            }
            return leftover + 1;
        }

        protected int _encode(final byte[] in, int ri, final int rlen, final byte[] out, int wi) {
            final int wo = wi;
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            final byte[] buf = buffer.array();
            final byte[] encodeMap = this.getEncodeMap();
            for (int loop = rlen / 4; loop > 0; --loop, ri += 4) {
                System.arraycopy(in, ri, buf, 0, 4);
                wi = this._writeData((long)buffer.getInt(0) & 0xFFFFFFFFL, encodeMap, out, wi);
            }
            final int leftover = rlen % 4;
            if (leftover == 0) {
                return wi - wo;
            }
            buffer.putInt(0, 0);
            System.arraycopy(in, ri, buf, 0, leftover);
            return wi - wo + this._encodeDangling(encodeMap, out, wi, buffer, leftover);
        }

        protected int _writeData(long sum, final byte[] map, final byte[] out, final int wi) {
            out[wi] = map[(int)(sum / 52200625L)];
            sum %= 52200625L;
            out[wi + 1] = map[(int)(sum / 614125L)];
            sum %= 614125L;
            out[wi + 2] = map[(int)(sum / 7225L)];
            sum %= 7225L;
            out[wi + 3] = map[(int)(sum / 85L)];
            out[wi + 4] = map[(int)(sum % 85L)];
            return wi + 5;
        }

        protected abstract byte[] getEncodeMap();

        public String getCharset() {
            return new String(this.getEncodeMap(), StandardCharsets.US_ASCII);
        }
    }

    public static class Rfc1924Encoder extends Encoder
    {
        private static final byte[] ENCODE_MAP;

        @Override
        protected byte[] getEncodeMap() {
            return Rfc1924Encoder.ENCODE_MAP;
        }

        static {
            ENCODE_MAP = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~".getBytes(StandardCharsets.US_ASCII);
        }
    }

    public static class IPv6Encoder extends Encoder
    {
        private static final byte[] ENCODE_MAP;

        @Override
        protected byte[] getEncodeMap() {
            return IPv6Encoder.ENCODE_MAP;
        }

        static {
            ENCODE_MAP = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~".getBytes(StandardCharsets.US_ASCII);
        }
    }

    public static class Z85Encoder extends Encoder
    {
        private static final byte[] ENCODE_MAP;

        @Override
        protected byte[] getEncodeMap() {
            return Z85Encoder.ENCODE_MAP;
        }

        static {
            ENCODE_MAP = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#".getBytes(StandardCharsets.US_ASCII);
        }
    }

    public static class Ascii85Encoder extends Encoder
    {
        private static final byte[] ENCODE_MAP;
        private boolean useZ;
        private boolean useY;
        private final ReadWriteLock lock;

        public Ascii85Encoder() {
            this.useZ = true;
            this.useY = true;
            this.lock = new ReentrantReadWriteLock(true);
        }

        @Override
        protected byte[] getEncodeMap() {
            return Ascii85Encoder.ENCODE_MAP;
        }

        @Override
        public int calcEncodedLength(final byte[] data, final int offset, final int length) {
            int result = super.calcEncodedLength(data, offset, length);
            if (this.useZ || this.useY) {
                final ByteBuffer buffer = ByteBuffer.wrap(data);
                for (int i = offset, len = offset + length - 4; i <= len; i += 4) {
                    if (this.useZ && data[i] == 0) {
                        if (buffer.getInt(i) == 0) {
                            result -= 4;
                        }
                    }
                    else if (this.useY && data[i] == 32 && buffer.getInt(i) == 538976288) {
                        result -= 4;
                    }
                }
            }
            return result;
        }

        public void setZeroCompression(final boolean compress) {
            this.lock.writeLock().lock();
            try {
                this.useZ = compress;
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }

        public boolean getZeroCompression() {
            this.lock.readLock().lock();
            try {
                return this.useZ;
            }
            finally {
                this.lock.readLock().unlock();
            }
        }

        public void setSpaceCompression(final boolean compress) {
            this.lock.writeLock().lock();
            try {
                this.useY = compress;
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }

        public boolean getSpaceCompression() {
            this.lock.readLock().lock();
            try {
                return this.useY;
            }
            finally {
                this.lock.readLock().unlock();
            }
        }

        @Override
        protected int _encode(final byte[] in, final int ri, final int rlen, final byte[] out, final int wi) {
            this.lock.readLock().lock();
            try {
                return super._encode(in, ri, rlen, out, wi);
            }
            finally {
                this.lock.readLock().unlock();
            }
        }

        @Override
        protected int _writeData(final long sum, final byte[] map, final byte[] out, int wi) {
            if (this.useZ && sum == 0L) {
                out[wi++] = 122;
            }
            else {
                if (!this.useY || sum != 538976288L) {
                    return super._writeData(sum, map, out, wi);
                }
                out[wi++] = 121;
            }
            return wi;
        }

        static {
            ENCODE_MAP = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstu".getBytes(StandardCharsets.US_ASCII);
        }
    }

    public abstract static class Decoder
    {
        public int calcDecodedLength(final String data) {
            return this.calcDecodedLength(data.getBytes(StandardCharsets.US_ASCII));
        }

        public int calcDecodedLength(final byte[] data) {
            return this.calcDecodedLength(data, 0, data.length);
        }

        public int calcDecodedLength(final byte[] data, final int offset, final int length) {
            if (length % 5 == 1) {
                throw new IllegalArgumentException(length + " is not a valid Base85/" + this.getName() + " data length.");
            }
            return (int)(length * 0.8);
        }

        public final String decode(final String data) {
            return new String(this.decode(data.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.UTF_8);
        }

        public final byte[] decode(final byte[] data) {
            return this.decode(data, 0, data.length);
        }

        public final byte[] decodeToBytes(final String data) {
            return this.decode(data.getBytes(StandardCharsets.US_ASCII));
        }

        public final byte[] decode(final byte[] data, final int offset, final int length) {
            final byte[] result = new byte[this.calcDecodedLength(data, offset, length)];
            try {
                final int len = this._decode(data, offset, length, result, 0);
                if (result.length != len) {
                    return Arrays.copyOf(result, len);
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                this.throwMalformed(ex);
            }
            return result;
        }

        public final int decode(final byte[] data, final int offset, final int length, final byte[] out, final int out_offset) {
            final int size = this.calcDecodedLength(data, offset, length);
            try {
                this._decode(data, offset, length, out, out_offset);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                this.throwMalformed(ex);
            }
            return size;
        }

        public byte[] decodeBlockReverse(final byte[] data) {
            final int size = Math.max(0, (int)Math.ceil(data.length * 0.8));
            final byte[] result = new byte[size];
            this.decodeBlockReverse(data, 0, data.length, result, 0);
            return result;
        }

        public int decodeBlockReverse(final byte[] data, final int offset, final int length, final byte[] out, final int out_offset) {
            final int size = (int)Math.ceil(length * 0.8);
            BigInteger sum = BigInteger.ZERO;
            final BigInteger b85 = BigInteger.valueOf(85L);
            final byte[] map = this.getDecodeMap();
            try {
                for (int i = offset, len = offset + length; i < len; ++i) {
                    sum = sum.multiply(b85).add(BigInteger.valueOf(map[data[i]]));
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                this.throwMalformed(ex);
            }
            System.arraycopy(sum.toByteArray(), 0, out, out_offset, size);
            return size;
        }

        public boolean test(final String data) {
            return this.test(data.getBytes(StandardCharsets.US_ASCII));
        }

        public boolean test(final byte[] data) {
            return this.test(data, 0, data.length);
        }

        public boolean test(final byte[] encoded_data, final int offset, final int length) {
            return this._test(encoded_data, offset, length);
        }

        protected boolean _test(final byte[] data, final int offset, final int length) {
            final byte[] valids = this.getDecodeMap();
            try {
                for (int i = offset, len = offset + length; i < len; ++i) {
                    final byte e = data[i];
                    if (valids[e] < 0) {
                        return false;
                    }
                }
                this.calcDecodedLength(data, offset, length);
            }
            catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                return false;
            }
            return true;
        }

        protected RuntimeException throwMalformed(final Exception ex) {
            throw new IllegalArgumentException("Malformed Base85/" + this.getName() + " data", (Throwable)ex);
        }

        protected int _decodeDangling(final byte[] decodeMap, final byte[] in, final int ri, final ByteBuffer buffer, final int leftover) {
            if (leftover == 1) {
                this.throwMalformed(null);
            }
            long sum = decodeMap[in[ri]] * 52200625L + decodeMap[in[ri + 1]] * 614125L + 85L;
            if (leftover >= 3) {
                sum += decodeMap[in[ri + 2]] * 7225L;
                if (leftover >= 4) {
                    sum += decodeMap[in[ri + 3]] * 85;
                }
                else {
                    sum += 7225L;
                }
            }
            else {
                sum += 621350L;
            }
            buffer.putInt(0, (int)sum);
            return leftover - 1;
        }

        protected int _decode(final byte[] in, int ri, final int rlen, final byte[] out, int wi) {
            final int wo = wi;
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            final byte[] buf = buffer.array();
            final byte[] decodeMap = this.getDecodeMap();
            for (int loop = rlen / 5; loop > 0; --loop, wi += 4, ri += 5) {
                this._putData(buffer, decodeMap, in, ri);
                System.arraycopy(buf, 0, out, wi, 4);
            }
            int leftover = rlen % 5;
            if (leftover == 0) {
                return wi - wo;
            }
            leftover = this._decodeDangling(decodeMap, in, ri, buffer, leftover);
            System.arraycopy(buf, 0, out, wi, leftover);
            return wi - wo + leftover;
        }

        protected void _putData(final ByteBuffer buffer, final byte[] map, final byte[] in, final int ri) {
            buffer.putInt(0, (int)(map[in[ri]] * 52200625L + map[in[ri + 1]] * 614125L + map[in[ri + 2]] * 7225L + map[in[ri + 3]] * 85 + map[in[ri + 4]]));
        }

        protected abstract byte[] getDecodeMap();

        protected abstract String getName();
    }

    public static class Rfc1924Decoder extends Decoder
    {
        private static final byte[] DECODE_MAP;

        @Override
        protected String getName() {
            return "RFC1924";
        }

        @Override
        protected byte[] getDecodeMap() {
            return Rfc1924Decoder.DECODE_MAP;
        }

        static {
            DECODE_MAP = new byte[127];
            Base85.buildDecodeMap(Rfc1924Encoder.ENCODE_MAP, Rfc1924Decoder.DECODE_MAP);
        }
    }

    public static class Z85Decoder extends Decoder
    {
        private static final byte[] DECODE_MAP;

        @Override
        protected String getName() {
            return "Z85";
        }

        @Override
        protected byte[] getDecodeMap() {
            return Z85Decoder.DECODE_MAP;
        }

        static {
            DECODE_MAP = new byte[127];
            Base85.buildDecodeMap(Z85Encoder.ENCODE_MAP, Z85Decoder.DECODE_MAP);
        }
    }

    private static class Ascii85Decoder extends Decoder
    {
        private static final byte[] zeros;
        private static final byte[] spaces;
        private static final byte[] DECODE_MAP;

        @Override
        public int calcDecodedLength(final byte[] encoded_data, final int offset, final int length) {
            int deflated = length;
            int len;
            int i;
            for (len = offset + length, i = offset; i < len; i += 5) {
                if (encoded_data[i] == 122 || encoded_data[i] == 121) {
                    deflated += 4;
                    i -= 4;
                }
            }
            if (i != len) {
                for (i -= 5; i < len && (encoded_data[i] == 122 || encoded_data[i] == 121); i -= 4) {
                    deflated += 4;
                }
            }
            return super.calcDecodedLength(null, 0, deflated);
        }

        @Override
        public boolean test(final byte[] encoded_data, final int offset, final int length) {
            try {
                int deviation = 0;
                for (int i = offset, len = offset + length; i < len; ++i) {
                    final byte e = encoded_data[i];
                    if (Ascii85Decoder.DECODE_MAP[e] < 0) {
                        if ((deviation + i - offset) % 5 != 0 || (e != 122 && e != 121)) {
                            return false;
                        }
                        deviation += 4;
                    }
                }
                super.calcDecodedLength(null, 0, length + deviation);
            }
            catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
                return false;
            }
            return true;
        }

        @Override
        protected String getName() {
            return "Ascii85";
        }

        @Override
        protected byte[] getDecodeMap() {
            return Ascii85Decoder.DECODE_MAP;
        }

        @Override
        protected int _decode(final byte[] in, int ri, final int rlen, final byte[] out, int wi) {
            final int re = ri + rlen;
            final int wo = wi;
            final ByteBuffer buffer = ByteBuffer.allocate(4);
            final byte[] buf = buffer.array();
            final byte[] decodeMap = this.getDecodeMap();
            final int max = ri + rlen;
            final int max2 = max - 4;
            while (ri < max) {
                while (ri < max && (in[ri] == 122 || in[ri] == 121)) {
                    byte[] src = null;
                    switch (in[ri++]) {
                        case 122: {
                            src = Ascii85Decoder.zeros;
                            break;
                        }
                        case 121: {
                            src = Ascii85Decoder.spaces;
                            break;
                        }
                    }
                    System.arraycopy(src, 0, out, wi, 4);
                    wi += 4;
                }
                if (ri >= max2) {
                    break;
                }
                this._putData(buffer, decodeMap, in, ri);
                ri += 5;
                System.arraycopy(buf, 0, out, wi, 4);
                wi += 4;
            }
            if (re == ri) {
                return wi - wo;
            }
            final int leftover = this._decodeDangling(decodeMap, in, ri, buffer, re - ri);
            System.arraycopy(buf, 0, out, wi, leftover);
            return wi - wo + leftover;
        }

        static {
            zeros = new byte[] { 0, 0, 0, 0 };
            spaces = new byte[] { 32, 32, 32, 32 };
            DECODE_MAP = new byte[127];
            Base85.buildDecodeMap(Ascii85Encoder.ENCODE_MAP, Ascii85Decoder.DECODE_MAP);
        }
    }
}
import java.nio.charset.StandardCharsets;
import sheepy.util.text.Base85;
import java.util.Scanner;

//
// Decompiled by Procyon v0.6.0
//

class CenoBytes
{
    public static void main(final String[] args) {
        final byte[] kitbal = "b7&q76n=gW3>RT?YXJcPb7*05YXJcPb7*05YXN?K3}_w=6n=hw3>RT?YXJcPb7*056n+5#b7*05YXN?K3}#_+YXJcPb7*05YXJcPb7*f36n+5#b7*05YXJd%3>O{^6n=hweHR{bYXJcPb7*05YXJcPb7*05YkqzKb7*05Ykq!y3>RT?YXJcPb7*05YXJcPb7)}=6afJb3>RT?YXJcP3>O}9Yb$;Za~B>B6ny~!FK8YO6n=gIb7*056n=hwb7*0GYYqVc4;LN{6n+5#4`^X>6n=hw3}<0-H+}&D3>O{^7k&X<3>RT?Ykq!y3>O{^6n=hwFKA(MHvxWr3>O{^WB~yI3>O{^6b@Yhb2J_eYkq!y3>RSy6n=hw3>Q6eYh3|;3}|k0YXJcP4`^X>6n=hwb7)}=6n=hwd}v`V6kP!UFBcwiYXJd$a};4%7XbkQ3>O_PYXMyW4;LPCYkdI$b7x@<6n=dHa};3?YXJccb7)~MYXJd$3};~t6n=dHb7&q77Y+dd3}_uLYkqzK4;OB76n+7H3}|5u6ajq!b7(z%YXN?K3}+n;6n=hwb7&oMHvs`%b7&n76n+5#4-_79YkqzK4`^X7WC0Eib7&oMWB~zvb7x_46ajq!d}v{EYkdI$a~B>CYXMyWa};56YYqVpb3$P*YXMyWb2MRcH-3H&a~B?R7k&-_d>3wVYaam}b97;HWB~yIb2MRcYXN-$a}*v86kP!UFKB6UYaM<cb75f)6nz1Gb7&q76b@Yhb2MQNWB~yIFBBdP6ajq!d}v{EYXJcPa}+&tYXNuxa};iJ6n+7HFKA&dYkdv@b7*01WB~yIa}i;3YXJ!Xb95eTYX|`Wb2MRc7XblXb2MRcYj^<x4`^X*YaanCb2J_eHvs`%3>O{^V}5>q3}|6}7k++zb7*dIYYqVpb7&n66n=hw3>RT?WPN^q3}|6&6n<R+a}*v86n+j}3>O}9YYu&W3>RT?b$)(bb7(yc6n+kV3>O}9Yaf1o3>O{^6afJaFBcvSHvs_&3>O{^YXJ^^3^ZYKH-3Iy3}_t<6n=hP3>RT?WPN^q3>R)L6n=hPb7*c26kP#b3>O|QYXJ@ca};3?YbyZ(b7*05YXJcPb7*05YXJd$d}kgF6n=gVb7)}>6n%aHb7*05YXJcP4`*R=YXJd$d}v{EYXJcPb7*05YXJcPa};iJYXJcP4`*R=YXJcPb7*05YXJcPa};iJYXJcPb7)}>WB~yIb7)}>WB~yIb7&oXYXJcPb7*05YXJd$d}v{EYXJcP4`*R=YXJcPb7*05YXN-@b7*05YXJcPb7&oXYXN=^3};~".getBytes();
        final byte[] amhoamho = "DEADFACE".getBytes();
        final byte[] amho = "7yZuW4pATQ".getBytes();
        final Scanner scanner = new Scanner(System.in);
        System.out.println("lilith's favorite cereal is also her favorite monster / ghoul / daemon / phantasm / poltergeist / creature / extraterrestrial.");
        System.out.print("To get both answers (and the flag), enter her entity (with special characters) as a password: ");
        final String sayongchaAmho = scanner.nextLine().trim();
        final Base85.Z85Decoder zdecoder = new Base85.Z85Decoder();
        final Base85.Rfc1924Decoder decoder = new Base85.Rfc1924Decoder();
        final byte[] amhoDec = zdecoder.decode(amho);
        for (int j = 0; j < amhoDec.length; ++j) {
            final int jdx = j % amhoamho.length;
            final byte[] array = amhoDec;
            final int n = j;
            array[n] ^= amhoamho[jdx];
        }
        final String amhoDecStr = new String(amhoDec, StandardCharsets.UTF_8);
        if (!amhoDecStr.equals(sayongchaAmho)) {
            System.out.println("Sorry, that is not the correct monster / cereal / password.  Please try again.");
            System.exit(0);
        }
        final byte[] kitbalDec = decoder.decode(kitbal);
        for (int i = 0; i < kitbalDec.length; ++i) {
            final int idx = i % amhoDec.length;
            final byte[] array2 = kitbalDec;
            final int n2 = i;
            array2[n2] ^= amhoDec[idx];
        }
        final String kitbalStr = new String(kitbalDec, StandardCharsets.UTF_8);
        System.out.println("If anyone has wisdom, let him / her decompile the Java code and crack the encrypted cereal!");
        System.out.println("Congratulations, Oh Wise One!");
        System.out.println("");
        System.out.println(kitbalStr);
    }
}
