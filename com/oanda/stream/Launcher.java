package com.oanda.stream;

public class Launcher {

    public static void main(String[] args) {

        // Set these variables to whatever personal ones are preferred
        String access_token = "";
        String account_id = "";

        JavaApiStreaming stream = new JavaApiStreaming(account_id, access_token);
        stream.start();

        stream.addPriceListener(new ExamplePriceListener());
        // add more reader of data stream.addPriceListener(new CustomPriceListener)

    }

    static class ExamplePriceListener implements IPriceListener {

        public void tick(String instrument, String time, double bid, double ask) {
            System.out.println("-------");
            System.out.println(instrument);
            System.out.println(time);
            System.out.println(bid);
            System.out.println(ask);
        }

    }

}
