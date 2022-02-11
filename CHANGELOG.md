# Changelog

## 2.2.1-SNAPSHOT - 2022-01-12

### Changed
- removed BasicCredentials from REST_API implementation
- added Header Authorization in replace of URL based api argument
- apikey was removed from url for GET method
- upgraded all libraries to the newest versions

## 2.1.3-SNAPSHOT - 2017-02-22

### Changed
- new exception `BugzillaAPIException`
- all methods raise the new exception instead of an exception `Exception`
- `OldBzChecker` is joined with `BzChecker`
- new method `BzChecker.getInstance (injector)` is available
- static factory `getInstance` decides which implementation of Bugzilla API will be used
  - if the property `bugzilla.url` contains of a string `rest` it uses REST API automatically
- a string "/rest" is not appended at the end of bugzilla url in the code anymore.
- a static factory `BzChecker.getInstance` throws `RuntimeException` now
- new property `bugzilla.apikey` has been added. You can use key to authorize 
  an access to REST API of bugzilla

### Added
- new static factory `getInstance(injector)`

### Fixed
- a field `summary` is already available
- REST api removes trailing spaces from all bugzilla related properties
