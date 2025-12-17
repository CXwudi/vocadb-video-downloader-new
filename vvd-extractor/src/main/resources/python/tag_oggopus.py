import base64
import os
import filetype
from mutagen.flac import Picture
from mutagen.oggopus import OggOpus


def get_image_type(data, file_path):
  """
  Get image type using filetype library with fallback to file extension.

  Uses filetype library instead of deprecated imghdr (removed in Python 3.13).
  """
  image_type = None
  kind = filetype.guess(data)
  if kind is not None and kind.mime.startswith('image/'):
    image_type = kind.extension
  else:
    # Fallback to file extension
    ext = os.path.splitext(file_path)[1].lower().lstrip('.')
    if ext in ('jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'):
      image_type = ext

  if image_type == 'jpg':
    return 'jpeg'
  return image_type


def write_thumbnail(file, thumbnail_file):
  """
  writing thumbnail to ogg file

  reference: https://github.com/ytdl-org/youtube-dl/pull/28894/files
  https://mutagen.readthedocs.io/en/latest/user/vcomment.html
  """
  with open(thumbnail_file, "rb") as f:
    thumbnail_data = f.read()
    image_type = get_image_type(thumbnail_data, thumbnail_file)

  p = Picture()
  p.data = thumbnail_data
  p.mime = "image/" + image_type
  p.type = 3  # means front cover

  file["METADATA_BLOCK_PICTURE"] = base64.b64encode(p.write()).decode("ascii")


def add_tag(input_file, thumbnail_file, label_dict, info_dict, audio_extractor_name, audio_tagger_name):
  """
  real add tags
  """

  file = OggOpus(input_file)
  file.delete()
  file["title"] = info_dict["defaultName"]
  file["artist"] = info_dict["artistString"]
  file["date"] = info_dict["publishDate"]
  file["genre"] = "VOCALOID"
  file["comment"] = "All rights belong to {}".format(info_dict["artistString"].split("feat.")[0].strip())
  write_thumbnail(file, thumbnail_file)
  # the filter should fine exactly one PV, otherwise it would not come to vvd-extractor module
  pv_dict = list(filter(lambda x: x["id"] == label_dict["vocaDbPvId"], info_dict["pvs"]))[0]
  file["pv url"] = pv_dict["url"]
  file["downloaded by"] = label_dict["downloaderName"]
  file["extracted by"] = audio_extractor_name
  file["tags edited by"] = audio_tagger_name
  file["tags provided by"] = "VocaDB (https://vocadb.net/S/{})".format(info_dict["id"])
  file["made by"] = "CXwudi's vocadb-video-downloader-new (https://github.com/CXwudi/vocadb-video-downloader-new)"

  if info_dict["albums"]:
    album_strs = map(lambda album: album["name"], info_dict["albums"])
    file["included by"] = "albums [{}]".format(", ".join(album_strs))

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
  parser.add_argument("-atn", "--audio-tagger-name", required=True, help="audio tagger name")


  # Grab the Arguments
  args = parser.parse_args()
  with open(args.label, "rb") as f:
    label_dict = json.load(f)
  with open(args.info, "rb") as f:
    info_dict = json.load(f)

  # commented out due to a japanese wired encoding problem
  # e.g. 霊々音頭でまた来世 is 霊々音頭でまた来世　in niconico website title
  # print("received args are " + str(vars(args))) 

  add_tag(args.input, args.thumbnail, label_dict, info_dict, args.audio_extractor_name, args.audio_tagger_name)


if __name__ == "__main__":
  main()
