<p align="center">
    <img src="https://repository-images.githubusercontent.com/621977748/fe1876d7-8b11-4fb1-adee-112fe2ce15d6" />
</p>

# chatpaths
Non-verbal communication app for helping kids communicate with the world :)

[![Android CI](https://github.com/gyund/chatpaths/actions/workflows/android.yml/badge.svg)](https://github.com/gyund/chatpaths/actions/workflows/android.yml)

<a href='https://play.google.com/store/apps/details?id=com.gy.chatpaths.aac.app'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' style="max-width:100%;"/></a>

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
