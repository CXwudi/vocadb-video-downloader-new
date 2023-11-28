def add_tag(input_file, thumbnail_file, label_dict, info_dict, audio_extractor_name, audio_tagger_name):
  """
  real add tags
  """
  from mutagen.mp3 import MP3, EasyMP3, TextFrame
  from mutagen.id3 import ID3, APIC, TXXX, ID3NoHeaderError
  import imghdr

  # Use EasyMP3 for simple id3 tags
  file = EasyMP3(input_file, ID3=ID3)
  file.delete()
  file["title"] = info_dict["defaultName"]
  file["artist"] = info_dict["artistString"]
  file["date"] = info_dict["publishDate"]
  file["genre"] = "VOCALOID"
  file["comments"] = "All rights belong to {}".format(info_dict["artistString"].split("feat.")[0].strip())
  file.save(v2_version=3)

  # Use MP3 for more complex id3 tags
  tags = ID3(input_file)

  with open(thumbnail_file, "rb") as f:
    image_type = imghdr.what(f)
    thumbnail_data = f.read()

  # Add APIC frame for album art
  tags.add(APIC(3, 'image/' + image_type, 3, 'Front cover', thumbnail_data))

  # Add TXXX frames for custom strings
  txxx_names = ["downloaded by", "pv url", "extracted by",
                "tags edited by", "tags provided by", "made by"]

  txxx_values = [label_dict["downloaderName"],
                 list(filter(lambda x: x["id"] == label_dict["vocaDbPvId"], info_dict["pvs"]))[0]["url"],
                 audio_extractor_name,
                 audio_tagger_name,
                 "VocaDB (https://vocadb.net/S/{})".format(info_dict["id"]),
                 "CXwudi's vocadb-video-downloader-new (https://github.com/CXwudi/vocadb-video-downloader-new)"]

  for name, value in zip(txxx_names, txxx_values):
    tags.add(TXXX(encoding=3, desc=name, text=value))

  if info_dict["albums"]:
    album_strs = map(lambda album: album["name"], info_dict["albums"])
    tags.add(TXXX(encoding=3, desc="included by", text="Albums [{}]".format(", ".join(album_strs))))

  tags.save(input_file, v2_version=3)

# ...the rest of your original code that calls this function can remain the same


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
  parser.add_argument("-atn", "--audio-tagger-name", required=True, help="audio tagger name")

  # Grab the Arguments
  args = parser.parse_args()
  with open(args.label, "rb") as f:
    label_dict = json.load(f)
  with open(args.info, "rb") as f:
    info_dict = json.load(f)

  # commented out due to a japanese wired encoding problem
  # e.g. 霊々音頭でまた来世 is 霊々音頭て　゙また来世　in niconico website title
  # print("received args are " + str(vars(args)))

  add_tag(args.input, args.thumbnail, label_dict, info_dict, args.audio_extractor_name, args.audio_tagger_name)


if __name__ == "__main__":
  main()
