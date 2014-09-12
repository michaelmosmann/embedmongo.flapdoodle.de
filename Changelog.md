### Changelog

#### 1.46.1 (SNAPSHOT)

- support for OSGi (thanks to Niels Betram)
- support for FreeBSD (thanks to Alex Sanchez)
- updated gradle build
- switch to enable textsearch in mongodb before 2.6

#### 1.46.0

- changed to semantic versions
- custom database dir supported
- upgrade mongo versions
- changed some log levels, fixed some error logs
- proxy configuration added

#### 1.45

- embed.process upgrade (better error logging)

#### 1.44

- mongo version upgrade (2.6.0, ...)
- mongo import command support

#### 1.43

#### 1.42

- massive performance improvment (removed option -v (can be enabled by config))
- mongo version upgrade (2.5.4, 2.4.9, 2.2.7)

#### 1.41

- mongo env command line bug fix (now working)
- mongo version upgrade (2.5.4, 2.4.8, 2.2.6)

#### 1.40

- mongo env command line bug fix

#### 1.39

- embed process dep update

#### 1.38

- embed process dep update
- bug fixes
- update examples

#### 1.37

- new mongo version supported

#### 1.36

- dependencies updated 

#### 1.35

- syncDelay set to 0 (no sync)
- process listener added (can copy db files after test into custom directory)

#### 1.34

- added builder for mongod and mongos config
- dependencies updated

#### 1.33 (not public)

#### 1.32

- added 2.5.0 as new development version
- added example for custom download path

#### 1.31

- minor bugfixes

#### 1.30

- deprecate old versions (left current production(2.4.1) and previous version left(2.2.3))
- coming versions will change use the 2.4 branch of mongodb as production version
- no release time check for deprecated versions, so use with care

#### 1.29

- major api changes, speed improvement, easier configuration, need to update documentation

#### 1.28

- mongod config refactor
- configurable startup timeout
- added windows2008 support

#### 1.27

- dep version change

#### 1.26

- bind_ip configuration parameter support (MongodConfig constructor)

#### 1.25

- dep version change

#### 1.24

- dep version change

#### 1.23

- added 2.0.7, 2.2.0
- mongodb java driver update to 2.9.0 (test dependency)

#### 1.22

- maven dep version range does not work as expected -> disabled

#### 1.21

- dependency version range for de.flapdoodle.embed.process

#### 1.20

- NPE fix with custom database directory
- custom database directory will not be deleted

#### 1.19

- **massive refactoring, some api breaks**
- **project split**
- some relevant process.stop() improvements

#### 1.18

- added some unit test support (thanx to trajano)
- added some logging only runtime config option
- added 2.0.7-rc1, 2.2.0-rc0
- command line post processor hock

#### 1.17

- added version 2.0.6 and 2.1.2
- version refactoring
- you can now have a custom version, so you do not depend on a new release of this project

#### 1.16

- added version 2.0.5 (main version 2.0 now points to it)
- changed http user agent
- customizeable mongod process output 
- better loopback device detection for mongod process shutdown via command

#### 1.15

- now we send ctrl+c on linux and osx, then send shutdown to server, then taskkill on windows (may the force be with us)
- disable journal for faster turnaround times
- noauth added
- customize artifact storage path
- detection if localhost is not loopback (command shutdown on mongod does not work for remote access)
- formated process output
- much better windows support

#### 1.14

- changed back to send ctrl+c and then send shutdown

#### 1.13

- mongod process management improvement 
 (windows mongod shutdown improvement (alpha) (some trouble stopping process on windows - http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092))
 - send shutdown to server
 - send ctrl+c to mongod will perform clean shutdown (untested on windows using taskkill)
- now with 2.1.1

#### 1.12

- NUMA support (alpha) - http://www.mongodb.org/display/DOCS/NUMA

#### 1.11

- timeout fix on slow systems
- stability on win plattforms (hopefully)

#### 1.10

- race condition and cleanup of mongod process

#### 1.9

- fixed 64Bit detection - amd64
- now with main versions 1.6, 1.8, 2.0, 2.1
