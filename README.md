# DCSA Event Core

The repository contains event functionality shared among different applications, i.e. the DCSA Event API functionality.
It is packaged as a jar, and uploaded to GitHub packages, to be downloaded via Maven

If you need any of the Services, Controllers or Repositories required by
this module, then you may have to explicitly define `basePackages` in the
`@EnableR2dbcRepositories` annotation.  For DCSA implementations, the
following definition should work:

```
@EnableR2dbcRepositories(basePackages = {"org.dcsa"}, repositoryBaseClass = ExtendedRepositoryImpl.class)
```

If you need more control over it, then all the repositories from the
DCSA Event Core are isolated in `org.dcsa.core.events.repositories`.
