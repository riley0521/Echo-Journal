# Echo Journal

An android application for making journal with your voice.

## Tech Stack

- [Jetpack Compose](https://developer.android.com/develop/ui/compose/setup) - UI Framework
    - Material3
    - Navigation
    - ViewModel compose
- [Splash Screen API](https://developer.android.com/develop/ui/views/launch/splash-screen#getting-started) - Splash screen that supports Android 12 and above.
- [Koin](https://insert-koin.io/docs/setup/koin) - Dependency Injection.
- [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html#example-json-serialization) - For converting JSON to data class and vice-versa.
- [Room](https://developer.android.com/training/data-storage/room) - SQLite wrapper with better compilation and coroutines support.
- [Datastore Preference](https://developer.android.com/topic/libraries/architecture/datastore) - To store user preference.
- [Jetpack Glance](https://developer.android.com/develop/ui/compose/glance/setup) - To create widget with jetpack compose.


## Features

### ![Moods](https://github.com/riley0521/Echo-Journal/blob/master/screenshots/Moods.PNG) Moods

### List of journal
- Display all audio log entry with their mood, sorted by date.
- Can be filtered by selected mood & topic.

### Create journal
- Add entry title, description, and mood for that entry.
- Append existing topic/s or create new topic/s.


## License
```text
Copyright 2025 Riley Farro

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```