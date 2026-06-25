package com.api.version;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/version")
@Tag(name = "App Version APIs", description = "Version management and update-check for mobile clients")
public class AppVersionController {

    private static final Logger log = LoggerFactory.getLogger(AppVersionController.class);

    private final AppVersionService service;

    public AppVersionController(AppVersionService service) {
        this.service = service;
    }

    /**
     * PUBLIC — no auth required.
     *
     * Called by the mobile app on every launch (from SplashScreen) to
     * determine whether an update is available and whether it is mandatory.
     *
     * @param platform    "ANDROID" or "IOS" (case-insensitive)
     * @param versionCode The buildNumber from the installed APK / IPA
     */
    @Operation(summary = "Check if an app update is available")
    @GetMapping("/check")
    public ResponseEntity<VersionCheckResponse> check(
            @RequestParam String platform,
            @RequestParam int versionCode) {
        log.info("GET /api/version/check - platform={} versionCode={}", platform, versionCode);
        return ResponseEntity.ok(service.checkVersion(platform, versionCode));
    }

    /**
     * ADMIN — publish a new release.
     *
     * Body fields:
     *   platform               "ANDROID" or "IOS"
     *   versionCode            int — monotonically increasing build number
     *   versionName            String — e.g. "1.5.0"
     *   minSupportedVersionCode int — any client below this is force-updated
     *   storeUrl               String — Play Store / App Store deep link
     *   releaseNotes           String — shown in the update dialog (optional)
     */
    @Operation(summary = "ADMIN: publish a new app version")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppVersion> publish(@RequestBody AppVersion version) {
        log.info("POST /api/version - platform={} versionCode={}",
                version.getPlatform(), version.getVersionCode());
        return ResponseEntity.ok(service.publishVersion(version));
    }

    /** ADMIN — list all version records across platforms. */
    @Operation(summary = "ADMIN: list all version records")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppVersion>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    /** ADMIN — activate or deactivate a specific version record. */
    @Operation(summary = "ADMIN: toggle active flag on a version record")
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppVersion> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("PATCH /api/version/{}/active - active={}", id, active);
        return ResponseEntity.ok(service.setActive(id, active));
    }
}
