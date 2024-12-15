package de.nebulit.backoffice.cryptoshredding.persistence

import de.nebulit.backoffice.common.NoArg
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface CryptoKeyRepository : JpaRepository<PersistedSecretKey, String>

@Table(name = "crypto_keys")
@Entity
@NoArg
class PersistedSecretKey {
    @Id
    lateinit var id: String

    @Lob
    lateinit var key: ByteArray
}
