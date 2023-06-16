package ru.digdes.school.service;

import org.springframework.data.domain.Page;
import ru.digdes.school.dto.CanDoPaging;
import ru.digdes.school.dto.CanDoFiltering;
import ru.digdes.school.dto.Stateable;

/**
 * Интерфейс сервисного слоя, содержит общие операции для всех сущностей
 * (кроме составной сущности "Команда проекта")
 * @param <T> dto соответствующей сущности, принимаемая из контроллера и возвращаемая в контроллер
 */
public interface BasicService<T> {
    T create(T createFrom);

    T update(T updateFrom);

    Page<T> search(CanDoPaging pagingObject, CanDoFiltering filteringObject);

    String changeState(Stateable changeStateObject);
}
