package de.nebulit.backoffice.cryptoshredding.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.nebulit.backoffice.cryptoshredding.CryptoShreddingSerializer
import de.nebulit.backoffice.cryptoshredding.KeyManager
import de.nebulit.backoffice.cryptoshredding.encryption.Decrypter
import de.nebulit.backoffice.cryptoshredding.encryption.Encrypter
import org.axonframework.serialization.ChainingConverter
import org.axonframework.serialization.RevisionResolver
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class CryptoShreddingConfig : BeanClassLoaderAware {
    private lateinit var classLoader: ClassLoader

    @Autowired
    private lateinit var encrypter: Encrypter

    @Autowired
    private lateinit var decrypter: Decrypter

    @Primary
    @Bean
    fun cryptoShreddingSerializer(
        applicationContext: ApplicationContext,
        revisionResolver: RevisionResolver,
        keyManager: KeyManager,
    ): CryptoShreddingSerializer {
        val objectMapperBeans =
            BeanFactoryUtils.beansOfTypeIncludingAncestors<ObjectMapper>(
                applicationContext,
                ObjectMapper::class.java,
            )
        val objectMapper =
            if (objectMapperBeans.containsKey("defaultAxonObjectMapper")) {
                objectMapperBeans["defaultAxonObjectMapper"]
            } else {
                jacksonObjectMapper()
            }
        val converter = ChainingConverter(classLoader)
        val builder =
            JacksonSerializer
                .builder()
                .revisionResolver(revisionResolver)
                .converter(converter)
                .objectMapper(objectMapper)
        return CryptoShreddingSerializer(keyManager, encrypter, decrypter, builder)
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }
}
