

# Mobile Integration

When integrating the embedded experience in mobile applications the current method is via **webviews** as follows:

<Image align="center" src="https://files.readme.io/77aa760cd89ba538f7707bc3b731ddc26f615f169ae279c547f6bc7c3d508073-Screenshot_2025-03-13_at_18.13.34.png" />

## Required permission

In order for the Embedded Experience to fully work on mobile devices, the webview must include the following permissions:

* **Camera access**: this will allow user to capture pictures of the required documents
* **Files access**: This will allow users to upload files already on the device
* **Location**: We used as an added safeguard for KYC validation purposes.