# location-remover

A web app that removes metadata, such as the location, from a picture.

Pick a JPEG file, and the app draws it on a canvas and encodes it again. The new
file holds the same pixels, so the Exif block of the original — and the location
inside it — is gone.

The picture never leaves your device. All the work happens in the browser.

## Link

<https://yuyuyuyuyu-dev.github.io/location-remover/>

## How it is built

This is a [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
app written in Kotlin.

- [/shared](./shared/src) holds the UI and the logic.
  - [commonMain](./shared/src/commonMain/kotlin) is the screen, the view model,
    and the interfaces for the work that only a browser can do.
  - [webMain](./shared/src/webMain/kotlin) implements those interfaces with
    browser APIs: the file dialog, the canvas, the download link, and the Web
    Share API.
- [/webApp](./webApp/src) is the web app itself. The
  [ComposePWA](https://github.com/yuyuyuyuyu-dev/ComposePWA) plugin turns its
  build output into a PWA.

**Only the web target works.** The `androidApp` and `desktopApp` modules and the
`android` and `jvm` targets are still there, and they compile, but every piece
of image handling on those targets is a `TODO()`. They stop as soon as they are
asked to do anything.

## Running the app

You need [Node.js](https://nodejs.org/), because the ComposePWA plugin calls
`workbox-cli` through `npx` to write the service worker.

Development server:

```bash
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

Production build (the PWA that is deployed):

```bash
./gradlew :webApp:wasmJsBrowserDistribution
```

The result is written to `webApp/build/dist/wasmJs/productionExecutable`.

## Running the tests

```bash
./gradlew :shared:jvmTest
```

## Deploying

Pushing to `main` runs the CI workflow, and a green run triggers
[the deploy workflow](./.github/workflows/deploy.yml), which publishes the
production build to GitHub Pages.
