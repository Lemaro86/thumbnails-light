import { NativeModules } from 'react-native';

const { RNThumbnailsLight } = NativeModules;

class ThumbnailsLight {
    async getThumbnail(sourceFilename, time) {
        return await RNThumbnailsLight.getThumbnail(sourceFilename, time);
    }
}

export default ThumbnailsLight;
