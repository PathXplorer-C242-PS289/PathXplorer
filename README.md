# Getting Started

```bash
git clone https://github.com/PathXplorer-C242-PS289/PathXplorer.git
```

open the project in your favorite IDE

## Setup Firebase Authentication

first, you need to connect project to firebase like below:

1. open the project in android studio
2. go to tools -> firebase
3. click on authentication -> Authenticate using Google
4. click on connect to firebase
5. follow the steps to connect the project to firebase
6. then when you are done, Add firebase Authentication, it will add some dependencies to build.gradle project level, and app level
7. then you need to add SHA1 and SHA256 keys to firebase project settings
8. to get the SHA1 and SHA256 keys, follow the steps below:
open the project in android studio
click on gradle on the right side of the IDE
click on the project -> Tasks -> android -> signingReport

or you can run the following command in the terminal

```bash
./gradlew signingReport

or 

gradle signingReport
```

9. when you have added the SHA1 and SHA256 keys to firebase project settings, you need to download the google-services.json file and add to the project /app folder.

add FIREBASE_CLIENT_ID on `local.rpoperties` file

```local.properties
FIREBASE_CLIENT_ID="YOUR_WEB_CLIENT_ID"
```

to get the web client id, follow the steps below:
- open your firebase project
- go to authentication -> sign-in method -> google
- enable google sign-in method
- click on web SDK configuration
- copy the web client id