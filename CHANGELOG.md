# Changelog

## 0.0.2

* Fixed "Invalid response from native code" error in makeCall method
* Added comprehensive debug logging throughout the plugin
* Improved native code response structure to match Flutter expectations
* Enhanced error handling and debugging capabilities
* Added detailed logging for method channel communication
* Added event channel debugging for better troubleshooting
* Improved TeleCall.fromMap method with better error handling
* Added debug logging for call state changes and event processing
* Fixed Map type conversion issues between native and Flutter code
* Improved event handling to properly convert Map<Object?, Object?> to Map<String, dynamic>

## 0.0.1

* Initial release of flutter_tele library
* Added TeleEndpoint class for telephony operations
* Added TeleCall class for call representation
* Implemented Android InCallService integration
* Added support for making outgoing calls
* Added support for answering incoming calls
* Added support for hanging up calls
* Added support for declining calls
* Added support for holding/unholding calls
* Added support for muting/unmuting calls
* Added support for speaker/earpiece switching
* Added event-driven architecture for call events
* Added support for multiple SIM cards
* Added comprehensive example application
* Added proper Android permissions and manifest configuration
