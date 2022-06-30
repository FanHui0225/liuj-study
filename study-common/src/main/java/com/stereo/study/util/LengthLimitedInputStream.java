package com.stereo.study.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liuj-ai on 2022/6/30.
 */
public class LengthLimitedInputStream extends FilterInputStream {
    private long remaining;

    protected LengthLimitedInputStream(InputStream in, long maxLength) {
        super(in);
        this.remaining = maxLength;
    }

    public int read() throws IOException {
        if (this.remaining > 0L) {
            int v = super.read();
            if (v != -1) {
                --this.remaining;
            }

            return v;
        } else {
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    private int remainingInt() {
        return (int) Math.min(this.remaining, 2147483647L);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.remaining == 0L) {
            return -1;
        } else {
            if ((long) len > this.remaining) {
                len = this.remainingInt();
            }

            int v = super.read(b, off, len);
            if (v != -1) {
                this.remaining -= (long) v;
            }

            return v;
        }
    }

    public int available() throws IOException {
        return Math.min(super.available(), this.remainingInt());
    }

    public long skip(long n) throws IOException {
        long v = super.skip(Math.min(this.remaining, n));
        this.remaining -= v;
        return v;
    }
}