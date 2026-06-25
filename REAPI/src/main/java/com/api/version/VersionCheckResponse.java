package com.api.version;

/**
 * Returned by {@code GET /api/version/check}.
 *
 * The client compares its own versionCode against the server's decision:
 *   updateAvailable — a newer build is in the store.
 *   forceUpdate     — the running build is below the minimum supported version;
 *                     the client must update before continuing.
 */
public class VersionCheckResponse {

    private final boolean updateAvailable;
    private final boolean forceUpdate;
    private final String latestVersionName;
    private final String storeUrl;
    private final String releaseNotes;

    public VersionCheckResponse(boolean updateAvailable,
                                boolean forceUpdate,
                                String latestVersionName,
                                String storeUrl,
                                String releaseNotes) {
        this.updateAvailable   = updateAvailable;
        this.forceUpdate       = forceUpdate;
        this.latestVersionName = latestVersionName;
        this.storeUrl          = storeUrl;
        this.releaseNotes      = releaseNotes;
    }

    /** Convenience factory used when no version record is configured yet. */
    public static VersionCheckResponse noUpdate() {
        return new VersionCheckResponse(false, false, null, null, null);
    }

    public boolean isUpdateAvailable()   { return updateAvailable; }
    public boolean isForceUpdate()       { return forceUpdate; }
    public String  getLatestVersionName(){ return latestVersionName; }
    public String  getStoreUrl()         { return storeUrl; }
    public String  getReleaseNotes()     { return releaseNotes; }
}
