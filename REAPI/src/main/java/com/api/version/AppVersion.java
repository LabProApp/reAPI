package com.api.version;

import com.api.commons.BaseEntity;
import jakarta.persistence.*;

/**
 * One active row per platform (ANDROID / IOS) tracks the latest published
 * version and the minimum version below which a forced update is required.
 *
 * An admin creates a new row whenever a release goes live; older rows are
 * deactivated by {@link AppVersionService#publishVersion}.
 */
@Entity
@Table(name = "app_version", indexes = {
    @Index(name = "idx_app_version_platform_active", columnList = "platform, is_active")
})
public class AppVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** "ANDROID" or "IOS" — case-insensitive throughout the service layer. */
    @Column(nullable = false, length = 10)
    private String platform;

    /** Monotonically-increasing integer from the build system (buildNumber). */
    @Column(nullable = false)
    private Integer versionCode;

    /** Human-readable semver string, e.g. "1.5.0". Shown in the update dialog. */
    @Column(nullable = false, length = 20)
    private String versionName;

    /**
     * Any client with a versionCode strictly below this value receives a
     * forced (non-dismissible) update prompt.
     */
    @Column(nullable = false)
    private Integer minSupportedVersionCode;

    /** Deep-link to the Play Store or App Store listing. */
    @Column(nullable = false, length = 500)
    private String storeUrl;

    /** Short changelog shown in the update dialog. Optional. */
    @Column(length = 500)
    private String releaseNotes;

    /**
     * Only the active row is considered by the version-check endpoint.
     * {@link AppVersionService#publishVersion} sets this to false on all
     * earlier rows for the same platform before saving the new one.
     */
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive = true;

    // ── Constructors ──────────────────────────────────────────────────────────

    public AppVersion() {}

    public AppVersion(String platform, Integer versionCode, String versionName,
                      Integer minSupportedVersionCode, String storeUrl,
                      String releaseNotes) {
        this.platform = platform;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.minSupportedVersionCode = minSupportedVersionCode;
        this.storeUrl = storeUrl;
        this.releaseNotes = releaseNotes;
        this.isActive = true;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Integer getVersionCode() { return versionCode; }
    public void setVersionCode(Integer versionCode) { this.versionCode = versionCode; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public Integer getMinSupportedVersionCode() { return minSupportedVersionCode; }
    public void setMinSupportedVersionCode(Integer minSupportedVersionCode) {
        this.minSupportedVersionCode = minSupportedVersionCode;
    }

    public String getStoreUrl() { return storeUrl; }
    public void setStoreUrl(String storeUrl) { this.storeUrl = storeUrl; }

    public String getReleaseNotes() { return releaseNotes; }
    public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
