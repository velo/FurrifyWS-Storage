package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlSourceRepository extends Repository<SourceSnapshot, Long> {
    SourceSnapshot save(SourceSnapshot sourceSnapshot);

    void deleteBySourceId(UUID sourceId);

    boolean existsByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    Optional<SourceSnapshot> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);
}

@Transactional(rollbackFor = {})
interface SqlSourceQueryRepositoryImpl extends SourceQueryRepository, Repository<SourceSnapshot, Long> {

    @Override
    Optional<SourceDetailsQueryDTO> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    @Override
    Page<SourceDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    @Query("select id from SourceSnapshot where sourceId = ?1")
    Long getIdBySourceId(UUID sourceId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SourceRepositoryImpl implements SourceRepository {

    private final SqlSourceRepository sqlSourceRepository;

    @Override
    public Source save(final Source post) {
        return Source.restore(sqlSourceRepository.save(post.getSnapshot()));
    }

    @Override
    public void deleteBySourceId(final UUID sourceId) {
        sqlSourceRepository.deleteBySourceId(sourceId);
    }

    @Override
    public Optional<Source> findByOwnerIdAndSourceId(final UUID ownerId, final UUID sourceId) {
        return sqlSourceRepository.findByOwnerIdAndSourceId(ownerId, sourceId).map(Source::restore);
    }

    @Override
    public boolean existsByOwnerIdAndSourceId(final UUID ownerId, final UUID sourceId) {
        return sqlSourceRepository.existsByOwnerIdAndSourceId(ownerId, sourceId);
    }


}