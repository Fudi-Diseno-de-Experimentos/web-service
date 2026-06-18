package synera.centralis.api.company.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.company.domain.model.queries.GetAllSpacesQuery;
import synera.centralis.api.company.domain.model.queries.GetSpaceByIdQuery;
import synera.centralis.api.company.domain.services.SpaceQueryService;
import synera.centralis.api.company.infrastructure.persistence.jpa.repositories.SpaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;

    public SpaceQueryServiceImpl(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Space> handle(GetAllSpacesQuery query) {
        return spaceRepository.findAllByCompanyId(query.companyId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Space> handle(GetSpaceByIdQuery query) {
        return spaceRepository.findByIdAndCompanyId(query.spaceId(), query.companyId());
    }
}
