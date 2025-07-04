# Changelog

## 0.0.5

* Fixed hangup call functionality for outgoing calls
* Added proper call mapping system between TeleCall and Android Call objects
* Enhanced call management to handle both incoming and outgoing calls correctly
* Fixed call state synchronization between Flutter and native Android calls
* Improved call lifecycle management with proper cleanup
* Added better error handling and logging for call operations
* Fixed call ID management to prevent conflicts between multiple calls
* Enhanced call control methods (answer, decline, hold, unhold) to work with call mapping
* Added support for proper call termination handling

## 0.0.4

* Fixed Kotlin type casting issue in TeleService.kt
* Added explicit type casting for Map<String, Any> in sendEvent calls
* Fixed compilation error on line 187 of TeleService.kt
* Ensured proper type compatibility between TeleCall.toMap() and sendEvent method

## 0.0.3

* **BREAKING:** Fixed makeCall to actually make real phone calls instead of just simulating
* Added real Intent.ACTION_CALL implementation for outgoing calls
* Added proper SIM slot selection for dual-SIM devices
* Added phone account handle selection for specific SIM cards
* Enhanced call state management with real Android Call objects
* Added proper incoming call handling with call details extraction
* Added call error event handling for failed call attempts
* Improved call state mapping to match Android Call states
* Added support for call direction detection (incoming/outgoing)
* Enhanced call termination handling with proper cleanup
* Added better error handling and logging for call operations
* Fixed call ID management to prevent conflicts
* Added proper call lifecycle management (added, changed, removed)

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
* Added runtime permission checking and requesting for phone permissions
* Enhanced start method to verify permissions before initializing telephony service
* **Example app:** Now checks and requests permissions at runtime, shows permission status, and handles event map types robustly

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
