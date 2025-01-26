package de.nebulit.readmodel.internal

import de.nebulit.readmodel.ExpiredTodoData
import de.nebulit.readmodel.TodoItemsToExpireQuery
import de.nebulit.readmodel.TodosReadModel
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TodosReadModelQueryHandler(
    private val repository: TodosReadModelRepository,
) {
    @QueryHandler
    fun handleQuery(query: TodoItemsToExpireQuery): TodosReadModel? {
        var todos = repository.findByExpiredFalseAndExpirationDateBefore(LocalDateTime.now())
        return TodosReadModel(
            todos.map { ExpiredTodoData(it.aggregateId, it.todo) },
        )
    }
}
