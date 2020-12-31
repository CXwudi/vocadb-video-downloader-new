def add_tag(input_file, thumbnail_file, resource_dict, info_dict):
  """
  real add tags
  """
  from mutagen.easymp4 import EasyMP4
  from mutagen.mp4 import MP4, MP4Cover

  file = EasyMP4(input_file)
  file.delete()
  file["title"] = info_dict["defaultName"]
  file["artist"] = info_dict["artistString"]
  file["date"] = info_dict["publishDate"]
  file["genre"] = "VOCALOID or others"
  file.save()

  file = MP4(input_file)
  tags = file.tags
  with open(thumbnail_file, "rb") as f:
    thumbnail_data = f.read()

  tags["covr"] = [MP4Cover(data=thumbnail_data)]
  tags["----:com.apple.iTunes:pv url"] = resource_dict["pvUrl"].encode()
  tags["----:com.apple.iTunes:downloaded by"] = "youtube-dl (tsukumijima's niconico fix version https://github.com/tsukumijima/youtube-dl)".encode()
  tags["----:com.apple.iTunes:extracted by"] = "FFmpeg".encode()
  tags["----:com.apple.iTunes:tags edited by"] = "Python mutagen library".encode()
  tags["----:com.apple.iTunes:tags provided by"] = "VocaDB (https://vocadb.net/S/{})".format(info_dict["id"]).encode()
  tags["----:com.apple.iTunes:made by"] = "CXwudi's vocadb-video-downloader-new (https://github.com/CXwudi/vocadb-video-downloader-new)".encode()


  if info_dict["albums"]:
    album_strs = map(lambda album: album["name"], info_dict["albums"])
    tags["----:com.apple.iTunes:obtainable from"] = "This song is obtainable from [{}]".format(", ".join(album_strs)).encode()
  
  file.save()

def main():
  import json
  import argparse
  # Set up CLI Arguments
  parser = argparse.ArgumentParser()

  parser.add_argument("-i", "--input", required=True, help="input audio file name")
  parser.add_argument("-r", "--resource", required=True, help="input resource file")
  parser.add_argument("-t", "--thumbnail", required=True, help="input thumbnail file")
  parser.add_argument("-if", "--info", required=True, help="input info file")

  # Grab the Arguments
  args = parser.parse_args()
  with open(args.resource, "rb") as f:
    resource_dict = json.load(f)
  with open(args.info, "rb") as f:
    info_dict = json.load(f)
  
  print("received args are" + str(args))

  add_tag(args.input, args.thumbnail, resource_dict, info_dict)

if __name__ == "__main__":
  main()