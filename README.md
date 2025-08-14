# Highly Recommended Programs
- `mpd`: Music Player Daemon, the actual daemon streaming music, managing playlists queues etc... (is hosted over a port $\implies$ can port forward)
- `mpc`: Music Player Client, a client that connects to mpd
- `ncmpcpp`: NCurses Music Player Client Plus Plus, a wrapper around `mpc` I believe...

# Setup Of Client Side

## `mpc` (highly recommended)

Latency relatively tame. Recommnd cuz `mpc` is tailor-made for `mpd`.

To connect (where `$SERVER_IP` is an environmental variable, or just your ip, domain name, etc...):

```bash
mpc add http://$SERVER_IP:8000
```

Now the server should be in your playlist. We can use `ncmpcpp` to navigate to the server and play it as if you're playing a regular song.

## `mpv` (second best)

These settings seems to give the best latency.

```bash
mpv http://$SERVER_IP:8000   --no-cache   --untimed   --no-audio-display   --gapless-audio=yes
```

## `vlc`
I don't know how to make `vlc` low latency, but it's probably doable. `vlc` is pretty crossplatform so that's good.

```bash
vlc http://$SERVER_IP:8000
```

or open `vlc`, and go to "media" > "open network stream" > insert `http://$SERVER_IP:8000` or your port, etc...

# Setup Of Server Side

## `mpd` config

```bash
music_directory        "~/music/" # or your music directory
playlist_directory     "~/.config/mpd/playlists"
db_file                "~/.local/state/mpd/database"
log_file               "~/.local/state/mpd/log"
pid_file               "~/.local/state/mpd/pid"
state_file             "~/.local/state/mpd/state"
sticker_file           "~/.local/state/mpd/sticker.sql"

bind_to_address        "localhost"
port                   "6600"

# disable if server is remote

#audio_output {
#    type            "pipewire"       # or "alsa" or "pipewire", depending on your system
#    name            "My Audio"
#}
#
#audio_output {
#type               "fifo"
#name               "my_fifo"
#path               "/tmp/mpd.fifo"
#format             "44100:16:2"
#}

audio_output {
    type        "httpd"
    name        "My HTTP Stream"
    encoder     "opus"      # optional
    port        "8000"
#   quality     "5.0"           # do not define if bitrate is defined
    bitrate     "256000"            # do not define if quality is defined
    format      "48000:16:2"
    always_on       "yes"           # prevent MPD from disconnecting all listeners when playback is stopped.
    tags            "yes"           # httpd supports sending tags to listening streams.
}

```

> [!NOTE]
> You might also need to create some directories since `mpd` won't do it for you...
> And you might also need disable firewall on the port you're hosting it on (if doing remote hosting... can't guarantee security though)

# My Minecraft `mpc` plugin

This so called "plugin" was "made" by someone who can't code a single line of ` java`.
- [x] wrapper around `mpc` for simple commands (commands with one argument)
- [x] used `yt-dlp` to search, extract audio and add to mpc playlist
- ~~ [] security ~~
- ~~ [] errors, logs and feedback ~~

## Usage
There are two commands `/mpc <arg1>` and `/mpc_add song title`

### `/mpc`
same usage as `mpc` in `bash`, refer to [this](https://www.musicpd.org/doc/mpc/html/) documentation.
but some useful commands include:
- `/mpc toggle` toggling pause and unpause
- `/mpc clear` clear main playlist
- `/mpc next` go to next song
- `/mpc prev` go to previous song

### `/mpc_add`
Searches youtube for the song. For instance, to search for a song called "go with the flow": 

```
/mpc_add go with the flow
```

## Installation
`SDKMAN` (good docs, no need for guide)

```bash
sdk install java
```

`Maven` is used to build the project 
```bash
sdk install maven
```

A spigot (or something) plugin must kinda have the following directory structure:

```
./MyPlugin/
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           └── myplugin
        │               └── MyPlugin.java
        └── resources
            └── plugin.yml
```

`pom.xml` probably tells `maven` what to do.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
   https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>ExternalCommand</artifactId>
  <version>1.0</version>
  <name>ExternalCommand</name>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>21</source> <!-- Use your server's Java version -->
          <target>21</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
  <repositories>
    <repository>
      <id>papermc</id>
      <url>https://repo.papermc.io/repository/maven-public/</url>
    </repository>
  </repositories>
  <dependencies>
    <dependency>
      <groupId>io.papermc.paper</groupId>
      <artifactId>paper-api</artifactId>
      <version>1.21.8-R0.1-SNAPSHOT</version> <!-- Use your server version -->
      <scope>provided</scope>
    </dependency>
  </dependencies>
</project>
```
> [!NOTE]
> To find your paper server version (with the correct syntax and all that) do `/ver` or `/version` in the minecraft console

`plugin.yml` is probably meant for the spigot or paper server to correctly do syntax error logging and for us to fill metadata about the plugin. It looks something like this:

```yaml
name: ExternalCommand
version: 1.0
main: com.example.myplugin.MyPlugin
api-version: 1.20
commands:
  mpc:
    description: toggles the state of the music 
    usage: /mpc
  mpc_add:
    description: queries youtube, downloads the audio and puts it in mpd's queue
    usage: /mpc_add "<song>"
```

The `MyPlugin.java` file is the meat of the plugin. It looks something like this:

```java
package com.example.myplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MyPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getLogger().info("ExternalCommand plugin enabled!");
  }

  @Override
  public void onDisable() {
    getLogger().info("ExternalCommand plugin disabled.");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("mpc")) {
      try {
        // Replace this with your actual script or command
        String[] fixed = {"mpc"};
        String[] fullCommand = new String[fixed.length + args.length];
        // copy from source array at inndex, to dest array at index, and copy this amount of lenth
        System.arraycopy(fixed,0, fullCommand, 0, fixed.length);
        System.arraycopy(args, 0, fullCommand, fixed.length, args.length);
        Process process = new ProcessBuilder(fullCommand).start();
        sender.sendMessage("✅ toggled mpc!");
      } catch (IOException e) {
        sender.sendMessage("❌ Failed to toggle mpc");
        e.printStackTrace();
        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("mpc_add")) {
      try {
        // Replace this with your actual script or command
          String fullArg = String.join(" ", args);
          sender.sendMessage(args);
          //sender.sendMessage("source /home/k1mch1/.bashrc && mpc_auto_add.sh " + fullArg);
          //Process process = new ProcessBuilder("/home/k1mch1/.local/bin/mpc_auto_add.sh", fullArg).start();
          ProcessBuilder pb = new ProcessBuilder("/home/k1mch1/.local/bin/mpc_auto_add.sh", fullArg); 
          pb.start();
      } catch (IOException e) {
        sender.sendMessage("❌ something went wrong...");
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }
}
```

God... OOP and it's consequences has been a disaster for all of IT-race.
This `java` file is simply a wrapper for `mpc` and the searching program written in `bash` called `mpc_auto_add.sh`.

```bash
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
```

## Building using `maven`

In the directory containing `pom.xml`, run the following and 󰕹  pray

```bash
mvn package
```

Once that's done, there should be a new directory called `target` with a `.jar` file. (From my speculation, `.jar` is just a special kind of binary). Move that `.jar` file to the `plugins` directory in the main server directory and reload the server.


