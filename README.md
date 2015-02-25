java-api-streaming-enhanced
==================

This is a fork of the Oanda streaming rates api project, but with many enhancements with the aim of making it a complete, stable, supervised data feed client. This example uses [apache httpcomponents](http://hc.apache.org/httpcomponents-client-ga/) for https
connections, [json-simple](https://code.google.com/p/json-simple/) for json decoding and [Apache Commons Email](http://commons.apache.org/proper/commons-email/) to email administrators in case of issues.

### Features

1) Class runs in its own thread.

2) Implements a listener interface so you can share data with other classes.

3) Updated code to latest Apache httpcomponents specs.

4) Automatic reconnection in case of IOException.

5) Heartbeats from the server are be monitored. The client application terminates the connection and re-connects its corresponding stream in the event that no data has been received (no ticks, no heartbeats) from the rates stream for more than 10 seconds.

6) If the re-connection attempt receives an HTTP 429 error, an exponential backoff is initiated on reconnects. It will back off for 1 second before initiating the next re-connection attempt and then double the backoff interval until the connection is successfully established.

7) If the connection goes down and the client cannot connect or reconnect after x number of tries, it sends an email or text to the administrator.


### Setup

Clone this repo to the location of your choice.

Modify the following variables in Launcher.java and JavaApiStreaming.java

    domain
    access_token
    account_id
    instruments

Load it into your favorite IDE and compile.  Create a jar file and run:

java -jar OandaStreamingAPI.jar


### Sample Output

    EUR_USD
    2014-03-21T17:56:09.932922Z
    1.37912
    1.37923
    -------
    USD_CAD
    2014-03-21T17:56:20.776248Z
    1.12011
    1.12029
    -------
    USD_JPY
    2014-03-21T17:56:13.668154Z
    102.262
    102.275

### More Information

http://developer.oanda.com/docs/v1/stream/#rates-streaming
