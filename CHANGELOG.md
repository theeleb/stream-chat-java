# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.0] - 2021-11-12

- Make StreamException constructors public
- Add ability to use SDK with multiple clients. Now, we can pass a custom client implementation to every
request object.
```java
    var client = new DefaultClient();
    var response = App.get().withClient(client).request();
```
- Add `App.verifyWebhookSignature`
- Add `User.create_token(String apiSecret, String userId, Date expiresAt, Date issuedAt)`

## [1.4.0] - 2021-11-03

- Add deleteMany for Channels
- Add deleteMany for Users
- Add TaskStatus to track async operations results

## [1.3.3] - 2021-11-01

- Add async url enrichment configuration

## [1.3.2] - 2021-10-27

- Fix channel type creation with commands

## [1.3.1] - 2021-10-18

- Support setting resources on channel types
- Add FAQ for common problems

## [1.3.0] - 2021-10-11

- Support for permissions v2
- Use edge by default
- Fix timeout setup and application into the requests
- Fix some nullability of some types (channel created_by_id, user id etc.)
- Fix NPE in some tests
- Improve CI to cancel unnecessary tasks

## [1.2.0] - 2021-07-09

- Add support for permission v2
- Fix token creation
- Make formatting a part of CI build

## [1.1.0] - 2021-06-25

- Add improved search support.
- Add javadoc publish into github pages.

## [1.0.1] - 2021-06-24

- Fix of release process. Grab this version over 1.0.0

## [1.0.0] - 2021-06-24

### Added

- Initial release of SDK with complete support of API.
