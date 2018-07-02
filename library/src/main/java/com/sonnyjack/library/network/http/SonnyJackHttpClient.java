package com.sonnyjack.library.network.http;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * @author SonnyJack
 * @data 2018/4/25 10:26
 * @idea Android Studio
 * <p>
 * To change this template use Preferences(File|Settings) | Editor | File and Code Templates | Includes
 * Description:
 */

public class SonnyJackHttpClient {

    private OkHttpClient mOkHttpClient;

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private long mConnectionTime = 30;//默认http请求时间30秒
    private long mDownloadTime = 30 * 60;//默认下载超时30分钟

    public SonnyJackHttpClient() {
        this(30, 30 * 60);
    }

    public SonnyJackHttpClient(long connectionTime, long downloadTime) {
        this(connectionTime, downloadTime, (InputStream) null);
    }

    public SonnyJackHttpClient(long connectionTime, long downloadTime, InputStream... inputStreams) {
        setConnectionTimeAndDownloadTime(connectionTime, downloadTime);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(mConnectionTime, TimeUnit.SECONDS)
                .readTimeout(mConnectionTime, TimeUnit.SECONDS)
                .writeTimeout(mConnectionTime, TimeUnit.SECONDS);
        //添加证书
        addCertificates(builder, inputStreams);
        mOkHttpClient = builder.build();
    }

    private SonnyJackHttpClient(OkHttpClient okHttpClient, long connectionTime, long downloadTime) {
        setConnectionTimeAndDownloadTime(connectionTime, downloadTime);
        mOkHttpClient = okHttpClient;
    }

    private void setConnectionTimeAndDownloadTime(long connectionTime, long downloadTime) {
        if (connectionTime > 0) {
            mConnectionTime = connectionTime;
        }
        if (downloadTime > 0) {
            mDownloadTime = downloadTime;
        }
    }

    public SonnyJackHttpClient clone(IHttpCallBack httpCallBack) {
        OkHttpClient okHttpClient = mOkHttpClient.newBuilder()
                .readTimeout(mDownloadTime, TimeUnit.SECONDS)
                .writeTimeout(mDownloadTime, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(httpCallBack)).build();
        SonnyJackHttpClient sonnyJackHttpClient = new SonnyJackHttpClient(okHttpClient, mConnectionTime, mDownloadTime);
        return sonnyJackHttpClient;
    }

    private void addCertificates(OkHttpClient.Builder okHttpClientBuilder, InputStream... inputStreams) {
        if (null == okHttpClientBuilder || null == inputStreams || inputStreams.length <= 0) {
            return;
        }
        /*try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            for (int i = 0, size = inputStreams.length; i < size; ) {
                InputStream certificate = inputStreams[i];
                String certificateAlias = Integer.toString(i++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    certificate.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                //throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                return;
            }
            X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStreams[0]);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Put the certificates a key store.
            char[] password = "password".toCharArray(); // Any password will work.
            KeyStore keyStore = newEmptyKeyStore(password);
            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            // Use it to build an X509 trust manager.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                //throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                return;
            }

            X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);
        }catch (Exception e){

        }
    }

    /**
     * 添加password
     * @param password
     * @return
     * @throws GeneralSecurityException
     */
    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // 这里添加自定义的密码，默认
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
