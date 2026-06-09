# EyeGuard Icons Packaging Guide

This directory holds the platform-specific icons and packaging assets for EyeGuard.

## Windows Icon
- **Path**: `packaging/windows/eye.ico`
- **Generation**: Generated automatically by the Maven build using the `IconConverter` utility.

## macOS Icon
- **Path**: `packaging/macos/eye.icns`
- **Generation**: Create on macOS using the following steps:
  1. Create an iconset directory:
     ```bash
     mkdir eye.iconset
     ```
  2. Scale the source `eye.png` using the `sips` command line tool:
     ```bash
     sips -z 16 16     src/main/resources/images/eye.png --out eye.iconset/icon_16x16.png
     sips -z 32 32     src/main/resources/images/eye.png --out eye.iconset/icon_16x16@2x.png
     sips -z 32 32     src/main/resources/images/eye.png --out eye.iconset/icon_32x32.png
     sips -z 64 64     src/main/resources/images/eye.png --out eye.iconset/icon_32x32@2x.png
     sips -z 128 128   src/main/resources/images/eye.png --out eye.iconset/icon_128x128.png
     sips -z 256 256   src/main/resources/images/eye.png --out eye.iconset/icon_128x128@2x.png
     sips -z 256 256   src/main/resources/images/eye.png --out eye.iconset/icon_256x256.png
     sips -z 512 512   src/main/resources/images/eye.png --out eye.iconset/icon_256x256@2x.png
     sips -z 512 512   src/main/resources/images/eye.png --out eye.iconset/icon_512x512.png
     sips -z 1024 1024 src/main/resources/images/eye.png --out eye.iconset/icon_512x512@2x.png
     ```
  3. Compile the iconset into a `.icns` file using `iconutil`:
     ```bash
     iconutil -c icns eye.iconset
     ```
  4. Move the output to `packaging/macos/eye.icns`.
