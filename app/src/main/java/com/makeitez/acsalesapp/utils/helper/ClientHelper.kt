package com.makeitez.acsalesapp.utils.helper

import com.makeitez.acsalesapp.utils.extensions.cast
import com.makeitez.acsalesapp.utils.extensions.logD
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

object ClientHelper {

    private const val DEFAULT_TIMEOUT_IN_SECONDS = 60L

    fun getNetworkClient(interceptor: Interceptor, sslVerificationOn: Boolean): OkHttpClient {
        return if (sslVerificationOn) getSafeOkHttpClient(interceptor) else getUnsafeOkHttpClient(interceptor)
    }

    private fun getSafeOkHttpClient(interceptor: Interceptor) =
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

    private fun getUnsafeOkHttpClient(interceptor: Interceptor): OkHttpClient {
        // Instantiate http logger
        val httpLogger = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = logD(message)
        }).apply { setLevel(HttpLoggingInterceptor.Level.BODY) }

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(interceptor)
            .addInterceptor(httpLogger)
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0].cast())
        builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
        return builder.build()
    }
}