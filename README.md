# vocadb-video-downloader-new

An integrated cli-based media archiving solution for VocaDB, it can:

1. read a favourite list in VocaDB and download them into a folder of JSON files (by calling [VocaDB APIs](https://github.com/VocaDB/vocadb-openapi-client-java/blob/main/README%20Original.md#documentation-for-api-endpoints))
2. download the PV for each songs in the favourite list (using the output from 1. as input)
3. extract the audio track from PV and add the thumbnail, tags to the extracted audio track (using the output from 2. as input)

These 3 steps are implemented respectively as

1. `vvd-taskproducer`
2. `vvd-downloader`
3. `vvd-extractor` (Currently work in progress)

A proper documentation for each module will be provided after `vvd-extractor` is completed.

But for now, if you really want to use this program, contact me through email or Discord at CXwudi#3565
