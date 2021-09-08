# Minecraft Server Resource Pack Bot

## Introduction
If you need to dynamically update your resource pack hosted on Dropbox, this bot is for you!
You and your friends can choose a channel in your discord server to deposit your new custom textures, and this bot will automatically send them to the Resource pack in Dropbox, after the proper setup.

## How to set it up?

You'll need to chosoe a discord channel for the bot to operate in. Use **-pack help** to figure out how. You'll also need:
- A Discord Developer Token (Paste it in **TOKEN** inside **Bot.java**);
- A Dropbox Developer Access Token (Paste it in **ACCESS_TOKEN** inside **DropboxUtils.java**);
- To change **PACK_NAME** to the right name of your Pack hosted on Dropbox.

## How to use it?

Once the bot is running on one of your discord channels, all you have to do is send the files, like in the following figure.

![image](https://user-images.githubusercontent.com/75852333/132571206-22174293-40f1-42d5-8375-2e1237c80b5e.png)


## How does it work?

This bot utilizes the **Dropbox Java API** and the **Discord Java API** to transfer files between both platforms.
