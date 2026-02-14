<p align="center">
	<a href='https://www.gnu.org/licenses/gpl-3.0'><img src="https://img.shields.io/badge/License-GPLv3-blue.svg" /></a>
	<a href='https://github.com/gyund/chatpaths/actions/workflows/android.yml'><img src="https://github.com/gyund/chatpaths/actions/workflows/android.yml/badge.svg?branch=main" /></a>
	<a href='https://github.com/gyund/chatpaths/actions/workflows/gradle-dependency-submission.yml'><img src="https://github.com/gyund/chatpaths/actions/workflows/gradle-dependency-submission.yml/badge.svg" /></a>
	<a href="https://cla-assistant.io/gyund/chatpaths"><img src="https://cla-assistant.io/readme/badge/gyund/chatpaths" alt="CLA assistant" /></a>
</p>

<p align="center">
	<img src="https://repository-images.githubusercontent.com/621977748/fe1876d7-8b11-4fb1-adee-112fe2ce15d6" />
</p>

ChatPaths is a communication app for helping non-verbal kids communicate while on the go. Currently it supports:

- Building and customizing personalized communication paths
- Contrasting color scheme to support visual needs
- Smart sorting algorithms to allow one-touch reordering of speech paths based on usage
- Manual ordering and anchoring of paths you don't want sorted
- Text to Speech prompting
- Voice recording of prompts for better contextual and familiar cues
- Multiple user profiles to support therapy and classroom environments
- Localization support for english, spanish, french, and hindi


## Structure

```
.
├── app                    # Mobile app
```

## Additional Notes

### Demo Video

[![Demo Video](https://img.youtube.com/vi/GlIo0wvHSXY/hqdefault.jpg)](https://www.youtube.com/embed/GlIo0wvHSXY?si=VoY87uA9UwJEhmEH)

### Image Search

Built in search functionality for paths uses translations that can be found [here](modules/builder/src/main/res/values/images.xml).
The function `getSearchString` found in the [UriHelper](modules/builder/src/main/java/com/gy/chatpaths/builder/UriHelper.kt)
is used to convert a search query from the user's native language to english, so that we can access
the appropriate resource string identified in english. This is a bit weird, and there's probably
a better way to do this.

Basically, if you add a new image to the system, you have to add a corresponding `search_<name>` entry.
The application will iterate through each image and retrieve the translation. If the string
being searched for is part of the string name, then it is selected for display.

## Incorporated Works

Chatpaths makes use of a number of 3rd party visual resources as well as compositions and
modifications of these resources to provide visuals. These include:

| Site                                                                                    | License                                                                  |
|-----------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| [googlefonts/noto-emoji](https://github.com/googlefonts/noto-emoji/blob/master/LICENSE) | ![License](https://img.shields.io/github/license/googlefonts/noto-emoji) |
| [twitter/twemoji](https://github.com/twitter/twemoji/blob/master/LICENSE-GRAPHICS)      | ![License](https://img.shields.io/github/license/twitter/twemoji)        |
| [openmoji](https://github.com/hfg-gmuend/openmoji/blob/master/LICENSE.txt)              | ![License](https://img.shields.io/github/license/hfg-gmuend/openmoji)    |


## Signing a Release

To build a _signed_ release, add these lines to your ~/.gradle/gradle.properties file

```
chatpathsKeyStoreFile=/Users/username/git/secret.keystore
chatpathsKeyStorePassword=
chatpathsKeyStoreAlias=
chatpathsKeyStoreAliasPassword=
```
