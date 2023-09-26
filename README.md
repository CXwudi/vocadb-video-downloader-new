# vocadb-video-downloader-new

An integrated cli-based media archiving solution for VocaDB, it can:

1. read a VocaDB favourite list and save it as a folder of JSON files (by calling [VocaDB APIs](https://vocadb.net/swagger/index.html))
2. download the PV or audio for each songs in the favourite list (using the output from 1. as input)
3. extract the audio track from PV if necessary, and add the thumbnail, tags to the audio track (using the output from 2. as input)

These 3 steps are implemented respectively as

1. [`vvd-taskproducer`](./vvd-taskproducer)
2. [`vvd-downloader`](./vvd-downloader)
3. [`vvd-extractor`](./vvd-extractor)

All 3 modules share a common document, please read it first: [Common document](./doc/common%20part.md)

Then for each module, please read their own README.md file for more details.
