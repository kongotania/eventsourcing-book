package de.nebulit.backoffice.cryptoshredding

import com.fasterxml.jackson.core.type.TypeReference
import de.nebulit.backoffice.cryptoshredding.encryption.Decrypter
import de.nebulit.backoffice.cryptoshredding.encryption.Encrypter
import de.nebulit.backoffice.cryptoshredding.persistence.CryptoKeyRepository
import de.nebulit.backoffice.cryptoshredding.persistence.PersistedSecretKey
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedObject
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Target(AnnotationTarget.FIELD)
annotation class EncryptedField

@Target(AnnotationTarget.FIELD)
annotation class EncryptionKeyIdentifier

@Component
class KeyManager(
    val repository: CryptoKeyRepository,
) {
    fun getKey(identifier: String): SecretKey? {
        val key = repository.findByIdOrNull(identifier)
        return key?.let { SecretKeySpec(key.key, "AES") }
    }

    fun getOrCreateKey(identifier: String): SecretKey {
        var key = repository.findByIdOrNull(identifier)

        if (key == null) {
            val genKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
            repository.save(
                PersistedSecretKey().apply {
                    this.key = genKey.encoded
                    this.id = identifier
                    this.deleted = false
                },
            )
            return genKey
        }
        return SecretKeySpec(key.key, "AES")
    }
}

class CryptoShreddingSerializer(
    private val keyManager: KeyManager,
    private val encrypter: Encrypter,
    private val decrypter: Decrypter,
    jacksonBuilder: Builder,
) : JacksonSerializer(jacksonBuilder) {
    override fun <S, T> deserialize(serializedObject: SerializedObject<S>): T {
        val objectType = classForType(serializedObject.type)
        val fieldsToDecrypt = objectType.declaredFields.filter { it.isAnnotationPresent(EncryptedField::class.java) }
        if (fieldsToDecrypt.isEmpty()) {
            return super.deserialize(serializedObject)
        }

        val jsonMap =
            objectMapper.readValue(
                serializedObject.data as ByteArray,
                object : TypeReference<MutableMap<String, Any>>() {},
            )
        val keyIdentifier =
            jsonMap[getEncryptionKeyFieldName(objectType)]?.toString()
                ?: throw IllegalStateException("Missing encryption key identifier in serialized data")

        val secretKey = keyManager.getKey(keyIdentifier)

        fieldsToDecrypt.forEach { field ->
            val encryptedValue = jsonMap[field.name]?.toString() ?: return@forEach
            val decryptedBytes =
                secretKey?.let {
                    decrypter.decrypt(it, Base64.getDecoder().decode(encryptedValue))
                } ?: DELETED_DEFAULT
            jsonMap[field.name] = decryptedBytes
        }
        return objectMapper.convertValue(jsonMap, objectType) as T
    }

    override fun <T : Any?> serialize(
        `object`: Any?,
        expectedRepresentation: Class<T>,
    ): SerializedObject<T> {
        if (`object` == null) {
            throw IllegalStateException("cannot serialize null object")
        }
        val fieldsToEncrypt =
            `object`::class.java.declaredFields.filter { it.isAnnotationPresent(EncryptedField::class.java) }
        if (fieldsToEncrypt.isEmpty()) {
            return super.serialize(`object`, expectedRepresentation)
        }

        val keyIdentifier = getKeyIdentifier(`object`)
        val secretKey = keyManager.getOrCreateKey(keyIdentifier)

        val jsonMap =
            objectMapper.convertValue(`object`, object : TypeReference<MutableMap<String, Any>>() {}).apply {
                fieldsToEncrypt.forEach { field ->
                    field.isAccessible = true
                    val fieldValue = field.get(`object`)?.toString()
                    if (fieldValue != null) {
                        val encryptedValue = encrypter.encrypt(secretKey, fieldValue)
                        this[field.name] = Base64.getEncoder().encodeToString(encryptedValue)
                    }
                }
            }

        val serializedData = objectMapper.writeValueAsBytes(jsonMap)
        return SimpleSerializedObject(
            serializedData as T,
            expectedRepresentation,
            super.typeForClass(`object`::class.java),
        )
    }

    override fun <T> canSerializeTo(expectedRepresentation: Class<T>): Boolean = super.canSerializeTo(expectedRepresentation)

    override fun typeForClass(type: Class<*>?): SerializedType = super.typeForClass(type)

    private fun getKeyIdentifier(instance: Any): String =
        instance::class.java.declaredFields
            .firstOrNull {
                it.isAnnotationPresent(EncryptionKeyIdentifier::class.java)
            }?.apply { isAccessible = true }
            ?.get(instance)
            ?.toString()
            ?: throw IllegalStateException("Missing encryption key identifier")

    private fun getEncryptionKeyFieldName(type: Class<*>): String =
        type.declaredFields
            .firstOrNull {
                it.isAnnotationPresent(EncryptionKeyIdentifier::class.java)
            }?.name ?: throw IllegalStateException("No field annotated with @EncryptionKeyIdentifier")

    companion object {
        const val DELETED_DEFAULT = "deleted"
    }
}
