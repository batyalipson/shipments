package com.shtibel.truckies.jsonParser;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Shtibel on 15/11/2015.
 */
public class CustomRequestBody extends RequestBody {

    public static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

    MediaType contentType;
    byte[] content;
    private final ProgressListener listener;

    public CustomRequestBody(final MediaType contentType, final byte[] content, ProgressListener listener) {
        this.contentType = contentType;
        this.content = content;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return content.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        Log.d("11111", "write to");
        try {
            InputStream is = new ByteArrayInputStream(content);
            source = Okio.source(is);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                this.listener.transferred(total);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Util.closeQuietly(source);
        }
    }

}