# Serenity Framework Template

It's a [Serenity framework](http://thucydides.info/docs/serenity-staging/) with some additional utility classes.

##Additions

- [Drag and drop]()
- [Utility methods for selenium driver]()
- [Tool to work with emails messages: Get, Search, Read]()
- [Tool to work with Local storage in browser]()
- [Some randomizer to generate random values]()


##Customization
Framework contains some examples of customizing some parts:
    
1. Implementation of Event Listener and custom driver can be found here:
    - CustomDriverFactory
    - CustomEventListener
    > (In case if you need to listen and process some actions before/after every selenium action)
        
2. Implementation your own elements in serenity can be found here:
    - `com.project.qa.controls`
3. Integration with TeamCity:
    - `com.project.qa.teamcity` (Classes to interact with TeamCity)
    - `com.project.qa.teamcity.suites` (Test suites to run)
        
##Installing

1. To get it work need to install [Maven](https://maven.apache.org/)
2. For UI test need to install driver you need (f.e. [ChromeDriver](http://chromedriver.chromium.org/))
3. After driver and maven are installed you need to run command `mvn clean verify` it will download all dependencies and run all test from the test package.
