![Build](https://github.com/MatteoArella/passit-frontend/workflows/Main/badge.svg?branch=master)

# PassIt! frontend
`PassIt!` is a platform for helping *tutors* to post ads and for *students* to search a tutor.

## Build
In order to build the frontend android app you have to firstly deploy the
[backend](https://github.com/MatteoArella/passit-backend).

After the backend has been deployed, the frontend will automatically import all required parameters
for using the existing backend resources (you must use the same AWS account both for backend and
frontend).

Build the app with:

```bash
./gradlew build
```
