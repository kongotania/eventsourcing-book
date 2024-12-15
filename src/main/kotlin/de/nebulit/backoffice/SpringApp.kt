package de.nebulit.backoffice

import de.nebulit.backoffice.cryptoshredding.CryptoShreddingSerializer
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.config.Configurer
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.PropagatingErrorHandler
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.modulith.Modulith
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class SecurityConfig {
    @Autowired private lateinit var userService: OAuth2UserService<OidcUserRequest, OidcUser>

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf: CsrfConfigurer<HttpSecurity> ->
                csrf.disable()
            } // Disable CSRF since we're stateless
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/public/**")
                    .permitAll() // Allow public endpoints
                    .anyRequest()
                    .authenticated()
            } // Protect all other endpoints
            .oauth2Login { oauth2: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauth2 // Enable OAuth2 login for Authorization Code Flow
                    .defaultSuccessUrl("/user", true)
                    .userInfoEndpoint { userInfo -> userInfo.oidcUserService(userService) }
            } // Redirect to /secure after successful login

        return http.build()
    }
}

@Configuration
class ValidatorConfig {
    @Bean fun localValidatorFactoryBean(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()
}

@Configuration
class ValidationConfig {
    @Bean
    fun beanValidationInterceptor(validatorFactory: LocalValidatorFactoryBean): BeanValidationInterceptor<*> =
        BeanValidationInterceptor<CommandMessage<*>>(validatorFactory)
}

@Configuration
class AxonConfig {
    @Autowired
    fun configurationEventHandling(config: EventProcessingConfigurer) {
        config.registerDefaultListenerInvocationErrorHandler { PropagatingErrorHandler.instance() }
    }

    @Autowired
    fun configurer(
        configurer: Configurer,
        cryptoShreddingSerializer: CryptoShreddingSerializer,
    ) {
        configurer.configureSerializer { _ -> cryptoShreddingSerializer }
    }

    @Bean
    fun commandGateway(
        commandBus: CommandBus?,
        dispatchInterceptors: List<MessageDispatchInterceptor<CommandMessage<*>>>,
        handlerInterceptor: List<MessageHandlerInterceptor<CommandMessage<*>>>,
    ): CommandGateway {
        handlerInterceptor.forEach { it -> commandBus?.registerHandlerInterceptor(it) }
        return DefaultCommandGateway
            .builder()
            .commandBus(commandBus!!)
            .dispatchInterceptors(*dispatchInterceptors.toTypedArray())
            .build()
    }
}

@Modulith(
    systemName = "System",
    sharedModules = ["de.nebulit.backoffice.common", "de.nebulit.backoffice.domain"],
    useFullyQualifiedModuleNames = true,
)
@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
@EntityScan(
    basePackages =
        [
            "de.nebulit.backoffice",
            "org.springframework.modulith.events.jpa",
            "org.axonframework.eventhandling.tokenstore",
            "org.axonframework.eventsourcing.eventstore.jpa",
            "org.axonframework.modelling.saga.repository.jpa",
            "org.axonframework.eventhandling.deadletter.jpa",
        ],
)
class SpringApp {
    companion object {
        fun main(args: Array<String>) {
            runApplication<SpringApp>(*args)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringApp>(*args)
}
