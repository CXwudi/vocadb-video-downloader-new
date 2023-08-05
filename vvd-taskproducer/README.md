# Task Producer

This module read a VocaDB favourite list, and for each song in the list, it will create a `-task` JSON file storing some metadata needed for the next two modules (`vvd-downloader` and `vvd-extractor`), and a `-info` JSON file storing all useful information about the song.

All JSON files are stored in the output directory specified in the configuration.

## Before moving on

If you haven't read the common document yet, please read it first: [Common document](../doc/common%20part.md)

## Additional Prerequisites

None

## All configurations

All configurations can be found in [`application.yml`](./src/main/resources/application.yml) file.

Below is a copy of the content of the `application.yml` file for your reference. However, make sure to check the latest version of the file yourself:

```yaml
io: # all fields are required
  # the VocaDB favourite list ID in Int, can be retrieved from URL https://vocadb.net/L/<The ID is here>
  input-list-id:
  # the output directory of this module, can be an absolute path or a relative path from the application current running directory
  output-directory:
  # the error directory of this module, used for reporting errors for debugging, can be an absolute path or a relative path from the application current running directory
  error-directory:

config: # all fields are required
  # the base url of VocaDB
  # can change it to https://utaitedb.net if your list is on UtaiteDB, but may some compatibility issues
  base-url: https://vocadb.net
  # the user agent to be used for calling VocaDB APIs, to help VocaDB staff identify this program (see https://github.com/VocaDB/vocadb/wiki/Public-API#api-usage-rules) 
  user-agent:
  # max number of songs you can fetch from one api request of the vocadb favourite list, max is 50
  api-page-size: 50
  # maximum amount of song can be processed at the same time
  # if set to < 1, it will be the number of logical CPU cores
  batch-size: 0
```
