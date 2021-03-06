package com.github.tcler.cmdline;

import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

public class Cmdline {
	HashMap<String, ArrayList<String>> optionMap = new HashMap<String, ArrayList<String>>();
	ArrayList<String> invalidOptions = new ArrayList<String>();
	ArrayList<String> forwardOptions = new ArrayList<String>();
	ArrayList<String> args = new ArrayList<String>();

	public enum Status {
		NOTOPT,
		KNOWN,
		NEEDARG,
		UNKNOWN,
		END,
		AGAIN,
	}

	private class ArgParseStatus {
		Status stat;
		String optname;
		String optarg;
	}

	public Cmdline(ArrayList<Option> optionList, String[] argv) {
		Option[] options = optionList.toArray(new Option[0]);
		ArrayList<String> nargv = new ArrayList<String>(Arrays.asList(argv));
		ArgParseStatus argStat;

		loop:
		while(nargv.size() > 0) {
			String prefix = "-";
			String curarg = nargv.get(0);
			if (curarg.length() > 1 && curarg.substring(0,2).equals("--")) {
				prefix = "--";
			}

			argStat = argparse(options, nargv);

			switch (argStat.stat) {
			case AGAIN:
				continue;
			case NOTOPT:
				nargv.add(argStat.optarg);
				break;
			case KNOWN:
				Option opt = getOptObj(options, argStat.optname);
				if (opt == null) {
					System.err.printf("[Warn] (%s) there might be link loop in your option list\n", argStat.optname);
					opt = getOptObj(options, argStat.optname);
				}
				argStat.optname = opt.names[0];

				if (opt.forward) {
					switch (opt.argtype) {
					case N:
						forwardOptions.add(prefix + argStat.optname);
						break;
					default:
						if (prefix.equals("--")) {
							forwardOptions.add(prefix + argStat.optname + "=" + argStat.optarg);
						} else {
							forwardOptions.add(prefix + argStat.optname + " " + argStat.optarg);
						}
						break;
					}
					continue;
				}

				if (! optionMap.containsKey(argStat.optname)) {
					optionMap.put(argStat.optname, new ArrayList<String>());
				}
				switch (opt.argtype) {
				case M:
					optionMap.get(argStat.optname).add(argStat.optarg);
					break;
				case N:
					optionMap.get(argStat.optname).add("set");
					break;
				default:
					optionMap.get(argStat.optname).add(0, argStat.optarg);
					break;
				}

				for (int i = 1; i < opt.names.length; ++i) {
					String n = opt.names[i];
					optionMap.put(n, optionMap.get(argStat.optname));
					//System.out.printf("debug: put %d %s\n", i, n);
				}
				break;
			case NEEDARG:
				invalidOptions.add("option: '" + argStat.optname + "' need argument");
				break;
			case UNKNOWN:
				invalidOptions.add("option: '" + argStat.optname + "' undefined");
				break;
			case END:
				//end of nargv or get --
				this.args.addAll(nargv);
				break loop;
			}
		}
	}

	Option getOptObj(Option[] options, String optname) {
		for (Option opt: options) {
			if (opt.names == null) continue;
			for (String n: opt.names) {
				if (n.equals(optname)) {
					return opt;
				}
			}
		}
		return null;
	}

	ArgParseStatus argparse(Option[] options, ArrayList<String> nargv) {
		ArgParseStatus parseStat = new ArgParseStatus();
		parseStat.stat = Status.UNKNOWN;

		boolean hasval = false;
		String val = "";
		Option opt;
		Option.ArgType argtype;

		if (nargv.size() == 0) {
			parseStat.stat = Status.END;
			return parseStat;
		}

		String rarg = nargv.get(0);
		nargv.remove(0);

		if (rarg.equals("-") || rarg.equals("")) {
			parseStat.optarg = rarg;
			parseStat.stat = Status.NOTOPT;
			return parseStat;
		}

		if (rarg.equals("--")) {
			parseStat.stat = Status.END;
			return parseStat;
		}

		switch (rarg.substring(0, 1)) {
		case "-":
			String opttype = "long";
			parseStat.optname = rarg.substring(1);
			if (parseStat.optname.substring(0,1).equals("-")) {
				parseStat.optname = parseStat.optname.substring(1);
			} else {
				opttype = "short";
			}

			int idx = parseStat.optname.indexOf("=");
			if (idx > -1) {
				String ooptname = parseStat.optname;
				String toptname = ooptname.substring(0, idx);
				if (null != getOptObj(options, toptname)) {
					parseStat.optname = toptname;
					val = ooptname.substring(idx+1);
					hasval = true;
				}
			}

			opt = getOptObj(options, parseStat.optname);
			if (opt != null) {
				parseStat.optname = opt.names[0];
				parseStat.stat = Status.KNOWN;

				argtype = opt.argtype;
				if (opt.link.length() > 0) {
					Option optlink2 = getOptObj(options, opt.link);
					if (optlink2 != null) {
						argtype = optlink2.argtype;
						parseStat.optname = optlink2.names[0];
					}
				}

				switch (argtype) {
				case O:
					if (hasval) {
						parseStat.optarg = val;
					}
					break;
				case Y:
				case M:
					if (hasval) {
						parseStat.optarg = val;
					} else if (nargv.size() > 0 && nargv.get(0) != "--") {
						parseStat.optarg = nargv.get(0);
						nargv.remove(0);
					} else {
						parseStat.stat = Status.NEEDARG;
					}
					break;
				}
			} else if (hasval == false && opttype.equals("short") && parseStat.optname.length() > 1) {
				ArrayList<String> argv2 = new ArrayList<String>();

				ShortLoop:
				while (parseStat.optname.length() > 0) {
					String s = parseStat.optname.substring(0,1);
					parseStat.optname = parseStat.optname.substring(1);

					if (s.equals("=") || s.equals("-") || s.equals("\\") || s.equals("'") || s.equals("\"")) {
						break;
					}

					opt = getOptObj(options, s);
					if (opt == null) {
						argv2.add("-" + s);
						continue;
					} else {
						argtype = opt.argtype;
						if (opt.link.length() > 0) {
							Option optlink2 = getOptObj(options, opt.link);
							if (optlink2 != null) {
								argtype = optlink2.argtype;
							}
						}

						switch (argtype) {
						case O:
							argv2.add("-" + s + "=" + parseStat.optname);
							break ShortLoop;
						default:
							argv2.add("-" + s);
							break;
						}
					}
				}
				nargv.addAll(0, argv2);
				parseStat.stat = Status.AGAIN;
				return parseStat;
			} else {
				parseStat.stat = Status.UNKNOWN;
			}
			break;
		default:
			parseStat.optarg = rarg;
			parseStat.stat = Status.NOTOPT;
			break;
		}

		return parseStat;
	}

	public boolean hasOption(String key) {
		return optionMap.containsKey(key);
	}

	public ArrayList<String> getOptionArgList(String key) {
		return optionMap.get(key);
	}
	public String getOptionArgString(String key) {
		return optionMap.get(key).get(0);
	}
	public int getOptionNumber(String key) {
		return optionMap.get(key).size();
	}
	public ArrayList<String> getInvalidOptions() {
		return invalidOptions;
	}
	public ArrayList<String> getForwardOptions() {
		return forwardOptions;
	}
	public ArrayList<String> getArgs() {
		return args;
	}

	String genOptdesc(String[] names) {
		String ss = "";
		String ls = "";

		for (String n: names) {
			if (n.length() == 1) {
				ss += " -" + n;
			} else {
				ls += " --" + n;
			}
		}

		return ss + ls;
	}

	public void getUsage (ArrayList<Option> optionList) {
		Option[] options = optionList.toArray(new Option[0]);

		for (Option opt: options) {
			if (opt.hide) {
				continue;
			}

			if (opt.names == null || opt.names.length == 0) {
				if (opt.help != null && opt.help.length() > 0) {
					System.out.printf("%s\n", opt.help);
				}
				continue;
			}

			int pad = 26;
			String argdesc = "";
			String optdesc = genOptdesc(opt.names);
			switch (opt.argtype) {
			case O:
				argdesc = " [arg]";
				break;
			case Y:
				argdesc = " <arg>";
				break;
			case M:
				argdesc = " {arg}";
				break;
			}
			String opthelp = opt.help;
			if (opt.help.equals("")) {
				opthelp = "nil #no help found for this option";
			}

			int optlen = argdesc.length() + optdesc.length();
			int helplen = opthelp.length();

			if (optlen > pad-4 && helplen > 8) {
				String fmt = "    %-" + pad + "s\n %" + pad + "s    %s\n";
				System.out.printf(fmt, optdesc+argdesc, "", opthelp);
			} else {
				String fmt = "    %-" + pad + "s %s\n";
				System.out.printf(fmt, optdesc+argdesc, opthelp);
			}
		}

		System.out.println("\nComments:");
		System.out.println("    *  [arg] means arg is optional, need use --opt=arg to specify an argument");
		System.out.println("       <arg> means arg is required, and -f a -f b will get the latest 'b'");
		System.out.println("       {arg} means arg is required, and -f a -f b will get a list 'a b'");
		System.out.println("");
		System.out.println("    *  if arg is required, '--opt arg' is same as '--opt=arg'");
		System.out.println("");
		System.out.println("    *  '-opt' will be treated as:");
		System.out.println("           '--opt'    if 'opt' is defined;");
		System.out.println("           '-o -p -t' if 'opt' is undefined;");
		System.out.println("           '-o -p=t'  if 'opt' is undefined and '-p' need an argument;");
		System.out.println("");
	}

	public static void main(String[] argv) {
		if (argv.length < 1) {
			System.out.println("argv is emplty, use test argv instead:");
			argv = new String[]{ "-h", "-H", "-f", "file", "--file", "file2", "-e", "s/abc/xyz/", "-r", "-n", "-s=A", "-oa=b", "-S", "", "-i", "-x", "xfile", "--wenj=file3", "--www", "-aa", "-vvv", "-S", "DD", "--", "-0", "-y" };
		}
		System.out.printf("argv: %s\n", Arrays.toString(argv));
		System.out.printf("----------------------------------------------------------------\n\n");
		ArrayList<Option> optionList = new ArrayList<Option>();
		optionList.add(new Option.Builder("Options group1:").build());
		optionList.add(new Option.Builder("help h H 帮助", "print this help").build());
		optionList.add(new Option.Builder("file f", "specify file", Option.ArgType.M).build());
		optionList.add(new Option.Builder("conf c", "config file", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("o", "mount option", Option.ArgType.O).build());
		optionList.add(new Option.Builder("v", "verbose output, -vvv means verbose level 3").build());
		optionList.add(new Option.Builder("x", "dump binary file to text", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("s", "enable smart mode", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("S", "").link("s").hide().build());

		optionList.add(new Option.Builder("\nOptions group2:").build());
		optionList.add(new Option.Builder("e", "sed -e option, will forward to child sed process", Option.ArgType.M).forward().build());
		optionList.add(new Option.Builder("r", "sed -r option, will forward to child sed process", Option.ArgType.Y).forward().build());
		optionList.add(new Option.Builder("n", "sed -n option, will forward to child sed process", Option.ArgType.Y).forward().build());

		Cmdline cl = new Cmdline(optionList, argv);

		if (cl.hasOption("help")) {
			cl.getUsage(optionList);
		}
		if (cl.hasOption("file")) {
			System.out.println("opt(file): " + cl.getOptionArgList("file").toString());
		}
		if (cl.hasOption("conf")) {
			System.out.println("opt(conf): " + cl.getOptionArgString("conf"));
		}
		if (cl.hasOption("o")) {
			System.out.println("opt(o): " + cl.getOptionArgString("o"));
		}

		if (cl.hasOption("v")) {
			int verboselevel = cl.getOptionNumber("v");
			System.out.println("opt(v): " + verboselevel);
		}
		if (cl.hasOption("s")) {
			System.out.println("opt(s): " + cl.getOptionArgString("s"));
		}

		System.out.println("invalidOptions: " + cl.getInvalidOptions().toString());
		System.out.println("forwardOptions: " + cl.getForwardOptions().toString());
		System.out.println("args: " + cl.getArgs().toString());
	}
}

