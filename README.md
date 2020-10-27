# react-native-thumbnails-light
Library for generation image from video. For video source you can use url.

## Getting started

`$ npm i react-native-thumbnails-light`

#### iOS

1. In pod add `pod 'RNThumbnailsLight', :path => '../node_modules/react-native-thumbnails-light/ios/RNThumbnailsLight.podspec'`
2. pod install
2. Run your project 

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.kt`
  - Add `import com.reactnative.thumbnailslight.RNThumbnailsLightPackage;;` to the imports at the top of the file
  - Add `new RNThumbnailsLightPackage()` to the list returned by the `getPackages()` method
  
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-thumbnails-light'
    project(':react-native-thumbnails-light').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-thumbnails-light/android')
    ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
    implementation project(':react-native-thumbnails-light')
  	```

## Usage
```javascript

import React, { useEffect, useState } from 'react';
import { Image, View, Text } from 'react-native';
import { getThumbnailAsync } from 'react-native-thumbnails-light';

const VideoThumnails = ({ url }) => {
	const [image, setImage] = useState(null);

	useEffect(() => {
		if (!image) {
			getImageUrlFromVideo();
		}
	}, []);

	const getImageUrlFromVideo = async () => {
		await getThumbnailAsync(url, { time: 5000 })
            .then(res => {
                setImage(res.uri);
            })
            .catch(e => {
            	console.log(e);
            });
        setImage(uri);
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
