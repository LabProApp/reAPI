package com.api.version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    /**
     * Returns the latest active version for a given platform, ordered by
     * versionCode descending so that the highest published build is first.
     */
    Optional<AppVersion> findFirstByPlatformIgnoreCaseAndIsActiveTrueOrderByVersionCodeDesc(String platform);

    /** Used by publishVersion to deactivate all prior rows for a platform. */
    List<AppVersion> findAllByPlatformIgnoreCase(String platform);
}
