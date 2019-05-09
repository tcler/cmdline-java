package com.github.tcler.cmdline;

public class Option {
	String[] names;
	String help;
	ArgType argtype;
	String link;
	boolean hide;
	boolean forward;

	public enum ArgType {
		N,
		O,
		Y,
		M,
	}

	public static class Builder {
		// Required parammeters
		String[] names = null;
		String help;

		// Optional parammeters
		ArgType argtype = ArgType.N;
		String link = "";
		boolean hide = false;
		boolean forward = false;

		public Builder(String helpGroup) {
			this.help = helpGroup;
		}
		public Builder(String names, String help, ArgType type) {
			if (names != null && !names.trim().isEmpty()) {
				this.names = names.split(" ");
			}
			this.help = help;
			this.argtype = type;
		}
		public Builder(String names, String help) {
			this(names, help, ArgType.N);
		}

		public Builder type(ArgType val) {
			argtype = val;
			return this;
		}
		public Builder link(String val) {
			link = val;
			return this;
		}
		public Builder hide(boolean val) {
			hide = val;
			return this;
		}
		public Builder hide() {
			return this.hide(true);
		}
		public Builder forward(boolean val) {
			forward = val;
			return this;
		}
		public Builder forward() {
			return this.forward(true);
		}

		public Option build() {
			return new Option(this);
		}
	}

	private Option(Builder builder) {
		this.names = builder.names;
		this.argtype = builder.argtype;
		this.help = builder.help;
		this.link = builder.link;
		this.hide = builder.hide;
		this.forward = builder.forward;
	}
}
