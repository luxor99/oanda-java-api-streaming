package com.oanda.stream;

public interface IPriceListener {

	void tick(String instrument, String time, double bid, double ask);
	
}
