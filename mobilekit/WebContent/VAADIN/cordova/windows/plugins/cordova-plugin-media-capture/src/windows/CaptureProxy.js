cordova.define("cordova-plugin-media-capture.CaptureProxy",function(e,t,n){function i(){function e(){var e=Array.prototype.slice.call(arguments);e.forEach(function(e){var t=document.getElementById(e);if(t){var n=t.style.display;t.style.display=n==="none"?"block":"none"}})}function t(){var e="margin: 7px; border: 2.5px solid white; width: 45%; height: 35px; color: white; background-color: black;";o=document.createElement("div"),o.style.cssText="background-position: 50% 50%; background-repeat: no-repeat; background-size: contain; background-color: black; left: 0px; top: 0px; width: 100%; height: 100%; position: fixed; z-index: 9999",o.innerHTML='<video id="capturePreview" style="width: 100%; height: 100%"></video><div id="previewButtons" style="width: 100%; bottom: 0px; display: flex; position: absolute; justify-content: space-around; background-color: black;"><button id="takePicture" style="'+e+'">Capture</button>'+'<button id="cancelCapture" style="'+e+'">Cancel</button>'+'<button id="selectPicture" style="display: none; '+e+'">Accept</button>'+'<button id="retakePicture" style="display: none; '+e+'">Retake</button>'+"</div>",document.body.appendChild(o),u=document.getElementById("capturePreview"),d=document.getElementById("cancelCapture"),s=new m.MediaCapture,l=new m.MediaCaptureInitializationSettings,l.streamingCaptureMode=m.StreamingCaptureMode.audioAndVideo}function n(e,t,n,r){var c=Windows.Devices.Enumeration.Panel.back;Windows.Devices.Enumeration.DeviceInformation.findAllAsync(Windows.Devices.Enumeration.DeviceClass.videoCapture).done(function(d){d.length>0?(d.forEach(function(e){e.enclosureLocation&&e.enclosureLocation.panel&&e.enclosureLocation.panel==c&&(l.videoDeviceId=e.id)}),s.initializeAsync(l).done(function(){s.setPreviewRotation(Windows.Media.Capture.VideoRotation.clockwise90Degrees),u.msZoom=!0,u.src=URL.createObjectURL(s),u.play(),o.style.display="block",u.onclick=e,document.getElementById("takePicture").onclick=e,document.getElementById("cancelCapture").onclick=function(){t(a.CAPTURE_NO_MEDIA_FILES)},document.getElementById("selectPicture").onclick=n,document.getElementById("retakePicture").onclick=r},function(e){i(),t(a.CAPTURE_INTERNAL_ERR,e)})):(i(),t(a.CAPTURE_INTERNAL_ERR))})}function i(){u.pause(),u.src=null,o&&document.body.removeChild(o),s&&(s.stopRecordAsync(),s=null)}var o,r,c,u=null,d=null,l=null,p=!1,s=null,m=Windows.Media.Capture;return{captureVideo:function(o,r){try{t(),n(function(){if(p)s.stopRecordAsync().done(function(){i(),o(c)});else{e("cancelCapture"),document.getElementById("takePicture").text="Stop";var t=Windows.Media.MediaProperties.MediaEncodingProfile.createMp4(Windows.Media.MediaProperties.VideoEncodingQuality.auto),n=Windows.Storage.CreationCollisionOption.generateUniqueName,u=Windows.Storage.ApplicationData.current.localFolder;u.createFileAsync("cameraCaptureVideo.mp4",n).done(function(e){s.startRecordToStorageFileAsync(t,e).done(function(){c=e,p=!0},function(e){i(),r(a.CAPTURE_INTERNAL_ERR,e)})},function(e){i(),r(a.CAPTURE_INTERNAL_ERR,e)})}},r)}catch(u){i(),r(a.CAPTURE_INTERNAL_ERR,u)}},capturePhoto:function(c,u){try{t(),n(function(){var t=Windows.Media.MediaProperties.ImageEncodingProperties.createJpeg(),n=Windows.Storage.CreationCollisionOption.replaceExisting,c=Windows.Storage.ApplicationData.current.temporaryFolder;c.createFileAsync("cameraCaptureImage.jpg",n).done(function(n){s.capturePhotoToStorageFileAsync(t,n).done(function(){r=n,o.style.backgroundImage='url("ms-appdata:///temp/'+n.name+'")',e("capturePreview","takePicture","cancelCapture","selectPicture","retakePicture")},function(e){i(),u(a.CAPTURE_INTERNAL_ERR,e)})},function(e){i(),u(a.CAPTURE_INTERNAL_ERR,e)})},function(e){i(),u(e)},function(){var e=Windows.Storage.CreationCollisionOption.generateUniqueName,t=Windows.Storage.ApplicationData.current.localFolder;r.copyAsync(t,r.name,e).done(function(e){i(),c(e)},function(e){i(),u(e)})},function(){e("capturePreview","takePicture","cancelCapture","selectPicture","retakePicture")})}catch(d){i(),u(a.CAPTURE_INTERNAL_ERR,d)}}}}var o=e("cordova-plugin-media-capture.MediaFile"),a=e("cordova-plugin-media-capture.CaptureError"),r=e("cordova-plugin-media-capture.CaptureAudioOptions"),c=e("cordova-plugin-media-capture.CaptureVideoOptions"),u=e("cordova-plugin-media-capture.MediaFileData");n.exports={captureAudio:function(e,t,n){var i=n[0],c=new r;if(i.duration===void 0)c.duration=3600;else{if(0>=i.duration)return t(new a(a.CAPTURE_INVALID_ARGUMENT)),void 0;c.duration=i.duration}var u=Windows.Media.Capture,d=Windows.Media.MediaProperties,l=Windows.Storage.ApplicationData.current.localFolder,p=Windows.Storage.NameCollisionOption.generateUniqueName,s=new u.MediaCapture,m=new u.MediaCaptureInitializationSettings,A=new d.MediaEncodingProfile.createMp3(d.AudioEncodingQuality.auto),E=new d.MediaEncodingProfile.createM4a(d.AudioEncodingQuality.auto);m.streamingCaptureMode=u.StreamingCaptureMode.audio;var f,C,g=function(){s.stopRecordAsync().then(function(){f.getBasicPropertiesAsync().then(function(t){var n=new o(f.name,"ms-appdata:///local/"+f.name,f.contentType,t.dateModified,t.size);n.fullPath=f.path,e([n])},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})};s.initializeAsync(m).done(function(){l.createFileAsync("captureAudio.mp3",p).then(function(e){f=e,s.startRecordToStorageFileAsync(A,f).then(function(){C=setTimeout(g,c.duration*1e3)},function(e){return e.number!==-0xNaN?(t(new a(a.CAPTURE_INTERNAL_ERR)),void 0):(clearTimeout(C),l.createFileAsync("captureAudio.m4a",p).then(function(e){f=e,s.startRecordToStorageFileAsync(E,f).then(function(){C=setTimeout(g,c.duration*1e3)},function(){t(new a(a.CAPTURE_INTERNAL_ERR))})}),void 0)})},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})})},captureImage:function(e,t){function n(e,n){var i=new a(e);i.message=n,t(i)}var r=Windows.Media.Capture;if(r.CameraCaptureUI){var c=new Windows.Media.Capture.CameraCaptureUI;c.photoSettings.allowCropping=!0,c.photoSettings.maxResolution=Windows.Media.Capture.CameraCaptureUIMaxPhotoResolution.highestAvailable,c.photoSettings.format=Windows.Media.Capture.CameraCaptureUIPhotoFormat.jpeg,c.captureFileAsync(Windows.Media.Capture.CameraCaptureUIMode.photo).done(function(n){n?n.moveAsync(Windows.Storage.ApplicationData.current.localFolder,"cameraCaptureImage.jpg",Windows.Storage.NameCollisionOption.generateUniqueName).then(function(){n.getBasicPropertiesAsync().then(function(t){var i=new o(n.name,"ms-appdata:///local/"+n.name,n.contentType,t.dateModified,t.size);i.fullPath=n.path,e([i])},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))}):t(new a(a.CAPTURE_NO_MEDIA_FILES))},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})}else{var u=new i;u.capturePhoto(function(t){t.getBasicPropertiesAsync().done(function(n){var i=new o(t.name,"ms-appdata:///local/"+t.name,t.contentType,n.dateModified,n.size);i.fullPath=t.path,e([i])},function(e){n(a.CAPTURE_INTERNAL_ERR,e)})},function(e){n(e)})}},captureVideo:function(e,t,n){function r(e,n){var i=new a(e);i.message=n,t(i)}var u=n[0],d=Windows.Media.Capture;if(d.CameraCaptureUI){var l=new c;u.duration&&u.duration>0&&(l.duration=u.duration),u.limit>1&&(l.limit=u.limit);var p=new Windows.Media.Capture.CameraCaptureUI;p.videoSettings.allowTrimming=!0,p.videoSettings.format=Windows.Media.Capture.CameraCaptureUIVideoFormat.mp4,p.videoSettings.maxDurationInSeconds=l.duration,p.captureFileAsync(Windows.Media.Capture.CameraCaptureUIMode.video).then(function(n){n?n.moveAsync(Windows.Storage.ApplicationData.current.localFolder,"cameraCaptureVideo.mp4",Windows.Storage.NameCollisionOption.generateUniqueName).then(function(){n.getBasicPropertiesAsync().then(function(t){var i=new o(n.name,"ms-appdata:///local/"+n.name,n.contentType,t.dateModified,t.size);i.fullPath=n.path,e([i])},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))}):t(new a(a.CAPTURE_NO_MEDIA_FILES))},function(){t(new a(a.CAPTURE_NO_MEDIA_FILES))})}else{var s=new i;s.captureVideo(function(t){t.getBasicPropertiesAsync().done(function(n){var i=new o(t.name,"ms-appdata:///local/"+t.name,t.contentType,n.dateModified,n.size);i.fullPath=t.path,e([i])},function(e){r(a.CAPTURE_INTERNAL_ERR,e)})},r)}},getFormatData:function(e,t,n){Windows.Storage.StorageFile.getFileFromPathAsync(n[0]).then(function(n){var i=(n.contentType+"").split("/")[0].toLowerCase();i==="audio"?n.properties.getMusicPropertiesAsync().then(function(t){e(new u(null,t.bitrate,0,0,t.duration/1e3))},function(){t(new a(a.CAPTURE_INVALID_ARGUMENT))}):i==="video"?n.properties.getVideoPropertiesAsync().then(function(t){e(new u(null,t.bitrate,t.height,t.width,t.duration/1e3))},function(){t(new a(a.CAPTURE_INVALID_ARGUMENT))}):i==="image"?n.properties.getImagePropertiesAsync().then(function(t){e(new u(null,0,t.height,t.width,0))},function(){t(new a(a.CAPTURE_INVALID_ARGUMENT))}):t(new a(a.CAPTURE_INVALID_ARGUMENT))},function(){t(new a(a.CAPTURE_INVALID_ARGUMENT))})}},e("cordova/exec/proxy").add("Capture",n.exports)})