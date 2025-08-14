#!/bin/bash

if [[ "$1" == "" ]]; then
  exit 1
  #yt-dlp --extract-audio --audio-quality 0 --print after_move:filepath ytsearch:"i really want to stay in your home" -o "$HOME/music/%(title)s.%(ext)s"
fi
logs=$(/home/k1mch1/.local/bin/yt-dlp --extract-audio --audio-quality 0 --print after_move:filepath ytsearch:"$1" -o "$HOME/music/%(title)s.%(ext)s")
echo $logs
#if [[ $logs =~ \[ExtractAudio\][[:space:]]*Destination:[[:space:]].+/(.+\.[0-9a-z]+)[[:space:]] ]]; then
file_name="$(basename "$logs")"
mpc update >/dev/null
echo "$file_name"
mpc idle database
mpc add "$file_name"
