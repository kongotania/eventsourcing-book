package de.eventsourcingbook.cart.support.converter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.stereotype.Component

@Component
class ServerSentEventMessageConverter(
    val objectMapper: ObjectMapper,
) : HttpMessageConverter<ServerSentEvent<*>?> {
    override fun write(
        event: ServerSentEvent<*>,
        contentType: MediaType?,
        outputMessage: HttpOutputMessage,
    ) {
        val json: String = objectMapper.writeValueAsString(event.data())
        outputMessage.body.write(("data: $json\n\n").toByteArray())
    }

    override fun canRead(
        clazz: Class<*>,
        mediaType: MediaType?,
    ): Boolean = ServerSentEvent::class.java.isAssignableFrom(clazz)

    override fun canWrite(
        clazz: Class<*>,
        mediaType: MediaType?,
    ): Boolean = ServerSentEvent::class.java.isAssignableFrom(clazz)

    override fun getSupportedMediaTypes(): MutableList<MediaType> = mutableListOf(MediaType.TEXT_EVENT_STREAM)

    override fun read(
        clazz: Class<out ServerSentEvent<*>?>,
        inputMessage: HttpInputMessage,
    ): ServerSentEvent<*> = throw IllegalStateException("not supported")
}
