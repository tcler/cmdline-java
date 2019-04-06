package com.github.tcler.cmdline;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
}
