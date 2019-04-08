package com.github.tcler.cmdline;

class Option {
	String[] names;
	ArgType argtype;
	String help;
	String link;
	boolean hide;
	boolean forward;

	public enum ArgType {
		N,
		O,
		Y,
		M,
	}

	Option(String n, ArgType argtype, String help, String link, boolean hide, boolean forw) {
		this.names = null;
		if (n != null && n.trim().length() > 0) {
			this.names = n.split(" ");
		}
		this.argtype = argtype;
		this.help = help;
		this.link = link;
		this.hide = hide;
		this.forward = forw;
	}

	/*
	Option(String n, ArgType argtype, String help) {
		Option(n, argtype, help, "", false, false);
	}
	Option(String n, ArgType argtype, String help, String link) {
		Option(n, argtype, help, link, false, false);
	}
	Option(String n, ArgType argtype, String help, boolean forw) {
		Option(n, argtype, help, "", false, forw);
	}
	Option(String n, ArgType argtype, String help, String link, boolean hide) {
		Option(n, argtype, help, link, hide, false);
	}
	Option(String help) {
		Option("", ArgType.N, help, "", false, false);
	}
	*/
}
