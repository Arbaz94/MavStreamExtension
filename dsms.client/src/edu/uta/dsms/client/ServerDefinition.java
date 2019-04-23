package edu.uta.dsms.client;

public class ServerDefinition {

	protected String _hostname;
	protected int _port;
	
	public ServerDefinition(String hostname, int port) {
		_hostname = hostname;
		_port = port;
	}
	
	public String toString() {
		return _hostname + ":" + _port;
	}
}
