package com.example.hae.model;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public abstract class DownloadCallback extends UrlRequest.Callback {
    private static final String TAG = "DownloadCallback";
    private final ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private final WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    int count;

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        int httpStatusCode = info.getHttpStatusCode();
        if (httpStatusCode == 200) {
            // The request was fulfilled. Start reading the response.
            request.read(ByteBuffer.allocateDirect(102400));
        } else if (httpStatusCode == 503) {
            // The service is unavailable. You should still check if the request
            // contains some data.
            request.read(ByteBuffer.allocateDirect(102400));
        }else{
            request.read(ByteBuffer.allocateDirect(102400));
        }

    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        byteBuffer.flip();

        try {
            receiveChannel.write(byteBuffer);
        } catch (IOException e) {
            android.util.Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        // Reset the buffer to prepare it for the next read
        byteBuffer.clear();

        // Continue reading the request
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        String json = bytesReceived.toString();
        onSucceeded(request, info, json);
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        onFailed(request,info,"");
    }

    protected abstract void onSucceeded(
            UrlRequest request, UrlResponseInfo info, String bodyBytes);
    protected abstract void onFailed(
            UrlRequest request, UrlResponseInfo info, String bodyBytes);

}
