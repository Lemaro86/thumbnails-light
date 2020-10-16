#import <AVFoundation/AVFoundation.h>
#import <AVFoundation/AVAsset.h>
#import <UIKit/UIKit.h>
#import "RNThumbnailsLight.h"

@implementation RNThumbnailsLight

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

static NSString* const OPTIONS_KEY_QUALITY = @"quality";
static NSString* const OPTIONS_KEY_TIME = @"time";
static NSString* const OPTIONS_KEY_HEADERS = @"headers";

- (BOOL)ensureDirExistsWithPath:(NSString *)path {
  BOOL isDir = NO;
  NSError *error;
  BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDir];
  if (!(exists && isDir)) {
      [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
      if (error) {
          return NO;
      }
  }
  return YES;
}

- (NSString *)generatePathInDirectory:(NSString *)directory withExtension:(NSString *)extension {
  NSString *fileName = [[[NSUUID UUID] UUIDString] stringByAppendingString:extension];
  [self ensureDirExistsWithPath:directory];
  return [directory stringByAppendingPathComponent:fileName];
}

- (NSString *)cacheDirectoryPath {
  NSArray *array = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
  return array[0];
}

RCT_REMAP_METHOD(getThumbnail,
                 sourceFilename:(NSString *)source
                 options:(NSDictionary *)options
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
  NSURL *url = [NSURL URLWithString:source];
//  NSLog(@"source-------%@", source);
//    check it late
//  if ([url isFileURL]) {
//     if (!_fileSystem) {
//       return reject(@"E_MISSING_MODULE", @"No FileSystem module.", nil);
//     }
//     if (!([_fileSystem permissionsForURI:url] & UMFileSystemPermissionRead)) {
//       return reject(@"E_FILESYSTEM_PERMISSIONS", [NSString stringWithFormat:@"File '%@' isn't readable.", source], nil);
//     }
//    return reject(@"error type of file", @"There where no file", nil);
//  }

  long timeInMs = [(NSNumber *)options[OPTIONS_KEY_TIME] integerValue] ?: 0;
  float quality = [(NSNumber *)options[OPTIONS_KEY_QUALITY] floatValue] ?: 1.0;
  NSDictionary *headers = options[OPTIONS_KEY_HEADERS] ?: @{};

  AVURLAsset *asset = [[AVURLAsset alloc] initWithURL:url options:@{@"AVURLAssetHTTPHeaderFieldsKey": headers}];
  AVAssetImageGenerator *generator = [[AVAssetImageGenerator alloc] initWithAsset:asset];
  generator.appliesPreferredTrackTransform = YES;


  NSError *err = NULL;
  CMTime time = CMTimeMake(timeInMs, 1000);

  CGImageRef imgRef = [generator copyCGImageAtTime:time actualTime:NULL error:&err];
  if (err) {
    return reject(@"E_THUM_FAIL", err.localizedFailureReason, err);
  }
  UIImage *thumbnail = [UIImage imageWithCGImage:imgRef];



  NSString *newPath = [self generatePathInDirectory:[[self cacheDirectoryPath] stringByAppendingPathComponent:@"VideoThumbnails"]
                            withExtension:@".jpg"];
//   [_fileSystem ensureDirExistsWithPath:directory];

//   NSString *fileName = [[[NSUUID UUID] UUIDString] stringByAppendingString:@".jpg"];
//   NSString *newPath = [directory stringByAppendingPathComponent:fileName];
  NSData *data = UIImageJPEGRepresentation(thumbnail, quality);
  if (![data writeToFile:newPath atomically:YES]) {
    return reject(@"E_WRITE_ERROR", @"Can't write to file.", nil);
  }
  NSURL *fileURL = [NSURL fileURLWithPath:newPath];
  NSString *filePath = [fileURL absoluteString];

  resolve(@{
            @"uri" : filePath,
            @"width" : @(thumbnail.size.width),
            @"height" : @(thumbnail.size.height),
            });
}

@end
