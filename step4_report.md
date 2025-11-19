# Step 4 report
## Development Process
During this step, a lot of the app's design was updated and unified to bring in more consistent and thematically suiting UI.

Functionality-wise, the QR code generation and import was added to allow sharing created quests between devices to be used in events. The app reacts to intents sent by other scanners from the device and launches, automatically importing the new quest and providing the code to enter the quest with.

## Testing strategy
Our unit and UI tests use JUnit and fake classes that mimic the app functionality.
The tests were written with focus on code coverage and core functionality. Thus, the tests mainly cover Creating and Joining lobbies (quests), which involves communicating with (fake) repository, correct quest code verification, code auto-generation.

## Build process for APK
The signed APK was generated via Android Studio build wizard. All the standard steps of using the keystore, choosing the release build and exporting the APK were involved.

## Known bugs or limitations
For now, the application should be generally bug-free. The QR code import is a complex process which involves extrnal code scanning, reacting to the intent and interacting with automatic app launch and splash screen, so there may appear some inconsistencies.
Also, the creation of new question points in the new lobby is not that intuitive from the new user perspective (it is not indicated that you must press and hold on the map to create a question), but this functionality is described in `README.md` of this repository for the context of this course.
