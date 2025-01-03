package de.eventsourcingbook.cart.support.converter

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val serverSentEventMessageConverter: ServerSentEventMessageConverter,
) : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // Register the custom ServerSentEventMessageConverter
        converters.add(serverSentEventMessageConverter)
    }
}
