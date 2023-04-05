<p align="center">
	<a href='https://github.com/gyund/chatpaths/actions/workflows/android.yml'><img src="https://github.com/gyund/chatpaths/actions/workflows/android.yml/badge.svg" /></a>
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


<p align="center">
	<a href='https://play.google.com/store/apps/details?id=com.gy.chatpaths.aac.app'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'"/></a>
</p>

## Structure

```
.
├── app                    # Mobile app
├── modules              
│   ├── model              # repository, DAOs, helpers
│   ├── builder            # utilities to build canned path collection heirarchies
```

## Incorporated Works

Chatpaths makes use of a number of 3rd party visual resources as well as compositions and
modifications of these resources to provide visuals. These include:

- [googlefonts/noto-emoji](https://github.com/googlefonts/noto-emoji/blob/master/LICENSE)
- [twitter/twemoji](https://github.com/twitter/twemoji/blob/master/LICENSE-GRAPHICS)
- [openmoji](https://github.com/hfg-gmuend/openmoji/blob/master/LICENSE.txt)

## Signing a Release

To build a _signed_ release, add these lines to your ~/.gradle/gradle.properties file

```
chatpathsKeyStoreFile=/Users/username/git/secret.keystore
chatpathsKeyStorePassword=
chatpathsKeyStoreAlias=
chatpathsKeyStoreAliasPassword=
```
