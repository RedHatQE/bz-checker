# Changelog

## 2.1.3-SNAPSHOT - 2017-02-22

### Changed
- new exception `BugzillaAPIException`
- all methods raise the new exception instead of an exception `Exception`
- `OldBzChecker` is joined with `BzChecker`
- new method `BzChecker.getInstance (injector)` is available
- static factory `getInstance` decides which implementation of Bugzilla API will be used
- a string "/rest" is not appended at the end of bugzilla url in the code anymore.
- a static factory `BzChecker.getInstance` throws `RuntimeException` now

### Added
- new static factory `getInstance(injector)`

### Fixed
- a field `summary` is already available
- REST api removes trailing spaces from all bugzilla related properties
