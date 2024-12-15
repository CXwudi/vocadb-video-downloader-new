# Extractor

This module, as the final module, mainly takes charge of two things:

1. Extracting and wrapping the audio source from
   the downloaded media source from the previous module (`vvd-downloader`).
2. Filling the metadata and embedding the thumbnail of the extracted audio file
   with the song information from the `-songInfo` JSON file

The output of this module is only a directory of audio files with metadata and thumbnail embedded.

## Before moving on

If you haven't read the common document yet, please read it first: [Common document](../doc/common%20part.md)

## Additional Prerequisites

Besides the common prerequisites, you also need:

1. Python3 (This module relies on some python codes to perform metadata manipulation)
2. `pip install mutagen` (Run this command to install the mutagen library)
3. FFmpeg (Highly recommended the [yt-dlp's distribution](https://github.com/yt-dlp/FFmpeg-Builds/releases/tag/latest))
4. [MediaInfo CLI](https://mediaarea.net/en/MediaInfo/Download)

## All configurations

All configurations can be found in [`application.yml`](./src/main/resources/application.yml) file.

Below is a copy of the content of the `application.yml` file for your reference. However, make sure to check for any updates to
the file yourself:

https://github.com/CXwudi/vocadb-video-downloader-new/blob/292351fcd341b22e8697a01ffdf657a5d9fa979f/vvd-extractor/src/main/resources/application.yml#L1-L62
