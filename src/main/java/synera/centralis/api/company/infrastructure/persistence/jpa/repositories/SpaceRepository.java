package synera.centralis.api.company.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synera.centralis.api.company.domain.model.aggregates.Space;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {

    List<Space> findAllByCompanyId(CompanyId companyId);

    Optional<Space> findByIdAndCompanyId(UUID id, CompanyId companyId);

    boolean existsByIdAndCompanyId(UUID id, CompanyId companyId);

    boolean existsByNameAndCompanyId(String name, CompanyId companyId);
}
