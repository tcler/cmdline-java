package com.github.tcler.cmdline;

public class Option {
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

	public Option(String n, ArgType argtype, String help, String link, boolean hide, boolean forw) {
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

	public Option(String n, ArgType argtype, String help) {
		this(n, argtype, help, "", false, false);
	}
	public Option(String n, ArgType argtype, String help, String link) {
		this(n, argtype, help, link, false, false);
	}
	public Option(String n, ArgType argtype, String help, boolean forw) {
		this(n, argtype, help, "", false, forw);
	}
	public Option(String n, ArgType argtype, String help, String link, boolean hide) {
		this(n, argtype, help, link, hide, false);
	}
	public Option(String help) {
		this("", ArgType.N, help, "", false, false);
	}
}
