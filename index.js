import { NativeModules } from 'react-native';

const { RNThumbnailsLight } = NativeModules;

export async function getThumbnailAsync(sourceFilename, time) {
    return await RNThumbnailsLight.getThumbnail(sourceFilename, time);
}
