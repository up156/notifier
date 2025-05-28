package com.notifier.notifier.mapper;

import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);

    Event toEntity(EventCreateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEvent(EventUpdateRequest request, @MappingTarget Event event);
}
