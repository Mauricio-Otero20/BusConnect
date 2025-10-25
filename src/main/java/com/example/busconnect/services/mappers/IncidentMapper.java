package com.example.busconnect.services.mappers;
import com.example.busconnect.api.dto.IncidentDtos.*;
import com.example.busconnect.domine.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Incident toEntity(IncidentCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(IncidentUpdateRequest dto, @MappingTarget Incident entity);

    @Mapping(target = "entityType", expression = "java(entity.getEntityType().name())")
    @Mapping(target = "type", expression = "java(entity.getType().name())")
    @Mapping(target = "reportedBy", source = "reportedBy.id")
    @Mapping(target = "reportedByName", source = "reportedBy.username")
    IncidentResponse toResponse(Incident entity);
}
