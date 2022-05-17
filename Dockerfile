FROM ubuntu:latest as base
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
ENV DEBIAN_FRONTEND noninteractive ## everyone is using this, so I add it in
RUN apt-get update \
    && apt-get upgrade -y

FROM base as ffmpeg-builder
RUN apt-get install -y  \
    curl \
    xz-utils ## needed for tar

# install ffmpeg based on different architectures
WORKDIR /opt/ffmpeg
RUN arch=$(arch | sed s/aarch64/arm64/ | sed s/x86_64/amd64/) && \
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

## using debian 11-slim, which is bullseye-slim
FROM base as main
MAINTAINER "CXwudi"

RUN apt-get install -y \
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
RUN apt-get install -y \
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
COPY --from=eclipse-temurin:17-focal $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# install ffmpeg from builder
COPY --from=ffmpeg-builder /opt/ffmpeg/bin /usr/local/bin

# simplify image
RUN rm -rf /var/lib/apt/lists/*
