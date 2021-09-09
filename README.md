# LocalStream

LocalStream is a streaming service written in Java and Kotlin. You can play music from a server (later you can also
watch video, but this features is not implemented yet). You need to set up a server yourself (or use a server of another
person). Currently, there is only a client for desktop PCs. A app for Android (and maybe iOS) is planned.

## TODO

- [ ] implement correct title play
    - [ ] implement a jump feature
    - [ ] fix the pause/resume feature
- [ ] fix upload so that there are no weird sounds in transferred files
- [ ] implement video play
- [ ] activity system
  - [ ] add timestamp
  - [ ] replace enum constant names with real names
- [ ] implement title administration system
  - [ ] implement title remove feature outside removing content from the db
  - [ ] implement a feature to edit titles
- [ ] implement an user system to manage access

## Important notes

- **This project is still in development and there are no releases yet. However, you can compile the client yourself.**
- The GUI is only available in **German**. There will be support for other languages (at least English).
- Current existing Java code will (may) be replaced with Kotlin.