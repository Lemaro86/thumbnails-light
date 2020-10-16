
Pod::Spec.new do |s|
  s.name         = "RNThumbnailsLight"
  s.version      = "1.0.0"
  s.summary      = "RNThumbnailsLight"
  s.description  = "just repo"
  s.homepage     = "https://github.com/Lemaro86/thumbnails-light"
  s.license      = "MIT"
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "8.0"
  s.source       = { :git => "git@github.com:Lemaro86/thumbnails-light.git", :tag => "#{s.version}" }
  s.source_files  = "RNThumbnailsLight*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

