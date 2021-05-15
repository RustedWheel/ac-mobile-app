package com.makeitez.acsalesapp.utils.helper

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.math.BigInteger
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class EncryptionHelper(private val context: Context) {

    private val keyStore: KeyStore
    private val keyAuthority by lazy {
        "CN=${context.packageName.replace(".", "_")}"
    }

    init {
        keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
        keyStore.load(null)
    }

    fun encrypt(text: String?, keyAlias: String): String? =
        text?.let {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val input = it.toByteArray(charset(UTF_8))
            val publicKey = getOrCreatePublicKey(keyAlias)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            encode(cipher.doFinal(input))
        }

    fun decrypt(encryptedText: String?, keyAlias: String): String? =
        encryptedText?.let {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val input = decode(it)
            val privateKey = getPrivateKey(keyAlias)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return String(cipher.doFinal(input), charset(UTF_8))
        }

    private fun getOrCreatePublicKey(alias: String): Key? = if (keyStore.containsAlias(alias)) {
        (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry).certificate.publicKey
    } else {
        if (Build.VERSION.SDK_INT >= 23) {
            createAsymmetricKeyPairAfterMashMallow(alias).public
        } else {
            createAsymmetricKeyPairBeforeMashMallow(alias).public
        }
    }

    private fun getPrivateKey(alias: String): Key? = if (keyStore.containsAlias(alias)) {
        (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry).privateKey
    } else {
        null
    }

    private fun createAsymmetricKeyPairBeforeMashMallow(alias: String): KeyPair {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE_TYPE)
        val spec = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSubject(X500Principal(keyAuthority))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build()
        keyPairGenerator.initialize(spec)
        return keyPairGenerator.generateKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createAsymmetricKeyPairAfterMashMallow(alias: String): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE_TYPE)
        val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
        keyPairGenerator.initialize(spec)
        return keyPairGenerator.generateKeyPair()
    }

    private fun encode(data: ByteArray) = Base64.encodeToString(data, Base64.DEFAULT)

    private fun decode(value: String) = Base64.decode(value, Base64.DEFAULT)

    companion object {
        private const val KEY_STORE_TYPE = "AndroidKeyStore"
        private const val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
        private const val UTF_8 = "UTF-8"
    }
}