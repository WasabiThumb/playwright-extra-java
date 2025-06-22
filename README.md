# playwright-extra-java
Modular plugin framework for [playwright-java](https://github.com/microsoft/playwright-java),
based on the [playwright-extra](https://github.com/berstend/puppeteer-extra/tree/master/packages/playwright-extra) 
library for [playwright](https://github.com/microsoft/playwright) (NodeJS).

## Quick Start
### Adding to your Build Script
#### Gradle (Kotlin)
```kotlin
dependencies {
    implementation("io.github.wasabithumb:playwright-extra:1.52.1.0")
}
```

#### Gradle (Groovy)
```groovy
dependencies {
    implementation 'io.github.wasabithumb:playwright-extra:1.52.1.0'
}
```

#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.github.wasabithumb</groupId>
        <artifactId>playwright-extra</artifactId>
        <version>1.52.1.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Initialization
```java
PlaywrightExtraOptions options = PlaywrightExtraOptions.builder()
        .plugins(
                ThisPlugin.thisPlugin(), 
                ThatPlugin.thatPlugin()
        )
        .build();

try (PlaywrightExtra playwright = PlaywrightExtra.create(options)) {
    // Identical to base Playwright
}
```

## Versioning
The versioning for this library closely follows the versioning of the base ``com.microsoft.playwright:playwright``
artifact, in the form ``<base major>.<base minor>.<extra minor>.<extra patch>``. As Microsoft releases patch releases
of ``playwright``, this library will also have a new patch release on best-effort basis.

## Plugin API
A list of plugin events may be found in the ``PluginDispatch`` class.
Proper documentation in progress.

## License
```text
Copyright 2025 Wasabi Codes

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
