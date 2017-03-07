# Changelog

## 2.1.3-SNAPSHOT - 2017-02-22

### Changed
- new exception `BugzillaAPIException`
- all methods raise the new exception instead of an exception `Exception`
- `OldBzChecker` is joined with `BzChecker`
- new method `BzChecker.getInstance (injector)` is available
