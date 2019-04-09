import java.util.Arrays;
import java.util.ArrayList;
import com.github.tcler.cmdline.*;

class CmdlineTest {
	public static void main(String[] argv) {
		if (argv.length < 1) {
			System.out.println("argv is emplty, use test argv instead:");
			argv = new String[]{ "-h", "-H", "-f", "file", "--file", "file2", "-e", "s/abc/xyz/", "-r", "-n", "-s=A", "-oa=b", "-S", "", "-i", "-x", "xfile", "--wenj=file3", "--www", "-aa", "-vvv", "-S", "DD", "--", "-0", "-y" };
		}
		System.out.printf("argv: %s\n", Arrays.toString(argv));
		System.out.printf("----------------------------------------------------------------\n\n");
		ArrayList<Option> optionList = new ArrayList<Option>();
		optionList.add(new Option("Options group1:"));
		optionList.add(new Option("help h H 帮助", Option.ArgType.N, "print this help"));
		optionList.add(new Option("file f", Option.ArgType.M, "specify file"));
		optionList.add(new Option("conf c", Option.ArgType.Y, "config file"));
		optionList.add(new Option("o", Option.ArgType.O, "mount option", ""));
		optionList.add(new Option("v", Option.ArgType.N, "verbose output, -vvv means verbose level 3"));
		optionList.add(new Option("x", Option.ArgType.Y, "dump binary file to text"));
		optionList.add(new Option("s", Option.ArgType.Y, "enable smart mode"));
		optionList.add(new Option("S", Option.ArgType.Y, "", "s", true));

		optionList.add(new Option("\nOptions group2:"));
		optionList.add(new Option("e", Option.ArgType.M, "sed -e option, will forward to child sed process", true));
		optionList.add(new Option("r", Option.ArgType.N, "sed -r option, will forward to child sed process", true));
		optionList.add(new Option("n", Option.ArgType.N, "sed -n option, will forward to child sed process", true));

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
