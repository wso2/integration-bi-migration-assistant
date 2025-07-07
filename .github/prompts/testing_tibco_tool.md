---
description: Testing TIBCO tool
globs:
alwaysApply: false
---
Ask for source directory if not provided. If target is not provided use `test` as the default

First you need to build and use the tool. For this follow these steps
1. Run `./gradlew clean tibcoReleaseLocal -x checkStyleMain -x test`.
2. Remove existing version of tool `bal tool remove migrate-tibco`.
3. Pull the new version of the tool `bal tool pull migrate-tibco --repository=local`


Then you can run the tool using `bal migrate-tibco $source -o $target`
