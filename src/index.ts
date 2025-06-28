import { NativeModules } from "react-native";

export interface WebVideoCasterOptions {
  /**
   * The URL of the video to be casted (required)
   */
  videoURL: string;

  /**
   * Custom headers to be sent with the video request
   * Key-value pairs for HTTP headers
   */
  headers?: { [key: string]: string };

  /**
   * Custom title for the video
   */
  title?: string;

  /**
   * URL of the poster/thumbnail image
   */
  posterURL?: string;

  /**
   * Array of subtitle tracks
   */
  subtitles?: SubtitleTrack[];

  /**
   * Hide the video URL from being displayed in the app
   * @default false
   */
  hideVideoAddress?: boolean;

  /**
   * Resume position in milliseconds
   */
  position?: number;

  /**
   * Custom User-Agent string
   * @deprecated Use headers['User-Agent'] instead for better compatibility
   */
  userAgent?: string;

  /**
   * Size of the media file in bytes (helps with subtitle detection)
   */
  size?: number;

  /**
   * Filename of the media (helps with subtitle detection)
   */
  filename?: string;

  /**
   * Suppress error messages if they occur
   * @default false
   */
  suppressErrorMessage?: boolean;

  /**
   * Custom MIME type for the video
   */
  mimeType?: string;
}

export interface SubtitleTrack {
  /**
   * URL of the subtitle file
   */
  url: string;

  /**
   * Language code (e.g., 'en', 'es', 'fr')
   */
  language?: string;

  /**
   * Display name for the subtitle track
   */
  name?: string;

  /**
   * MIME type of the subtitle file (e.g., 'text/vtt', 'application/x-subrip')
   */
  mimeType?: string;

  /**
   * Whether this subtitle track should be enabled by default
   * @default false
   */
  enabled?: boolean;
}

export interface RNWebVideoCasterInterface {
  /**
   * Opens the Web Video Caster app to play the specified video with advanced options
   * If the Web Video Caster app is not installed, it will redirect to the Play Store
   * @param options Configuration object with video URL and optional features
   */
  playVideo(options: WebVideoCasterOptions): void;

  /**
   * Simple play method for backward compatibility
   * @param videoURL The URL of the video to be casted
   * @deprecated Use playVideo() instead for access to new features
   */
  play(videoURL: string): void;

  /**
   * Check if the Web Video Caster app is installed on the device
   * @param callback Callback function that receives a boolean indicating if the app is installed
   */
  isAppInstalled(callback: (isInstalled: boolean) => void): void;
}

const { RNWebVideoCaster } = NativeModules;

export default RNWebVideoCaster as RNWebVideoCasterInterface;
