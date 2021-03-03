package com.github.passit.core.platform

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.github.passit.core.domain.EncryptedData
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {

    init {
        generateAndStoreKey()
    }

    fun getCipherForEncryption(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    fun getCipherForDecryption(initializationVector: ByteArray): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, initializationVector)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return cipher
    }

    fun encrypt(plainData: String, cipher: Cipher): EncryptedData {
        val encrypted = cipher.doFinal(plainData.toByteArray(Charsets.UTF_8))
        return EncryptedData(encrypted, cipher.iv)
    }

    fun decrypt(encryptedData: ByteArray, cipher: Cipher): String {
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEY_PROVIDER)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun generateAndStoreKey() {
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_PROVIDER).apply {
            init(buildKeyGenParamSpec())
            generateKey()
        }
    }

    private fun buildKeyGenParamSpec(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
            .build()
    }

    companion object {
        private const val KEY_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "AppKey"
        private const val TRANSFORMATION =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
    }
}