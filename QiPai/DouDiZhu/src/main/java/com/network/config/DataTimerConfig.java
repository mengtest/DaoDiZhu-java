package com.network.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("dataTimerConfig")
public class DataTimerConfig {

	private int delay;
	private int period;
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
}
