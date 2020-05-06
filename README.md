# Hiromi Bot

Hiromi is a open source Discord Bot. [Discord](https://discordapp.com/) is a VoIP platform commonly used by gamers... W.I.P.

## Shared Hosting

Hiromi is hosted online free of charge. Feel free to [invite this bot](https://discordapp.com/api/oauth2/authorize?client_id=629044583258390538&permissions=1564470519&scope=bot) to your server and use it. The hosted version includes the music features. In case of high usage music features can be turned off temporarily.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

#### Java 11
Go to the [OpenJDK Archive](https://jdk.java.net/archive/) and look for Java version 11.0.6.

#### Discord Bot Token
- To get a Discord Bot token go to your [Developer Portal](https://discordapp.com/developers/applications).
- Click on ``New Application``, enter a name.
- Fill in the information on the ``General Information`` page.
- Go to the ``Bot`` page and click on ``Add Bot``.
- On this page you'll see the token. Later on you can copy the Discord Bot Token from this page.

#### Hiromi API Token
- WIP

### Installing

- Clone this repository ``git clone https://github.com/daanh432/HiromiBot.git``
- Enter the directory ``cd HiromiBot``
- Copy the ``settings.json.example`` to ``settings.json`` and fill in the values.
- Execute ``./gradlew run`` 

```
INFO   Default embed template has been set.
INFO   Login Successful!
INFO   Connected to WebSocket
INFO   Finished Loading!
INFO   BotName#1234 is ready
INFO   Bot has started on X guilds.
```
Do you get a similiar output to this above? Congratulations, you've succesfully set up your local development enviorment.

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [JDA](https://github.com/DV8FromTheWorld/JDA) - The Discord API Library Used
* [Gradle](https://gradle.org/) - Dependency Management
* [Unirest](https://github.com/Kong/unirest-java) - Used for fetching data from APIs

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/daanh432/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/daanh432/HiromiBot/tags). 

## Authors

* **daanh432** - *Initial work* - [daanh432](https://github.com/daanh432)

See also the list of [contributors](https://github.com/daanh432/HiromiBot/contributors) who participated in this project.

## License

This project is licensed under the AGPL-3.0 License - see the [LICENSE](LICENSE) file for details
