package com.happi.android.player;


import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESDataSource implements DataSource {
    private final DataSource upstream;
    private final byte[] encryptionKey;
    private final byte[] encryptionIv;
    private long bytesRemaining;
    private ByteArrayInputStream inputStream;
    private CipherInputStream cipherInputStream;

     AESDataSource(DataSource upstream, byte[] encryptionKey, byte[] encryptionIv) {
        this.upstream = upstream;
        this.encryptionKey = encryptionKey;
        this.encryptionIv = encryptionIv;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Log.e("AWS","#########--> "+dataSpec.toString());
        DataSourceInputStream encryptedInputStream = new DataSourceInputStream(upstream, dataSpec);
        Cipher cipher = createAESCipher(encryptionKey, encryptionIv);
        CipherInputStream cipherInputStream = new CipherInputStream(encryptedInputStream, cipher);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] block = new byte[2048];
        int i;
        while ((i = cipherInputStream.read(block)) != -1) {
            baos.write(block, 0, i);
        }
        baos.close();
        inputStream = new ByteArrayInputStream(baos.toByteArray());
        if (dataSpec.length != C.LENGTH_UNSET) {
            bytesRemaining = dataSpec.length;
        } else {
            bytesRemaining = inputStream.available();
            if (bytesRemaining == 0) {
                bytesRemaining = C.LENGTH_UNSET;
            }
        }
        return bytesRemaining;

    }

    private Cipher createAESCipher(byte[] encryptionKey, byte[] encryptionIv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        int ivIndex = encryptionIv.length - 16;

        Key cipherKey = new SecretKeySpec(encryptionKey, "AES");
        AlgorithmParameterSpec cipherIV = new IvParameterSpec(encryptionIv);

        try {
            cipher.init(Cipher.DECRYPT_MODE, cipherKey,new IvParameterSpec(encryptionIv, ivIndex, 16));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cipher;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    private byte[] cipherInputStreamToByteArray(CipherInputStream cipherInputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        while ((len = cipherInputStream.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos.toByteArray();
    }



    @Override
    public void close() throws IOException {
        if (cipherInputStream != null) {
            cipherInputStream = null;
            upstream.close();
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength){
//        Assertions.checkState(cipherInputStream != null);
//        int bytesRead = cipherInputStream.read(buffer, offset, readLength);
//        if (bytesRead < 0) {
//            return -1;
//        }
//        return bytesRead;
//
//        }

        if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        } else {
            int bytesRead;
            int bytesToRead = bytesRemaining == C.RESULT_END_OF_INPUT ? readLength : (int) Math.min(bytesRemaining, readLength);
            bytesRead = inputStream.read(buffer, offset, bytesToRead);
            if (bytesRead > 0) {
                if (bytesRemaining != C.RESULT_END_OF_INPUT) {
                    bytesRemaining -= bytesRead;
                }
            }
            return bytesRead;
        }
    }

    @Override
    public Uri getUri() {
        return null;
    }
}






