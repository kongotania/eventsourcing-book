package de.nebulit.backoffice.cryptoshredding.encryption

import org.springframework.stereotype.Component
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface Encrypter {
    fun encrypt(
        secretKey: SecretKey,
        cleartext: String,
    ): ByteArray
}

/**
 * based on
 * https://github.com/everest-engineering/axon-crypto-shredding-extension/blob/main/src/main/java/engineering/everest/axon/cryptoshredding/encryption/DefaultAesEncrypter.java
 */
@Component
class DefeaultAesEncrypter(
    val secureRandom: SecureRandom = SecureRandom(),
) : Encrypter {
    override fun encrypt(
        secretKey: SecretKey,
        cleartext: String,
    ): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val initializationVector = createInitializationVector()
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            GCMParameterSpec(AUTHENTICATION_TAG_SIZE_BITS, initializationVector),
            secureRandom,
        )
        return concatInitializationVectorAndCipherText(
            initializationVector,
            cipher.doFinal(cleartext.toByteArray()),
        )
    }

    private fun createInitializationVector(): ByteArray {
        val initializationVector = ByteArray(INITIALIZATION_VECTOR_LENGTH_BYTES)
        secureRandom!!.nextBytes(initializationVector)
        return initializationVector
    }

    private fun concatInitializationVectorAndCipherText(
        initializationVector: ByteArray,
        cipherText: ByteArray,
    ): ByteArray {
        val initializationVectorWithCipherText = ByteArray(initializationVector.size + cipherText.size)
        System.arraycopy(
            initializationVector,
            0,
            initializationVectorWithCipherText,
            0,
            INITIALIZATION_VECTOR_LENGTH_BYTES,
        )
        System.arraycopy(cipherText, 0, initializationVectorWithCipherText, initializationVector.size, cipherText.size)
        return initializationVectorWithCipherText
    }

    companion object {
        val CIPHER_ALGORITHM: String = "AES/GCM/NoPadding"
        val INITIALIZATION_VECTOR_LENGTH_BYTES: Int = 12
        val AUTHENTICATION_TAG_SIZE_BITS: Int = 128
    }
}
