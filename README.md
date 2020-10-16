# react-native-thumbnails-light

## Getting started

`$ npm i react-native-thumbnails-light`

#### iOS

1. In pod add `pod 'RNThumbnailsLight', :path => '../node_modules/react-native-thumbnails-light/ios/RNThumbnailsLight.podspec'`
2. pod install
2. Run your project 

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

## Usage
```javascript

import React, { useEffect, useState } from 'react';
import { Image, View, Text } from 'react-native';
import ThumbnailsLight from 'react-native-thumbnails-light';

const VideoThumnails = ({ url }) => {
	const [image, setImage] = useState(null);

	useEffect(() => {
		if (!image) {
			generateThumbnail();
		}
	}, []);

	const getImageUrlFromVideo = async () => {
		try {
			const thumbnailsLight = new ThumbnailsLight();
			const { uri } = await thumbnailsLight.getThumbnail(url, { time: 5000 });
			setImage(uri);
		} catch (e) {
			console.warn(e);
		}
	};

    return (
		<View>
			{image ? (
                <Image
                    source={{ uri: image }}
                    style={{ width: 100, height: 100 }}
                />
            ) : (
                <Text>Loading...</Text>
            )}
		</View>
	);
}

export default VideoThumnails;
```
