# Minecraft Server Resource Pack Bot

## Introduction
If you need to dynamically update your resource pack hosted on Dropbox, this bot is for you!
You and your friends can choose a channel in your discord server to deposit your new custom textures, and this bot will automatically send them to the Resource pack in Dropbox, after the proper setup.

## How to set it up?

You'll need to chosoe a discord channel for the bot to operate in. Use **-pack help** to figure out how. You'll also need:
- A Discord Developer Token (Paste it in **TOKEN** inside **Bot.java**);
- A Dropbox Developer Access Token (Paste it in **TOKEN** inside **DropboxUtils.java**);
- To change DROPBOX_PATH to the right CIT path on your.

## How does it work?

This bot utilizes the Dropbox Java API and the Discord Java API to transfer files between both platforms.
