package de.nebulit.backoffice.cryptoshredding.encryption

import org.springframework.stereotype.Component
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface Decrypter {
    fun decrypt(
        secretKey: SecretKey,
        initializationVectorAndCipherText: ByteArray,
    ): String
}

/**
 * Based on
 * https://github.com/everest-engineering/axon-crypto-shredding-extension/blob/main/src/main/java/engineering/everest/axon/cryptoshredding/encryption/DefaultAesDecrypter.java
 */
@Component
class DefaultAesDecrypter(
    private val secureRandom: SecureRandom = SecureRandom(),
) : Decrypter {
    override fun decrypt(
        secretKey: SecretKey,
        initializationVectorAndCipherText: ByteArray,
    ): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(
                AUTHENTICATION_TAG_SIZE_BITS,
                initializationVectorAndCipherText,
                0,
                INITIALIZATION_VECTOR_LENGTH_BYTES,
            ),
            secureRandom,
        )
        return String(
            cipher.doFinal(
                initializationVectorAndCipherText,
                INITIALIZATION_VECTOR_LENGTH_BYTES,
                initializationVectorAndCipherText.size - INITIALIZATION_VECTOR_LENGTH_BYTES,
            ),
        )
    }

    companion object {
        private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        private const val INITIALIZATION_VECTOR_LENGTH_BYTES = 12
        private const val AUTHENTICATION_TAG_SIZE_BITS = 128
    }
}
