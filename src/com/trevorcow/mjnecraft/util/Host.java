package com.trevorcow.mjnecraft.util;

public class Host {

	public String hostAddr;
	public int port;

	public Host(String hostAddr, int port) {
		this.hostAddr = hostAddr;
		this.port = port;
	}

	public Host(String hostAddr) {
		this(hostAddr, 25565);
	}

	@Override
	public String toString() {
		return hostAddr + ":" + port;
	}
}
