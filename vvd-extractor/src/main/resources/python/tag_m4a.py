def add_tag(input_file, thumbnail_file, label_dict, info_dict, audio_extractor_name):
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
  file["genre"] = "VOCALOID"
  file["comment"] = "All rights belong to {}".format(info_dict["artistString"].split("feat.")[0].strip())
  file.save()

  file = MP4(input_file)
  tags = file.tags
  with open(thumbnail_file, "rb") as f:
    thumbnail_data = f.read()

  tags["covr"] = [MP4Cover(data=thumbnail_data)]
  tags["----:com.apple.iTunes:downloaded by"] = label_dict["downloaderName"].encode()
  # the filter should fine exactly one PV, otherwise it would not come to vvd-extractor module
  pv_dict = list(filter(lambda x: x["id"] == label_dict["pvVocaDbId"], info_dict["pvs"]))[0]
  tags["----:com.apple.iTunes:pv url"] = pv_dict["url"].encode()
  tags["----:com.apple.iTunes:extracted by"] = audio_extractor_name.encode()
  tags["----:com.apple.iTunes:tags edited by"] = "Python mutagen library".encode()
  tags["----:com.apple.iTunes:tags provided by"] = "VocaDB (https://vocadb.net/S/{})".format(info_dict["id"]).encode()
  tags["----:com.apple.iTunes:made by"] = "CXwudi's vocadb-video-downloader-new (https://github.com/CXwudi/vocadb-video-downloader-new)".encode()


  if info_dict["albums"]:
    album_strs = map(lambda album: album["name"], info_dict["albums"])
    tags["----:com.apple.iTunes:included by"] = "Albums [{}]".format(", ".join(album_strs)).encode()

  file.save()


def main():
  import json
  import argparse
  # Set up CLI Arguments
  parser = argparse.ArgumentParser()

  parser.add_argument("-i", "--input", required=True, help="input audio file")
  parser.add_argument("-l", "--label", required=True, help="input label file")
  parser.add_argument("-t", "--thumbnail", required=True, help="input thumbnail file")
  parser.add_argument("-if", "--info", required=True, help="input info file")
  parser.add_argument("-aen", "--audio-extractor-name", required=True, help="audio extractor name")

  # Grab the Arguments
  args = parser.parse_args()
  with open(args.label, "rb") as f:
    label_dict = json.load(f)
  with open(args.info, "rb") as f:
    info_dict = json.load(f)

  # commented out due to a japanese wired encoding problem
  # e.g. 霊々音頭でまた来世 is 霊々音頭て　゙また来世　in niconico website title
  # print("received args are " + str(vars(args)))

  add_tag(args.input, args.thumbnail, label_dict, info_dict, args.audio_extractor_name)


if __name__ == "__main__":
  main()
