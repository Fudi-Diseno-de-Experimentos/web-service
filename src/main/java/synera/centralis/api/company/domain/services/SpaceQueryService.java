package synera.centralis.api.company.domain.services;

import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.company.domain.model.queries.GetAllSpacesQuery;
import synera.centralis.api.company.domain.model.queries.GetSpaceByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SpaceQueryService {
    List<Space> handle(GetAllSpacesQuery query);
    Optional<Space> handle(GetSpaceByIdQuery query);
}
