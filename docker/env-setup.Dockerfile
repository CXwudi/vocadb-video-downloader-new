## using debian 11-slim, which is bullseye-slim
FROM debian:bullseye-slim as base
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
## everyone is using this, so I add it in
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update

FROM base as external_bin_setuper
RUN apt-get install -y  \
    curl \
    xz-utils ## https://superuser.com/questions/801159/cannot-decompress-tar-xz-file-getting-xz-cannot-exec-no-such-file-or-direct

# install ffmpeg based on different architectures
WORKDIR /opt/ffmpeg
# get architecture
RUN arch=$(arch | sed s/aarch64/arm64/ | sed s/x86_64/amd64/) && \
    # setup ffmpeg
    # for amd64, use yt-dlp specific ffmpeg
    if [ "$arch" == "amd64" ]; then \
          curl --request GET -L \
             --url 'https://github.com/yt-dlp/FFmpeg-Builds/releases/download/latest/ffmpeg-n5.0-latest-linux64-gpl-5.0.tar.xz'\
             --output 'ffmpeg.tar.xz' \
          && tar -xvf ffmpeg.tar.xz \
          && mkdir bin \
          && mv ffmpeg-*/bin/* ./bin; \
    else \
         curl --request GET -L \
                 --url 'https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-${arch}-static.tar.xz'\
                 --output 'ffmpeg.tar.xz' \
         && tar -xvf ffmpeg.tar.xz \
         && mkdir bin \
         && mv ffmpeg-*/ff* ./bin; \
    fi

FROM base as main
LABEL Author="CXwudi"

RUN apt-get install -y --no-install-recommends \
    # needed by add-apt-repository
    #    software-properties-common \
    locales \
    mediainfo

RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && locale-gen # set UTF-8 to support Chinese and Japanese
ENV \
  LANG="en_US.UTF-8" \
  LANGUAGE="en_US:en" \
  LC_ALL="en_US.UTF-8"

# let the python installation be a saperate step, so that we can change to any installation method without invalidating previous layers
RUN apt-get install -y --no-install-recommends\
    python3-pip \
    && update-alternatives --install /usr/bin/python python /usr/bin/python3 2 \
    && pip install --upgrade  \
    pip \
    mutagen  \
    yt-dlp
#RUN add-apt-repository ppa:deadsnakes/ppa -y \
#    && apt-get update \
#    && apt-get install python3.10 -y \
#    && update-alternatives --install /usr/bin/python python /usr/bin/python3.10 2 \
#    && pip3 install --upgrade pip && pip3 install --upgrade mutagen

# install Java from other image,
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:17 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# install ffmpeg from builder
COPY --from=external_bin_setuper /opt/ffmpeg/bin /usr/local/bin

# simplify image
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
