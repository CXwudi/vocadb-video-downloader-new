import base64
import imghdr
from mutagen.flac import Picture
from mutagen.oggopus import OggOpus


def write_thumbnail(file, thumbnail_file):
  """
  writing thumbnail to ogg opus file

  reference: https://github.com/ytdl-org/youtube-dl/pull/28894/files
  https://mutagen.readthedocs.io/en/latest/user/vcomment.html
  """
  with open(thumbnail_file, "rb") as f:
    thumbnail_data = f.read()
    image_type = imghdr.what(f)
  
  p = Picture()
  p.data = thumbnail_data
  p.mime = image_type
  p.type = 3 # means front cover

  encoded_thumb = base64.b64decode(p.write())

  file["metadata_block_picture"] = [encoded_thumb.decode("ascii")]
  

def add_tag(input_file, thumbnail_file, resource_dict, info_dict):
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

  file["pv url"] = resource_dict["pvUrl"]
  file["downloaded by"] = "yt-dlp (included tsukumijima's niconico fix, see https://github.com/yt-dlp/yt-dlp/pull/49)"
  file["extracted by"] = "FFmpeg"
  file["tags edited by"] = "Python mutagen library"
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
  
  # commented out due to a japanese wired encoding problem
  # e.g. 霊々音頭でまた来世 is 霊々音頭でまた来世　in niconico website title
  # print("received args are " + str(vars(args))) 

  add_tag(args.input, args.thumbnail, resource_dict, info_dict)

if __name__ == "__main__":
  main()