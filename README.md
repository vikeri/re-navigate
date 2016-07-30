# re-navigate
> Example of React Native Navigation with [re-frame](https://github.com/Day8/re-frame)/[re-natal](https://github.com/drapanjanas/re-natal/)

This example uses React Native's new Navigation component [NavigationExperimental](https://github.com/ericvicenti/navigation-rfc) which has a more FRP-like setup (like redux) and thus it works ridiculously well with [re-frame](https://github.com/Day8/re-frame).

## Example code

It is based on the scaffold from [re-natal](https://github.com/drapanjanas/re-natal/), almost everything is found in [navigator-cljs.ios.core](src/navigator_cljs/ios/core.cljs)

## Run

Requirements: 
- node & npm
- leiningen `brew install leiningen`
- re-natal & react-native-cli `npm install -g re-natal react-native-cli` 

`cd` into the directory.

```
npm install && lein prod-build && react-native run-ios
```

## Todo

 - [ ] Get Tabs to work


## Notes

- React (15.2.1)
- React Native (0.31.0)

In the future this might become a library if it would be useful to reuse things like the [navigation handlers](src/navigator_cljs/handlers.cljs#L40-L62) and the [db schema](src/navigator_cljs/db.cljs#L5-L15).

It has been tested on both Android and iOS.

## Contributors

[Viktor Eriksson](https://github.com/vikeri)

[Anthony Mittaz](https://github.com/sync)
