![AnimeTwist](./app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

# Official Anime Twist android application

Client app for [twist.moe](https://twist.moe/) anime streaming site. 

## App development summary

- Written in Kotlin
- Following MVVM architectural pattern
- Uses Navigation, Livedata and ViewModel architecture components
- Uses Koin for dependency injection

## Submitting issues

Any feature requests or bugs can be reported [here](../../issues).

Quick guidelines:  
***For bugs:*** Describe the problem and the steps to reproduce it, maybe include some screenshots from the app for reference.  
***For feature requests:*** Describe all of the new features in detail so they can be easily understood and implemented.

## Setup the dev environment
First of all, you'll need the latest version of the android studio or any other version that supports the points mentioned in the dev summary.

As for the project setup, just clone this repository using `git clone https://github.com/AnimeTwist/twist-mobile.git`

### API / Decrypt keys

Before building you'll need to provide secrets for some services we use in the app. Currently, it's just one decrypt key to decode anime media data from the API, but in the future, we'll maybe expand them by adding anime tracking services.

To make these secrets accessible to the app add secrets.properties file to the root directory. Modify its contents to look like this example:
```
decrypt_key=<insert>
```
<sub> *(We can't release the decrypt key to the public for obvious reasons, just add some random string of characters so the app builds. If you seriously need it for the development contact someone from the dev team)* </sub>

## Contributing

Contributions and patches are encouraged and may be submitted by forking this project and
submitting a pull request. You can also help out and implement some of the open feature requests [here](../../issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement+no%3Aassignee).

## License & Privacy Policy

This project is licensed under the [GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html) License. You can read the details here:
- [License](./LICENSE)
- [Privacy Policy](./PRIV_POLICY.md)
