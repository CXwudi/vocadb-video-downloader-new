# vocadb-video-downloader-new

An integrated CLI-based media archiving solution for VocaDB. It can:

1. Read a VocaDB favourite list and save it as a folder of JSON files
   by calling the
   [VocaDB APIs](https://vocadb.net/swagger/index.html).
2. Download the PV or audio for each song in the favourite list
   using the output from step 1 as input.
3. Extract the audio track from the PV if necessary, and add the
   thumbnail and tags to the audio track using the output from step 2
   as input.

These 3 steps are implemented respectively as:

1. [`vvd-taskproducer`](./vvd-taskproducer)
2. [`vvd-downloader`](./vvd-downloader)
3. [`vvd-extractor`](./vvd-extractor)

## Prerequisites

1. Java 25 or above.

All 3 modules share a common document. Please read it first:
[Common document](./doc/common%20part.md)

Then for each module, read its own `README.md` file for more details.
