package com.happi.android.player;

import android.os.AsyncTask;

import com.happi.android.utils.KeyUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AsyncDecrypt extends AsyncTask<Void, String, String> {

    private static Charset charset = StandardCharsets.ISO_8859_1;
    private static final String AWS_ACCESS_KEY_ID = KeyUtils.AWS_ACCESS_KEY_ID;
    private static final String AWS_SECRET_KEY = KeyUtils.AWS_SECRET_KEY;
    private String KEY_STRING = KeyUtils.KEY_STRING;
    private String DECODE_KEY;

    public interface AsyncDecryptListener {
        void processFinish(String plainText);
    }
    private AsyncDecryptListener listener;
    @Override
    protected String doInBackground(Void... voids) {

//        final AWSCredentials creds = new AWSCredentials() {
//            @Override
//            public String getAWSAccessKeyId() {
//                return AWS_ACCESS_KEY_ID;
//            }
//
//            @Override
//            public String getAWSSecretKey() {
//                return AWS_SECRET_KEY;
//            }
//        };
//
//        AWSKMSClient kms = new AWSKMSClient(creds);
//
//
//        try {
//            DECODE_KEY = new String(KEY_STRING.getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        ByteBuffer byteBuffer = getByteBuffer(DECODE_KEY);
//        Map<String,String> contextMap = new HashMap<>();
//        contextMap.put("service","elastictranscoder.amazonaws.com");
//
//        DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(byteBuffer).withEncryptionContext(contextMap);
//        DecryptResult decryptResult = kms.decrypt(decryptRequest);
//        ByteBuffer plainText= decryptResult.getPlaintext();
//        String plaintext = getString(decryptResult.getPlaintext());
//
//        Log.e("AWS","#########--> "+plaintext);

        return null;
    }
    @Override
    protected void onPostExecute(String plainText) {
        super.onPostExecute(plainText);
        if(listener != null){
            listener.processFinish(plainText);
        }
    }
    private static String getString(ByteBuffer b) {
        byte[] byteArray = new byte[b.remaining()];
        b.get(byteArray);
        return new String(byteArray, charset);
    }
//    private static ByteBuffer getByteBuffer(String str) {
//        byte[] bytes = Base64.decode(str.getBytes(charset));
//        return ByteBuffer.wrap(bytes);
//    }
    public void setListener(AsyncDecryptListener listener){
        this.listener = listener;
    }
}
