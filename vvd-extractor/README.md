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

```yaml
io: # all fields are required
  # this must be pointing to the output directory of the previous module, the task producer module. In another word, the directory specified in the 'io.output-directory' field of the previous module
  # can be an absolute path or a relative path from the application current running directory
  input-directory:
  # the output directory of this module, can be an absolute path or a relative path from the application current running directory
  output-directory:
  # the error directory of this module, used for reporting errors for debugging, can be an absolute path or a relative path from the application current running directory
  error-directory:

config: # configuration
  # all fields are required
  environment: 
    # specify the launch CMD of each dependency as a list of string.
    # All commands will be executed using the root directory of the project as the current directory
    python-launch-cmd:
    ffmpeg-launch-cmd:
    mediainfo-launch-cmd:

  # all fields are required
  batch:
    # number of files to process in parallel, default is 0 which represents # of cores
    batch-size: 0 

  # all fields are required
  process:
    # This two fields control the timeout of each process in this module
    # Each process in this module should be run in fairly quick time, even a one-hour video should be run in less than 2 minutes
    # if you think you need more time, change the setting here
    timeout: 5
    unit: minutes

  # all fields are required
  retry:
    retry-on-extraction: 2 # number of times to retry on audio extraction error
    retry-on-tagging: 2 # number of times to retry on audio file tagging error

  # optional fields
  current-time:
    # The module will modify the last modified time of the audio file to match the order of input tasks.
    # Specifically, a task with a smaller order number in the `-task` JSON file will have an earlier last modified time.
    # But the module needs a base timestamp to calculate the last modified time of each audio file
    # By default, this field is empty means using the time when this app is launched.
    # If you want to start from a specific time, change the setting here
    # The format is yyyy-MM-dd HH:mm:ss, with the local time zone in this running device
    start-from: ""
```