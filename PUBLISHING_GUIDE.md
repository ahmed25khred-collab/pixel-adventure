# 📱 Google Play Publishing Guide

## Step-by-Step Guide to Publish Pixel Adventure

### Phase 1: Preparation

#### 1. Create Google Play Developer Account
- Visit: https://play.google.com/console
- Pay $25 one-time developer fee
- Complete your merchant and profile information

#### 2. Install Required Tools
```bash
# Install Android Studio (includes SDK)
# Download: https://developer.android.com/studio

# Install keytool (comes with JDK)
# Verify: keytool -version
```

#### 3. Generate Signing Key
```bash
keytool -genkey -v -keystore pixel-adventure.jks -keyalg RSA -keysize 2048 -validity 10000 -alias pixel-key

# You'll be prompted for:
# - Keystore password: Choose strong password (e.g., "PixelAdv2024!")
# - Key password: Same as keystore or different
# - Name: Your name or studio name
# - Organization: Your company
# - City/State/Country: Your location
```

**⚠️ IMPORTANT**: Save your keystore file and password securely. You'll need it for future updates!

### Phase 2: Build Signed Release

#### 1. Update build.gradle
The build.gradle already includes signing config. Update keystore path:
```gradle
signingConfigs {
    release {
        storeFile file("pixel-adventure.jks")  // Update path
        storePassword "YOUR_PASSWORD"           // Your keystore password
        keyAlias "pixel-key"
        keyPassword "YOUR_PASSWORD"             // Your key password
    }
}
```

#### 2. Build Android App Bundle (AAB)
```bash
# From project root directory
./gradlew bundleRelease

# Output location: app/release/app-release.aab
# File size: ~15-20 MB
```

#### 3. Verify Build Success
```bash
# Check if AAB was created
ls -lh app/release/app-release.aab
```

### Phase 3: Google Play Console Setup

#### 1. Create New App
- Open Google Play Console: https://play.google.com/console
- Click "Create app"
- Enter app name: "Pixel Adventure"
- Select category: "Games > Casual"
- Select type: "Game"

#### 2. Fill in App Details
**Store Listing Section:**
- **Short description** (80 chars max): 
  "Jump, collect coins, and avoid enemies in this addictive pixel game!"

- **Full description** (4000 chars max):
  "Pixel Adventure is a fun and engaging casual game where you control a pixel character. 
  
  Features:
  - Simple tap-to-jump controls
  - Collect golden coins for points
  - Avoid enemies and obstacles
  - Track your high score
  - Smooth animations and colorful graphics
  - Perfect for quick gaming sessions
  
  Can you reach the highest score?"

- **Screenshots** (2-8 required):
  - Generate screenshots from device
  - Show gameplay, menu, leaderboard
  - Recommended size: 1080x1920px

- **Feature graphic** (1024x500px required):
  - Create using photo editor
  - Show game title and key features

- **App icon** (512x512px required):
  - Design colorful pixel adventure icon
  - Upload as PNG

#### 3. Content Rating
- Complete questionnaire
- Usually rated "Everyone" or "Everyone 10+"
- Go to: Content rating → Questionnaire

#### 4. App Signing
- Google Play will handle app signing for production
- Upload unsigned AAB (we created signed for testing)

### Phase 4: Upload Release

#### 1. Create Release
- Navigate to: Release → Production
- Click "Create new release"
- Upload app-release.aab file
- Review app integrity check

#### 2. Release Notes
```
Version 1.0.0 - Launch

🎮 Initial Release of Pixel Adventure!

Features:
✨ Beautiful pixel art graphics
🎯 Addictive gameplay mechanics
🏆 High score tracking
💾 Game progress saved locally
📱 Optimized for all devices

Jump, collect coins, and avoid enemies!
Compete for the highest score.

Enjoy! 🚀
```

#### 3. Review and Submit
- Check all required fields are complete
- Review pricing (select "Free")
- Review privacy policy (add if needed)
- Click "Review release"
- Click "Submit"

### Phase 5: Review & Launch

#### Waiting for Review
- Google Play will review your app (usually 2-4 hours, sometimes up to 7 days)
- You'll receive email notification
- App may be rejected if it violates policies

#### Common Rejection Reasons & Fixes:
| Issue | Solution |
|-------|----------|
| Misleading app name/icon | Ensure name matches gameplay |
| Poor graphics quality | Add proper drawable resources |
| Crashes on startup | Test on multiple devices |
| Missing privacy policy | Add privacy policy (required) |
| Too similar to copyrighted game | Your name "Pixel Adventure" is safe |

#### After Approval
- App becomes visible on Google Play
- Setup takes ~2-4 hours for full rollout
- Share your app: https://play.google.com/store/apps/details?id=com.pixeladventure.game

### Phase 6: Post-Launch

#### Monitor Performance
- Google Play Console → Acquisition → Overview
- Track: installs, uninstalls, ratings, reviews
- Monitor crashes: Crashes & ANRs section

#### Update Your App
```bash
# For updates:
# 1. Increment versionCode in build.gradle
versionCode 2  // Was 1

# 2. Rebuild
./gradlew bundleRelease

# 3. Upload new AAB to production or test release
```

#### Respond to Reviews
- Positive: Thank users for feedback
- Negative: Address concerns, ask for rerating
- Show you care about user experience

### File Checklist for Publishing

- [ ] Signed APK/AAB file
- [ ] App icon (512x512)
- [ ] Feature graphic (1024x500)
- [ ] Minimum 2 screenshots
- [ ] Short description (80 chars)
- [ ] Full description (up to 4000 chars)
- [ ] Privacy policy URL (if collecting data)
- [ ] Release notes
- [ ] Content rating questionnaire

### Important URLs

- Google Play Console: https://play.google.com/console
- Android Developer Docs: https://developer.android.com
- Google Play Policies: https://play.google.com/about/developer-content-policy/

### Troubleshooting

**"App crashes on launch"**
- Check AndroidManifest.xml for correct activities
- Test on physical device, not just emulator
- Check logcat for errors

**"Build failed - signing issues"**
- Verify keystore file exists
- Check passwords in build.gradle
- Run: `keytool -list -v -keystore pixel-adventure.jks`

**"App bundle too large"**
- Remove unused resources
- Compress images
- Google Play handles compression

**"Play Store not showing my app"**
- Check it's marked as released (not draft)
- Wait 2-4 hours for full propagation
- Verify app meets policy requirements

---

## 🎉 Success!

Once approved, your game is live on Google Play! 🚀

Share with friends:
```
Download Pixel Adventure on Google Play: https://play.google.com/store/apps/details?id=com.pixeladventure.game
```

---

**Questions? Check Google Play Developer Help:**
https://support.google.com/googleplay/android-developer/
