
<h1 align="center">
  <br>
  <a href="https://github.com/Marten4n6/Email4n6"><img src="https://i.imgur.com/DPN9gmO.png" alt="Email4n6" width="320"></a>
  <br>
  Email4n6
  <br>
</h1>

<h4 align="center">A simple cross-platform forensic program for processing email files.</h4>

<p align="center">
  <a href="https://github.com/Marten4n6/Email4n6/blob/master/LICENSE.txt">
      <img src="https://img.shields.io/badge/license-GPLv3-blue.svg">
  </a>
  <a href="https://github.com/Marten4n6/Email4n6/issues">
    <img src="https://img.shields.io/github/issues/Marten4n6/Email4n6.svg">
  </a>
  <a href="https://github.com/Marten4n6/Email4n6">
      <img src="https://img.shields.io/badge/contributions-welcome-orange.svg">
  </a>
</p>

---

## Features

- High performance indexing and searching via [Lucene](https://lucene.apache.org/core/#lucenetm-features)
- Simple tagging and bookmark system
- Quickly create reports
- View the body, headers and attachments of messages
- Portable
  * Email4n6 lives in a single directory which can easily be moved.
- Cross platform
  * Linux, macOS and Windows ready.

##

Currently only PST and OST (untested) files are supported, once these
formats are well tested support for files such as EML should follow.

## How To Use

Email4n6 requires [Java 8](https://java.com/en/download/) to be installed. <br/>
If you're running on Linux and are using OpenJDK, please make sure to also install OpenJFX.

#### Download

You can [download](https://github.com/Marten4n6/Email4n6/releases) the latest version of Email4n6 for Linux, macOS and Windows.

#### From Source

To compile this application from source, you'll need [Git](https://git-scm.com/) and [Maven](https://maven.apache.org/) installed on your computer.

```bash
# Clone or download this repository
$ git clone https://github.com/Marten4n6/Email4n6

# Go into the repository
$ cd Email4n6

# Compile with dependencies
$ mvn clean compile assembly:single

# Lastly, run Email4n6
$ java -jar target/Email4n6-*.jar
```

## Screenshots
![](https://i.imgur.com/EiAesJY.png)

## Motivation
This project was created because I was unable to find any good open-source alternatives to commercial software, such as
[Forensic Explorer](http://www.forensicexplorer.com/) and [Intella](https://www.vound-software.com/) for working with email files. Although these products offer a lot more than just working with email files, this project can hopefully be used to replace the email functionality.

## Versioning

Email4n6 will be maintained under the Semantic Versioning guidelines as much as possible. <br/>
Releases will be numbered with the follow format:
```
<major>.<minor>.<patch>
```

And constructed with the following guidelines:
- Breaking backward compatibility bumps the major
- New additions without breaking backward compatibility bumps the minor
- Bug fixes and misc changes bump the patch

For more information on SemVer, please visit https://semver.org/.

## Issues

Feel free to submit any issues or feature requests.

## Credits

This project would not be possible without the following awesome open-source projects:
- [java-libpst](https://github.com/rjohnsondev/java-libpst), huge thanks to [Richard Johnson](https://github.com/rjohnsondev)
- Indexing and searching via [Apache Lucene](https://lucene.apache.org/)
- HTML report templates are rendered via [FreeMarker](https://freemarker.apache.org/)
- Icons created by [IconMonstr](https://iconmonstr.com/)
- Logo created by [motusora](https://www.behance.net/motusora)

## License

[GPLv3](https://github.com/Marten4n6/Email4n6/blob/master/LICENSE.txt)
