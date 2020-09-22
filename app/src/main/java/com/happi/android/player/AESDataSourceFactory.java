package com.happi.android.player;

import com.google.android.exoplayer2.upstream.DataSource;

public class AESDataSourceFactory implements DataSource.Factory {

        private  DataSource upstream;
        private  byte[] encryptionKey;
        private  byte[] encryptionIv;


        public AESDataSourceFactory(DataSource upstream, byte[] encryptionKey, byte[] encryptionIv) {
                this.upstream = upstream;
                this.encryptionKey = encryptionKey;
                this.encryptionIv = encryptionIv;
        }

        @Override
        public AESDataSource createDataSource() {
                return new AESDataSource(upstream,encryptionKey,encryptionIv);
        }
}
