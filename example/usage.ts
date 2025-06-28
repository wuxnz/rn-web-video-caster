import RNWebVideoCaster, {
  RNWebVideoCasterInterface,
  WebVideoCasterOptions,
  SubtitleTrack,
} from "rn-web-video-caster";

// Example usage with full type support and new features
class VideoPlayerService {
  private caster: RNWebVideoCasterInterface;

  constructor() {
    this.caster = RNWebVideoCaster;
  }

  /**
   * Cast a video with basic options
   */
  castVideo(videoUrl: string): void {
    // Simple usage for backward compatibility
    RNWebVideoCaster.play(videoUrl);
  }

  /**
   * Cast a video with advanced features
   */
  castVideoAdvanced(videoUrl: string): void {
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
        enabled: false,
      },
    ];

    const options: WebVideoCasterOptions = {
      videoURL: videoUrl,
      title: "My Awesome Movie",
      posterURL: "https://example.com/poster.jpg",
      headers: {
        Authorization: "Bearer your-token-here",
        Referer: "https://your-site.com",
        "User-Agent": "MyApp/1.0", // Include User-Agent in headers for better compatibility
      },
      subtitles: subtitles,
      hideVideoAddress: true,
      position: 30000, // Start at 30 seconds
      size: 1024000000, // 1GB file size
      filename: "movie.mp4",
      suppressErrorMessage: false,
      mimeType: "video/mp4",
    };

    // TypeScript will provide full intellisense and type checking here
    RNWebVideoCaster.playVideo(options);
  }

  /**
   * Cast a live stream with custom headers
   */
  castLiveStream(streamUrl: string, authToken: string): void {
    const options: WebVideoCasterOptions = {
      videoURL: streamUrl,
      title: "Live Sports Event",
      headers: {
        Authorization: `Bearer ${authToken}`,
        "User-Agent": "MyStreamingApp/2.0",
      },
      hideVideoAddress: true,
      mimeType: "application/x-mpegURL", // HLS stream
    };

    RNWebVideoCaster.playVideo(options);
  }

  /**
   * Cast a video with multiple subtitle tracks
   */
  castVideoWithSubtitles(videoUrl: string): void {
    const options: WebVideoCasterOptions = {
      videoURL: videoUrl,
      title: "International Film",
      subtitles: [
        {
          url: "https://example.com/subs/en.vtt",
          language: "en",
          name: "English",
          enabled: true,
        },
        {
          url: "https://example.com/subs/fr.vtt",
          language: "fr",
          name: "Français",
        },
        {
          url: "https://example.com/subs/de.vtt",
          language: "de",
          name: "Deutsch",
        },
      ],
    };

    RNWebVideoCaster.playVideo(options);
  }

  /**
   * Example method to cast a video with validation and error handling
   */
  castVideoWithValidation(options: WebVideoCasterOptions): boolean {
    try {
      if (!options.videoURL || typeof options.videoURL !== "string") {
        console.error("Invalid video URL provided");
        return false;
      }

      // Validate subtitle tracks if provided
      if (options.subtitles) {
        for (const subtitle of options.subtitles) {
          if (!subtitle.url) {
            console.error("Subtitle track missing URL");
            return false;
          }
        }
      }

      // Cast the video with all options
      RNWebVideoCaster.playVideo(options);
      return true;
    } catch (error) {
      console.error("Failed to cast video:", error);
      return false;
    }
  }
}

// Usage examples
const videoService = new VideoPlayerService();

// Basic video casting
videoService.castVideo("https://example.com/video.mp4");

// Advanced video casting with all features
videoService.castVideoAdvanced("https://example.com/movie.mp4");

// Cast live stream with authentication
videoService.castLiveStream(
  "https://stream.example.com/live.m3u8",
  "your-auth-token"
);

// Cast with multiple subtitles
videoService.castVideoWithSubtitles(
  "https://example.com/international-film.mp4"
);

// Cast with full options and validation
const advancedOptions: WebVideoCasterOptions = {
  videoURL: "https://example.com/premium-content.mp4",
  title: "Premium Movie Title",
  posterURL: "https://example.com/poster-premium.jpg",
  headers: {
    Authorization: "Bearer premium-token",
    "X-Custom-Header": "custom-value",
  },
  subtitles: [
    {
      url: "https://example.com/subs/premium-en.vtt",
      language: "en",
      name: "English (Premium)",
      enabled: true,
    },
  ],
  hideVideoAddress: true,
  userAgent: "PremiumVideoApp/1.0",
  suppressErrorMessage: false,
};

const success = videoService.castVideoWithValidation(advancedOptions);
if (success) {
  console.log("Premium video casting initiated successfully");
}

// Debug: Check if Web Video Caster app is installed
RNWebVideoCaster.isAppInstalled((isInstalled) => {
  if (isInstalled) {
    console.log("✅ Web Video Caster app is installed and ready to use");
  } else {
    console.log(
      "❌ Web Video Caster app is not installed - will redirect to Play Store"
    );
  }
});
