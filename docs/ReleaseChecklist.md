# Al HissN - Release Checklist & Compliance

## Pre-Release Validation
- [ ] Run `./gradlew lint` and ensure 0 critical errors.
- [ ] Run `./gradlew test` and ensure all unit/instrumentation tests pass.
- [ ] Build AAB using `./gradlew bundleRelease`.
- [ ] Inspect AAB using `apkanalyzer` to confirm ProGuard shrinking worked and no extra resources are packed.
- [ ] Verify the keystore.jks is securely backed up and not tracked in Git.

## Play Store Compliance Checklist
- [ ] **Accessibility Service Rationale:** Ensure the in-app disclosure explicitly states *why* accessibility is needed (to detect malicious overlays/UI elements) *before* asking for permission.
- [ ] **VPN Service Disclosure:** Ensure the prompt explains that the VPN is local-only for DNS blocking and does not transmit traffic off-device.
- [ ] **Biometric Lock:** Verify that biometric locks appropriately fallback to device PIN if biometrics are unavailable.
- [ ] **Data Deletion Flow:** Ensure the "Wipe Local Data" function permanently removes all Room DB entries, DataStore preferences, and caches.
- [ ] **Overlay Consent:** Explain system alert window usage for drawing warnings over malicious apps.

## Deployment Strategy
1. Upload to Play Console Internal Testing Track.
2. Ensure automated pre-launch reports raise no accessibility or crash flags.
3. Promote to Closed Beta.
4. Stage rollout to Production at 10%, monitor Crashlytics/Play Console ANR tracking.
5. If issues arise > 1% crash rate, halt rollout and patch.
6. Post-launch monitoring: Route user feedback and check Play Console ANR metrics daily.