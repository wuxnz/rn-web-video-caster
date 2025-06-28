# 📺 rn-web-video-caster: Enhanced React Native Web Video Caster Integration

An **enhanced React Native module** for seamless integration with the **Web Video Caster app**. Features **full TypeScript support**, **Kotlin implementation**, and **advanced casting capabilities** including headers, subtitles, and metadata support.

This module is an **updated and enhanced version** of [react-native-web-video-caster](https://github.com/edwardxuluc/react-native-web-video-caster) with modern architecture and comprehensive new features.

---

## 🔥 Features

- **🎯 Full TypeScript Support**: Complete type definitions and IntelliSense for better development experience
- **🚀 Modern Kotlin Implementation**: Converted from Java to Kotlin for improved performance and maintainability
- **📱 Simple Integration**: Easy-to-use API for casting videos to Web Video Caster app
- **⚡ Auto-install Fallback**: Automatically redirects to Play Store if Web Video Caster app is not installed
- **🎬 Advanced Casting Features**: Support for custom headers, subtitles, poster images, and metadata
- **🔒 Privacy & Security**: Options to hide video URLs and suppress error messages
- **🌍 Multi-language Subtitles**: Support for multiple subtitle tracks with language selection
- **📺 Professional Streaming**: Custom User-Agent, file size hints, and MIME type support
- **🎵 Resume Playback**: Start videos at specific positions with millisecond precision

---

## ⚠️ Enhanced Version Notice

This module is based on [edwardxuluc/react-native-web-video-caster](https://github.com/edwardxuluc/react-native-web-video-caster) but includes significant enhancements:

- ✅ **TypeScript rewrite** with complete type safety
- ✅ **Kotlin conversion** from Java for better Android performance
- ✅ **Advanced Web Video Caster features** (headers, subtitles, metadata)
- ✅ **Modern React Native compatibility**
- ✅ **Comprehensive documentation** and examples

---

## 📦 Installation

```bash
npm install rn-web-video-caster
```

### Android Setup

Add the package to your React Native project:

```bash
# React Native 0.60+
cd ios && pod install

# React Native < 0.60
react-native link rn-web-video-caster
```

---

## 🚀 Quick Start

### Basic Usage

```typescript
import RNWebVideoCaster from "rn-web-video-caster";

// Simple video casting
RNWebVideoCaster.play("https://example.com/video.mp4");
```

### Advanced Usage with All Features

```typescript
import RNWebVideoCaster, {
  WebVideoCasterOptions,
  SubtitleTrack,
} from "rn-web-video-caster";

const subtitles: SubtitleTrack[] = [
  {
    url: "https://example.com/subtitles/english.vtt",
    language: "en",
    name: "English",
    mimeType: "text/vtt",
    enabled: true,
  },
  {
    url: "https://example.com/subtitles/spanish.srt",
    language: "es",
    name: "Español",
    mimeType: "application/x-subrip",
  },
];

const options: WebVideoCasterOptions = {
  videoURL: "https://example.com/movie.mp4",
  title: "My Awesome Movie",
  posterURL: "https://example.com/poster.jpg",
  headers: {
    Authorization: "Bearer your-token-here",
    Referer: "https://your-site.com",
    "X-Custom-Header": "custom-value",
  },
  subtitles: subtitles,
  hideVideoAddress: true,
  position: 30000, // Start at 30 seconds
  userAgent: "MyApp/1.0",
  size: 1024000000, // File size in bytes
  filename: "movie.mp4",
  suppressErrorMessage: false,
  mimeType: "video/mp4",
};

RNWebVideoCaster.playVideo(options);
```

---

## 📋 API Reference

### Methods

#### `play(videoURL: string): void`

Basic method for casting a video URL to Web Video Caster.

**Parameters:**

- `videoURL` (string): The URL of the video to cast

#### `playVideo(options: WebVideoCasterOptions): void`

Advanced method with full feature support.

**Parameters:**

- `options` (WebVideoCasterOptions): Configuration object with all casting options

### Types

#### `WebVideoCasterOptions`

| Property               | Type                      | Required | Description                                        |
| ---------------------- | ------------------------- | -------- | -------------------------------------------------- |
| `videoURL`             | `string`                  | ✅       | The URL of the video to be casted                  |
| `title`                | `string`                  | ❌       | Custom title for the video                         |
| `posterURL`            | `string`                  | ❌       | URL of the poster/thumbnail image                  |
| `headers`              | `{[key: string]: string}` | ❌       | Custom HTTP headers                                |
| `subtitles`            | `SubtitleTrack[]`         | ❌       | Array of subtitle tracks                           |
| `hideVideoAddress`     | `boolean`                 | ❌       | Hide video URL from display (default: false)       |
| `position`             | `number`                  | ❌       | Resume position in milliseconds                    |
| `userAgent`            | `string`                  | ❌       | Custom User-Agent string                           |
| `size`                 | `number`                  | ❌       | File size in bytes (helps with subtitle detection) |
| `filename`             | `string`                  | ❌       | Filename (helps with subtitle detection)           |
| `suppressErrorMessage` | `boolean`                 | ❌       | Suppress error messages (default: false)           |
| `mimeType`             | `string`                  | ❌       | Custom MIME type for the video                     |

#### `SubtitleTrack`

| Property   | Type      | Required | Description                                          |
| ---------- | --------- | -------- | ---------------------------------------------------- |
| `url`      | `string`  | ✅       | URL of the subtitle file                             |
| `language` | `string`  | ❌       | Language code (e.g., 'en', 'es', 'fr')               |
| `name`     | `string`  | ❌       | Display name for the subtitle track                  |
| `mimeType` | `string`  | ❌       | MIME type ('text/vtt', 'application/x-subrip', etc.) |
| `enabled`  | `boolean` | ❌       | Whether this track should be enabled by default      |

---

## 🎯 Use Cases

### 🔐 Authenticated Video Streaming

```typescript
const options: WebVideoCasterOptions = {
  videoURL: "https://secure.example.com/video.mp4",
  title: "Protected Content",
  headers: {
    Authorization: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "X-API-Key": "your-api-key",
    Referer: "https://your-app.com",
    "User-Agent": "MyApp/1.0",
    Cookie: "session=abc123; auth=xyz789",
  },
};

RNWebVideoCaster.playVideo(options);
```

### 🌍 Subtitle Support

```typescript
// Note: Web Video Caster currently supports one subtitle track per video
const subtitles: SubtitleTrack[] = [
  {
    url: "https://example.com/subs/en.vtt",
    language: "en",
    name: "English",
    mimeType: "text/vtt",
    enabled: true,
  },
];

const options: WebVideoCasterOptions = {
  videoURL: "https://example.com/movie.mp4",
  title: "Movie with Subtitles",
  subtitles: subtitles,
};

RNWebVideoCaster.playVideo(options);
```

### 📺 Professional Video Presentations

```typescript
const options: WebVideoCasterOptions = {
  videoURL: "https://example.com/movie.mp4",
  title: "The Amazing Movie (2024)",
  posterURL: "https://example.com/posters/amazing-movie.jpg",
};
```

### 🔒 Privacy-Focused Casting

```typescript
const options: WebVideoCasterOptions = {
  videoURL: "https://private-server.com/secret-video.mp4",
  hideVideoAddress: true,
  suppressErrorMessage: true,
};
```

### ⏰ Resume Playback

```typescript
const options: WebVideoCasterOptions = {
  videoURL: "https://example.com/long-movie.mp4",
  position: 1800000, // Start at 30 minutes (30 * 60 * 1000 ms)
};
```

---

## 📺 Live Streaming Support

Perfect for live streaming applications:

```typescript
const streamOptions: WebVideoCasterOptions = {
  videoURL: "https://stream.example.com/live.m3u8",
  title: "Live Sports Event",
  headers: {
    Authorization: "Bearer live-stream-token",
    "User-Agent": "MyStreamingApp/2.0",
  },
  hideVideoAddress: true,
  mimeType: "application/x-mpegURL", // HLS stream
};

RNWebVideoCaster.playVideo(streamOptions);
```

---

## 💡 Requirements

- **React Native**: 0.41.2 or higher
- **Android**: API level 16 (Android 4.1) or higher
- **Web Video Caster App**: Available on Google Play Store
- **TypeScript**: 4.9.0 or higher (for TypeScript projects)

---

## 🛠️ Troubleshooting

### App Opens Play Store Instead of Web Video Caster

If the module opens the Play Store instead of the Web Video Caster app, try:

1. **Check if the app is installed**:

   ```typescript
   RNWebVideoCaster.isAppInstalled((isInstalled) => {
     console.log("Web Video Caster installed:", isInstalled);
   });
   ```

2. **Test with a simple video first**:

   ```typescript
   RNWebVideoCaster.play(
     "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
   );
   ```

3. **Verify your video URL is accessible** - try opening it in a browser first

### Headers Not Working

Ensure headers are passed correctly as an object (the module will convert them to JSON internally):

```typescript
// ✅ Correct - Pass headers as object
const options = {
  videoURL: "your-video-url",
  headers: {
    Authorization: "Bearer token",
    Referer: "https://yoursite.com",
    "User-Agent": "MyApp/1.0", // Include User-Agent here, not as separate property
  },
};

// ❌ Incorrect - Don't stringify manually
const options = {
  videoURL: "your-video-url",
  headers: JSON.stringify({ Authorization: "Bearer token" }), // Don't stringify!
};

// ❌ Incorrect - Don't use separate userAgent property
const options = {
  videoURL: "your-video-url",
  userAgent: "MyApp/1.0", // Use headers['User-Agent'] instead
};
```

---

## 🏗️ Development

```bash
# Clone the repository
git clone https://github.com/wuxnz/rn-web-video-caster.git

# Install dependencies
npm install

# Build TypeScript
npm run build

# Run in development
npm run prepare
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Based on [react-native-web-video-caster](https://github.com/edwardxuluc/react-native-web-video-caster) by Edward Xuluc
- Enhanced with modern TypeScript and Kotlin implementation
- Inspired by the Web Video Caster app's integration capabilities

---

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/wuxnz/rn-web-video-caster/issues) page
2. Create a new issue with detailed information
3. Contribute to the project with improvements

**Made with ❤️ by [wuxnz](https://github.com/wuxnz)**
