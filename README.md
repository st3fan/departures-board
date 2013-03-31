[![Build Status](https://travis-ci.org/st3fan/departures-board.png)](https://travis-ci.org/st3fan/departures-board)

Finding your way home with Clojure
==================================

Learning a new technology is much more fun when you actually have a
mission. So I decided to make a little web app that can show the
departure times of [streetcars in Toronto](http://en.wikipedia.org/wiki/Toronto_streetcar_system).

This is possible because the City of Toronto has made the [NextBus API](http://www1.toronto.ca/wps/portal/open_data/open_data_item_details?vgnextoid=4427790e6f21d210VgnVCM1000003dd60f89RCRD&vgnextchannel=6e886aa8cc819210VgnVCM10000067d60f89RCRD)
available as part of its [Open Data](http://www1.toronto.ca/wps/portal/open_data/open_data_item_details?vgnextoid=4427790e6f21d210VgnVCM1000003dd60f89RCRD&vgnextchannel=6e886aa8cc819210VgnVCM10000067d60f89RCRD) initiative, which provides info
about routes, stop locations and arrival time predictions.

More info at http://stefan.arentz.ca/finding-your-way-home-with-clojure.html

Running the App
===============

To run this app in development mode you will need to have [Leiningen 2.0](https://github.com/technomancy/leiningen) installed. Then you can just do:

```
$ cd departures-board
$ lein ring server-headless
```

The dashboard will run on [http://localhost:3000/index.html](http://localhost:3000/index.html)

You can also create a `war` file and then drop it in a servlet container like Tomcat or even JBoss.

```
$ cd departures-board
$ lein ring uberwar
$ cp target/departures-board-0.1.0-SNAPSHOT-standalone.war $DEPLOYMENT_DIR/
```

The app has just one file system dependency: it needs to be able to create a `~/.departures-board` directory where it will store route information.
