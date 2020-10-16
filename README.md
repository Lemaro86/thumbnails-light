
# react-native-thumbnails-light

## Getting started

`$ npm install react-native-thumbnails-light --save`

### Mostly automatic installation

`$ react-native link react-native-thumbnails-light`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-thumbnails-light` and add `RNThumbnailsLight.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNThumbnailsLight.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNThumbnailsLightPackage;` to the imports at the top of the file
  - Add `new RNThumbnailsLightPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-thumbnails-light'
  	project(':react-native-thumbnails-light').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-thumbnails-light/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-thumbnails-light')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNThumbnailsLight.sln` in `node_modules/react-native-thumbnails-light/windows/RNThumbnailsLight.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Thumbnails.Light.RNThumbnailsLight;` to the usings at the top of the file
  - Add `new RNThumbnailsLightPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNThumbnailsLight from 'react-native-thumbnails-light';

// TODO: What to do with the module?
RNThumbnailsLight;
```
  